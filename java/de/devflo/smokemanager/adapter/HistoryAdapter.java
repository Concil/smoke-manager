package de.devflo.smokemanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.devflo.smokemanager.R;
import de.devflo.smokemanager.entitys.Cigarette;
import de.devflo.smokemanager.entitys.HistoryModel;

public class HistoryAdapter extends ArrayAdapter<HistoryModel>  {

    Context mContext;

    public HistoryAdapter(@NonNull Context context, @NonNull List<HistoryModel> objects) {
        super(context, 0, objects);

        this.mContext = context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HistoryModel history = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_history, parent, false);

        TextView time = convertView.findViewById(R.id.time);
        TextView after = convertView.findViewById(R.id.after);
        ImageView icon = convertView.findViewById(R.id.icon);




        if(history != null) {
            Calendar cigCalender = Calendar.getInstance();
            cigCalender.setTimeInMillis(history.timestamp);
            int checkYear = cigCalender.get(Calendar.YEAR);
            int checkMonth = cigCalender.get(Calendar.MONTH);
            int checkDay = cigCalender.get(Calendar.DAY_OF_MONTH);

            Calendar currentCalender = Calendar.getInstance();
            currentCalender.setTime(new Date());
            int currentYear = currentCalender.get(Calendar.YEAR);
            int currentMonth = currentCalender.get(Calendar.MONTH);
            int currentDay = currentCalender.get(Calendar.DAY_OF_MONTH);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(history.timestamp);
            String format = "";
            if(checkYear == currentYear && checkMonth == currentMonth && checkDay == currentDay) {
                format = new SimpleDateFormat("HH:mm").format(calendar.getTime());
            } else {
                format = new SimpleDateFormat("dd.MM.yyy HH:mm").format(calendar.getTime());
            }
            time.setText(format + " Uhr");


            if(history.type == 0) {
                icon.setImageDrawable(mContext.getDrawable(R.drawable.cig_02));
            } else if(history.type == 1) {
                icon.setImageDrawable(mContext.getDrawable(R.drawable.cigs_pack_02));
            }


            long afterTime = ((System.currentTimeMillis() / 1000) - (history.timestamp / 1000));
            long hours;
            long minutes;
            hours = afterTime / 3600;
            minutes = (afterTime % 3600) / 60;
            after.setText(String.format("%02d Std. %02d min", hours, minutes));

            return convertView;
        }

        return null;
    }
}


