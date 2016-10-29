package com.community.yuequ.gui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.yuequ.R;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.modle.RProgram;

import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public class GridView3RowAdapter extends BaseAdapter{
    private List<RProgram> mPrograms;
    public GridView3RowAdapter(List<RProgram> list){
        this.mPrograms = list;
    }
    @Override
    public int getCount() {
        if(mPrograms==null){
            return 0;
        }
        if(mPrograms.size()>3){
            return 3;
        }
        return mPrograms.size();
    }

    @Override
    public Object getItem(int position) {
        return mPrograms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(parent.getContext(), R.layout.recommend_video_3row, null);
            viewHolder.iv_cover = (ImageView) convertView.findViewById(R.id.iv_movie_cover);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_movie_name);
            viewHolder.tv_des = (TextView) convertView.findViewById(R.id.tv_movie_dsc);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RProgram rProgram = mPrograms.get(position);
        viewHolder.tv_title.setText(rProgram.name);
        viewHolder.tv_des.setText(rProgram.remark);
        ImageManager.getInstance().loadUrlImage(parent.getContext(),rProgram.img_path,viewHolder.iv_cover);

        return convertView;
    }

    static class ViewHolder{
        ImageView iv_cover;
        TextView tv_title;
        TextView tv_des;
    }
}
