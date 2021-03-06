package com.lynp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lynp.BaseFragmentActivity;
import com.lynp.R;
import com.lynp.ui.data.VersionEntry;
import com.lynp.ui.util.PersistanceManager;
import com.lynp.ui.util.UserGuideDialog;
import com.lynp.ui.adapter.HomeViewPagerAdapter;
import com.lynp.ui.util.DeviceInfo;
import com.lynp.ui.util.HttpRequestUtil;
import com.lynp.ui.util.JsonUtilsParser;
import com.lynp.ui.util.LocalParams;
import com.lynp.ui.util.ToastHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * 原味
 * Created by nmg on 16/1/30.
 */
public class MainActivity extends BaseFragmentActivity {

    @Bind(R.id.MainPage)
    HomeViewPager MainPage;
    @Bind(R.id.radiogroup)
    RadioGroup radiogroup;
    HomeViewPagerAdapter MainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        initGuide();
        initWidget();
//        doAuthInit();
//        checkVersion();
    }

    private void initWidget() {
        buildAdapter();
        setWidgetLsn();
//        initTipChooseCombo();
    }

    private void initGuide() {
        if (!PersistanceManager.getOnce(this)) {
            UserGuideDialog userGuideDialog = new UserGuideDialog(this);
            userGuideDialog.show();
        }
    }

//    private void initTipChooseCombo() {
//        if (!PersistanceManager.getOnce(this)) {
//            tipChooseComboVb.inflate();
//            View view = findViewById(R.id.container_ll);
//            View view1 = findViewById(R.id.combo_iv);
//            view.setOnClickListener(new AvoidDoubleClickListener() {
//                @Override
//                public void onViewClick(View v) {
//                }
//            });
//            view1.setOnClickListener(new AvoidDoubleClickListener() {
//                @Override
//                public void onViewClick(View v) {
//                    tipChooseComboVb.setVisibility(View.INVISIBLE);
//                    gotoComboFragment(R.id.mainpage_ly, new ComboFragment(), ComboFragment.class.getName());
//                    PersistanceManager.saveOnce(SqcFarmActivity.this, true);
//                }
//            });
//        }
//    }

//    private void gotoComboFragment(int id, Fragment fragment, String tag) {
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.add(id, fragment);
//        transaction.addToBackStack(tag);
//        transaction.commitAllowingStateLoss();
//    }

    void setWidgetLsn() {
        radiogroup.setOnCheckedChangeListener(checkedChangeListener);
        MainPage.setOnPageChangeListener(pageChangeLsn);
        MainPage.setOffscreenPageLimit(3);
        buildRadioButtonStatus(0);
    }

    private void buildAdapter() {
        if (MainAdapter == null)
            MainAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        MainPage.setAdapter(MainAdapter);
    }

    private ViewPager.OnPageChangeListener pageChangeLsn = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            buildRadioButtonStatus(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * @param index
     */
    public void buildRadioButtonStatus(int index) {
        RadioButton rb = ((RadioButton) radiogroup.getChildAt(index));
        rb.setChecked(true);
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.home:
                    MainPage.setCurrentItem(0);
                    break;
                case R.id.shopping_cart:
                    MainPage.setCurrentItem(1);
                    break;
                case R.id.mine:
                    MainPage.setCurrentItem(2);
                    break;
                default:
                    break;
            }

        }
    };

    void checkVersion() {
        if (PersistanceManager.getIsCheckVersion(getApplicationContext()))
            return;
        RequestParams params = new RequestParams();
        params.add("apptype", "5");
        params.add("platform", "2");
        HttpRequestUtil.getHttpClient(getApplication()).get(LocalParams.getBaseUrl() + "app/version", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    VersionEntry vEntry = JsonUtilsParser.fromJson(new String(data), VersionEntry.class);
                    if (vEntry != null) {
                        if (TextUtils.isEmpty(vEntry.errmsg)) {
                            if (!TextUtils.isEmpty(vEntry.version) && vEntry.version.compareTo(DeviceInfo.getVersion(MainActivity.this)) > 0) {
                                showUpdateDlg(vEntry);
                            }
                        } else {
                            ToastHelper.showShort(MainActivity.this, vEntry.errmsg);
                        }
                    }
                }
            }


            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {

            }

        });
    }

    private void showUpdateDlg(final VersionEntry vEntry) {
        if (vEntry == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                if (vEntry.status == 2) {
                    finish();
                }

            }
        });
        builder.setNeutralButton(vEntry.status == 1 ? R.string.ignore : R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (vEntry.status == 2) {
                    dialog.dismiss();
                    finish();
                } else if (vEntry.status == 1) {
                    dialog.dismiss();
                    PersistanceManager.saveIsCheckVersion(getApplicationContext(), true);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 鉴权
     */
//    private void doAuthInit() {
//        HttpRequestUtil.getHttpClient(this).get(LocalParams.getBaseUrl() + "auth/init",
//                new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int sCode, Header[] headers, byte[] data) {
//                        for (Header h : headers) {
//                            if (h.getName().equals("X-Xsrftoken")) {
//                                PersistanceManager.saveCookieValue(SqcFarmActivity.this, h.getValue());
//                                break;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
//                    }
//                });
//    }


}
