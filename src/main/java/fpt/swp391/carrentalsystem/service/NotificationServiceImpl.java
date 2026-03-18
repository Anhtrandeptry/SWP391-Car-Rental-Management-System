package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.User;
 import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@carrentalsystem.com}")
    private String fromEmail;

    @Value("${twilio.account-sid:YOUR_ACCOUNT_SID}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token:YOUR_AUTH_TOKEN}")
    private String twilioAuthToken;

    @Value("${twilio.phone-number:+1234567890}")
    private String twilioPhoneNumber;

    @Override
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            String customerEmail = booking.getCustomer().getEmail();
            String subject = "✅ Xác Nhận Đặt Xe - Booking #" + booking.getBookingId();

            String body = String.format(
                    "Kính gửi %s %s,\n\n" +
                    "Đơn đặt xe của bạn đã được xác nhận.\n\n" +
                    "Thông tin chi tiết:\n" +
                    "- Mã đơn: #%d\n" +
                    "- Xe: %s (%s %s)\n" +
                    "- Ngày bắt đầu: %s\n" +
                    "- Ngày kết thúc: %s\n" +
                    "- Địa điểm nhận xe: %s\n" +
                    "- Phí thuê: %,.0f VND\n" +
                    "- Phí giữ chỗ: %,.0f VND\n" +
                    "- Phí thế chấp: %,.0f VND\n" +
                    "- Tổng cộng: %,.0f VND\n\n" +
                    "Vui lòng tiến hành thanh toán trong 3 phút.\n\n" +
                    "Trân trọng,\nCar Rental System",
                    booking.getCustomer().getFirstName(),
                    booking.getCustomer().getLastName(),
                    booking.getBookingId(),
                    booking.getCar().getName(),
                    booking.getCar().getBrand(),
                    booking.getCar().getModel(),
                    booking.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    booking.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    booking.getPickupLocation(),
                    booking.getRentalFee(),
                    booking.getHoldingFee(),
                    booking.getDepositAmount(),
                    booking.getTotalAmount()
            );

            sendEmail(customerEmail, subject, body);
            log.info("Booking confirmation email sent to: {}", customerEmail);

        } catch (Exception e) {
            log.error("Error sending booking confirmation email: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendPaymentSuccessEmail(Booking booking) {
        try {
            String customerEmail = booking.getCustomer().getEmail();
            String subject = "💳 Thanh Toán Thành Công - Booking #" + booking.getBookingId();

            String body = String.format(
                    "Kính gửi %s %s,\n\n" +
                    "Thanh toán của bạn đã được xác nhận thành công!\n\n" +
                    "Chi tiết thanh toán:\n" +
                    "- Mã đơn: #%d\n" +
                    "- Số tiền thanh toán: %,.0f VND\n" +
                    "- Thời gian: %s\n\n" +
                    "Đơn thuê xe của bạn đã được xác nhận.\n" +
                    "Vui lòng chuẩn bị các tài liệu cần thiết (CMND, bằng lái xe).\n\n" +
                    "Trân trọng,\nCar Rental System",
                    booking.getCustomer().getFirstName(),
                    booking.getCustomer().getLastName(),
                    booking.getBookingId(),
                    booking.getHoldingFee(),
                    booking.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );

            sendEmail(customerEmail, subject, body);
            log.info("Payment success email sent to: {}", customerEmail);

        } catch (Exception e) {
            log.error("Error sending payment success email: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendBookingCancelledEmail(Booking booking) {
        try {
            String customerEmail = booking.getCustomer().getEmail();
            String subject = "❌ Đơn Đặt Xe Bị Hủy - Booking #" + booking.getBookingId();

            String body = String.format(
                    "Kính gửi %s %s,\n\n" +
                    "Rất tiếc, đơn đặt xe của bạn đã bị hủy vì quá thời gian thanh toán (3 phút).\n\n" +
                    "Thông tin đơn:\n" +
                    "- Mã đơn: #%d\n" +
                    "- Xe: %s\n" +
                    "- Lý do: Không hoàn tất thanh toán phí giữ chỗ\n\n" +
                    "Bạn có thể tạo đơn mới bất cứ lúc nào.\n\n" +
                    "Trân trọng,\nCar Rental System",
                    booking.getCustomer().getFirstName(),
                    booking.getCustomer().getLastName(),
                    booking.getBookingId(),
                    booking.getCar().getName()
            );

            sendEmail(customerEmail, subject, body);
            log.info("Booking cancelled email sent to: {}", customerEmail);

        } catch (Exception e) {
            log.error("Error sending booking cancelled email: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendOwnerNotification(Booking booking) {
        try {
            String ownerEmail = booking.getCar().getOwner().getEmail();
            String subject = "🚗 Có Khách Đặt Xe - Booking #" + booking.getBookingId();

            String body = String.format(
                    "Kính gửi %s %s,\n\n" +
                    "Có khách đặt thuê chiếc xe của bạn.\n\n" +
                    "Thông tin đơn:\n" +
                    "- Mã đơn: #%d\n" +
                    "- Xe: %s\n" +
                    "- Khách hàng: %s %s\n" +
                    "- Điện thoại: %s\n" +
                    "- Ngày bắt đầu: %s\n" +
                    "- Ngày kết thúc: %s\n" +
                    "- Địa điểm nhận xe: %s\n\n" +
                    "Vui lòng chuẩn bị xe theo đúng thời gian.\n\n" +
                    "Trân trọng,\nCar Rental System",
                    booking.getCar().getOwner().getFirstName(),
                    booking.getCar().getOwner().getLastName(),
                    booking.getBookingId(),
                    booking.getCar().getName(),
                    booking.getCustomer().getFirstName(),
                    booking.getCustomer().getLastName(),
                    booking.getCustomer().getPhoneNumber(),
                    booking.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    booking.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    booking.getPickupLocation()
            );

            sendEmail(ownerEmail, subject, body);
            log.info("Owner notification email sent to: {}", ownerEmail);

        } catch (Exception e) {
            log.error("Error sending owner notification: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendSMS(String phoneNumber, String message) {
        try {
            // Initialize Twilio
            Twilio.init(twilioAccountSid, twilioAuthToken);

            Message smsMessage = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    message
            ).create();

            log.info("SMS sent successfully. SID: {}", smsMessage.getSid());

        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", phoneNumber, e.getMessage(), e);
        }
    }

    @Override
    public void sendPaymentReminderEmail(Booking booking) {
        try {
            String customerEmail = booking.getCustomer().getEmail();
            String subject = "⏰ Nhắc Nhở Thanh Toán - Booking #" + booking.getBookingId();

            String body = String.format(
                    "Kính gửi %s %s,\n\n" +
                    "Đơn đặt xe của bạn sắp hết thời gian thanh toán (3 phút).\n" +
                    "Vui lòng hoàn tất thanh toán phí giữ chỗ (%,.0f VND) ngay để xác nhận đơn.\n\n" +
                    "Mã đơn: #%d\n" +
                    "Xe: %s\n\n" +
                    "Nếu bạn không thanh toán, đơn sẽ bị hủy và xe sẽ được cho thuê cho khách hàng khác.\n\n" +
                    "Trân trọng,\nCar Rental System",
                    booking.getCustomer().getFirstName(),
                    booking.getCustomer().getLastName(),
                    booking.getHoldingFee(),
                    booking.getBookingId(),
                    booking.getCar().getName()
            );

            sendEmail(customerEmail, subject, body);
            log.info("Payment reminder email sent to: {}", customerEmail);

        } catch (Exception e) {
            log.error("Error sending payment reminder email: {}", e.getMessage(), e);
        }
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Error sending email to {}: {}", to, e.getMessage(), e);
        }
    }
}

