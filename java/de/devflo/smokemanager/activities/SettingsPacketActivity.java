package de.devflo.smokemanager.activities;
import de.devflo.smokemanager.R;
import de.devflo.smokemanager.adapter.PacketListAdapter;
import de.devflo.smokemanager.entitys.Packet;
import io.realm.Realm;
import io.realm.RealmResults;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.UUID;

public class SettingsPacketActivity extends AppCompatActivity {

    // [Database] //
    private Realm realm;
    private RealmResults<Packet> packets;

    // [View] //
    FloatingActionButton btn_add;
    ListView list;
    ArrayList<Packet> list_content = new ArrayList<>();

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_packet);
        getSupportActionBar().setElevation(0);
        mActivity = this;


        realm = Realm.getDefaultInstance();
        packets = realm.where(Packet.class).findAllAsync();


        list_content.clear();
        for(Packet pack : packets) {
            list_content.add(pack);
        }

        list = findViewById(R.id.list);
        list.setEmptyView(findViewById(R.id.listEmpty));
        final PacketListAdapter packetListAdapter = new PacketListAdapter(this, list_content);
        list.setAdapter(packetListAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Packet packet    = list_content.get(position);
                //Toast.makeText(getApplicationContext(),"Position :"+itemPosition+"\n ListItem : " + packet.toString() , Toast.LENGTH_LONG).show();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
                dialogBuilder.setTitle("Packung bearbeiten ");
                View viewInflated = LayoutInflater.from(mActivity).inflate(R.layout.dialog_add_packet, (ViewGroup) findViewById(android.R.id.content), false);

                final EditText name = viewInflated.findViewById(R.id.name);
                final EditText price = viewInflated.findViewById(R.id.price);
                final EditText count = viewInflated.findViewById(R.id.count);
                name.setText(packet.name);
                price.setText(String.valueOf(packet.money));
                count.setText(String.valueOf(packet.count));

                dialogBuilder.setView(viewInflated);
                dialogBuilder.setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if(!name.getText().toString().isEmpty() && !price.getText().toString().isEmpty() && !count.getText().toString().isEmpty()) {

                            realm.beginTransaction();
                            packet.name = name.getText().toString();
                            packet.money = Double.parseDouble(price.getText().toString());
                            packet.count = Integer.parseInt(count.getText().toString());
                            realm.commitTransaction();

                            packetListAdapter.notifyDataSetChanged();


                            FancyToast.makeText(mActivity,"Erfolgreich Bearbetet",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false);
                        } else {
                            FancyToast.makeText(mActivity,"Bitte alle Felder ausfüllen!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false);
                        }
                    }
                });
                dialogBuilder.setNegativeButton(android.R.string.cancel, null);
                dialogBuilder.setCancelable(false);
                dialogBuilder.show();
            }

        });

        btn_add = findViewById(R.id.fab_packet_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
                dialogBuilder.setTitle("Neue Packung hinzufügen:");
                View viewInflated = LayoutInflater.from(mActivity).inflate(R.layout.dialog_add_packet, (ViewGroup) findViewById(android.R.id.content), false);

                final EditText name = viewInflated.findViewById(R.id.name);
                final EditText price = viewInflated.findViewById(R.id.price);
                final EditText count = viewInflated.findViewById(R.id.count);

                dialogBuilder.setView(viewInflated);
                dialogBuilder.setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if(!name.getText().toString().isEmpty() && !price.getText().toString().isEmpty() && !count.getText().toString().isEmpty()) {
                            realm.beginTransaction();
                            Packet packet = realm.createObject(Packet.class, UUID.randomUUID().toString());
                            packet.name = name.getText().toString();
                            packet.money = Double.parseDouble(price.getText().toString());
                            packet.count = Integer.parseInt(count.getText().toString());
                            realm.commitTransaction();
                            list_content.add(packet);


                            packetListAdapter.notifyDataSetChanged();


                            FancyToast.makeText(mActivity,"Erfolgreich Hinzugefügt",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false);
                        } else {
                            FancyToast.makeText(mActivity,"Bitte alle Felder ausfüllen!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false);
                        }
                    }
                });
                dialogBuilder.setNegativeButton(android.R.string.cancel, null);
                dialogBuilder.setCancelable(false);
                dialogBuilder.show();
            }
        });
    }
}
