package com.flight.travelmyswiftpay.providers.photos.api;

import com.flight.travelmyswiftpay.providers.photos.PhotoItem;

import java.util.ArrayList;

public interface PhotosCallback {

    void completed(ArrayList<PhotoItem> photos, boolean canLoadMore);
    void failed();
}
