package com.example.myapplication;

import android.widget.Toast;

public class ObservableInteger
{
    private OnIntegerChangeListener listener;

    private int value = -1;

    public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
    {
        this.listener = listener;
    }

    public int get()
    {
        return value;
    }

    public void set(int value)
    {
        this.value = value;

        if(listener != null)
        {
            listener.onIntegerChanged(value);
        } else {
            System.out.println("null");
        }
    }

    public void add(int i) {
        if (this.value == -1) {
            this.value = 0;
        }
        this.value+=i;
        if(listener != null)
        {
            listener.onIntegerChanged(this.value);
        } else {
            System.out.println("null");
        }
    }
    public interface OnIntegerChangeListener
    {
        public void onIntegerChanged(int newValue);
    }

    public boolean isNull(){
        return this.listener==null;
    }
}
