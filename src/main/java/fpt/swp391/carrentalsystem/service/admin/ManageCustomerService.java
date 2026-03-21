package fpt.swp391.carrentalsystem.service.admin;

import fpt.swp391.carrentalsystem.dto.response.CustomerResponse;
import fpt.swp391.carrentalsystem.dto.response.CustomerStatsResponse;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManageCustomerService {
    Page<CustomerResponse> getAllCustomers(String keyword, UserStatus status, Pageable pageable);
    // hàm tính toán thống kê số lượng các tài khoản customer
    CustomerStatsResponse getCustomerStats();

    CustomerResponse getCustomerById(Long customerId);

    void disableCustomer(Long customerId);

    void enableCustomer(Long customerId);

    // Hàm để chuyển từ Pending sang Active
    void approveCustomer(Long customerId);

    // Hàm để chuyển bất kỳ trạng thái nào về Pending
    void setToPending(Long customerId);
}
