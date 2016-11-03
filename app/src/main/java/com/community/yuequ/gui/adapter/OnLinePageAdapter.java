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
import com.community.yuequ.gui.OnLineListActivity;
import com.community.yuequ.gui.OnLinePageFragment;
import com.community.yuequ.gui.VideoOrPicGroupActivity;
import com.community.yuequ.modle.RTextImage;
import com.community.yuequ.modle.VideoOrPicGroup;

import java.util.List;

public class OnLinePageAdapter extends RecyclerView.Adapter<OnLinePageAdapter.ViewHolder> {
    private OnLinePageFragment mFragment;
    private List<RTextImage> mRTextImages;


    public OnLinePageAdapter(OnLinePageFragment fragment, List<RTextImage> programas) {
        super();
        mFragment = fragment;
        mRTextImages = programas;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.yq_video_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RTextImage programa = mRTextImages.get(position);
        String title = programa.name+"("+programa.program_cnt+")";
        holder.tv_label.setText(title);
        if(!TextUtils.isEmpty(programa.content_desc)) {
            holder.tv_group_desc.setText(programa.content_desc);
        }
        ImageManager.getInstance().loadUrlImage(mFragment, programa.img_path, holder.iv_groupcover);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(),OnLineListActivity.class);
                intent.putExtra("column_id",programa.id);
                intent.putExtra("type","2");//图文
                intent.putExtra("column_name",programa.name);
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
        return mRTextImages.size();
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
