package com.example.jome9.iscl;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;




public class Findbicycle extends AppCompatActivity
        implements TMapGpsManager.onLocationChangedCallback {

    String myJSON;

    ImageButton mybycBtn;
    final Context context = this;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_LATI = "lati";
    private static final String TAG_LONGTI = "longti";

    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;
    ListView list;
    String lati;
    String longti;
    Double dlati;
    Double dlongti;

    private Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;
    private static String mApiKey = "86d8cbb8-7ee4-4d00-8d52-ca55321ec1e9"; // ???????????? appKey
    private static int mMarkerID;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();


    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findbicycle);
        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String, String>>();
        getData("http://192.168.0.180/adddata.php");
        //getData("http://192.168.0.6/adddata.php");

        mContext = this;

        FrameLayout linearLayout = (FrameLayout) findViewById(R.id.frameLayoutTmap);
        tmapview = new TMapView(this);
        linearLayout.addView(tmapview);
        tmapview.setSKTMapApiKey(mApiKey);

        //addPoint();
        //showMarkerPoint();

        /* ?????? ?????? ?????? */
        tmapview.setCompassMode(true);
        /* ????????? ??????????????? */
        tmapview.setIconVisibility(true);

        /* ????????? */
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

        tmapgps = new TMapGpsManager(Findbicycle.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //????????? ??????????????? ??? ????????? ????????????.
        //????????? ??? ???????????????.
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps??? ??? ????????? ????????????.
        tmapgps.OpenGps();
        ImageButton currentBtn = (ImageButton) findViewById(R.id.current);
        mybycBtn = (ImageButton) findViewById(R.id.btnmybyc);

        /*  ??????????????? ????????? ??????????????? ?????? */
        //tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // ????????????
        alertDialogBuilder.setTitle("???????????????");
        // AlertDialog ??????
        alertDialogBuilder
                .setMessage("????????? ????????? ???????????????.")
                .setCancelable(false)
                .setNegativeButton("???",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                m_mapPoint.add(new MapPoint(" ", dlati,dlongti));
                                showMarkerPoint();
                                tmapview.setCenterPoint(dlongti,dlati);       //??? ?????????(?????????)??? ????????????????????????
                            }
                        })
                .setPositiveButton("?????????, ??? ????????? ?????????.",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // ?????????????????? ????????????
                                tmapview.setTrackingMode(true);
                            }
                        });

        // ??????????????? ??????
        AlertDialog alertDialog = alertDialogBuilder.create();
        // ??????????????? ????????????
        alertDialog.show();



        //tmapview.setTrackingMode(false);


        // ???????????? ?????? ?????? ????????? ??? ???????????????
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback()
        {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                Toast.makeText(getApplicationContext(),"?????? : " + lati + "?????? :" + longti, Toast.LENGTH_LONG);
            }
        });


        mybycBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                dlati = Double.parseDouble(lati);
//                dlongti = Double.parseDouble(longti);
                Toast.makeText(getApplicationContext(), "??? ???????????? ????????? ???????????????", Toast.LENGTH_LONG).show();
                m_mapPoint.add(new MapPoint(" ", dlati,dlongti));
                showMarkerPoint();
                tmapview.setTrackingMode(false);
                tmapview.setCenterPoint(dlongti,dlati);       //??? ?????????(?????????)??? ????????????????????????
                //addPoint();
                //m_mapPoint.add(new MapPoint("BIKE", dlati, dlongti));
            }
        });


        currentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,10,mLocationListener);
                Toast.makeText(getApplicationContext(), "??? ????????? 50m?????? ????????? ??? ????????????", Toast.LENGTH_LONG).show();
                // locationManager.requestLocationUpdates();
                setGps(); //???????????????
                tmapview.setTrackingMode(true);

            }
        });



        ImageButton btnOpenDrawer = (ImageButton) findViewById(R.id.btn_mainmenu); // ????????? ????????? ??? ?????? ?????? ??????
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout); // ??????????????? DrawerLayout ?????? ??????
        final View drawerView = (View) findViewById(R.id.drawer); // Drawer ??????(???) ?????? ??????
        //LinearLayout logininfoBtn = (LinearLayout) findViewById(R.id.btn_logininfo);
        TextView logininformationText = (TextView) findViewById(R.id.logininformation);
        LinearLayout findwayBtn = (LinearLayout) findViewById(R.id.btn_findway);
        LinearLayout findbicycleBtn = (LinearLayout) findViewById(R.id.btn_findbicycle);
        LinearLayout keepbicycleBtn = (LinearLayout) findViewById(R.id.btn_keepbicycle);
        LinearLayout weatherBtn = (LinearLayout) findViewById(R.id.btn_weather);
        LinearLayout ridingrecordBtn = (LinearLayout) findViewById(R.id.btn_ridingrecord);
        LinearLayout bluetoothBtn = (LinearLayout) findViewById(R.id.btn_bluetoothBtn);
        LinearLayout helpBtn = (LinearLayout) findViewById(R.id.btn_help);
        LinearLayout infoBtn = (LinearLayout) findViewById(R.id.btn_developerinfo);
        ImageButton helpbtn = (ImageButton) findViewById(R.id.helpbtn);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        logininformationText.setText(userID);

        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), help_findbicycle.class);
                startActivity(intent);
            }
        });

        //??????????????????(????????? ??????)
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });//?????????????????????
        findwayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        findbicycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(drawerView);
            }
        });
        keepbicycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Keepbicycle_menu.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Weather.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        ridingrecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RidingList.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Help.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });


    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                lati = c.getString(TAG_LATI);
                longti = c.getString(TAG_LONGTI);


                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_LATI, lati);
                persons.put(TAG_LONGTI, longti);

                personList.add(persons);

            }

            ListAdapter adapter = new SimpleAdapter(
                    Findbicycle.this, personList, R.layout.list_item,
                    new String[]{TAG_LATI, TAG_LONGTI},
                    new int[]{R.id.lati, R.id.longti}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dlati = Double.valueOf(lati);
        dlongti = Double.valueOf(longti);

//        Log.e("lati ?????????", String.valueOf(dlati));
//        Log.e("longti ?????????", String.valueOf(dlongti));

    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;

                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;

                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);

        Log.e("lati ?????????", String.valueOf(lati));
        Log.e("longti ?????????", String.valueOf(longti));
    }

    public void addPoint() {
        m_mapPoint.add(new MapPoint("bike", dlati,dlongti));
    }


    public void showMarkerPoint() {// ?????? ????????? ????????? ?????????.
        for (int i = 0; i < m_mapPoint.size(); i++) {
            TMapPoint point = new TMapPoint(m_mapPoint.get(i).getLatitude(),
                    m_mapPoint.get(i).getLongitude());
            TMapMarkerItem item1 = new TMapMarkerItem();
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mark_findbicycle);
            //poi_dot??? ????????? ?????? ?????? ??? ??????????????????

            item1.setTMapPoint(point);
            item1.setName(m_mapPoint.get(i).getName());
            item1.setVisible(item1.VISIBLE);

            item1.setIcon(bitmap);

            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mark_findbicycle);

            // ????????? ?????? ????????? ?????? ???????????????.
            item1.setCalloutTitle(m_mapPoint.get(i).getName());
            //item1.setCalloutSubTitle("??????");
            item1.setCanShowCallout(true);
            item1.setAutoCalloutVisible(true);


            String strID = String.format("pmarker%d", mMarkerID++);

            tmapview.addMarkerItem(strID, item1);
            mArrayMarkerID.add(strID);
        }
    }

    //??????????????? ??????
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "????????????????????? ?????? ??? ????????? ?????????????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            if (location != null) {
                double dlati = location.getLatitude();
                double dlongti = location.getLongitude();
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };



    public void setGps() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // ????????? ???????????????(???????????? NETWORK_PROVIDER ??????)
                1000, // ??????????????? ?????? ???????????? (miliSecond)
                1, // ??????????????? ?????? ???????????? (m)
                mLocationListener);

    }
}
