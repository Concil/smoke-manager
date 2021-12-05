package de.devflo.smokemanager.entitys;

import android.graphics.drawable.Drawable;

public class SettingsModel {
    public int id;
    public String title;
    public String description;
    public int drawable;

    public SettingsModel(int id, String title, String description, int drawable) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.drawable = drawable;
    }
}
