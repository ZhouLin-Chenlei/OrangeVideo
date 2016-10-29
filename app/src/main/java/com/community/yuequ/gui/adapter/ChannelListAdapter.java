package com.community.yuequ.gui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.yuequ.R;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.gui.ChannelFragment;
import com.community.yuequ.gui.PicListActivity;
import com.community.yuequ.gui.VideoListActivity;
import com.community.yuequ.modle.Channel;

import java.util.List;

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {
    private ChannelFragment mFragment;
    private List<Channel> mChannels;


    public ChannelListAdapter(ChannelFragment fragment, List<Channel> list) {
        super();
        mFragment = fragment;
        this.mChannels=list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.channel_list_card , parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Channel channel = mChannels.get(position);

        holder.tv_channle_title.setText(channel.name);
        ImageManager.getInstance().loadUrlImage(mFragment,channel.img_path, holder.iv_channel_cover);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if("1".equals(channel.type)){
                    intent.setClass(mFragment.getContext(),VideoListActivity.class);
                }else{
                    intent.setClass(mFragment.getContext(),PicListActivity.class);
                }
                intent.putExtra("column_id",channel.id);
                intent.putExtra("type",channel.type);//1:视频;2:图文
                intent.putExtra("column_name",channel.name);
                intent.putExtra("from",4);
                mFragment.startActivity(intent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return 0l;
    }

    @Override
    public int getItemCount() {
        return mChannels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_channel_cover;
        public TextView tv_channle_title;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_channel_cover = (ImageView) itemView.findViewById(R.id.iv_channel_cover);
            tv_channle_title = (TextView) itemView.findViewById(R.id.tv_channle_title);
        }

    }
}
