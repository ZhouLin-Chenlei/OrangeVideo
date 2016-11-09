package com.community.yuequ.gui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.yuequ.R;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.gui.VideoSecondGroupActivity;
import com.community.yuequ.gui.VideoPageFragment;
import com.community.yuequ.modle.OrVideoGroup;

import java.util.List;

public class VideoPageAdapter extends RecyclerView.Adapter<VideoPageAdapter.ViewHolder> {
    private VideoPageFragment mFragment;
    private List<OrVideoGroup> mVideoOrPicGroups;


    public VideoPageAdapter(VideoPageFragment fragment, List<OrVideoGroup> list) {
        super();
        mFragment = fragment;
        mVideoOrPicGroups=list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.yq_video_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OrVideoGroup rTextImage = mVideoOrPicGroups.get(position);
        String title = rTextImage.name+"("+rTextImage.program_cnt+")";

        holder.tv_label.setText(title);
        if(!TextUtils.isEmpty(rTextImage.content_desc)){
            holder.tv_group_desc.setText(rTextImage.content_desc);
        }

        ImageManager.getInstance().loadUrlImage(mFragment,rTextImage.img_path,holder.iv_groupcover);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(),VideoSecondGroupActivity.class);
                intent.putExtra("column_id",rTextImage.id);
                intent.putExtra("type","1");//视频
                intent.putExtra("column_name",rTextImage.name);
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
        return mVideoOrPicGroups.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_groupcover;
        public TextView tv_label;
        public TextView tv_title1;
        public TextView tv_title2;
        public TextView tv_group_desc;
        public TextView tv_more;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_groupcover = (ImageView) itemView.findViewById(R.id.iv_groupcover);
            tv_label = (TextView) itemView.findViewById(R.id.tv_label);
            tv_title1 = (TextView) itemView.findViewById(R.id.tv_title1);
            tv_title2 = (TextView) itemView.findViewById(R.id.tv_title2);
            tv_group_desc = (TextView) itemView.findViewById(R.id.tv_group_desc);
            tv_more = (TextView) itemView.findViewById(R.id.tv_more);
        }

    }
}
