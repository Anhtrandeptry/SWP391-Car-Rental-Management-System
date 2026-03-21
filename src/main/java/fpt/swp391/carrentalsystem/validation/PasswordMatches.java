package fpt.swp391.carrentalsystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "Xác nhận mật khẩu mới không khớp.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}