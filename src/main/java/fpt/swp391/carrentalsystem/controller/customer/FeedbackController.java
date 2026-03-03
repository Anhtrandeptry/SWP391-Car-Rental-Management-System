//package fpt.swp391.carrentalsystem.controller.customer;
//import fpt.swp391.carrentalsystem.dto.request.FeedbackForm;
//import fpt.swp391.carrentalsystem.security.CustomUserDetails;
//import fpt.swp391.carrentalsystem.service.FeedbackService;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequestMapping("/feedback")
//public class FeedbackController {
//
//    private final FeedbackService feedbackService;
//
//    public FeedbackController(FeedbackService feedbackService) {
//        this.feedbackService = feedbackService;
//    }
//
//    @GetMapping("/new")
//    public String showForm(Model model) {
//        model.addAttribute("form", new FeedbackForm());
//        return "feedback-form"; // file feedback-form.html
//    }
//
//    @PostMapping("/new")
//    public String submit(
//            @ModelAttribute("form") FeedbackForm form,
//            @AuthenticationPrincipal CustomUserDetails user,
//            Model model
//    ) {
//        try {
//            Long customerId = user.getUser().getId(); // tùy CustomUserDetails của bạn
//            feedbackService.createFeedback(customerId, form);
//            return "redirect:/feedback/new?ok";
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage());
//            return "feedback-form";
//        }
//    }
//}
