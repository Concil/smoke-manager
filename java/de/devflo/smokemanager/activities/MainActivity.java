package de.devflo.smokemanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.droidbyme.dialoglib.AnimUtils;
import com.droidbyme.dialoglib.DroidDialog;
import com.github.clans.fab.FloatingActionButton;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import de.devflo.smokemanager.R;
import de.devflo.smokemanager.adapter.AddDialogAdapter;
import de.devflo.smokemanager.adapter.HistoryAdapter;
import de.devflo.smokemanager.entitys.Cigarette;
import de.devflo.smokemanager.entitys.HistoryModel;
import de.devflo.smokemanager.entitys.Packet;
import de.devflo.smokemanager.entitys.PacketsBuyed;
import de.devflo.smokemanager.helpers.SharedPreferencesHelper;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

import static java.util.Comparator.comparing;

public class MainActivity extends AppCompatActivity {

    // [Database] //
    private Realm realm;
    private RealmQuery<Cigarette> cigs;
    private RealmQuery<Packet> packets;
    private RealmQuery<PacketsBuyed> packetsBuyed;

    // [VIEWS] //
    FloatingActionButton addCig;
    FloatingActionButton addPacket;
    TextView count;
    TextView afterSmoke;
    TextView money;
    TextView countPacket;
    ListView todayHistory;

    HistoryAdapter historyAdapter;
    ArrayList<Cigarette> list_content = new ArrayList<>();
    ArrayList<HistoryModel> historyContent = new ArrayList<>();


    int cigTodayCount = 0;
    int packetCount = 0;
    double cigMoney = 0;


    // [STUFF] //
    Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        //SharedPreferencesHelper.clear(this);

        if(!SharedPreferencesHelper.contains(this, "importantInformationAtStart")) {
            new DroidDialog.Builder(this)
                .icon(R.drawable.ic_action_tick)
                .cancelable(true, true)
                .title(getString(R.string.DialogImportant_title))
                .content(getString(R.string.DialogImportant_content))
                .neutralButton("Dismiss", new DroidDialog.onNeutralListener() {
                    @Override
                    public void onNeutral(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .animation(AnimUtils.AnimFadeInOut)
            .show();
            SharedPreferencesHelper.put(this, "importantInformationAtStart", true);
        }


        list_content.clear();
        mActivity = this;

        count = findViewById(R.id.count);
        money = findViewById(R.id.money);
        afterSmoke = findViewById(R.id.afterSmoke);
        countPacket = findViewById(R.id.countPacket);
        todayHistory = findViewById(R.id.todayHistory);
        todayHistory.setEmptyView(findViewById(R.id.todayHistoryEmpty));




        //Realm.deleteRealm(Realm.getDefaultConfiguration());
        realm = Realm.getDefaultInstance();
        cigs = realm.where(Cigarette.class);
        packets = realm.where(Packet.class);
        packetsBuyed = realm.where(PacketsBuyed.class);

        historyAdapter = new HistoryAdapter(this, historyContent);
        todayHistory.setAdapter(historyAdapter);
        todayHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Cigarette cigarette = historyContent.get(position);
                // is HistoryModel now... search maybe the array as timestamp??
                //Todo: edit entrys
            }
        });

        addCig = findViewById(R.id.add_cig);
        addPacket = findViewById(R.id.add_packet);
        addCig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    checkDayLimit();
                    realm.beginTransaction();
                    Cigarette cig = realm.createObject(Cigarette.class, UUID.randomUUID().toString());
                    cig.timestamp = System.currentTimeMillis();
                    realm.commitTransaction();

                    if(list_content.size() >= 10) list_content.remove(list_content.size() - 1);
                    list_content.add(0, cig);
                    historyAdapter.notifyDataSetChanged();
                } finally {
                    cigTodayCount ++;
                    count.setText(String.valueOf(cigTodayCount));
                    FancyToast.makeText(getApplicationContext(),"Eine Zigarette wurde hinzugefügt.",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                    afterSmoke.setText("0 minuten");

                    checkHistorys();
                }
            }
        });
        addPacket = findViewById(R.id.add_packet);
        addPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packets = realm.where(Packet.class);
                DialogPlus dialog = DialogPlus.newDialog(mActivity)
                        .setContentHolder(new ListHolder())
                        .setAdapter(new AddDialogAdapter(getApplicationContext(), packets))
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                try {
                                    realm.beginTransaction();
                                    PacketsBuyed buyed = realm.createObject(PacketsBuyed.class, UUID.randomUUID().toString());
                                    buyed.timestamp = System.currentTimeMillis();
                                    buyed.packetID = packets.findAll().get(position).id;
                                    buyed.currentPrice = packets.findAll().get(position).money;
                                    realm.commitTransaction();
                                    System.out.println("[[ DEBUG ]] get ID: " + buyed.packetID);
                                } finally {
                                    packetCount++;
                                    cigMoney = (cigMoney + packets.findAll().get(position).money);
                                    countPacket.setText(String.valueOf(packetCount));
                                    setMoney(cigMoney);
                                }
                                //FancyToast.makeText(getApplicationContext(),"clicked on " + item.toString(),FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                            }
                        })
                        .setExpanded(true)
                        .setCancelable(true)
                        .setGravity(Gravity.BOTTOM)
                        .create();
                dialog.show();
            }
        });

        if(packets.count() != 0) {
            for(Packet packet : packets.findAll()) {
                System.out.println("[[ DEBUG ]] " + packet);
            }
        }

        Date currentWeekYearDate = new Date();
        currentWeekYearDate.setTime(Calendar.getInstance().getTimeInMillis());
        String currentWeekYear = new SimpleDateFormat("w").format(currentWeekYearDate);
        if(packetsBuyed.count() != 0) {
            for(PacketsBuyed buyed : packetsBuyed.findAll()) {
                Date objectWeekYearDate = new Date();
                objectWeekYearDate.setTime(Calendar.getInstance().getTimeInMillis());
                String objectWeekYear = new SimpleDateFormat("w").format(objectWeekYearDate);
                if(currentWeekYear.matches(objectWeekYear)) {
                    packetCount ++;
                    cigMoney = (buyed.currentPrice + cigMoney);
                    System.out.println("[[ DEBUG ]] is bought this week: " + buyed + " currentWeek: " + currentWeekYear + " objectWeek: " + objectWeekYear);
                } else {
                    System.out.println("[[ DEBUG ]] isnt bought this week" + buyed + " currentWeek: " + currentWeekYear + " objectWeek: " + objectWeekYear);
                }
            }
        }


        cigs = realm.where(Cigarette.class);
        if(cigs.count() != 0) {
            long afterTime = ((System.currentTimeMillis() / 1000) - (cigs.findAll().last().timestamp / 1000));
            setAfterSmoke(afterTime);
        } else {
            afterSmoke.setText("noch keine geraucht.");
        }

        setMoney(cigMoney);
        countPacket.setText(String.valueOf(packetCount));
        count.setText(String.valueOf(cigTodayCount));

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("[[ DEBUG ]] check after smoke time...");
                    cigs = realm.where(Cigarette.class);

                    if(cigs.count() != 0) {
                        long afterTime = ((System.currentTimeMillis() / 1000) - (cigs.findAll().last().timestamp / 1000));
                        setAfterSmoke(afterTime);
                    }
                }
                catch (Exception e) {
                    System.out.println("[[ERROR]] " + e);
                }
                finally {
                    handler.postDelayed(this, 25000);
                }
            }
        };

        handler.post(runnable);

        this.checkDayLimit();
        this.checkHistorys();
    }

    public void checkHistorys() {
        Calendar currentCalender = Calendar.getInstance();
        currentCalender.setTime(new Date());
        int currentYear = currentCalender.get(Calendar.YEAR);
        int currentMonth = currentCalender.get(Calendar.MONTH);
        int currentDay = currentCalender.get(Calendar.DAY_OF_MONTH);

        cigs = realm.where(Cigarette.class);
        packetsBuyed = realm.where(PacketsBuyed.class);

        this.cigTodayCount = 0;

        ArrayList<HistoryModel> tempHistory = new ArrayList<>();
        tempHistory.clear();

        historyContent.clear();

        int index = 0;
        for(Cigarette cigarette : cigs.sort("timestamp", Sort.DESCENDING).findAll()) {
            Calendar cigCalender = Calendar.getInstance();
            cigCalender.setTimeInMillis(cigarette.timestamp);
            int checkYear = cigCalender.get(Calendar.YEAR);
            int checkMonth = cigCalender.get(Calendar.MONTH);
            int checkDay = cigCalender.get(Calendar.DAY_OF_MONTH);

            HistoryModel history = new HistoryModel();
            history.type = 0;
            history.timestamp = cigarette.timestamp;
            tempHistory.add(history);

            System.out.println("[[ DEBUG ]] " + cigarette);

            if(checkYear == currentYear && checkMonth == currentMonth && checkDay == currentDay) this.cigTodayCount ++;
        }

        for(PacketsBuyed buyed : packetsBuyed.sort("timestamp", Sort.DESCENDING).findAll()) {
            HistoryModel history = new HistoryModel();
            history.type = 1;
            history.timestamp = buyed.timestamp;
            tempHistory.add(history);
        }

        Collections.sort(tempHistory);

        System.out.println("[[ DEBUG ]] \n" + tempHistory);


        index = 0;
        for(HistoryModel history : tempHistory) {
            if(index <= 10) {
                historyContent.add(history);
            }
            index++;
        }

        historyAdapter.notifyDataSetChanged();
        count.setText(String.valueOf(cigTodayCount));
    }

    public void checkDayLimit() {
        int dayLimit = (int) SharedPreferencesHelper.get(mActivity, "dayLimit", 0);
        if(dayLimit != 0) {
            int diff = (dayLimit - cigTodayCount);
            if(diff <= 1) {
                count.setTextColor(getResources().getColor(R.color.colorRed));
            } else if(diff <= 4) {
                count.setTextColor(getResources().getColor(R.color.colorOrange));
            } else {
                count.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }
    }

    public void setAfterSmoke(long time) {
        long hours;
        long minutes;

        hours = time / 3600;
        minutes = (time % 3600) / 60;

        afterSmoke.setText(String.format("%02d Std. %02d min", hours, minutes));
    }

    public void setMoney(Double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        money.setText(format.format(amount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MaterialMenuInflater.with(this).setDefaultColor(Color.WHITE).inflate(R.menu.menu_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return (true);
        } else if(item.getItemId() == R.id.share) {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_TEXT, "Hey, look at this app called 'Smoke Manager'\nthere you can control your limits and see your reports how often you smoke!\n\nhttps://play.google.com/store/apps/details?id=de.devflo.smokemanager");
            startActivity(Intent.createChooser(share, "Share!"));
        } else if(item.getItemId() == R.id.report) {
            startActivity(new Intent(getApplicationContext(), ReportActivity.class));
        } else if (item.getItemId() == R.id.bilanz) {
            startActivity(new Intent(getApplicationContext(), ResultActivity.class));
            return (true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onResume() {
        super.onResume();
        int dayLimit = (int) SharedPreferencesHelper.get(mActivity, "dayLimit", 0);
        if (dayLimit != 0) {
            int diff = (dayLimit - cigTodayCount);
            if (diff <= 1) {
                count.setTextColor(getResources().getColor(R.color.colorRed));
            } else if (diff <= 4) {
                count.setTextColor(getResources().getColor(R.color.colorOrange));
            } else {
                count.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }
    }
}
