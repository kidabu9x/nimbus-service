package vn.com.nimbus.blog.internal.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.nimbus.blog.internal.service.HachiumService;
import vn.com.nimbus.data.domain.HachiumCategory;
import vn.com.nimbus.data.domain.HachiumCourse;
import vn.com.nimbus.data.repository.HachiumCategoryRepository;
import vn.com.nimbus.data.repository.HachiumCourseRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HachiumServiceImpl implements HachiumService {
    private final HachiumCategoryRepository hachiumCategoryRepository;
    private final HachiumCourseRepository hachiumCourseRepository;

    @Autowired
    public HachiumServiceImpl(
            HachiumCategoryRepository hachiumCategoryRepository,
            HachiumCourseRepository hachiumCourseRepository
    ) {
        this.hachiumCategoryRepository = hachiumCategoryRepository;
        this.hachiumCourseRepository = hachiumCourseRepository;
    }

    @Override
    @Transactional
    public boolean syncData() {
        try {
            final String blogUrl = "https://nimbus.com.vn/courses";
            Document doc = Jsoup.connect(blogUrl).get();

            Elements categoryDivs = doc.select(".category-href");

            List<HachiumCategory> categories = new ArrayList<>();
            categoryDivs.forEach(categoryDiv -> {
                String categoryLink = categoryDiv.attr("href");
                String categoryName = categoryDiv.text();
                String[] categorySplit = categoryLink.split("=");
                if (categorySplit.length > 1) {
                    Long categoryId = Long.parseLong(categorySplit[1]);
                    categories.add(new HachiumCategory(categoryId, categoryLink, categoryName));
                }
            });

            hachiumCategoryRepository.saveAll(categories);

            Map<String, HachiumCourse> mapUrlAndCourse = new HashMap<>();
            List<String> urls = new ArrayList<>();

            categories.forEach(category -> {
                try {
                    Document courseHtml = Jsoup.connect(category.getUrl()).get();
                    Elements courseDivs = courseHtml.getElementsByClass("col-xs-12 col-md-4");
                    courseDivs.forEach(div -> {

                        Element linkTag = div.select("a").first();
                        String courseUrl = linkTag.attr("href");

                        Element imgTag = div.select(".course-ava.hachium").first();
                        String style = imgTag.attr("style");
                        String courseAvatar = style.substring(22, style.length() - 2);

                        Element nameTag = div.select(".name-course.hachium").first();
                        String courseTitle = nameTag.text();

                        Element authorTag = div.select(".name-author.hachium").first();
                        String authorName = authorTag.text();

                        Element oldPriceTag = div.select(".old-price").first();
                        Long oldPrice = null;
                        if (oldPriceTag != null) {
                            String oldPriceStr = oldPriceTag.text();
                            oldPrice = Long.parseLong(oldPriceStr.replace(",", "").replace("đ", ""));
                        }

                        Element priceTag = div.select(".price.hachium").first();
                        String priceStr = priceTag.text();
                        long price = 0L;
                        if (!StringUtils.isEmpty(priceStr) && !priceStr.equals("Miễn phí")) {
                            price = Long.parseLong(priceStr.replace(",", "").replace("đ", ""));
                        }

                        HachiumCourse course = new HachiumCourse();
                        course.setUrl(courseUrl);
                        course.setTitle(courseTitle);
                        course.setImage(courseAvatar);
                        course.setAuthor(authorName);
                        course.setOldPrice(oldPrice);
                        course.setPrice(price);
                        course.setHachiumCategoryId(category.getId());

                        mapUrlAndCourse.put(course.getUrl(), course);
                        urls.add(course.getUrl());
                    });
                } catch (IOException e) {
                    log.warn(e.getMessage());
                    e.printStackTrace();
                }
            });

            List<HachiumCourse> existCourses = hachiumCourseRepository.findByUrlIn(urls);
            List<HachiumCourse> deleteCourses = new ArrayList<>();
            for (HachiumCourse existCourse : existCourses) {
                HachiumCourse course = mapUrlAndCourse.get(existCourse.getUrl());
                if (course == null) {
                    deleteCourses.add(existCourse);
                    continue;
                }
                course.setId(existCourse.getId());
            }
            List<HachiumCourse> courses = new ArrayList<>(mapUrlAndCourse.values());

            hachiumCourseRepository.deleteAll(deleteCourses);
            hachiumCourseRepository.saveAll(courses);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
