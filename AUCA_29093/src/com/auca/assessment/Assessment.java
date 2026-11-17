package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.main.Validator;
import java.time.LocalDate;

public class Assessment implements Identifiable, Registrable {

    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType, double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public double getMaximumScore() {
        return maximumScore;
    }

    public double getObtainedScore() {
        return obtainedScore;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public void setMaximumScore(double maximumScore) {
        this.maximumScore = maximumScore;
    }

    public void setObtainedScore(double obtainedScore) {
        this.obtainedScore = obtainedScore;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String getId() {
        return assessmentId;
    }

    @Override
    public String getDisplayName() {
        return assessmentType + " (" + obtainedScore + "/" + maximumScore + ")";
    }

    @Override
    public boolean isActive() {
        return enrollment != null && enrollment.isActive();
    }

    @Override
    public void validate() throws ValidationException {
        Validator.requireId("Assessment ID", assessmentId);
        Validator.requireNotNull("Enrollment", enrollment);
        if (!enrollment.isActive()) {
            throw new ValidationException("Only an active enrollment can be assessed.");
        }
        Validator.requireOneOf("Assessment type", assessmentType, Validator.ASSESSMENT_TYPES);
        if (maximumScore <= 0) {
            throw new ValidationException("Maximum score must be greater than zero.");
        }
        if (obtainedScore < 0 || obtainedScore > maximumScore) {
            throw new ValidationException("Obtained score must be between 0 and " + maximumScore + ".");
        }
        Validator.requireNotFuture("Assessment date", assessmentDate);
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + assessmentId + "] " + getDisplayName() + " for "
                + (enrollment == null ? "?" : enrollment.getDisplayName());
    }

    @Override
    public String toString() {
        return "Assessment{id=" + assessmentId
                + ", enrollment=" + (enrollment == null ? "none" : enrollment.getDisplayName())
                + ", type=" + assessmentType + ", maximumScore=" + maximumScore
                + ", obtainedScore=" + obtainedScore + ", date=" + assessmentDate + "}";
    }
}
