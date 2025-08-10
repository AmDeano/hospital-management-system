package com.hospital.employee.entity;

/**
 * Enumeration for work days of the week
 */
/*public enum WorkDay {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private final String displayName;

    WorkDay(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Check if the day is a weekend day
     * @return true if Saturday or Sunday
     */
    /*public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }

    /**
     * Check if the day is a weekday
     * @return true if Monday through Friday
     */
    /*public boolean isWeekday() {
        return !isWeekend();
    }
}*/
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum WorkDay {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private final String displayName;

    WorkDay(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Serialize enum as its name() (e.g. MONDAY)
    @JsonValue
    public String toJson() {
        return name();
    }

    @Override
    public String toString() {
        return name();
    }

    // Optional: for deserialization from string to enum
    @JsonCreator
    public static WorkDay fromJson(String value) {
        return WorkDay.valueOf(value.toUpperCase());
    }

    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }

    public boolean isWeekday() {
        return !isWeekend();
    }
}
