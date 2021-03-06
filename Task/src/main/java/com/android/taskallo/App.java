package com.android.taskallo;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.android.taskallo.bean.User;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.Log;
import com.android.taskallo.core.utils.SPUtils;
import com.android.taskallo.exception.GlobalExceptionHandler;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * 全局设置
 *
 * @author flan
 * @since 2016/5/6
 */
public class App extends Application {

    public static RequestQueue requestQueue;

    public static String phone = "";   //电话
    public static String nickName = "";   //用户名
    public static String passWord = "";   //用户密码
    public static String userHeadUrl = "";   //用户头像URL
    public static String email = "";   //邮箱
    public static String token = "";
    public static User user;
    public static String deviceId = "";  //设备id

    public static int net_status = 0;

    public static boolean isDeleteApk = false;
    public static boolean allowAnyNet = false;
    public static boolean isReceiveMsg = false;
    public static String userCode;
    public static String loginType;

    @Override
    public void onCreate() {
        super.onCreate();
        token = (String) SPUtils.get(this, Constant.CONFIG_TOKEN, "");
        userHeadUrl = (String) SPUtils.get(this, Constant.CONFIG_HEAD_PHONE, "");
        phone = (String) SPUtils.get(this, Constant.CONFIG_USER_PHONE, "");
        email = (String) SPUtils.get(this, Constant.CONFIG_USER_EMAIL, "");
        nickName = (String) SPUtils.get(this, Constant.CONFIG_NICK_NAME, "");
        userCode = (String) SPUtils.get(this, Constant.CONFIG_USER_CODE, "");
        passWord = (String) SPUtils.get(this, Constant.CONFIG_USER_PWD, "");
        loginType = (String) SPUtils.get(this, Constant.CONFIG_LOGIN_TYPE, "1");

        isReceiveMsg = (boolean) SPUtils.get(this, Constant.CFG_RECEIVE_MSG, true);
        allowAnyNet = (boolean) SPUtils.get(this, Constant.CFG_ALLOW_4G_LOAD, false);
        isDeleteApk = (boolean) SPUtils.get(this, Constant.CFG_DELETE_APK, false);

        //设置全局异常处理器
        GlobalExceptionHandler globalExceptionHandler = GlobalExceptionHandler.getInstance();
        globalExceptionHandler.init(getApplicationContext());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Fresco.initialize(this);
        checkNetState();    //获取当前网络状态
        initGloalData();


        //配置友盟
        UMShareAPI.get(this);
        initUmengKey();
        Log.setLevel(Log.DEBUG);    //设置Log打印级别
        android.util.Log.d("app修改", phone + ",密码:" + passWord + ",启动：" + token);
        Config.DEBUG = true;

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }

    private void initUmengKey() {
        PlatformConfig.setWeixin("wxd60a30dd49d09626", "0525fb7c7c0dce4aaeba06cf02442605");
        PlatformConfig.setQQZone("1105610048", "cRaNEPSsHj95Ay9p");
        PlatformConfig.setSinaWeibo("3533498134", "28073913cd2521023ef4fcd9bdde88bf", "http://www" +
                ".ngame.cn/views/platform.html");

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void checkNetState() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiInfo.isConnected()) {
            net_status = Constant.NET_STATUS_WIFI;
        } else if (mobileInfo.isConnected()) {
            net_status = Constant.NET_STATUS_4G;
        } else {
            net_status = Constant.NET_STATUS_DISCONNECT;
        }
    }

    public void initGloalData() {
        // 初始化缓存目录
//        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(),
// "JZT/Imageloader/Cache");
        // Imageloader的配置信息
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)

                .memoryCacheExtraOptions(480, 800)// max width, max height，即保存的每个缓存文件的最大长宽

                .threadPoolSize(20)// 线程池内加载的数量

                .threadPriority(Thread.NORM_PRIORITY - 2) // 降低线程的优先级保证主UI线程不受太大影响

                .denyCacheImageMultipleSizesInMemory()

                .memoryCache(new LruMemoryCache(5 * 1024 * 1024)) // 建议内存设在5-10M,
                // 可以有比较好的表现UsingFreqLimitedMemoryCache
                // implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(5 * 1024 * 1024)

                .discCacheSize(520 * 1024 * 1024)

                .discCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5 加密

                .tasksProcessingOrder(QueueProcessingType.LIFO)

                .discCacheFileCount(100)// 缓存的文件数量
                // .discCache(new UnlimitedDiscCache(cacheDir))
                // 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())

                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) //
                // connectTimeout(5s),readTimeout(30 s)超时时间

                .writeDebugLogs() // Remove for release app
                .build();// 开始构建
        // Initialize ImageLoader with configuration.
        // 初始化ImageLoader
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("", "登陆 重启: ");
        MultiDex.install(this);
    }
}
