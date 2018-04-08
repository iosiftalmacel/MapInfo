package com.talmacel.iosif.wearebase.models;

import java.io.Serializable;

/**
 * Created by Iosif on 18/03/2018.
 */

public class BaseData implements Serializable {
    public String id;
    public String name;
    public long date;
    public int open;
    public int closed;
    public float rating;
    public String description;
    public String type;
    public String status;
    public LocalizationData loc;
    public UrlData[] urls;
    public int number_of_devices;
    public Object brands_available;
    public DonationData donations;
    public CommentData[] comments;
}
