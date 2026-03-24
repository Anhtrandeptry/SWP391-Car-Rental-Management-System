package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.request.FeedbackForm;
import fpt.swp391.carrentalsystem.dto.response.FeedbackResponse;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.FeedbackService;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepository;

    public FeedbackController(FeedbackService feedbackService, UserRepository userRepository) {
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
    }

    private long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new RuntimeException("Không xác định được user đăng nhập.");

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails cud && cud.getId() != null) {
            return cud.getId();
        }


        String login = auth.getName();
        return userRepository.findByEmailOrPhoneNumber(login, login)
                .map(u -> u.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user theo login: " + login));
    }

    @GetMapping("/new")
    public String sendForm(Model model,
                           @RequestParam(value = "ok", required = false) String ok) {
        model.addAttribute("form", new FeedbackForm());
        model.addAttribute("ok", ok != null);

        model.addAttribute("suggestions", """
- Mô tả đúng trải nghiệm bạn gặp phải (trước/sau khi nhận xe).
- Nêu rõ điểm tốt và điểm cần cải thiện.
- Tránh dùng từ xúc phạm, tập trung vào sự kiện.
- Nếu có vấn đề, ghi rõ thời gian và tình huống.
""");

        return "customer/feedback-send";
    }

    @PostMapping("/new")
    public String sendSubmit(@Valid @ModelAttribute("form") FeedbackForm form,
                             BindingResult br,
                             Model model) {
        if (br.hasErrors()) {
            model.addAttribute("suggestions", """
- Mô tả đúng trải nghiệm bạn gặp phải (trước/sau khi nhận xe).
- Nêu rõ điểm tốt và điểm cần cải thiện.
- Tránh dùng từ xúc phạm, tập trung vào sự kiện.
- Nếu có vấn đề, ghi rõ thời gian và tình huống.
""");
            return "customer/feedback-send";
        }

        try {
            feedbackService.sendFeedback(currentUserId(), form);
            return "redirect:/customer/feedbacks/new?ok=1";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("suggestions", """
- Mô tả đúng trải nghiệm bạn gặp phải (trước/sau khi nhận xe).
- Nêu rõ điểm tốt và điểm cần cải thiện.
- Tránh dùng từ xúc phạm, tập trung vào sự kiện.
- Nếu có vấn đề, ghi rõ thời gian và tình huống.
""");
            return "customer/feedback-send";
        }
    }

    @GetMapping
    public String list(Model model) {
        long userId = currentUserId();
        List<FeedbackResponse> feedbacks = feedbackService.getMyFeedbacks(userId);

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("stats", feedbackService.getMyFeedbackStats(userId));

        return "customer/feedback-list";
    }
}