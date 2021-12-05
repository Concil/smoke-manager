package de.devflo.smokemanager.entitys;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PacketsBuyed extends RealmObject {
    @PrimaryKey
    public String id;

    public long timestamp;
    public String packetID;
    public double currentPrice;
}
