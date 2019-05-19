package com.gama.farm_fun;

import android.net.Uri;

public class PreUploadPic {
    public int number;
    public Uri uri = null;

    public PreUploadPic(int number) {
        this.number = number;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
