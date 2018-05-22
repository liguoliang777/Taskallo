package com.android.taskallo.activity.main;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.taskallo.user.view.UserCenterActivity;
import com.android.taskallo.util.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.activity.classify.ClassifyFragment;
import com.android.taskallo.activity.hub.HubPostsActivity;
import com.android.taskallo.activity.manager.DownloadCenterActivity;
import com.android.taskallo.activity.manager.ManagerFragment;
import com.android.taskallo.activity.rank.RankActivity;
import com.android.taskallo.activity.rank.RankFragment;
import com.android.taskallo.activity.sm.NecessaryOrLikeActivity;
import com.android.taskallo.adapter.FragmentViewPagerAdapter;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.VersionInfo;
import com.android.taskallo.core.fileload.FileLoadManager;
import com.android.taskallo.core.fileload.FileLoadService;
import com.android.taskallo.core.fileload.GameFileStatus;
import com.android.taskallo.core.fileload.IFileLoad;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.AppInstallHelper;
import com.android.taskallo.core.utils.CommonUtil;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.Log;
import com.android.taskallo.core.utils.LoginHelper;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.core.utils.UMEventNameConstant;
import com.android.taskallo.exception.NoSDCardException;
import com.android.taskallo.fragment.SimpleDialogFragment;
import com.android.taskallo.push.model.PushMessage;
import com.android.taskallo.push.view.MessageDetailActivity;
import com.android.taskallo.push.view.MsgCenterActivity;
import com.android.taskallo.push.view.NotifyMsgDetailActivity;
import com.android.taskallo.search.view.SearchActivity;
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
    private ClassifyFragment classifyFragment;
    private ManagerFragment managerFragment;
    private int currentMenu;
    private FragmentViewPagerAdapter adapter;
    private FragmentManager fragmentManager;
    private LinearLayout home, menu_game_hub, video, manager;
    private Button bt_home, bt_game, bt_video, bt_manager;
    private TextView tv_home, tv_video, tv_manager, mEditProfileTv, tv_notifi_num,
            menu_gamehub_tv, mTitleTv;
    private int colorDark;
    private int colorNormal;
    private String imgUrl;
    private List<Fragment> mfragmentlist = new ArrayList<>();
    private int rbIndex;
    private ImageView im_toSearch, mRankBt, mDownloadBt, mLikeBt, mHubBt;
    private FrameLayout fl_notifi;
    private SimpleDraweeView mIconIv;
    private String pwd;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button menu_game_hub_bt;
    private MainHubFragment gameMainHubFragment;

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
        home = (LinearLayout) findViewById(R.id.main_tab_0);
        //game = (LinearLayout) findViewById(R.id.menu_game_ll);
        menu_game_hub = (LinearLayout) findViewById(R.id.main_tab_2);
        video = (LinearLayout) findViewById(R.id.main_tab_1);
        manager = (LinearLayout) findViewById(R.id.main_tab_3);

        bt_home = (Button) findViewById(R.id.menu_home_bt1);
        //bt_game = (Button) findViewById(R.id.menu_game_bt);
        bt_video = (Button) findViewById(R.id.menu_video_bt);
        bt_manager = (Button) findViewById(R.id.menu_manager_bt);

        tv_home = (TextView) findViewById(R.id.menu_home_tv);
        //tv_game = (TextView) findViewById(R.id.menu_game_tv);
        menu_gamehub_tv = (TextView) findViewById(R.id.menu_gamehub_tv);
        tv_video = (TextView) findViewById(R.id.menu_video_tv);
        tv_manager = (TextView) findViewById(R.id.menu_manager_tv);
        menu_game_hub_bt = (Button) findViewById(R.id.menu_game_hub_bt);

        //标题上面的消息和搜索
        im_toSearch = (ImageView) findViewById(R.id.im_toSearch);
        fl_notifi = (FrameLayout) findViewById(R.id.fl_notifi);
        tv_notifi_num = (TextView) findViewById(R.id.tv_notifi_num); //右上角消息数目

        mIconIv = (SimpleDraweeView) findViewById(R.id.iv_icon_title);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mDownloadBt = (ImageView) findViewById(R.id.main_download_bt);
        mLikeBt = (ImageView) findViewById(R.id.main_like_bt);
        mHubBt = (ImageView) findViewById(R.id.main_hub_bt);
        mRankBt = (ImageView) findViewById(R.id.main_rank_bt);
        im_toSearch.setOnClickListener(this);
        fl_notifi.setOnClickListener(this);
        mIconIv.setOnClickListener(this);
        mDownloadBt.setOnClickListener(this);
        mLikeBt.setOnClickListener(this);
        mHubBt.setOnClickListener(this);
        mRankBt.setOnClickListener(this);

        colorDark = getResources().getColor(R.color.mainColor);
        colorNormal = getResources().getColor(R.color.color_333333);

//        init(viewPager, getSupportFragmentManager());
        fragmentManager = getSupportFragmentManager();
        setCurrentMenu(0);    //当前选中标签

        home.setOnClickListener(mTabClickListener);
        //game.setOnTouchListener(listener);
        menu_game_hub.setOnClickListener(mTabClickListener);
        video.setOnClickListener(mTabClickListener);
        manager.setOnClickListener(mTabClickListener);

        pwd = App.passWord;
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

        fileLoad = FileLoadManager.getInstance(this);
        //判断是否有新版本APP
        //checkUpdate();

    }


    View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_tab_0:
                    setCurrentMenu(0);
                    break;
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
        boolean isAvatarChanged = preferences.getBoolean(KeyConstant.AVATAR_HAS_CHANGED, true);
        if (isAvatarChanged) {
        }

    }

    /**
     * 设置当前选中的菜单项
     *
     * @param currentMenu
     */
    public void setCurrentMenu(int currentMenu) {
        this.currentMenu = currentMenu;

        bt_home.setSelected(false);
        //bt_game.setSelected(false);
        bt_video.setSelected(false);
        bt_manager.setSelected(false);
        menu_game_hub_bt.setSelected(false);

        tv_home.setTextColor(colorNormal);
        //tv_game.setTextColor(colorNormal);
        menu_gamehub_tv.setTextColor(colorNormal);
        tv_video.setTextColor(colorNormal);
        tv_manager.setTextColor(colorNormal);

//        if (viewPager != null) {
//            viewPager.setCurrentItem(currentMenu);
//        }
//        switchFragment(currentMenu);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (null == recommendFragment) {
            recommendFragment = new RecommendFragment();
            transaction.add(R.id.main_list_fragments, recommendFragment);
        }
        if (null == classifyFragment) {
            classifyFragment = new ClassifyFragment();
            transaction.add(R.id.main_list_fragments, classifyFragment);
        }
        if (null == gameMainHubFragment) {
            gameMainHubFragment = new MainHubFragment();
            transaction.add(R.id.main_list_fragments, gameMainHubFragment);
        }
        if (null == managerFragment) {
            managerFragment = new ManagerFragment();
            transaction.add(R.id.main_list_fragments, managerFragment);
        }
        switch (currentMenu) {
            case 0://推荐
                transaction.show(recommendFragment).hide(classifyFragment).hide(gameMainHubFragment)
                        .hide(managerFragment);
                recommendFragment.scroll2Top();
                recommendFragment.setShow(true);
                if (null != classifyFragment) {
                    classifyFragment.setShow(false);
                }
                bt_home.setSelected(true);
                mTitleTv.setText("热门游戏");
                fl_notifi.setVisibility(View.VISIBLE);
                im_toSearch.setVisibility(View.VISIBLE);
                mDownloadBt.setVisibility(View.GONE);
                mLikeBt.setVisibility(View.GONE);
                mHubBt.setVisibility(View.GONE);
                mRankBt.setVisibility(View.GONE);
                tv_home.setTextColor(colorDark);

                //埋点
                MobclickAgent.onEvent(context, UMEventNameConstant.mainRecommendButtonClickCount);

                break;
          /*  case 1://排行
                if (null == rankingFragment) {
                    rankingFragment = new RankFragment();
                    transaction.add(R.id.main_list_fragments, rankingFragment);
                } else {
                    transaction.show(rankingFragment);
                }
                if (null != classifyFragment) {
                    classifyFragment.setShow(false);
                }
                recommendFragment.setShow(false);
                bt_game.setSelected(true);
                mTitleTv.setText("排行榜");
                fl_notifi.setVisibility(View.GONE);
                im_toSearch.setVisibility(View.VISIBLE);
                mDownloadBt.setVisibility(View.GONE);
                mLikeBt.setVisibility(View.GONE);
                mHubBt.setVisibility(View.GONE);
                tv_game.setTextColor(colorDark);
                break;*/
            case 1://分类
                transaction.show(classifyFragment).hide(recommendFragment).hide(gameMainHubFragment)
                        .hide(managerFragment);
                classifyFragment.scroll2Top();
                classifyFragment.setShow(true);
                recommendFragment.setShow(false);
                bt_video.setSelected(true);
                mTitleTv.setText("分类");
                mDownloadBt.setVisibility(View.GONE);
                mLikeBt.setVisibility(View.GONE);
                mHubBt.setVisibility(View.GONE);
                fl_notifi.setVisibility(View.GONE);
                im_toSearch.setVisibility(View.VISIBLE);
                mRankBt.setVisibility(View.VISIBLE);
                tv_video.setTextColor(colorDark);
                MobclickAgent.onEvent(context, UMEventNameConstant.mainDiscoverButtonClickCount);
                break;
            case 2://圈子
                transaction.show(gameMainHubFragment).hide(recommendFragment).hide(classifyFragment)
                        .hide(managerFragment);
                menu_game_hub_bt.setSelected(true);
                mTitleTv.setText(R.string.main_tab_04);
                fl_notifi.setVisibility(View.GONE);
                mDownloadBt.setVisibility(View.GONE);
                mLikeBt.setVisibility(View.GONE);
                mHubBt.setVisibility(View.VISIBLE);
                mRankBt.setVisibility(View.GONE);
                im_toSearch.setVisibility(View.GONE);
                menu_gamehub_tv.setTextColor(colorDark);
                MobclickAgent.onEvent(context, UMEventNameConstant.mainCircleButtonClickCount);
                break;


            case 3://管理
                transaction.show(managerFragment).hide(recommendFragment).hide
                        (gameMainHubFragment).hide(classifyFragment);
                recommendFragment.setShow(false);
                if (null != classifyFragment) {
                    classifyFragment.setShow(false);
                }
                bt_manager.setSelected(true);
                mTitleTv.setText("管理");
                mDownloadBt.setVisibility(View.VISIBLE);
                mLikeBt.setVisibility(View.VISIBLE);
                mRankBt.setVisibility(View.GONE);
                im_toSearch.setVisibility(View.GONE);
                mHubBt.setVisibility(View.GONE);
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
            case R.id.fl_notifi:
                startActivity(new Intent(context, MsgCenterActivity.class));
                break;
            case R.id.main_download_bt:
                startActivity(new Intent(context, DownloadCenterActivity.class));
                break;
            case R.id.main_like_bt:
                Intent intent = new Intent(context, NecessaryOrLikeActivity.class);
                intent.putExtra(KeyConstant.likeOrNecessaryExtraKey, KeyConstant.EXTRA_LIKE);
                startActivity(intent);
                break;
            case R.id.main_hub_bt:
                startActivity(new Intent(context, HubPostsActivity.class));
                break;
            case R.id.main_rank_bt:
                startActivity(new Intent(context, RankActivity.class));
                MobclickAgent.onEvent(context, UMEventNameConstant.mainRankButtonClickCount);
                break;
            case R.id.iv_icon_title:
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


}
