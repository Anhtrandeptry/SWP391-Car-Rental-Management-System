package fpt.swp391.carrentalsystem.controller.admin;

import fpt.swp391.carrentalsystem.dto.response.CustomerResponse;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.service.admin.ManageCustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ManageCustomerService manageCustomerService;

    // ✅ Constructor Injection (khuyến nghị)
    public AdminController(ManageCustomerService manageCustomerService) {
        this.manageCustomerService = manageCustomerService;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/admin-dashboard";
    }

    @GetMapping("/customers")
    public String customerList(Model model) {
        model.addAttribute("customers", manageCustomerService.getAllCustomers());
        model.addAttribute("stats", manageCustomerService.getCustomerStats());
        return "admin/customer-list";
    }

    // Lấy chi tiết khách hàng theo ID dưới dạng JSON
    @GetMapping("/customers/detail-page")
    public String customerDetailPage(@RequestParam("id") Long id, Model model) {
        // Lấy dữ liệu chi tiết từ service
        CustomerResponse customer = manageCustomerService.getCustomerById(id);

        // Đưa dữ liệu vào model để Thymeleaf bên trang detail sử dụng
        model.addAttribute("customer", customer);

        // Trả về file customer-detail.html
        return "admin/customer-detail";
    }

    @PostMapping("/customers/approve")
    public String approveCustomer(@RequestParam("id") Long id) {
        manageCustomerService.approveCustomer(id);
        return "redirect:/admin/customers";
    }

    // Chuyển sang DISABLED (Khóa tài khoản)
    @PostMapping("/customers/disable")
    public String disableCustomer(@RequestParam("id") Long id) {
        manageCustomerService.disableCustomer(id);
        return "redirect:/admin/customers";
    }

    // Chuyển sang PENDING (Đưa về trạng thái chờ)
    @PostMapping("/customers/pending")
    public String setPending(@RequestParam("id") Long id) {
        manageCustomerService.setToPending(id);
        return "redirect:/admin/customers";
    }
}
