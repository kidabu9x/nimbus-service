package vn.com.nimbus.blog.internal.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.com.nimbus.common.model.dto.SlugPoolDto;
import vn.com.nimbus.common.service.SlugPoolService;
import vn.com.nimbus.data.domain.Blog;
import vn.com.nimbus.data.domain.Category;
import vn.com.nimbus.data.domain.constant.SlugPoolType;
import vn.com.nimbus.data.repository.BlogRepository;
import vn.com.nimbus.data.repository.CategoryRepository;

import java.util.List;

@Component
@Slf4j
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final SlugPoolService slugPoolService;


    public CommandLineAppStartupRunner(
            BlogRepository blogRepository,
            CategoryRepository categoryRepository,
            SlugPoolService slugPoolService
    ) {
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.slugPoolService = slugPoolService;
    }

    @Override
    public void run(String...args) {
        log.info("Start update database for new slug pool");
        List<Blog> blogs = blogRepository.findAll();
        blogs.forEach(blog -> {
            SlugPoolDto dto = new SlugPoolDto();
            dto.setType(SlugPoolType.BLOG);
            dto.setTitle(blog.getTitle());
            dto.setTargetId(blog.getId());
            slugPoolService.save(dto);
        });
        List<Category> categories = categoryRepository.findAll();
        categories.forEach(category -> {
            SlugPoolDto dto = new SlugPoolDto();
            dto.setType(SlugPoolType.CATEGORY);
            dto.setTitle(category.getTitle());
            dto.setTargetId(category.getId());
            slugPoolService.save(dto);
        });

        log.info("Success");
        // TODO: need remove this in the next go-live
    }
}