package com.team33.meetingmate.ui.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team33.meetingmate.R;

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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private int resourceId;

    public CalendarAdapter(Context context, int resource, List<Map<String, Object>> objects) {
        super(context, resource, objects);
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

//        Set onClick listener - method 2
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), ((ViewHolder) view.getTag()).text.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });

        viewHolder.title.setText((String) eventData.get("summary"));
        viewHolder.location.setText((String) eventData.get("location"));
        viewHolder.startTime.setText(eventData.get("startHour").toString() + ":" + eventData.get("startMin").toString());
        viewHolder.endTime.setText(eventData.get("endHour").toString() + ":" + eventData.get("endMin").toString());

        return view;
    }
}