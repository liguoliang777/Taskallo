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
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.taskallo.R;
import com.android.taskallo.bean.FileListInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 手柄游戏的ListView控件适配器
 *
 * @author zeng
 * @since 2016-10-25
 */
public class FileListAdapter extends BaseAdapter {

    private List<FileListInfo> gameInfoList;
    private FragmentManager fragmentManager;

    private Context context;
    private static Handler uiHandler = new Handler();


    public FileListAdapter(Context context, FragmentManager fm, List<FileListInfo> mFileListData) {
        super();
        this.context = context;
        this.fragmentManager = fm;
        gameInfoList = mFileListData;
    }

    /**
     * 设置ListView中的数据
     *
     * @param gameInfoList 游戏数据
     */
    public void setDate(List<FileListInfo> gameInfoList) {
        this.gameInfoList = gameInfoList;

    }

    @Override
    public int getCount() {
        if (gameInfoList != null) {
            return gameInfoList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (gameInfoList != null) {
            return gameInfoList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clean() {
        if (gameInfoList != null)
            gameInfoList.clear();
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final FileListInfo gameInfo = gameInfoList.get(position);

        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout
                            .item_card_detail_file_list, parent,
                    false);
            holder.img = (ImageView) convertView.findViewById(R.id.card_detail_file_list_item_iv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (gameInfo != null) {
            String imgUrl = gameInfo.fileUrl;
            String fileId = gameInfo.fileId;
            Picasso.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_def_logo_720_288)
                    .error(R.drawable.ic_def_logo_720_288)
                    .centerInside()
                    .tag(context)
                    .into(holder.img);

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //到附件详情界面,全屏的 dialog
                }
            });
        }


        return convertView;
    }

    /**
     * 用于保存ListView中重用的item视图的引用
     *
     * @author flan
     * @since 2015年10月28日
     */
    public static class ViewHolder {
        private ImageView img;
    }

}














