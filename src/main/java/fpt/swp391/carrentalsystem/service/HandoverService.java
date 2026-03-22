package fpt.swp391.carrentalsystem.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import fpt.swp391.carrentalsystem.entity.*;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.HandoverStatus;
import fpt.swp391.carrentalsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HandoverService {

    private final HandoverRepository handoverRepository;
    private final HandoverImageRepository handoverImageRepository;
    private final BookingRepository bookingRepository;
    private final Cloudinary cloudinary;

    @Transactional
    public void createHandoverRequest(Integer bookingId, Integer fuelLevel, Integer odometer,
                                      String description, List<MultipartFile> files) throws Exception {
        if (files == null || files.isEmpty() || files.get(0).isEmpty()) {
            throw new RuntimeException();
        }

        if (files.size() > 10) {
            throw new RuntimeException("<10");
        }

        long maxFileSize = 1024 * 1024;
        for (MultipartFile file : files) {
            if (file.getSize() > maxFileSize) {
                throw new RuntimeException();
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException();
            }
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + bookingId));

        Handover handover = Handover.builder()
                .booking(booking)
                .fuelLevel(fuelLevel)
                .odometerReading(odometer)
                .description(description)
                .status(HandoverStatus.PENDING)
                .build();

        handover = handoverRepository.save(handover);

        if (files != null && !files.isEmpty()) {
            String folderName = String.format("handovers/booking_%d_%s",
                    booking.getBookingId(),
                    slugify(booking.getCustomer().getFirstName() + "_" + booking.getCustomer().getLastName()));

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                        "folder", folderName,
                        "resource_type", "auto"
                ));

                String url = (String) uploadResult.get("secure_url");

                HandoverImage imgEntity = HandoverImage.builder()
                        .handover(handover)
                        .imageUrl(url)
                        .build();

                handoverImageRepository.save(imgEntity);
            }
        }

        booking.setStatus(BookingStatus.HANDOVER_PENDING);
        bookingRepository.save(booking);
    }

    private String slugify(String input) {
        if (input == null) return "customer";
        return input.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }

    @Transactional
    public void approveHandover(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        booking.setStatus(BookingStatus.IN_USE);
        bookingRepository.save(booking);

        Handover handover = handoverRepository.findByBooking_BookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi bàn giao"));
        handover.setStatus(HandoverStatus.ACCEPTED);
        handoverRepository.save(handover);
    }

    @Transactional
    public void rejectHandover(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Handover handover = handoverRepository.findByBooking_BookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi bàn giao"));
        handoverRepository.delete(handover);
    }
}