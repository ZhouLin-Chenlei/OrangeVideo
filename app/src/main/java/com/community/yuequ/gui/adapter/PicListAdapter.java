package com.community.yuequ.gui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.community.yuequ.R;
import com.community.yuequ.gui.PicDetailActivity;
import com.community.yuequ.gui.VideoDetailActivity;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.view.GroupImageView;

import java.util.List;

/**
 * modou
 */
public class PicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private List<RProgram> mRPrograms;
    private static final int TYPE_LIST = 0;
    private static final int TYPE_FOOT_VIEW = 1;
    private View footView;

    public PicListAdapter(Context  context){
        this.mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_LIST:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic_list, parent, false);
                viewHolder = new ListViewHolder(view);
                break;
            case TYPE_FOOT_VIEW:
                footView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footview_layout, parent, false);
                footView.setVisibility(View.GONE);
                viewHolder = new FootViewHolder(footView);
                break;
            default:
                viewHolder = new ListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic_list, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListViewHolder) {
            final ListViewHolder listViewHolder = (ListViewHolder) holder;
            final RProgram rProgram = mRPrograms.get(position);
            listViewHolder.tv_second_title.setText(rProgram.name);
            listViewHolder.tv_dsc.setText(rProgram.remark);
            listViewHolder.mGroupImageView.setPics(rProgram.picList);
            listViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, PicDetailActivity.class);
                    intent.putExtra("program",rProgram);
                    mContext.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if(mRPrograms==null){
            return 1;
        }
        return mRPrograms.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT_VIEW;
        } else {
            return TYPE_LIST;
        }
    }

    public void setLoadMoreViewText(String text) {
        if (footView == null) return;
        ((TextView) footView.findViewById(R.id.tv_loading_more)).setText(text);
        notifyItemChanged(getItemCount());
    }

    public void setLoadMoreViewVisibility(int visibility) {
        if (footView == null) return;
        footView.setVisibility(visibility);
        notifyItemChanged(getItemCount());
    }

    public boolean isLoadMoreShown() {
        if (footView == null) return false;
        return footView.isShown();
    }

    public String getLoadMoreViewText() {
        if (footView == null) return "";
        return ((TextView) footView.findViewById(R.id.tv_loading_more)).getText().toString();
    }



    public void setData(List<RProgram> list) {
        mRPrograms = list;
        notifyDataSetChanged();

    }

    public void addData(List<RProgram> list) {
        if(list!=null&&!list.isEmpty()){
            mRPrograms.addAll(list);
            notifyDataSetChanged();
        }
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_second_title;
        public TextView tv_dsc;
        public GroupImageView mGroupImageView;


        public ListViewHolder(View view) {
            super(view);

            tv_second_title = (TextView) itemView.findViewById(R.id.tv_second_title);
            tv_dsc = (TextView) itemView.findViewById(R.id.tv_dsc);
            mGroupImageView = (GroupImageView) itemView.findViewById(R.id.giv_image_group);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {
        TextView tvLoadingMore;

        public FootViewHolder(View view) {
            super(view);
            tvLoadingMore = (TextView) view.findViewById(R.id.tv_loading_more);
        }
    }
}
