package com.example.myapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void setData(String s) {
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<String> getData() {
        return mutableLiveData;
    }
}
