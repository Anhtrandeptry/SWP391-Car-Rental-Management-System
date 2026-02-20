package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.User;

public interface NotificationService {
    void sendBookingConfirmationEmail(Booking booking);
    void sendPaymentSuccessEmail(Booking booking);
    void sendBookingCancelledEmail(Booking booking);
    void sendOwnerNotification(Booking booking);
    void sendSMS(String phoneNumber, String message);
    void sendPaymentReminderEmail(Booking booking);
}

