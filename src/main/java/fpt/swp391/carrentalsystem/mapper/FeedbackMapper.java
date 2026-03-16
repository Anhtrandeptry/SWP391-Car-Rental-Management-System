package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.response.FeedbackResponse;
import fpt.swp391.carrentalsystem.entity.Feedback;

public class FeedbackMapper {

    public static FeedbackResponse toResponse(Feedback f) {
        if (f == null) return null;

        return FeedbackResponse.builder()
                .id(f.getId())
                .bookingId(f.getBookingId())
                .rating(f.getRating())
                .title(f.getTitle())
                .content(f.getComment())
                .status(f.getStatus())
                .systemReply(f.getSystemReply())
                .createdAt(f.getCreatedAt())
                .build();
    }
}