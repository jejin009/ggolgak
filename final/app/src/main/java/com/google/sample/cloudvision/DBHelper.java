package com.google.sample.cloudvision;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.stream.IntStream;

public class DBHelper extends SQLiteOpenHelper {


    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL("CREATE TABLE IF NOT EXISTS BeerBOOK (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, alcohol TEXT, country TEXT, beertype TEXT, ipa INTEGER, feature TEXT, image BLOB, star INTEGER, cluster INTEGER);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

  /*  public void insert(String name, String alcohol, String country, byte[] image) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO BeerBOOK VALUES(null, '" + name + "', '" + alcohol + "', '" + country + "','" + null + "');");
        SQLiteStatement p = db.compileStatement("UPDATE BeerBook SET image=?");
        p.clearBindings();
        p.bindBlob(1, image);
        db.close();
    }
    */

    public void csvinsert(Context context) {
        int star = 0;

        SQLiteDatabase db = getWritableDatabase();
        String mCSVfile = "file2.csv";
        AssetManager manager = context.getAssets();
        InputStream inStream = null;
        try {
            inStream = manager.open(mCSVfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] colums = line.split(",");
                if (colums.length != 6) {
                    Log.d("CSVParser", "Skipping Bad CSV Row");
                    continue;
                }
                ContentValues cv = new ContentValues(2);

                db.execSQL("INSERT INTO BeerBOOK VALUES(null, '" + colums[0].trim() + "', '" + colums[1].trim() + "', '" + colums[2].trim() + "','" + colums[3].trim() + "','" + colums[4].trim() + "','" + colums[5].trim() + "','" + null + "','" + star + "','" + star + "');");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void update(int _id, int cluster) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE BeerBOOK SET cluster=" + cluster + " WHERE _id='" + _id + "';");
        db.close();
    }


    public void delete(String name) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM BeerBOOK WHERE name='" + name + "';");
        db.close();
    }


    public void deleteall() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM BeerBOOK");
        // db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'BeerBOOK'");
    }

    public String getRname(String findstring) {
        SQLiteDatabase db = getReadableDatabase();
        String rname = "";
        String sql = "SELECT * FROM BeerBOOK WHERE name ='" + findstring + "';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
            rname = cursor.getString(1);

        return rname;
    }

    public String getRdosu(String findstring) {
        SQLiteDatabase db = getReadableDatabase();
        String rdosu = "";
        String sql = "SELECT * FROM BeerBOOK WHERE name ='" + findstring + "';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
            rdosu = cursor.getString(2) + "% alc";

        return rdosu;
    }

    public String getRcountry(String findstring) {
        SQLiteDatabase db = getReadableDatabase();
        String rcountry = "";
        String sql = "SELECT * FROM BeerBOOK WHERE name ='" + findstring + "';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
            rcountry = "나라   " + cursor.getString(3);

        return rcountry;
    }

    public String getRbeertype(String findstring) {
        SQLiteDatabase db = getReadableDatabase();
        String rbeertype = "";
        String sql = "SELECT * FROM BeerBOOK WHERE name ='" + findstring + "';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
            rbeertype = cursor.getString(4);

        return rbeertype;
    }

    public String getRipa(String findstring) {
        SQLiteDatabase db = getReadableDatabase();
        String ripa = "";
        String sql = "SELECT * FROM BeerBOOK WHERE name ='" + findstring + "';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
            ripa = "IBU    " + cursor.getString(5);

        return ripa;
    }

    public String getRfeature(String findstring) {
        SQLiteDatabase db = getReadableDatabase();
        String rfeature = "";
        String sql = "SELECT * FROM BeerBOOK WHERE name ='" + findstring + "';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
            rfeature = cursor.getString(6);

        return rfeature;
    }

    public Bitmap getImageResult(byte[] b) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }

    public int findNum(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM BeerBOOK";
        Cursor cursor = db.rawQuery(sql, null);
        int num = 0;
        while (cursor.moveToNext()) {
            String tempName = cursor.getString(1);
            if (tempName.equals(name)) {
                num = cursor.getInt(0);
                cursor.close();
                break;
            }
        }
        return num;


    }

    public int whichcluster(int index) {
        SQLiteDatabase db = getReadableDatabase();
        int clu = 0;
        String sql = "SELECT * FROM BeerBOOK WHERE _id ='" + index + "';";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            clu = cursor.getInt(9);
        }
        return clu;
    }


    public void changestarnum(String name, int num) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE BeerBOOK SET star=" + num + " WHERE name='" + name + "';");
        db.close();
    }

    public String[] starnumone() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM BeerBOOK WHERE star=1;";

        Cursor cursor = db.rawQuery(sql, null);
        int clength = cursor.getCount();
        String[] staronebeer = new String[clength];
        int i = 0;

        while (cursor.moveToNext()) {
            staronebeer[i] = cursor.getString(1);
            i++;
        }
        return staronebeer;
    }

    public int isitmyfavorite(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM BeerBOOK";
        Cursor cursor = db.rawQuery(sql, null);
        int num = 0;

        while (cursor.moveToNext()) {
            String tempName = cursor.getString(1);
            if (tempName.equals(name)) {
                num = cursor.getInt(8);
                cursor.close();
                break;
            }
        }
        return num;
    }

    public void putclusterdb(int id, int cluster) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE BeerBOOK SET cluster=" + cluster + " WHERE _id='" + id + "';");
        db.close();
    }

    public String[] fivebeer() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM BeerBOOK";
        Cursor cursor = db.rawQuery(sql, null);
        int[] staronebeer = new int[]{0, 0, 0, 0, 0};


        int temp;
        while (cursor.moveToNext()) {
            temp = cursor.getInt(8);
            if (temp == 1) {
                staronebeer[cursor.getInt(9)] = 1;
            }
        }

        int i = 0, j = 0;

        int alength = 0;
        String sql2;

        for (i = 0; i < 5; i++) {
            if (staronebeer[i] == 1) {
                sql2 = "SELECT * FROM BeerBOOK WHERE cluster='" + i + "';";
                Cursor cursor2 = db.rawQuery(sql2, null);
                alength = alength + cursor2.getCount();
            }
        }
        String[] clusteredbeer = new String[alength];

        for (i = 0; i < 5; i++) {
            if (staronebeer[i] == 1) {
                sql2 = "SELECT * FROM BeerBOOK WHERE cluster='" + i + "';";
                Cursor cursor2 = db.rawQuery(sql2, null);
                alength = +cursor2.getCount();

                while (cursor2.moveToNext()) {
                    clusteredbeer[j] = cursor2.getString(1);
                    j++;
                }

            }
        }

        Random random = new Random();

        // 1~10까지의 정수를 랜덤하게 출력
        // nextInt 에 10 을 입력하면 0~9 까지의 데이타가 추출되므로 +1 을 한것이다.
        String[] recombeer = new String[5];
        int[] checkrandom = {0, 0, 0, 0, 0};
        int length = clusteredbeer.length;
        int data = 0;

        Loop1:
        for (i = 0; i < 5; i++) {
            data = random.nextInt(length);
            for (int k = 0; k < 4; k++) {
                if (data == checkrandom[k]) {
                    i--;
                    continue Loop1;
                }
            }
            checkrandom[i] = data;
            recombeer[i] = clusteredbeer[data];
        }


        return recombeer;
    }

    public String getDosu(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM BeerBOOK WHERE name='" + name + "';";
        Cursor cursor = db.rawQuery(sql, null);
        String dosunum = null;
        while (cursor.moveToNext()) {
            String tempName = cursor.getString(1);
            if (tempName.equals(name)) {
                dosunum = cursor.getString(2);
                cursor.close();
                break;
            }
        }
        return dosunum;
    }

    String[] recommand(String name) {

        String[] recombeer = new String[4];

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM BeerBOOK WHERE name='" + name + "';";
        Cursor cursor = db.rawQuery(sql, null);
        int cluster = 0;

        while (cursor.moveToNext()) {
            cluster = cursor.getInt(9);
        }
        cursor.close();

        String sql2 = "SELECT * FROM BeerBOOK WHERE cluster='" + cluster + "';";
        Cursor cursor2 = db.rawQuery(sql2, null);

        String[] allbeer = new String[30];

        int j = 0;
        while (cursor2.moveToNext()) {
            allbeer[j] = cursor2.getString(1);
            j++;
        }

        recombeer[0] = allbeer[1];
        recombeer[1] = allbeer[3];
        recombeer[2] = allbeer[5];
        recombeer[3] = allbeer[6];

        for(int z=0; z<recombeer.length;z++){
            if(recombeer[z].equals(name)){
                recombeer[z]=allbeer[2];
            }
        }

        return recombeer;

    }

}





