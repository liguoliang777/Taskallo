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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.FileListInfo;
import com.android.taskallo.project.view.CardDetailActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 手柄游戏的ListView控件适配器
 *
 * @author zeng
 * @since 2016-10-25
 */
public class FileListAdapter extends BaseAdapter {

    private List<FileListInfo> gameInfoList;
    private CardDetailActivity context;

    public FileListAdapter(CardDetailActivity context, List<FileListInfo> mFileListData) {
        super();
        this.context = context;
        gameInfoList = mFileListData;
    }

    public void setDate(List<FileListInfo> gameInfoList) {
        this.gameInfoList = gameInfoList;
        notifyDataSetChanged();
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
        if (gameInfoList == null || gameInfoList.size() == 0) {
            return null;
        }
        final FileListInfo gameInfo = gameInfoList.get(position);

        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout
                            .item_card_detail_file_list, parent,
                    false);
            holder.filePicIv = (SimpleDraweeView) convertView.findViewById(R.id
                    .card_detail_file_list_item_iv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (gameInfo != null) {
            String imgUrl = gameInfo.fileUrl;
            Log.d("", "图片:" + imgUrl);
            String fileId = gameInfo.fileId;
            holder.filePicIv.setImageURI(imgUrl);

            holder.filePicIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFileDetailDialog(gameInfo);
                }
            });
        }


        return convertView;
    }

    private void closeInputMethod(EditText centerRenameEt) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService
                (Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(centerRenameEt.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showFileDetailDialog(final FileListInfo gameInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme_fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_file_detail, null);
        SimpleDraweeView fileDetailSDV = (SimpleDraweeView) v.findViewById(R.id
                .card_detail_file_sdv);
        fileDetailSDV.setImageURI(gameInfo.fileUrl);

        final TextView centerTitleTv = (TextView) v.findViewById(R.id.dialog_center_title_tv);
        final EditText centerRenameEt = (EditText) v.findViewById(R.id.dialog_center_rename_et);
        final String fileNameStr = gameInfo.fileName == null ? "" : gameInfo.fileName;
        centerTitleTv.setText(fileNameStr);
        centerRenameEt.setText(fileNameStr);
        centerRenameEt.setSelection(fileNameStr.length());

        final Button moreMenuBt = (Button) v.findViewById(R.id.dialog_btn_menu_bt);
        final Button renameSaveBt = (Button) v.findViewById(R.id.dialog_btn_sure);
        Button finishBt = (Button) v.findViewById(R.id.dialog_btn_cancel);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);

        renameSaveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameSaveBt.setVisibility(View.INVISIBLE);
                moreMenuBt.setVisibility(View.VISIBLE);
                centerTitleTv.setVisibility(View.VISIBLE);
                centerRenameEt.setVisibility(View.INVISIBLE);
                //关闭输入法
                closeInputMethod(centerRenameEt);
            }
        });
        moreMenuBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View inflate = LayoutInflater.from(context).inflate(R.layout
                        .layout_file_detail_top_menu, null);

                final PopupWindow popWindow = new PopupWindow(inflate, LinearLayout.LayoutParams
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

                popWindow.showAsDropDown(v);
                //重命名
                inflate.findViewById(R.id.file_item_menu_rename_bt).setOnClickListener
                        (new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
                                dialog.getWindow().clearFlags(WindowManager.LayoutParams
                                        .FLAG_ALT_FOCUSABLE_IM);
                                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                                        .SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                                popWindow.dismiss();
                                centerRenameEt.setVisibility(View.VISIBLE);
                                renameSaveBt.setVisibility(View.VISIBLE);
                                moreMenuBt.setVisibility(View.INVISIBLE);
                            }
                        });
                inflate.findViewById(R.id.file_item_menu_delete_bt).setOnClickListener
                        (new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //删除

                                popWindow.dismiss();
                                dialog.dismiss();
                            }
                        });

            }
        });

        finishBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 用于保存ListView中重用的item视图的引用
     *
     * @author flan
     * @since 2015年10月28日
     */
    public static class ViewHolder {
        private SimpleDraweeView filePicIv;
    }

}














