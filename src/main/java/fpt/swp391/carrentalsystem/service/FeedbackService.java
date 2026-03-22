package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.FeedbackForm;
import fpt.swp391.carrentalsystem.dto.response.FeedbackResponse;
import fpt.swp391.carrentalsystem.dto.response.FeedbackStatsResponse;
import fpt.swp391.carrentalsystem.entity.Feedback;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.FeedbackStatus;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.mapper.FeedbackMapper;
import fpt.swp391.carrentalsystem.repository.FeedbackRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void sendFeedback(long customerId, FeedbackForm form) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found id=" + customerId));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("Chỉ CUSTOMER mới được gửi feedback.");
        }

        // Validate bổ sung BE (ngoài @Valid)
        if (form.getRating() == null || form.getRating() < 1 || form.getRating() > 5) {
            throw new RuntimeException("Rating phải từ 1 đến 5.");
        }
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            throw new RuntimeException("Feedback Title không được để trống.");
        }
        if (form.getContent() == null || form.getContent().isBlank()) {
            throw new RuntimeException("Feedback Content không được để trống.");
        }
        if (form.getBookingId() == null || form.getBookingId() <= 0) {
            throw new RuntimeException("Booking ID không hợp lệ.");
        }

        // Mỗi booking chỉ 1 feedback / customer (thực tế hay dùng)
        if (feedbackRepository.existsByBookingIdAndCustomer_Id(form.getBookingId(), customerId)) {
            throw new RuntimeException("Booking này bạn đã feedback rồi.");
        }

        Feedback feedback = Feedback.builder()
                .bookingId(form.getBookingId())
                .customer(customer)
                .rating(form.getRating())
                .title(form.getTitle().trim())
                .comment(form.getContent().trim())
                .status(FeedbackStatus.PROCESSING)
                .build();

        feedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getMyFeedbacks(long customerId) {
        return feedbackRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(FeedbackMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeedbackStatsResponse getMyFeedbackStats(long customerId) {
        long total = feedbackRepository.countByCustomer_Id(customerId);
        Double avg = feedbackRepository.avgRatingByCustomer(customerId);
        long replies = feedbackRepository.countRepliedByCustomer(customerId);

        return FeedbackStatsResponse.builder()
                .totalFeedback(total)
                .averageRating(avg == null ? 0.0 : avg)
                .totalReplies(replies)
                .build();
    }
}