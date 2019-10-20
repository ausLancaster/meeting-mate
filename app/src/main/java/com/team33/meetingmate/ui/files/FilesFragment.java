package com.team33.meetingmate.ui.files;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team33.meetingmate.AppActivity;
import com.team33.meetingmate.R;

public class FilesFragment extends Fragment {

    private static int RECEIVE_RESULT_REQUEST_CODE = 202;

    private FilesViewModel filesViewModel;
    private View view;
    private AppActivity activity;
    private FilesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        filesViewModel =
                ViewModelProviders.of(this).get(FilesViewModel.class);
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        view = getView();
        activity = ((AppActivity) getActivity());

        Button receive = view.findViewById(R.id.button_receive);
        receive.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BluetoothReceiveActivity.class);
            startActivityForResult(intent, RECEIVE_RESULT_REQUEST_CODE);
        });
    }
}