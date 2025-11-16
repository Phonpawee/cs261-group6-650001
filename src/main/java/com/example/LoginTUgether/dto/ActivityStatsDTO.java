package com.example.LoginTUgether.dto;

public class ActivityStatsDTO {

    private Long eventId;
    private int capacity;
    private long applied;
    private long selected;
    private long waitlisted;
    private long rejected;
    private long remaining;

    // ===== Getter & Setter =====
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public long getApplied() {
        return applied;
    }

    public void setApplied(long applied) {
        this.applied = applied;
    }

    public long getSelected() {
        return selected;
    }

    public void setSelected(long selected) {
        this.selected = selected;
    }

    public long getWaitlisted() {
        return waitlisted;
    }

    public void setWaitlisted(long waitlisted) {
        this.waitlisted = waitlisted;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getRemaining() {
        return remaining;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }
}



