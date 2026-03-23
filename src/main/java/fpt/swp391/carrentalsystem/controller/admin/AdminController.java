package fpt.swp391.carrentalsystem.controller.admin;

import fpt.swp391.carrentalsystem.dto.response.CustomerResponse;
import fpt.swp391.carrentalsystem.dto.response.OwnerResponse;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.service.admin.ManageCustomerService;
import fpt.swp391.carrentalsystem.service.admin.ManageOwnerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ManageCustomerService manageCustomerService;
    private final ManageOwnerService manageOwnerService;

    // ✅ Constructor Injection (khuyến nghị)
    public AdminController(ManageCustomerService manageCustomerService,
                           ManageOwnerService manageOwnerService) {
        this.manageCustomerService = manageCustomerService;
        this.manageOwnerService = manageOwnerService;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/admin-dashboard";
    }

    @GetMapping("/customers")
    public String customerList(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) UserStatus status, // Nhận status
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 8);

        // Truyền cả status xuống service
        Page<CustomerResponse> customerPage = manageCustomerService.getAllCustomers(keyword, status, pageable);

        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalElements", customerPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status); // Truyền lại status để giữ trên select box
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


    // 1. Trang danh sách chủ xe
    @GetMapping("/owners")
    public String listOwners(
            @RequestParam(name = "keyword", required = false) String keyword, // Nhận từ khóa tìm kiếm
            @RequestParam(name = "status", required = false) UserStatus status, // Nhận trạng thái để lọc
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // 1. Khởi tạo phân trang: trang hiện tại, số phần tử = 8
        Pageable pageable = PageRequest.of(page, 8);

        // 2. Gọi Service trả về Page (Truyền thêm keyword và status)
        Page<OwnerResponse> ownerPage = manageOwnerService.getAllOwners(keyword, status, pageable);

        // 3. Đưa dữ liệu danh sách chủ xe của trang hiện tại vào model
        model.addAttribute("owners", ownerPage.getContent());

        // 4. Đưa các thông số phân trang, từ khóa và trạng thái lọc ra giao diện (UI)
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ownerPage.getTotalPages());
        model.addAttribute("totalElements", ownerPage.getTotalElements());
        model.addAttribute("keyword", keyword); // Giữ lại từ khóa trên ô nhập liệu
        model.addAttribute("status", status); // Giữ lại trạng thái đã chọn trên select box

        // 5. Thống kê số lượng chủ xe
        model.addAttribute("stats", manageOwnerService.getOwnerStats());

        return "admin/owner-list";
    }

    // 2. Trang chi tiết chủ xe
    @GetMapping("/owners/detail-page")
    public String ownerDetailPage(@RequestParam("id") Long id, Model model) {
        OwnerResponse owner = manageOwnerService.getOwnerById(id);
        model.addAttribute("owner", owner);
        return "admin/owner-detail"; // Trả về file owner-detail.html
    }

    // 3. Phê duyệt chủ xe
    @PostMapping("/owners/approve")
    public String approveOwner(@RequestParam("id") Long id) {
        manageOwnerService.approveOwner(id);
        return "redirect:/admin/owners";
    }

    // 4. Khóa tài khoản chủ xe
    @PostMapping("/owners/disable")
    public String disableOwner(@RequestParam("id") Long id) {
        manageOwnerService.disableOwner(id);
        return "redirect:/admin/owners";
    }

    // 5. Đưa chủ xe về trạng thái chờ
    @PostMapping("/owners/pending")
    public String setOwnerPending(@RequestParam("id") Long id) {
        manageOwnerService.setToPending(id);
        return "redirect:/admin/owners";
    }


}
