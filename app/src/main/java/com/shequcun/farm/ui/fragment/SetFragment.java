package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.VersionEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.MyAdapter;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cz.msebera.android.httpclient.Header;

/**
 * 设置
 * Created by apple on 15/8/22.
 */
public class SetFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.set_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.set);
    }


    @Override
    protected void setWidgetLsn() {
        buildAdapter();
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new MyAdapter(getBaseAct(), getResources().getStringArray(R.array.set_array));
        adapter.setVerName("V " + DeviceInfo.getVersion(getBaseAct()));
        mLv.setAdapter(adapter);
    }


    Bundle buildBundle(String url, int tId) {
        Bundle bundle = new Bundle();
        bundle.putString("Url", url);
        bundle.putInt("TitleId", tId);
        return bundle;
    }


    void checkVersion() {
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加載中...");
        RequestParams params = new RequestParams();
        params.add("apptype", "5");
        params.add("platform", "2");
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "app/version", params, new AsyncHttpResponseHandler() {
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    VersionEntry vEntry = JsonUtilsParser.fromJson(new String(data), VersionEntry.class);
                    if (vEntry != null) {
                        if (TextUtils.isEmpty(vEntry.errmsg)) {
                            if (!TextUtils.isEmpty(vEntry.version) && vEntry.version.compareTo(DeviceInfo.getVersion(getBaseAct())) > 0) {
                                showUpdateDlg(vEntry);
                            } else {
                                ToastHelper.showShort(getBaseAct(), "您的版本已是最新版本咯!");
                            }
                        } else {
                            ToastHelper.showShort(getBaseAct(), vEntry.errmsg);
                        }
                    }
                }
            }


            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "更新失敗,錯誤碼" + sCode);
            }

            @Override
            public void onStart() {
                super.onStart();
                pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDlg.dismiss();
            }
        });
    }


    private void showUpdateDlg(final VersionEntry vEntry) {
        if (vEntry == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseAct());
        builder.setTitle("提示");
        builder.setMessage(vEntry.change);
        builder.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(vEntry.url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        builder.setNeutralButton(vEntry.status == 1 ? R.string.ignore : R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (vEntry.status == 2) {
                    dialog.dismiss();
                } else if (vEntry.status == 1) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @OnClick(R.id.exit_login)
    void showExitDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseAct());
        builder.setTitle("提示");
        builder.setMessage("是否退出登录");
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doLogout();
            }
        });
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }

    /**
     * 注销
     */

    void doLogout() {
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));

        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "auth/logout", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDlg.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                try {
                    if (data != null && data.length > 0) {
                        String result = new String(data);
                        JSONObject jObj = new JSONObject(result);
                        if (jObj != null) {
                            boolean logout = jObj.optBoolean("logout");
                            if (logout) {
//                                PersistanceManager.saveCookieValue(getBaseAct(),"");
                                ToastHelper.showShort(getBaseAct(), R.string.logout_success);
                                new CacheManager(getBaseAct()).delUserLoginToDisk();
                                IntentUtil.sendUpdateMyInfoMsg(getBaseAct());
                                IntentUtil.sendUpdateComboMsg(getBaseAct());
                                IntentUtil.sendUpdateFarmShoppingCartMsg(getBaseAct());
                                popBackStack();
                            } else {
                                ToastHelper.showShort(getBaseAct(), R.string.logout_fail);
                            }
                        }

                    }
                } catch (Exception e) {

                }


            }

            @Override
            public void onFailure(int sCode, Header[] h,
                                  byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "退出失败,错误码" + sCode);
            }
        });
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    @OnItemClick(R.id.mLv)
    void onItemClick(int pos) {
        if (adapter == null || mLv == null)
            return;

        switch (pos - mLv.getHeaderViewsCount()) {
            case 0://關於有菜
                gotoFragmentByAdd(buildBundle("https://youcai.shequcun.com/about", R.string.about), R.id.mainpage_ly, new SetWebViewFragment(), SetWebViewFragment.class.getName());
                break;
            case 1://檢查更新
                checkVersion();
                break;
            case 2://幫助
                gotoFragmentByAdd(buildBundle("https://store.shequcun.com/help/ychelp", R.string.help), R.id.mainpage_ly, new SetWebViewFragment(), SetWebViewFragment.class.getName());
//                    ToastHelper.showShort(getBaseAct(), "坐等关于有菜帮助的内容");
                break;
            case 3://問題反饋
                gotoFragmentByAdd(R.id.mainpage_ly, new FeedbackFragment(), FeedbackFragment.class.getName());
                break;
            case 4://给我们打分
                gotoMaketApp();
                break;
        }
    }

    private void gotoMaketApp() {
        String appPackageName = getActivity().getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + appPackageName));
        if (checkIntentExist(getActivity(), intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "您的手机未安装应用市场app", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkIntentExist(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }

    MyAdapter adapter;
    @Bind(R.id.mLv)
    ListView mLv;
}
