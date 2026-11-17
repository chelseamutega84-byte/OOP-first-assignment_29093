package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.main.Validator;
import com.auca.student.Student;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePayment implements Identifiable, Registrable {

    private String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount, LocalDate paymentDate, String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Student getStudent() {
        return student;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String getId() {
        return paymentId;
    }

    @Override
    public String getDisplayName() {
        return "Payment " + receiptNumber + " (" + amount + ")";
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId("Payment ID", paymentId);
        Validator.requireNotNull("Student", student);
        Validator.requirePositive("Amount", amount);
        Validator.requireNotFuture("Payment date", paymentDate);
        Validator.requireOneOf("Payment method", paymentMethod, Validator.PAYMENT_METHODS);
        Validator.requireText("Receipt number", receiptNumber);
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + paymentId + "] " + amount + " by " + (student == null ? "?" : student.getDisplayName())
                + " on " + paymentDate;
    }

    @Override
    public String toString() {
        return "FeePayment{id=" + paymentId
                + ", student=" + (student == null ? "none" : student.getDisplayName())
                + ", amount=" + amount + ", date=" + paymentDate + ", method=" + paymentMethod
                + ", receipt=" + receiptNumber + "}";
    }
}
