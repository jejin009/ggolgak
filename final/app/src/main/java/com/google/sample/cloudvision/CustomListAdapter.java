package com.google.sample.cloudvision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class CustomListAdapter extends BaseAdapter  {

    Context mContext;
    LayoutInflater inflater;
    private List<ListViewItem> listviewitem = null;
    private ArrayList<ListViewItem> arraylist;



    public CustomListAdapter(Context context, List<ListViewItem> listviewitem){

        //super(context, R.layout.listview_row , nameArrayParam);

        mContext=context;
        this.listviewitem=listviewitem;
        inflater=LayoutInflater.from(mContext);
        this.arraylist=new ArrayList<ListViewItem>();
        this.arraylist.addAll(listviewitem);



    }

    public class ViewHolder {
        TextView name;
        TextView info;
        ImageView image;
    }

    @Override
    public int getCount() {
        return listviewitem.size();
    }

    @Override
    public ListViewItem getItem(int position) {
        return listviewitem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_row, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.info = (TextView) view.findViewById(R.id.info);
            holder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(listviewitem.get(position).getname());
        holder.info.setText(listviewitem.get(position).getinfo());
        // Set the results into ImageView
        holder.image.setImageResource(listviewitem.get(position)
                .getimage());
        // Listen for ListView Item Click

        return view;

    };

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        listviewitem.clear();
        if (charText.length() == 0) {
            listviewitem.addAll(arraylist);
        } else {
            for (ListViewItem LV : arraylist) {
                if (LV.getname().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    listviewitem.add(LV);
                }
            }
        }
        notifyDataSetChanged();
    }
/*
    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = listViewItemList ;
                results.count = listViewItemList.size() ;
            } else {
                ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>() ;

                for (ListViewItem item : listViewItemList) {
                    if (item.getTitle().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.getDesc().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<ListViewItem>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
*/
}


