package com.google.sample.cloudvision;

import android.app.Application;

public class GlobalVar extends Application {
        private int data;
        public int getData()
        {
            return data;
        }
        public void setData(int data)
        {
            this.data = data;
        }
        private int count;
    public int getCount()
    {
        return count;
    }
    public void setCount(int count)
    {
        this.count = count;
    }

    private int[] data1 = new int[10];
    public int[] getData1()
    {
        return data1;
    }
    public void setData1(int[] data1)
    {
        this.data1 = data1;
    }

    private String ss;
    public String getString(){
            return ss;
    }
    public void setString(String sss) {
        this.ss = ss + sss;
    }
}
