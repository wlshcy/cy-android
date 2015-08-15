package com.bitmap.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;

public class ImageCacheManager implements ImageCache {

    /**
     * Volley recommends in-memory L1 cache but both a disk and memory cache are
     * provided. Volley includes a L2 disk cache out of the box but you can
     * technically use a disk cache as an L1 cache provided you can live with
     * potential i/o blocking.
     */

    private static ImageCacheManager mInstance;

    /**
     * Volley image loader
     */
    private ImageLoader mImageLoader;

    /**
     * L2缓存管理类
     */
    DiskLruImageCache dLruImgCache;

    /**
     * L1缓存管理类
     */
    BitmapLruImageCache bLruImgCache;

    /**
     * @return instance of the cache manager
     */
    public static ImageCacheManager getInstance() {
        if (mInstance == null)
            mInstance = new ImageCacheManager();

        return mInstance;
    }

    /**
     * Initializer for the manager. Must be called prior to use.
     *
     * @param context        application context
     * @param uniqueName     name for the cache location
     * @param cacheSize      max size for the cache
     * @param compressFormat file type compression format.
     * @param quality
     */
    public void init(Context context, String uniqueName, int cacheSize,
                     CompressFormat compressFormat, int quality) {
        if (dLruImgCache != null || bLruImgCache != null || mImageLoader != null)
            return;
        dLruImgCache = new DiskLruImageCache(context, uniqueName, cacheSize,
                compressFormat, quality);
        bLruImgCache = new BitmapLruImageCache(cacheSize);
        mImageLoader = new ImageLoader(Volley.newRequestQueue(context), this);
    }

    public Bitmap getBitmap(String url) {
        try {
            Bitmap bMap = bLruImgCache.getBitmap(url);
//            if (bMap == null) {
//                bMap = dLruImgCache.getBitmap(url);
//            }
            return bMap;

        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }

    public void putBitmap(String url, Bitmap bitmap) {
        try {
            bLruImgCache.putBitmap(url, bitmap);
            dLruImgCache.putBitmap(url, bitmap);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }

    /**
     * Executes and image load
     *
     * @param url      location of image
     * @param listener Listener for completion
     */
    public void getImage(String url, ImageListener listener) {
        mImageLoader.get(url, listener);
    }

    /**
     * @return instance of the image loader
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void displayImage(ImageView mImg, String requestUrl) {
        if (TextUtils.isEmpty(requestUrl))
            return;
        ImageListener mListener = ImageLoader.getImageListener(mImg,
                android.R.drawable.ic_menu_rotate,
                android.R.drawable.ic_lock_idle_lock);
        mImageLoader.get(requestUrl, mListener);
    }

    public void displayImage(String requestUrl, ImageListener mLsn) {
        if (TextUtils.isEmpty(requestUrl))
            return;
        mImageLoader.get(requestUrl, mLsn);
    }

    public void release() {
        dLruImgCache = null;
        mImageLoader = null;
        bLruImgCache = null;
        mInstance = null;
    }
}
