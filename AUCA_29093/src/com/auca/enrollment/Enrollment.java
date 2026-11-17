package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.main.Validator;
import com.auca.student.Student;
import java.time.LocalDate;

public class Enrollment implements Identifiable, Registrable {

    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate, String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public String getSemester() {
        return semester;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override
    public String getId() {
        return enrollmentId;
    }

    @Override
    public String getDisplayName() {
        return (student == null ? "?" : student.getDisplayName()) + " in "
                + (course == null ? "?" : course.getCourseCode()) + " (" + semester + ")";
    }

    @Override
    public boolean isActive() {
        return DEFAULT_STATUS.equalsIgnoreCase(enrollmentStatus);
    }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId("Enrollment ID", enrollmentId);
        Validator.requireNotNull("Student", student);
        Validator.requireNotNull("Course", course);
        Validator.requireNotFuture("Enrollment date", enrollmentDate);
        Validator.requireOneOf("Semester", semester, Validator.SEMESTERS);
        Validator.requireOneOf("Enrollment status", enrollmentStatus, Validator.ENROLLMENT_STATUSES);
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + enrollmentId + "] " + getDisplayName() + " - " + enrollmentStatus;
    }

    @Override
    public String toString() {
        return "Enrollment{id=" + enrollmentId
                + ", student=" + (student == null ? "none" : student.getDisplayName())
                + ", course=" + (course == null ? "none" : course.getDisplayName())
                + ", date=" + enrollmentDate + ", semester=" + semester + ", status=" + enrollmentStatus + "}";
    }
}
