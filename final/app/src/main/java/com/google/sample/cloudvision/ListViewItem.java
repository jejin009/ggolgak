package com.google.sample.cloudvision;

public class ListViewItem {
    private int image;
    private String name;
    private String info;

    public ListViewItem(int image, String name, String info) {
        this.image = image;
        this.name = name;
        this.info = info;

    }

    public int getimage() {
        return this.image;
    }

    public String getname() {
        return this.name;
    }

    public String getinfo() {
        return this.info;
    }



}