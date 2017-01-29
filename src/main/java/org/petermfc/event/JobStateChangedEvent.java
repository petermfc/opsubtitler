package org.petermfc.event;

import org.petermfc.Globals.JobState;
import org.petermfc.Globals.JobType;

public class JobStateChangedEvent {
    private JobType jobType;
    private JobState jobState;
    private String stringData;
    private Double doubleData;

    public JobStateChangedEvent(JobType jobType, JobState jobState, String data) {
        this.jobType = jobType;
        this.jobState = jobState;
        this.stringData = data;
    }

    public JobStateChangedEvent(JobType jobType, JobState jobState, Double data) {
        this.jobType = jobType;
        this.jobState = jobState;
        this.doubleData = data;
    }

    public JobType getJobType() {
        return this.jobType;
    }

    public JobState getJobState() {
        return jobState;
    }

    public String getStringData() {
        return stringData;
    }

    public Double getDoubleData() {
        return doubleData;
    }
}
