package fpt.swp391.carrentalsystem.validation;

import fpt.swp391.carrentalsystem.dto.ChangePasswordForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, ChangePasswordForm> {

    @Override
    public boolean isValid(ChangePasswordForm form, ConstraintValidatorContext context) {
        if (form == null) return true;

        String np = form.getNewPassword();
        String cp = form.getConfirmPassword();

        if (np == null || cp == null) return true;

        boolean ok = np.equals(cp);
        if (!ok) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return ok;
    }
}