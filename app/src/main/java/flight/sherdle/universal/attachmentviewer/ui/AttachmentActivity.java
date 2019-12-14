package flight.sherdle.universal.attachmentviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import flight.sherdle.universal.R;
import flight.sherdle.universal.attachmentviewer.loader.DefaultAudioLoader;
import flight.sherdle.universal.attachmentviewer.loader.DefaultFileLoader;
import flight.sherdle.universal.attachmentviewer.loader.DefaultVideoLoader;
import flight.sherdle.universal.attachmentviewer.loader.MediaLoader;
import flight.sherdle.universal.attachmentviewer.loader.PicassoImageLoader;
import flight.sherdle.universal.attachmentviewer.model.Attachment;
import flight.sherdle.universal.attachmentviewer.model.MediaAttachment;
import flight.sherdle.universal.attachmentviewer.widgets.ScrollGalleryView;
import flight.sherdle.universal.util.Helper;
import flight.sherdle.universal.util.ThemeUtils;

/**
 * This file is part of the Universal template
 * For license information, please check the LICENSE
 * file in the root of this project
 *
 * @author Sherdle
 * Copyright 2018
 */

public class AttachmentActivity extends AppCompatActivity {

    private ScrollGalleryView scrollGalleryView;
    private List<MediaLoader> mediaList;

    public static String IMAGES = "images";
    public static String INDEX = "index";

    public static void startActivity(Context source, MediaAttachment image){
        Intent intent = new Intent(source, AttachmentActivity.class);
        intent.putExtra(IMAGES, new ArrayList<>(Collections.singleton(image)));
        source.startActivity(intent);
    }

    public static void startActivity(Context source, ArrayList<MediaAttachment> images){
        Intent intent = new Intent(source, AttachmentActivity.class);
        intent.putExtra(IMAGES, images);
        source.startActivity(intent);
    }

    public static void startActivity(Context source, ArrayList<MediaAttachment> images, int defaultPosition){
        Intent intent = new Intent(source, AttachmentActivity.class);
        intent.putExtra(IMAGES, images);
        intent.putExtra(INDEX, defaultPosition);
        source.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        setContentView(R.layout.activity_attachment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Helper.setStatusBarColor(this, R.color.black);

        ArrayList<Attachment> images = (ArrayList<Attachment>) getIntent().getSerializableExtra(IMAGES);
        int defaultIndex = getIntent().getIntExtra(INDEX, 0);
        scrollGalleryView = findViewById(R.id.scroll_gallery_view);

        Helper.admobLoader(this, findViewById(R.id.adView));

        mediaList = easyInitView(scrollGalleryView, images, getSupportFragmentManager());
        scrollGalleryView.setCurrentItem(defaultIndex);
    }

    public static List<MediaLoader> easyInitView(ScrollGalleryView view, ArrayList<Attachment> images, FragmentManager fm){

        List<MediaLoader> infos = new ArrayList<>(images.size());
        for (Attachment attachment : images) {
            if (attachment instanceof MediaAttachment) {
                MediaAttachment mediaAttachment = ((MediaAttachment) attachment);
                if (mediaAttachment.getMime().contains(MediaAttachment.MIME_PATTERN_IMAGE))
                    infos.add(new PicassoImageLoader(mediaAttachment));
                else if (mediaAttachment.getMime().contains(MediaAttachment.MIME_PATTERN_VID))
                    infos.add(new DefaultVideoLoader(mediaAttachment));
                else if (mediaAttachment.getMime().contains(MediaAttachment.MIME_PATTERN_AUDIO))
                    infos.add(new DefaultAudioLoader(mediaAttachment));
                else
                    infos.add(new DefaultFileLoader(mediaAttachment));
            }
        }

        view.setThumbnailSize((int) view.getContext().getResources().getDimension(R.dimen.thumbnail_height))
                .setZoom(true)
                .setFragmentManager(fm)
                .addMedia(infos);

        if (infos.size() == 1){
            view.hideThumbnails(true);
        }

        return infos;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
