package com.android.taskallo.project.Item;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.ListInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.NetUtil;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.project.view.ProjListActivity;
import com.android.taskallo.util.ToastUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_TYPE_FOOTER = 2;
    private List<ListInfo> mList;
    private int dm_margin_left;
    private ProjListActivity context;
    private LayoutInflater from;
    private String mProjectId;

    public ItemAdapter(Context context, List<ListInfo> list) {
        dm_margin_left = context.getResources().
                getDimensionPixelOffset(R.dimen.dm_200);
        from = LayoutInflater.from(context);
        mList = list;
    }

    public void setContext(ProjListActivity context, String projectId) {
        this.context = context;
        this.mProjectId = projectId;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 1 : mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return ITEM_TYPE_FOOTER;
        } else {
            return 0;
        }
    }

    /*底部Item*/
    class FootHolder extends RecyclerView.ViewHolder {
        public TextView footBt;

        public FootHolder(View itemView) {
            super(itemView);
            footBt = itemView.findViewById(R.id.proj_list_item_footer_add);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOTER) {
            View view = from.inflate(R.layout.layout_proj_item_footer, parent, false);
            FootHolder viewHolder = new FootHolder(view);
            return viewHolder;
        } else {
            View inflate = from.inflate(R.layout.layout_proj_item, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(inflate);
            return itemViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder hold, int position) {
        if (hold instanceof FootHolder) {//最后一个
            FootHolder footHolder = (FootHolder) hold;
            footHolder.footBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddCardAlertDialog(R.string.list_title);
                }
            });
        } else {
            final ItemViewHolder holder = (ItemViewHolder) hold;
            holder.itemItemRV.setLayoutManager(
                    new ItemLayoutManager(holder.itemView.getContext()));
            RecyclerView.Adapter adapter = new ItemItemAdapter(context, position, 10);
            holder.itemItemRV.setAdapter(adapter);
            holder.mItemAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddCardAlertDialog(R.string.card_title);
                }
            });
            if (holder.itemItemRV.getItemDecorationAt(0) == null) {
                holder.itemItemRV.addItemDecoration(new ItemLineDecoration());
            }
            holder.itemItemRV.getItemAnimator().setAddDuration(0);
            holder.itemItemRV.getItemAnimator().setRemoveDuration(0);

            holder.mMenuBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopWindow(view);
                }
            });
            holder.mItemTitle.setSelection(holder.mItemTitle.getText().length());


            holder.mSaveBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newTitleStr = holder.mItemTitle.getText().toString();
                    if (TextUtil.isAnyEmpty(newTitleStr)) {
                        ToastUtil.show(context, "标题不能为空");
                    } else {
                        //提交新标题
                        holder.mItemTitle.clearFocus();
                        InputMethodManager imm = (InputMethodManager) context
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(holder.mItemTitle.getWindowToken(), 0);
                    }

                }
            });
            holder.mItemTitle.setOnFocusChangeListener(new android.view.View
                    .OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    holder.mSaveBt.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    Button listCopyListBt;
    PopupWindow popWindow;//分享提醒

    private void showPopWindow(View v) {
        View inflate = from.inflate(R.layout.layout_proj_list_menu, null);
        listCopyListBt = (Button) inflate.findViewById(R.id.list_copy_list_bt);

        popWindow = new PopupWindow(inflate, LinearLayout.LayoutParams
                .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
        // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
        v.getLocationOnScreen(location);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        //软键盘不会挡着popupwindow
        popWindow.setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);

        popWindow.showAsDropDown(v, dm_margin_left, dm_margin_left / 10);
        listCopyListBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //前往去分享
                popWindow.dismiss();
            }
        });

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        RecyclerView itemItemRV;
        EditText mItemTitle;
        Button mItemAdd, mMenuBt;
        Button mSaveBt;

        ItemViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View itemView) {
            itemItemRV = itemView.findViewById(R.id.item_recycler_view);
            mItemTitle = itemView.findViewById(R.id.proj_list_item_title);
            mMenuBt = itemView.findViewById(R.id.proj_list_item_menu_bt);
            mSaveBt = itemView.findViewById(R.id.proj_list_item_title_save_bt);
            mItemAdd = itemView.findViewById(R.id.item_add);
        }

    }

    //添加卡片
    private void showAddCardAlertDialog(final int hintText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog_add_card);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_add_card, null);
        Button btnPositive = (Button) v.findViewById(R.id.dialog_add_card_ok);
        final MaterialEditText etContent = (MaterialEditText) v.findViewById(R.id
                .dialog_add_card_title);
        etContent.setHint(hintText);
        etContent.setFloatingLabelText(context.getString(hintText));

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    etContent.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String title = etContent.getText().toString();
                if (TextUtil.isEmpty(title)) {
                    etContent.setError(context.getString(R.string.enter_cannot_empty));
                }
                //添加卡片
                if (hintText == R.string.card_title) {
                    ToastUtil.show(context, "卡片");
                } else {
                    addList(dialog, title);

                }
            }
        });
    }

    //添加列表
    private void addList(final Dialog dialog, final String title) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, "网络异常,请检查网络设置");
            return;
        }
        // 0 默认状态，1 已删除，2  收藏，3 已完成
        String url = Constant.WEB_SITE1 + UrlConstant.url_item;

        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null) {
                    ToastUtil.show(context, context.getString(R.string.server_exception));
                    return;
                }
                Log.d("", result.msg + ",添加列表:" + result.data);
                if (result.code == 0 && result.data != null) {
                    ToastUtil.show(context, "列表创建成功");
                    dialog.cancel();
                }
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(
                        Request.Method.POST, url,
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
                        params.put(KeyConstant.projectId, mProjectId);
                        params.put(KeyConstant.listItemName, title);
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
}
