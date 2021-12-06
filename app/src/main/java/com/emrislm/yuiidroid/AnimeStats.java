package com.emrislm.yuiidroid;

public class AnimeStats {
    private int watching;
    private int completed;
    private int on_hold;
    private int dropped;
    private int plan_to_watch;
    private int total;

    public int getWatching() {
        return watching;
    }
    public void setWatching(int watching) {
        this.watching = watching;
    }

    public int getCompleted() {
        return completed;
    }
    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getOn_hold() {
        return on_hold;
    }
    public void setOn_hold(int on_hold) {
        this.on_hold = on_hold;
    }

    public int getDropped() {
        return dropped;
    }
    public void setDropped(int dropped) {
        this.dropped = dropped;
    }

    public int getPlan_to_watch() {
        return plan_to_watch;
    }
    public void setPlan_to_watch(int plan_to_watch) {
        this.plan_to_watch = plan_to_watch;
    }

    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
}
