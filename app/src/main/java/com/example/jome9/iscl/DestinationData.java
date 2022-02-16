package com.example.jome9.iscl;

/**
 * Created by jome9 on 2019-04-12.
 */

import java.io.Serializable;

public class DestinationData implements Serializable {
    String data;
    double lat;
    double lon;

    public DestinationData(String data,double lat, double lon) {
        this.data = data;
        this.lat = lat;
        this.lon = lon;
    }
}