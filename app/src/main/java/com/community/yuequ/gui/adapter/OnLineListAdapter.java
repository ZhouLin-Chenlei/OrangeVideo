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
public class OnLineListAdapter extends RecyclerView.Adapter<OnLineListAdapter.ViewHolder>{
    private Activity mContext;
    private String type = "1";
    private List<VideoOrPicGroup> mList;
    public OnLineListAdapter(Activity activity){
        this.mContext = activity;
    }

    public void setType(String type){
        this.type = type;
    }
    @Override
    public OnLineListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_online_list , parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OnLineListAdapter.ViewHolder holder, int position) {
        final VideoOrPicGroup programa = mList.get(position);
        holder.tv_title.setText(programa.name);
        holder.tv_desc.setText("一段描述");
        ImageManager.getInstance().loadUrlImage(mContext,programa.img_path,holder.iv_img);

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
}
