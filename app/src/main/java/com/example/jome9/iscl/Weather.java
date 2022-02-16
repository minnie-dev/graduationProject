package com.example.jome9.iscl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skt.Tmap.TMapAddressInfo;
import com.skt.Tmap.TMapData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.example.jome9.iscl.MainActivity.startLat;
import static com.example.jome9.iscl.MainActivity.startLon;
import static com.example.jome9.iscl.TransferTm.tmLat;
import static com.example.jome9.iscl.TransferTm.tmLon;

public class Weather extends AppCompatActivity {
    TMapData tmapdata;
    static String locationName;
    String weatherURL;
    TextView temp;
    TextView tempMin, tempMax;
    ImageView state, oneW, twoW, threeW, fourW, fiveW, sixW, sevenW, eightW, nineW, tenW, elevenW, twelveW;
    TextView location; TextView Estate;
    TextView oneHour, oneTemp, twoHour, twoTemp, threeHour, threeTemp, fourHour, fourTemp, fiveHour, fiveTemp, sixHour, sixTemp, sevenHour, sevenTemp,
            eightHour, eightTemp, nineHour, nineTemp, tenHour, tenTemp, elevenHour,elevenTemp, twelveHour,twelveTemp;
    String str, weather;
    String APIKey = "c0168e9bdecd7430c214eb1dfa5ca9da";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ImageButton btnOpenDrawer = (ImageButton) findViewById(R.id.btn_mainmenu); // 드로어 화면을 열 버튼 객체 참조
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout); // 전체화면인 DrawerLayout 객체 참조
        final View drawerView = (View) findViewById(R.id.drawer); // Drawer 화면(뷰) 객체 참조
        LinearLayout logininfoBtn = (LinearLayout) findViewById(R.id.btn_logininfo);
        TextView logininformationText = (TextView) findViewById(R.id.logininformation);
        LinearLayout findwayBtn = (LinearLayout) findViewById(R.id.btn_findway);
        LinearLayout findbicycleBtn = (LinearLayout) findViewById(R.id.btn_findbicycle);
        LinearLayout weatherBtn = (LinearLayout) findViewById(R.id.btn_weather);
        LinearLayout ridingrecordBtn = (LinearLayout) findViewById(R.id.btn_ridingrecord);
        LinearLayout keepbicycleBtn = (LinearLayout) findViewById(R.id.btn_keepbicycle);
        LinearLayout bluetoothBtn = (LinearLayout) findViewById(R.id.btn_bluetoothBtn);
        LinearLayout helpBtn = (LinearLayout) findViewById(R.id.btn_help);
        ImageButton helpbtn = (ImageButton) findViewById(R.id.helpbtn);
        location = (TextView) findViewById(R.id.Location); Estate = (TextView)findViewById(R.id.stateExplain);
        temp = (TextView) findViewById(R.id.temperature);
        tempMin = (TextView) findViewById(R.id.temperatureMin);tempMax = (TextView) findViewById(R.id.temperatureMax);
        state = (ImageView) findViewById(R.id.state);oneW=(ImageView)findViewById(R.id.oneWeather);twoW=(ImageView)findViewById(R.id.twoWeather);threeW=(ImageView)findViewById(R.id.threeWeather);fourW=(ImageView)findViewById(R.id.fourWeather);fiveW=(ImageView)findViewById(R.id.fiveWeather);sixW=(ImageView)findViewById(R.id.sixWeather);
        sevenW=(ImageView)findViewById(R.id.sevenWeather); eightW=(ImageView)findViewById(R.id.eightWeather);nineW=(ImageView)findViewById(R.id.nineWeather);tenW=(ImageView)findViewById(R.id.tenWeather);elevenW=(ImageView)findViewById(R.id.elevenWeather);twelveW=(ImageView)findViewById(R.id.twelveWeather);
        oneHour=(TextView)findViewById(R.id.oneHour);oneTemp=(TextView)findViewById(R.id.oneTemp);
        twoHour=(TextView)findViewById(R.id.twoHour);twoTemp=(TextView)findViewById(R.id.twoTemp);
        threeHour=(TextView)findViewById(R.id.threeHour);threeTemp=(TextView)findViewById(R.id.threeTemp);
        fourHour=(TextView)findViewById(R.id.fourHour);fourTemp=(TextView)findViewById(R.id.fourTemp);
        fiveHour=(TextView)findViewById(R.id.fiveHour);fiveTemp=(TextView)findViewById(R.id.fiveTemp);
        sixHour=(TextView)findViewById(R.id.sixHour);sixTemp=(TextView)findViewById(R.id.sixTemp);
        sevenHour = (TextView)findViewById(R.id.sevenHour);sevenTemp=(TextView)findViewById(R.id.sevenTemp);
        eightHour=(TextView)findViewById(R.id.eightHour);eightTemp=(TextView)findViewById(R.id.eightTemp);
        nineHour=(TextView)findViewById(R.id.nineHour);nineTemp=(TextView)findViewById(R.id.nineTemp);
        tenHour = (TextView)findViewById(R.id.tenHour);tenTemp=(TextView)findViewById(R.id.tenTemp);
        elevenHour=(TextView)findViewById(R.id.elevenHour); elevenTemp=(TextView)findViewById(R.id.elevenTemp);
        twelveHour=(TextView)findViewById(R.id.twelveHour); twelveTemp=(TextView)findViewById(R.id.twelveTemp);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        logininformationText.setText(userID);

        tmapdata = new TMapData();
        WeatherAsyncTask wProcessTask = new WeatherAsyncTask();

        tmapdata.reverseGeocoding(startLat, startLon, "A02", new TMapData.reverseGeocodingListenerCallback() {
            @Override
            public void onReverseGeocoding(TMapAddressInfo addressInfo) {
                locationName = addressInfo.strGu_gun;
            }
        });
        TransferTm.Geo2Tm(startLon, startLat);
        System.out.println("################## tm으로 변환된 lon : "+tmLon+" lat : "+tmLat);  //확인용

        //long getUnixTime = System.currentTimeMillis() / 1000;
        weatherURL = "https://api.darksky.net/forecast/" + APIKey +"/" + startLat + "," +startLon+"?lang=ko";
        System.out.println(weatherURL); //확인용

        wProcessTask.execute();

        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), help_weather.class);
                startActivity(intent);
            }
        });
        //메인메뉴버튼(드로어 열기)
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });//메인메뉴버튼끝

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
                Intent intent = new Intent(getApplicationContext(), Findbicycle.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        weatherBtn.setOnClickListener(new View.OnClickListener() {
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

    }   //onCreate 종료

    //AsyncTask 실행순서 : onPreExecute -> doInBackground -> onPostExecute
    public class WeatherAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        //doInBackground( ) 메소드에서 작업이 끝나면 onPostExcuted( ) 로 결과 파라미터를 리턴
        protected String doInBackground(String... datas) {
            try {
                URL url = new URL(weatherURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    weather = buffer.toString();
                    //Log.i("weather : ", weather); //확인용
                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return weather;
        }   //doinbackground 종료

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            JsonParser Parser = new JsonParser();
            JsonObject cJsonObj = (JsonObject) Parser.parse(weather);
            JsonObject currentObject = (JsonObject) cJsonObj.get("currently");   //메인은 중괄호로만 이뤄져있어서 오브젝트만 사용함

            //-- 현재 날씨 파싱
            String tempSave = String.valueOf(changeCelsius(currentObject.get("temperature").getAsFloat()));
            temp.setText(tempSave+"°");
            String stateSave = currentObject.get("icon").toString();
            stateSave = removeQuotes(stateSave);
            matchingPNG(stateSave, state);
            Estate.setText(currentObject.get("summary").toString());
            location.setText(locationName);
            JsonObject dailyObject = (JsonObject) cJsonObj.get("daily");  //daily object 안에 data array 요소 중 최저,최고,일몰,일출 시간을 사용
            JsonArray dailyDataArray = (JsonArray) dailyObject.get("data");
            JsonElement dailyElement = dailyDataArray.get(0);
            String temperatureMax = String.valueOf(changeCelsius(dailyElement.getAsJsonObject().get("temperatureHigh").getAsFloat()));
            String temperatureMin = String.valueOf(changeCelsius(dailyElement.getAsJsonObject().get("temperatureLow").getAsFloat()));
            tempMin.setText("최저 "+temperatureMin+"°");
            tempMax.setText("최고 "+temperatureMax+"°");
            //-- 1시간 날씨 파싱
            JsonObject hourlyObject = (JsonObject) cJsonObj.get("hourly");  //hourly object 안에 data array가 있다.
            JsonArray dataArray = (JsonArray) hourlyObject.get("data");
            for(int i=0;i<15;i++) {
                JsonElement hourWeatherElement = dataArray.get(i); //중괄호가 총 49개니까
                String icon = hourWeatherElement.getAsJsonObject().get("icon").getAsString();
                String temperature = String.valueOf(changeCelsius(hourWeatherElement.getAsJsonObject().get("temperature").getAsFloat()));
                String time = changeUNIXTime(hourWeatherElement.getAsJsonObject().get("time").getAsString());
                System.out.println(i+"번. "+time.substring(8,10)+"일 "+time.substring(11,13)+"시 "+temperature+" "+icon);

                switch (i) {
                    case 0 :
                        oneHour.setText(time.substring(11,13)+"시");
                        oneTemp.setText(temperature+"°");
                        matchingPNG(icon, oneW);
                        break;
                    case 1 :
                        twoHour.setText(time.substring(11,13)+"시");
                        twoTemp.setText(temperature+"°");
                        matchingPNG(icon, twoW);
                        break;
                    case 2 :
                        threeHour.setText(time.substring(11,13)+"시");
                        threeTemp.setText(temperature+"°");
                        matchingPNG(icon, threeW);
                        break;
                    case 3 :
                        fourHour.setText(time.substring(11,13)+"시");
                        fourTemp.setText(temperature+"°");
                        matchingPNG(icon, fourW);
                        break;
                    case 4 :
                        fiveHour.setText(time.substring(11,13)+"시");
                        fiveTemp.setText(temperature+"°");
                        matchingPNG(icon, fiveW);
                        break;
                    case 5 :
                        sixHour.setText(time.substring(11,13)+"시");
                        sixTemp.setText(temperature+"°");
                        matchingPNG(icon, sixW);
                        break;
                    case 6 :
                        sevenHour.setText(time.substring(11,13)+"시");
                        sevenTemp.setText(temperature+"°");
                        matchingPNG(icon, sevenW);
                        break;
                    case 7 :
                        eightHour.setText(time.substring(11,13)+"시");
                        eightTemp.setText(temperature+"°");
                        matchingPNG(icon, eightW);
                        break;
                    case 8 :
                        nineHour.setText(time.substring(11,13)+"시");
                        nineTemp.setText(temperature+"°");
                        matchingPNG(icon, nineW);
                        break;
                    case 9 :
                        tenHour.setText(time.substring(11,13)+"시");
                        tenTemp.setText(temperature+"°");
                        matchingPNG(icon, tenW);
                        break;
                    case 10 :
                        elevenHour.setText(time.substring(11,13)+"시");
                        elevenTemp.setText(temperature+"°");
                        matchingPNG(icon, elevenW);
                        break;
                    case 11 :
                        twelveHour.setText(time.substring(11,13)+"시");
                        twelveTemp.setText(temperature+"°");
                        matchingPNG(icon, twelveW);
                        break;
                }//switch문 종료

            }//for문 종료

        }   //onPostExecute 종료
    }   //WeatherAsyncTask 종료

    void matchingPNG(String weatherState, ImageView imgV) {
        switch (weatherState) {
            case "cloudy" :
            case "partly-cloudy-day" :
            case "partly-cloudy-night" :
                imgV.setImageResource(R.drawable.clouds);
                break;
            case "clear-day" :
            case "clear-night" :
                imgV.setImageResource(R.drawable.clear);
                break;
            case "rain" :
                imgV.setImageResource(R.drawable.rain);
                break;
            case "thunderstorm":
                imgV.setImageResource(R.drawable.thunderstorm);
                break;
            case "snow":
            case "sleet":
                imgV.setImageResource(R.drawable.snow);
                break;
            case "tornado":
                imgV.setImageResource(R.drawable.tornado);
                break;
            case "fog":
                imgV.setImageResource(R.drawable.fog);
                break;
            case "hail":
                //state.setImageResource(R.drawable.dust);
                break;
        }
    }//matchingPNG 종료

    int changeCelsius(float fahrenheit) {
        int celsius = (int) Math.floor((fahrenheit-32.0)*5/9);
        return  celsius;
    }
    String removeQuotes(String name) {
        int length = name.length();
        name = name.substring(1,length-1);
        return name;
    }
    String changeUNIXTime(String time) {
        long unixTime =  Integer.parseInt(time) * 1000L;
        Date date = new Date(unixTime);
        time = String.valueOf(date);
        return time;
    }

    //두번누르면 종료
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
            Toast.makeText(getApplicationContext(), "뒤로가기버튼을 한번 더 누르면 메인화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}   //weather클래스 종료

