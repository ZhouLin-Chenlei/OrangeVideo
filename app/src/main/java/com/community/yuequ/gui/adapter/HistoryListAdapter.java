package com.community.yuequ.gui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.gui.HistoryActivity;
import com.community.yuequ.gui.VideoDetailActivity;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.provider.History;
import com.community.yuequ.view.ContextMenuRecyclerView;

import java.util.List;

/**
 * modou
 */
public class HistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private HistoryActivity mContext;
    private View footView;
    private static final int TYPE_LIST = 0;
    private static final int TYPE_FOOT_VIEW = 1;
    private List<RProgram> programs;

    public HistoryListAdapter(HistoryActivity context) {
        this.mContext = context;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final RProgram program = programs.get(position);

            viewHolder.tv_name.setText(program.name);
            ImageManager.getInstance().loadUrlImage(mContext, program.img_path, viewHolder.iv_cover);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Contants.SHOWTYPE_LINK.equals(program.show_type)){
                        try {
                            String openurl = program.link_url;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        Intent intent = new Intent(mContext, VideoDetailActivity.class);
                        intent.putExtra("program", program);
                        mContext.startActivity(intent);
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (programs == null) {
            return 1;
        }
        return programs.size() + 1;
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
        programs = list;
        notifyDataSetChanged();

    }

    public void addData(List<RProgram> list) {
        if (list != null && !list.isEmpty()) {
            programs.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Nullable
    public RProgram getItem(int position) {
        if (position < 0 || position >= programs.size())
            return null;
        else
            return programs.get(position);
    }
    @MainThread
    public void remove(int position) {
        if (position == -1)
            return;
        programs.remove(position);
        notifyItemRemoved(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        public ImageView iv_cover;
        public ImageView iv_play;
        public TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover);
            iv_play = (ImageView) itemView.findViewById(R.id.iv_play);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            itemView.setOnLongClickListener(this);
        }

//        public void onMoreClick(View v) {
//            mContext.openContextMenu(getLayoutPosition());
//        }

        @Override
        public boolean onLongClick(View v) {
            mContext.mRecyclerView.openContextMenu(getLayoutPosition());
            return true;
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
