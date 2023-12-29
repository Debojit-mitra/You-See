package com.bunny.entertainment.yousee.models;

public class PeopleWorkDetailsModel {

    public String workYear, workTitle, workRole, workRoleName, workRating, workType;
    public int workNumber;

    public PeopleWorkDetailsModel() {
    }

    public PeopleWorkDetailsModel(String workYear, String workTitle, String workRole, String workRoleName, String workRating, String workType, int workNumber) {
        this.workYear = workYear;
        this.workTitle = workTitle;
        this.workRole = workRole;
        this.workRoleName = workRoleName;
        this.workRating = workRating;
        this.workType = workType;
        this.workNumber = workNumber;
    }

    public String getWorkYear() {
        return workYear;
    }

    public void setWorkYear(String workYear) {
        this.workYear = workYear;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public String getWorkRole() {
        return workRole;
    }

    public void setWorkRole(String workRole) {
        this.workRole = workRole;
    }

    public String getWorkRoleName() {
        return workRoleName;
    }

    public void setWorkRoleName(String workRoleName) {
        this.workRoleName = workRoleName;
    }

    public String getWorkRating() {
        return workRating;
    }

    public void setWorkRating(String workRating) {
        this.workRating = workRating;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public int getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(int workNumber) {
        this.workNumber = workNumber;
    }
}
