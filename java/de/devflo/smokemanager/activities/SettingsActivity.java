package de.devflo.smokemanager.activities;
import de.devflo.smokemanager.R;
import de.devflo.smokemanager.adapter.SettingsListAdapter;
import de.devflo.smokemanager.entitys.Cigarette;
import de.devflo.smokemanager.entitys.Packet;
import de.devflo.smokemanager.entitys.PacketsBuyed;
import de.devflo.smokemanager.entitys.SettingsModel;
import de.devflo.smokemanager.helpers.SharedPreferencesHelper;
import io.realm.Realm;
import io.realm.RealmResults;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.shashank.sony.fancytoastlib.FancyToast;


import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    Activity mActivity;

    // [Database] //
    private Realm realm;
    private RealmResults<Cigarette> cigs;
    private RealmResults<Packet> packets;
    private RealmResults<PacketsBuyed> packetsBuyed;

    TextView item_1;
    TextView item_3;
    TextView item_about;
    TextView item_delete;

    final ArrayList<SettingsModel> mainSettings = new ArrayList<>();
    ListView listMainSettings;
    ListView listOtherSettings;

    int dayLimit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setElevation(0);
        setTitle("Einstellungen");
        mActivity = this;
        realm = Realm.getDefaultInstance();

        if(SharedPreferencesHelper.contains(this, "dayLimit")) dayLimit = (int) SharedPreferencesHelper.get(this, "dayLimit", 0);


        mainSettings.add(new SettingsModel(0, "Packung Datenbank", "Zigarettenpackungen hinzufügen oder bearbeiten.", R.drawable.cigs_pack));
        mainSettings.add(new SettingsModel(1, "Tageslimit: " + dayLimit, "Ändern Sie Ihren Tageslimit.", R.drawable.daylimit));
        mainSettings.add(new SettingsModel(2, "Alle Daten Löschen", "Sämtliche erfasste Daten werden sofort gelöscht.", R.drawable.delete));
        mainSettings.add(new SettingsModel(3, "Über diese App", "", R.drawable.about));


        // [VIEWS] //
        final ListView listMainSettings = findViewById(R.id.mainSettings);
        final SettingsListAdapter settingsListAdapter = new SettingsListAdapter(getApplicationContext(), mainSettings);
        settingsListAdapter.setNotifyOnChange(true);
        listMainSettings.setAdapter(settingsListAdapter);

        listMainSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                SettingsModel setting = mainSettings.get(position);
                if(setting.id == 0) {
                    Intent intent = new Intent(getApplicationContext(), SettingsPacketActivity.class);
                    startActivity(intent);
                } else if(setting.id == 1) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
                    View viewInflated = LayoutInflater.from(mActivity).inflate(R.layout.dialog_add_daylimit, (ViewGroup) findViewById(android.R.id.content), false);
                    final EditText newLimit = viewInflated.findViewById(R.id.newDayLimit);
                    dialogBuilder.setView(viewInflated);
                    dialogBuilder.setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            if(!newLimit.getText().toString().isEmpty()) {
                                dayLimit = Integer.parseInt(newLimit.getText().toString());
                                SharedPreferencesHelper.put(mActivity, "dayLimit", dayLimit);
                                mainSettings.get(position).title = "Tageslimit: " + dayLimit;
                                settingsListAdapter.notifyDataSetChanged();
                                FancyToast.makeText(mActivity,"Tageslimit gesetzt",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false);
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton(android.R.string.cancel, null);
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.show();
                } else if(setting.id == 2) {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            cigs =  bgRealm.where(Cigarette.class).findAll();
                            packets =  bgRealm.where(Packet.class).findAll();
                            packetsBuyed =  bgRealm.where(PacketsBuyed.class).findAll();

                            cigs.deleteAllFromRealm();
                            packets.deleteAllFromRealm();
                            packetsBuyed.deleteAllFromRealm();

                            SharedPreferencesHelper.remove(getApplicationContext(), "dayLimit");
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            mActivity.finishAffinity();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            System.out.println("[[ ERROR ]] cant delete: " + error);
                            FancyToast.makeText(getApplicationContext(),"Fehler in der App. Kann daten nicht löschen!",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                        }
                    });
                } else if(setting.id == 3) {
                    Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
