package vn.com.nimbus.blog.internal.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.nimbus.blog.internal.service.HachiumService;
import vn.com.nimbus.data.domain.HachiumCategory;
import vn.com.nimbus.data.domain.HachiumCourse;
import vn.com.nimbus.data.repository.HachiumCategoryRepository;
import vn.com.nimbus.data.repository.HachiumCourseRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public void syncData() {
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

            categories.forEach(category -> {
                try {
                    Document courseHtml = Jsoup.connect(category.getUrl()).get();
                    Elements courseDivs = courseHtml.getElementsByClass("col-xs-12 col-md-4");
                    List<HachiumCourse> courses = new ArrayList<>();
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

                        courses.add(course);
                    });
                } catch (IOException e) {
                    log.warn(e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
