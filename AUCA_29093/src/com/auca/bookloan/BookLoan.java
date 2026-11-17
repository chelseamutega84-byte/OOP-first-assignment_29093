package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.main.Validator;
import com.auca.student.Student;
import java.time.LocalDate;

public class BookLoan implements Identifiable, Registrable {

    private String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate, LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    public String getLoanId() {
        return loanId;
    }

    public Student getStudent() {
        return student;
    }

    public LibraryBook getBook() {
        return book;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setBook(LibraryBook book) {
        this.book = book;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    @Override
    public String getId() {
        return loanId;
    }

    @Override
    public String getDisplayName() {
        return (book == null ? "?" : book.getTitle()) + " -> " + (student == null ? "?" : student.getDisplayName());
    }

    /** A loan is active while the book has not been returned. */
    @Override
    public boolean isActive() {
        return returnedDate == null;
    }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId("Loan ID", loanId);
        Validator.requireNotNull("Student", student);
        Validator.requireNotNull("Book", book);
        Validator.requireNotNull("Borrowed date", borrowedDate);
        Validator.requireNotNull("Due date", dueDate);
        if (!dueDate.isAfter(borrowedDate)) {
            throw new ValidationException("Due date must be after the borrowed date.");
        }
        if (returnedDate != null && returnedDate.isBefore(borrowedDate)) {
            throw new ValidationException("Returned date cannot be before the borrowed date.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + loanId + "] " + getDisplayName() + " due " + dueDate
                + (returnedDate == null ? " (not returned)" : " (returned " + returnedDate + ")");
    }

    @Override
    public String toString() {
        return "BookLoan{id=" + loanId
                + ", student=" + (student == null ? "none" : student.getDisplayName())
                + ", book=" + (book == null ? "none" : book.getTitle())
                + ", borrowed=" + borrowedDate + ", due=" + dueDate
                + ", returned=" + (returnedDate == null ? "not yet" : returnedDate) + "}";
    }
}
