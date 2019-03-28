package com.google.sample.cloudvision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;


import java.io.ByteArrayOutputStream;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class photodb extends AppCompatActivity implements OnLikeListener,
        OnAnimationEndListener {
    LikeButton starButton;
    public static final String TAG = "MainActivity";
    String findstring;
    int index;
    int[] assignments=new int [50];


    Bitmap getDrawbleByBitmap(Context context, String findstring) {

        Resources resources = context.getResources();
        findstring=findstring.toLowerCase().replaceAll(" ", "");
        findstring=findstring.replace("/","").replace("-","").replace("'","");
        if(findstring.equals("312urbanwheatale"))
            findstring = "d312urbanwheatale";


        return BitmapFactory.decodeResource(resources, resources.getIdentifier(findstring,"drawable",getPackageName()));

    }

    public byte[] getByteArrayFromDrawable(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        return data;
    }


    public byte[] wannagetimage(String findstring) {
        return getByteArrayFromDrawable(getDrawbleByBitmap(this, findstring));
    }

    // ImageView lalala=(ImageView)findViewById(R.id.image);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afterphoto);
        ImageView blobImg= (ImageView)findViewById(R.id.image);
        starButton = (LikeButton) findViewById(R.id.star_button);

        starButton.setOnAnimationEndListener(this);
        starButton.setOnLikeListener(this);

        ImageView recom1= (ImageView)findViewById(R.id.recomimage1);
        ImageView recom2= (ImageView)findViewById(R.id.recomimage2);
        ImageView recom3= (ImageView)findViewById(R.id.recomimage3);

        ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        TextView dosutext = (TextView) findViewById(R.id.dosutext);

        String[] recombeer;


        Intent intent = getIntent(); /*데이터 수신*/

        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "beerbook1.db", null, 1);



        findstring = intent.getExtras().getString("name"); /*String형*/
        //  int clustering = intent.getExtras().getInt("cluster");

        index=intent.getExtras().getInt("index");
        recombeer = dbHelper.recommand(findstring);

        byte[] recomi1 = wannagetimage(recombeer[0]);
        Bitmap bitmap1=dbHelper.getImageResult(recomi1);
        recom1.setImageBitmap(bitmap1);
        recom1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String cs = recombeer[0];
                int indexs;
                Intent intent = new Intent(getApplicationContext(), photodb.class);
                if(findstring.equals("망고링고"))
                    findstring = "MangoRingo";
                else if(findstring.equals("suntory - The Premium Malt's"))
                    findstring = "suntory The Premium Malts";
                intent.putExtra("name", cs);
                indexs = dbHelper.findNum(cs);
                intent.putExtra("index",index);
                startActivity(intent);

            }
        });

        byte[] recomi2 = wannagetimage(recombeer[1]);
        Bitmap bitmap2=dbHelper.getImageResult(recomi2);
        recom2.setImageBitmap(bitmap2);
        recom2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String cs = recombeer[1];
                int indexs;
                Intent intent = new Intent(getApplicationContext(), photodb.class);
                if(findstring.equals("망고링고"))
                    findstring = "MangoRingo";
                else if(findstring.equals("suntory - The Premium Malt's"))
                    findstring = "suntory The Premium Malts";
                intent.putExtra("name", cs);
                indexs = dbHelper.findNum(cs);
                intent.putExtra("index",index);
                startActivity(intent);

            }
        });

        byte[] recomi3 = wannagetimage(recombeer[2]);
        Bitmap bitmap3=dbHelper.getImageResult(recomi3);
        recom3.setImageBitmap(bitmap3);
        recom3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String cs = recombeer[2];
                int indexs;
                Intent intent = new Intent(getApplicationContext(), photodb.class);
                if(findstring.equals("망고링고"))
                    findstring = "MangoRingo";
                else if(findstring.equals("suntory - The Premium Malt's"))
                    findstring = "suntory The Premium Malts";
                intent.putExtra("name", cs);
                indexs = dbHelper.findNum(cs);
                intent.putExtra("index",index);
                startActivity(intent);

            }
        });



        assignments=MainActivity.sendclusterresult();
        findstring = findstring.replace("\n", "");

        starButton.setLikeddb(dbHelper.isitmyfavorite(findstring));

        findstring = changeString(findstring);

        byte[] image = wannagetimage(findstring);
        TextView rname = (TextView) findViewById(R.id.name);
        TextView rdosu = (TextView) findViewById(R.id.dosu);
        TextView rcountry = (TextView) findViewById(R.id.country);
        TextView rbeertype= (TextView) findViewById(R.id.beertype);
        TextView ripa= (TextView) findViewById(R.id.ipa);
        TextView rfeature = (TextView) findViewById(R.id.feature);

        //result.setText("helloresult");

        // DB에 데이터 추가 //

       /* for (int i = 0; i < names.length; i++) {
            dbHelper.insert(names[i], alcohol[i], country[i], image);
        }
        */
        dbHelper.update(index,assignments[index]);
        String dosuString = dbHelper.getDosu(findstring);
        rname.setText(dbHelper.getRname(findstring) );
        rdosu.setText(dbHelper.getRdosu(findstring) );
        rcountry.setText(dbHelper.getRcountry(findstring) );
        rbeertype.setText("종류    "+dbHelper.getRbeertype(findstring) );
        ripa.setText(dbHelper.getRipa(findstring) );
        rfeature.setText(dbHelper.getRfeature(findstring) );


        //float dosu = Float.parseFloat(dosuString);
        ///int dosuint = (int)dosu;

        // progress.setProgress(dosuint);
        // dosutext.setText("도수:"+dosuString);





        //result.setText(dbHelper.getResult(findstring) +"cluster : "+clustering);
        Bitmap bitmap=dbHelper.getImageResult(image);
        blobImg.setImageBitmap(bitmap);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void liked(LikeButton likeButton) {
      //  Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show();
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "beerbook1.db", null, 1);
        dbHelper.changestarnum(findstring,1);
    }

    @Override
    public void unLiked(LikeButton likeButton) {
       // Toast.makeText(this, "Disliked!", Toast.LENGTH_SHORT).show();
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "beerbook1.db", null, 1);
        dbHelper.changestarnum(findstring,0);
    }

    @Override public void onAnimationEnd(LikeButton likeButton) {
        Log.d(TAG, "Animation End for %s" + likeButton);
    }
    public String changeString(String findstring){
        if(findstring.contains("aard"))
            findstring="Hoegaarden";

        else if(findstring.contains("66"))
            findstring="Blanc1664";
        else if(findstring.contains("singta"))
            findstring="Tsingtao";


        else if(findstring.contains("KI"))
            findstring="Kirin Ichibang";

        else if(findstring.contains("inek"))
            findstring="Heineken";

        else if(findstring.contains("HF"))
            findstring="Cass";

        else if(findstring.contains("RTOI"))
            findstring="Stella Artois";

        else if(findstring.contains("SINGA"))
            findstring="Tiger";

        else if(findstring.contains("NN"))
            findstring="Guinness Draught";

        else if(findstring.contains("ahi"))
            findstring="Asahi Super Dry";


        else if(findstring.contains("weise"))
            findstring="Budweiser";
        else if(findstring.contains("호가든"))
            findstring="Hoegaarden";
        else if(findstring.contains("onen"))
            findstring="Blanc1664";
        else if(findstring.contains("PPO"))
            findstring="Sapporo Premium Beer";
        else if(findstring.contains("PILSNER"))
            findstring="Pilsner Urquell";
        else if(findstring.contains("Wei"))
            findstring="Paulaner Hefe-Weizen";
        else if(findstring.contains("ASAHI"))
            findstring="Asahi Super Dry";
        else if(findstring.contains("kirin"))
            findstring="Kirin Ichibang";


        return findstring;
    }
}