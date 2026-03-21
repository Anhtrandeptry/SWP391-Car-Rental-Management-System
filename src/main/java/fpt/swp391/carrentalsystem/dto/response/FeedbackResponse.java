package fpt.swp391.carrentalsystem.dto.response;

import fpt.swp391.carrentalsystem.enums.FeedbackStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {
    private Long id;
    private Long bookingId;
    private Integer rating;
    private String title;
    private String content;
    private FeedbackStatus status;
    private String systemReply;
    private LocalDateTime createdAt;
}