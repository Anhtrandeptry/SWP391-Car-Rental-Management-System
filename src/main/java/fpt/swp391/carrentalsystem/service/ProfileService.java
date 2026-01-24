package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.ChangePasswordForm;
import fpt.swp391.carrentalsystem.dto.UpdateProfileForm;
import fpt.swp391.carrentalsystem.dto.UserProfile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProfileService {

    private final JdbcTemplate jdbc;

    public ProfileService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public UserProfile getProfile(int userId) {
        String sql = """
            SELECT user_id, first_name, last_name, gender, email,
                   phone_number, address, national_id, drivers_license
            FROM users
            WHERE user_id = ?
        """;

        return jdbc.queryForObject(sql, (rs, rowNum) -> {
            UserProfile u = new UserProfile();
            u.setUserId(rs.getInt("user_id"));
            u.setFirstName(rs.getString("first_name"));
            u.setLastName(rs.getString("last_name"));
            u.setGender(rs.getString("gender"));
            u.setEmail(rs.getString("email"));
            u.setPhoneNumber(rs.getString("phone_number"));
            u.setAddress(rs.getString("address"));
            u.setNationalId(rs.getString("national_id"));
            u.setDriversLicense(rs.getString("drivers_license"));
            return u;
        }, userId);
    }

    public void updateProfile(int userId, UpdateProfileForm f) {
        String sql = """
            UPDATE users
            SET first_name=?, last_name=?, gender=?, phone_number=?, address=?,
                national_id=?, drivers_license=?
            WHERE user_id=?
        """;

        jdbc.update(sql,
                f.getFirstName(),
                f.getLastName(),
                f.getGender(),
                f.getPhoneNumber(),
                f.getAddress(),
                f.getNationalId(),
                f.getDriversLicense(),
                userId
        );
    }

    // demo: password_hash đang lưu plain text
    public void changePassword(int userId, ChangePasswordForm f) {
        if (f.getNewPassword() == null || f.getNewPassword().length() < 8) {
            throw new RuntimeException("Mật khẩu mới phải >= 8 ký tự.");
        }
        if (!f.getNewPassword().equals(f.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu mới không khớp.");
        }

        String current = jdbc.queryForObject(
                "SELECT password_hash FROM users WHERE user_id=?",
                String.class, userId
        );

        if (current == null || !f.getCurrentPassword().equals(current)) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng.");
        }

        jdbc.update("UPDATE users SET password_hash=? WHERE user_id=?",
                f.getNewPassword(), userId);
    }

    public Map<String, Object> getStats(int userId) {
        Long trips = jdbc.queryForObject(
                "SELECT COUNT(*) FROM bookings WHERE customer_id=?",
                Long.class, userId
        );

        Double rating = jdbc.queryForObject(
                "SELECT COALESCE(AVG(rating),0) FROM feedbacks WHERE customer_id=?",
                Double.class, userId
        );

        long favorites = 0; // chưa có bảng favorites

        double rounded = Math.round(rating * 10.0) / 10.0;

        return Map.of(
                "trips", trips,
                "favorites", favorites,
                "rating", rounded
        );
    }
}
