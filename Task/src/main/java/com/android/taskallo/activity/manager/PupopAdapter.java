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

package com.android.taskallo.activity.manager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.taskallo.R;

public class PupopAdapter extends BaseAdapter {
    private Context context;
    private FragmentManager fm;
    private String[] list;
    private int type;
    private final LayoutInflater inflater;
    private TextView gameNameTv;
    private Intent intent;
    private int mSelected_Position = 0;

    public PupopAdapter(Context context, String[] products) {
        super();
        this.context = context;
        this.list = products;
        inflater = LayoutInflater.from(context);
    }

    public void setList(String[] products, int mSelectedPosition) {
        this.list = list;
        mSelected_Position = mSelectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        convertView = inflater.inflate(R.layout.fragment_1_popup_window, parent, false);

        TextView titleTv = convertView.findViewById(R.id.fragment1_item_tv);
        if (mSelected_Position == position) {
            titleTv.setTextColor(ContextCompat.getColor(context, R.color.mainColor));
        }
        titleTv.setText(list[position]);

        return convertView;
    }
}