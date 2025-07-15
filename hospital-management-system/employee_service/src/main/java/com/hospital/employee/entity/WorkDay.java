package com.hospital.employee.entity;

/**
 * Enumeration for work days of the week
 */
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

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Check if the day is a weekend day
     * @return true if Saturday or Sunday
     */
    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }

    /**
     * Check if the day is a weekday
     * @return true if Monday through Friday
     */
    public boolean isWeekday() {
        return !isWeekend();
    }
}