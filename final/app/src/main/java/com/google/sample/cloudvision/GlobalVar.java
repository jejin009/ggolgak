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
}
