package com.bango.ngintipbot.models;

import android.content.Context;
import android.location.Location;

public class GatherDataParameter {
	private Location location;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
