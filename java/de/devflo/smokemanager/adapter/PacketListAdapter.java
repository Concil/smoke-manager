package de.devflo.smokemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import de.devflo.smokemanager.R;
import de.devflo.smokemanager.entitys.Packet;

public class PacketListAdapter extends ArrayAdapter<Packet> {

    public PacketListAdapter(@NonNull Context context, @NonNull List<Packet> objects) {
        super(context, 0, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Packet packet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_packets, parent, false);

        TextView name = convertView.findViewById(R.id.name);
        TextView count = convertView.findViewById(R.id.count);
        TextView price = convertView.findViewById(R.id.price);
        if (packet != null) {
            name.setText(packet.name);
            count.setText(String.format("Anzahl: %s", String.valueOf(packet.count)));
            price.setText(String.format("%.2f", packet.money) + " €");
        } else {
            name.setText("Error: Object not found");
            count.setText("null");
            price.setText("null");
        }
        return convertView;
    }
}
