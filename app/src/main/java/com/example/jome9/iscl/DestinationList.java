package com.example.jome9.iscl;

/**
 * Created by jome9 on 2019-04-12.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.io.InputStreamReader;
import java.util.ArrayList;


public class DestinationList extends AppCompatActivity {
    TMapData tmapdata;
    TMapPoint tMapPointStart;
    TMapView tMapView;
    static TMapPOIItem item;
    static double LatSave, LonSave;
    static String poiIdSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinationlist);

        TextView t = (TextView)findViewById(R.id.t);
        final ListView listV = (ListView)findViewById(R.id.list);

        // Data 객체를 받을 Intent 생성
        final Intent searchIntent = getIntent();
        DestinationData data = (DestinationData) searchIntent.getSerializableExtra("Ddata");
        tMapPointStart = new TMapPoint(data.lat, data.lon);
        tmapdata = new TMapData();
        tMapView = new TMapView(this);
        final ArrayList<Poi> poiList = new ArrayList<Poi>();
        final ArrayAdapter<Poi> adapter = new ArrayAdapter<Poi>(this, android.R.layout.simple_list_item_1, poiList);  //배열로부터 데이터 가져오는 역할
        listV.setAdapter(adapter); //어댑터 연결
        ImageButton btn_back = (ImageButton) findViewById(R.id.back_btn);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //searchDestination에 저장된 문자가 포함된 반경 5km 장소를 50개만 표시
        tmapdata.findAroundKeywordPOI(tMapPointStart, data.data, 33, 200, new TMapData.FindAroundKeywordPOIListenerCallback() {
            @Override
            public void onFindAroundKeywordPOI(final ArrayList<TMapPOIItem> poiItem) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        poiList.clear();
                        //poiItem의 객체를 꺼내서 TMapPOIItem item에 넣음
                        for (int i = 0; i < poiItem.size(); i++) {
                            item = poiItem.get(i);
                            Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                                    "Address: " + item.getPOIAddress().replace("null", "" + "Point : " + item.getPOIPoint().toString())+" >>"+i);    //확인용
                            adapter.add(new Poi(item));

                        }   //for문 종료

                    }   //run()종료
                });

            }

        }); //티맵 findaroundpoi 리스너 종료

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Poi poi = (Poi) listV.getItemAtPosition(position);
                MarkerInfo info = new MarkerInfo(poi.item.getPOIPoint().getLatitude(), poi.item.getPOIPoint().getLongitude(), poi.item.getPOIID(), poi.item.getPOIName());
                System.out.println("lat : "+info.latSave+" LON : "+info.lonSave+ ", id : "+info.poiIdSave+info.poiNameSave);
                Intent markerIntent = new Intent(getApplicationContext(), MainActivity.class);
                markerIntent.putExtra("info", info);
                setResult(2, markerIntent);
                finish();
                //startActivity(markerIntent);

            }
        });
    }   //onCreate종료

    //***********************************메소드 정의

}   //클래스 종료
