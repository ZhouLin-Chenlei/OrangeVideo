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
import com.community.yuequ.gui.VideoOrPicGroupActivity;
import com.community.yuequ.gui.YQImageFragment;
import com.community.yuequ.modle.RTextImage;

import java.util.List;

public class YQImageAdapter extends RecyclerView.Adapter<YQImageAdapter.ViewHolder> {
    private YQImageFragment mFragment;
    private List<RTextImage> rTextImages;


    public YQImageAdapter(YQImageFragment fragment, List<RTextImage> list) {
        super();
        mFragment = fragment;
        rTextImages=list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.yq_video_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RTextImage rTextImage = rTextImages.get(position);
        String title = rTextImage.name+"（"+rTextImage.program_cnt+"）";

        holder.ml_item_title.setText(title);
        holder.ml_item_detail.setText(rTextImage.content_desc);

        ImageManager.getInstance().loadUrlImage(mFragment,rTextImage.img_path,holder.ml_item_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(),VideoOrPicGroupActivity.class);
                intent.putExtra("column_id",rTextImage.id);
                intent.putExtra("type","2");//图文
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
        return rTextImages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ml_item_image;
        public TextView ml_item_title;
        public TextView ml_item_detail;
        public ViewHolder(View itemView) {
            super(itemView);
            ml_item_image = (ImageView) itemView.findViewById(R.id.ml_item_image);
            ml_item_title = (TextView) itemView.findViewById(R.id.ml_item_title);
            ml_item_detail = (TextView) itemView.findViewById(R.id.ml_item_detail);
        }

    }
}
