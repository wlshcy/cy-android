package com.shequcun.farm;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.bitmap.cache.ImageCacheManager;
import com.shequcun.farm.ui.fragment.BaseFragment;
import com.shequcun.farm.ui.fragment.FragmentMgrInterface;
import com.shequcun.farm.util.HttpRequestUtil;


public abstract class BaseFragmentActivity extends FragmentActivity implements
        FragmentMgrInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        createImageCache();
    }

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.fragement = selectedFragment;
    }

    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for
     * a Disk based LRU implementation.
     */
    private void createImageCache() {
        ImageCacheManager.getInstance().init(this, "cacheimg",
                DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT,
                DISK_IMAGECACHE_QUALITY);
    }

    @Override
    public void onBackPressed() {
        if (fragement == null || !fragement.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                ImageCacheManager.getInstance().release();
                HttpRequestUtil.release();
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    private int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 30;
    //    private static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so
    protected BaseFragment fragement;
}
