package com.google.sample.cloudvision;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class starbeer extends AppCompatActivity {
    String[] listviewTitle = new String[]{
            "asahi", "asahi superdry"
    };


    String[] listviewShortDescription = new String[]{
            "Android ListView Short Description", "Android ListView Short Description"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommandbeer);

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        String[] staronebeer;

        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "beerbook1.db", null, 1);
        staronebeer = dbHelper.starnumone();
       int[] listviewImage=new int[staronebeer.length];
       String changename;

        for (int i = 0; i < staronebeer.length; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();

            hm.put("listview_title", staronebeer[i]);
            hm.put("listview_discription", dbHelper.getRbeertype(staronebeer[i]));

            changename = staronebeer[i].toLowerCase().replaceAll(" ", "");
            changename = changename.replace("/", "").replace("-", "").replace("'", "");
            listviewImage[i]=getResources().getIdentifier(changename, "drawable", getPackageName());
            hm.put("listview_image", Integer.toString(listviewImage[i]));

            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.recommand_list, from, to);
        ListView androidListView = (ListView) findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String findstring;
            int index;
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
// make Toast when click
                findstring = (String) parent.getAdapter().getItem(position).toString();
                String aaa = findstring.substring(findstring.lastIndexOf("="));
                aaa = aaa.replace("=","");
                aaa = aaa.replace("}","");
                findstring = aaa.replace("\"","");
                Intent intent = new Intent(getApplicationContext(), photodb.class);
                if(findstring.equals("망고링고"))
                    findstring = "MangoRingo";
                else if(findstring.equals("suntory - The Premium Malt's"))
                    findstring = "suntory The Premium Malts";
                intent.putExtra("name", findstring);

                index = dbHelper.findNum(findstring);
                intent.putExtra("index",index);
                startActivity(intent);

            }
        });
    }
}
