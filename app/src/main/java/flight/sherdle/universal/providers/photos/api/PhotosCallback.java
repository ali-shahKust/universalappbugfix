package flight.sherdle.universal.providers.photos.api;

import flight.sherdle.universal.providers.photos.PhotoItem;

import java.util.ArrayList;

public interface PhotosCallback {

    void completed(ArrayList<PhotoItem> photos, boolean canLoadMore);
    void failed();
}
