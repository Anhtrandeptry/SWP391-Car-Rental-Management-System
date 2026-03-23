package fpt.swp391.carrentalsystem.dto;

import fpt.swp391.carrentalsystem.validation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordMatches
public class ChangePasswordForm {

    @NotBlank(message = "Mật khẩu hiện tại không được để trống.")
    private String currentPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống.")
    @Size(min = 8, max = 72, message = "Mật khẩu mới phải từ 8 đến 72 ký tự.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$",
            message = "Mật khẩu mới phải có ít nhất 1 chữ và 1 số."
    )
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống.")
    private String confirmPassword;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}