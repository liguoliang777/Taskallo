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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.MemberInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

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

    public MemberListAdapter(Context context, List<MemberInfo> memberInfoList) {
        super();
        this.context = context;
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
        MemberInfo memberInfo = memberInfos.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.item_memeber_list, parent,
                false);
        SimpleDraweeView infoIv = (SimpleDraweeView) convertView.findViewById(R.id.member_info_iv);
        TextView titleTv = (TextView) convertView.findViewById(R.id.member_info_name_tv);
        TextView removeBt = (TextView) convertView.findViewById(R.id.member_info_remove_tv);

        String name = memberInfo.nickName;
        titleTv.setText(name == null ? "" : name);

        String imgUrl = memberInfo.headPortrait == null ? "" : memberInfo.headPortrait;
        infoIv.setImageURI(imgUrl);
        return convertView;
    }
}














