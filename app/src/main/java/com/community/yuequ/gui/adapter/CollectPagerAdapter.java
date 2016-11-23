

package com.community.yuequ.gui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.community.yuequ.R;
import com.community.yuequ.gui.OnlineCollectFragment;
import com.community.yuequ.gui.VedioCollectFragment;


public class CollectPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private VedioCollectFragment mVedioCollectFragment;
    private OnlineCollectFragment mOnLineCollectFragment;


    public CollectPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        mVedioCollectFragment = VedioCollectFragment.newInstance();
        mOnLineCollectFragment = OnlineCollectFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mVedioCollectFragment;
           default:
               return mOnLineCollectFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    // Workaround to refresh views with notifyDataSetChanged()
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.tab_vediocollect);
            default:
                return mContext.getString(R.string.tab_onlinecollect);
        }
    }

}