package com.google.sample.cloudvision;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;
    static int[] assignments = new int[50];

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;


    private FloatingActionButton fab;
    private FloatingActionButton recommandfab;
    private FloatingActionButton starfab;
    private TextView recomtext;
    private TextView cameratext;
    private  TextView startext;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabout;
    private TextView mImageDetails;
    private ImageView mMainImage;
    String passmessage;


    ArrayList<ListViewItem> arraylist = new ArrayList<ListViewItem>();

    private EditText editsearch;

    private String names[];

    {
        names = new String[]{

                "Asahi Super Dry",
                "Blanc1664",
                "Breda Royal Beer / Lager",
                "Budweiser",
                "Carlsberg",
                "Cass",
                "Classe Royale Premium Hefeweissbier",
                "Coors Light",
                "Corona Extra",
                "Corona Light",
                "Desperados Original",
                "Faxe Premium",
                "Faxe Stout",
                "Goose IPA",
                "Guinness Draught",
                "Harbin Lager",
                "Harboe Bear Beer Premium Lager",
                "Heineken",
                "Hite",
                "Hoegaarden",
                "Jeju Wit Ale",
                "Kaiserdom Kellerbier",
                "Kirin Ichibang",
                "Kozel dark",
                "Leffe Blonde",
                "Leffe Brune",
                "Max",
                "miller lite",
                "Paulaner Hefe-Weizen",
                "Peroni Original",
                "Peroni Red",
                "Pilsner Urquell",
                "Royal Dutch Post Horn Hefeweizen",
                "royal dutch",
                "San Miguel Dark Lager",
                "San Miguel Pale Pilsen",
                "Sapporo Premium Beer",
                "Schofferhofer Grapefruit",
                "Stella Artois",
                "suntory - The Premium Malt's",
                "Tiger",
                "Tsingtao",
                "Wabar Dunkel",
                "Weidmann Weissbier Hefeweiss"

        };
    }
    String findstring ="";
    int index;
    //private ArrayAdapter<String> listAdapter;
    private  CustomListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "beerbook1.db", null, 1);
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst", false);
        if(first==false){
            Log.d("Is first Time?", "first");
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst",true);
            editor.commit();
//앱 최초 실행시 하고 싶은 작업
            //dbHelper.deleteall();
            dbHelper.csvinsert(getApplicationContext());

        }else{
            Log.d("Is first Time?", "not first");
        }

        editsearch = (EditText) findViewById(R.id.editText);
        ListView itemList = (ListView) findViewById(R.id.listView);

        mImageDetails = findViewById(R.id.image_details);

        //String[] listViewAdapterContent = names;
        //listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listViewAdapterContent);

        //itemList.setAdapter(listAdapter);

        String[] ipas = new String[names.length];

        String ripa;
        String changename;
        Integer[] imageint = new Integer[names.length];
        for (int i = 0; i < names.length; i++) {
            changename = names[i].toLowerCase().replaceAll(" ", "");
            changename = changename.replace("/", "").replace("-", "").replace("'", "");
            if (names[i].contains("suntory"))
                names[i] = "Suntory The Premium Malts";
            ipas[i] = dbHelper.getRbeertype(names[i]);
            imageint[i] = getResources().getIdentifier(changename, "drawable", getPackageName());
        }

        ListView list = (ListView) findViewById(R.id.listView);
        for (int i = 0; i < names.length; i++)
        {
            ListViewItem LV = new ListViewItem(imageint[i], names[i],
                    ipas[i]);
            // Binds all strings into an array
            arraylist.add(LV);
        }

        listAdapter = new CustomListAdapter(this, arraylist);
        list.setAdapter(listAdapter);
        editsearch = (EditText) findViewById(R.id.editText);

        list.setTextFilterEnabled(true);


        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        recomtext = findViewById(R.id.recomtext);
        cameratext = findViewById(R.id.cameratext);
        startext = findViewById(R.id.startext);
        fab = findViewById(R.id.fab);
        fab.bringToFront();
        recommandfab = findViewById(R.id.recommandfab);
        recommandfab.bringToFront();
        starfab = findViewById(R.id.starfab);
        starfab.bringToFront();
        fabout = findViewById(R.id.fabout);


        fabout.setOnClickListener(this);
        fab.setOnClickListener(this);
        starfab.setOnClickListener(this);
        recommandfab.setOnClickListener(this);

        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder
                    .setMessage(R.string.dialog_select_prompt)
                    .setPositiveButton(R.string.dialog_select_gallery, (dialog, which) -> startGalleryChooser())
                    .setNegativeButton(R.string.dialog_select_camera, (dialog, which) -> startCamera());
            builder.create().show();
        });
        recommandfab.setOnClickListener(view->{
            Intent intent2 = new Intent(getApplicationContext(), RecommandBeer.class);
            startActivity(intent2);
        });
        starfab.setOnClickListener(view->{
            Intent intent3 = new Intent(getApplicationContext(), starbeer.class);
            //intent2.putExtra("name", findstring);
            startActivity(intent3);
        });

        mImageDetails = findViewById(R.id.image_details);
        mMainImage = findViewById(R.id.main_image);


        //sendclusterresult();

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// make Toast when click
                findstring=listAdapter.getItem(position).getname();
                //Toast.makeText(getApplicationContext(), "Position " + findstring, Toast.LENGTH_LONG).show();
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

        SimpleKMeans kmeans = new SimpleKMeans();
        kmeans.setSeed(10);
        kmeans.setPreserveInstancesOrder(true);
        try {
            kmeans.setNumClusters(5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        InputStream infile = getResources().openRawResource(R.raw.clustering_sagi);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(infile, Charset.forName("UTF-8"))
        );

        Instances data = null;
        try {
            data = new Instances(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            kmeans.buildClusterer(data);
        } catch (Exception e) {
            e.printStackTrace();
        }



        try {
            assignments = kmeans.getAssignments();
        } catch (Exception e) {
            e.printStackTrace();
        }


        int i=0;
        for (int clusterNum : assignments) {
            dbHelper.update(i, clusterNum);
            i++;
        }


        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                listAdapter.filter(text);
            }
        });

    }
    public static int[] sendclusterresult() {
        return assignments;
    }
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fabout:
                anim();
                //Toast.makeText(this, "Floating Action Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab:
                anim();
               // Toast.makeText(this, "Button1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.recommandfab:
                anim();
               // Toast.makeText(this, "Button2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.starfab:
                anim();
               // Toast.makeText(this, "Button3", Toast.LENGTH_SHORT).show();
                break;
        }


    }
    public void anim() {

        if (isFabOpen) {
            fab.startAnimation(fab_close);
            recommandfab.startAnimation(fab_close);
            starfab.startAnimation(fab_close);
            recomtext.setVisibility(View.INVISIBLE);
            cameratext.setVisibility(View.INVISIBLE);
            startext.setVisibility(View.INVISIBLE);
            fab.setClickable(false);
            recommandfab.setClickable(false);
            starfab.setClickable(false);
            isFabOpen = false;
        } else {
            fab.startAnimation(fab_open);
            recommandfab.startAnimation(fab_open);
            starfab.startAnimation(fab_open);
            recomtext.setVisibility(View.VISIBLE);
            cameratext.setVisibility(View.VISIBLE);
            startext.setVisibility(View.VISIBLE);
            recommandfab.bringToFront();
            fab.bringToFront();
            recomtext.bringToFront();
            fab.setClickable(true);
            recommandfab.setClickable(true);
            starfab.setClickable(true);
            recommandfab.bringToFront();
            isFabOpen = true;
        }
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
           // Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(textDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                TextView imageDetail = activity.findViewById(R.id.image_details);
                imageDetail.setText(result);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            passmessage = labelDetectionTask.execute().get();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String findstring = passmessage;
        //findstring = findstring.toLowerCase();
        //Toast.makeText(getApplicationContext(), "passname " + findstring, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), photodb.class);
        intent.putExtra("name", findstring);
        startActivity(intent);

    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("H");

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message.append(labels.get(0).getDescription());
        } else {
            message.append("nothing");
        }

        return message.toString();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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
}
