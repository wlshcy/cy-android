package com.shequcun.farm;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.shequcun.farm.ui.fragment.BaseFragment;
import com.shequcun.farm.ui.fragment.FragmentMgrInterface;
import com.shequcun.farm.util.HttpRequestUtil;
import com.umeng.analytics.MobclickAgent;


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
        if (!ImageLoader.getInstance().isInited())
            initImageLoader(this);
    }

    @Override
    public void onBackPressed() {
        if (fragement == null || !fragement.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//                ImageCacheManager.getInstance().release();
                HttpRequestUtil.release();
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    void initImageLoader(Context context) {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_place_holder)
                .showImageOnFail(R.drawable.ic_loading_failure)
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true).considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
//                .memoryCacheExtraOptions(400, 400)
                        // default = device screen dimensions
//                .diskCacheExtraOptions(400, 400, null)
                .threadPoolSize(5)
                        // default Thread.NORM_PRIORITY - 1
                .threadPriority(Thread.NORM_PRIORITY)
                        // default FIFO
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                        // default
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                        // default
                .imageDownloader(new BaseImageDownloader(context))
                        // default
                .imageDecoder(new BaseImageDecoder(false))
                        // default
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        // default
                .defaultDisplayImageOptions(imageOptions).build();

        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //umeng统计时长
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //umeng统计时长
        MobclickAgent.onPause(this);
    }

    protected BaseFragment fragement;
}
