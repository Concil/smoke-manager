package de.devflo.smokemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.devflo.smokemanager.R;
import de.devflo.smokemanager.entitys.Packet;
import io.realm.RealmQuery;

public class AddDialogAdapter extends BaseAdapter {
    private Context context;
    private RealmQuery<Packet> items;

    //public constructor
    public AddDialogAdapter(Context context, RealmQuery<Packet> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return (int) items.count();
    }

    @Override
    public Object getItem(int position) {
        return items.findAll().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = LayoutInflater.from(context).inflate(R.layout.listview_packets, parent, false);

        Packet packet = (Packet) getItem(position);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView count = (TextView) convertView.findViewById(R.id.count);
        TextView price = (TextView) convertView.findViewById(R.id.price);

        name.setText(packet.name);
        count.setText("Count: " + String.valueOf(packet.count));
        price.setText(String.format("%.2f", packet.money) + " €");

        return convertView;
    }
}
