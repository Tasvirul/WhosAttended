package com.monsteroftheheaven.whosattended;

/**
 * Created by Sayma on 8/3/2015.
 */
public class StudentInfo {
    private String studentName;
    private String studentId;

    public StudentInfo(String name, String Id ){
        this.studentName = name;
        this.studentId = Id;
    }
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    private boolean status=false;
}
