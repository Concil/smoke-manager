package de.devflo.smokemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import de.devflo.smokemanager.R;
import de.devflo.smokemanager.entitys.SettingsModel;

public class SettingsListAdapter extends ArrayAdapter<SettingsModel> {
    Context mContext;
    public SettingsListAdapter(@NonNull Context context, @NonNull List<SettingsModel> objects) {
        super(context, 0, objects);

        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SettingsModel setting = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_settings, parent, false);


        TextView name = convertView.findViewById(R.id.title);
        ImageView icon = convertView.findViewById(R.id.icon);
        TextView description = convertView.findViewById(R.id.description);
        name.setText(setting.title);
        description.setText(setting.description);
        icon.setImageDrawable(mContext.getDrawable(setting.drawable));
        return convertView;
    }
}
