package com.shequcun.farm.db;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * 类说明 :
 * 
 * @version 1.0
 */
public class NetUtil {
	/**
	 * 没有网络
	 */
	public static final int NETWORK_NONE = -1;
	/**
	 * 当前正在使用wifi
	 */
	public static final int NETWORK_WIFI = 0;
	/**
	 * 当前正在使用手机3g网络
	 */
	public static final int NETWORK_MOBILE = 1;
	private static final int SET_CONNECTION_TIMEOUT = 30000;
	private static final int SET_SOCKET_TIMEOUT = 200000;

	/**
	 * http请求（get）
	 */
	public static final int HTTP_METHOD_GET = 100;
	/**
	 * http请求（post）
	 */
	public static final int HTTP_METHOD_POST = 101;
	/**
	 * http请求（put）
	 */
	public static final int HTTP_METHOD_PUT = 102;
	/**
	 * http请求（delete）
	 */
	public static final int HTTP_METHOD_DELETE = 103;
	public static final String IMAGE_CACHE_DIR = "/Android/data/shequcun/hamlet/";

	/**
	 * 获取当前手机联网类型
	 * 
	 * @return
	 */
	public static int getCurNetworkType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return NETWORK_NONE;
		}
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return NETWORK_NONE;
		}

		int type = networkinfo.getType();
		if (type == ConnectivityManager.TYPE_WIFI) {
			return NETWORK_WIFI;
		}
		return NETWORK_MOBILE;
	}

	public static Bitmap getBitmapByUrl(String urlString)
			throws MalformedURLException, IOException {
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(25000);
		connection.setReadTimeout(90000);
		Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
		return bitmap;
	}

	public static String inputStreamToString(InputStream is, String encoding) {
		try {
			byte[] b = new byte[1024];
			StringBuilder sb = new StringBuilder();
			if (is == null) {
				return "";
			}

			int bytesRead = 0;
			while (true) {
				bytesRead = is.read(b, 0, 1024); // return final read bytes
				// counts
				if (bytesRead == -1) {// end of InputStream
					return sb.toString();
				}
				sb.append(new String(b, 0, bytesRead, encoding)); // convert to
				// string
				// using
				// bytes
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getString(InputStream is) {
		try {
			String result = "";
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = is.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			// Return result from buffered stream
			result = new String(content.toByteArray());
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String shortUrl(String url) {
		// 要使用生成 URL 的字符
		String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h",
				"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
				"u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z" };
		// 对传入网址进行 MD5 加密
		String sMD5EncryptResult = null;
		try {
			sMD5EncryptResult = CryptoUtil.md5(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String hex = sMD5EncryptResult;
		String[] resUrl = new String[4];
		for (int i = 0; i < 4; i++) {
			// 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
			String sTempSubString = hex.substring(i * 8, i * 8 + 8);
			// 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用
			// long ，则会越界
			long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
			String outChars = "";
			for (int j = 0; j < 6; j++) {
				// 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
				long index = 0x0000003D & lHexLong;
				// 把取得的字符相加
				outChars += chars[(int) index];
				// 每次循环按位右移 5 位
				lHexLong = lHexLong >> 5;
			}
			// 把字符串存入对应索引的输出数组
			resUrl[i] = outChars;
		}

		return resUrl[0] + resUrl[1];

	}

	public static String encodeParameters(HashMap<String, String> httpParams) {
		if (null == httpParams || httpParams.isEmpty()) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		Iterator<Entry<String, String>> iter = httpParams.entrySet().iterator();
		boolean first = true;
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
					.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();
			if (first)
				first = false;
			else
				buf.append("&");
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
						.append(URLEncoder.encode(value, "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
				neverHappen.printStackTrace();
			}
		}
		return buf.toString();
	}

	public static String encodeParametersToJson(
			HashMap<String, String> httpParams) {
		if (null == httpParams || httpParams.isEmpty()) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		buf.append("{");
		boolean first = true;
		Iterator<Entry<String, String>> iter = httpParams.entrySet().iterator();
		while (iter.hasNext()) {
			if (first)
				first = false;
			else
				buf.append(",");
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
					.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();
			buf.append("\"").append(key).append("\":\"").append(value)
					.append("\"");
		}
		buf.append("}");
		return buf.toString();
	}

	/**
	 * Get a HttpClient object which is setting correctly .
	 * 
	 * @param context
	 *            : context of activity
	 * @return HttpClient: HttpClient object
	 */
	public static HttpClient getHttpClient(Context context) {
		BasicHttpParams httpParameters = new BasicHttpParams();
		// Set the default socket timeout (SO_TIMEOUT) // in
		// milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				SET_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SET_SOCKET_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParameters);
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			// 获取当前正在使用的APN接入点
			Uri uri = Uri.parse("content://telephony/carriers/preferapn");
			Cursor mCursor = context.getContentResolver().query(uri, null,
					null, null, null);
			if (mCursor != null && mCursor.moveToFirst()) {
				// 游标移至第一条记录，当然也只有一条
				String proxyStr = mCursor.getString(mCursor
						.getColumnIndex("proxy"));
				if (proxyStr != null && proxyStr.trim().length() > 0) {
					HttpHost proxy = new HttpHost(proxyStr, 80);
					client.getParams().setParameter(
							ConnRouteParams.DEFAULT_PROXY, proxy);
				}
			}
		}
		return client;
	}

	/**
	 * Read http requests result from response .
	 * 
	 * @param response
	 *            : http response by executing httpclient
	 * 
	 * @return String : http response content
	 */
	private static String read(HttpResponse response) throws Exception {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null
					&& header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}

			result = new String(content.toByteArray()); // Return result from
			// buffered stream
			return result;
		} catch (IllegalStateException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		}
	}

	/**
	 * 执行post请求，获取服务器返回的信息
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            访问链接
	 * @param params
	 *            参数
	 * @return
	 * @throws Exception
	 *             异常
	 */
	public static String post(Context context, String url,
			HashMap<String, String> params) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpPost post = new HttpPost(url);
			post.setHeader("X-CLIENT-AGENT", "android");
			byte[] data = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024 * 50);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			String postParam = encodeParameters(params);
			data = postParam.getBytes("UTF-8");
			bos.write(data);
			data = bos.toByteArray();
			bos.close();
			ByteArrayEntity formEntity = new ByteArrayEntity(data);
			post.setEntity(formEntity);
			HttpResponse response = client.execute(post);
			StatusLine status = response.getStatusLine();
			String result = read(response);
			if (status.getStatusCode() != 200) {
				throw new Exception(result);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {
				client.getConnectionManager().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Upload image into output stream .
	 * 
	 * @param out
	 *            : output stream for uploading weibo
	 * @param
	 *            : bitmap for uploading
	 * @return void
	 */
	public static void imageContentToUpload(OutputStream out, Bitmap bitmap,
			int compressRate) {
		BufferedInputStream bis = null;
		try {
			StringBuilder temp = new StringBuilder();
			temp.append("--7cd4a6d158c")
					.append("\r\n")
					.append("Content-Disposition: form-data;")
					.append(" name=\"filename\"; filename=\"upload_image.jpg\"")
					.append("\r\n").append("Content-Type: image/jpg")
					.append("\r\n\r\n");
			out.write(temp.toString().getBytes());
			if (compressRate > 0 && compressRate <= 100) {
				bitmap.compress(CompressFormat.JPEG, compressRate, out);
			} else {
				bitmap.compress(CompressFormat.JPEG, 85, out);
			}
			out.write("\r\n".getBytes());
			out.write(("\r\n" + "--7cd4a6d158c--").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 检查当前网络连接是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo activeNetInfo = connectivityManager
					.getActiveNetworkInfo();
			if (activeNetInfo != null && activeNetInfo.isAvailable()) { // 当前网络可用
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据url加载图片（首先加载本地缓存的图片，若无缓存则先从网络取然后缓存到本地，之后再返回本地缓存的图片，
	 * 缓存时将url进行md5加密之后转化为短url作为要存储的图片文件名）
	 * 
	 * @param imageUrl
	 *            要访问的图片的url地址
	 * @return 获取到的图片对象或者空值
	 */
	public static Drawable cachedLoadImageFromUrl(String imageUrl) {
		Drawable drawable = null;
		if (imageUrl == null || "null".equals(imageUrl) || "".equals(imageUrl))
			return null;
		String shortUrl = NetUtil.shortUrl(imageUrl);
		String filapath = Environment.getExternalStorageDirectory()
				+ IMAGE_CACHE_DIR;
		File fileDir = new File(filapath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		File file = new File(filapath + shortUrl);
		if (file.exists() && file.isFile()) { // 优先读取本地缓存
			try {
				drawable = Drawable.createFromPath(file.toString());
				if (drawable != null)
					return drawable;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file); // 将网络上获取的图片缓存到本地
			InputStream is = new URL(imageUrl).openStream();
			int data = is.read();
			while (data != -1) {
				fos.write(data);
				data = is.read();
			}
			fos.close();
			is.close();
			drawable = Drawable.createFromPath(file.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return drawable;
	}



	public static String handleStatusCode(int statusCode, Resources resources) {
		String m_Tips = "请求错误，请稍后再试！";
		// if(statusCode ==-1){
		// }else if(statusCode ==200){
		// }else if(statusCode>=400 && statusCode<500){
		// switch(statusCode){
		// case 400:
		// m_Tips = "请求错误，请稍后再试！";
		// break;
		// case 401:
		// m_Tips = "未授权的请求，请稍后再试！";
		// break;
		// case 403:
		// m_Tips = "服务器拒绝请求，请稍后再试！";
		// break;
		// case 404:
		// m_Tips = "找不到请求地址，请稍后再试！";
		// break;
		// default:
		// m_Tips = "网络不给力，请稍后再试！";
		// break;
		// }
		// }else if(statusCode>=500){
		// switch(statusCode){
		// case 500:
		// m_Tips = "服务器内部错误，请稍后再试！";
		// break;
		// case 501:
		// m_Tips = "服务器无法识别请求，请稍后再试！";
		// break;
		// case 502:
		// m_Tips = "服务器网关错误，请稍后再试！";
		// break;
		// case 503:
		// m_Tips = "服务器正在维护中，请稍后再试！";
		// break;
		// case 504:
		// m_Tips = "网关请求超时，请稍后再试！";
		// break;
		// case 1000:
		// case 1001:
		// m_Tips = "网络连接超时，请稍后再试！";
		// break;
		// default :
		// m_Tips = "未知的服务器错误，请稍后再试！";
		// break;
		// }
		// }
		return m_Tips;
	}

	/**
	 * 存数据库前将字符串转义
	 * 
	 * @param keyWord
	 * @return
	 */
	public static String sqliteEscape(String keyWord) {
		keyWord = keyWord.replace("'", "&apos;"); // 将特殊字符转义
		return keyWord;
	}

	/**
	 * 取出来之后还原
	 * 
	 * @param keyWord
	 * @return
	 */
	public static String reSqliteEscape(String keyWord) {
		keyWord = keyWord.replace("&apos;", "'"); // 将转义后的字符还原
		return keyWord;
	}

	public static InputStream save(Context context, String fileName,
			InputStream is) throws Exception {
		String result = inputStreamToString(is, "UTF-8");
		return save(context, fileName, result);
	}

	/**
	 * 保存文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            文件内容
	 * @throws Exception
	 */
	public static InputStream save(Context context, String fileName,
			String content) throws Exception {

		// 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
		if (!fileName.endsWith(".txt")) {
			fileName = fileName + ".txt";
		}

		byte[] buf = fileName.getBytes("iso8859-1");

		fileName = new String(buf, "utf-8");

		// Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
		// Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
		// Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
		// MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
		// 如果希望文件被其他应用读和写，可以传入：
		// openFileOutput("output.txt", Context.MODE_WORLD_READABLE +
		// Context.MODE_WORLD_WRITEABLE);

		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		fos.write(content.getBytes());
		fos.close();

		FileInputStream fis = context.openFileInput(fileName);
		return fis;
	}

	/**
	 * 读取文件内容
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件内容
	 * @throws Exception
	 */
	public String read(Context context, String fileName) throws Exception {

		// 当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
		if (!fileName.endsWith(".txt")) {
			fileName = fileName + ".txt";
		}

		FileInputStream fis = context.openFileInput(fileName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[1024];
		int len = 0;

		// 将读取后的数据放置在内存中---ByteArrayOutputStream
		while ((len = fis.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}

		fis.close();
		baos.close();

		// 返回内存中存储的数据
		return baos.toString();
	}

	public static FileInputStream getCacheFile(Context context, String fileName)
			throws Exception {
		if (!fileName.endsWith(".txt")) {
			fileName = fileName + ".txt";
		}
		FileInputStream fis = context.openFileInput(fileName);
		return fis;
	}

	public static boolean isNetConnected(Context context) {
		ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			// do something
			// 不能联网
			return false;
		}
		return true;
	}
}
