package fpt.swp391.carrentalsystem.service.admin.impl;

import fpt.swp391.carrentalsystem.dto.response.CustomerResponse;
import fpt.swp391.carrentalsystem.dto.response.CustomerStatsResponse;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.mapper.admin.CustomerMapper;
import fpt.swp391.carrentalsystem.repository.CustomerRepository;
import fpt.swp391.carrentalsystem.service.admin.ManageCustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageCustomerServiceImpl implements ManageCustomerService {

    private final CustomerRepository customerRepository;

    public ManageCustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findByRole(Role.CUSTOMER)
                .stream()
                .map(CustomerMapper::toResponse)
                .toList();
    }

    @Override
    public CustomerStatsResponse getCustomerStats() {
        // Lấy toàn bộ danh sách khách hàng từ Database thông qua Repository
        List<CustomerResponse> customers = getAllCustomers();

        // Sử dụng Java Stream để phân loại và đếm
        return CustomerStatsResponse.builder()
                .totalUsers(customers.size())
                .activeUsers(customers.stream()
                        .filter(c -> c.getStatus() == UserStatus.ACTIVE).count())
                .disabledUsers(customers.stream()
                        .filter(c -> c.getStatus() == UserStatus.DISABLED).count())
                .pendingApproval(customers.stream()
                        .filter(c -> c.getStatus() == UserStatus.PENDING).count())
                .totalGrowth("+12%") // Giá trị tạm thời theo Requirement của bạn
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
