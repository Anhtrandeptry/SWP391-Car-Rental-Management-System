package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackStatsResponse {
    private long totalFeedback;
    private double averageRating;
    private long totalReplies;
}