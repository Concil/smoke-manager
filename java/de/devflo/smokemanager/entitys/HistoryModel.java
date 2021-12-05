package de.devflo.smokemanager.entitys;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class HistoryModel  implements Comparable<HistoryModel> {
    public int type;
    public long timestamp;

    public Date getDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timestamp);
        return calendar.getTime();
    }

    @Override
    public int compareTo(HistoryModel o) {
        return o.getDateTime().compareTo(getDateTime());
    }
}
