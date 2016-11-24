package com.community.yuequ.gui.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.community.yuequ.gui.OnLineSecondListActivity;
import com.community.yuequ.modle.RProgram;

import java.util.List;

/**
 * modou
 */
public class OnLineListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity mContext;
    private View footView;
    private static final int TYPE_LIST = 0;
    private static final int TYPE_FOOT_VIEW = 1;
    private List<RProgram> mList;
    public OnLineListAdapter(Activity activity){
        this.mContext = activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_LIST:
                viewHolder = new OnLineListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_list, parent, false));
                break;
            case TYPE_FOOT_VIEW:
                footView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footview_layout, parent, false);
                footView.setVisibility(View.GONE);
                viewHolder = new OnLineListAdapter.FootViewHolder(footView);
                break;
            default:
                viewHolder = new OnLineListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_list, parent, false));
                break;
        }
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final RProgram programa = mList.get(position);
            viewHolder.tv_title.setText(programa.name);
            viewHolder.tv_desc.setText("正在直播："+programa.remark);
//            ImageManager.getInstance().loadUrlImage(mContext, programa.img_path, viewHolder.iv_img);
            Glide
                    .with(mContext)
                    .load( programa.img_path)
                    .placeholder(R.mipmap.jiazai)
                    .dontAnimate()
                    .into(viewHolder.iv_img);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, LiveVideoActivity.class);
                    intent.putExtra("program", programa);
//                    intent.putExtra("column_id", programa.id);
//                    intent.putExtra("column_name", programa.name);
                    mContext.startActivityForResult(intent, 17);
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

    public List<RProgram> getList(){
        return mList;
    }
    public void setData(List<RProgram> result) {
        mList = result;
        notifyDataSetChanged();
    }
    public void addData(List<RProgram> list) {
        if (list != null && !list.isEmpty()) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_img;
        public TextView tv_title;
        public TextView tv_desc;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
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
