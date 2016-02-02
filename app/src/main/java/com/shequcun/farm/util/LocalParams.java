package com.shequcun.farm.util;

public class LocalParams {
//    INSTANCE;

//    public void initData(Context context) {
//        con_str_list = new HashMap<String, String>();
//        InputStream tmpStream = null;
//        try {
////            ApplicationInfo appInfo = context.getPackageManager()
////                    .getApplicationInfo(context.getPackageName(), 0);
//            // isDebugMode = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
//            tmpStream = context.getResources().getAssets().open(config_release);
//            BufferedReader bufReader = new BufferedReader(
//                    new InputStreamReader(tmpStream, "UTF-8"));
//            String str;
//            while ((str = bufReader.readLine()) != null) {
//                if (str != null && str.length() > 0
//                        && str.startsWith("#") == false) {
//                    String[] tmp = str.split("=");
//                    if (tmp != null && tmp.length >= 2) {
//                        String name = tmp[0];
//                        String value = tmp[1];
//                        for (int i = 0; i < tmp.length - 2; i++) {
//                            value += "=";
//                            value += tmp[i + 2];
//                        }
//                        if (name != null && value != null) {
//                            name = name.trim();
//                            value = value.trim();
//                            if (name.length() > 0 && value.length() > 0)
//                                con_str_list.put(name, value);
//                            // Log.i("-----------", name + " ======" + value);
//                        }
//                    }
//                }
//            }
//            bufReader.close();
//            tmpStream.close();
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        } finally {
//            try {
//                tmpStream.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public String getWxAppId() {
//        return getKeyValue("wx_pay_app_id");
//    }

//    public String getKeyValue(String key) {
//        String val = con_str_list.get(key);
//        if (TextUtils.isEmpty(val))
//            val = "";
//        return val;
//    }

    public static String getBaseUrl() {
//        return getKeyValue("base_url");
//        return "https://api.shequcun.com/";
//        return "http://192.168.1.222:8002/";
//        return "http://192.168.1.100:9100/";
        return "http://192.168.1.101:8282";
    }

    public static String getWxAppId() {
        return "wxedddf5c468bfd955";
    }

//    public String getAlipayPid() {
//        return getKeyValue(Alipay_Pid);
//    }
//
//    public String getAlipaySeller() {
//        return getKeyValue(Alipay_Seller);
//    }
//
//    public String getAlipayRsaPrivate() {
//        return getKeyValue(Alipay_Rsa_Private);
//    }
//
//    public String getAlipayRsaPublic() {
//        return getKeyValue(Alipay_Rsa_Public);
//    }
//
//    private final String Alipay_Pid = "apipay_pid";
//    private final String Alipay_Seller = "alipay_seller";
//    private final String Alipay_Rsa_Private = "alipay_rsa_private";
//    private final String Alipay_Rsa_Public = "alipay_rsa_public";
//
//    private final String config_release = "sqc_farm_config.data";
//    HashMap<String, String> con_str_list;
}
