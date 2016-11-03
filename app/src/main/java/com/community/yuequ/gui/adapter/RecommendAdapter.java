package com.community.yuequ.gui.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.gui.PicDetailActivity;
import com.community.yuequ.gui.RecommendFragment;
import com.community.yuequ.gui.VideoDetailActivity;
import com.community.yuequ.gui.VideoOrPicGroupActivity;
import com.community.yuequ.gui.VideoListActivity;
import com.community.yuequ.imple.HomeData;
import com.community.yuequ.modle.HomeItem;
import com.community.yuequ.modle.HomeOnline;
import com.community.yuequ.modle.HomeTitle;
import com.community.yuequ.modle.RGroup;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.view.GroupImageView;
import com.community.yuequ.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecommendFragment mFragment;

//    private final List<RGroup> mPrograms = new ArrayList<>();
    private final List<HomeData> mDatas = new ArrayList<>();
    private static final int TYPE_HEAD_VIEW = 0;
    private static final int TYPE_TITLE_VIEW = 1;
    private static final int TYPE_LIST_VIDEO_2R = 2;
    private static final int TYPE_LIST_VIDEO_3R = 3;
    private static final int TYPE_LIST_VIDEO_GRID = 4;
    private static final int TYPE_LIST_TEXTPIC = 5;

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
            case TYPE_TITLE_VIEW:
                View titleV = LayoutInflater.from(parent.getContext()).inflate(R.layout.rl_list_title, parent, false);
                viewHolder = new TitleViewHolder(titleV);

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
        if (holder instanceof HeadViewHolder) {


        }else if(holder instanceof  TitleViewHolder){
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            final HomeTitle rGroup = (HomeTitle) getItem(position);

            if("影视".equals(rGroup.titleName)){
                Drawable drawable = ContextCompat.getDrawable(mFragment.getContext(), R.mipmap.icon_htitle_yinshi);
//                /// 这一步必须要做,否则不会显示.
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                titleViewHolder.tv_title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }else if("娱乐".equals(rGroup.titleName)){
                Drawable drawable = ContextCompat.getDrawable(mFragment.getContext(), R.mipmap.icon_htitle_yule);
                titleViewHolder.tv_title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }else if("搞笑".equals(rGroup.titleName)){
                Drawable drawable = ContextCompat.getDrawable(mFragment.getContext(), R.mipmap.icon_htitle_gaoxiao);
                titleViewHolder.tv_title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }else{
                Drawable drawable = ContextCompat.getDrawable(mFragment.getContext(), R.mipmap.icon_htitle_online);
                titleViewHolder.tv_title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }

            titleViewHolder.tv_title.setText(rGroup.titleName);
            titleViewHolder.rl_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mFragment.getContext(), VideoOrPicGroupActivity.class);
                    intent.putExtra("column_id", rGroup.id);
                    intent.putExtra("type", rGroup.type);
                    intent.putExtra("column_name", rGroup.titleName);
                    mFragment.startActivity(intent);
                }
            });


        } else if (holder instanceof Movie2RowViewHolder) {
            Movie2RowViewHolder movie2RowViewHolder = ((Movie2RowViewHolder) holder);
            final HomeItem rGroup = (HomeItem) getItem(position);
            if(!rGroup.mPrograms.isEmpty()){
                final RProgram program = rGroup.mPrograms.get(0);

                movie2RowViewHolder.tv_name.setText(program.name);
                movie2RowViewHolder.tv_desc.setText(program.remark);
                ImageManager.getInstance().loadUrlImage(mFragment.getContext(),program.img_path,movie2RowViewHolder.iv_image);

                movie2RowViewHolder.ll_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Contants.SHOWTYPE_LINK.equals(program.show_type)) {
                            try {
                                String openurl = program.link_url;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                                mFragment.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VideoDetailActivity.class);
//                        intent.putExtra("name",rProgram.name);
//                        intent.putExtra("id",rProgram.id);
                            intent.putExtra("program", program);
                            mFragment.startActivity(intent);
                        }
                    }
                });

            }

            if(rGroup.mPrograms.size() > 1){
                final RProgram program2 = rGroup.mPrograms.get(1);

                movie2RowViewHolder.tv_name2.setText(program2.name);
                movie2RowViewHolder.tv_desc2.setText(program2.remark);
                ImageManager.getInstance().loadUrlImage(mFragment.getContext(),program2.img_path,movie2RowViewHolder.iv_image2);
                movie2RowViewHolder.ll_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Contants.SHOWTYPE_LINK.equals(program2.show_type)) {
                            try {
                                String openurl = program2.link_url;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                                mFragment.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VideoDetailActivity.class);
//                        intent.putExtra("name",rProgram.name);
//                        intent.putExtra("id",rProgram.id);
                            intent.putExtra("program", program2);
                            mFragment.startActivity(intent);
                        }
                    }
                });

            }

        } else if (holder instanceof Movie3RowViewHolder) {
            Movie3RowViewHolder movie3RowViewHolder = ((Movie3RowViewHolder) holder);
            final HomeItem rGroup = (HomeItem) getItem(position);
            if(!rGroup.mPrograms.isEmpty()){
                final RProgram program = rGroup.mPrograms.get(0);

                movie3RowViewHolder.tv_name.setText(program.name);
                movie3RowViewHolder.tv_desc.setText(program.remark);
                ImageManager.getInstance().loadUrlImage(mFragment.getContext(),program.img_path,movie3RowViewHolder.iv_image);

                movie3RowViewHolder.ll_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Contants.SHOWTYPE_LINK.equals(program.show_type)) {
                            try {
                                String openurl = program.link_url;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                                mFragment.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VideoDetailActivity.class);
//                        intent.putExtra("name",rProgram.name);
//                        intent.putExtra("id",rProgram.id);
                            intent.putExtra("program", program);
                            mFragment.startActivity(intent);
                        }
                    }
                });

            }

            if(rGroup.mPrograms.size() > 1){
                final RProgram program2 = rGroup.mPrograms.get(1);

                movie3RowViewHolder.tv_name2.setText(program2.name);
                movie3RowViewHolder.tv_desc2.setText(program2.remark);
                ImageManager.getInstance().loadUrlImage(mFragment.getContext(),program2.img_path,movie3RowViewHolder.iv_image2);
                movie3RowViewHolder.ll_mid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Contants.SHOWTYPE_LINK.equals(program2.show_type)) {
                            try {
                                String openurl = program2.link_url;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                                mFragment.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VideoDetailActivity.class);
//                        intent.putExtra("name",rProgram.name);
//                        intent.putExtra("id",rProgram.id);
                            intent.putExtra("program", program2);
                            mFragment.startActivity(intent);
                        }
                    }
                });

            }



            if(rGroup.mPrograms.size() > 2){
                final RProgram program3 = rGroup.mPrograms.get(2);

                movie3RowViewHolder.tv_name3.setText(program3.name);
                movie3RowViewHolder.tv_desc3.setText(program3.remark);
                ImageManager.getInstance().loadUrlImage(mFragment.getContext(),program3.img_path,movie3RowViewHolder.iv_image3);
                movie3RowViewHolder.ll_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Contants.SHOWTYPE_LINK.equals(program3.show_type)) {
                            try {
                                String openurl = program3.link_url;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                                mFragment.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent intent = new Intent(mFragment.getActivity(), VideoDetailActivity.class);
//                        intent.putExtra("name",rProgram.name);
//                        intent.putExtra("id",rProgram.id);
                            intent.putExtra("program", program3);
                            mFragment.startActivity(intent);
                        }
                    }
                });

            }


        } else if (holder instanceof Pic2RowViewHolder) {
            Pic2RowViewHolder pic2RowViewHolder = ((Pic2RowViewHolder) holder);
            final HomeOnline rGroup = (HomeOnline) getItem(position);


        }
    }

    public HomeData getItem(int position) {
        if (hasHeader()) {
            --position;
        }
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        int size = mDatas.size();
        if (hasHeader()) {
            size++;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return TYPE_HEAD_VIEW;
        }else {
            return getItem(position).getItemType();
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

   /* public void setData(List<RGroup> programs) {
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
*/

    public void setData(List<RGroup> programs) {
        List<HomeData> tempData = new ArrayList<>();
        boolean isFirst = true;
        for (RGroup group : programs) {

            HomeTitle title = new HomeTitle(group.column_id, group.column_name, group.type, TYPE_TITLE_VIEW);
            tempData.add(title);
            if ("1".equals(group.type) && group.plist!=null) {
                if (isFirst) {

                    int i = 0;
                    int index = 0;
                    while (index < group.plist.size() && i < 2) {
                        int row = 0;
                        i++;
                        HomeItem item = new HomeItem(TYPE_LIST_VIDEO_2R);

                        for (; index < group.plist.size(); index++) {

                            if (row > 1) {
                                break;
                            }
                            item.mPrograms.add(group.plist.get(index));
                            row++;
                        }

                        tempData.add(item);
                    }

                } else {
                    int i = 0;
                    int index = 0;
                    while (index < group.plist.size() && i < 2) {
                        int row = 0;
                        i++;
                        HomeItem item = new HomeItem(TYPE_LIST_VIDEO_3R);

                        for (; index < group.plist.size(); index++) {

                            if (row > 2) {
                                break;
                            }
                            item.mPrograms.add(group.plist.get(index));
                            row++;
                        }

                        tempData.add(item);
                    }
                }
                isFirst = false;
            }else{
                for (int i = 0; group.plist!=null && i < group.plist.size(); i++) {
                    HomeOnline item = new HomeOnline(TYPE_LIST_TEXTPIC);
                    item.mProgram = group.plist.get(i);
                    tempData.add(item);
                }
            }
        }
        mDatas.clear();
        mDatas.addAll(tempData);
        notifyDataSetChanged();
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        public View rl_title;
        public TextView tv_title;


        public TitleViewHolder(View itemView) {
            super(itemView);
            rl_title = itemView.findViewById(R.id.rl_title);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);

        }

    }
    public class Movie2RowViewHolder extends RecyclerView.ViewHolder {
        public View ll_left;
        public ImageView iv_image;
        public TextView tv_name;
        public TextView tv_desc;

        public View ll_right;
        public ImageView iv_image2;
        public TextView tv_name2;
        public TextView tv_desc2;
        public Movie2RowViewHolder(View itemView) {
            super(itemView);
            ll_left = itemView.findViewById(R.id.ll_left);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);

            ll_right = itemView.findViewById(R.id.ll_right);
            iv_image2 = (ImageView) itemView.findViewById(R.id.iv_image2);
            tv_name2 = (TextView) itemView.findViewById(R.id.tv_name2);
            tv_desc2 = (TextView) itemView.findViewById(R.id.tv_desc2);
        }

    }

    public class Movie3RowViewHolder extends RecyclerView.ViewHolder {
        public View ll_left;
        public ImageView iv_image;
        public TextView tv_name;
        public TextView tv_desc;

        public View ll_mid;
        public ImageView iv_image2;
        public TextView tv_name2;
        public TextView tv_desc2;

        public View ll_right;
        public ImageView iv_image3;
        public TextView tv_name3;
        public TextView tv_desc3;

        public Movie3RowViewHolder(View itemView) {
            super(itemView);
            ll_left = itemView.findViewById(R.id.ll_left);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);

            ll_mid = itemView.findViewById(R.id.ll_mid);
            iv_image2 = (ImageView) itemView.findViewById(R.id.iv_image2);
            tv_name2 = (TextView) itemView.findViewById(R.id.tv_name2);
            tv_desc2 = (TextView) itemView.findViewById(R.id.tv_desc2);

            ll_right = itemView.findViewById(R.id.ll_right);
            iv_image3 = (ImageView) itemView.findViewById(R.id.iv_image3);
            tv_name3 = (TextView) itemView.findViewById(R.id.tv_name3);
            tv_desc3 = (TextView) itemView.findViewById(R.id.tv_desc3);
        }

    }

    public class Pic2RowViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_image;
        public TextView tv_name;
        public TextView tv_desc;

        public Pic2RowViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);

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
