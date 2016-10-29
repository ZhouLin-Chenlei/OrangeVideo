package com.community.yuequ.gui.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.gui.PicDetailActivity;
import com.community.yuequ.gui.RecommendFragment;
import com.community.yuequ.gui.VideoDetailActivity;
import com.community.yuequ.gui.VideoOrPicGroupActivity;
import com.community.yuequ.gui.VideoListActivity;
import com.community.yuequ.modle.RGroup;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.view.GroupImageView;
import com.community.yuequ.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private RecommendFragment mFragment;

    private final List<RGroup> mPrograms = new ArrayList<>();
    private static final int TYPE_HEAD_VIEW = 0;
    private static final int TYPE_LIST_VIDEO_2R = 1;
    private static final int TYPE_LIST_VIDEO_3R = 2;
    private static final int TYPE_LIST_TEXTPIC = 3;

//    private static final int TYPE_LIST_P2 = 4;
//    private static final int TYPE_LIST_P3 = 5;

    private View headView;
//    private View footView;

    public RecommendAdapter(RecommendFragment fragment) {
        mFragment = fragment;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_HEAD_VIEW:
                viewHolder = new HeadViewHolder(headView);
                break;

            case TYPE_LIST_VIDEO_2R:
                View movie2Row = LayoutInflater.from(parent.getContext()).inflate(R.layout.rl_list_movie_2row, parent, false);
                viewHolder = new Movie2RowViewHolder(movie2Row);

                break;
            case TYPE_LIST_VIDEO_3R:
                View movie3Row = LayoutInflater.from(parent.getContext()).inflate(R.layout.rl_list_movie_3row, parent, false);
                viewHolder = new Movie3RowViewHolder(movie3Row);

                break;
            default:
                View pic2Row = LayoutInflater.from(parent.getContext()).inflate(R.layout.rl_list_pic_2row, parent, false);
                viewHolder = new Pic2RowViewHolder(pic2Row);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeadViewHolder){


        }else  if (holder instanceof Movie2RowViewHolder) {
            Movie2RowViewHolder movie2RowViewHolder = ((Movie2RowViewHolder) holder);
            final RGroup rGroup = getItem(position);
            movie2RowViewHolder.tv_title.setText(rGroup.column_name);
            movie2RowViewHolder.gridView.setAdapter(new GridView2RowAdapter(rGroup.plist));
            movie2RowViewHolder.rl_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mFragment.getContext(),VideoOrPicGroupActivity.class);
                    intent.putExtra("column_id",rGroup.column_id);
                    intent.putExtra("type",rGroup.type);
                    intent.putExtra("column_name",rGroup.column_name);
                    mFragment.startActivity(intent);
                }
            });
            movie2RowViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object item = parent.getAdapter().getItem(position);
                    if(item instanceof RProgram){

                        RProgram rProgram = (RProgram) item;
                        if(Contants.SHOWTYPE_LINK.equals(rProgram.show_type)){
                            try {
                                String openurl = rProgram.link_url;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                                mFragment.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            Intent intent = new Intent(mFragment.getActivity(), VideoDetailActivity.class);
//                        intent.putExtra("name",rProgram.name);
//                        intent.putExtra("id",rProgram.id);
                            intent.putExtra("program",rProgram);
                            mFragment.startActivity(intent);
                        }
                    }
                }
            });


        }else  if (holder instanceof Movie3RowViewHolder) {
            Movie3RowViewHolder movie3RowViewHolder = ((Movie3RowViewHolder) holder);
           final RGroup rGroup = getItem(position);
            movie3RowViewHolder.tv_title.setText(rGroup.column_name);
            movie3RowViewHolder.gridView.setAdapter(new GridView3RowAdapter(rGroup.plist));

            movie3RowViewHolder.rl_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mFragment.getContext(),VideoOrPicGroupActivity.class);
                    intent.putExtra("column_id",rGroup.column_id);
                    intent.putExtra("type",rGroup.type);
                    intent.putExtra("column_name",rGroup.column_name);
                    mFragment.startActivity(intent);
                }
            });

            movie3RowViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object item = parent.getAdapter().getItem(position);
                    if(item instanceof RProgram){
                        RProgram rProgram = (RProgram) item;
                        if(Contants.SHOWTYPE_LINK.equals(rProgram.show_type)){
                            try {
                                String openurl = rProgram.link_url;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                                mFragment.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            Intent intent = new Intent(mFragment.getActivity(), VideoDetailActivity.class);
//                        intent.putExtra("name",rProgram.name);
//                        intent.putExtra("id",rProgram.id);
                            intent.putExtra("program", rProgram);
                            mFragment.startActivity(intent);
                        }
                    }
                }
            });

        }else  if (holder instanceof Pic2RowViewHolder) {
            Pic2RowViewHolder pic2RowViewHolder = ((Pic2RowViewHolder) holder);
            final RGroup rGroup = getItem(position);
            pic2RowViewHolder.tv_title.setText(rGroup.column_name);
            pic2RowViewHolder.rl_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mFragment.getContext(),VideoOrPicGroupActivity.class);
                    intent.putExtra("column_id",rGroup.column_id);
                    intent.putExtra("type",rGroup.type);
                    intent.putExtra("column_name",rGroup.column_name);
                    mFragment.startActivity(intent);
                }
            });
            pic2RowViewHolder.ll_preagram1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RProgram rProgram = rGroup.plist.get(0);
                    Intent intent = new Intent(mFragment.getActivity(), PicDetailActivity.class);
                    intent.putExtra("program",rProgram);
                    mFragment.startActivity(intent);
                }
            });

            pic2RowViewHolder.ll_preagram2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RProgram rProgram = rGroup.plist.get(1);
                    Intent intent = new Intent(mFragment.getActivity(), PicDetailActivity.class);
                    intent.putExtra("program",rProgram);
                    mFragment.startActivity(intent);
                }
            });
           if(rGroup.plist!=null && !rGroup.plist.isEmpty()){
               if(rGroup.plist.size()>0){
                   RProgram rProgram = rGroup.plist.get(0);

                   pic2RowViewHolder.ll_preagram1.setVisibility(View.VISIBLE);
                   pic2RowViewHolder.tv_second_title.setText(rProgram.name);
                   pic2RowViewHolder.tv_dsc.setText(rProgram.remark);
                   pic2RowViewHolder.mGroupImageView.setPics(rProgram.picList);
               }else{
                   pic2RowViewHolder.ll_preagram1.setVisibility(View.GONE);
               }

               if(rGroup.plist.size() > 1){
                   RProgram rProgram = rGroup.plist.get(1);

                   pic2RowViewHolder.ll_preagram2.setVisibility(View.VISIBLE);

                   pic2RowViewHolder.tv_second_title2.setText(rProgram.name);
                   pic2RowViewHolder.tv_dsc2.setText(rProgram.remark);
                   pic2RowViewHolder.mGroupImageView2.setPics(rProgram.picList);
               }else{
                   pic2RowViewHolder.ll_preagram2.setVisibility(View.GONE);
               }


           }
        }
    }

    public RGroup getItem(int position) {
        if (hasHeader()) {
            --position;
        }
        return mPrograms.get(position);
    }

    @Override
    public int getItemCount() {
        int size = mPrograms.size();
        if (hasHeader()) {
            size++;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if(isHeaderPosition(position)){
            return TYPE_HEAD_VIEW;
        }else if (getItem(position).tworow && "1".equals(getItem(position).type)) {
            return TYPE_LIST_VIDEO_2R;
        } else if ("1".equals(getItem(position).type)) {
            return TYPE_LIST_VIDEO_3R;
        }else{
            return TYPE_LIST_TEXTPIC;
        }
    }

    protected boolean hasHeader() {
        return getHeader() != null;
    }
    public View getHeader() {
        return headView;
    }
    protected boolean isHeaderType(int viewType) {
        return viewType == TYPE_HEAD_VIEW;
    }

    public boolean isHeaderPosition(int position) {
        return hasHeader() && position == 0;
    }

    public void addHeadView(View headView) {
//        if(hasHeader()){
//           return;
//        }
        this.headView = headView;
        notifyItemChanged(0);

    }

    public void setData(List<RGroup> programs) {
        boolean isFirst = true;
        for (RGroup group : programs){
            if("1".equals(group.type)){
                if(isFirst){
                    group.tworow = true;
                }else{
                    group.tworow = false;
                }
                isFirst = false;
            }
        }
        mPrograms.clear();
        mPrograms.addAll(programs);
        notifyDataSetChanged();
    }

    public class Movie2RowViewHolder extends RecyclerView.ViewHolder {
        public View rl_title;
        public TextView tv_title;
        public MyGridView gridView;
        public Movie2RowViewHolder(View itemView) {
            super(itemView);
            rl_title = itemView.findViewById(R.id.rl_title);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            gridView = (MyGridView) itemView.findViewById(R.id.gridView);

        }

    }
    public class Movie3RowViewHolder extends RecyclerView.ViewHolder {
        public View rl_title;
        public TextView tv_title;
        public MyGridView gridView;

        public Movie3RowViewHolder(View itemView) {
            super(itemView);
            rl_title = itemView.findViewById(R.id.rl_title);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            gridView = (MyGridView) itemView.findViewById(R.id.gridView);
        }

    }

    public class Pic2RowViewHolder extends RecyclerView.ViewHolder {
        public View rl_title;

        public View ll_preagram1;
        public TextView tv_title;
        public TextView tv_second_title;
        public TextView tv_dsc;
        public GroupImageView mGroupImageView;

        public View ll_preagram2;
        public TextView tv_second_title2;
        public TextView tv_dsc2;
        public GroupImageView mGroupImageView2;

        public Pic2RowViewHolder(View itemView) {
            super(itemView);
            rl_title = itemView.findViewById(R.id.rl_title);
            ll_preagram1 = itemView.findViewById(R.id.ll_preagram1);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_second_title = (TextView) itemView.findViewById(R.id.tv_second_title);
            tv_dsc = (TextView) itemView.findViewById(R.id.tv_dsc);
            mGroupImageView = (GroupImageView) itemView.findViewById(R.id.giv_image_group);
            ll_preagram2 = itemView.findViewById(R.id.ll_preagram2);
            tv_second_title2 = (TextView) itemView.findViewById(R.id.tv_second_title2);
            tv_dsc2 = (TextView) itemView.findViewById(R.id.tv_dsc2);
            mGroupImageView2 = (GroupImageView) itemView.findViewById(R.id.giv_image_group2);
        }

    }

    static class HeadViewHolder extends RecyclerView.ViewHolder {

        View headView;

        public HeadViewHolder(View view) {
            super(view);
            headView = view;
        }
    }
}
