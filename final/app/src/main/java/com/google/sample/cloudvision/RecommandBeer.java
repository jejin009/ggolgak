package com.google.sample.cloudvision;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class RecommandBeer extends AppCompatActivity {

    CircleMenu circleMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 1. 레이아웃을 정의한 레이아웃 리소스(R.layout)을 사용하여 현재 액티비티의 화면을 구성하도록 합니다.
        setContentView(R.layout.recommandcircle);


        circleMenu=(CircleMenu) findViewById(R.id.circleMenu);

        final ArrayList<String> list = new ArrayList<>();

        String[] staronebeer;
        String[] replacestaronebeer= new String[5];
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "beerbook1.db", null, 1);
        staronebeer = dbHelper.fivebeer();
        // 4. ArrayList 객체에 데이터를 집어넣습니다.


        for (int i = 0; i < staronebeer.length; i++) {
            replacestaronebeer[i]=staronebeer[i].toLowerCase().replaceAll(" ","" );
            replacestaronebeer[i]=replacestaronebeer[i].replace("/","").replace("-","").replace("'","");

        }

        for (int i = 0; i < staronebeer.length; i++) {
            list.add(staronebeer[i]);

        }



        circleMenu.setMainMenu(Color.parseColor("#4FB0C6"),R.drawable.beer, R.drawable.plus)
                .addSubMenu(Color.parseColor("#00dffc"), getResources().getIdentifier(replacestaronebeer[0], "drawable", getPackageName()))
                .addSubMenu(Color.parseColor("#008c9e"), getResources().getIdentifier(replacestaronebeer[1], "drawable", getPackageName()))
                .addSubMenu(Color.parseColor("#005f6b"), getResources().getIdentifier(replacestaronebeer[2], "drawable", getPackageName()))
                .addSubMenu(Color.parseColor("#D7FFF1"), getResources().getIdentifier(replacestaronebeer[3], "drawable", getPackageName()))
                .addSubMenu(Color.parseColor("#8EC0E4"), getResources().getIdentifier(replacestaronebeer[4], "drawable", getPackageName()))

                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {

                        Intent intent = new Intent(getApplicationContext(), photodb.class);

                        String findstring = staronebeer[index];

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



        // 5. ArrayList 객체와 ListView 객체를 연결하기 위해 ArrayAdapter객체를 사용합니다.
        // 우선 ArrayList 객체를 ArrayAdapter 객체에 연결합니다.


    }
}