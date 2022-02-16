package com.example.jome9.iscl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

public class TransferTm extends AppCompatActivity {
    static double tmLat=0, tmLon=0;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Geo2Tm(tmLon, tmLat);
    }

    // WGS84를 tm좌표로 변환하는 메소드
    public static void Geo2Tm(double plon, double plat) {
        double lon, lat;
        //lon = plon;
        //lat = plat;
        lon = plon * PI / 180;
        lat = plat * PI / 180;
        double m_arScaleFactor = 1;
        double m_arLonCenter = 2.21661859489632;
        double m_arLatCenter = 0.663225115757845;
        double m_arFalseNorthing = 500000.0;
        double m_arFalseEasting = 200000.0;
        double x, y;
        double m_dDstInd = 0.0;
        double m_arMajor = 6378137.0;
        double m_arMinor = 6356752.3142;
        double delta_lon; // Delta longitude (Given longitude - center longitude)
        double sin_phi, cos_phi; // sin and cos value
        double al, als; // temporary values
        double b, c, t, tq; // temporary values
        double con, n, ml; // cone constant, small m
        double temp = m_arMinor / m_arMajor;
        double m_dSrcEs = 1.0 - temp * temp;
        double m_dDstEs = 1.0 - temp * temp;
        double m_dDstEsp = m_dDstEs / (1.0 - m_dDstEs);
        double m_dDstE0 = 1.0 - 0.25 * m_dDstEs * (1.0 + m_dDstEs / 16.0 * (3.0 + 1.25 * m_dDstEs));
        double m_dDstE1 = 0.375 * m_dDstEs * (1.0 + 0.25 * m_dDstEs * (1.0 + 0.46875 * m_dDstEs));
        double m_dDstE2 = 0.05859375 * m_dDstEs * m_dDstEs * (1.0 + 0.75 * m_dDstEs);
        double m_dDstE3 = m_dDstEs * m_dDstEs * m_dDstEs * (35.0 / 3072.0);
        double m_dDstMl0 = m_arMajor * (m_dDstE0 * m_arLatCenter - m_dDstE1 * sin(2.0 * m_arLatCenter) + m_dDstE2 * sin(4.0 * m_arLatCenter) - m_dDstE3 * sin(6.0 * m_arLatCenter));
        //m_dDstInd = 0.0;
        delta_lon = lon - m_arLonCenter;
        sin_phi = sin(lat);
        cos_phi = cos(lat);
        b = 0;
        x = 0.5 * m_arMajor * m_arScaleFactor * log((1.0 + b) / (1.0 - b));
        con = acos(cos_phi * cos(delta_lon) / sqrt(1.0 - b * b));
        al = cos_phi * delta_lon;
        als = al * al;
        c = m_dDstEsp * cos_phi * cos_phi;
        tq = tan(lat);
        t = tq * tq;
        con = 1.0 - m_dDstEs * sin_phi * sin_phi;
        n = m_arMajor / sqrt(con);
        ml = m_arMajor * (m_dDstE0 * lat - m_dDstE1 * sin(2.0 * lat) + m_dDstE2 * sin(4.0 * lat) - m_dDstE3 * sin(6.0 * lat));
        x = m_arScaleFactor * n * al * (1.0 + als / 6.0 * (1.0 - t + c + als / 20.0 * (5.0 - 18.0 * t + t * t + 72.0 * c - 58.0 * m_dDstEsp))) + m_arFalseEasting;
        y = m_arScaleFactor * (ml - m_dDstMl0 + n * tq * (als * (0.5 + als / 24.0 * (5.0 - t + 9.0 * c + 4.0 * c * c + als / 30.0 * (61.0 - 58.0 * t + t * t + 600.0 * c - 330.0 * m_dDstEsp))))) + m_arFalseNorthing;
        tmLon = x;
        tmLat = y;
    }
}