package com.android.taskallo.project;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.adapter.TagListAdapter;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.TagInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.util.ToastUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 全部专题
 * Created by gp on 2017/4/13 0013.
 */

public class TagListActivity extends BaseFgActivity {
    private int selectedPosition = 0;
    float[] outerRadian = new float[]{10, 10, 10, 10, 10, 10, 10, 10};
    private Button tv_title, addTagBt;
    private TagListAdapter tagAdapter;
    private TagListActivity context;
    private GridView gview;
    private String mProjId, mBoardId;
    private Dialog defAvatarDialog;
    private AvatarAdapter mAvatarAdapter;
    private List<TagInfo> defTaglist = new ArrayList<>();
    private EditText tagTitleEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.selected_topics_activity);
        context = TagListActivity.this;
        Bundle bundle = getIntent().getExtras();
        mProjId = bundle.getString(KeyConstant.projectId);
        mBoardId = bundle.getString(KeyConstant.boardId);

        //defTaglist = (List<TagInfo>) getIntent().getSerializableExtra(KeyConstant.tagInfo);
        init();
        getData();
        //获取已经关联的数据
        getRelationData();
    }

    private void init() {
        tv_title = (Button) findViewById(R.id.left_bt);
        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        addTagBt = (Button) findViewById(R.id.title_right_bt);
        tv_title.setText("");

        titleTv.setText("标签");

        addTagBt.setVisibility(View.VISIBLE);
        addTagBt.setText("");
        Drawable dra = getResources().getDrawable(R.drawable.ic_add);
        dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
        addTagBt.setCompoundDrawables(null, null, dra, null);

        addTagBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog(null, "", "");
                defAvatarDialog.show();
                showInputMethod();
            }
        });
        gview = (GridView) findViewById(R.id.gview);

        tagAdapter = new TagListAdapter(this, defTaglist, mBoardId);
        gview.setAdapter(tagAdapter);


        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
    }

    private void getRelationData() {
        String url = Constant.WEB_SITE1 + UrlConstant.url_label + UrlConstant.url_label + "/" +
                mBoardId;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }

        Response.Listener<JsonResult<List<TagInfo>>> successListener = new Response
                .Listener<JsonResult<List<TagInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<TagInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                List<TagInfo> relationInfo = result.data;
                if (result.code == 0 && context != null && relationInfo != null) {
                    tagAdapter.setRelationData(relationInfo);
                }
            }
        };

        Request<JsonResult<List<TagInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<TagInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<List<TagInfo>>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, App.token);
                        Log.d(TAG, "token1111:" + App.token);
                        params.put(KeyConstant.appType, Constant.APP_TYPE_ID_0_ANDROID);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void showInputMethod() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //自动弹出键盘
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService
                        (Context
                                .INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }


    private void initDialog(final TagInfo item, String titleStr, String labelColour) {
        defAvatarDialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_def_tag, null);
        GridView gridView = (GridView) inflate.findViewById(R.id.tag_add_grid_view);
        Button cancelBt = (Button) inflate.findViewById(R.id.tag_add_cancel_bt);
        final Button deletedBt = (Button) inflate.findViewById(R.id.tag_add_delete_tv);
        TextView titleTopTv = (TextView) inflate.findViewById(R.id.tag_add_title_tv);
        titleTopTv.setText(item == null ? "新增标签" : "编辑标签");
        deletedBt.setVisibility(item == null ? View.GONE : View.VISIBLE);
        deletedBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除标签
                if (item != null) {
                    deleteTagThread(item.labelId);
                }
            }
        });
        tagTitleEt = (EditText) inflate.findViewById(R.id.tag_add_title_et);
        tagTitleEt.setText(titleStr);
        tagTitleEt.setSelection(titleStr.length());
        Button addBt = (Button) inflate.findViewById(R.id.tag_add_ok_bt);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defAvatarDialog.cancel();
            }
        });
        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加
                String s = tagTitleEt.getText().toString();
                String tagTitle = s == null ? "" : s;

                String labelColour = tagStrArr[selectedPosition];

                if (item == null) {
                    //添加
                    addTagThread(tagTitle, labelColour);
                } else {
                    //编辑完成
                    updateTagThread(item.labelId, tagTitle, labelColour);
                }
            }
        });
        mAvatarAdapter = new AvatarAdapter();
        gridView.setAdapter(mAvatarAdapter);
        if (item != null) {
            for (int i = 0; i < tagStrArr.length; i++) {
                String arrLaberColor = tagStrArr[i];
                if (arrLaberColor.equals(labelColour)) {
                    selectedPosition = i;
                    mAvatarAdapter.notifyDataSetChanged();
                }
            }
        }


        defAvatarDialog.setContentView(inflate);//将布局设置给Dialog
        Window dialogWindow = defAvatarDialog.getWindow(); //获取当前Activity所在的窗体
        dialogWindow.setGravity(Gravity.BOTTOM);//设置Dialog从窗体底部弹出
        WindowManager.LayoutParams params = dialogWindow.getAttributes();   //获得窗体的属性
        //params.y = 20;  Dialog距离底部的距离
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置Dialog距离底部的距离
        dialogWindow.setAttributes(params); //将属性设置给窗体
    }

    //删除标签
    private void deleteTagThread(String labelId) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }
        String url = Constant.WEB_SITE1 + UrlConstant.url_label + "/" + mBoardId + "/" + labelId;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }
                if (result.code == 0) {
                    getData();
                    defAvatarDialog.cancel();
                }
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.DELETE, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        ToastUtil.show(context, context.getString(R.string.server_exception));

                    }
                }, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.id, mBoardId);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, App.token);
                        params.put(KeyConstant.appType, Constant.APP_TYPE_ID_0_ANDROID);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void updateTagThread(String labelId, final String tagTitle, final String labelColour) {
        String url = Constant.WEB_SITE1 + UrlConstant.url_label + "/" + labelId;
        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null || result.code != 0) {
                    if (result.code == -311) {
                        ToastUtil.show(context, "该标签名字已经存在");
                    } else {
                        ToastUtil.show(context, getString(R.string.requery_failed));
                    }
                    return;
                }

                getData();
                defAvatarDialog.cancel();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(context, getString(R.string.requery_failed));
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.PUT, url,
                        successListener, errorListener, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.projectId, mProjId);
                        params.put(KeyConstant.boardId, mBoardId);
                        params.put(KeyConstant.labelName, tagTitle);
                        params.put(KeyConstant.labelColour, labelColour);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, App.token);
                        params.put(KeyConstant.appType, Constant.APP_TYPE_ID_0_ANDROID);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    //添加标签
    private void addTagThread(final String tagTitle, final String labelColour) {
        String url = Constant.WEB_SITE1 + UrlConstant.url_label;
        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null || result.code != 0) {
                    ToastUtil.show(context, getString(R.string.requery_failed));
                    return;
                }

                getData();
                defAvatarDialog.cancel();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(context, getString(R.string.requery_failed));
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.POST, url,
                        successListener, errorListener, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.projectId, mProjId);
                        params.put(KeyConstant.boardId, mBoardId);
                        params.put(KeyConstant.labelName, tagTitle);
                        params.put(KeyConstant.labelColour, labelColour);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, App.token);
                        params.put(KeyConstant.appType, Constant.APP_TYPE_ID_0_ANDROID);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }


    public void getData() {
        //getRelationData();
        String url = Constant.WEB_SITE1 + UrlConstant.url_label + "/" + mProjId + "/" + mBoardId;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }

        Response.Listener<JsonResult<List<TagInfo>>> successListener = new Response
                .Listener<JsonResult<List<TagInfo>>>() {
            @Override
            public void onResponse(JsonResult<List<TagInfo>> result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.server_exception));
                    return;
                }

                List<TagInfo> projDetailInfo = result.data;
                if (result.code == 0 && context != null && projDetailInfo != null) {
                    setData(projDetailInfo);
                }
            }
        };

        Request<JsonResult<List<TagInfo>>> versionRequest = new
                GsonRequest<JsonResult<List<TagInfo>>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "网络连接错误！" + volleyError.getMessage());
                    }
                }, new TypeToken<JsonResult<List<TagInfo>>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, App.token);
                        params.put(KeyConstant.appType, Constant.APP_TYPE_ID_0_ANDROID);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);

    }

    private void setData(List<TagInfo> result) {
        if (result != null && result.size() > 0) {
            defTaglist = result;
            tagAdapter.setList(defTaglist);
        } else {
            ToastUtil.show(context, "该项目下标签数据为空");
        }
    }

    String tagStrArr[] = new String[]{"#00CD66", "#CDCD00", "#fec055", "#CD4F39",
            "#95e645", "#4590e5", "#a87afb", "#d94bee", "#45d1e5", "#1a1a1a", "#cccccc"
    };

    public void updateTag(TagInfo item) {
        initDialog(item, item.labelName == null ? "" : item.labelName, item.labelColour);
        defAvatarDialog.show();
        showInputMethod();
    }

    //默认头像适配器
    public class AvatarAdapter extends BaseAdapter {

        public AvatarAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return tagStrArr.length;
        }

        @Override
        public Object getItem(int position) {
            return tagStrArr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            convertView = View.inflate(parent.getContext(), R.layout.gridview_rag_add, null);
            Button mTagColorBt = (Button) convertView.findViewById(R.id.tag_add_color_bt);
            ImageView itemSelectedTag = (ImageView) convertView.findViewById(R.id
                    .tag_add_selected_tag);
            String labelColour = tagStrArr[position];
            if (labelColour != null) {
                ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(outerRadian, null,
                        null));
                drawable.getPaint().setStyle(Paint.Style.FILL);
                drawable.getPaint().setColor(Color.parseColor(labelColour));
                //构建Controller
                mTagColorBt.setBackground(drawable);
            }
            if (selectedPosition == position) {
                itemSelectedTag.setVisibility(View.VISIBLE);
            } else {
                itemSelectedTag.setVisibility(View.INVISIBLE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            });


            return convertView;
        }
    }
}
