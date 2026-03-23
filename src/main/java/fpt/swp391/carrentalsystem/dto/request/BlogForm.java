package fpt.swp391.carrentalsystem.dto.request;

import fpt.swp391.carrentalsystem.enums.BlogStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BlogForm {

    @NotBlank(message = "Tiêu đề không được để trống.")
    @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự.")
    private String title;

    @Size(max = 500, message = "Tóm tắt tối đa 500 ký tự.")
    private String summary;

    @Size(max = 500, message = "Thumbnail URL tối đa 500 ký tự.")
    private String thumbnailUrl;

    @NotBlank(message = "Nội dung không được để trống.")
    private String contentHtml;

    private BlogStatus status = BlogStatus.PUBLISHED;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }
    public BlogStatus getStatus() { return status; }
    public void setStatus(BlogStatus status) { this.status = status; }
}