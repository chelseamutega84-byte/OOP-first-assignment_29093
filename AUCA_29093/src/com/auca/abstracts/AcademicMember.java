package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.main.Validator;
import java.time.LocalDate;

public abstract class AcademicMember extends Person {

    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    public AcademicMember(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public Department getDepartment() {
        return department;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public void setInstitutionalEmail(String institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    /** Each subclass states its academic category (e.g. STUDENT, TEACHER). */
    public abstract String getAcademicCategory();

    @Override
    public boolean isActive() {
        return DEFAULT_STATUS.equalsIgnoreCase(academicStatus);
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Validator.requireId("Member number", memberNumber);
        Validator.requireNotNull("Department", department);
        Validator.requireNotFuture("Join date", joinDate);
        Validator.requireOneOf("Academic status", academicStatus, Validator.ACADEMIC_STATUSES);
        Validator.requireEmail("Institutional email", institutionalEmail);
        Validator.requireLength("Advisor name", advisorName, 2, 120);
    }

    @Override
    public String getRegistrationSummary() {
        return getAcademicCategory() + " " + getDisplayName() + " [" + getId() + "] - "
                + (department == null ? "no department" : department.getDepartmentName());
    }

    @Override
    public String toString() {
        return super.toString() + ", AcademicMember{memberNumber=" + memberNumber
                + ", department=" + (department == null ? "none" : department.getDepartmentName())
                + ", joinDate=" + joinDate + ", status=" + academicStatus
                + ", institutionalEmail=" + institutionalEmail + ", advisor=" + advisorName + "}";
    }
}
