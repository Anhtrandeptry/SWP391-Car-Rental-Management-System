package fpt.swp391.carrentalsystem.controller.admin;

import fpt.swp391.carrentalsystem.dto.response.BookingStatsResponse;
import fpt.swp391.carrentalsystem.dto.response.RevenueStatsResponse;
import fpt.swp391.carrentalsystem.service.admin.impl.ReportMockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/reports")
public class ReportController {

    @Autowired
    private ReportMockService reportService;

    /**
     * View báo cáo doanh thu (Dựa trên Ảnh 1)
     * URL: /admin/reports/revenue
     */
    @GetMapping("/revenue")
    public String getRevenueReport(Model model,
                                   @RequestParam(defaultValue = "30") String range) {

        // Lấy dữ liệu mock từ service
        RevenueStatsResponse revenueStats = reportService.getMockRevenueStats();

        // Truyền dữ liệu sang giao diện
        model.addAttribute("stats", revenueStats);
        model.addAttribute("activePage", "revenue"); // Dùng để highlight menu/tab
        model.addAttribute("currentRange", range);   // 7 ngày, 30 ngày, Quý, Năm

        return "admin/report"; // File HTML chung hoặc riêng tùy bạn thiết kế
    }

    /**
     * View thống kê booking (Dựa trên Ảnh 2)
     * URL: /admin/reports/booking-statistics
     */
    @GetMapping("/booking-statistics")
    public String getBookingStatistics(Model model,
                                       @RequestParam(defaultValue = "30") String range) {

        // Lấy dữ liệu mock cho thống kê đơn hàng
        BookingStatsResponse bookingStats = reportService.getMockBookingStats();

        // Truyền dữ liệu sang giao diện
        model.addAttribute("stats", bookingStats);
        model.addAttribute("activePage", "booking");
        model.addAttribute("currentRange", range);

        return "admin/report-management";
    }


}