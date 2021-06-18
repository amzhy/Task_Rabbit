package com.example.myapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    MutableLiveData<NewTask> mutableLiveData = new MutableLiveData<>();

    public void setData(NewTask s) {
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<NewTask> getData() {
        return mutableLiveData;
    }
}
