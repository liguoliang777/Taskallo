package cn.ngame.store.activity.hub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.AddPointBodyBean;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.MsgDetailBodyBean;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.NormalDataBean;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.PostDetailBean;
import com.jzt.hol.android.jkda.sdk.rx.ObserverWrapper;
import com.jzt.hol.android.jkda.sdk.services.gamehub.AddPointClient;
import com.jzt.hol.android.jkda.sdk.services.gamehub.PostDetailClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import cn.ngame.store.R;
import cn.ngame.store.StoreApplication;
import cn.ngame.store.activity.BaseFgActivity;
import cn.ngame.store.bean.User;
import cn.ngame.store.core.utils.Constant;
import cn.ngame.store.core.utils.ImageUtil;
import cn.ngame.store.core.utils.KeyConstant;
import cn.ngame.store.util.ConvUtil;
import cn.ngame.store.util.ToastUtil;
import cn.ngame.store.view.zan.HeartLayout;


/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class HubItemActivity extends BaseFgActivity {
    private HubItemActivity mContext;
    private int postId = 0;
    private TextView mTitleTv, mFromTv, mDescTv, mTimeTv, mHubNameTv, mWatchNum, mSupportNumTv;
    private ImageView mSupportBt;
    private HeartLayout heartLayout;
    private SimpleDraweeView mFromIcon, mHubImageView;
    private PostDetailBean.DataBean.ShowPostCategoryBean hubInfo;
    private RelativeLayout hubLayout;
    private LinearLayout imageLayout;
    private List<PostDetailBean.DataBean.PostImageListBean> postImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_game_hub_detail);
        initView();
        try {
            postId = getIntent().getIntExtra(KeyConstant.ID, 0);
            Log.d(TAG, "id====: " + postId);
        } catch (Exception e) {
        }
        mContext = this;
        //请求数据
        getData();
    }

    private void initView() {
        mFromTv = findViewById(R.id.hub_detail_from_tv);
        mFromIcon = findViewById(R.id.game_hub_sdv);
        mTitleTv = findViewById(R.id.game_hub_detail_title_tv);
        mDescTv = findViewById(R.id.game_hub_detail_desc_tv);
        mTimeTv = findViewById(R.id.hub_detail_time_tv);
        mSupportNumTv = findViewById(R.id.game_hub_support_tv);
        mSupportBt = findViewById(R.id.game_hub_support_bt);
        mWatchNum = findViewById(R.id.see_numb_tv);
        mHubNameTv = findViewById(R.id.hub_detail_hub_name_tv);
        mHubImageView = findViewById(R.id.hub_detail_hub_iv);
        heartLayout = findViewById(R.id.heart_layout);
        hubLayout = findViewById(R.id.hub_detail_hub_layout);

        imageLayout = findViewById(R.id.hub_item_layout);
    }

    protected static final String TAG = HubItemActivity.class.getSimpleName();

    private void setMsgDetail(PostDetailBean result) {
        final PostDetailBean.DataBean data = result.getData();
        Log.d(TAG, "获取单条数据: ==" + data);
        if (data == null) {
            return;
        }
        mFromIcon.setImageURI(data.getPostRoleHeadPhoto());
        mFromTv.setText(data.getPostRoleName());
        mTitleTv.setText(data.getPostTitle());
        mTimeTv.setText(String.valueOf(DateUtils.getRelativeTimeSpanString(
                data.getUpdateTime())).replace(" ", ""));
        mSupportNumTv.setText(data.getPointNum() + "赞");
        mWatchNum.setText(String.valueOf(data.getWatchNum()));
        hubInfo = data.getShowPostCategory();
        if (hubInfo != null) {
            mHubNameTv.setText(hubInfo.getPostCategoryName());
            mHubImageView.setImageURI(hubInfo.getPostCategoryUrl());
            hubLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra(KeyConstant.postId, hubInfo.getId());
                    intent.setClass(mContext, CircleActivity.class);
                    startActivity(intent);
                }
            });
        }

     /*   postImageList = data.getPostImageList();
        LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R
                .dimen.dm300));
        pointParams.topMargin = getResources().getDimensionPixelSize(R.dimen.dm010);
        if (postImageList != null) {
            for (int i = 0; i < postImageList.size(); i++) {
                SimpleDraweeView iv = new SimpleDraweeView(mContext);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setLayoutParams(pointParams);
                iv.setImageURI(postImageList.get(i).getPostImageAddress());

                imageLayout.addView(iv);
            }
        }*/

        if (data.getIsPoint() == 1) {
            mSupportBt.setBackgroundResource(R.drawable.zan);
            mSupportNumTv.setTextColor(ContextCompat.getColor(mContext, R.color.mainColor));
            mSupportBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.show(mContext, "已经点过赞了哦~");
                    //heartLayout.addFavor();
                }
            });
        } else {
            mSupportBt.setBackgroundResource(R.drawable.un_zan);
            mSupportBt.setEnabled(true);
            mSupportBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickAgree(1, postId);
                    heartLayout.addFavor();
                }
            });
        }



        String postContent = data.getPostContent();
        //if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {}
        Log.d(TAG, "4.4===========");
        Spanned spanned = Html.fromHtml(postContent,new HtmlImageGetter(),null);
        mDescTv.setText(spanned);

    }
    /**
     * 重写图片加载接口
     *
     * @author Ruffian
     * @date 2016年1月15日
     *
     */
    class HtmlImageGetter implements Html.ImageGetter {

        /**
         * 获取图片
         */
        @Override
        public Drawable getDrawable(String source) {
            LevelListDrawable d = new LevelListDrawable();
            Drawable empty = getResources().getDrawable(
                    R.drawable.ic_def_logo_720_288);
            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, ImageUtil.getScreenWidth(mContext),
                    empty.getIntrinsicHeight());
            new LoadImage().execute(source, d);
            return d;
        }

        /**
         * 异步下载图片类
         *
         * @author Ruffian
         * @date 2016年1月15日
         *
         */
        class LoadImage extends AsyncTask<Object, Void, Bitmap> {

            private LevelListDrawable mDrawable;

            @Override
            protected Bitmap doInBackground(Object... params) {
                String source = (String) params[0];
                mDrawable = (LevelListDrawable) params[1];
                try {
                    InputStream is = new URL(source).openStream();
                    return BitmapFactory.decodeStream(is);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            /**
             * 图片下载完成后执行
             */
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    BitmapDrawable d = new BitmapDrawable(bitmap);
                    mDrawable.addLevel(1, 1, d);
                    /**
                     * 适配图片大小 <br/>
                     * 默认大小：bitmap.getWidth(), bitmap.getHeight()<br/>
                     * 适配屏幕：getDrawableAdapter
                     */
                    mDrawable = getDrawableAdapter(mContext, mDrawable,
                            bitmap.getWidth(), bitmap.getHeight());

                    // mDrawable.setBounds(0, 0, bitmap.getWidth(),
                    // bitmap.getHeight());

                    mDrawable.setLevel(1);

                    /**
                     * 图片下载完成之后重新赋值textView<br/>
                     * mtvActNewsContent:我项目中使用的textView
                     *
                     */
                    mDescTv.invalidate();
                    CharSequence t = mDescTv.getText();
                    mDescTv.setText(t);

                }
            }

            /**
             * 加载网络图片,适配大小
             *
             * @param context
             * @param drawable
             * @param oldWidth
             * @param oldHeight
             * @return
             * @author Ruffian
             * @date 2016年1月15日
             */
            public LevelListDrawable getDrawableAdapter(Context context,
                                                        LevelListDrawable drawable, int oldWidth, int oldHeight) {
                LevelListDrawable newDrawable = drawable;
                long newHeight = 0;// 未知数
                int newWidth = ImageUtil.getScreenWidth(mContext);
                newHeight = (newWidth * oldHeight) / oldWidth;
                // LogUtils.w("oldWidth:" + oldWidth + "oldHeight:" +
                // oldHeight);
                // LogUtils.w("newHeight:" + newHeight + "newWidth:" +
                // newWidth);
                newDrawable.setBounds(0, 0, newWidth, (int) newHeight);
                return newDrawable;
            }
        }

    }

    /**
     * @param type 1表示帖子点赞，2表示评论点赞，3表示投票
     * @param id   帖子id/评论id
     */
    public void clickAgree(final int type, int id) {
        mSupportBt.setEnabled(false);
        //帖子id
        AddPointBodyBean bodyBean = new AddPointBodyBean();
        User user = StoreApplication.user;
        if (user != null) {
            bodyBean.setUserCode(user.userCode);
        } else {
            bodyBean.setDeviceOnlyNum(StoreApplication.deviceId);
        }
        bodyBean.setAppTypeId(Constant.APP_TYPE_ID_0_ANDROID);
        bodyBean.setPostId(id);  //帖子id
        new AddPointClient(this, bodyBean).observable()
//                .compose(this.<DiscountListBean>bindToLifecycle())
                .subscribe(new ObserverWrapper<NormalDataBean>() {
                    @Override
                    public void onError(Throwable e) {
                        mSupportBt.setEnabled(true);
                        ToastUtil.show(mContext, "点赞失败,请稍后重试~");
                    }

                    @Override
                    public void onNext(NormalDataBean result) {
                        if (mContext == null || mContext.isFinishing()) {
                            return;
                        }
                        mSupportBt.setEnabled(true);
                        if (result != null && result.getCode() == 0) {
                            if (type == 1) { //区分帖子点赞和评论点赞
                                ToastUtil.show(mContext, "点赞成功~");
                                mSupportNumTv.setText(ConvUtil.NI(mSupportNumTv.getText()
                                        .toString()) + 1 + "赞");
                                mSupportNumTv.setTextColor(ContextCompat.getColor(mContext, R
                                        .color.mainColor));
                                mSupportBt.setBackgroundResource(R.drawable.zan);
                                mSupportBt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ToastUtil.show(mContext, "已经点过赞了哦~");
                                        //heartLayout.addFavor();
                                    }
                                });
                            } else {
                            }
                        } else {
                            ToastUtil.show(mContext, "点赞失败,请稍后重试~");
                        }
                    }
                });
    }

    /**
     * 获取游戏详情
     */
    private void getData() {
        MsgDetailBodyBean bodyBean = new MsgDetailBodyBean();
        User user = StoreApplication.user;
        if (user != null) {
            bodyBean.setUserCode(user.userCode);
        } else {
            bodyBean.setDeviceOnlyNum(StoreApplication.deviceId);
        }
        bodyBean.setPostId(postId);
        bodyBean.setAppTypeId(Constant.APP_TYPE_ID_0_ANDROID);
        new PostDetailClient(this, bodyBean).observable()
                .subscribe(new ObserverWrapper<PostDetailBean>() {
                    @Override
                    public void onError(Throwable e) {
                        //("服务器开小差咯~");
                    }

                    @Override
                    public void onNext(PostDetailBean result) {
                        if (result != null && result.getCode() == 0) {
                            setMsgDetail(result);
                        } else {
                            //ToastUtil.show(mContext, "获取失败");
                            // mDescTv.setText("获取失败~");
                        }
                    }
                });
    }

    public void onHubItemBackClick(View view) {
        finish();
    }
}
