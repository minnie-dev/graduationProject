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
    static double LatSave, LonSave; //검색한 장소들 위경도 저장할 변수
    static String searchDestination;    //검색한 장소를 저장할 변수
    static String urlSave;  //파싱할 url저장하는 변수
    static String poiIdSave;    //목적지의 poi id를 저장하는 변수
    static String endEncodeResult; //도착장소를 utf-8기반으로 인코딩해서 저장하는 변수
    static String destination;  //목적지이름을 저장하는 변수
    static double destinationLat, destinationLon;   //목적지로 선택한 장소의 위경도를 저장할 변수
    static double startLat = 0, startLon = 0;   //출발지로 선택한 장소의 위경도를 저장할 변수 (실시간 위치 저장)
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
    static int dNum = 0;  //  위치확인 버튼 눌렸나 확인하는 변수 1이면 눌리고 0이면 안 눌림
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
    SimpleDateFormat df = new SimpleDateFormat("hh시 mm분 ss초");
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

    //day1과 day 변경
    Date day1;
    SimpleDateFormat day = new SimpleDateFormat("yyyy년 M월 dd일");
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

        ImageButton btnOpenDrawer = (ImageButton) findViewById(R.id.btn_mainmenu); // 드로어 화면을 열 버튼 객체 참조
        destinationEd = (EditText) findViewById(R.id.input_destination);
        ImageButton destinationSearchBtn = (ImageButton) findViewById(R.id.btn_search);

        dName = (TextView) findViewById(R.id.destinationName);
        ImageButton destiBtn = (ImageButton) findViewById(R.id.destinationBtn); //목적지설정
        dName2 = (TextView) findViewById(R.id.destinationName2);
        final ImageButton naviBtn = (ImageButton) findViewById(R.id.naviBtn);   //안내시작
        tv = tv = (TextView) findViewById(R.id.distance);

        //Button bluetoothBtn = (Button) findViewById(R.id.btn_bluetoothBtn);
        ImageButton currentBtn = (ImageButton) findViewById(R.id.current);

        backKeyClickHandler = new BackpressCloseHandler(this); //뒤로가기버튼

        //드로어메뉴 버튼들
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout); // 전체화면인 DrawerLayout 객체 참조
        final View drawerView = (View) findViewById(R.id.drawer); // Drawer 화면(뷰) 객체 참조
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

        //*****************************GPS (현재위치)
        tMapView.setCompassMode(true);
        tMapView.setSightVisible(true);
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
        //블루투스로 데이터 수신
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();  //확인용
                checkNum = Integer.parseInt(message);
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

        //********************* 버튼 클릭이벤트
        //메인메뉴버튼(드로어 열기)
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });//메인메뉴버튼끝

        //(드로어)길찾기 버튼(드로어닫기 - 메인화면으로)
        findwayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawerView);
            }
        });//(드로어)길찾기버튼끝
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
                Toast.makeText(MainActivity.this, "경고음이 꺼집니다.", Toast.LENGTH_SHORT).show();
                //mTextMsg.append("안드 --> 아두"+"\n");
                //mTextMsg.append("STOP"+"\n");
                System.out.println("안드 --> 아두");
            }
        });

        togglebtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                Toast.makeText(MainActivity.this, "보안모드가 설정됩니다.", Toast.LENGTH_SHORT).show();
                bt.send("Y", true);
                toggletext.setText("보안모드 ON");
                //mTextMsg.append("토글 ON"+"\n");
                //mTextMsg.append("Y"+"\n");
                System.out.println("토글 ON");
                stoplayout.setVisibility(View.VISIBLE);
            }
            else{
                Toast.makeText(MainActivity.this, "보안모드가 해제됩니다.", Toast.LENGTH_SHORT).show();
                bt.send("J", true);
                toggletext.setText("보안모드 OFF");
                //mTextMsg.append("토글 OFF"+"\n");
                //mTextMsg.append("N"+"\n");
                System.out.println("토글 OFF");
                stoplayout.setVisibility(View.GONE);
            }
        }
        });

        //목적지검색버튼
        destinationSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pointNumber = 0;
                searchDestination = destinationEd.getText().toString();  //텍스트에디트에 입력한 문자를 searchDestination에 저장
                destinationEd.setText("");
                DestinationData data = new DestinationData(searchDestination, startLat, startLon);
                // Intent에 Data객체 저장
                Intent searchIntent = new Intent(MainActivity.this, DestinationList.class);
                searchIntent.putExtra("Ddata", data);
                // DestinationList클래스로 Activity 전환
                startActivityForResult(searchIntent, 0);
            }
        }); //목적지검색버튼 (destinationSearchBtn) 종료

        //목적지 설정 버튼 (폴리라인 버튼 + url저장됨)
        destiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naviFrameLayout.setVisibility(View.VISIBLE);
                setFrameLayout.setVisibility(View.INVISIBLE);
                tMapPointStart = new TMapPoint(startLat, startLon);//출발지 설정
                tMapPointEnd = new TMapPoint(destinationLat, destinationLon); //목적지 설정
                //Toast.makeText(MainActivity.this, "위도 : "+destinationLat+", id값 : "+poiIdSave+"이름 : "+poi.item.getPOIName(), Toast.LENGTH_LONG).show(); //확인용
                Toast.makeText(MainActivity.this, "목적지 설정완료", Toast.LENGTH_LONG).show();
                try {
                    endEncodeResult = URLEncoder.encode(destination, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("!!!!!!!!!!!!!! 결과 : " + endEncodeResult);
                urlSave = "https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&callback=&appKey=86d8cbb8-7ee4-4d00-8d52-ca55321ec1e9&passList=&angle=&speed=&reqCoordType=WGS84GEO&searchOption=0&resCoordType=WGS84GEO&sort=&gpsTime=15000"
                        + "&startX=" + startLon + "&startY=" + startLat +
                        "&endPoiId=" + poiIdSave + "&endX=" + destinationLon + "&endY=" + destinationLat +
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
                });
            }
        }); //목적지 설정버튼 종료

        //안내시작 버튼(포인트 지점 + 거리 계산)
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
                tMapView.setCompassMode(true); //나침반기능설정
                tMapView.setCenterPoint(startLon, startLat);       //이 위경도(현위치)를 지도위치가운데로
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                nowtime = System.currentTimeMillis(); // 걸린시간
                htime1 = new Date(); // 출발한 시간 추출
                now = df.format(htime1); // 출발한 시간
                day1 = new Date();//변경
                ridingday = day.format(day1); // 변경, 날짜
                count = 0;
                a = 1;
               // arrive(); // 도착 함수      //테스트할때만 살려놓기

                //naviBtn.setVisibility(View.INVISIBLE);
                naviFrameLayout.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "파싱 확인", Toast.LENGTH_SHORT).show();
                MyAsyncTask mProcessTask = new MyAsyncTask();
                mProcessTask.execute();
            }
        });

        //블루투스 버튼
        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //블루투스 설정 코드
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }


            }
        }); //bluetoothBtn 종료


        //현재위치 버튼
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
                Toast.makeText(getApplicationContext(), "내 위치와 50m정도 차이날 수 있습니다", Toast.LENGTH_LONG).show();
                // locationManager.requestLocationUpdates();
                //setGps(); //현위치탐색

            }
        });


        //안내시작 버튼(: 포인트 지점과의 거리측정가 됨)

    }//onCreate끝


    @Override
    public void onLocationChange(Location location) {
        startLat = location.getLatitude();
        startLon = location.getLongitude();
    }

    //AsyncTask 생성 - 모든 네트워크 로직을 여기서 작성해 준다.
    //AsyncTask 실행순서 : onPreExecute -> doInBackground -> onPostExecute
    public class MyAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //OkHttpClient client = new OkHttpClient();

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

            getDistance(startLat, startLon, saveLat[checkNum], saveLon[checkNum]); //처음거리 값

            Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_SHORT).show();

        }   //onPostExecute 종료

    }   //MyAsyncTask 종료

    //******************************** 메소드 정의 *******************************************
    //지도 이동 메소드
    private void moveMap(double lat, double lng) {
        tMapView.setCenterPoint(lng, lat);
    }

    //거리계산 메소드
    public void getDistance(double a, double b, double c, double d) {
        float results[] = new float[3];
        Location.distanceBetween(a, b, c, d, results);
        System.out.println((int) results[0]);
        meter = (int) results[0];
        tv.setText(meter + "m");
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
        tMapView.addMarkerItem("markerItem" + i, markerItem);  //id값을 다르게해 마커를 찍는다.
        markerItem.setCanShowCallout(true);
        i++;
    }   //addMarker 종료

    private void addPointMarkerYellow(double lat, double lng, int i) {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pointy);
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //저장된 위경도를 사용해 포인트 만듦
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);   //마커 이미지 설정
        markerItem.setTMapPoint(tMapPoint);  //위경도 설정
        tMapView.addMarkerItem("markerItem" + i, markerItem);  //id값을 다르게해 마커를 찍는다.
        markerItem.setCanShowCallout(true);
        i++;
    }   //addMarker 종료

    private void addCircle(double lat, double lng, int i) {
        TMapPoint tMapPoint = new TMapPoint(lat, lng);   //저장된 위경도를 사용해 포인트 만듦
        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint(tMapPoint);
        tMapCircle.setRadius(50);
        tMapCircle.setCircleWidth(7);
        tMapCircle.setLineColor(Color.GRAY);
        tMapCircle.setAreaColor(Color.BLUE);
        tMapCircle.setAreaAlpha(300);
        tMapView.addTMapCircle("circle" + i, tMapCircle);
        i++;
    }   //addCircle 종료


    //목적지 도착
    private void arrive() {

        arrivetime = System.currentTimeMillis();
        dtime1 = new Date(); // 도착한 시간 추출
        arrive = df.format(dtime1); // 도착한 시간

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


        //변경
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "Record.db", null, 1);

        dbHelper.insert(num, ridingday, now, arrive, m_time, s_time, arrive11, speed);


        Intent okok = new Intent(MainActivity.this, Ridingrecord.class);
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
    }   //onActivityResult종료

}