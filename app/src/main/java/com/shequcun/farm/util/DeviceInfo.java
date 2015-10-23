package com.shequcun.farm.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.security.auth.x500.X500Principal;

/**
 * @author apple
 */
public class DeviceInfo {

    public static String getVersion(Context mContext) {
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "null";
        }
    }


    public static String getReleseVersion() {
        String release;
        try {
            release = Build.VERSION.RELEASE;
        } catch (Exception e) {
            release = "null";
        }
        return release;
    }

    public static String getModelName() {
        String model;
        try {
            model = Build.MODEL;
        } catch (Exception e) {
            model = "null";
        }
        return model;
    }

    public static String getDeviceId(Context mContext) {

        String deviceId;
        try {
            final TelephonyManager tm = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = ""
                    + android.provider.Settings.Secure.getString(
                    mContext.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceId = deviceUuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
            deviceId = "null";
        }
        return deviceId;
    }

    public static int getDeviceWidth(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getDeviceHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    private static final X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");

    public static boolean isDebuggable(Context ctx) {
        boolean debuggable = false;
        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (int i = 0; i < signatures.length; i++) {
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            //debuggable variable will remain false
        } catch (CertificateException e) {
            //debuggable variable will remain false
        }
        return debuggable;
    }


}
