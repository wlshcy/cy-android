package com.shequcun.farm.task;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.util.DeviceInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cong on 15/10/19.
 * 图片模糊是因为第三方为了节省内存做的处理
 */
public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> imageViewWeakReference;
    private Activity context;

    public BitmapAsyncTask(Activity context, ImageView imageView) {
        this.context = context;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imgUrl = params[0];
        InputStream input = null;
        FileOutputStream output = null;
        HttpURLConnection con = null;
        File file = null;
        try {
            file = new File(context.getCacheDir(), "xxxx.jpg");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                }
            } catch (IOException e) {
                return null;
            }
            URL url = new URL(imgUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
//            con.setConnectTimeout(5 * 1000);
//            con.setReadTimeout(5 * 1000);
            con.connect();
            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            input = con.getInputStream();
            output = new FileOutputStream(file);
            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) > 0) {
                output.write(data, 0, count);
            }
            output.flush();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (con != null)
                con.disconnect();
        }
        if (file != null) {
            Bitmap bitmap = decodeFromFile(file.getAbsolutePath());
            file.delete();
            return bitmap;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null) return;
        ImageView imageView = imageViewWeakReference.get();
        if (imageView != null)
            imageView.setImageBitmap(bitmap);
    }

    private Bitmap decodeFromFile(String filepath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);
        /**如果按照屏幕尺度会导致显示不出图片*/
        options.inSampleSize = calculateInSampleSize(options, 480);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int w) {
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > w) {
            final int wRatio = Math.round((float) width / (float) w);
            inSampleSize = wRatio;
        }
        return inSampleSize;
    }
}
