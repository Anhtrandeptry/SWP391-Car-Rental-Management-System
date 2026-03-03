//package fpt.swp391.carrentalsystem.service;
//
//import fpt.swp391.carrentalsystem.dto.request.FeedbackForm;
//import fpt.swp391.carrentalsystem.entity.Feedback;
//import fpt.swp391.carrentalsystem.entity.User;
//import fpt.swp391.carrentalsystem.repository.FeedbackRepository;
//import fpt.swp391.carrentalsystem.repository.UserRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class FeedbackService {
//
//    private final FeedbackRepository feedbackRepository;
//    private final UserRepository userRepository;
//
//    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository) {
//        this.feedbackRepository = feedbackRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Transactional
//    public void createFeedback(Long customerId, FeedbackForm form) {
//        // validate nhanh cho khỏi lỗi bậy
//        if (form.getRating() == null || form.getRating() < 1 || form.getRating() > 5) {
//            throw new RuntimeException("Vui lòng chọn số sao (1-5).");
//        }
//        if (form.getTitle() == null || form.getTitle().trim().isEmpty()) {
//            throw new RuntimeException("Tiêu đề không được để trống.");
//        }
//        if (form.getContent() == null || form.getContent().trim().isEmpty()) {
//            throw new RuntimeException("Nội dung phản hồi không được để trống.");
//        }
//
//        User customer = userRepository.findById(customerId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy user id=" + customerId));
//
//        Feedback fb = Feedback.builder()
//                .customer(customer)
//                .rating(form.getRating())
//                .title(form.getTitle().trim())
//                .content(form.getContent().trim())
//                .build();
//
//        feedbackRepository.save(fb);
//    }
//}
