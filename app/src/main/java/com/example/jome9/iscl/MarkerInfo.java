package com.example.jome9.iscl;

import java.io.Serializable;

/**
 * Created by jome9 on 2019-04-12.
 */

public class MarkerInfo implements Serializable {
    double latSave, lonSave;
    String poiIdSave, poiNameSave;
    public MarkerInfo(double latSave, double lonSave, String poiIdSave, String poiNameSave) {
        this.latSave = latSave;
        this.lonSave = lonSave;
        this.poiIdSave = poiIdSave;
        this.poiNameSave = poiNameSave;
    }
}