package com.jlink.flink.utils;

import java.io.Serializable;
import java.util.List;

public class DeployResult implements Serializable {

    //通用信息
    private String version;
    //yarn相关信息
    private String applicationId;
    private String  user;
    private String  queue;
    private String  state;
    private String  finalState;
    private String applicationName;
    private String  applicationType;
    private String trackingUrl;
    private String yarnTrackingUrl;
    private Long  startTime;
    private String applicationAttemptId;

    //任务相关信息
    private String jobId;
    private String jobStatus;
    private String jobGraph;
    private String jobName;
    private Integer maximumParallelism;

    //业务相关信息
    private String businessId;

    //记录任务发布类型
    private String deployTarget;

    //存储保存点地址
    private String savePointPath;

    private List<JobInfo> jobInfos;

    //存储容器id信息
    private String amContainerId;
    private List<String> containerIds;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFinalState() {
        return finalState;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getApplicationAttemptId() {
        return applicationAttemptId;
    }

    public void setApplicationAttemptId(String applicationAttemptId) {
        this.applicationAttemptId = applicationAttemptId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobGraph() {
        return jobGraph;
    }

    public void setJobGraph(String jobGraph) {
        this.jobGraph = jobGraph;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getMaximumParallelism() {
        return maximumParallelism;
    }

    public void setMaximumParallelism(Integer maximumParallelism) {
        this.maximumParallelism = maximumParallelism;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getDeployTarget() {
        return deployTarget;
    }

    public void setDeployTarget(String deployTarget) {
        this.deployTarget = deployTarget;
    }

    public String getSavePointPath() {
        return savePointPath;
    }

    public void setSavePointPath(String savePointPath) {
        this.savePointPath = savePointPath;
    }

    public List<JobInfo> getJobInfos() {
        return jobInfos;
    }

    public void setJobInfos(List<JobInfo> jobInfos) {
        this.jobInfos = jobInfos;
    }

    public String getAmContainerId() {
        return amContainerId;
    }

    public void setAmContainerId(String amContainerId) {
        this.amContainerId = amContainerId;
    }

    public List<String> getContainerIds() {
        return containerIds;
    }

    public void setContainerIds(List<String> containerIds) {
        this.containerIds = containerIds;
    }

    public String getYarnTrackingUrl() {
        return yarnTrackingUrl;
    }

    public void setYarnTrackingUrl(String yarnTrackingUrl) {
        this.yarnTrackingUrl = yarnTrackingUrl;
    }
}
