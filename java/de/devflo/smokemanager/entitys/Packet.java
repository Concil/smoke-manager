package de.devflo.smokemanager.entitys;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Packet extends RealmObject {
    @PrimaryKey
    public String id;

    public String name;
    public int count;
    public double money;
}
