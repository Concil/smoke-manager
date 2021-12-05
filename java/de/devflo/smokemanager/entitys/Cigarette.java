package de.devflo.smokemanager.entitys;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Cigarette extends RealmObject {
    @PrimaryKey
    public String id;
    public long timestamp;

    //public String getTimestamp() { return this.timestamp; }
    //public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
