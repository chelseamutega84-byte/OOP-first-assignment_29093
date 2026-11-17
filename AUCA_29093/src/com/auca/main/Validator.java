package com.auca.main;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reusable validation rules (Section 8 of the assignment).
 * Every method throws ValidationException with a clear message.
 */
public final class Validator {

    public static final List<String> ACADEMIC_STATUSES   = List.of("ACTIVE", "INACTIVE");
    public static final List<String> EMPLOYMENT_TYPES    = List.of("FULL_TIME", "PART_TIME", "CONTRACT");
    public static final List<String> SEMESTERS           = List.of("SEMESTER_1", "SEMESTER_2", "SEMESTER_3");
    public static final List<String> ENROLLMENT_STATUSES = List.of("ACTIVE", "COMPLETED", "DROPPED");
    public static final List<String> ASSESSMENT_TYPES    = List.of("QUIZ", "ASSIGNMENT", "MIDTERM", "EXAM", "PROJECT");
    public static final List<String> PAYMENT_METHODS     = List.of("CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD");

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?\\d+$");

    private Validator() { }

    public static String requireText(String label, String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(label + " cannot be blank.");
        }
        return value.trim();
    }

    public static void requireLength(String label, String value, int min, int max) throws ValidationException {
        String text = requireText(label, value);
        if (text.length() < min || text.length() > max) {
            throw new ValidationException(label + " must be " + min + "-" + max + " characters.");
        }
    }

    public static void requireId(String label, String value) throws ValidationException {
        requireLength(label, value, Identifiable.MIN_ID_LENGTH, Identifiable.MAX_ID_LENGTH);
    }

    public static void requireName(String label, String value) throws ValidationException {
        requireLength(label, value, Registrable.MIN_NAME_LENGTH, Registrable.MAX_NAME_LENGTH);
    }

    public static void requireEmail(String label, String value) throws ValidationException {
        String text = requireText(label, value);
        if (text.length() > Registrable.MAX_EMAIL_LENGTH) {
            throw new ValidationException(label + " must not exceed " + Registrable.MAX_EMAIL_LENGTH + " characters.");
        }
        if (!EMAIL.matcher(text).matches()) {
            throw new ValidationException(label + " is not a valid email address.");
        }
    }

    public static void requirePhone(String label, String value) throws ValidationException {
        String text = requireText(label, value);
        if (!PHONE.matcher(text).matches()) {
            throw new ValidationException(label + " may contain only digits with an optional leading +.");
        }
        int digits = text.startsWith("+") ? text.length() - 1 : text.length();
        if (digits < Registrable.MIN_PHONE_LENGTH || digits > Registrable.MAX_PHONE_LENGTH) {
            throw new ValidationException(label + " must contain " + Registrable.MIN_PHONE_LENGTH
                    + "-" + Registrable.MAX_PHONE_LENGTH + " digits.");
        }
    }

    public static void requireNotNull(String label, Object value) throws ValidationException {
        if (value == null) {
            throw new ValidationException(label + " is required.");
        }
    }

    public static void requirePast(String label, LocalDate date) throws ValidationException {
        requireNotNull(label, date);
        if (!date.isBefore(LocalDate.now())) {
            throw new ValidationException(label + " must be in the past.");
        }
    }

    public static void requireNotFuture(String label, LocalDate date) throws ValidationException {
        requireNotNull(label, date);
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException(label + " cannot be in the future.");
        }
    }

    public static void requireRange(String label, int value, int min, int max) throws ValidationException {
        if (value < min || value > max) {
            throw new ValidationException(label + " must be between " + min + " and " + max + ".");
        }
    }

    public static void requirePositive(String label, BigDecimal value) throws ValidationException {
        requireNotNull(label, value);
        if (value.signum() <= 0) {
            throw new ValidationException(label + " must be greater than zero.");
        }
    }

    public static void requireOneOf(String label, String value, List<String> allowed) throws ValidationException {
        String text = requireText(label, value);
        for (String option : allowed) {
            if (option.equalsIgnoreCase(text)) {
                return;
            }
        }
        throw new ValidationException(label + " must be one of " + allowed + ".");
    }
}
