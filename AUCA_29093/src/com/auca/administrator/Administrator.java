package com.auca.administrator;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;
import com.auca.main.Validator;
import java.time.LocalDate;

public class Administrator extends Person {

    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel;
    private boolean active;

    public Administrator(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String staffNumber, String jobTitle, String assignedOffice, LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setAssignedOffice(String assignedOffice) {
        this.assignedOffice = assignedOffice;
    }

    public void setEmploymentDate(LocalDate employmentDate) {
        this.employmentDate = employmentDate;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getRoleDescription() {
        return "Administrator";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Validator.requireId("Staff number", staffNumber);
        Validator.requireLength("Job title", jobTitle, 2, 120);
        Validator.requireText("Assigned office", assignedOffice);
        Validator.requireNotFuture("Employment date", employmentDate);
        Validator.requireRange("Access level", accessLevel, 1, 5);
    }

    @Override
    public String toString() {
        return super.toString() + ", Administrator{staffNumber=" + staffNumber + ", jobTitle=" + jobTitle
                + ", office=" + assignedOffice + ", employmentDate=" + employmentDate
                + ", accessLevel=" + accessLevel + ", active=" + active + "}";
    }
}
