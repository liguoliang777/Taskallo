package com.android.taskallo.gamehub.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.taskallo.activity.BaseFgActivity;
import com.android.taskallo.adapter.QuickConsultationAdapter;
import com.android.taskallo.core.utils.ImageUtil;
import com.android.taskallo.gamehub.bean.PictureBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gp on 2017/2/13 0013.
 */

public class CommonBaseActivity extends BaseFgActivity implements QuickConsultationAdapter
        .OnGridViewItemClickListener {

    public GridView gridView;
    public ImageView iv_upload;
    public TextView tv_info;
    public QuickConsultationAdapter adapter;
    public List<PictureBean> pictures = new ArrayList<PictureBean>();//图片文件
    public Bundle bundle;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 设置GirdView参数，绑定数据
     */
    public void setGridView() {
        int size = pictures != null ? pictures.size() : 0;
        int length = 69;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;

        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density) - 2;
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.width = itemWidth * size + (size == 1 ? 25 : 0);
        //gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(0); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        //gridView.setNumColumns(size); // 设置列数量=列表集合数
        adapter = new QuickConsultationAdapter(this, pictures, this);
        gridView.setAdapter(adapter);
        reSetLVHeight(gridView);

    }

    public void reSetLVHeight(AbsListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        View view;
        int count = listAdapter.getCount();
        if (count == 5||count == 10) {
            count = count-1;
        }
        for (int i = 0; i < count / 5 + 1; i++) {
            view = listAdapter.getView(i, null, listView);
            //宽度为屏幕宽度
            int i1 = View.MeasureSpec.makeMeasureSpec(ImageUtil.getScreenWidth(this),
                    View.MeasureSpec.EXACTLY);
            //根据屏幕宽度计算高度
            int i2 = View.MeasureSpec.makeMeasureSpec(i1, View.MeasureSpec.UNSPECIFIED);
            view.measure(i1, i2);
            totalHeight += view.getMeasuredHeight();
        }
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }

    //设置界面传递的参数
    public Bundle setBundle() {
        bundle = new Bundle();
        bundle.putSerializable("pictures", (Serializable) pictures);
        return bundle;
    }

    //获得界面传递的参数
    public void getBundle() {
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            if (bundle != null) {
                pictures = (List<PictureBean>) bundle.getSerializable("pictures") != null ?
                        (List<PictureBean>) bundle.getSerializable("pictures") : new
                        ArrayList<PictureBean>();
            }
        }
    }

    @Override
    public void onGridViewItemClick(int position, Boolean isDelete) {
        PictureBean bean = pictures.get(position);
        if (isDelete == true) { //删除图片
            for (int j = 0; j < pictures.size(); j++) {
                if (bean.getLocalURL().equals(pictures.get(j).getLocalURL())) {
                    pictures.remove(j);
                    break;
                }
            }
            if (iv_upload != null) {
                iv_upload.setVisibility(View.VISIBLE);
            }
            if (tv_info != null) {
                if (pictures.size() == 0) {
                    tv_info.setVisibility(View.VISIBLE);
                } else {
                    tv_info.setVisibility(View.GONE);
                }
            }
            setGridView();
        }
    }

    public void removePicture(PictureBean pictureBean) {
        for (int i = 0; i < pictures.size(); i++) {
            if (pictureBean.getLocalURL().equals(pictures.get(i).getLocalURL())) {
                pictures.remove(i);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearDatas();
    }

    protected void clearDatas() {
        adapter = null;
        pictures = null;
        bundle = null;
        intent = null;
    }
}

