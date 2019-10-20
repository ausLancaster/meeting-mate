package com.team33.meetingmate.ui.notifications;

import android.content.Context;
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

public class NotificationListAdapter extends ArrayAdapter<Map<String, Object>> {
    static class ViewHolder {
        @BindView(R.id.notification_text)
        TextView text;
        @BindView(R.id.notification_time_passed)
        TextView time_passed;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private Context context;

    private Date previousDate;

    private int resourceId;

    public NotificationListAdapter(Context context, int resource, List<Map<String, Object>> objects) {
        super(context, resource, objects);
        this.context = context;
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> eventData = getItem(position);
        View view;
        com.team33.meetingmate.ui.notifications.NotificationListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new com.team33.meetingmate.ui.notifications.NotificationListAdapter.ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (com.team33.meetingmate.ui.notifications.NotificationListAdapter.ViewHolder) view.getTag();
        }

        viewHolder.text.setText((String) eventData.get("text"));
        Date date = new Date((long) eventData.get("date"));
        viewHolder.time_passed.setText(TimeUtility.formatTimePassed(date));
        // only show date if it is different from the previous event (so that events in same day are grouped together)
        /*if (previousDate != null &&
                date.getYear() == previousDate.getYear() &&
                date.getMonth() == previousDate.getMonth() &&
                date.getDay() == previousDate.getDay()) {
            viewHolder.date.setVisibility(View.GONE);
        } else {
            viewHolder.date.setVisibility(View.VISIBLE);
        }
        viewHolder.time_passed.setText(TimeUtility.formatTimePassed(date));
        viewHolder.date.setText(TimeUtility.formatDate(date));
        previousDate = (Date) date.clone();*/

        return view;
    }
}