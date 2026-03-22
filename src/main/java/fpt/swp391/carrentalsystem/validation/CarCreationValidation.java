package fpt.swp391.carrentalsystem.validation;

/**
 * Validation groups cho multi-step car creation form.
 * Cho phép validate từng nhóm field theo từng bước.
 */
public final class CarCreationValidation {

    private CarCreationValidation() {}

    /** Validation group cho Step 1: Thiết lập xe */
    public interface Step1 {}

    /** Validation group cho Step 2: Thông tin chi tiết */
    public interface Step2 {}
}
