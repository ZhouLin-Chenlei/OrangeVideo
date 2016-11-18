package com.community.yuequ.gui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.R;
import com.community.yuequ.gui.adapter.HistoryListAdapter;
import com.community.yuequ.gui.adapter.VideoListAdapter;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.provider.History;
import com.community.yuequ.view.ContextMenuRecyclerView;
import com.community.yuequ.view.DividerItemDecoration;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.SwipeRefreshLayout;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    private static final String TAG = HistoryActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private TextView mTitleView;
    private PageStatuLayout mStatuLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    public ContextMenuRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HistoryListAdapter mListAdapter;
    private GetHistoryTask historyTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mStatuLayout = new PageStatuLayout(this)
                .setReloadListener(this)
                .hide();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitleView.setText(R.string.history);
        mRecyclerView = (ContextMenuRecyclerView) findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.addOnScrollListener(mScrollListener);
        mListAdapter = new HistoryListAdapter(this);
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.setLoadMoreViewVisibility(View.GONE);
        mListAdapter.setLoadMoreViewText(getString(R.string.loading_data));

        registerForContextMenu(mRecyclerView);


        loadHistory();
    }

    @Override
    public void onRefresh() {
        loadHistory();
    }

    private void loadHistory(){
        historyTask = new GetHistoryTask(this);
        historyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_status:
                loadHistory();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (menuInfo == null)
            return;
        // Do not show the menu of media group.
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo)menuInfo;
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.video_list, menu);

        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        return info != null && handleContextItemSelected(item, info.position);
    }
    private boolean handleContextItemSelected(MenuItem menu, final int position) {
        if (position >= mListAdapter.getItemCount()){
            return false;
        }
        RProgram item = mListAdapter.getItem(position);
        if (item == null)
            return false;
        int delete = History.deleteHistory(this,item.id);
        if(delete>0){
            mListAdapter.remove(position);

        }else{
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        }
        return true;

    }
    protected class GetHistoryTask extends AsyncTask<Void,Void,List<RProgram>>{
        private AppCompatActivity mActivity;

        public GetHistoryTask(AppCompatActivity activity) {
            mActivity = activity;
        }

        @Override
        protected List<RProgram> doInBackground(Void... voids) {
            return History.getHistorys(mActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            if(mListAdapter.getItemCount()<=1){
                mStatuLayout.show()
                        .setProgressBarVisibility(true)
                        .setText(null);
            }else {
                mStatuLayout.hide()
                        .setProgressBarVisibility(false)
                        .setText(null);
            }
        }

        @Override
        protected void onPostExecute(List<RProgram> histories) {
            super.onPostExecute(histories);

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            if(histories!=null){
                mListAdapter.setData(histories);
            }


            if (mStatuLayout != null) {
                if(mListAdapter.getItemCount()<=1){
                    mStatuLayout.show()
                            .setProgressBarVisibility(false)
                            .setText(getString(R.string.no_data));
                }else {
                    mStatuLayout.hide()
                            .setProgressBarVisibility(false)
                            .setText(null);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(List<RProgram> histories) {
            super.onCancelled(histories);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(historyTask!=null){
            historyTask.cancel(true);
        }
    }
}
