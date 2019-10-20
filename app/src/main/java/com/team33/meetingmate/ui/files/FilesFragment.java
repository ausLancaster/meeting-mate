package com.team33.meetingmate.ui.files;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.team33.meetingmate.AppActivity;
import com.team33.meetingmate.R;

public class FilesFragment extends Fragment {

    private FilesViewModel filesViewModel;
    private View view;
    private AppActivity activity;


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
    }
}