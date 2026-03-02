package fpt.swp391.carrentalsystem.service.admin.impl;

import fpt.swp391.carrentalsystem.dto.response.CustomerResponse;
import fpt.swp391.carrentalsystem.dto.response.CustomerStatsResponse;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.mapper.admin.CustomerMapper;
import fpt.swp391.carrentalsystem.repository.CustomerRepository;
import fpt.swp391.carrentalsystem.service.admin.ManageCustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ManageCustomerServiceImpl implements ManageCustomerService {

    private final CustomerRepository customerRepository;

    public ManageCustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(String keyword, UserStatus status, Pageable pageable) {
        // Xử lý trim keyword
        String safeKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<User> customers = customerRepository.searchAndFilterCustomers(
                Role.CUSTOMER,
                safeKeyword,
                status,
                pageable
        );

        return customers.map(CustomerMapper::toResponse);
    }

    @Override
    public CustomerStatsResponse getCustomerStats() {
        // Gọi trực tiếp repository để lấy con số tổng từ Database
        long total = customerRepository.countByRole(Role.CUSTOMER);
        long active = customerRepository.countByRoleAndStatus(Role.CUSTOMER, UserStatus.ACTIVE);
        long disabled = customerRepository.countByRoleAndStatus(Role.CUSTOMER, UserStatus.DISABLED);
        long pending = customerRepository.countByRoleAndStatus(Role.CUSTOMER, UserStatus.PENDING);

        return CustomerStatsResponse.builder()
                .totalUsers((int) total) // Ép kiểu về int nếu DTO yêu cầu
                .activeUsers(active)
                .disabledUsers(disabled)
                .pendingApproval(pending)
                .totalGrowth("+12%") // Giữ nguyên theo mẫu của bạn
                .activeGrowth("+8%")
                .build();
    }
    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        User user = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (user.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("User is not a customer");
        }

        return CustomerMapper.toResponse(user);
    }

    @Override
    public void disableCustomer(Long customerId) {
        User user = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        user.setStatus(UserStatus.DISABLED);
        customerRepository.save(user);
    }

    @Override
    public void enableCustomer(Long customerId) {
        User user = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        user.setStatus(UserStatus.ACTIVE);
        customerRepository.save(user);
    }

    @Override
    public void approveCustomer(Long customerId) {
        User customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setStatus(UserStatus.ACTIVE);
        customerRepository.save(customer);
    }

    @Override
    public void setToPending(Long customerId) {
        User customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setStatus(UserStatus.PENDING);
        customerRepository.save(customer);
    }

}
