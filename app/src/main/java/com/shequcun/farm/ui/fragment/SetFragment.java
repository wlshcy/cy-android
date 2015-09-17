package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.VersionEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.MyAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;
import org.json.JSONObject;

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
        mLv = (ListView) v.findViewById(R.id.mLv);
        exit_login = v.findViewById(R.id.exit_login);
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.set);
    }

    @Override
    protected void setWidgetLsn() {
        mLv.setOnItemClickListener(onItemClick);
        back.setOnClickListener(onClick);
        exit_login.setOnClickListener(onClick);
        buildAdapter();
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new MyAdapter(getActivity(), getResources().getStringArray(R.array.set_array));
        adapter.setVerName("V " + getVersionName());
        mLv.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adapter == null || mLv == null)
                return;

            switch (position - mLv.getHeaderViewsCount()) {
                case 0://關於有菜
                    gotoFragmentByAdd(buildBundle("https://store.shequcun.com/about/ycabout", R.string.about), R.id.mainpage_ly, new SetWebViewFragment(), SetWebViewFragment.class.getName());
                    break;
                case 1://檢查更新
                    checkVersion();
                    break;
                case 2://幫助
                    gotoFragmentByAdd(buildBundle("https://store.shequcun.com/help/ychelp", R.string.help), R.id.mainpage_ly, new SetWebViewFragment(), SetWebViewFragment.class.getName());
//                    ToastHelper.showShort(getActivity(), "坐等关于有菜帮助的内容");
                    break;
                case 3://問題反饋
                    gotoFragmentByAdd(R.id.mainpage_ly, new FeedbackFragment(), FeedbackFragment.class.getName());
                    break;
            }

        }
    };


    Bundle buildBundle(String url, int tId) {
        Bundle bundle = new Bundle();
        bundle.putString("Url", url);
        bundle.putInt("TitleId", tId);
        return bundle;
    }


    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back)
                popBackStack();
            else if (exit_login == v)
                showExitDlg();
        }
    };


    void checkVersion() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加載中...");
        RequestParams params = new RequestParams();
        params.add("apptype", "5");
        params.add("platform", "2");
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "app/version", params, new AsyncHttpResponseHandler() {
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    VersionEntry vEntry = JsonUtilsParser.fromJson(new String(data), VersionEntry.class);
                    if (vEntry != null) {
                        if (TextUtils.isEmpty(vEntry.errmsg)) {
                            if (!TextUtils.isEmpty(vEntry.version) && vEntry.version.compareTo(getVersionName()) > 0) {
                                showUpdateDlg(vEntry);
                            } else {
                                ToastHelper.showShort(getActivity(), "您的版本已是最新版本咯!");
                            }
                        } else {
                            ToastHelper.showShort(getActivity(), vEntry.errmsg);
                        }
                    }
                }
            }


            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "更新失敗,錯誤碼" + sCode);
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


    String getVersionName() {
        PackageInfo info = null;
        PackageManager manager = getActivity().getPackageManager();
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void showUpdateDlg(final VersionEntry vEntry) {
        if (vEntry == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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


    private void showExitDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    private void doLogout() {
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));

        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "auth/logout", params, new AsyncHttpResponseHandler() {

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
                                PersistanceManager.saveCookieValue(getActivity(),"");
                                ToastHelper.showShort(getActivity(), R.string.logout_success);
                                new CacheManager(getActivity()).delUserLoginToDisk();
                                IntentUtil.sendUpdateMyInfoMsg(getActivity());
                                IntentUtil.sendUpdateComboMsg(getActivity());
                                IntentUtil.sendUpdateFarmShoppingCartMsg(getActivity());
                                popBackStack();
                            } else {
                                ToastHelper.showShort(getActivity(), R.string.logout_fail);
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
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "退出失败,错误码" + sCode);
            }
        });
    }

    MyAdapter adapter;
    ListView mLv;
    View exit_login;
    View back;
}
