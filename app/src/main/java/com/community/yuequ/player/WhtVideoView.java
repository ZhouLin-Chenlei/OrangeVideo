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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.R;
import com.community.yuequ.modle.RProgramDetail;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;


import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.MediaPlayer.ShownHideListener;
import io.vov.vitamio.utils.StringUtils;
/**
 * 视频SDK的基本容器类，是一个FrameLayout，一般包含三层，按照从下往上的顺序：<br>
 * 第一层：自定义的VideoView，只是用来播放视频<br>
 * 第二层：控制层：一堆按钮集合，在这一层实现所有的控制单元<br>
 * 第三层：显示一些信息
 * @author zhoulin
 *
 *
 */
public class WhtVideoView extends FrameLayout implements OnPreparedListener,
		OnCompletionListener, OnInfoListener, OnErrorListener,
		OnSeekCompleteListener, OnBufferingUpdateListener, OnTimedTextListener,
		OnVideoSizeChangedListener, ShownHideListener {

	private Activity mContext;

	private View mMediaController;
	private ImageButton mLock;
	private ImageButton mScreenToggle;
	private ImageButton mVideoback;
	private SeekBar mProgress;
	private TextView mEndTime,mCurrentTime;
	private ImageButton mPauseButton;
	private ListView mProgramlist;
	private ProgramAdapter adapter;
	/**
	 * 全屏控制布局
	 */
	private View mControlsLayout;
	private View mSystemInfoLayout;

	private View mMenu;
	// private TextView mDateTime;
	// private TextView mDownloadRate;
	private TextView mFileName;
	// private TextView mBatteryLevel;

	private TextView mOperationInfo;
	private View mOperationVolLum;
	private ImageView mVolLumNum;
	private ImageView mVolLumBg;

	private long mDuration;
	private boolean mShowing;
	private boolean mScreenLocked = false;
	private boolean mDragging;
	private boolean mInstantSeeking = true;
	private static final int DEFAULT_TIME_OUT = 5000;
	private static final int DEFAULT_LONG_TIME_SHOW = 120000;
	private static final int DEFAULT_MID_TIME_SHOW = 15000;
	private static final int DEFAULT_SEEKBAR_VALUE = 1000;
	private static final int TIME_TICK_INTERVAL = 1000;
	private TextView mLiveTextView;
	private ProgressBar pb;
	private TextView loadRateView;

	/** 全屏的时候 VDVideoView 的直接父亲 */
	private LinearLayout mVideoFullScreenContainer = null;
	private ViewGroup.LayoutParams mVideoViewParams = null;
	/** 小屏时VDVideoView 的直接父亲 */
	private ViewGroup mVDVideoViewContainer = null;
	private ViewGroup mExternalFullScreenContainer;

	private AudioManager mAM;
	private int mMaxVolume;
	private float mBrightness = 0.01f;
	private int mVolume = 0;
	private Handler mHandler;

	private Animation mAnimSlideInTop;
	private Animation mAnimSlideInBottom;
	private Animation mAnimSlideOutTop;
	private Animation mAnimSlideOutBottom;

	private CommonGestures mGestures;
//	private int mVideoMode = VideoView.VIDEO_LAYOUT_ORIGIN;

	private VideoView mVideoView;

	private boolean isLive = false;// 是不是直播
	// private boolean isPermission = true;
	private TelephonyManager mTelephonyManager;
	private ScreenReceiver mScreenReceiver;
	private boolean mReceiverRegistered = false;

	private final ArrayList<RProgramDetail> videoBeans = new ArrayList<RProgramDetail>();
	private Uri mUri = null;

	private int mIndex = 0;
	private boolean mEnd = false;
	// private OnBuyListener onBuyListener;
	private OnIndexChangeListener indexChangeListener;
	private OnCompletionListener onCompletionListener;
	private float mSeekTo = -1f;
	public static final IntentFilter SCREEN_FILTER = new IntentFilter(
			Intent.ACTION_SCREEN_ON);
	static {
		SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_OFF);
	}

	public WhtVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = (Activity) context;
		setBackgroundColor(0x0);
		init();
		View contorller = makeControllerView();
		initControllerView(contorller);

		if (mVideoView == null) {
			mVideoView = new VideoView(context);
			mVideoView.setOnCompletionListener(this);
			mVideoView.setOnPreparedListener(this);
			mVideoView.setOnInfoListener(this);
			mVideoView.setOnErrorListener(this);
			mVideoView.setOnSeekCompleteListener(this);
			mVideoView.setOnBufferingUpdateListener(this);
			mVideoView.setShownHideListener(this);
			initVedio();
		}
		if (((View) mVideoView).getParent() == null) {
			addVideoView(mVideoView);
		}
	}

	public WhtVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = (Activity) context;
		setBackgroundColor(0x0);
		init();
		View contorller = makeControllerView();
		initControllerView(contorller);

		if (mVideoView == null) {
			mVideoView = new VideoView(context);
			mVideoView.setOnCompletionListener(this);
			mVideoView.setOnPreparedListener(this);
			mVideoView.setOnInfoListener(this);
			mVideoView.setOnErrorListener(this);
			mVideoView.setOnSeekCompleteListener(this);
			mVideoView.setOnBufferingUpdateListener(this);
			mVideoView.setShownHideListener(this);
			mVideoView.setSizeChangedListener(this);
			initVedio();
		}
		if (((View) mVideoView).getParent() == null) {
			addVideoView(mVideoView);
		}
	}

	private class ScreenReceiver extends BroadcastReceiver {
		private boolean screenOn = true;

		@Override
		public void onReceive(Context context, Intent intent) {
//			WhtLog.i("TAG", "ScreenReceiver:"+intent.getAction());
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				screenOn = false;
				pause();
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				screenOn = true;
			}
		}
	}

	private PhoneStateListener mPhoneListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// WhtLog.i("TAG", "PhoneStateListener:"+state);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_RINGING:
				pause();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 初始化数据
	 */
	private void init() {
		mVideoFullScreenContainer = new LinearLayout(mContext);
		mVideoFullScreenContainer.setOrientation(LinearLayout.HORIZONTAL);

		mVideoFullScreenContainer.setBackgroundColor(0x000000);
		mVideoFullScreenContainer.setVisibility(View.GONE);
	}

	/**
	 * 初始化屏幕部分，保证横屏进入正常
	 */
	public void initVedio() {

		manageReceivers();
		int screenOrientation = getScreenOrientation();
		boolean isFullScreen = false;
		if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			isFullScreen = true;
		} else if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			isFullScreen = false;
		}
		setIsFullScreen(isFullScreen, true);
	}

	private void manageReceivers() {

		if (!mReceiverRegistered) {
			mScreenReceiver = new ScreenReceiver();
			mContext.registerReceiver(mScreenReceiver, SCREEN_FILTER);

			mTelephonyManager = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			mTelephonyManager.listen(mPhoneListener,
					PhoneStateListener.LISTEN_CALL_STATE);
			mReceiverRegistered = true;

		}
	}

	private void unRegister() {
		if (mReceiverRegistered) {
			try {
				if (mScreenReceiver != null)
					mContext.unregisterReceiver(mScreenReceiver);

			} catch (IllegalArgumentException e) {
			}
			mReceiverRegistered = false;
		}
	}

	/**
	 * 视频直播界面设置
	 *
	 * @param isLive
	 */
	public void setIsLive(boolean isLive) {

		if (isLive) {
			mLiveTextView.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
			mCurrentTime.setVisibility(View.GONE);
			mEndTime.setVisibility(View.GONE);
		} else {
			mLiveTextView.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
			mCurrentTime.setVisibility(View.VISIBLE);
			mEndTime.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 横竖屏切换，从onConfigurationChanged入口进来的
	 *
	 * @param isFullScreen
	 *            true 表全屏；false 表非全屏
	 */
	public void setIsFullScreen(boolean isFullScreen) {
		setIsFullScreen(isFullScreen, false);
	}

	/**
	 * 增加一个私有函数，用来做initVideo时候的调用专用
	 *
	 * @param isFullScreen
	 * @param isInited
	 */
	private void setIsFullScreen(boolean isFullScreen, boolean isInited) {

		if (isFullScreen) {// 全屏
			final int h = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 45, getResources()
							.getDisplayMetrics());
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mControlsLayout
					.getLayoutParams();
			params.height = h;
			mControlsLayout.setLayoutParams(params);
			// if(isShowing()){
			// mControlsLayout.setVisibility(View.VISIBLE);
			mSystemInfoLayout.setVisibility(View.VISIBLE);
			// }
			mLock.setVisibility(View.VISIBLE);
			mControlsLayout.requestLayout();

		} else {// 竖屏
			final int h = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 34, getResources()
							.getDisplayMetrics());
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mControlsLayout
					.getLayoutParams();
			params.height = h;
			mControlsLayout.setLayoutParams(params);
			// mControlsLayout.setVisibility(View.VISIBLE);
			mSystemInfoLayout.setVisibility(View.GONE);
			mLock.setVisibility(View.GONE);
			mProgramlist.setVisibility(View.GONE);
			mControlsLayout.requestLayout();
		}
		if (!isInited) {
			// 回调接口，实现相应的横竖屏转换通知

		}

		if (mVDVideoViewContainer == null) {
			return;
		}

		// 设置当前的转屏方式
		setIsFullModeUsingContainer(mVDVideoViewContainer, isFullScreen);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mGestures != null) {
			mGestures.setWidthHeight(getWidth(), getHeight());
		}
	}

	/**
	 * 设置当前vdvideoview的父类容器，[只能手动指定，否则，再第一次进入即为横屏时候，无法处理]
	 *
	 * @param container
	 */
	public void setVDVideoViewContainer(ViewGroup container) {
		mVDVideoViewContainer = container;
	}

	/**
	 * 具体的转屏执行函数--使用父窗口方式
	 *
	 * @param container
	 * @param isFullScreen
	 */
	private void setIsFullModeUsingContainer(ViewGroup container,
			boolean isFullScreen) {
		if (container == null) {
			throw new IllegalArgumentException("container is null");
		}
		if (mVideoViewParams == null) {
			mVideoViewParams = getLayoutParams();
		}
		mVideoView.beginChangeParentView();

		if (mVideoFullScreenContainer != null) {
			mVideoFullScreenContainer.removeAllViews();
		}

		if (mVideoFullScreenContainer.getParent() == null) {
			// container.addView(mVideoFullScreenContainer,
			// ViewGroup.LayoutParams.MATCH_PARENT,
			// ViewGroup.LayoutParams.MATCH_PARENT);
			// 将全屏控件提升到最顶上的viewgroup里面
			changeToRoot(mVideoFullScreenContainer);
		}

		if (isFullScreen) {
			if (mExternalFullScreenContainer != null) {
				mExternalFullScreenContainer.setVisibility(VISIBLE);
			}

			mVideoFullScreenContainer.setVisibility(View.VISIBLE);

			container.removeView(this);
			mVideoFullScreenContainer.addView(this,
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
		} else {
			if (mExternalFullScreenContainer != null) {
				mExternalFullScreenContainer.setVisibility(GONE);
			}

			mVideoFullScreenContainer.setVisibility(View.GONE);
			// 竖屏
			if (getParent() == null) {
				container.addView(this, mVideoViewParams);
			}
		}

		mVideoView.endChangeParentView();
		mVideoView.requestVideoLayout();
		// if (prePlayerStatus != VDPlayerInfo.PLAYER_PAUSING) {
		// VDVideoViewController.getInstance().setScreenOrientationPause(false);
		// VDVideoViewController.getInstance().resume();
		// }
	}

	/**
	 * 将当前给定的容器，提升到activity的顶层容器中
	 *
	 * @param view
	 */
	private void changeToRoot(View view) {
		if (mContext == null || view == null) {
			return;
		}
		Activity activity = (Activity) mContext;
		ViewGroup vgRoot = null;
		if (mExternalFullScreenContainer == null) {
			vgRoot = (ViewGroup) activity.findViewById(android.R.id.content);
		} else {
			vgRoot = mExternalFullScreenContainer;
			mExternalFullScreenContainer.setVisibility(View.VISIBLE);
		}
		if (vgRoot != null) {
			try {
				ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				vgRoot.addView(view, lp);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 释放资源
	 */
	public void release(boolean isOnlyReloadVideo) {

		if (mHandler != null)
			mHandler.removeCallbacksAndMessages(null);

		if (mVideoView != null) {
			mVideoView.reset();

		}
		if (!isOnlyReloadVideo) {
			isLive = false;
			mUri = null;
			videoBeans.clear();
			mIndex = 0;

			if (mVideoView != null) {
				removeView((View) mVideoView);
			}
			unRegister();
			mVideoView = null;
			// if (DeviceUtils.hasICS()) {
			// android.os.Process.killProcess(android.os.Process.myPid());
			// }
		}

	}

	public void stop() {
		if (mVideoView != null) {
			// WhtLog.i("TAG", "stop----");
			savePosition();
			mVideoView.stopPlayback();
			updatePausePlay();
		}
	}

	public void releaseSurface() {
		if (mVideoView != null && mVideoView.isInPlaybackState()) {
			if (mVideoView.isInPlaybackState()) {
				savePosition();
			}
			mVideoView.stopPlayback();
			mVideoView.releaseSurface();
		}

	}

	private void addVideoView(VideoView vv) {
		if (vv == null) {
			return;
		}
		setBackgroundColor(Color.BLACK);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		addView((VideoView) vv, 0, lp);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);

	}

	private void initControllerView(View v) {
		mHandler = new MHandler(this);
		mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mGestures = new CommonGestures(mContext);
		mGestures.setTouchListener(mTouchListener, true);

		mAnimSlideOutBottom = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_out_bottom);
		mAnimSlideOutTop = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_out_top);
		mAnimSlideInBottom = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_bottom);
		mAnimSlideInTop = AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_top);
		mAnimSlideOutTop.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {

				mMediaController.setVisibility(View.GONE);
				// showButtons(false);
				mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
				mHandler.sendEmptyMessageDelayed(MSG_HIDE_SYSTEM_UI,
						DEFAULT_TIME_OUT);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		mMediaController = v.findViewById(R.id.mediacontroller);

		mSystemInfoLayout = v.findViewById(R.id.info_panel);

		mEndTime = (TextView) v
				.findViewById(R.id.mediacontroller_time_total);
		mCurrentTime = (TextView) v
				.findViewById(R.id.mediacontroller_time_current);
		mMenu = v.findViewById(R.id.video_menu);
		mMenu.setOnClickListener(mMenuListener);
		// FractionalTouchDelegate.setupDelegate(mSystemInfoLayout, mMenu,
		// new RectF(1.0f, 1f, 1.2f, 1.2f));

		mFileName = (TextView) v.findViewById(R.id.mediacontroller_file_name);
		// mDateTime = (TextView) v.findViewById(R.id.date_time);
		// mDownloadRate = (TextView) v.findViewById(R.id.download_rate);
		// mBatteryLevel = (TextView) v.findViewById(R.id.battery_level);

		mControlsLayout = v.findViewById(R.id.mediacontroller_controls);

		mOperationInfo = (TextView) v.findViewById(R.id.operation_info);
		mOperationVolLum = v.findViewById(R.id.operation_volume_brightness);
		mVolLumBg = (ImageView) v.findViewById(R.id.operation_bg);
		mVolLumNum = (ImageView) v.findViewById(R.id.operation_percent);

		mLock = (ImageButton) v.findViewById(R.id.mediacontroller_lock);
		mLock.setOnClickListener(mLockClickListener);
		// FractionalTouchDelegate.setupDelegate(mSystemInfoLayout, mLock,
		// new RectF(1.0f, 1f, 1.2f, 1.2f));

		mScreenToggle = (ImageButton) v
				.findViewById(R.id.mediacontroller_screen_size);
		mScreenToggle.setOnClickListener(mScreenToggleListener);

		mPauseButton = (ImageButton) v
				.findViewById(R.id.mediacontroller_play_pause);
		mPauseButton.setOnClickListener(mPauseListener);

		mProgress = (SeekBar) v.findViewById(R.id.mediacontroller_seekbar);
		mProgress.setOnSeekBarChangeListener(mSeekListener);
		mProgress.setMax(DEFAULT_SEEKBAR_VALUE);
		mLiveTextView = (TextView) v.findViewById(R.id.live_textview);
		mLiveTextView.setVisibility(View.GONE);

		mVideoback = (ImageButton) v.findViewById(R.id.video_back);
		mVideoback.setOnClickListener(mVideoBackClickListener);

		pb = (ProgressBar) v.findViewById(R.id.probar);
		loadRateView = (TextView) v.findViewById(R.id.load_rate);

		mProgramlist = (ListView) v.findViewById(R.id.programlist);
		mProgramlist.setVisibility(View.GONE);

		mMediaController.setVisibility(View.GONE);
	}

	/**
	 * Set the view that acts as the anchor for the control view. This can for
	 * example be a VideoView, or your Activity's main view.
	 *
	 * @param view
	 *            The view to which to anchor the controller when it is visible.
	 */
	public void setAnchorView(View view) {

		// mAnchor = view;
		// if (!mFromXml) {
		// removeAllViews();
		// mRoot = makeControllerView();
		// mWindow.setContentView(mRoot);
		// mWindow.setWidth(LayoutParams.MATCH_PARENT);
		// mWindow.setHeight(LayoutParams.WRAP_CONTENT);
		// }
		// initControllerView(mRoot);
	}

	private CommonGestures.TouchListener mTouchListener = new CommonGestures.TouchListener() {
		private boolean wasStopped = false;
		long mVideo_start_length = 0;
		int mSpeed = 0;

		@Override
		public void onGestureBegin() {

			mBrightness = ((Activity) mContext).getWindow().getAttributes().screenBrightness;
			mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;
			if (mVolume < 0)
				mVolume = 0;
		}

		@Override
		public void onGestureEnd() {

			mOperationVolLum.setVisibility(View.GONE);
			// TODO onGestureEnd
			if (isLive) {
				return;
			}
			if (mDragging) {

//				if (!isPermission && mVideo_start_length >= getFreeTime()) {
//					// WhtLog.i("TAG", "onGestureEnd");
//					pause();
//					showbuy();
//
//				} else {
					mVideoView.seekTo(mVideo_start_length);
//				}

				mOperationInfo.setVisibility(View.GONE);
				mVideo_start_length = 0;
				show(DEFAULT_TIME_OUT);
				// mHandler.removeMessages(MSG_SHOW_PROGRESS);
				mDragging = false;
				mSpeed = 0;
				// mHandler.sendEmptyMessageDelayed(MSG_SHOW_PROGRESS, 1000);
			}

		}

		@Override
		public void onLeftSlide(float percent) {
			setBrightness(mBrightness + percent);
			setBrightnessScale(((Activity) mContext).getWindow()
					.getAttributes().screenBrightness);
		}

		@Override
		public void onRightSlide(float percent) {
			int v = (int) (percent * mMaxVolume) + mVolume;
			setVolume(v);
		}

		@Override
		public void onSingleTap() {
			if (mShowing)
				hide();
			else
				show();

		}

		@Override
		public void onDoubleTap() {
			int aspectRatio = toggleAspectRatio();
			String aspectRatioText = MeasureHelper.getAspectRatioText(mContext, aspectRatio);
			setOperationInfo(aspectRatioText, 500);
		}

		@Override
		public void onLongPress() {
			// doPauseResume();
		}

		@Override
		public void onScale(float scaleFactor, int state) {
			switch (state) {
				case CommonGestures.SCALE_STATE_BEGIN:
					// mVideoMode = VideoView.VIDEO_LAYOUT_FIT_PARENT;
					// toggleVideoMode(mVideoMode);
					break;
				case CommonGestures.SCALE_STATE_SCALEING:
					// float currentRatio = mPlayer.scale(scaleFactor);
					// setOperationInfo((int) (currentRatio * 100) + "%", 500);
					break;
				case CommonGestures.SCALE_STATE_END:
					break;
			}
		}

		@Override
		public void onSpeedX(float distanceX) {
			if (isLive) {
				return;
			}

//			Log.i("TAG", "distanceX:" + distanceX);
			long mVideo_total_length = mVideoView.getDuration();//总长度
			long mVideo_current_length = mVideoView.getCurrentPosition();// 当前播放长度
			if (distanceX > 0) {// 往左滑动 --
				mSpeed = (int) (mSpeed - distanceX);
			} else if (distanceX < 0) {// 往右滑动 ++
				mSpeed = (int) (mSpeed + Math.abs(distanceX));
			}
			int i = mSpeed * 1000;// 快进长度

			mVideo_start_length = mVideo_current_length +i;// 快进之后长度
			if (mVideo_start_length >= mVideo_total_length) {
				mVideo_start_length = mVideo_total_length;
			} else if (mVideo_start_length <= 0) {
				mVideo_start_length = 0;
			}


//			String start_length = length2time(mVideo_start_length);

			String time = StringUtils.generateTime(mVideo_start_length);

			setOperationInfo(time, 1500);

		}

		@Override
		public void onGestureBeginX() {
			if (isLive) {
				return;
			}
			mSpeed = 0;
			mDragging = true;

			// show(3600000);
			// mHandler.removeMessages(MSG_SHOW_PROGRESS);
			wasStopped = !mVideoView.isPlaying();
			// if (mInstantSeeking) {
			// mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
			// if (wasStopped) {
			// mVideoView.start();
			// }
			// }

		}

	};

	/**
	 * Create the view that holds the widgets that control playback. Derived
	 * classes can override this to create their own.
	 *
	 * @return The controller view.
	 */
	private View makeControllerView() {
		LayoutInflater inflater = ((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		return inflater.inflate(R.layout.mediacontroller, this);
	}

	private View.OnClickListener mMenuListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mProgramlist.isShown()) {
				show(DEFAULT_TIME_OUT);
				mProgramlist.setVisibility(View.GONE);
			} else {
				show(DEFAULT_MID_TIME_SHOW);
				mProgramlist.setVisibility(View.VISIBLE);
			}
		}
	};

	private void setOperationInfo(String info, long time) {
		mOperationInfo.setText(info);
		mOperationInfo.setVisibility(View.VISIBLE);
		mHandler.removeMessages(MSG_HIDE_OPERATION_INFO);
		mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_INFO, time);
	}

	private void setBrightnessScale(float scale) {
		setGraphicOperationProgress(R.drawable.video_brightness_bg, scale);
	}

	private void setVolumeScale(float scale) {
		setGraphicOperationProgress(R.drawable.video_volumn_bg, scale);
	}

	private void setGraphicOperationProgress(int bgID, float scale) {
		mVolLumBg.setImageResource(bgID);
		mOperationInfo.setVisibility(View.GONE);
		mOperationVolLum.setVisibility(View.VISIBLE);
		ViewGroup.LayoutParams lp = mVolLumNum.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * scale);
		mVolLumNum.setLayoutParams(lp);
	}

	public void setFileName(String name) {
		mFileName.setText(name);
	}

	// public void setDownloadRate(String rate) {
	// mDownloadRate.setVisibility(View.VISIBLE);
	// mDownloadRate.setText(rate);
	// }

	// public void setBatteryLevel(String level) {
	// mBatteryLevel.setVisibility(View.VISIBLE);
	// mBatteryLevel.setText(level);
	// }

	public void show() {
		show(DEFAULT_TIME_OUT);
	}

	public boolean isShowing() {
		return mShowing;
	}

	public void show(int timeout) {

		if (!mShowing) {
			if (mPauseButton != null)
				mPauseButton.requestFocus();

			mMediaController.setVisibility(View.VISIBLE);
			mControlsLayout.startAnimation(mAnimSlideInTop);

			if (mSystemInfoLayout.isShown()) {
				mSystemInfoLayout.startAnimation(mAnimSlideInBottom);
			}

			mShowing = true;
			// if (mShownListener != null)
			// mShownListener.onShown();
		}
		updatePausePlay();
		// mHandler.sendEmptyMessage(MSG_TIME_TICK);
		mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);

		if (timeout != 0) {
			mHandler.removeMessages(MSG_FADE_OUT);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_FADE_OUT),
					timeout);
		}
	}

	public void hide() {

		if (mShowing) {
			try {
//				if (isPermission) {
//					mHandler.removeMessages(MSG_TIME_TICK);
//				}
				mHandler.removeMessages(MSG_SHOW_PROGRESS);
				mProgramlist.setVisibility(View.GONE);
				mControlsLayout.startAnimation(mAnimSlideOutTop);
				if (mSystemInfoLayout.isShown()) {
					mSystemInfoLayout.startAnimation(mAnimSlideOutBottom);
				}
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			}
			mShowing = false;
			// if (mHiddenListener != null)
			// mHiddenListener.onHidden();
		}
	}

	// public void setOnShownListener(OnShownListener l) {
	// mShownListener = l;
	// }
	//
	// public void setOnHiddenListener(OnHiddenListener l) {
	// mHiddenListener = l;
	// }


	private static final int[] s_allAspectRatio = {
			VideoView.AR_ASPECT_FIT_PARENT,
			VideoView.AR_ASPECT_FILL_PARENT,
			VideoView.AR_ASPECT_WRAP_CONTENT,
			// IRenderView.AR_MATCH_PARENT,
			VideoView.AR_16_9_FIT_PARENT, VideoView.AR_4_3_FIT_PARENT };
	private int mCurrentAspectRatioIndex = 0;
	private int mCurrentAspectRatio = s_allAspectRatio[0];

	public int toggleAspectRatio() {
		mCurrentAspectRatioIndex++;
		mCurrentAspectRatioIndex %= s_allAspectRatio.length;

		mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];
		if (mVideoView != null)
			mVideoView.setAspectRatio(mCurrentAspectRatio);
		return mCurrentAspectRatio;
	}

	private void lock(boolean toLock) {
		if (toLock) {
			mLock.setImageResource(R.drawable.mediacontroller_lock);
			mMenu.setVisibility(View.GONE);

			mProgress.setEnabled(false);
			if (mScreenLocked != toLock)
				setOperationInfo(
						mContext.getString(R.string.video_screen_locked), 1000);
		} else {
			mLock.setImageResource(R.drawable.mediacontroller_unlock);
			// If you wanna to show, set mMenu visible
			setMenuVisbilable();

			mProgress.setEnabled(true);
			if (mScreenLocked != toLock)
				setOperationInfo(
						mContext.getString(R.string.video_screen_unlocked),
						1000);
		}
		mScreenLocked = toLock;
		mGestures.setTouchListener(mTouchListener, !mScreenLocked);
	}

	public boolean isLocked() {
		return mScreenLocked;
	}

	private static final int MSG_FADE_OUT = 1;
	private static final int MSG_SHOW_PROGRESS = 2;
	private static final int MSG_HIDE_SYSTEM_UI = 3;
	private static final int MSG_TIME_TICK = 4;
	private static final int MSG_HIDE_OPERATION_INFO = 5;
	private static final int MSG_HIDE_OPERATION_VOLLUM = 6;
	//	private static final int MSG_SHOW_PROGRESS_BUY = 7;
	private static class MHandler extends Handler {
		private WeakReference<WhtVideoView> mc;

		public MHandler(WhtVideoView mc) {
			this.mc = new WeakReference<WhtVideoView>(mc);
		}

		@Override
		public void handleMessage(Message msg) {
			WhtVideoView c = mc.get();
			if (c == null)
				return;

			switch (msg.what) {
			case MSG_FADE_OUT:
				c.hide();
				break;
			case MSG_SHOW_PROGRESS:
				long pos = c.setProgress();
				// Log.i("TAG", "cuuuuu:" + pos);
				if (!c.mDragging && c.mShowing) {
					msg = obtainMessage(MSG_SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
					c.updatePausePlay();
				}
				break;
			case MSG_HIDE_SYSTEM_UI:
				if (!c.mShowing)
					// c.showSystemUi(false);
					break;
			case MSG_TIME_TICK:
//				if (!c.isPermission) {
//					if (c.isPlaying() && !c.mDragging) {
//						long position = c.getCurrentPosition();
//						if (position >= c.getFreeTime()) {
//							// WhtLog.i("TAG", "MSG_TIME_TICK-onPause----");
//							c.pause();
//							c.showbuy();
//						}
//					}
//
//					sendEmptyMessageDelayed(MSG_TIME_TICK, TIME_TICK_INTERVAL);
//				} else {
//					removeMessages(MSG_TIME_TICK);
//				}
				break;
			case MSG_HIDE_OPERATION_INFO:
				c.mOperationInfo.setVisibility(View.GONE);
				break;
			case MSG_HIDE_OPERATION_VOLLUM:
				c.mOperationVolLum.setVisibility(View.GONE);
				break;
			// case MSG_SHOW_PROGRESS_BUY:
			// if (c.isPlaying() && !PaySdk.hasProgPermission(c.getNewProg(),
			// false)){
			// msg = obtainMessage(MSG_SHOW_PROGRESS_BUY);
			// sendMessageDelayed(msg, TIME_TICK_INTERVAL);
			// long position = c.getCurrentPosition();
			//
			// if(position > c.getFreeTime()){
			// c.pause();
			// c.showBuy();
			// }
			// }else {
			// removeMessages(MSG_SHOW_PROGRESS_BUY);
			// }
			// break;
			}
		}
	};

	private long setProgress() {
		if (mVideoView == null || mDragging)
			return 0;

		long position = mVideoView.getCurrentPosition();
		long duration = mVideoView.getDuration();
		if (duration > 0) {
			long pos = 1000L * position / duration;
			mProgress.setProgress((int) pos);
		}
		int percent = mVideoView.getBufferPercentage();
//		WhtLog.i("TAG", "percent："+percent);
		mProgress.setSecondaryProgress(percent * 10);

		mDuration = duration;

		if(mEndTime!=null){
			mEndTime.setText("/"+StringUtils.generateTime(mDuration));
		}
		if (mCurrentTime != null) {
			mCurrentTime.setText(StringUtils.generateTime(position));

		}
		return position;
	}

//	public void showbuy() {
//		if (onBuyListener != null) {
//			onBuyListener.showBuyWindow();
//		}
//	}

	public long getCurrentPosition() {
		if(mVideoView==null)return 0;
		return mVideoView.getCurrentPosition();
	}

//	public NewProg getNewProg() {
//		return mNewProg;
//	}
//
//	public long getFreeTime() {
//		if (mNewProg == null)
//			return 0;
//		return mNewProg.getFreeTime();
//	}

	private void setVolume(int v) {
		if (v > mMaxVolume)
			v = mMaxVolume;
		else if (v < 0)
			v = 0;
		mAM.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
		setVolumeScale((float) v / mMaxVolume);
	}

	private void setBrightness(float f) {
		WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
				.getAttributes();
		lp.screenBrightness = f;
		if (lp.screenBrightness > 1.0f)
			lp.screenBrightness = 1.0f;
		else if (lp.screenBrightness < 0.01f)
			lp.screenBrightness = 0.01f;
		((Activity) mContext).getWindow().setAttributes(lp);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestures.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(DEFAULT_TIME_OUT);
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();

		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_MUTE:
				return super.dispatchKeyEvent(event);
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
				int step = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? 1 : -1;
				setVolume(mVolume + step);
				mHandler.removeMessages(MSG_HIDE_OPERATION_VOLLUM);
				mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_VOLLUM, 500);
				return true;
		}

		if (isLocked()) {
			show();
			return true;
		}

		if (event.getRepeatCount() == 0
				&& (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
				|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
			doPauseResume();
			show(DEFAULT_TIME_OUT);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
			if (mVideoView.isPlaying()) {
				mVideoView.pause();
				updatePausePlay();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			int screenOrientation = getScreenOrientation();
			if(mContext instanceof VideoViewActivity){
				return super.dispatchKeyEvent(event);
			}else if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {

				setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				return super.dispatchKeyEvent(event);
			}

			return true;
		} else {
			show(DEFAULT_TIME_OUT);
		}
		return super.dispatchKeyEvent(event);
	}

	public boolean onBackPressed() {
		if (isLocked()) {
			show();
			return true;
		}else if(mContext instanceof VideoViewActivity){
			return false;
		} else if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			return true;
		}

		return false;
	}

	private void updatePausePlay() {
		if (mPauseButton == null)
			return;
		if (mVideoView.isPlaying())
			mPauseButton.setImageResource(R.drawable.player_pause);
		else
			mPauseButton.setImageResource(R.drawable.player_play);
	}

	public void doPauseResume() {
		if (mVideoView == null)
			return;

//		if (!isPermission) {
//			long position = getCurrentPosition();
//			if (position >= getFreeTime()) {
//				showbuy();
//				return;
//			}
//		}

		if (mVideoView.isPlaying())
			mVideoView.pause();
		else
			mVideoView.start();
		updatePausePlay();

	}

	public boolean isPlaying() {
		return mVideoView != null && mVideoView.isPlaying();
	}

	private OnClickListener mPauseListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isLocked()) {
				show(DEFAULT_TIME_OUT);
				return;
			}
			if (mVideoView.isPlaying())
				show(DEFAULT_MID_TIME_SHOW);
			else
				show();
			doPauseResume();
		}
	};

	private View.OnClickListener mVideoBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isLocked()) {
				show(DEFAULT_TIME_OUT);
				return;
			}
			int screenOrientation = getScreenOrientation();
			if(mContext instanceof VideoViewActivity){
				if (!mContext.isFinishing()) {
					mContext.finish();
				}
			}else if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				if (!mContext.isFinishing()) {
					mContext.finish();
				}

			} else {
				show();
				setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			}
		}
	};
	private OnClickListener mLockClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// hide();
			lock(!mScreenLocked);
			show();
		}
	};

	private OnClickListener mScreenToggleListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// show(DEFAULT_TIME_OUT);
			if (isLocked()) {
				return;
			}
			int screenOrientation = getScreenOrientation();
			if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

				setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			} else {
				int aspectRatio = toggleAspectRatio();
				String aspectRatioText = MeasureHelper.getAspectRatioText(mContext, aspectRatio);
				setOperationInfo(aspectRatioText, 500);

			}

		}

	};

	private void setActivityOrientation(int orientation) {
		if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {

			((Activity) mContext).getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			((Activity) mContext)
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		} else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			((Activity) mContext).getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN); // 取消全屏
			((Activity) mContext)
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		private boolean wasStopped = false;

		@Override
		public void onStartTrackingTouch(SeekBar bar) {
			mDragging = true;
			show(3600000);
			mHandler.removeMessages(MSG_SHOW_PROGRESS);
			wasStopped = !mVideoView.isPlaying();
			if (mInstantSeeking) {
				mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
				if (wasStopped) {
					mVideoView.start();

				}
			}
		}

		@Override
		public void onProgressChanged(SeekBar bar, int progress,
									  boolean fromuser) {
			if (!fromuser)
				return;
			//TODO onProgressChanged
			long newposition = (mDuration * progress) / 1000;

			String time = StringUtils.generateTime(newposition);
			if (mInstantSeeking)
				mVideoView.seekTo(newposition);
			setOperationInfo(time, 1500);
			if (mCurrentTime != null) {
				mCurrentTime.setText(time);
			}
		}

		@Override
		public void onStopTrackingTouch(SeekBar bar) {
			if (!mInstantSeeking) {
				mVideoView.seekTo((mDuration * bar.getProgress()) / 1000);
			} else if (wasStopped) {
				mVideoView.pause();
			}
			mOperationInfo.setVisibility(View.GONE);
			show(DEFAULT_TIME_OUT);
			mHandler.removeMessages(MSG_SHOW_PROGRESS);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_PROGRESS, 1000);
		}
	};

	@Override
	public void setEnabled(boolean enabled) {
		if (mPauseButton != null)
			mPauseButton.setEnabled(enabled);
		if (mProgress != null)
			mProgress.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		setEnabled(true);
		show(DEFAULT_MID_TIME_SHOW);

//		if (!isPermission) {// 如果没有观看权限就启动
//			mHandler.removeMessages(MSG_TIME_TICK);
//			mHandler.sendEmptyMessageDelayed(MSG_TIME_TICK, TIME_TICK_INTERVAL);
//		} else {
//			mHandler.removeMessages(MSG_TIME_TICK);
//		}
		// WhtLog.i("TAG", "mSeekTo:"+mSeekTo+"/"+(int) (mSeekTo *
		// mVideoView.getDuration()));
		if (mSeekTo > 0 && mSeekTo < 1) {
			seekTo(mSeekTo);
		}
		mSeekTo = -1;
	}

	public void seekTo(float percent) {
		if (mVideoView != null)
			mVideoView.seekTo((int) (percent * mVideoView.getDuration()));

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mEnd = true;
		loadRateView.setText("");
		loadRateView.setVisibility(View.GONE);
		pb.setVisibility(View.GONE);

		if (mIndex < videoBeans.size() - 1) {
			mIndex++;
			play(mIndex);
			if (indexChangeListener != null) {
				indexChangeListener.onIndexChange(mIndex);
			}
			Toast.makeText(mContext, "自动播放下一集！", Toast.LENGTH_SHORT).show();

		} else {
			// ToastUtils.showShort("播放完成！");
			stop();
		}


		if(onCompletionListener!=null){
			onCompletionListener.onCompletion(mp);
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				if (mVideoView.isPlaying()) {
					mVideoView.pause();
					pb.setVisibility(View.VISIBLE);
					// downloadRateView.setText("");
					loadRateView.setText("");
					// downloadRateView.setVisibility(View.VISIBLE);
					loadRateView.setVisibility(View.VISIBLE);

				}
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				mVideoView.start();
				pb.setVisibility(View.GONE);
				// downloadRateView.setVisibility(View.GONE);
				loadRateView.setVisibility(View.GONE);
				break;
			case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
				// downloadRateView.setText("" + extra + "kb/s" + "  ");
				// setDownloadRate("" + extra + "kb/s" + "  ");

				break;
		}
		return true;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Toast.makeText(mContext, "视频播放错误！", Toast.LENGTH_SHORT).show();
		loadRateView.setText("");
		pb.setVisibility(View.GONE);
		loadRateView.setVisibility(View.GONE);
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		if (loadRateView.isShown())
			loadRateView.setText(percent + "%");

	}

	@Override
	public void onTimedText(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTimedTextUpdate(byte[] pixels, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

		requestLayout();

	}

	/**
	 * 计费点弹计费
	 *
	 * @param l
	 */
	// public void setOnBuyListener(OnBuyListener l) {
	// onBuyListener = l;
	// }

	public void setOnIndexChangeListener(OnIndexChangeListener l) {
		indexChangeListener = l;
	}

	@SuppressWarnings("deprecation")
	private int getScreenRotation() {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /*
																 * Android 2.2
																 * has
																 * getRotation
																 */) {
			try {
				Method m = display.getClass().getDeclaredMethod("getRotation");
				return (Integer) m.invoke(display);
			} catch (Exception e) {
				return Surface.ROTATION_0;
			}
		} else {
			return display.getOrientation();
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private int getScreenOrientation() {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int rot = getScreenRotation();
		/*
		 * Since getRotation() returns the screen's "natural" orientation, which
		 * is not guaranteed to be SCREEN_ORIENTATION_PORTRAIT, we have to
		 * invert the SCREEN_ORIENTATION value if it is "naturally" landscape.
		 */
		@SuppressWarnings("deprecation")
		boolean defaultWide = display.getWidth() > display.getHeight();
		if (rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
			defaultWide = !defaultWide;
		if (defaultWide) {
			switch (rot) {
				case Surface.ROTATION_0:
					return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				case Surface.ROTATION_90:
					return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				case Surface.ROTATION_180:
					// SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
					// Level 9+
					return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
							: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				case Surface.ROTATION_270:
					// SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
					// Level 9+
					return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
							: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				default:
					return 0;
			}
		} else {
			switch (rot) {
				case Surface.ROTATION_0:
					return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				case Surface.ROTATION_90:
					return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				case Surface.ROTATION_180:
					// SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
					// Level 9+
					return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
							: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				case Surface.ROTATION_270:
					// SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
					// Level 9+
					return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
							: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				default:
					return 0;
			}
		}
	}

	/*
	 * public interface MediaPlayerControl { void start();
	 *
	 * void pause();
	 *
	 * void stop();
	 *
	 * void seekTo(long pos);
	 *
	 * boolean isPlaying();
	 *
	 * long getDuration();
	 *
	 * long getCurrentPosition();
	 *
	 * int getBufferPercentage();
	 *
	 * void previous();
	 *
	 * void next();
	 *
	 * long goForward();
	 *
	 * long goBack();
	 *
	 * void toggleVideoMode(int mode);
	 *
	 * void showMenu();
	 *
	 * void removeLoadingView();
	 *
	 * void snapshot();
	 *
	 * }
	 */

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			play(position);
			ProgramAdapter adapter = (ProgramAdapter) parent.getAdapter();
			adapter.setSelection(position);
			if (indexChangeListener != null) {
				indexChangeListener.onIndexChange(position);
			}
		}
	};

	public void open(Context context, boolean live,
			ArrayList<RProgramDetail> list) {
		if (mVideoView == null) {
			mVideoView = new VideoView(context);
			mVideoView.setOnCompletionListener(this);
			mVideoView.setOnPreparedListener(this);
			mVideoView.setOnInfoListener(this);
			mVideoView.setOnErrorListener(this);
			mVideoView.setOnSeekCompleteListener(this);
			mVideoView.setOnBufferingUpdateListener(this);
			mVideoView.setShownHideListener(this);
		}
		initVedio();
		videoBeans.clear();
		videoBeans.addAll(list);

		isLive = live;
		setIsLive(isLive);
		// 第一步 初始化自己集合
		setProgramList();
		// 第二步 显示menu按钮
		setMenuVisbilable();
		if (((View) mVideoView).getParent() == null) {
			addVideoView(mVideoView);
		}

	}

	private void setProgramList() {
		adapter = new ProgramAdapter(mContext, videoBeans);
		mProgramlist.setAdapter(adapter);
		adapter.setSelection(mIndex);
		mProgramlist.setOnItemClickListener(onItemClickListener);

	}
	/**
	 * 设置是否需要显示子集列表
	 */
	private void setMenuVisbilable() {
		if (videoBeans.size()>1) {
			mMenu.setVisibility(View.VISIBLE);

		} else {
			mMenu.setVisibility(View.GONE);

		}
	}

	/**
	 * 0 ：表示播放本身或者第一集， 大于0表示播放子集0
	 *
	 * @param index
	 */
	public void play(int index) {
		if (videoBeans.isEmpty())
			return;

		String url = null;

		if (index < 0)
			index = 0;
		this.mIndex = index;
		url = videoBeans.get(index).video_path;
		if (adapter != null) {
			adapter.setSelection(index);
		}
		if (!TextUtils.isEmpty(url)) {
			Uri uri = Uri.parse(url);
			if (mUri != null && url.equals(mUri.toString())) {
				if (mVideoView != null && !mVideoView.isPlaying()) {
					mVideoView.start();
				}
				return;
			}
			mUri = uri;

		}
		mSeekTo = getStartPosition();
		String title = videoBeans.get(index).name;
		title = (title == null) ? "" : title;
		setFileName(title);
		mVideoView.setVideoURI(mUri);

	}

	public void onResume() {

		resume();

	}

	/**
	 * 继续
	 */
	public void resume() {
		if (mVideoView != null) {
			mVideoView.start();

		}
		updatePausePlay();

	}

	public void onPause() {

		pause();

	}

	/**
	 * 暂停
	 */
	public void pause() {

		if (mVideoView == null)
			return;
		savePosition();

		mVideoView.pause();

		updatePausePlay();

	}

	public static final String SESSION_LAST_POSITION_SUFIX = ".lst";

	private void savePosition() {
		// WhtLog.i("TAG", "savePosition");
		if (mVideoView != null && mUri != null) {
//			PreferenceUtils.put(
//					mUri.toString(),
//					StringUtils.generateTime((int) (0.5 + mVideoView
//							.getCurrentPosition()))
//							+ " / "
//							+ StringUtils.generateTime(mVideoView.getDuration()));
			PreferenceUtils.reset(mContext);
			if (mEnd){
				PreferenceUtils.put(mUri + SESSION_LAST_POSITION_SUFIX, 1.0f);
				mSeekTo = 1.0f;

			}
			else{
				float pos = (float) (getCurrentPosition() / (double)mVideoView.getDuration());
				PreferenceUtils.put(mUri + SESSION_LAST_POSITION_SUFIX,pos);
				mSeekTo = pos;
			}
//			WhtLog.i("TAG", "savePosition-存- : "+(float) (getCurrentPosition() / (double)mVideoView.getDuration()));

		}
	}

	private float getStartPosition() {
		float pos = PreferenceUtils.getFloat(mUri+ SESSION_LAST_POSITION_SUFIX, 7.7f);
//		WhtLog.i("TAG", "getStartPosition-取- : "+pos);
		return pos;

	}

	public void addOnCompletionListener(OnCompletionListener onCompletionListener) {
		// TODO Auto-generated method stub
		this.onCompletionListener = onCompletionListener;
	}

//	@Override
//	public void onRestoreInstanceState(Parcelable state) {
//		SavedState savedState = (SavedState) state;
//		super.onRestoreInstanceState(savedState.getSuperState());
//		mIndex = savedState.mIndex;
//		mSeekTo = savedState.mSeekTo;
//		mUrl = savedState.mUrl;
//		if(mUrl!=null){
//			mUri  = Uri.parse(mUrl);
//		}
//
//		requestLayout();
//	}

//	@Override
//	public Parcelable onSaveInstanceState() {
//		Parcelable superState = super.onSaveInstanceState();
//		SavedState savedState = new SavedState(superState);
//		savedState.mIndex = mIndex;
//		savedState.mSeekTo = mSeekTo;
//		savedState.mUrl = mUrl;
//		return savedState;
//	}
//	static class SavedState extends BaseSavedState {
//		int mIndex;
//		float mSeekTo;
//		String mUrl;
//		public SavedState(Parcelable superState) {
//			super(superState);
//		}
//
//		private SavedState(Parcel in) {
//			super(in);
//			mIndex = in.readInt();
//			mSeekTo = in.readFloat();
//			mUrl = in.readString();
//
//		}
//
//		@Override
//		public void writeToParcel(Parcel dest, int flags) {
//			super.writeToParcel(dest, flags);
//			dest.writeInt(mIndex);
//			dest.writeFloat(mSeekTo);
//			dest.writeString(mUrl);
//		}
//
//		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
//			@Override
//			public SavedState createFromParcel(Parcel in) {
//				return new SavedState(in);
//			}
//
//			@Override
//			public SavedState[] newArray(int size) {
//				return new SavedState[size];
//			}
//		};
//	}

}
