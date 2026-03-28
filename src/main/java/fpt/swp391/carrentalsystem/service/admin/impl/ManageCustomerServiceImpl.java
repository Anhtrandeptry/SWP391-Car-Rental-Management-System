package fpt.swp391.carrentalsystem.service.admin.impl;

import fpt.swp391.carrentalsystem.dto.response.BookingHistoryResponse;
import fpt.swp391.carrentalsystem.dto.response.CustomerResponse;
import fpt.swp391.carrentalsystem.dto.response.CustomerStatsResponse;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.mapper.admin.CustomerMapper;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.CustomerRepository;
import fpt.swp391.carrentalsystem.service.admin.ManageCustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ManageCustomerServiceImpl implements ManageCustomerService {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public ManageCustomerServiceImpl(CustomerRepository customerRepository,
                                     BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(String keyword, UserStatus status, Pageable pageable) {

        String safeKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<User> customers = customerRepository.searchAndFilterCustomers(
                Role.CUSTOMER,
                safeKeyword,
                status,
                pageable
        );

        // 🔥 Lấy stats 1 lần
        List<Object[]> stats = bookingRepository.getCustomerBookingStats();

        Map<Long, Object[]> statsMap = stats.stream()
                .collect(Collectors.toMap(
                        s -> (Long) s[0], // customerId
                        s -> s
                ));

        // 🔥 map sang response + inject thêm stats
        return customers.map(user -> {
            CustomerResponse res = CustomerMapper.toResponse(user);

            Object[] stat = statsMap.get(user.getId());

            if (stat != null) {
                res.setTotalBookings((Long) stat[1]);
                res.setTotalSpent((BigDecimal) stat[2]);
            } else {
                res.setTotalBookings(0L);
                res.setTotalSpent(BigDecimal.ZERO);
            }

            return res;
        });
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

        CustomerResponse response = CustomerMapper.toResponse(user);

        // 🔥 lấy toàn bộ stats
        List<Object[]> stats = bookingRepository.getCustomerBookingStats();

        // 🔥 tìm đúng customer
        Object[] stat = stats.stream()
                .filter(s -> s[0].equals(customerId))
                .findFirst()
                .orElse(null);

        if (stat != null) {
            response.setTotalBookings((Long) stat[1]);
            response.setTotalSpent((BigDecimal) stat[2]);
        } else {
            response.setTotalBookings(0L);
            response.setTotalSpent(BigDecimal.ZERO);
        }

        return response;
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

    public List<BookingHistoryResponse> getRecentBookings(Long customerId) {
        List<Booking> bookings = bookingRepository.findTop5ByCustomerId(
                customerId,
                PageRequest.of(0, 5)
        );

        return bookings.stream().map(b -> BookingHistoryResponse.builder()
                .id(b.getBookingId().longValue())
                .carName(b.getCar().getName())
                .startDate(b.getStartDate())
                .endDate(b.getEndDate())
                .totalAmount(b.getTotalAmount().doubleValue())
                .status(b.getStatus().name())
                .build()
        ).toList();
    }

}
