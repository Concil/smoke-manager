package de.devflo.smokemanager;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SmokeManager extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("smokemanager.realm")
                .schemaVersion(2)
                .build();
        Realm.setDefaultConfiguration(config);
    }


}
