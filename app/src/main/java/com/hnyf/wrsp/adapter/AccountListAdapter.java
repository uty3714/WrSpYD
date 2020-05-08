package com.hnyf.wrsp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnyf.wrsp.R;
import com.hnyf.wrsp.listener.ICustomClickListener;

import java.util.List;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountHolder> {

    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;

    private ICustomClickListener mListener = null;

    public void setICustomClickListener(ICustomClickListener listener) {
        this.mListener = listener;
    }

    public AccountListAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mInflater = LayoutInflater.from(mContext);
    }


    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountHolder(mInflater.inflate(R.layout.item_account_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder holder, final int position) {

        holder.mTitle.setText(mList.get(position));
        holder.mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCustomClick(v, position);
                }
            }
        });
        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCustomClick(v, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AccountHolder extends RecyclerView.ViewHolder {


        public TextView mTitle;
        private Button mSwitchBtn;
        private Button mDeleteBtn;

        public AccountHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.item_account_title);
            mSwitchBtn = itemView.findViewById(R.id.item_account_switch_btn);
            mDeleteBtn = itemView.findViewById(R.id.item_account_delete_btn);


        }
    }

}
