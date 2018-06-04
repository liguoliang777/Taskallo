package com.android.taskallo.project.ProjListItem;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.project.view.ProjListActivity;
import com.rengwuxian.materialedittext.MaterialEditText;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {


    ProjListActivity context;
    private LayoutInflater from;

    public ItemAdapter(Context context) {
        from = LayoutInflater.from(context);
    }

    public void setContext(ProjListActivity context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(from.inflate(R.layout.layout_proj_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.recyclerView.setLayoutManager(new ItemLayoutManager(holder.itemView.getContext()));

        RecyclerView.Adapter adapter = new ItemItemAdapter(position, 10);
        holder.recyclerView.setAdapter(adapter);

        holder.mItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCardAlertDialog();
            }
        });
        if (holder.recyclerView.getItemDecorationAt(0) == null) {
            holder.recyclerView.addItemDecoration(new ItemLineDecoration());
        }
        holder.recyclerView.getItemAnimator().setAddDuration(0);
        holder.recyclerView.getItemAnimator().setRemoveDuration(0);
    }

    private void showAddCardAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Dialog_add_card);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_add_card, null);
        Button btnPositive = (Button) v.findViewById(R.id.dialog_add_card_ok);
        final MaterialEditText etContent = (MaterialEditText) v.findViewById(R.id
                .dialog_add_card_title);
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
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);//可以设置显示的位置
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (TextUtil.isEmpty(str)) {
                    etContent.setError(context.getString(R.string.enter_cannot_empty));
                } else {
                    dialog.dismiss();
                }
            }
        });



    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView mItemTitle;
        Button mItemAdd;
        private EditText mItemEnterEt;

        ItemViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View itemView) {
            recyclerView = itemView.findViewById(R.id.item_recycler_view);
            mItemTitle = itemView.findViewById(R.id.item_title);
            mItemAdd = itemView.findViewById(R.id.item_add);
        }
    }

}
