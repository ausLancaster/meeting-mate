package com.team33.meetingmate.firebase.events;

import java.util.List;
import java.util.Map;

public interface IEventsFetcherCallback {
    public void onComplete(List<Map<String, Object>> calendarList);
    public void onError(String error);
}
