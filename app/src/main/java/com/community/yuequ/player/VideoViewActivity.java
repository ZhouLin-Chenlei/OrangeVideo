/*
 * Copyright (C) 2012 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.community.yuequ.player;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.community.yuequ.R;
import com.community.yuequ.imple.PlayData;
import com.community.yuequ.modle.RProgramDetail;

import java.util.ArrayList;

import io.vov.vitamio.Vitamio;

/**
 * Vitamio播放器官网 https://www.vitamio.org/
 * 开发建议 https://www.vitamio.org/docs/Tutorial/2014/0423/32.html 其中第5项可以简单减少包的体积
 * 定制服务介绍 https://www.vitamio.org/License/
 * @author zl
 *
 */

@SuppressLint("HandlerLeak")
public class VideoViewActivity extends AppCompatActivity{
	private Toolbar mToolbar;
	private TextView mTitleView;
	private WhtVideoView whtVideoView;
	private boolean mCreated = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Vitamio.isInitialized(getApplicationContext());

		loadView(R.layout.activity_video);
		mCreated = true;

		RProgramDetail programDetail = (RProgramDetail) getIntent().getSerializableExtra("programDetail");
		if(programDetail!=null){

			ArrayList<PlayData> programDetails = new ArrayList<>();
			programDetails.add(programDetail);
			whtVideoView.open(this, false, programDetails);
			whtVideoView.play(0);
			mTitleView.setText(programDetail.name);

		}


	}


	@Override
	protected void onPause() {
		super.onPause();
		if (!mCreated)
			return;
		if (whtVideoView != null) {
			whtVideoView.onPause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!mCreated)
			return;
		if (whtVideoView != null) {
			whtVideoView.releaseSurface();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (whtVideoView != null) {
		// whtVideoView.onResume();
		// }
		if (!mCreated)
			return;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (whtVideoView != null)
			whtVideoView.release(false);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			whtVideoView.setIsFullScreen(true);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			whtVideoView.setIsFullScreen(false);
		}
	}

	private void loadView(int id) {
		setContentView(id);
		getWindow().setBackgroundDrawable(null);
		whtVideoView = (WhtVideoView) findViewById(R.id.whtvideoview);
		whtVideoView.setVDVideoViewContainer((ViewGroup) whtVideoView
				.getParent());

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("");
		setSupportActionBar(mToolbar);
		mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);

	}

	@Override
	public void onBackPressed() {

		if (whtVideoView != null && whtVideoView.onBackPressed()) {

		} else {
			super.onBackPressed();

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
}
