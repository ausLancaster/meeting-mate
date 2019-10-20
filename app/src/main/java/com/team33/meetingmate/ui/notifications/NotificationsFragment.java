package com.team33.meetingmate.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team33.meetingmate.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            Log.d("Notifications", "Notification list is empty");
                        }
                        List<Map<String, Object>> newNotifications = new ArrayList<>();
                        List<Map<String, Object>> earlierNotifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if ((boolean) document.get("seen") == true) {
                                earlierNotifications.add(document.getData());
                            } else {
                                DocumentReference dr = db.collection("notifications").document(document.getId());
                                dr.update("seen", true);
                                newNotifications.add(document.getData());
                            }
                            Log.d("Notifications", document.getId() + " => " + document.getData());
                        }
                        Collections.sort(
                                newNotifications,
                                (Map<String, Object> e1, Map<String, Object> e2) ->
                                        Long.compare((long)e1.get("date"), (long)e2.get("date"))
                        );
                        Collections.sort(
                                earlierNotifications,
                                (Map<String, Object> e1, Map<String, Object> e2) ->
                                        Long.compare((long)e1.get("date"), (long)e2.get("date"))
                        );
                        TextView headingNew = getView().findViewById(R.id.heading_new);
                        TextView headingEarlier = getView().findViewById(R.id.heading_earlier);
                        if (newNotifications.size() == 0) {
                            headingNew.setVisibility(View.GONE);
                        } else {
                            headingNew.setVisibility(View.VISIBLE);
                        }
                        if (earlierNotifications.size() == 0) {
                            headingEarlier.setVisibility(View.GONE);
                        } else {
                            headingEarlier.setVisibility(View.VISIBLE);
                        }
                        NotificationListAdapter adapterNew = new NotificationListAdapter(getActivity(), R.layout.notification_list_item, newNotifications);
                        ListView newList = root.findViewById(R.id.notifications_new);
                        newList.setAdapter(adapterNew);
                        NotificationListAdapter adapterEarlier = new NotificationListAdapter(getActivity(), R.layout.notification_list_item, earlierNotifications);
                        ListView earlierList = root.findViewById(R.id.notifications_earlier);
                        earlierList.setAdapter(adapterEarlier);
                    } else {
                        Log.d("Notifications", "Error getting documents: ", task.getException());
                    }
                });

        return root;
    }
}