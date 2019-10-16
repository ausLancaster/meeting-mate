package com.team33.meetingmate.ui.calendar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team33.meetingmate.R;
import com.team33.meetingmate.TimeUtility;

import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarAdapter extends ArrayAdapter<Map<String, Object>> {
    static class ViewHolder {
        @BindView(R.id.listevent_title)
        TextView title;
        @BindView(R.id.listevent_location)
        TextView location;
        @BindView(R.id.listevent_starttime)
        TextView startTime;
        @BindView(R.id.listevent_endtime)
        TextView endTime;
        @BindView(R.id.listevent_date)
        TextView date;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private Context context;

    private Date previousDate;

    private int resourceId;

    public CalendarAdapter(Context context, int resource, List<Map<String, Object>> objects) {
        super(context, resource, objects);
        this.context = context;
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> eventData = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("EventID", (long) eventData.get("id"));
                context.startActivity(intent);
            }
        });

        viewHolder.title.setText((String) eventData.get("summary"));
        viewHolder.location.setText((String) eventData.get("location"));
        Date startDate = new Date((long) eventData.get("startDate"));
        // only show date if it is different from the previous event (so that events in same day are grouped together)
        if (previousDate != null &&
                startDate.getYear() == previousDate.getYear() &&
                startDate.getMonth() == previousDate.getMonth() &&
                startDate.getDay() == previousDate.getDay()) {
            viewHolder.date.setVisibility(View.GONE);
        } else {
            viewHolder.date.setVisibility(View.VISIBLE);
        }
        viewHolder.startTime.setText(TimeUtility.formatTime(startDate));
        viewHolder.endTime.setText(TimeUtility.formatTime(new Date((long) eventData.get("endDate"))));
        viewHolder.date.setText(TimeUtility.formatDate(startDate));
        previousDate = (Date) startDate.clone();

        return view;
    }
}