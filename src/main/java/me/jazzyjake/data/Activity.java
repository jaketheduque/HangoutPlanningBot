package me.jazzyjake.data;

public class Activity {
    private final long id;
    private final String activity;

    public Activity(long id, String activity) {
        this.id = id;
        this.activity = activity;
    }

    public long getId() {
        return id;
    }

    public String getActivity() {
        return activity;
    }
}
