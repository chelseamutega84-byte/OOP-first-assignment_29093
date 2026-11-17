package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.main.Validator;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable {

    private String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle, int creditHours, Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public Department getDepartment() {
        return department;
    }

    public Teacher getAssignedTeacher() {
        return assignedTeacher;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setAssignedTeacher(Teacher assignedTeacher) {
        this.assignedTeacher = assignedTeacher;
    }

    @Override
    public String getId() {
        return courseId;
    }

    @Override
    public String getDisplayName() {
        return courseCode + " - " + courseTitle;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId("Course ID", courseId);
        Validator.requireLength("Course code", courseCode, 2, 20);
        Validator.requireLength("Course title", courseTitle, 2, 120);
        Validator.requireRange("Credit hours", creditHours, 1, 6);
        Validator.requireNotNull("Department", department);
        Validator.requireNotNull("Assigned teacher", assignedTeacher);
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + courseId + "] " + getDisplayName() + " (" + creditHours + " credits)";
    }

    @Override
    public String toString() {
        return "Course{id=" + courseId + ", code=" + courseCode + ", title=" + courseTitle
                + ", creditHours=" + creditHours
                + ", department=" + (department == null ? "none" : department.getDepartmentName())
                + ", teacher=" + (assignedTeacher == null ? "none" : assignedTeacher.getDisplayName()) + "}";
    }
}
