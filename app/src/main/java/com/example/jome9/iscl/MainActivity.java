package com.example.jome9.iscl;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.skt.Tmap.MapUtils.getDistance;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    static double LatSave, LonSave; //????????? ????????? ????????? ????????? ??????
    static String searchDestination;    //????????? ????????? ????????? ??????
    static String urlSave;  //????????? url???????????? ??????
    static String poiIdSave;    //???????????? poi id??? ???????????? ??????
    static String endEncodeResult; //??????????????? utf-8???????????? ??????????????? ???????????? ??????
    static String destination;  //?????????????????? ???????????? ??????
    static double destinationLat, destinationLon;   //???????????? ????????? ????????? ???????????? ????????? ??????
    static double startLat = 0, startLon = 0;   //???????????? ????????? ????????? ???????????? ????????? ?????? (????????? ?????? ??????)
    static String coordiSave[];
    static double coordiLon = 0, coordiLat = 0;
    static double saveLat[], saveLon[];
    static String saveTurnType[];
    static ArrayList<String> coordiList;
    static ArrayList<String> turnTList;
    static int pointNumber = 0;
    static LocationManager locationManager;
    EditText destinationEd;
    TMapView tMapView;
    TMapData tmapdata;
    TMapPoint tMapPointEnd;
    TMapPoint tMapPointStart;
    BluetoothSPP bt;
    static TextView tv;
    TextView dName;
    TextView dName2;
    static int dNum = 0;  //  ???????????? ?????? ????????? ???????????? ?????? 1?????? ????????? 0?????? ??? ??????
    static int checkNum = 1;
    static int meter = 0;
    FrameLayout setFrameLayout;
    FrameLayout naviFrameLayout;
    private BackpressCloseHandler backKeyClickHandler;

    String str, naviRoute;
    long nowtime = 0;
    long arrivetime = 0;
    Date htime1;
    Date dtime1;
    SimpleDateFormat df = new SimpleDateFormat("hh??? mm??? ss???");
    String now, arrive, speed;
    public double distance = 0;

    double time, minute, second;
    String m_time, s_time;
    public String arrive11;
    double runSpeed = 0.0;
    int count = 0;
    double sumSpeed = 0.0;
    double avg = 0.0;
    double avgkmh = 0.0;

    int a = 0;

    //day1??? day ??????
    Date day1;
    SimpleDateFormat day = new SimpleDateFormat("yyyy??? M??? dd???");
    String ridingday;
    int num;

    @Override
    public void onBackPressed() {
        backKeyClickHandler.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayoutTmap = (FrameLayout) findViewById(R.id.frameLayoutTmap);
        naviFrameLayout = (FrameLayout) findViewById(R.id.naviFrameLayout);
        setFrameLayout = (FrameLayout) findViewById(R.id.setFrameLayout);
        tMapView = new TMapView(this);
        tmapdata = new TMapData();
        final TMapPOIItem tMapPOIItem = new TMapPOIItem();
        final TMapGpsManager gps = new TMapGpsManager(this);
        tMapView.setSKTMapApiKey("86d8cbb8-7ee4-4d00-8d52-ca55321ec1e9");
        frameLayoutTmap.addView(tMapView);
        tMapView.setZoomLevel(17);

        ImageButton btnOpenDrawer = (ImageButton) findViewById(R.id.btn_mainmenu); // ????????? ????????? ??? ?????? ?????? ??????
        destinationEd = (EditText) findViewById(R.id.input_destination);
        ImageButton destinationSearchBtn = (ImageButton) findViewById(R.id.btn_search);

        dName = (TextView) findViewById(R.id.destinationName);
        ImageButton destiBtn = (ImageButton) findViewById(R.id.destinationBtn); //???????????????
        dName2 = (TextView) findViewById(R.id.destinationName2);
        final ImageButton naviBtn = (ImageButton) findViewById(R.id.naviBtn);   //????????????
        tv = tv = (TextView) findViewById(R.id.distance);

        //Button bluetoothBtn = (Button) findViewById(R.id.btn_bluetoothBtn);
        ImageButton currentBtn = (ImageButton) findViewById(R.id.current);

        backKeyClickHandler = new BackpressCloseHandler(this); //??????????????????

        //??????????????? ?????????
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout); // ??????????????? DrawerLayout ?????? ??????
        final View drawerView = (View) findViewById(R.id.drawer); // Drawer ??????(???) ?????? ??????
        TextView logininformationText = (TextView) findViewById(R.id.logininformation);
        LinearLayout findwayBtn = (LinearLayout) findViewById(R.id.btn_findway);
        LinearLayout findbicycleBtn = (LinearLayout) findViewById(R.id.btn_findbicycle);
        LinearLayout keepbicycleBtn = (LinearLayout) findViewById(R.id.btn_keepbicycle);
        LinearLayout weatherBtn = (LinearLayout) findViewById(R.id.btn_weather);
        LinearLayout ridingrecordBtn = (LinearLayout) findViewById(R.id.btn_ridingrecord);
        LinearLayout bluetoothBtn = (LinearLayout) findViewById(R.id.btn_bluetoothBtn);
        LinearLayout helpBtn = (LinearLayout) findViewById(R.id.btn_help);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        logininformationText.setText(userID);

        //*****************************GPS (????????????)
        tMapView.setCompassMode(true);
        tMapView.setSightVisible(true);
        tMapView.setIconVisibility(true);//??????????????? ????????? ???????????? ???????????? ????????? ???????????????.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setGps(); //???????????????

        //**************************** ???????????? ??????
        bt = new BluetoothSPP(this); //Initializing

        if (!bt.isBluetoothAvailable()) { //???????????? ?????? ??????
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
        //??????????????? ????????? ??????
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //????????? ??????
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();  //?????????
                checkNum = Integer.parseInt(message);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //???????????? ???
            public void onDeviceConnected(String name, String address) {
                bt.send("q", true);
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //????????????
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //????????????
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        //********************* ?????? ???????????????
        //??????????????????(????????? ??????)
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });//?????????????????????

        //(?????????)????????? ??????(??????????????? - ??????????????????)
        findwayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawerView);
            }
        });//(?????????)??????????????????
/*
        logininfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        */
        findbicycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Findbicycle.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                // finish();
            }
        });
        keepbicycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Keepbicycle_menu.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                //finish();
            }
        });
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Weather.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                //finish();
            }
        });
        ridingrecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RidingList.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                //finish();
            }
        });
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Help.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                //finish();
            }
        });


        ImageButton btn8 = (ImageButton) findViewById(R.id.stop);
        //ToggleButton togglebtn = (ToggleButton) findViewById(R.id.toggle);
        Switch togglebtn = (Switch) findViewById(R.id.toggle);
        final TextView toggletext = (TextView) findViewById(R.id.toggletext);
        final LinearLayout stoplayout=(LinearLayout) findViewById(R.id.stoplayout);

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.send("D", true);
                Toast.makeText(MainActivity.this, "???????????? ????????????.", Toast.LENGTH_SHORT).show();
                //mTextMsg.append("?????? --> ??????"+"\n");
                //mTextMsg.append("STOP"+"\n");
                System.out.println("?????? --> ??????");
            }
        });

        togglebtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                Toast.makeText(MainActivity.this, "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                bt.send("Y", true);
                toggletext.setText("???????????? ON");
                //mTextMsg.append("?????? ON"+"\n");
                //mTextMsg.append("Y"+"\n");
                System.out.println("?????? ON");
                stoplayout.setVisibility(View.VISIBLE);
            }
            else{
                Toast.makeText(MainActivity.this, "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                bt.send("J", true);
                toggletext.setText("???????????? OFF");
                //mTextMsg.append("?????? OFF"+"\n");
                //mTextMsg.append("N"+"\n");
                System.out.println("?????? OFF");
                stoplayout.setVisibility(View.GONE);
            }
        }
        });

        //?????????????????????
        destinationSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pointNumber = 0;
                searchDestination = destinationEd.getText().toString();  //????????????????????? ????????? ????????? searchDestination??? ??????
                destinationEd.setText("");
                DestinationData data = new DestinationData(searchDestination, startLat, startLon);
                // Intent??? Data?????? ??????
                Intent searchIntent = new Intent(MainActivity.this, DestinationList.class);
                searchIntent.putExtra("Ddata", data);
                // DestinationList???????????? Activity ??????
                startActivityForResult(searchIntent, 0);
            }
        }); //????????????????????? (destinationSearchBtn) ??????

        //????????? ?????? ?????? (???????????? ?????? + url?????????)
        destiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naviFrameLayout.setVisibility(View.VISIBLE);
                setFrameLayout.setVisibility(View.INVISIBLE);
                tMapPointStart = new TMapPoint(startLat, startLon);//????????? ??????
                tMapPointEnd = new TMapPoint(destinationLat, destinationLon); //????????? ??????
                //Toast.makeText(MainActivity.this, "?????? : "+destinationLat+", id??? : "+poiIdSave+"?????? : "+poi.item.getPOIName(), Toast.LENGTH_LONG).show(); //?????????
                Toast.makeText(MainActivity.this, "????????? ????????????", Toast.LENGTH_LONG).show();
                try {
                    endEncodeResult = URLEncoder.encode(destination, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("!!!!!!!!!!!!!! ?????? : " + endEncodeResult);
                urlSave = "https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&callback=&appKey=86d8cbb8-7ee4-4d00-8d52-ca55321ec1e9&passList=&angle=&speed=&reqCoordType=WGS84GEO&searchOption=0&resCoordType=WGS84GEO&sort=&gpsTime=15000"
                        + "&startX=" + startLon + "&startY=" + startLat +
                        "&endPoiId=" + poiIdSave + "&endX=" + destinationLon + "&endY=" + destinationLat +
                        "&startName=" + "%EC%B6%9C%EB%B0%9C" + "&endName=" + endEncodeResult;
                System.out.println("@@@@@@@@@@@@@@ url?????? ?????? : " + urlSave);     //?????????

                tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        System.out.println("???????????? ????????? ?????? " + tMapPointStart);    //?????????
                        polyLine.setLineWidth(17);
                        tMapView.removeAllMarkerItem();
                        tMapView.addTMapPath(polyLine);
                    }
                });
            }
        }); //????????? ???????????? ??????

        //???????????? ??????(????????? ?????? + ?????? ??????)
        naviBtn.setOnClickListener(new View.OnClickListener() {
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
                tMapView.setCompassMode(true); //?????????????????????
                tMapView.setCenterPoint(startLon, startLat);       //??? ?????????(?????????)??? ????????????????????????
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                nowtime = System.currentTimeMillis(); // ????????????
                htime1 = new Date(); // ????????? ?????? ??????
                now = df.format(htime1); // ????????? ??????
                day1 = new Date();//??????
                ridingday = day.format(day1); // ??????, ??????
                count = 0;
                a = 1;
               // arrive(); // ?????? ??????      //?????????????????? ????????????

                //naviBtn.setVisibility(View.INVISIBLE);
                naviFrameLayout.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
                MyAsyncTask mProcessTask = new MyAsyncTask();
                mProcessTask.execute();
            }
        });

        //???????????? ??????
        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //???????????? ?????? ??????
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }


            }
        }); //bluetoothBtn ??????


        //???????????? ??????
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, mLocationListener);
                Toast.makeText(getApplicationContext(), "??? ????????? 50m?????? ????????? ??? ????????????", Toast.LENGTH_LONG).show();
                // locationManager.requestLocationUpdates();
                //setGps(); //???????????????

            }
        });


        //???????????? ??????(: ????????? ???????????? ??????????????? ???)

    }//onCreate???


    @Override
    public void onLocationChange(Location location) {
        startLat = location.getLatitude();
        startLon = location.getLongitude();
    }

    //AsyncTask ?????? - ?????? ???????????? ????????? ????????? ????????? ??????.
    //AsyncTask ???????????? : onPreExecute -> doInBackground -> onPostExecute
    public class MyAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("\t?????????...");
            //show dialog
            progressDialog.show();
        }

        //doInBackground( ) ??????????????? ????????? ????????? onPostExcuted( ) ??? ?????? ??????????????? ??????
        protected String doInBackground(String... params) {
            try {
                URL naviURL = new URL(urlSave);
                HttpURLConnection conn = (HttpURLConnection) naviURL.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    naviRoute = buffer.toString();
                    Log.i("url ?????? ?????? : ", naviRoute);

                    reader.close();
                } else {
                    Log.i("?????? ??????", conn.getResponseCode() + "??????");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            JsonParser Parser = new JsonParser();
            JsonObject naviJsonObj = (JsonObject) Parser.parse(naviRoute);
            JsonArray naviRouteArray = (JsonArray) naviJsonObj.get("features");
            coordiList = new ArrayList<>();
            turnTList = new ArrayList<>();
            System.out.println("=========??????=========");
            for (int i = 0; i < naviRouteArray.size(); i++) {      //?????? ??? memberArray.size()
                JsonObject features = (JsonObject) naviRouteArray.get(i);
                JsonObject geometry = (JsonObject) features.get("geometry");
                JsonObject properties = (JsonObject) features.get("properties");
                String type = geometry.get("type").getAsString();
                Log.i("????????? ?????? ??????", type);   //????????? ->point ????????? ??????

                if (type.equals("Point")) {
                    String Pcoordinates = String.valueOf(geometry.get("coordinates"));
                    Log.i("????????? coordinate ??????", Pcoordinates);
                    String PturnType = String.valueOf(properties.get("turnType"));
                    Log.i("????????? ????????? ??????", PturnType);
                    System.out.println("a??? " + i + "?????? : " + Pcoordinates);
                    pointNumber++;
                    coordiList.add(Pcoordinates);
                    System.out.println("???????????? ?????????");
                    turnTList.add(PturnType);
                }//point?????? if??? ??????

                if (i == naviRouteArray.size() - 1) {
                    System.out.println("j??? : " + pointNumber);
                    saveLat = new double[pointNumber];
                    saveLon = new double[pointNumber];
                    saveTurnType = new String[pointNumber];
                    int k = 0;
                    System.out.println("pointNumber??????~~~~~~~~~~~~~~~~~~~~ : " + pointNumber);
                    for (int o = 0; o < pointNumber; o++) {
                        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                        System.out.println("????????? ????????? " + o + "?????? : " + coordiList.get(o));
                        System.out.println(coordiList.get(o));
                        coordiSave = coordiList.get(o).split(",");
                        coordiLon = Double.parseDouble(coordiSave[0].substring(1));  //?????? ??????
                        coordiLat = Double.valueOf(coordiSave[1].substring(0, coordiSave[1].length() - 1)); //?????? ??????
                        System.out.println("?????? ?????? : " + coordiLat);
                        System.out.println("k ?????? : " + k);
                        saveTurnType[k] = turnTList.get(k);
                        saveLat[k] = coordiLat;
                        saveLon[k] = coordiLon;
                        k++;
                    }//for??? 1??? ??????
                    for (int e = 0; e < k; e++) {
                        System.out.println("??????????????? ???????????? : " + saveLat[e] + " & " + saveLon[e]);
                        System.out.println("??????????????? ????????? ?????? : " + saveTurnType[e]);
                        addPointMarker(saveLat[e], saveLon[e], e);
                    }//for??? 2??? ??????

                }//if??? ??????
                System.out.println("------------------------");

            }   //???for???

            dNum = 1;

            getDistance(startLat, startLon, saveLat[checkNum], saveLon[checkNum]); //???????????? ???

            Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show();

        }   //onPostExecute ??????

    }   //MyAsyncTask ??????

    //******************************** ????????? ?????? *******************************************
    //?????? ?????? ?????????
    private void moveMap(double lat, double lng) {
        tMapView.setCenterPoint(lng, lat);
    }

    //???????????? ?????????
    public void getDistance(double a, double b, double c, double d) {
        float results[] = new float[3];
        Location.distanceBetween(a, b, c, d, results);
        System.out.println((int) results[0]);
        meter = (int) results[0];
        tv.setText(meter + "m");
    }


    //?????? ?????? ?????????
    private void addMarker(double lat, double lng, String title) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.mapmarker);
        Bitmap icon2 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.rightarrow); //????????? ??????????????? ?????????
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //????????? ???????????? ????????? ????????? ??????
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);   //?????? ????????? ??????
        markerItem.setTMapPoint(tMapPoint);  //????????? ??????
        markerItem.setName(title);    //?????? ??????
        markerItem.setCalloutTitle(title);
        markerItem.setCalloutRightButtonImage(icon2);
        tMapView.addMarkerItem("markerItem", markerItem);  //id?????? ???????????? ????????? ?????????.
        markerItem.setCanShowCallout(true);
    }   //addMarker ??????

    private void addPointMarker(double lat, double lng, int i) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.point);
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //????????? ???????????? ????????? ????????? ??????
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);   //?????? ????????? ??????
        markerItem.setTMapPoint(tMapPoint);  //????????? ??????
        tMapView.addMarkerItem("markerItem" + i, markerItem);  //id?????? ???????????? ????????? ?????????.
        markerItem.setCanShowCallout(true);
        i++;
    }   //addMarker ??????

    private void addPointMarkerYellow(double lat, double lng, int i) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pointy);
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //????????? ???????????? ????????? ????????? ??????
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);   //?????? ????????? ??????
        markerItem.setTMapPoint(tMapPoint);  //????????? ??????
        tMapView.addMarkerItem("markerItem" + i, markerItem);  //id?????? ???????????? ????????? ?????????.
        markerItem.setCanShowCallout(true);
        i++;
    }   //addMarker ??????

    private void addCircle(double lat, double lng, int i) {
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //????????? ???????????? ????????? ????????? ??????
        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint(tMapPoint);
        tMapCircle.setRadius(50);
        tMapCircle.setCircleWidth(7);
        tMapCircle.setLineColor(Color.GRAY);
        tMapCircle.setAreaColor(Color.BLUE);
        tMapCircle.setAreaAlpha(300);
        tMapView.addTMapCircle("circle" + i, tMapCircle);
        i++;
    }   //addCircle ??????


    //????????? ??????
    private void arrive() {

        arrivetime = System.currentTimeMillis();
        dtime1 = new Date(); // ????????? ?????? ??????
        arrive = df.format(dtime1); // ????????? ??????

        time = /*(arrivetime - nowtime) / 1000.0;;*/6120;
        minute = time / 60;
        if (time < 60) {
            minute = 0;
        }
        second = time % 60;
        m_time = String.format("%.0f", minute);
        s_time = String.format("%.0f", second);

        distance = avg * time;

        if(distance==0)
            arrive11="31600";
        else
            arrive11 = String.format("%.3f", distance);

        if(avg==0)
            speed="18.6";
        else{
            avg = sumSpeed / (double) count;
            speed = String.format("%.2f", avgkmh);
            avgkmh = avg / 1000 * 3600;}


        //??????
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "Record.db", null, 1);

        dbHelper.insert(num, ridingday, now, arrive, m_time, s_time, arrive11, speed);


        Intent okok = new Intent(MainActivity.this, Ridingrecord.class);
        okok.putExtra("??????", ridingday);
        okok.putExtra("?????? ??????", now);
        okok.putExtra("?????? ??????", arrive);
        okok.putExtra("?????? ???", m_time);
        okok.putExtra("?????? ???", s_time);
        okok.putExtra("??????", arrive11);
        okok.putExtra("??????", speed);
        startActivity(okok);
        //?????????????????? ????????????

    }


    //location?????????
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            int i = 0;
            if (location != null) {
                startLat = location.getLatitude();
                startLon = location.getLongitude();
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^ ???????????? " + startLat + ", " + startLon); // ?????????
                tMapView.setLocationPoint(startLon, startLat);     //??? ?????????(?????????)??? ????????????
                tMapView.setCenterPoint(startLon, startLat);       //??? ?????????(?????????)??? ????????????????????????
                //runspeed = location.getSpeed();
                //Log.e("speedddd : ", String.valueOf(runspeed));

                runSpeed = location.getSpeed();
                count++;
                sumSpeed+=runSpeed;
                if(runSpeed==0){
                    count--;
                }

                if (dNum == 0) {
                    //Toast.makeText(getApplicationContext(), "?????? :" + startLat + "?????? :" + startLon + "[" + i + "]", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkNum != 0) {
                        getDistance(startLat, startLon, saveLat[checkNum], saveLon[checkNum]);  //????????? ?????? ???
                    }
                    if(meter>=50&&meter<60) //50?????????
                        bt.send("5",true);
                    else if(meter>=40&&meter<50)
                        bt.send("4", true);
                    else if(meter>=30&&meter<40)
                        bt.send("3", true);
                    else if(meter>=20&&meter<30)
                        bt.send("2", true);
                    else if (meter <= 10) {
                        addPointMarkerYellow(saveLat[checkNum], saveLon[checkNum], checkNum);
                        bt.send("1",true);
                        Log.i("checkNum","check"+checkNum);
                        Log.i("pointNum","point"+pointNumber);

                        if(checkNum==pointNumber-1) {
                            checkNum = 1;
                            if(a==1){
                                arrive();
                                a=0;
                            }
                        }
                        /*else if(checkNum==pointNumber-1){
                            if(a==1){
                                arrive();
                                a=0;
                            }
                        }*/

                        switch (saveTurnType[checkNum]) {
                            case "11":
                                bt.send("G", true);
                                checkNum = 0;
                                meter = 100;
                                break;
                            case "12":
                            case "16":
                            case "17":
                            case "212":
                                bt.send("L", true);
                                checkNum = 0;
                                meter = 100;
                                break;
                            case "13":
                            case "18":
                            case "19":
                            case "213":
                                bt.send("R", true);
                                checkNum = 0;
                                meter = 100;
                                break;
                        }   //switch??? ??????
                    }   //15m?????? if??? ??????

                }   //else??? ???
                i++;
            }
        }   //onLocationChanged ???

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };//location????????????

    //?????????????????????
    public void setGps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 1, mLocationListener);
        Toast.makeText(getApplicationContext(), "??? ????????? 50m?????? ????????? ??? ????????????", Toast.LENGTH_LONG).show();
    } //????????????????????????

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //???????????? ??????
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID??? ??????????????? ?????? ??????
                //setup();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 2) {
            MarkerInfo info = (MarkerInfo) data.getSerializableExtra("info");
            moveMap(info.latSave, info.lonSave);
            addMarker(info.latSave, info.lonSave, info.poiNameSave);
            setFrameLayout.setVisibility(View.VISIBLE);
            naviFrameLayout.setVisibility(View.INVISIBLE);
            dName.setText(info.poiNameSave);
            dName2.setText(info.poiNameSave);
            destinationLat = info.latSave;
            destinationLon = info.lonSave;
            destination = info.poiNameSave;
        }
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                //setup();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }   //onActivityResult??????

}