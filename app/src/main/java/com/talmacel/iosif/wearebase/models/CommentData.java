package com.talmacel.iosif.wearebase.models;

import java.io.Serializable;

/**
 * Created by Iosif on 18/03/2018.
 */

public class CommentData implements Serializable {
    public String author;
    public String gravatar;
    public long date;
    public String content;
    public String openclosed;
    public int rating;
}