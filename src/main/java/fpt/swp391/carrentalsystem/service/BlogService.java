package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.BlogForm;
import fpt.swp391.carrentalsystem.entity.Blog;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BlogStatus;
import fpt.swp391.carrentalsystem.repository.BlogRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.utils.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public BlogService(BlogRepository blogRepository, UserRepository userRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    // ===== Public =====
    @Transactional(readOnly = true)
    public List<Blog> getPublishedBlogs() {
        return blogRepository.findByStatusOrderByCreatedAtDesc(BlogStatus.PUBLISHED);
    }

    @Transactional(readOnly = true)
    public Blog getPublishedBlogById(Long id) {
        Blog b = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        if (b.getStatus() != BlogStatus.PUBLISHED) {
            throw new RuntimeException("Blog is not published");
        }
        return b;
    }

    // ===== Admin =====
    @Transactional(readOnly = true)
    public List<Blog> getAllBlogsAdmin() {
        return blogRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Blog getBlogByIdAdmin(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
    }

    @Transactional
    public Blog createBlog(long adminUserId, BlogForm form) {
        validateForm(form);

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String baseSlug = SlugUtil.slugify(form.getTitle());
        String uniqueSlug = makeUniqueSlug(baseSlug);

        Blog blog = Blog.builder()
                .title(form.getTitle().trim())
                .slug(uniqueSlug)
                .summary(form.getSummary() == null ? null : form.getSummary().trim())
                .thumbnailUrl(form.getThumbnailUrl() == null ? null : form.getThumbnailUrl().trim())
                .contentHtml(form.getContentHtml())
                .status(form.getStatus() == null ? BlogStatus.DRAFT : form.getStatus())
                .createdBy(admin)
                .build();

        return blogRepository.save(blog);
    }

    @Transactional
    public Blog updateBlog(long adminUserId, Long blogId, BlogForm form) {
        validateForm(form);

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        // nếu đổi title thì đổi slug (và đảm bảo unique)
        String newTitle = form.getTitle().trim();
        if (!newTitle.equals(blog.getTitle())) {
            String baseSlug = SlugUtil.slugify(newTitle);
            blog.setSlug(makeUniqueSlugExcluding(baseSlug, blog.getId()));
        }

        blog.setTitle(newTitle);
        blog.setSummary(form.getSummary() == null ? null : form.getSummary().trim());
        blog.setThumbnailUrl(form.getThumbnailUrl() == null ? null : form.getThumbnailUrl().trim());
        blog.setContentHtml(form.getContentHtml());
        blog.setStatus(form.getStatus() == null ? BlogStatus.DRAFT : form.getStatus());

        return blogRepository.save(blog);
    }

    @Transactional
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    private void validateForm(BlogForm form) {
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            throw new RuntimeException("Tiêu đề không được để trống.");
        }
        if (form.getContentHtml() == null || form.getContentHtml().isBlank()) {
            throw new RuntimeException("Nội dung không được để trống.");
        }
    }

    private String makeUniqueSlug(String base) {
        String slug = base;
        int i = 1;
        while (blogRepository.existsBySlug(slug)) {
            slug = base + "-" + i;
            i++;
        }
        return slug;
    }

    private String makeUniqueSlugExcluding(String base, Long excludeId) {
        String slug = base;
        int i = 1;
        while (true) {
            Blog existing = blogRepository.findBySlug(slug).orElse(null);
            if (existing == null || existing.getId().equals(excludeId)) return slug;
            slug = base + "-" + i;
            i++;
        }
    }
}