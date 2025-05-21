package com.soremed.backend.dto;

public class DashboardStatsDTO {
    private long inProgress;
    private long completed;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(long inProgress, long completed) {
        this.inProgress = inProgress;
        this.completed  = completed;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}