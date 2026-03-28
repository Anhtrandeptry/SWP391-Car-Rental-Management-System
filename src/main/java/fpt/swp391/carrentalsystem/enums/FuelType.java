package fpt.swp391.carrentalsystem.enums;

public enum FuelType {
    PETROL("Xăng"),
    ELECTRIC("Điện"),
    DIESEL("Dầu diesel"),
    HYBRID("Hybrid");

    private final String displayName;

    FuelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

