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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import static com.example.jome9.iscl.MainActivity.checkNum;
//import static com.example.jome9.iscl.MainActivity.locationManager;

public class neartest extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private Context mContext = null;
    private boolean m_bTrackingMode = true;
    static LocationManager locationManager;
    static int checkNum=1;
    private TMapGpsManager tmapgps = null;
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
    // 밑에서 부터 추가한 것
    static String endEncodeResult;
    static String urlSave;
    long nowtime = 0;
    Date htime1;
    String now;
    SimpleDateFormat df = new SimpleDateFormat("hh시 mm분 ss초");
    public double totaldistance;
    public double distance;
    static TextView tv;
    long arrivetime = 0;
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
    static int dNum = 0;
    static int meter = 0;
    BluetoothSPP bt;
    static double radLat = 0, radLon = 0;
    static String Name;
    Workbook workbook = null;
    Sheet sheet = null;
    static double markerSpotLatitude;
    static double markerSpotLongitude;
    int count=0;
    int a=0;
    double sumSpeed=0.0;
    double avg=0.0;
    double avgkmh=0.0;
    double minute,second;
    String m_time, s_time;
    String str, naviRoute;
    ImageButton bikeimg;
    ImageButton notic;
    double runSpeed=0.0;
    TextView dName; TextView dName2;

    Date day1;
    SimpleDateFormat day = new SimpleDateFormat("yyyy년 M월 dd일");
    String ridingday;
    int num;

    private CustomDialog dialog;

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            startLat = location.getLatitude(); // 현재위도
            startLon = location.getLongitude(); // 현재경도
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        tmapdata = new TMapData();

        tv = (TextView) findViewById(R.id.distance); // 추가한 것

        bikeimg = (ImageButton) findViewById(R.id.bikeimg);
        // notic =(ImageButton)findViewById(R.id.notic);
        ImageButton currentBtn = (ImageButton) findViewById(R.id.current);

        mContext = this;
        FrameLayout layout1 = (FrameLayout) findViewById(R.id.frameLayoutTmap);
        tMapView = new TMapView(this);
        layout1.addView(tMapView);
        tMapView.setSKTMapApiKey(mApiKey);

        tMapView.setCompassMode(true);
        tMapView.setIconVisibility(true);

        tMapView.setCenterPoint(startLon, startLat); // 화면 중심을 목적지로 이동
        tMapView.setZoomLevel(12);

        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        tmapgps = new TMapGpsManager(neartest.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();

        /* 화면중심을 단말의 현재위치로 이동*/
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        ImageButton btn_back = (ImageButton) findViewById(R.id.back_btn);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

       // dialog();


        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() { // 터치위치 위경도값 추출
            @Override
            public boolean onPressEvent(ArrayList arrayList, ArrayList arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                for (TMapMarkerItem item : arrayList) {
                    TMapPoint markerSpot = item.getTMapPoint();
                    markerSpotLatitude = markerSpot.getLatitude(); // 마커의 위도
                    markerSpotLongitude = markerSpot.getLongitude(); // 마커의 경도

                    // Toast.makeText(getApplicationContext(), "LAT 값 : " + markerSpotLatitude + " LON 값 : " + markerSpotLongitude, Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() { // 풍선 오른쪽 이미지 클릭시
            @Override
            public void onCalloutRightButton(final TMapMarkerItem tMapMarkerItem) {

                tMapPointStart = new TMapPoint(startLat, startLon);//출발지 설정
                tMapPointEnd = new TMapPoint(markerSpotLatitude, markerSpotLongitude); //목적지 설정
                //Toast.makeText(MainActivity.this, "위도 : "+destinationLat+", id값 : "+poiIdSave+"이름 : "+poi.item.getPOIName(), Toast.LENGTH_LONG).show(); //확인용
                Toast.makeText(neartest.this, "목적지 설정완료", Toast.LENGTH_LONG).show();
                try {
                    endEncodeResult = URLEncoder.encode(Name, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("!!!!!!!!!!!!!! 결과 : " + endEncodeResult);
                urlSave = "https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&callback=&appKey=86d8cbb8-7ee4-4d00-8d52-ca55321ec1e9&passList=&angle=&speed=&reqCoordType=WGS84GEO&searchOption=0&resCoordType=WGS84GEO&sort=&gpsTime=15000"
                        + "&startX=" + startLon + "&startY=" + startLat +
                        "&endX=" + markerSpotLongitude + "&endY=" + markerSpotLatitude +
                        "&startName=" + "%EC%B6%9C%EB%B0%9C" + "&endName=" + endEncodeResult;
                System.out.println("@@@@@@@@@@@@@@ url결과 확인 : " + urlSave);     //확인용

                tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        System.out.println("폴리라인 시작점 확인 " + tMapPointStart);    //확인용
                        polyLine.setLineWidth(17);
                        tMapView.removeAllMarkerItem();
                        tMapView.addTMapPath(polyLine);
                    }
                });  //목적지 설정버튼 종료


                AlertDialog.Builder builder = new AlertDialog.Builder(neartest.this);
                builder.setTitle("경로안내 설정");
                builder.setIcon(R.drawable.notic);
                builder.setMessage("경로안내를 시작하시겠습니까?");
                builder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(neartest.this, "목적지 설정완료!!", Toast.LENGTH_SHORT).show();

                        tMapView.setCenterPoint(startLon, startLat);       //이 위경도(현위치)를 지도위치가운데로
                        tMapView.setZoomLevel(18); // 지도 화면 크기 줌 설정

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
                        tMapView.setCompassMode(true); //나침반기능설정
                        tMapView.setCenterPoint(startLon, startLat);       //이 위경도(현위치)를 지도위치가운데로
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                        nowtime = System.currentTimeMillis(); // 걸린시간
                        htime1 = new Date(); // 출발한 시간 추출
                        now = df.format(htime1); // 출발한 시간
                        count=0;
                        a=1;
                        arrive(); // 도착 함수(테스트에만 살려놓기!)

                        //naviBtn.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "파싱 확인", Toast.LENGTH_SHORT).show();
                        MyAsyncTask mProcessTask = new MyAsyncTask();
                        mProcessTask.execute();
                    }
                });

                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();


            }
        });

        //*****************************GPS (현재위치)
        tMapView.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정합니다.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setGps(); //현위치탐색


        //**************************** 블루투스 설정
        bt = new BluetoothSPP(this); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(neartest.this, message, Toast.LENGTH_SHORT).show();  //확인용
                if(message.equals("1")) {
                    checkNum +=1;
                    System.out.println("checkNum 값 : "+checkNum);
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                bt.send("q", true);
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        bikeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exceltest();
                Toast.makeText(neartest.this,"반경 5km 검색결과입니다.",Toast.LENGTH_SHORT).show();
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
                setGps(); //현위치탐색
            }
        });

    } // oncreate 끝

 /*   public void dialog(){
        CustomDialog oDialog = new CustomDialog(this);
        oDialog.setCancelable(false);
        oDialog.show();
    }*/

    //AsyncTask 생성 - 모든 네트워크 로직을 여기서 작성해 준다.
    //AsyncTask 실행순서 : onPreExecute -> doInBackground -> onPostExecute
    public class MyAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(neartest.this);
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("\t로딩중...");
            //show dialog
            progressDialog.show();
        }

        //doInBackground( ) 메소드에서 작업이 끝나면 onPostExcuted( ) 로 결과 파라미터를 리턴
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
                    Log.i("url 내용 확인 : ", naviRoute);

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
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
            System.out.println("=========결과=========");
            for (int i = 0; i < naviRouteArray.size(); i++) {      //수정 전 memberArray.size()
                JsonObject features = (JsonObject) naviRouteArray.get(i);
                JsonObject geometry = (JsonObject) features.get("geometry");
                JsonObject properties = (JsonObject) features.get("properties");
                String type = geometry.get("type").getAsString();
                Log.i("파싱한 타입 확인", type);   //확인용 ->point 출력될 것임

                if (type.equals("Point")) {
                    String Pcoordinates = String.valueOf(geometry.get("coordinates"));
                    Log.i("파싱한 coordinate 확인", Pcoordinates);
                    String PturnType = String.valueOf(properties.get("turnType"));
                    Log.i("파싱한 턴타입 확인", PturnType);
                    System.out.println("a는 " + i + "번째 : " + Pcoordinates);
                    pointNumber++;
                    coordiList.add(Pcoordinates);
                    System.out.println("리스트에 저장됨");
                    turnTList.add(PturnType);
                }//point일치 if문 종료

                if (i == naviRouteArray.size() - 1) {
                    System.out.println("j값 : " + pointNumber);
                    saveLat = new double[pointNumber];
                    saveLon = new double[pointNumber];
                    saveTurnType = new String[pointNumber];
                    int k = 0;
                    System.out.println("pointNumber확인~~~~~~~~~~~~~~~~~~~~ : " + pointNumber);
                    for (int o = 0; o < pointNumber; o++) {
                        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                        System.out.println("위경도 리스트 " + o + "번째 : " + coordiList.get(o));
                        System.out.println(coordiList.get(o));
                        coordiSave = coordiList.get(o).split(",");
                        coordiLon = Double.parseDouble(coordiSave[0].substring(1));  //경도 추출
                        coordiLat = Double.valueOf(coordiSave[1].substring(0, coordiSave[1].length() - 1)); //위도 추출
                        System.out.println("더블 확인 : " + coordiLat);
                        System.out.println("k 확인 : " + k);
                        saveTurnType[k] = turnTList.get(k);
                        saveLat[k] = coordiLat;
                        saveLon[k] = coordiLon;
                        k++;
                    }//for문 1번 종료
                    for (int e = 0; e < k; e++) {
                        System.out.println("마지막으로 배열확인 : " + saveLat[e] + " & " + saveLon[e]);
                        System.out.println("마지막으로 턴타입 확인 : " + saveTurnType[e]);
                        addPointMarker(saveLat[e], saveLon[e], e);
                    }//for문 2번 종료

                }//if문 종료
                System.out.println("------------------------");

            }   //큰for문

            dNum = 1;

//            getDistance(startLat, startLon, saveLat[checkNum], saveLon[checkNum]); //처음거리 값

            Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_SHORT).show();

        }   //onPostExecute 종료

    }   //MyAsyncTask 종료

    private void moveMap(double lat, double lng) {
        tMapView.setCenterPoint(lng, lat);
    }

    public void exceltest(){ // 반경 구하기

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

                String excelload3 = sheet.getCell(3, row).getContents(); // 위도
                String excelload4 = sheet.getCell(4, row).getContents(); // 경도
                Name = sheet.getCell(0,row).getContents(); // 자전거 보관소명
                radLat = Double.valueOf(excelload3).doubleValue();
                radLon = Double.valueOf(excelload4).doubleValue();

                theta = startLon - radLon;
                dist = Math.sin(DegreeToRadian(startLat)) * Math.sin(DegreeToRadian(radLat)) + Math.cos(DegreeToRadian(startLat))
                        * Math.cos(DegreeToRadian(radLat)) * Math.cos(DegreeToRadian(theta));
                dist = Math.acos(dist);
                dist = RadianToDegree(dist);

                dist = dist * 60 * 1.1515;
                dist = dist * 1.609344; // km로 계산

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

                } //if문
            } //for문
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
    //randian -> degree 변환
    public double RadianToDegree(double radian){
        return radian * 180d / Math.PI;
    }

    //마커 찍기 메소드
    private void addMarker(double lat, double lng, String title) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.mapmarker);
        Bitmap icon2 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.rightarrow); //풍선뷰 오른쪽버튼 이미지
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //저장된 위경도를 사용해 포인트 만듦
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);   //마커 이미지 설정
        markerItem.setTMapPoint(tMapPoint);  //위경도 설정
        markerItem.setName(title);    //이름 설정
        markerItem.setCalloutTitle(title);
        markerItem.setCalloutRightButtonImage(icon2);
        tMapView.addMarkerItem("markerItem", markerItem);  //id값을 다르게해 마커를 찍는다.
        markerItem.setCanShowCallout(true);
    }   //addMarker 종료

    private void addPointMarker(double lat, double lng, int i) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.point);
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //저장된 위경도를 사용해 포인트 만듦
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);   //마커 이미지 설정
        markerItem.setTMapPoint(tMapPoint);  //위경도 설정
        tMapView.addMarkerItem("markerItem"+i, markerItem);  //id값을 다르게해 마커를 찍는다.
        markerItem.setCanShowCallout(true);
        i++;
    }   //addMarker 종료
    private void addPointMarkerYellow(double lat, double lng, int i) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pointy);
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //저장된 위경도를 사용해 포인트 만듦
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);   //마커 이미지 설정
        markerItem.setTMapPoint(tMapPoint);  //위경도 설정
        tMapView.addMarkerItem("markerItem"+i, markerItem);  //id값을 다르게해 마커를 찍는다.
        markerItem.setCanShowCallout(true);
        i++;
    }   //addMarker 종료

    public void circle(){ // 3km 반경 원 그리기

        TMapPoint tMapPoint = new TMapPoint(startLat, startLon);

        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint( tMapPoint );
        tMapCircle.setRadius(3000); // 반지름
        tMapCircle.setCircleWidth(2); // 선두께
        tMapCircle.setLineColor(Color.BLACK); // 라인색상
        tMapCircle.setAreaColor(Color.WHITE); // 원 안 색상
        tMapCircle.setAreaAlpha(0); // 원 안 투명도
        tMapView.addTMapCircle("circle1", tMapCircle);

    }


    //거리계산 메소드
    public void getDistance (double a, double b, double c, double d) {
        float results[] = new float[3];
        Location.distanceBetween(a,b,c,d, results);
        System.out.println((int)results[0]);
        meter = (int)results[0];
        //tv.setText(meter+"m");
    }
    //목적지 도착
    private void arrive() {

        arrivetime = System.currentTimeMillis();
        dtime1 = new Date(); // 도착한 시간 추출
        arrive = df.format(dtime1); // 도착한 시간

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


        //변경
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "Record.db", null, 1);

        dbHelper.insert(num, ridingday, now, arrive, m_time, s_time, arrive11, speed);


        Intent okok = new Intent(neartest.this, Ridingrecord.class);
        okok.putExtra("날짜", ridingday);
        okok.putExtra("출발 시간", now);
        okok.putExtra("도착 시간", arrive);
        okok.putExtra("걸린 분", m_time);
        okok.putExtra("걸린 초", s_time);
        okok.putExtra("거리", arrive11);
        okok.putExtra("속도", speed);
        startActivity(okok);
        //테스트할때만 살려놓기

    }

    //location리스너
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            int i = 0;
            if (location != null) {
                startLat = location.getLatitude();
                startLon = location.getLongitude();
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^ 설정완료 " + startLat + ", " + startLon); // 확인용
                tMapView.setLocationPoint(startLon, startLat);     //이 위경도(현위치)에 마크찍기
                tMapView.setCenterPoint(startLon, startLat);       //이 위경도(현위치)를 지도위치가운데로
                //runspeed = location.getSpeed();
                //Log.e("speedddd : ", String.valueOf(runspeed));

                runSpeed = location.getSpeed();
                count++;
                sumSpeed+=runSpeed;
                if(runSpeed==0){
                    count--;
                }

                if (dNum == 0) {
                    //Toast.makeText(getApplicationContext(), "위도 :" + startLat + "경도 :" + startLon + "[" + i + "]", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkNum != 0) {
                        getDistance(startLat, startLon, saveLat[checkNum], saveLon[checkNum]);  //변화된 거리 값
                    }
                    if(meter>=50&&meter<60) //50미터대
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
                        }   //switch문 종료
                    }   //15m이하 if문 종료

                }   //else문 끝
                i++;
            }
        }   //onLocationChanged 끝

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };//location리스너끝

    //현위치탐색함수
    public void setGps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 1, mLocationListener);
        Toast.makeText(getApplicationContext(), "내 위치와 50m정도 차이날 수 있습니다", Toast.LENGTH_LONG).show();
    } //현위치탐색함수끝

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
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
    }   //onActivityResult종료


}