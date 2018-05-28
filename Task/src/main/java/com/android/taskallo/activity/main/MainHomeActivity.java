package com.android.taskallo.activity.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.activity.classify.Fragment0;
import com.android.taskallo.activity.rank.RankFragment;
import com.android.taskallo.adapter.FragmentViewPagerAdapter;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.User;
import com.android.taskallo.bean.VersionInfo;
import com.android.taskallo.core.fileload.FileLoadManager;
import com.android.taskallo.core.fileload.FileLoadService;
import com.android.taskallo.core.fileload.GameFileStatus;
import com.android.taskallo.core.fileload.IFileLoad;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.AppInstallHelper;
import com.android.taskallo.core.utils.CommonUtil;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.DataCleanManager;
import com.android.taskallo.core.utils.DialogHelper;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.Log;
import com.android.taskallo.core.utils.LoginHelper;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.core.utils.UMEventNameConstant;
import com.android.taskallo.exception.NoSDCardException;
import com.android.taskallo.fragment.SimpleDialogFragment;
import com.android.taskallo.push.model.PushMessage;
import com.android.taskallo.push.view.MessageDetailActivity;
import com.android.taskallo.push.view.MsgListFragment;
import com.android.taskallo.push.view.NotifyMsgDetailActivity;
import com.android.taskallo.search.view.SearchActivity;
import com.android.taskallo.user.view.ChangePwdActivity;
import com.android.taskallo.user.view.SendBindCodeActivity;
import com.android.taskallo.user.view.LoginActivity;
import com.android.taskallo.user.view.UserCenterActivity;
import com.android.taskallo.util.ToastUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 首页底部tab栏
 * Created by gp on 2017/3/14 0014.
 */
@SuppressLint("WrongConstant")
public class MainHomeActivity extends BaseFgActivity implements View.OnClickListener {
    public String TAG = MainHomeActivity.class.getSimpleName();
    public static MainHomeActivity context;
    private boolean isExit = false;     //是否安装后第一次启动
    private IFileLoad fileLoad;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private NumberProgressBar progressBar;
    private RemoteViews remoteViews = null;
    private Notification notification = null;
    private NotificationManager mNotificationManager = null;
    private VersionInfo versionInfo = null;
    private boolean isRunningBackground = false;
    private boolean isDownloading = false;
    private boolean isChecking = false;
    private RecommendFragment recommendFragment;
    private RankFragment rankingFragment;
    private Fragment0 fragment0;
    private int currentMenu;
    private FragmentViewPagerAdapter adapter;
    private FragmentManager fragmentManager;
    private LinearLayout video, manager;
    private RelativeLayout menu_game_hub;
    private Button bt_game, bt_video, bt_manager;
    private TextView tv_video, tv_manager, mEditProfileTv, tv_notifi_num,
            menu_gamehub_tv, mTitleTv;
    private int colorDark;
    private int colorNormal;
    private String imgUrl;
    private List<Fragment> mfragmentlist = new ArrayList<>();
    private int rbIndex;
    private ImageView im_toSearch, mEditBt;
    private ImageButton fl_notifi;
    private SimpleDraweeView mIconIv;
    private String pwd;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button menu_game_hub_bt;
    private ScrollView mMeLayout;
    private String mToken = "";
    private TextView mNameTv;
    private TextView mPhoneTv;
    private TextView mEmailTv;
    private TextView tvClear;
    private MsgListFragment msgFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(this, FileLoadService.class);
        startService(serviceIntent);
        setContentView(R.layout.activity_main_home);
        context = this;
        initStatusBar();
        preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        //home = (LinearLayout) findViewById(R.id.main_tab_0);
        //game = (LinearLayout) findViewById(R.id.menu_game_ll);
        menu_game_hub = (RelativeLayout) findViewById(R.id.main_tab_2);
        mMeLayout = (ScrollView) findViewById(R.id.main_me_layout);
        video = (LinearLayout) findViewById(R.id.main_tab_1);
        manager = (LinearLayout) findViewById(R.id.main_tab_3);

        //bt_home = (Button) findViewById(R.id.menu_home_bt1);
        //bt_game = (Button) findViewById(R.id.menu_game_bt);
        bt_video = (Button) findViewById(R.id.menu_video_bt);
        bt_manager = (Button) findViewById(R.id.menu_manager_bt);

        //tv_home = (TextView) findViewById(R.id.menu_home_tv);
        //tv_game = (TextView) findViewById(R.id.menu_game_tv);
        menu_gamehub_tv = (TextView) findViewById(R.id.menu_gamehub_tv);
        tv_video = (TextView) findViewById(R.id.menu_video_tv);
        tv_manager = (TextView) findViewById(R.id.menu_manager_tv);
        menu_game_hub_bt = (Button) findViewById(R.id.menu_game_hub_bt);

        //标题上面的消息和搜索
        im_toSearch = (ImageView) findViewById(R.id.im_toSearch);
        fl_notifi = (ImageButton) findViewById(R.id.main_top_add_bt);
        tv_notifi_num = (TextView) findViewById(R.id.tv_notifi_num); //右上角消息数目

        mIconIv = (SimpleDraweeView) findViewById(R.id.iv_icon_title);
        mNameTv = (TextView) findViewById(R.id.me_user_name_tv);
        mPhoneTv = (TextView) findViewById(R.id.me_user_phone_tv);
        mEmailTv = (TextView) findViewById(R.id.me_user_email_tv);

        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mEditBt = (ImageView) findViewById(R.id.main_profile_edit_bt);
        im_toSearch.setOnClickListener(this);
        fl_notifi.setOnClickListener(this);
        mEditBt.setOnClickListener(this);
        mIconIv.setOnClickListener(this);
        mNameTv.setOnClickListener(this);

        colorDark = getResources().getColor(R.color.mainColor);
        colorNormal = getResources().getColor(R.color.color_333333);

//        init(viewPager, getSupportFragmentManager());
        fragmentManager = getSupportFragmentManager();
        setCurrentMenu(1);    //当前选中标签

        //home.setOnClickListener(mTabClickListener);
        //game.setOnTouchListener(listener);
        menu_game_hub.setOnClickListener(mTabClickListener);
        video.setOnClickListener(mTabClickListener);
        manager.setOnClickListener(mTabClickListener);

        pwd = App.passWord;
        getUserByToken();

        //如果用户没有主动退出，则重新登录
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtil.isEmpty(pwd)) {
                    LoginHelper loginHelper = new LoginHelper(context);
                    loginHelper.reLoadSP();
                }
            }
        }).start();

        //申请SD卡读写权限
        CommonUtil.verifyStoragePermissions(this);

        //fileLoad = FileLoadManager.getInstance(this);
        //判断是否有新版本APP
        //checkUpdate();

    }

    /**
     * 获取用户信息
     */
    private void getUserByToken() {
        mToken = App.token;
        if (TextUtil.isEmpty(mToken)) {
            return;
        }
        String url = Constant.WEB_SITE + Constant.URL_GET_USER_BY_TOKEN;
        Response.Listener<JsonResult<User>> successListener = new Response
                .Listener<JsonResult<User>>() {
            @Override
            public void onResponse(JsonResult<User> result) {
                if (result == null) {
                    ToastUtil.show(context, "服务端异常");
                    return;
                }
                Log.d(TAG, "请求" + mToken);
                if (result.code == 0 && result.data != null) {
                    User mUser = result.data;
                    App.user = mUser;
                    setMeInfoData(mUser);
                } else {
                    Log.d(TAG, "HTTP请求成功：服务端返回错误：" + result.msg);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(context, "网络连接失败");
                android.util.Log.e(TAG, "请求失败: " + volleyError.getMessage());
            }
        };

        Request<JsonResult<User>> versionRequest = new GsonRequest<JsonResult<User>>(Request
                .Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult<User>>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.TOKEN, mToken);
                params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);

    }

    private void setMeInfoData(User mUser) {
        if (context != null && !context.isFinishing()) {
            App.userHeadUrl = mUser.headPortrait;
            App.nickName = mUser.nickName;
            App.phone = mUser.phoneNumber;
            App.email = mUser.email == null ? "" : mUser.email;
            mIconIv.setImageURI(mUser.headPortrait);
            mNameTv.setText(mUser.nickName);
            mPhoneTv.setText(mUser.phoneNumber);
            mEmailTv.setText(mUser.email);
        }
    }


    View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
              /*  case R.id.main_tab_0:
                    setCurrentMenu(0);
                    break;*/
                case R.id.main_tab_1:
                    setCurrentMenu(1);
                    break;
                case R.id.main_tab_2:
                    setCurrentMenu(2);
                    break;
                case R.id.main_tab_3:
                    setCurrentMenu(3);
                    break;
            }
        }
    };
    private int delayMillis = 100;
    View.OnClickListener mItemLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //修改密碼
                case R.id.main_me_item_change_pwd:
                    startActivity(new Intent(context, ChangePwdActivity.class));
                    break;

                //清除缓存
                case R.id.main_me_item_clean_cache:
                    cleanCache();
                    break;
                case R.id.main_tab_2:
                    break;
                case R.id.main_tab_3:
                    break;
            }
        }
    };

    //清除緩存
    private void cleanCache() {
        String text = tvClear.getText().toString();
        if ("0KB".equals(text)) {
            ToastUtil.show(context, "没有缓存了~");
            return;
        }

        if (text.endsWith("MB")) {
            delayMillis = 1000;
        } else if (text.endsWith("KB")) {
            delayMillis = 200;
        } else {
            delayMillis = 1000;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定清除缓存吗？");
        //    设置一个PositiveButton
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                DataCleanManager.clearAllCache(context);
                final DialogHelper dialogHelper = new DialogHelper
                        (getSupportFragmentManager(), context);
                dialogHelper.showAlert("清理中...", false);

                tvClear.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogHelper.hideAlert();
                        ToastUtil.show(context, "缓存已清除~");
                        if (null != tvClear) {
                            tvClear.setText("0KB");
                        }
                    }
                }, delayMillis);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //主界面顶部头像
        boolean isAvatarChanged = preferences.getBoolean(KeyConstant.AVATAR_HAS_CHANGED, false);
        if (isAvatarChanged) {
            android.util.Log.d(TAG, "onStart: 55");
            getUserByToken();
            editor.putBoolean(KeyConstant.AVATAR_HAS_CHANGED, false).apply();
        }
        //显示App缓存
        tvClear = (TextView) findViewById(R.id.me_item_tv_clear);
        try {
            String cacheSize = DataCleanManager.getTotalCacheSize(this);
            tvClear.setText(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.main_me_item_change_pwd).setOnClickListener(mItemLayoutClickListener);
        findViewById(R.id.main_me_item_clean_cache).setOnClickListener(mItemLayoutClickListener);
    }

    /**
     * 设置当前选中的菜单项
     *
     * @param currentMenu
     */
    public void setCurrentMenu(int currentMenu) {
        this.currentMenu = currentMenu;

        //bt_home.setSelected(false);
        //bt_game.setSelected(false);
        bt_video.setSelected(false);
        bt_manager.setSelected(false);
        menu_game_hub_bt.setSelected(false);

        //tv_home.setTextColor(colorNormal);
        //tv_game.setTextColor(colorNormal);
        menu_gamehub_tv.setTextColor(colorNormal);
        tv_video.setTextColor(colorNormal);
        tv_manager.setTextColor(colorNormal);

//        if (viewPager != null) {
//            viewPager.setCurrentItem(currentMenu);
//        }
//        switchFragment(currentMenu);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
      /*  if (null == recommendFragment) {
            recommendFragment = new RecommendFragment();
            transaction.add(R.id.main_list_fragments, recommendFragment);
        }*/
        if (null == fragment0) {
            fragment0 = new Fragment0();
            transaction.add(R.id.main_list_fragments, fragment0);
        }
        if (null == msgFragment) {
            //通知
            msgFragment = new MsgListFragment();
            Bundle bundleYG = new Bundle();
            bundleYG.putLong("labelId", PushMessage.MSG_TYPE_TZ);
            msgFragment.setArguments(bundleYG);
            transaction.add(R.id.main_list_fragments, msgFragment);
        }
      /*  if (null == managerFragment) {
            managerFragment = new ManagerFragment();
            transaction.add(R.id.main_list_fragments, managerFragment);
        }*/
        switch (currentMenu) {
         /*   case 0://推荐
                transaction.show(recommendFragment).hide(fragment0).hide(gameMainHubFragment)
                        .hide(managerFragment);
                recommendFragment.scroll2Top();
                recommendFragment.setShow(true);
                if (null != fragment0) {
                    fragment0.setShow(false);
                }
                //bt_home.setSelected(true);
                mTitleTv.setText(R.string.main_top_title_tab_1);
                fl_notifi.setVisibility(View.VISIBLE);
                im_toSearch.setVisibility(View.VISIBLE);
                mEditBt.setVisibility(View.GONE);
                mLikeBt.setVisibility(View.GONE);
                mHubBt.setVisibility(View.GONE);
                mRankBt.setVisibility(View.GONE);
                tv_home.setTextColor(colorDark);

                //埋点
                MobclickAgent.onEvent(context, UMEventNameConstant.mainRecommendButtonClickCount);

                break;*/
          /*  case 1://排行
                if (null == rankingFragment) {
                    rankingFragment = new RankFragment();
                    transaction.add(R.id.main_list_fragments, rankingFragment);
                } else {
                    transaction.show(rankingFragment);
                }
                if (null != fragment0) {
                    fragment0.setShow(false);
                }
                recommendFragment.setShow(false);
                bt_game.setSelected(true);
                mTitleTv.setText("排行榜");
                fl_notifi.setVisibility(View.GONE);
                im_toSearch.setVisibility(View.VISIBLE);
                mEditBt.setVisibility(View.GONE);
                mLikeBt.setVisibility(View.GONE);
                mHubBt.setVisibility(View.GONE);
                tv_game.setTextColor(colorDark);
                break;*/
            case 1://项目
                transaction.show(fragment0).hide(msgFragment);
                //recommendFragment.setShow(false);
                bt_video.setSelected(true);
                mTitleTv.setText(R.string.look_board);
                mMeLayout.setVisibility(View.GONE);
                mIconIv.setVisibility(View.GONE);
                fl_notifi.setVisibility(View.VISIBLE);
                im_toSearch.setVisibility(View.VISIBLE);
                tv_video.setTextColor(colorDark);
                MobclickAgent.onEvent(context, UMEventNameConstant.mainDiscoverButtonClickCount);
                break;
            case 2://通知
                transaction.show(msgFragment).hide(fragment0);
                menu_game_hub_bt.setSelected(true);
                mTitleTv.setText(R.string.main_bottom_tab_01);
                fl_notifi.setVisibility(View.GONE);
                mIconIv.setVisibility(View.GONE);
                mMeLayout.setVisibility(View.GONE);
                im_toSearch.setVisibility(View.GONE);
                menu_gamehub_tv.setTextColor(colorDark);
                MobclickAgent.onEvent(context, UMEventNameConstant.mainCircleButtonClickCount);
                break;


            case 3://管理
                transaction.hide(msgFragment).hide(fragment0);
                //recommendFragment.setShow(false);
                bt_manager.setSelected(true);
                mTitleTv.setText("");
                mMeLayout.setVisibility(View.VISIBLE);
                mIconIv.setVisibility(View.VISIBLE);
                im_toSearch.setVisibility(View.GONE);
                fl_notifi.setVisibility(View.GONE);
                tv_manager.setTextColor(colorDark);
                MobclickAgent.onEvent(context, UMEventNameConstant.mainManagerButtonClickCount);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //判断App是否从通知栏消息进来，如果是，直接启动消息详情页
        long pushMsgId = intent.getLongExtra("msgId", 0);
        int pushMsgType = intent.getIntExtra("type", 0);
        //Log.e(TAG,"id "+pushMsgId +" type "+pushMsgType);
        if (pushMsgId > 0 && pushMsgType > 0) {
            if (pushMsgType == PushMessage.MSG_TYPE_HD) {

                Intent msgIntent = new Intent(this, MessageDetailActivity.class);
                msgIntent.putExtra("msgId", pushMsgId);
                msgIntent.putExtra("type", pushMsgType);
                startActivity(msgIntent);

            } else if (pushMsgType == PushMessage.MSG_TYPE_TZ) {

                PushMessage msg = (PushMessage) intent.getSerializableExtra("msg");
                Intent msgIntent = new Intent(this, NotifyMsgDetailActivity.class);
                msgIntent.putExtra("msg", msg);
                startActivity(msgIntent);
            }

        }
    }

    /**
     * 处理按钮点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case but_search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case but_msg:
                Intent msgIntent = new Intent(this, PushMessageActivity.class);
                startActivity(msgIntent);
                break;*/
            case R.id.im_toSearch:
                startActivity(new Intent(context, SearchActivity.class));
                break;
            case R.id.main_top_add_bt:

                break;
            case R.id.iv_icon_title:
            case R.id.me_user_name_tv:
            case R.id.main_profile_edit_bt:
                startActivity(new Intent(context, UserCenterActivity.class));
                break;
        }
    }


    @Override
    protected void onDestroy() {
        //关闭后台服务
        stopService(new Intent(this, FileLoadService.class));
        //关闭OTA升级服务
        super.onDestroy();
        MobclickAgent.onKillProcess(context);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    /**
     * 双击退出程序
     */
    private void exitBy2Click() {
        if (!isExit) {
            isExit = true;
            ToastUtil.show(context, "再点一次退出");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            FileLoadManager manager = FileLoadManager.getInstance(this);
            manager.destroy();
            finish();
        }
    }


    /**
     * 检测是否有更新
     */
    private void checkUpdate() {
        if (App.net_status != Constant.NET_STATUS_WIFI) {
            return;
        }
        if (isChecking) {
            return;
        }
        if (isDownloading) {
            if (isRunningBackground) {
                //Toast.makeText(context, "更新！", Toast.LENGTH_SHORT).show();
            } else {
                showProgressDialog();
            }
            return;
        }

        String url = Constant.WEB_SITE + Constant.URL_APP_UPDATE;
        Response.Listener<JsonResult<VersionInfo>> successListener = new Response
                .Listener<JsonResult<VersionInfo>>() {
            @Override
            public void onResponse(JsonResult<VersionInfo> result) {

                if (result == null || result.code != 0) {
                    isChecking = false;
                    return;
                }

                versionInfo = result.data;
                if (versionInfo != null) {

                    //如果后台正在升级，则直接显示进度框
                    GameFileStatus downloadFileInfo = fileLoad.getGameFileLoadStatus(versionInfo
                                    .fileName, versionInfo.url,
                            versionInfo.packageName, versionInfo.versionCode);
                    if (downloadFileInfo != null) {
                        if (downloadFileInfo.getStatus() == GameFileStatus.STATE_DOWNLOAD ||
                                downloadFileInfo.getStatus() ==
                                        GameFileStatus.STATE_PAUSE) {

                            showProgressDialog();
                            doUpdateUi();
                            isChecking = false;
                            return;
                        }
                    }

                    //判读是否需要更新
                    int localVersion = CommonUtil.getVersionCode(context);
                    android.util.Log.d(TAG, "版本:" + localVersion);
                    if (localVersion < versionInfo.versionCode) {
                        showUpdateDialog();
                        CommonUtil.verifyStoragePermissions(context); //申请读写SD卡权限
                    } else {
                        //Toast.makeText(MainHomeActivity.this,"当前已是最新版本",Toast.LENGTH_SHORT)
                        // .show();
                    }
                } else {
                    //Toast.makeText(MainHomeActivity.this,"检测失败：服务端异常！",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "HTTP请求成功：服务端返回错误！");
                }
                isChecking = false;
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.d(TAG, "HTTP请求失败：网络连接错误！");
                isChecking = false;
            }
        };

        Request<JsonResult<VersionInfo>> versionRequest = new
                GsonRequest<JsonResult<VersionInfo>>(Request.Method.POST, url,
                        successListener, errorListener, new TypeToken<JsonResult<VersionInfo>>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("appType", "0");
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
        isChecking = true;
    }

    /**
     * 显示更新对话框
     */
    private void showUpdateDialog() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("updateDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setCancelable(false);
        dialogFragment.setDialogWidth(260);


        LayoutInflater inflater = getLayoutInflater();
        LinearLayout contentView = (LinearLayout) inflater.inflate(R.layout.layout_dialog_update,
                null);
        TextView tv_title = (TextView) contentView.findViewById(R.id.tv_title);

        String fileSizeStr = Formatter.formatFileSize(context, versionInfo.fileSize);

        tv_title.setText("有新版本：V" + versionInfo.versionName + "（" + fileSizeStr + "）");
        TextView tv_summary = (TextView) contentView.findViewById(R.id.tv_summary);
        tv_summary.setText(versionInfo.content);

        dialogFragment.setContentView(contentView);

        dialogFragment.setNegativeButton(R.string.update_now, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();

                fileLoad.load(versionInfo.fileName, versionInfo.url, versionInfo.md5, versionInfo
                        .packageName, versionInfo
                        .versionCode, versionInfo.fileName, versionInfo.url, versionInfo.id, false);
                showProgressDialog();   //显示进度条对话框
                doUpdateUi();           //启动更新进度条线程
            }
        });
        dialogFragment.show(ft, "updateDialog");
    }

    /**
     * 显示下载进度的对话框
     */
    private void showProgressDialog() {

        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("progressDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();

        dialogFragment.setCancelable(false);
        dialogFragment.setDialogWidth(255);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout contentView = (LinearLayout) inflater.inflate(R.layout
                .layout_dialog_download, null);
        progressBar = (NumberProgressBar) contentView.findViewById(R.id.progress_bar);

        dialogFragment.setContentView(contentView);

        dialogFragment.setPositiveButton("关闭", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();

                fileLoad.delete(versionInfo.url);

                if (mNotificationManager != null) {
                    mNotificationManager.cancel(1);
                }
                isDownloading = false;
                finish();
            }
        });

       /* dialogFragment.setNegativeButton(R.string.update_background, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogFragment.dismiss();

                remoteViews = new RemoteViews(getPackageName(), R.layout
                .layout_notification_download);
                notification = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContent(remoteViews)
                        .build();
                mNotificationManager = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, notification);
                isRunningBackground = true;

            }
        });*/
        dialogFragment.show(ft, "progressDialog");

    }

    private void doUpdateUi() {
        isDownloading = true;
        //执行更新进度条的操作
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isDownloading) {
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        GameFileStatus downloadFileInfo = fileLoad.getGameFileLoadStatus
                                (versionInfo.fileName, versionInfo.url,
                                        versionInfo.packageName, versionInfo.versionCode);
                        if (downloadFileInfo != null) {

                            double finished = downloadFileInfo.getFinished();
                            double length = downloadFileInfo.getLength();
                            final double process = finished / length * 100;

                            if (isRunningBackground) {
                                remoteViews.setProgressBar(R.id.progress_bar, 100, (int) process,
                                        false);
                                if (process >= 100) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);


                                    File filePath = null;
                                    try {
                                        filePath = new File(CommonUtil.getFileLoadBasePath(),
                                                versionInfo.fileName);
                                    } catch (NoSDCardException e) {
                                        e.printStackTrace();
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        String packageName = context.getApplicationContext()
                                                .getPackageName();
                                        String authority = new StringBuilder(packageName).append
                                                (".provider").toString();
                                        Uri contentUri = FileProvider.getUriForFile(context,
                                                authority, filePath);
                                        intent.setDataAndType(contentUri, "application/vnd" +
                                                ".android.package-archive");
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(filePath),
                                                "application/vnd.android.package-archive");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    }

                                    remoteViews.setTextViewText(R.id.text1, "下载完成");
                                    notification = new Notification.Builder(context)
                                            .setSmallIcon(R.drawable.ic_launcher)
                                            .setContent(remoteViews)
                                            .setContentIntent(PendingIntent.getActivity(context,
                                                    0, intent, 0))
                                            .build();
                                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                                    isRunningBackground = false;
                                } else {
                                    remoteViews.setTextViewText(R.id.text1, "正在下载: " + (int)
                                            process + "%");
                                }
                                mNotificationManager.notify(1, notification);
                            } else {
                                if (progressBar != null) {
                                    progressBar.setProgress((int) process);
                                }
                                if (process == 100) {
                                    //处理安装App的操作
                                    FragmentTransaction ft = getSupportFragmentManager()
                                            .beginTransaction();
                                    SimpleDialogFragment prev = (SimpleDialogFragment)
                                            getSupportFragmentManager()
                                                    .findFragmentByTag("downloadDialog");
                                    if (prev != null) {
                                        ft.remove(prev);
                                    }
                                    ft.commit();
                                    isRunningBackground = false;
                                    AppInstallHelper.installApk(context, versionInfo.fileName);
                                }
                            }

                            if (process >= 100) {
                                isDownloading = false;
                            }
                        }
                    }
                });
            }
        }, 0, 500);
    }


    public void onMeTopPhoneClick(View view) {
        Intent intent = new Intent(context, SendBindCodeActivity.class);
        intent.putExtra(KeyConstant.EDIT_TYPE, Constant.PHONE);
        startActivity(intent);
    }

    public void onMeTopEmailClick(View view) {
        Intent intent = new Intent(context, SendBindCodeActivity.class);
        intent.putExtra(KeyConstant.EDIT_TYPE, Constant.EMAIL);
        startActivity(intent);
    }

    /**
     * 处理退出操作
     */
    public void onLogoutClick(View view) {
        showLogoutDialog();
    }

    //退出登录
    public void showLogoutDialog() {

        final Dialog dialog = new Dialog(context, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_logout, null);

        inflate.findViewById(R.id.logout_yes_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                logoutClearData();
                startActivity(new Intent(context, LoginActivity.class));
                context.finish();
            }
        });
        inflate.findViewById(R.id.logout_cancel_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(inflate);//将布局设置给Dialog

        setDialogWindow(dialog);
    }

    private void setDialogWindow(Dialog dialog) {
        Window dialogWindow = dialog.getWindow(); //获取当前Activity所在的窗体
        dialogWindow.setGravity(Gravity.BOTTOM);//设置Dialog从窗体底部弹出
        WindowManager.LayoutParams params = dialogWindow.getAttributes();   //获得窗体的属性
        //params.y = 20;  Dialog距离底部的距离
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置Dialog距离底部的距离
        dialogWindow.setAttributes(params); //将属性设置给窗体
        dialog.show();//显示对话框
    }

    //退出登录
    private void logoutClearData() {
        editor.putString(Constant.CONFIG_USER_PWD, "");
        editor.putString(Constant.CONFIG_TOKEN, "");
        editor.putString(Constant.CONFIG_LOGIN_TYPE, Constant.PHONE);
        editor.putBoolean(KeyConstant.AVATAR_HAS_CHANGED, true);
        editor.apply();

        App.userHeadUrl = "";
        App.loginType = Constant.PHONE;
        App.nickName = "";
        App.userCode = "";
        App.phone = "";
        App.passWord = "";
        App.token = "";
        App.user = null;
    }
}
