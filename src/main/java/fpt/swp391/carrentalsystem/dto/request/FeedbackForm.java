package fpt.swp391.carrentalsystem.dto.request;

import jakarta.validation.constraints.*;

public class FeedbackForm {

    @NotNull(message = "Booking ID không được để trống.")
    @Positive(message = "Booking ID phải là số dương.")
    private Long bookingId;

    @NotNull(message = "Rating không được để trống.")
    @Min(value = 1, message = "Rating tối thiểu là 1 sao.")
    @Max(value = 5, message = "Rating tối đa là 5 sao.")
    private Integer rating;

    @NotBlank(message = "Feedback Title không được để trống.")
    @Size(max = 150, message = "Feedback Title tối đa 150 ký tự.")
    private String title;

    @NotBlank(message = "Feedback Content không được để trống.")
    @Size(max = 5000, message = "Feedback Content tối đa 5000 ký tự.")
    private String content;

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}