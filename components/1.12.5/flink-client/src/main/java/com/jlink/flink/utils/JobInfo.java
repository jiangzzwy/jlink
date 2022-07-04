package com.jlink.flink.utils;

import java.io.Serializable;

public class JobInfo implements Serializable {
    String jobId;
    String status;
    String name;

    public JobInfo(String jobId, String name,String status) {
        this.jobId = jobId;
        this.name=name;
        this.status = status;
    }

    public JobInfo() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
