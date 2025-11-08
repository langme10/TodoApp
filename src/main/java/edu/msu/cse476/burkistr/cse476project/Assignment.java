package edu.msu.cse476.burkistr.cse476project;

import java.io.Serializable;

public class Assignment implements Serializable {

    private String assignmentName;
    private String assignmentId;
    private String className;
    private String assignmentType;
    private int year;
    private int month;
    private int day;
    private boolean completed;
    private String location;


    private String color;

    public Assignment() {

    }

    public Assignment(String assignmentName, String className, String classLocation, String assignmentType,
                      int year, int month, int day, String color) {
        this.assignmentName = assignmentName;
        this.className = className;
        this.location = classLocation;
        this.assignmentType = assignmentType;
        this.year = year;
        this.month = month;
        this.day = day;
        this.completed = false;
        this.color = color;
    }


    public Assignment(String assignmentName, String className,String classLocation, String assignmentType,
                      int year, int month, int day) {
        this(assignmentName, className, classLocation, assignmentType, year, month, day, "Choose Color");
    }

    // Getters and Setters

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
}
