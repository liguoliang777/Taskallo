
package com.android.taskallo.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.TagInfo;
import com.android.taskallo.core.net.GsonRequest;
import com.android.taskallo.core.utils.Constant;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.core.utils.UrlConstant;
import com.android.taskallo.project.TagListActivity;
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
 * @author gp
 */
public class TagListAdapter extends BaseAdapter {

    private TagListActivity context;
    private List<TagInfo> list;
    private ShapeDrawable drawable;
    private String mBoardId;
    float[] outerRadian = new float[]{10, 10, 10, 10, 10, 10, 10, 10};
    private List<TagInfo> relationInfo = new ArrayList<>();

    public TagListAdapter(TagListActivity context, List<TagInfo> list, String mBoardId) {
        super();
        this.context = context;
        this.list = list;
        this.mBoardId = mBoardId;
    }

    public void setList(List<TagInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.tag_list_item, parent,
                    false);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tag_item_tv);
            holder.itemColorBt = (Button) convertView.findViewById(R.id.tag_item_bg);
            holder.itemEditBt = (Button) convertView.findViewById(R.id.tag_item_edit);
            holder.itemSelectedTag = (ImageView) convertView.findViewById(R.id
                    .tag_item_selected_tag);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TagInfo item = list.get(position);
        if (item != null) {
            String labelColour = item.labelColour;
            if (labelColour != null) {
                drawable = new ShapeDrawable(new RoundRectShape(outerRadian, null, null));
                drawable.getPaint().setStyle(Paint.Style.FILL);
                drawable.getPaint().setColor(Color.parseColor(labelColour));
                //构建Controller
                holder.itemColorBt.setBackground(drawable);
                holder.itemSelectedTag.setVisibility(View.INVISIBLE);
                for (TagInfo tagInfo : relationInfo) {
                    if (tagInfo != null) {
                        String relationLabelColour = tagInfo.labelColour;
                        if (labelColour.equals(relationLabelColour)) {
                            holder.itemSelectedTag.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            }
            holder.itemColorBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int visibility = holder.itemSelectedTag.getVisibility();
                    if (visibility == View.VISIBLE) {
                        //取消关联
                        cancelRelationTagThread(item.labelId, holder.itemSelectedTag);
                    } else {
                        //关联
                        relationTagThread(item.labelId, holder.itemSelectedTag);
                    }
                }
            });

            //标题
            String labelName = item.labelName;
            if (labelName != null) {
                holder.tv_title.setText(labelName);
            }

            //编辑修改标签
            holder.itemEditBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.updateTag(item);

                }
            });

        }


        return convertView;
    }

    //关联
    private void relationTagThread(final String labelId, final ImageView itemSelectedTag) {
        String url = Constant.WEB_SITE1 + UrlConstant.url_relation + "/" + mBoardId + "/" + labelId;
        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null || result.code != 0) {
                    ToastUtil.show(context, context.getString(R.string.requery_failed));
                    return;
                }
                itemSelectedTag.setVisibility(View.VISIBLE);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(context, context.getString(R.string.requery_failed));
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.PUT, url,
                        successListener, errorListener, new TypeToken<JsonResult>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.boardId, mBoardId);
                        params.put(KeyConstant.labelId, labelId);
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

    //关联
    private void cancelRelationTagThread(final String labelId, final ImageView itemSelectedTag) {
        String url = Constant.WEB_SITE1 + UrlConstant.url_relation + "/" + mBoardId + "/" + labelId;
        Response.Listener<JsonResult> successListener = new Response
                .Listener<JsonResult>() {
            @Override
            public void onResponse(JsonResult result) {
                if (result == null || result.code != 0) {
                    ToastUtil.show(context, context.getString(R.string.requery_failed));
                    return;
                }
                itemSelectedTag.setVisibility(View.INVISIBLE);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.show(context, context.getString(R.string.requery_failed));
            }
        };

        Request<JsonResult> versionRequest = new
                GsonRequest<JsonResult>(Request.Method.DELETE, url,
                        successListener, errorListener, new TypeToken<JsonResult>() {
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

    public void setRelationData(List<TagInfo> relationInfo) {
        this.relationInfo = relationInfo;
        notifyDataSetChanged();
    }

    class ViewHolder {
        private Button itemColorBt, itemEditBt;
        private TextView tv_title;
        private ImageView itemSelectedTag;

    }
}














