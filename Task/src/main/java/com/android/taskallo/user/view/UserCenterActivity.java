package com.android.taskallo.user.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.CommonUtil;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.DialogHelper;
import com.android.taskallo.core.utils.FileUtil;
import com.android.taskallo.core.utils.ImageUtil;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.Log;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.exception.NoSDCardException;
import com.android.taskallo.fragment.OneBtDialogFragment;
import com.android.taskallo.util.ToastUtil;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户中心界面
 * Created by zeng on 2016/5/26.
 */
public class UserCenterActivity extends BaseFgActivity {

    public String TAG = UserCenterActivity.class.getSimpleName();
    private UserCenterActivity content;
    private String pwd;

    private SimpleDraweeView img_photo;
    private TextView mPhoneTv, mEmailTv;
    private EditText tv_nickname;

    private String nickName;

    private SharedPreferences preferences;
    private int REQUEST_CODE_CAPTURE_CAMERA = 1458;

    private String mCurrentPhotoPath;

    private File mTempDir;
    private String imgStrPost = "";
    private String avatarUrl;
    private String LOGIN_TYPE;
    private String IMG_TYPE = "-1";//0表示用户有图片地址  1 表示用户选的本地，base64字符串。
    private SharedPreferences.Editor editor;
    private ArrayAdapter<String> mAdapter;
    private Dialog defAvatarDialog;
    private FragmentManager fm;
    private Uri fileUri;
    private RelativeLayout imgPhotoLayout;
    private Button titleRightBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        this.setContentView(R.layout.activity_user_center);
        content = this;
        fm = getSupportFragmentManager();
        preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView centerTv = (TextView) findViewById(R.id.center_tv);
        centerTv.setText(R.string.me_profile);
        LOGIN_TYPE = App.loginType;

        try {
            mTempDir = new File(CommonUtil.getImageBasePath());
        } catch (NoSDCardException e) {
            e.printStackTrace();
        }
        if (mTempDir != null && !mTempDir.exists()) {
            mTempDir.mkdirs();
        }
        img_photo = (SimpleDraweeView) findViewById(R.id.img_photo);
        imgPhotoLayout = (RelativeLayout) findViewById(R.id.img_photo_layout);
        mPhoneTv = (TextView) findViewById(R.id.tv_phone);
        tv_nickname = (EditText) findViewById(R.id.tv_nickname);
        mEmailTv = (TextView) findViewById(R.id.profile_email_tv);

        imgPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改头像
                showChangeAvatarDialog();

            }
        });
        pwd = App.passWord;
        imgStrPost = App.userHeadUrl;
        nickName = App.nickName;

        img_photo.setImageURI(imgStrPost);
        mPhoneTv.setText(App.phone == null ? "" : App.phone);
        mEmailTv.setText(App.email == null ? "" : App.email);

        tv_nickname.setText(nickName);
        tv_nickname.setSelection(nickName.length());
     /*   if (pwd == null) {
            //getUserInfo();
            android.util.Log.d(TAG, "user == null");
            LoginHelper loginHelper = new LoginHelper(UserCenterActivity.this);
            loginHelper.reLogin();
            //setUserInfo();
        } else {
            setUserInfo();
        }*/


        //*/重新登录
     /*   LoginHelper loginHelper = new LoginHelper(this);
        loginHelper.reLogin();*/
        titleRightBt = (Button) findViewById(R.id.title_right_bt);
        titleRightBt.setText("保存");
        titleRightBt.setVisibility(View.VISIBLE);
        titleRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickNameStr = tv_nickname.getText().toString();
                if (nickNameStr.length() == 0) {
                    ToastUtil.show(content, "昵称为空哦！");
                    return;
                }
                if (nickNameStr.equals(nickName) && "-1".equals(IMG_TYPE)) {
                    ToastUtil.show(content, "您未修改任何资料哦");
                    //content.finish();
                } else {
                    nickName = nickNameStr;
                    titleRightBt.setClickable(false);
                    uploadImage();
                }
            }
        });

        //默认头像地址
        for (int i = 1; i < 21; i++) {
            if (i < 10) {
                mUrlList.add(UrlConstant.RECOMMED_URL_START + "0" + i + ".png");
            } else {
                mUrlList.add(UrlConstant.RECOMMED_URL_START + i + ".png");
            }
        }
        defAvatarDialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_recommend_avatar,
                null);
        GridView gridView = (GridView) inflate.findViewById(R.id.recommend_grid_view);
        gridView.setAdapter(new AvatarAdapter());
        defAvatarDialog.setContentView(inflate);//将布局设置给Dialog
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imgStrPost = mUrlList.get(position);
                img_photo.setImageURI(imgStrPost);
                IMG_TYPE = "0";
                defAvatarDialog.dismiss();
            }
        });
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            //StoreService.uploadImage(file);
            avatarUrl = uri.toString();
            img_photo.setImageURI(avatarUrl);

            String path = uri.getPath();
            //File file = new File(path);
            imgStrPost = ImageUtil.getImageStr(path);
            android.util.Log.d(TAG, path + "修改参数:图片地址:" + imgStrPost);
            IMG_TYPE = "1";
        } else if (resultCode == Crop.RESULT_ERROR) {
            //Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //修改头像
    public void showChangeAvatarDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_change_avatar,
                null);

        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                int id = v.getId();
                if (id == R.id.choose_local_tv) {//本地相册
                    Crop.pickImage(content);
                } else if (id == R.id.choose_camera_tv) {//相机
                    getImageFromCamera();
                } else if (id == R.id.choose_recomend_tv) {
                    //选择推荐头像
                    setDialogWindow(defAvatarDialog);
                }
            }
        };
        inflate.findViewById(R.id.choose_local_tv).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.choose_recomend_tv).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.choose_camera_tv).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.choose_cancel_tv).setOnClickListener(mDialogClickLstener);

        dialog.setContentView(inflate);//将布局设置给Dialog
        setDialogWindow(dialog);
    }

    private List<String> mUrlList = new ArrayList<>();

    public void onProfilePhoneBtClick(View view) {
        Intent intent = new Intent(content, SendBindCodeActivity.class);
        intent.putExtra(KeyConstant.EDIT_TYPE, Constant.PHONE);
        startActivity(intent);

    }

    public void onProfileEmailBtClick(View view) {
        Intent intent = new Intent(content, SendBindCodeActivity.class);
        intent.putExtra(KeyConstant.EDIT_TYPE, Constant.EMAIL);
        startActivity(intent);
    }

    //默认头像适配器
    public class AvatarAdapter extends BaseAdapter {
        public AvatarAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return mUrlList.size();
        }

        @Override
        public Object getItem(int position) {
            return mUrlList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            AvatarAdapter.ViewHolder holder;
            if (convertView == null) {
                holder = new AvatarAdapter.ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout
                        .gridview_image_view_item, null);
                holder.mIconIv = (SimpleDraweeView) convertView.findViewById(R.id
                        .recommend_icon_gv_iv);
                convertView.setTag(holder);
            } else {
                holder = (AvatarAdapter.ViewHolder) convertView.getTag();
            }
            final String uriString = mUrlList.get(position);
            holder.mIconIv.setImageURI(uriString);
            return convertView;
        }

        class ViewHolder {
            private SimpleDraweeView mIconIv;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                beginCrop(result.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, result);
            } else if (requestCode == REQUEST_CODE_CAPTURE_CAMERA) {
                if (fileUri != null) {
                    beginCrop(fileUri);
                }
            }
        }
    }

    private void beginCrop(Uri source) {
        String fileName = "Temp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File cropFile = new File(mTempDir, fileName);
        Uri outputUri = Uri.fromFile(cropFile);
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    protected void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = "Temp_camera" + String.valueOf(System.currentTimeMillis());
        File cropFile = new File(mTempDir, fileName);
        fileUri = FileUtil.getUriForFile(content, cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

        mCurrentPhotoPath = fileUri.getPath();
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);
    }


    private void uploadImage() {
        editor.putBoolean(KeyConstant.AVATAR_HAS_CHANGED, true).apply();
        android.util.Log.d(TAG, App.token + "修改 点击: " + App.userHeadUrl);

        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + Constant.URL_MODIFY_USER_DATA;

        Response.Listener<JsonResult> succesListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                int code = result.code;
                Log.d(TAG, "修改成功" + code);
                if (code == 0) {
                    String token = (String) result.data;
                    ToastUtil.show(content, "资料修改成功!");
                    editor.putString(Constant.CONFIG_TOKEN, token);
                    editor.putString(Constant.CONFIG_NICK_NAME, nickName);
                    editor.apply();

                    App.token = token;
                    App.nickName = nickName;
                    content.finish();
                } else if (code >= -4 && code <= -1) {
                          /*  android.util.Log.d(TAG, "ic_back: " + code + result.msg);
                            if (content != null && !content.isFinishing()) {
                                showReLoginDialog();
                            }*/
                    //需要重新登录
                    //logoutClearData();
                } else {
                    ToastUtil.show(content, "修改失败");
                    Log.d(TAG, "HTTP请求成功：修改失败！" + code + result.msg);
                }
                //隐藏提示框
                DialogHelper.hideWaiting(fm);
                titleRightBt.setClickable(true);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                titleRightBt.setClickable(true);
                DialogHelper.hideWaiting(fm);
                ToastUtil.show(content, getString(R.string.server_exception));
                Log.d(TAG, "修改" + volleyError.getCause());
            }
        };
        Request<JsonResult> versionRequest1 = new GsonRequest<JsonResult>(Request
                .Method.POST, url,
                succesListener, errorListener, new TypeToken<JsonResult>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() {
                //设置POST请求参数
                Log.d(TAG, imgStrPost + "修改参数," + App.token);
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.TOKEN, App.token);
                params.put(KeyConstant.NICK_NAME, nickName);
                params.put(KeyConstant.head_Portrait, imgStrPost + "");
                params.put(KeyConstant.APP_TYPE_ID, Constant.APP_TYPE_ID_0_ANDROID);  //
                return params;
            }
        };
        App.requestQueue.add(versionRequest1);

    }

    /**
     * 显示结果对话框
     */
    private void showReLoginDialog() {
        final OneBtDialogFragment dialogFragment = new OneBtDialogFragment();
        dialogFragment.setTitle(R.string.relogin_msg);
        dialogFragment.setDialogWidth(content.getResources().getDimensionPixelSize(R.dimen
                .unlogin_dialog_width));
        dialogFragment.setNegativeButton(R.string.login_now, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
                content.startActivity(new Intent(content, LoginActivity.class));
                content.finish();
            }
        });
        dialogFragment.show(fm, "successDialog");
    }


/* private void showChangeNicknameDialog() {

        final FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("progressDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();

        dialogFragment.setTitle("昵称修改");
        dialogFragment.setDialogWidth(250);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout contentView = (LinearLayout) inflater.inflate(R.layout.layout_dialog_edit,
        null);
        final EditText editText = (EditText) contentView.findViewById(R.id.et_content);
        dialogFragment.setContentView(contentView);

        dialogFragment.setPositiveButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
            }
        });

        dialogFragment.setNegativeButton(R.string.sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickName = editText.getText().toString();
                if (nickName.length() > 0) {
                    if (nickName.length() > 13) {
                        Toast.makeText(UserCenterActivity.this, "亲，您的昵称太长了！", Toast.LENGTH_SHORT)
                        .show();
                        return;
                    } else if (!TextUtil.isLegal(nickName.trim(), "[A-Za-z0-9\\u4e00-\\u9fa5_]+")) {
                        Toast.makeText(UserCenterActivity.this, "亲，只允许中文，英文，数字等字符哦！", Toast
                        .LENGTH_SHORT).show();
                        return;
                    }
                    dialogFragment.dismiss();
                    changeNickname();
                } else {
                    Toast.makeText(UserCenterActivity.this, "昵称不能为空哦！", Toast.LENGTH_SHORT).show();
                }


            }
        });
        dialogFragment.show(ft, "progressDialog");

    }*/
    /**
     * 修改昵称
     */
   /* private void changeNickname() {

        final String url = Constant.WEB_SITE + Constant.URL_CHANGE_NICKNAME;
        Response.Listener<JsonResult> successListener = new Response.Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    Toast.makeText(UserCenterActivity.this, "服务端异常", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (result.code == 0) {
                    if (nickName != null) {
                        nickName = nickName.length() > 10 ? nickName.substring(0, 10) : nickName;
                    }
                    tv_nickname.setText(nickName);
                    if (user != null) {
                        user.nickName = nickName;
                    }
                    if (user != null) {
                        user.nickName = nickName;
                        App.nickName = nickName;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constant.CONFIG_NICK_NAME, nickName);
                        editor.apply();
                    }
                    Toast.makeText(UserCenterActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "HTTP请求成功：服务端返回错误: " + result.msg);
                    Toast.makeText(UserCenterActivity.this, result.msg, Toast.LENGTH_SHORT).show();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(UserCenterActivity.this, "修改失败，请检查网络连接!");
                Log.d(TAG, "HTTP请求失败：网络连接错误！");
            }
        };

        Request<JsonResult> versionRequest = new GsonRequest<JsonResult>(Request.Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult>() {
        }.getType()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("email", App.email);
                params.put("userCode", App.userCode);
                params.put("newNickName", nickName);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }*/

    /*   *//**
     * 获取用户信息
     *//*
    private void getUserInfo() {
        final String url = Constant.WEB_SITE + Constant.URL_USER_INFO;
        Response.Listener<JsonResult<User>> successListener = new Response
        .Listener<JsonResult<User>>() {
            @Override
            public void onResponse(JsonResult<User> result) {
                if (result == null) {
                    Toast.makeText(UserCenterActivity.this, "服务端异常", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (result.code == 0 && result.data != null) {
                    user = result.data;
                    user = user;
                    String userHeadPhoto = user.headPortrait;
                    if (userHeadPhoto != null && App.userHeadUrl != null &&
                            !App.userHeadUrl.equals(userHeadPhoto)) {
                        App.userHeadUrl = userHeadPhoto;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constant.CONFIG_HEAD_PHONE, userHeadPhoto);
                        editor.apply();
                    } else {
                        App.userHeadUrl = userHeadPhoto;
                    }

                    nickName = user.nickName;
                    if (nickName != null) {
                        nickName = nickName.length() > 10 ? nickName.substring(0, 10) : nickName;
                    }
                    tv_nickname.setText(nickName);
                    tv_account.setText(user.phoneNumber);
                    imageLoader.displayImage(App.userHeadUrl, img_photo, roundOptions);

                } else {
                    Log.d(TAG, "HTTP请求成功：服务端返回错误: " + result.msg);
                    Toast.makeText(UserCenterActivity.this, "服务端异常，请重新登录！", Toast.LENGTH_SHORT)
                    .show();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(UserCenterActivity.this, "加载用户信息，请检查网络连接!", Toast.LENGTH_SHORT)
                .show();
                Log.d(TAG, "HTTP请求失败：网络连接错误！");
            }
        };

        Request<JsonResult<User>> versionRequest = new GsonRequest<JsonResult<User>>(Request
        .Method.POST, url,
                successListener, errorListener, new TypeToken<JsonResult<User>>() {
        }.getType()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("email", App.email);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }*/

}
