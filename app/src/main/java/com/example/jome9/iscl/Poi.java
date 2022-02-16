package com.example.jome9.iscl;

/**
 * Created by jome9 on 2019-04-09.
 */


import com.skt.Tmap.TMapPOIItem;

public class Poi {
    TMapPOIItem item;

    public Poi(TMapPOIItem item){
        this.item = item;
    }
    @Override
    public String toString() {
        return item.getPOIName();
    }

}

