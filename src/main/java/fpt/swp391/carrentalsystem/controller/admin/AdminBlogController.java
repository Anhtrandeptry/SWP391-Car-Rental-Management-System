package fpt.swp391.carrentalsystem.controller.admin;

import fpt.swp391.carrentalsystem.dto.request.BlogForm;
import fpt.swp391.carrentalsystem.entity.Blog;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.sercurity.CustomUserDetails;
import fpt.swp391.carrentalsystem.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/blogs")
public class AdminBlogController {

    private final BlogService blogService;
    private final UserRepository userRepository;

    public AdminBlogController(BlogService blogService, UserRepository userRepository) {
        this.blogService = blogService;
        this.userRepository = userRepository;
    }

    private long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new RuntimeException("Not authenticated");
        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails cud && cud.getId() != null) {
            return cud.getId();
        }

        String login = auth.getName();
        return userRepository.findByEmailOrPhoneNumber(login, login)
                .map(u -> u.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public String adminList(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogsAdmin());
        return "admin/blog-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        BlogForm f = new BlogForm();
        f.setStatus(fpt.swp391.carrentalsystem.enums.BlogStatus.PUBLISHED);
        model.addAttribute("form", f);
        model.addAttribute("mode", "create");
        return "admin/blog-form";
    }

    @PostMapping("/new")
    public String createSubmit(@Valid @ModelAttribute("form") BlogForm form,
                               BindingResult br,
                               Model model) {
        if (br.hasErrors()) {
            model.addAttribute("mode", "create");
            return "admin/blog-form";
        }
        try {
            blogService.createBlog(currentUserId(), form);
            return "redirect:/admin/blogs";
        } catch (Exception e) {
            model.addAttribute("mode", "create");
            model.addAttribute("error", e.getMessage());
            return "admin/blog-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlogByIdAdmin(id);

        BlogForm form = new BlogForm();
        form.setTitle(blog.getTitle());
        form.setSummary(blog.getSummary());
        form.setThumbnailUrl(blog.getThumbnailUrl());
        form.setContentHtml(blog.getContentHtml());
        form.setStatus(blog.getStatus());

        model.addAttribute("form", form);
        model.addAttribute("blogId", id);
        model.addAttribute("mode", "edit");
        return "admin/blog-form";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("form") BlogForm form,
                             BindingResult br,
                             Model model) {
        if (br.hasErrors()) {
            model.addAttribute("blogId", id);
            model.addAttribute("mode", "edit");
            return "admin/blog-form";
        }
        try {
            blogService.updateBlog(currentUserId(), id, form);
            return "redirect:/admin/blogs";
        } catch (Exception e) {
            model.addAttribute("blogId", id);
            model.addAttribute("mode", "edit");
            model.addAttribute("error", e.getMessage());
            return "admin/blog-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return "redirect:/admin/blogs";
    }
}