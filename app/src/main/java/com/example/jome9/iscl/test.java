package com.example.jome9.iscl;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonArray;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.logging.LogManager;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.jome9.iscl.MainActivity.checkNum;
import static com.example.jome9.iscl.MainActivity.locationManager;

public class test extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps =null;
    private TMapView tMapView = null;
    private static String mApiKey = "86d8cbb8-7ee4-4d00-8d52-ca55321ec1e9";
    private static int mMarkerID;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();

    static double startLat = 0, startLon = 0;
    TMapPoint tMapPointEnd;
    TMapPoint tMapPointStart;
    TMapData tmapdata;
    // ????????? ?????? ????????? ???
    static String endEncodeResult;
    static String urlSave;
    long nowtime=0;
    Date htime1;
    String now;
    SimpleDateFormat df=new SimpleDateFormat("hh??? mm??? ss???");
    public double totaldistance;
    public double distance;
    static TextView tv;
    long arrivetime=0;
    Date dtime1;
    String arrive;
    double time;
    String time1212;
    public String arrive11;
    double avgspeed;
    String speed;
    static ArrayList<String> coordiList;
    static ArrayList<String> turnTList;
    static double saveLat[], saveLon[];
    static int pointNumber = 0;
    static String saveTurnType[];
    static String coordiSave[];
    static double coordiLon = 0, coordiLat = 0;
    static int dNum=0;
    static int meter=0;
    BluetoothSPP bt;
    static double radLat=0,radLon=0;
    static String Name;
    Workbook workbook = null;
    Sheet sheet = null;
    static double markerSpotLatitude;
    static double markerSpotLongitude;
    String str, naviRoute;
    double minute,second;
    String m_time, s_time;
    double sumSpeed=0.0;
    double avg=0.0;
    double avgkmh=0.0;
    int count=0;
    double runSpeed=0.0;
    int a=0;
    Date day1;
    SimpleDateFormat day = new SimpleDateFormat("yyyy??? M??? dd???");
    String ridingday;
    int num;

    TextView dName; TextView dName2;

    ImageButton bikeimg;
    ImageButton notic;
    private CustomDialog dialog;
    @Override
    public void onLocationChange(Location location){
        if(m_bTrackingMode){
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            startLat = location.getLatitude(); // ????????????
            startLon = location.getLongitude(); // ????????????
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        tmapdata = new TMapData();

        tv = (TextView) findViewById(R.id.distance); // ????????? ???

        bikeimg = (ImageButton)findViewById(R.id.bikeimg);
        // notic =(ImageButton)findViewById(R.id.notic);
        ImageButton currentBtn = (ImageButton) findViewById(R.id.current);

        Intent intent = getIntent(); // MainActivity?????? ????????? ??? ??????
        String ExcelLat = intent.getStringExtra("ExcelLat");
        String ExcelLon = intent.getStringExtra("ExcelLon");
        String Name = intent.getStringExtra("name");
        double Lat = Double.valueOf(ExcelLat).doubleValue(); // ???????????? ExcelLat??? double????????? ??????
        double Lon = Double.valueOf(ExcelLon).doubleValue(); // ???????????? ExcelLon??? double????????? ??????

        ImageButton btn_back = (ImageButton) findViewById(R.id.back_btn);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mContext = this;
        FrameLayout layout1 = (FrameLayout)findViewById(R.id.frameLayoutTmap);
        tMapView = new TMapView(this);
        layout1.addView(tMapView);
        tMapView.setSKTMapApiKey(mApiKey);

        tMapView.setCompassMode(true);
        tMapView.setIconVisibility(true);

        tMapView.setCenterPoint(Lon,Lat); // ?????? ????????? ???????????? ??????
        tMapView.setZoomLevel(12);

        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        tmapgps = new TMapGpsManager(test.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();

        /* ??????????????? ????????? ??????????????? ??????*/
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        addPoint();
//        dialog();

        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() { // ???????????? ???????????? ??????
            @Override
            public boolean onPressEvent(ArrayList arrayList, ArrayList arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                for (TMapMarkerItem item : arrayList) {
                    TMapPoint markerSpot = item.getTMapPoint();
                    markerSpotLatitude = markerSpot.getLatitude(); // ????????? ??????
                    markerSpotLongitude = markerSpot.getLongitude(); // ????????? ??????

                    // Toast.makeText(getApplicationContext(), "LAT ??? : " + markerSpotLatitude + " LON ??? : " + markerSpotLongitude, Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() { // ?????? ????????? ????????? ?????????
            @Override
            public void onCalloutRightButton(final TMapMarkerItem tMapMarkerItem) {

                AlertDialog.Builder builder = new AlertDialog.Builder(test.this);
                builder.setTitle("???????????? ??????");
                builder.setIcon(R.drawable.notic);
                builder.setMessage("??????????????? ?????????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(test.this,"????????? ????????????!!",Toast.LENGTH_SHORT).show();

                        Intent intent = getIntent(); // MainActivity?????? ????????? ??? ??????
                        String Name = intent.getStringExtra("name");

                        tMapPointStart = new TMapPoint(startLat, startLon);//????????? ??????
                        tMapPointEnd = new TMapPoint(markerSpotLatitude, markerSpotLongitude); //????????? ??????

                        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd, new TMapData.FindPathDataListenerCallback() {
                            @Override
                            public void onFindPathData(TMapPolyLine polyLine) {
                                System.out.println("???????????? ????????? ?????? " + tMapPointStart);
                                polyLine.setLineWidth(17);
                                tMapView.removeAllMarkerItem();
                                tMapView.addTMapPath(polyLine);
                            }
                        });
                        // ????????? ?????? ????????? ???
                        try {
                            endEncodeResult = URLEncoder.encode(Name, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.println("!!!!!!!!!!!!!! ?????? : " + endEncodeResult);
                        urlSave = "https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&callback=&appKey=86d8cbb8-7ee4-4d00-8d52-ca55321ec1e9&passList=&angle=&speed=&reqCoordType=WGS84GEO&searchOption=0&resCoordType=WGS84GEO&sort=&gpsTime=15000"
                                + "&startX=" + startLon + "&startY=" + startLat +
                                "&endX=" + markerSpotLongitude + "&endY=" + markerSpotLatitude +
                                "&startName=" + "%EC%B6%9C%EB%B0%9C" + "&endName=" + endEncodeResult;
                        System.out.println("@@@@@@@@@@@@@@ url?????? ?????? : " + urlSave);

                        MyAsyncTask mProcessTask = new MyAsyncTask();
                        mProcessTask.execute();
                        tMapView.setCompassMode(true); //?????????????????????
                        // tMapView.setCenterPoint(startLon, startLat);       //??? ?????????(?????????)??? ????????????????????????

                        /* ??????????????? ????????? ??????????????? ??????*/
                        tMapView.setZoomLevel(18);
                        tMapView.setTrackingMode(true);
                        tMapView.setSightVisible(true);


                        // ???????????? ?????? ????????? ????????? ????????? ?????? ??????
                        nowtime=System.currentTimeMillis(); // ????????????
                        htime1 = new Date(); // ????????? ?????? ??????
                        now = df.format(htime1); // ????????? ??????
                        // arrive(); // ?????? ??????
                    }
                });
                builder.setNegativeButton("??????",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();


            }
        });

        //*****************************GPS (????????????)
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

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //????????? ??????
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(test.this, message, Toast.LENGTH_SHORT).show();  //?????????
                if(message.equals("1")) {
                    checkNum +=1;
                    System.out.println("checkNum ??? : "+checkNum);
                }
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

        bikeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exceltest();
                Toast.makeText(test.this,"?????? 5km ?????????????????????.",Toast.LENGTH_SHORT).show();

            }
        });
      /*  notic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });*/

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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,10,mLocationListener);
                // locationManager.requestLocationUpdates();
                setGps(); //???????????????

            }
        });

    } // oncreat ???

  /*  public void dialog(){
        CustomDialog oDialog = new CustomDialog(this);
        oDialog.setCancelable(false);
        oDialog.show();
    }*/

    //AsyncTask ?????? - ?????? ???????????? ????????? ????????? ????????? ??????.
    //AsyncTask ???????????? : onPreExecute -> doInBackground -> onPostExecute
    public class MyAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(test.this);
        OkHttpClient client = new OkHttpClient();

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

//            getDistance(startLat, startLon, saveLat[checkNum], saveLon[checkNum]); //???????????? ???

            Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show();

        }   //onPostExecute ??????

    }   //MyAsyncTask ??????

    private void moveMap(double lat, double lng) {
        tMapView.setCenterPoint(lng, lat);
    }

    public void exceltest(){ // ?????? ?????????

        tMapView.setZoomLevel(13);
        circle();

        double theta, dist;
        //double radLat,radLon;

        try {
            InputStream inputStream = getBaseContext().getResources().getAssets().open("dd.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);
            int MaxColumn = 2, RowStart = 1, RowEnd = sheet.getColumn(MaxColumn - 1).length -1, ColumnStart = 0, ColumnEnd = sheet.getRow(2).length - 1;

            for(int row = RowStart; row <= RowEnd; row++) {

                String excelload3 = sheet.getCell(3, row).getContents(); // ??????
                String excelload4 = sheet.getCell(4, row).getContents(); // ??????
                Name = sheet.getCell(0,row).getContents(); // ????????? ????????????
                radLat = Double.valueOf(excelload3).doubleValue();
                radLon = Double.valueOf(excelload4).doubleValue();

                theta = startLon - radLon;
                dist = Math.sin(DegreeToRadian(startLat)) * Math.sin(DegreeToRadian(radLat)) + Math.cos(DegreeToRadian(startLat))
                        * Math.cos(DegreeToRadian(radLat)) * Math.cos(DegreeToRadian(theta));
                dist = Math.acos(dist);
                dist = RadianToDegree(dist);

                dist = dist * 60 * 1.1515;
                dist = dist * 1.609344; // km??? ??????

                if(dist < 3){
                    // Toast.makeText(test.this, String.valueOf(dist), Toast.LENGTH_SHORT).show();
                    final ArrayList alTMapPoint = new ArrayList();

                    m_mapPoint.add(new MapPoint(Name,radLat,radLon));

                    TMapPoint point = new TMapPoint(radLat,radLon);
                    TMapMarkerItem item1 = new TMapMarkerItem();
                    Bitmap bitmap = null;
                    bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.mapmarker);

                    item1.setTMapPoint(point);
                    item1.setName(Name);
                    item1.setVisible(item1.VISIBLE);

                    item1.setIcon(bitmap);

                    // bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.mapmarker);

                    item1.setCalloutTitle(Name);
                    item1.setCalloutSubTitle(Name);
                    item1.setCanShowCallout(true);
                    //item1.setAutoCalloutVisible(true);

                    Bitmap bitmap_i = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.rightarrow);

                    item1.setCalloutRightButtonImage(bitmap_i);

                    String strID = String.format("pmarker"+row,mMarkerID++);

                    tMapView.addMarkerItem(strID,item1);
                    mArrayMarkerID.add(strID);
                    //tMapView.addMarkerItem("marker"+row,marker);

                } //if???
            } //for???
        } catch (IOException e) {
            e.printStackTrace();
        }catch (BiffException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }

    }
    public double DegreeToRadian(double degree){
        return degree * Math.PI / 180.0;
    }
    //randian -> degree ??????
    public double RadianToDegree(double radian){
        return radian * 180d / Math.PI;
    }

    public void addPoint(){
        Intent intent = getIntent(); // MainActivity?????? ????????? ??? ??????
        String ExcelLat = intent.getStringExtra("ExcelLat");
        String ExcelLon = intent.getStringExtra("ExcelLon");
        String Name = intent.getStringExtra("name");
        double Lat = Double.valueOf(ExcelLat).doubleValue(); // ???????????? ExcelLat??? double????????? ??????
        double Lon = Double.valueOf(ExcelLon).doubleValue(); // ???????????? ExcelLon??? double????????? ??????

        m_mapPoint.add(new MapPoint(Name,Lat,Lon));

        TMapPoint point = new TMapPoint(Lat,Lon);
        TMapMarkerItem item1 = new TMapMarkerItem();
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.mapmarker);

        item1.setTMapPoint(point);
        item1.setName(Name);
        item1.setVisible(item1.VISIBLE);

        item1.setIcon(bitmap);

        // bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.mapmarker);

        item1.setCalloutTitle(Name);
        item1.setCalloutSubTitle(Name);
        item1.setCanShowCallout(true);
        item1.setAutoCalloutVisible(true);

        Bitmap bitmap_i = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.rightarrow);

        item1.setCalloutRightButtonImage(bitmap_i);
        String strID = String.format("pmarker%d",mMarkerID++);

        tMapView.addMarkerItem(strID,item1);
        mArrayMarkerID.add(strID);
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
        tMapView.addMarkerItem("markerItem"+i, markerItem);  //id?????? ???????????? ????????? ?????????.
        markerItem.setCanShowCallout(true);
        i++;
    }   //addMarker ??????
    private void addPointMarkerYellow(double lat, double lng, int i) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pointy);
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //????????? ???????????? ????????? ????????? ??????
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);   //?????? ????????? ??????
        markerItem.setTMapPoint(tMapPoint);  //????????? ??????
        tMapView.addMarkerItem("markerItem"+i, markerItem);  //id?????? ???????????? ????????? ?????????.
        markerItem.setCanShowCallout(true);
        i++;
    }   //addMarker ??????

    public void circle(){ // 3km ?????? ??? ?????????

        TMapPoint tMapPoint = new TMapPoint(startLat, startLon);

        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint( tMapPoint );
        tMapCircle.setRadius(3000); // ?????????
        tMapCircle.setCircleWidth(2); // ?????????
        tMapCircle.setLineColor(Color.BLACK); // ????????????
        tMapCircle.setAreaColor(Color.WHITE); // ??? ??? ??????
        tMapCircle.setAreaAlpha(0); // ??? ??? ?????????
        tMapView.addTMapCircle("circle1", tMapCircle);

    }


    //???????????? ?????????
    public void getDistance (double a, double b, double c, double d) {
        float results[] = new float[3];
        Location.distanceBetween(a,b,c,d, results);
        System.out.println((int)results[0]);
        meter = (int)results[0];
        //tv.setText(meter+"m");
    }
    //????????? ??????
    private void arrive() {

        arrivetime = System.currentTimeMillis();
        dtime1 = new Date(); // ????????? ?????? ??????
        arrive = df.format(dtime1); // ????????? ??????

        time = (arrivetime - nowtime) / 1000.0;;
        minute = time / 60;
        if (time < 60) {
            minute = 0;
        }
        second = time % 60;
        m_time = String.format("%.0f", minute);
        s_time = String.format("%.0f", second);

        distance = avg * time;

        if(distance==0)
            arrive11="0";
        else
            arrive11 = String.format("%.3f", distance);

        if(avg==0)
            speed="0";
        else{
            avg = sumSpeed / (double) count;
            speed = String.format("%.2f", avgkmh);
            avgkmh = avg / 1000 * 3600;}


        //??????
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "Record.db", null, 1);

        dbHelper.insert(num, ridingday, now, arrive, m_time, s_time, arrive11, speed);


        Intent okok = new Intent(test.this, Ridingrecord.class);
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
            dName.setText(info.poiNameSave);
            dName2.setText(info.poiNameSave);
            markerSpotLatitude = info.latSave;
            markerSpotLongitude = info.lonSave;
            Name = info.poiNameSave;
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