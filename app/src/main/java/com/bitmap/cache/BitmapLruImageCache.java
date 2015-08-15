package com.bitmap.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Basic LRU Memory cache.
 *
 * @author
 */
public class BitmapLruImageCache extends LruCache<String, Bitmap> {

    public BitmapLruImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    public Bitmap getBitmap(String url) {
        return get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
