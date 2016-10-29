package com.community.yuequ.gui.adapter;

import android.app.Activity;
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
import com.community.yuequ.gui.PicListActivity;
import com.community.yuequ.gui.VideoListActivity;
import com.community.yuequ.modle.VideoOrPicGroup;

import java.util.List;

/**
 * modou
 */
public class VideoOrPicGroupAdapter extends RecyclerView.Adapter<VideoOrPicGroupAdapter.ViewHolder>{
    private Activity mContext;
    private String type = "1";
    private List<VideoOrPicGroup> mList;
    public VideoOrPicGroupAdapter(Activity activity){
        this.mContext = activity;
    }

    public void setType(String type){
        this.type = type;
    }
    @Override
    public VideoOrPicGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.video_group_card , parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VideoOrPicGroupAdapter.ViewHolder holder, int position) {
        final VideoOrPicGroup programa = mList.get(position);
        holder.tv_group_name.setText(programa.name);
        ImageManager.getInstance().loadUrlImage(mContext,programa.img_path,holder.iv_grouppic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if("1".equals(type)){
                    intent.setClass(mContext,VideoListActivity.class);
                }else{
                    intent.setClass(mContext,PicListActivity.class);
                }
                intent.putExtra("column_id",programa.id);
                intent.putExtra("type",type);//1:视频;2:图文
                intent.putExtra("column_name",programa.name);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mList==null)return 0;
        return mList.size();
    }

    public void setData(List<VideoOrPicGroup> result) {
        mList = result;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_grouppic;
        public TextView tv_group_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_grouppic = (ImageView) itemView.findViewById(R.id.iv_grouppic);
            tv_group_name = (TextView) itemView.findViewById(R.id.tv_group_name);
        }
    }
}
