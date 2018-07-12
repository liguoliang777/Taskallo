/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.taskallo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.taskallo.App;
import com.android.taskallo.R;
import com.android.taskallo.bean.JsonResult;
import com.android.taskallo.bean.MemberInfo;
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
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页 GridView控件适配器
 *
 * @author zeng
 * @since 2016-06-07
 */
public class MemberListAdapter extends BaseAdapter {

    private static final String TAG = MemberListAdapter.class.getSimpleName();

    private List<MemberInfo> memberInfos;
    private Context context;
    private String mBoardId;

    public MemberListAdapter(Context context, List<MemberInfo> memberInfoList, String mBoardId) {
        super();
        this.context = context;
        this.mBoardId = mBoardId;
        memberInfos = memberInfoList;
    }

    @Override
    public int getCount() {
        if (memberInfos != null) {
            return memberInfos.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (memberInfos != null) {
            return memberInfos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final MemberInfo memberInfo = memberInfos.get(position);
        if (memberInfo == null) {
            return null;
        }
        convertView = LayoutInflater.from(context).inflate(R.layout.item_memeber_list, parent,
                false);
        SimpleDraweeView infoIv = (SimpleDraweeView) convertView.findViewById(R.id.member_info_iv);
        TextView titleTv = (TextView) convertView.findViewById(R.id.member_info_name_tv);
        TextView removeBt = (TextView) convertView.findViewById(R.id.member_info_remove_tv);

        String name = memberInfo.nickName;
        titleTv.setText(name == null ? "" : name);

        String imgUrl = memberInfo.headPortrait == null ? "" : memberInfo.headPortrait;
        infoIv.setImageURI(imgUrl);

        //移除成员
        removeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog1 = new AlertDialog.Builder(context, R.style
                        .dialog_appcompat_theme)
                        .setTitle("确定从此卡片中移除该成员吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!NetUtil.isNetworkConnected(context)) {
                                    ToastUtil.show(context, "网络异常,请检查网络设置");
                                    return;
                                }
                                String url = Constant.WEB_SITE1 + UrlConstant.url_label + "/" +
                                        mBoardId + "/" + memberInfo.id + "/" + memberInfo.nickName;

                                Response.Listener<JsonResult> successListener = new Response
                                        .Listener<JsonResult>() {
                                    @Override
                                    public void onResponse(JsonResult result) {
                                        if (result == null) {
                                            ToastUtil.show(context, context.getString(R.string
                                                    .server_exception));
                                            return;
                                        }
                                        if (result.code == 0) {
                                            memberInfos.remove(memberInfo);
                                            notifyDataSetChanged();
                                            ToastUtil.show(context, "成员移除成功");
                                        }
                                    }
                                };

                                Request<JsonResult> versionRequest = new
                                        GsonRequest<JsonResult>(Request.Method.DELETE, url,
                                                successListener, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                volleyError.printStackTrace();
                                                ToastUtil.show(context, context.getString(R
                                                        .string.server_exception));

                                            }
                                        }, new TypeToken<JsonResult>() {
                                        }.getType()) {
                                            @Override
                                            public Map<String, String> getParams() throws
                                                    AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put(KeyConstant.boardId, mBoardId);
                                                params.put(KeyConstant.boardMemberId, memberInfo
                                                        .id + "");
                                                params.put(KeyConstant.nickName, memberInfo
                                                        .nickName);
                                                return params;
                                            }

                                            @Override
                                            public Map<String, String> getHeaders() throws
                                                    AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put(KeyConstant.Content_Type, Constant
                                                        .application_json);
                                                params.put(KeyConstant.Authorization, App.token);
                                                params.put(KeyConstant.appType, Constant
                                                        .APP_TYPE_ID_0_ANDROID);
                                                return params;
                                            }
                                        };
                                App.requestQueue.add(versionRequest);
                            }
                        }).create();
                dialog1.show();
            }
        });
        return convertView;
    }

    public void setData(List<MemberInfo> memberInfoList) {
        memberInfos = memberInfoList;
        notifyDataSetChanged();
    }
}














