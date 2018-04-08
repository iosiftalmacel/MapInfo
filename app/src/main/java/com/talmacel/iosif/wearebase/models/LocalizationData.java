package com.talmacel.iosif.wearebase.models;

import java.io.Serializable;

/**
 * Created by Iosif on 18/03/2018.
 */

public class LocalizationData implements Serializable {
    public String city;
    public String organization;
    public String street_adress;
    public String state;
    public String country;
    public String zip;
    public LatLngData latlng;
}
