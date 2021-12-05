package de.devflo.smokemanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.devflo.smokemanager.R;
import de.devflo.smokemanager.adapter.HistoryAdapter;
import de.devflo.smokemanager.entitys.Cigarette;
import de.devflo.smokemanager.entitys.HistoryModel;
import de.devflo.smokemanager.entitys.PacketsBuyed;
import de.devflo.smokemanager.entitys.ReportSearchModel;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

public class ReportActivity extends AppCompatActivity {

    // [Database] //
    private Realm realm;
    private RealmQuery<Cigarette> cigs;
    private RealmQuery<PacketsBuyed> packetsBuyed;

    ArrayList<ReportSearchModel> searchList = new ArrayList<>();
    ArrayList<HistoryModel> historyContent = new ArrayList<>();

    TextView descriptionTitle;
    ListView description;

    ArrayList<String> listContent = new ArrayList<>();
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getSupportActionBar().setElevation(0);
        setTitle("Rückblick");

        listContent.clear();
        List<EventDay> events = new ArrayList<>();

        realm = Realm.getDefaultInstance();
        cigs = realm.where(Cigarette.class);

        CalendarView calendarView = findViewById(R.id.calendarView);
        description = findViewById(R.id.description);

        descriptionTitle = findViewById(R.id.descriptionTitle);

        historyAdapter = new HistoryAdapter(this, historyContent);
        description.setAdapter(historyAdapter);


        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        checkDateHistory(currentDate);

        calendarView.setEvents(events);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                historyContent.clear();
                descriptionTitle.setText("");

                checkDateHistory(clickedDayCalendar);
            }
        });
        description.setEmptyView(findViewById(R.id.descriptionEmpty));
    }


    public void checkDateHistory(Calendar clickedCalendar) {
        int smokedCigs = 0;
        int buyedPacks = 0;

        int clickedYear = clickedCalendar.get(Calendar.YEAR);
        int clickedMonth = clickedCalendar.get(Calendar.MONTH);
        int clickedDay = clickedCalendar.get(Calendar.DAY_OF_MONTH);


        cigs = realm.where(Cigarette.class);
        packetsBuyed = realm.where(PacketsBuyed.class);

        ArrayList<HistoryModel> tempHistory = new ArrayList<>();
        tempHistory.clear();

        historyContent.clear();

        for(Cigarette cigarette : cigs.sort("timestamp", Sort.DESCENDING).findAll()) {
            Calendar cigCalender = Calendar.getInstance();
            cigCalender.setTimeInMillis(cigarette.timestamp);
            int checkYear = cigCalender.get(Calendar.YEAR);
            int checkMonth = cigCalender.get(Calendar.MONTH);
            int checkDay = cigCalender.get(Calendar.DAY_OF_MONTH);

            if(clickedYear == checkYear && clickedMonth == checkMonth && clickedDay == checkDay) {
                HistoryModel history = new HistoryModel();
                history.type = 0;
                history.timestamp = cigarette.timestamp;
                tempHistory.add(history);

                smokedCigs++;
            }

            System.out.println("[[ DEBUG ]] " + cigarette);
        }

        for(PacketsBuyed buyed : packetsBuyed.sort("timestamp", Sort.DESCENDING).findAll()) {
            Calendar cigCalender = Calendar.getInstance();
            cigCalender.setTimeInMillis(buyed.timestamp);
            int checkYear = cigCalender.get(Calendar.YEAR);
            int checkMonth = cigCalender.get(Calendar.MONTH);
            int checkDay = cigCalender.get(Calendar.DAY_OF_MONTH);

            if(clickedYear == checkYear && clickedMonth == checkMonth && clickedDay == checkDay) {
                HistoryModel history = new HistoryModel();
                history.type = 1;
                history.timestamp = buyed.timestamp;
                tempHistory.add(history);

                buyedPacks++;
            }
        }

        Collections.sort(tempHistory);


        for(HistoryModel history : tempHistory) {
            historyContent.add(history);
        }

        historyAdapter.notifyDataSetChanged();
        descriptionTitle.setText("Zigaretten: " + smokedCigs + " Packungen: " + buyedPacks);
        //count.setText(String.valueOf(cigTodayCount));
    }
}
