package com.example.supportcenter_01.RoomDataBase;

import static com.example.supportcenter_01.Login.UserEmail;

import android.content.SharedPreferences;

public class LeaveApply {

    private String leave;
    private String userEmail;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private float hours;
    private String reason;
    private float salary;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {

        this.userEmail = UserEmail;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getHours() {
        return hours;
    }

    public void setHours(float hours) {
        this.hours = hours;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }



}
