package romain.oliva.channelmessaging.images;

import android.graphics.Bitmap;

public interface OnDownloadImage {
    public void onError(String error);
    public void onCompleted(int requestCode, Bitmap response);
}
