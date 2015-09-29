package com.shequcun.farm.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.common.widget.ExtendedViewPager;
import com.common.widget.TouchImageView;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.shequcun.farm.R;
import com.shequcun.farm.model.PhotoModel;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;

import java.util.List;

import butterknife.Bind;

/**
 * 浏览图片
 * Created by apple on 15/8/11.
 */
public class BrowseImageFragment extends BaseFragment {
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mViewPager.setAdapter(new TouchImageAdapter());
        mViewPager.setCurrentItem(index);
        initTips(v);
    }

    @Override
    protected void setWidgetLsn() {
        mViewPager.addOnPageChangeListener(onPageChangeListener);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        photos = (List<PhotoModel>) bundle.getSerializable(KEY_PHOTOS);
        if (photos == null || photos.isEmpty()) {

        }
        index = bundle.getInt(KEY_INDEX);
//        cancel = bundle.getBoolean(KEY_CANCEL);
        if (!ImageLoader.getInstance().isInited())
            initImageLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.browse_image_ly, null);
    }


    private void initTips(View view) {
        ViewGroup group = (ViewGroup) view.findViewById(R.id.photos_tip_ll);
        tips = new ImageView[photos.size()];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            group.addView(imageView, layoutParams);
        }
    }


    class TouchImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final TouchImageView img = new TouchImageView(container.getContext());
            PhotoModel model = photos.get(position);
//            取消图片改变状态
//            if (cancel) {
//                toggleCancelImage(model.isCancel());
//            }
            String url = "";
            if (model.isFromNetwork()) {
                url = model.getUrl();
                ImageLoader.getInstance().displayImage(url, img, image_display_options_cache, new MyImageLoadingListener(img, true));
            } else {
                url = "file://" + model.getOriginalPath();
                ImageLoader.getInstance().displayImage(url, img, image_display_options_disc, new MyImageLoadingListener(img));
            }
            img.setOnClickListener(mOnClickListener);
//            img.setOnLongClickListener(new MyOnLongClickListener(
//                    url));
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

//    private class MyOnLongClickListener implements View.OnLongClickListener {
//        private final String url;
//
//        private MyOnLongClickListener(String url) {
//            this.url = url;
//        }
//
//        @Override
//        public boolean onLongClick(View v) {
//            // TODO Auto-generated method stub
//            altertDialog(url);
//            return false;
//        }
//    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            popBackStack();
//            if (onFrgmFinishListener != null) {
//                onFrgmFinishListener.onFrgmFinish(PhotoZoomInFragment.class, null);
//            }
        }
    };

    private View.OnClickListener onCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = mViewPager.getCurrentItem();
            if (index > -1) {
                PhotoModel model = photos.get(index);
                model.setIsCancel(!model.isCancel());
//              //  toggleCancelImage(model.isCancel());
            }
        }
    };


    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            toggleTip(position % photos.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            int cur = mViewPager.getCurrentItem();
            if (cur != state) {
                PhotoModel model = photos.get(cur);
//                toggleCancelImage(model.isCancel());
            }
        }
    };

    private void toggleTip(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

//    private void altertDialog(final String url) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setCancelable(true);
//        builder.setItems(new String[]{getResources().getString(R.string.save_to_mobile)}, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
////                String result = ImageUtil.saveImage(getActivity(), url);
//                String result = ImageUtil.saveImage(getActivity(), saveBitmap);
//                if (result != null) {
//                    String txt = getResources().getString(R.string.image_has_been_saved_to_w_file).replaceAll("W", result);
//                    Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                } else {
//                    Toast.makeText(getActivity(), R.string.image_saved_failure, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        builder.show();
//    }

    public class MyImageLoadingListener implements ImageLoadingListener {
        ImageView imageView;
        boolean save;

        public MyImageLoadingListener(ImageView imageView) {
            this.imageView = imageView;
        }

        public MyImageLoadingListener(ImageView imageView, boolean save) {
            this.imageView = imageView;
            this.save = save;
        }

        @Override
        public void onLoadingStarted(String s, View view) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingFailed(String s, View view, FailReason failReason) {
            mProgressBar.setVisibility(View.GONE);
            if (imageView != null)
                imageView.setImageResource(R.drawable.ic_loading_failure);
        }

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            mProgressBar.setVisibility(View.GONE);
//            if (save)
//                saveBitmap = bitmap;
        }

        @Override
        public void onLoadingCancelled(String s, View view) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    void initImageLoader(Context context) {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.grey_def_bg)
                .showImageOnFail(R.drawable.ic_loading_failure)
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true).considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .memoryCacheExtraOptions(400, 400)
                        // default = device screen dimensions
                .diskCacheExtraOptions(400, 400, null)
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
                .diskCache(
                        new UnlimitedDiskCache(StorageUtils.getCacheDirectory(
                                context, true)))
                        // default
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                        // default
                .imageDownloader(new BaseImageDownloader(context))
                        // default
                .imageDecoder(new BaseImageDecoder(false))
                        // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        // default
                .defaultDisplayImageOptions(imageOptions).build();

        ImageLoader.getInstance().init(config);
    }

    private DisplayImageOptions image_display_options_disc = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.grey_def_bg)
            .showImageForEmptyUri(R.drawable.grey_def_bg)
            .showImageOnFail(R.drawable.grey_def_bg).cacheOnDisc(true).build();
    private DisplayImageOptions image_display_options_cache = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.grey_def_bg)
            .showImageForEmptyUri(R.drawable.grey_def_bg)
            .showImageOnFail(R.drawable.grey_def_bg).cacheInMemory(true).build();

    private int index = 0;
    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;
    private ImageView[] tips;
    @Bind(R.id.view_pager)
    ExtendedViewPager mViewPager;
    //    private Bitmap saveBitmap;
    public static final String KEY_PHOTOS = "photos";
    public static final String KEY_INDEX = "index";
    public static final String KEY_CANCEL = "cancel";
    private List<PhotoModel> photos;
    private PhotoModel currentPhoto;
}
