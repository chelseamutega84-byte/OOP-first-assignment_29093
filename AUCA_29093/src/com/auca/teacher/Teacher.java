package com.auca.teacher;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.main.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Teacher extends AcademicMember {

    private String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType;

    public Teacher(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName, String employeeNumber, String specialization, LocalDate hireDate, BigDecimal salary, String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth, memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    @Override
    public String getRoleDescription() {
        return "Teacher";
    }

    @Override
    public String getAcademicCategory() {
        return "TEACHER";
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Validator.requireId("Employee number", employeeNumber);
        Validator.requireLength("Specialization", specialization, 2, 120);
        Validator.requireNotFuture("Hire date", hireDate);
        Validator.requirePositive("Salary", salary);
        Validator.requireText("Office number", officeNumber);
        Validator.requireOneOf("Employment type", employmentType, Validator.EMPLOYMENT_TYPES);
    }

    @Override
    public String toString() {
        return super.toString() + ", Teacher{employeeNumber=" + employeeNumber + ", specialization=" + specialization
                + ", hireDate=" + hireDate + ", salary=" + salary + ", office=" + officeNumber
                + ", employmentType=" + employmentType + "}";
    }
}
