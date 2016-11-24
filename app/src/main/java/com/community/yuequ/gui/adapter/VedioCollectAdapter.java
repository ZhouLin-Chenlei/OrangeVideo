package com.community.yuequ.gui.adapter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.community.yuequ.R;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.gui.LiveVideoActivity;
import com.community.yuequ.gui.VideoDetailActivity;
import com.community.yuequ.modle.Collect;
import com.community.yuequ.modle.RProgram;

import java.util.List;

/**
 * modou
 */
public class VedioCollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Fragment mFragment;
    private View footView;
    private static final int TYPE_LIST = 0;
    private static final int TYPE_FOOT_VIEW = 1;
    private List<Collect> mList;
    public VedioCollectAdapter(Fragment fragment){
        this.mFragment = fragment;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_LIST:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
                viewHolder = new ViewHolder(view);
                break;
            case TYPE_FOOT_VIEW:
                footView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footview_layout, parent, false);
                footView.setVisibility(View.GONE);
                viewHolder = new FootViewHolder(footView);
                break;
            default:
                viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false));
                break;
        }
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final Collect collect = mList.get(position);
            viewHolder.tv_name.setText(collect.program_name);
//            viewHolder.tv_desc.setText("正在直播："+collect.remark);
            ImageManager.getInstance().loadUrlImage(mFragment,collect.program_img_path, viewHolder.iv_cover);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RProgram program = new RProgram();
                    program.id = collect.program_id;
                    program.name = collect.program_name;
                    program.img_path = collect.program_img_path;
                    program.remark = collect.remark;
                    program.isCollection = collect.isCollection;

                    Intent intent = new Intent();
                    intent.setClass(mFragment.getActivity(), VideoDetailActivity.class);
                    intent.putExtra("program", program);
//                    intent.putExtra("column_id", programa.id);
//                    intent.putExtra("column_name", programa.name);
                    mFragment.startActivityForResult(intent,17);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mList==null)return 1;
        return mList.size()+1;
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

    public void setData(List<Collect> result) {
        mList = result;
        notifyDataSetChanged();
    }
    public void addData(List<Collect> list) {
        if (list != null && !list.isEmpty()) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public List<Collect> getList() {
        return mList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_cover;
        public ImageView iv_play;
        public TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover);
            iv_play = (ImageView) itemView.findViewById(R.id.iv_play);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);

        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {

        TextView tvLoadingMore;

        public FootViewHolder(View itemView) {
            super(itemView);
            tvLoadingMore = (TextView) itemView.findViewById(R.id.tv_loading_more);
        }
    }
}
