package fpt.swp391.carrentalsystem.controller.common;

import fpt.swp391.carrentalsystem.entity.Blog;
import fpt.swp391.carrentalsystem.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/public/blogs")
public class PublicBlogController {

    private final BlogService blogService;

    public PublicBlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public String list(Model model) {
        List<Blog> blogs = blogService.getPublishedBlogs();
        model.addAttribute("blogs", blogs);
        return "public/blog-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Blog blog = blogService.getPublishedBlogById(id);
        model.addAttribute("blog", blog);
        return "public/blog-detail";
    }
}