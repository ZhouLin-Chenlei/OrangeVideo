package com.community.yuequ.gui;


import android.support.v4.app.Fragment;

/**
 *
 */
public abstract class BaseTabFragment extends Fragment {
    protected volatile boolean mReadyToDisplay = true;
    protected volatile boolean isLoading;

    /**
     * 加载要显示的数据
     */
    protected abstract void initData();
    protected void getDataBefore() {
        isLoading = true;
    }

    protected void getDataFail() {

    }
    protected void getDataAfter() {
        isLoading = false;
    }

    @Override
    public void onDestroyView() {
        isLoading = false;
        super.onDestroyView();
    }

    public void setReadyToDisplay(boolean ready) {
        if (ready && !mReadyToDisplay){
//            display();

        }
        else{
            mReadyToDisplay = ready;

        }
    }

//    protected abstract void display();




}
