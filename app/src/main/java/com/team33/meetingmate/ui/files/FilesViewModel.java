package com.team33.meetingmate.ui.files;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FilesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FilesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Files");
    }

    public LiveData<String> getText() {
        return mText;
    }
}