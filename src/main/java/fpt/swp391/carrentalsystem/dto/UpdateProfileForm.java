package fpt.swp391.carrentalsystem.dto;

import jakarta.validation.constraints.*;

public class UpdateProfileForm {

    private static final String NAME_REGEX = "^[\\p{L}][\\p{L}\\s'.-]{0,49}$";

    @NotBlank(message = "First name không được để trống.")
    @Size(max = 50, message = "First name tối đa 50 ký tự.")
    @Pattern(regexp = NAME_REGEX, message = "First name chỉ được chứa chữ và các ký tự . ' -")
    private String firstName;

    @NotBlank(message = "Last name không được để trống.")
    @Size(max = 50, message = "Last name tối đa 50 ký tự.")
    @Pattern(regexp = NAME_REGEX, message = "Last name chỉ được chứa chữ và các ký tự . ' -")
    private String lastName;

    @NotBlank(message = "Giới tính không được để trống.")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender phải là MALE, FEMALE hoặc OTHER.")
    private String gender;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự.")
    @Pattern(
            regexp = "^(0\\d{9}|\\+84\\d{9})$",
            message = "Số điện thoại không hợp lệ (vd: 0912345678 hoặc +84912345678)."
    )
    private String phoneNumber;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không hợp lệ.")
    @Size(max = 100, message = "Email tối đa 100 ký tự.")
    private String email;

    @NotBlank(message = "Address không được để trống.")
    @Size(min = 5, max = 255, message = "Address phải từ 5 đến 255 ký tự.")
    private String address;

    @NotBlank(message = "National ID không được để trống.")
    @Size(max = 20, message = "National ID tối đa 20 ký tự.")
    @Pattern(regexp = "^(\\d{9}|\\d{12})$", message = "CMND/CCCD phải gồm 9 hoặc 12 chữ số.")
    private String nationalId;

    @NotBlank(message = "Driver license không được để trống.")
    @Size(min = 12, max = 12, message = "Giấy phép lái xe phải 12 số.")
    @Pattern(regexp = "^\\d{12}$", message = "Giấy phép lái xe phải gồm 12 chữ số.")
    private String driversLicense;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getDriversLicense() { return driversLicense; }
    public void setDriversLicense(String driversLicense) { this.driversLicense = driversLicense; }
}
