package com.android.taskallo.activity.hub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.core.utils.CommonUtil;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.ImageUtil;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.util.ToastUtil;
import com.android.taskallo.view.zan.HeartLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.AddPointBodyBean;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.MsgDetailBodyBean;
import com.jzt.hol.android.jkda.sdk.bean.gamehub.PostDetailBean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class HubItemActivity extends AppCompatActivity {
    private HubItemActivity mContext;
    private int postId = 0;
    private TextView mTitleTv, mFromTv, mDescTv, mTimeTv, mHubNameTv, mWatchNum, mSupportNumTv;
    private ImageView mSupportBt;
    private HeartLayout heartLayout;
    private SimpleDraweeView mFromIcon, mHubImageView;
    private PostDetailBean.DataBean.ShowPostCategoryBean hubInfo;
    private RelativeLayout hubLayout;
    private LinearLayout imageLayout;
    private JCVideoPlayerStandard jzVideoPlayerStandard;
    private List<PostDetailBean.DataBean.PostImageListBean> postImageList;
    private int isPoint = 0;
    private int pointNum = 0;
    private String gameVideoLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initStatusBar();
        setContentView(R.layout.activity_game_hub_detail);
        initView();
        postId = getIntent().getIntExtra(KeyConstant.id, 0);
        mContext = this;
        //请求数据
        getData();
    }

    private void initView() {
        mFromTv = (TextView) findViewById(R.id.hub_detail_from_tv);
        mFromIcon = (SimpleDraweeView) findViewById(R.id.game_hub_sdv);
        mTitleTv = (TextView) findViewById(R.id.game_hub_detail_title_tv);
        mDescTv = (TextView) findViewById(R.id.game_hub_detail_desc_tv);
        mTimeTv = (TextView) findViewById(R.id.hub_detail_time_tv);
        mSupportNumTv = (TextView) findViewById(R.id.game_hub_support_tv);
        mSupportBt = (ImageView) findViewById(R.id.game_hub_support_bt);
        mWatchNum = (TextView) findViewById(R.id.see_numb_tv);
        mHubNameTv = (TextView) findViewById(R.id.hub_detail_hub_name_tv);
        mHubImageView = (SimpleDraweeView) findViewById(R.id.hub_detail_hub_iv);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        hubLayout = (RelativeLayout) findViewById(R.id.hub_detail_hub_layout);

        imageLayout = (LinearLayout) findViewById(R.id.hub_item_layout);

        //视频
        jzVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.hub_item_detial_ngame_vp);
        jzVideoPlayerStandard.topContainer.setVisibility(View.GONE);
    }

    protected static final String TAG = HubItemActivity.class.getSimpleName();

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //清除播放记录
        JCVideoPlayer.clearSavedProgress(mContext, gameVideoLink);
    }

    private void setMsgDetail(PostDetailBean result) {
        mSupportBt.setVisibility(View.VISIBLE);
        final PostDetailBean.DataBean data = result.getData();
        if (data == null) {
            return;
        }
        //视频
        postImageList = data.getPostImageList();
        if (postImageList != null) {
            for (PostDetailBean.DataBean.PostImageListBean postImageListBean : postImageList) {
                if (postImageListBean != null && 9 == postImageListBean.getPostOrderNo()) {
                    gameVideoLink = postImageListBean.getPostImageAddress();
                    jzVideoPlayerStandard.setVisibility(View.VISIBLE);
                    jzVideoPlayerStandard.setUp(gameVideoLink, JCVideoPlayerStandard
                            .SCREEN_LAYOUT_NORMAL, "");
                    //jzVideoPlayerStandard.backButton.setVisibility(View.GONE);
                    if (NetUtil.isWifiConnected(mContext)) {
                        jzVideoPlayerStandard.startVideo();
                    }
                    break;
                }
            }
        }


        mFromIcon.setImageURI(data.getPostRoleHeadPhoto());
        mFromTv.setText(data.getPostRoleName());
        mTitleTv.setText(data.getPostTitle());
        mTimeTv.setText(String.valueOf(DateUtils.getRelativeTimeSpanString(
                data.getUpdateTime())).replace(" ", ""));
        pointNum = data.getPointNum();
        mSupportNumTv.setText(pointNum + "赞");
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
        isPoint = data.getIsPoint();
        if (CommonUtil.isLogined()) {
            if (isPoint == 1) {
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
                mSupportNumTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_333333));
                mSupportBt.setEnabled(true);
                mSupportBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickAgree(1, postId);
                        heartLayout.addFavor();
                    }
                });
            }
        } else {
            mSupportBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.showUnLoginDialog(getSupportFragmentManager(), mContext, R.string
                            .unlogin_msg);
                }
            });

        }

        String postContent = data.getPostContent();
        Log.d(TAG, "postContent: " + postContent);
        String replaced = postContent.replace("<br />", "");
        Spanned spanned = Html.fromHtml(replaced, new HtmlImageGetter(), null);
        mDescTv.setText(spanned);
    }

    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        setIntent(intent);
        postId = getIntent().getIntExtra(KeyConstant.id, 0);
        Log.d(TAG, "onNewIntent: " + postId);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "重启onRestart: " + postId);
        MsgDetailBodyBean bodyBean = new MsgDetailBodyBean();
        String userCode = App.userCode;
        bodyBean.setUserCode(userCode);
        bodyBean.setPostId(postId);
        bodyBean.setAppTypeId(Constant.APP_TYPE_ID_0_ANDROID);
    }

    /**
     * 重写图片加载接口
     *
     * @author Ruffian
     * @date 2016年1月15日
     */
    class HtmlImageGetter implements Html.ImageGetter {

        /**
         * 获取图片
         */
        @Override
        public Drawable getDrawable(String source) {
            LevelListDrawable d = new LevelListDrawable();
            Drawable empty = getResources().getDrawable(
                    R.color.transparent);
            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, ImageUtil.getScreenWidth(mContext),
                    300);
            new LoadImage().execute(source, d);
            return d;
        }

        /**
         * 异步下载图片类
         *
         * @author Ruffian
         * @date 2016年1月15日
         */
        class LoadImage extends AsyncTask<Object, Void, Bitmap> {

            private LevelListDrawable mDrawable;

            @Override
            protected Bitmap doInBackground(Object... params) {
                String source = (String) params[0];
                mDrawable = (LevelListDrawable) params[1];
                try {
                    InputStream is = new URL(source).openStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        options.inSampleSize = 2;
                    }
                    return BitmapFactory.decodeStream(is, null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
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
                                                        LevelListDrawable drawable, int oldWidth,
                                                        int oldHeight) {
                LevelListDrawable newDrawable = drawable;
                long newHeight = 0;// 未知数
                int newWidth = ImageUtil.getScreenWidth(mContext) - getResources()
                        .getDimensionPixelOffset(R.dimen.dm040);
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
        String userCode = App.userCode;
        bodyBean.setUserCode(userCode);
        bodyBean.setAppTypeId(Constant.APP_TYPE_ID_0_ANDROID);
        bodyBean.setPostId(id);  //帖子id

    }

    /**
     * 获取游戏详情
     */
    private void getData() {
        MsgDetailBodyBean bodyBean = new MsgDetailBodyBean();
        String userCode = App.userCode;
        bodyBean.setUserCode(userCode);
        bodyBean.setPostId(postId);
        bodyBean.setAppTypeId(Constant.APP_TYPE_ID_0_ANDROID);

    }

    public void onHubItemBackClick(View view) {
        finish();
    }
}
