/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 YIXIA.COM
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

import io.vov.vitamio.MediaFormat;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCachingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.MediaPlayer.ShownHideListener;
import io.vov.vitamio.MediaPlayer.TrackInfo;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.utils.ScreenResolution;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.community.yuequ.player.MeasureHelper;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * <p/>
 * VideoView also provide many wrapper methods for
 * {@link io.vov.vitamio.MediaPlayer}, such as {@link #getVideoWidth()},
 * {@link #setTimedTextShown(boolean)}
 */
public class VideoView extends SurfaceView {
	public static final int AR_ASPECT_FIT_PARENT = 0; // without clip
	public static final int AR_ASPECT_FILL_PARENT = 1; // may clip
	public static final int AR_ASPECT_WRAP_CONTENT = 2;
	public static final int AR_MATCH_PARENT = 3;
	public static final int AR_16_9_FIT_PARENT = 4;
	public static final int AR_4_3_FIT_PARENT = 5;
	private MeasureHelper mMeasureHelper;

	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;
	private static final int STATE_SUSPEND = 6;
	private static final int STATE_RESUME = 7;
	private static final int STATE_SUSPEND_UNSUPPORTED = 8;
	OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			Log.d("onVideoSizeChanged: (%dx%d)", width, height);
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
//			mVideoAspectRatio = mp.getVideoAspectRatio();
			if (mVideoWidth != 0 && mVideoHeight != 0){
				setVideoSize(mVideoWidth, mVideoHeight);
//				setVideoSampleAspectRatio(mVideoWidth, mVideoHeight);
				if(mSizeChangedCallback!=null){
					mSizeChangedCallback.onVideoSizeChanged( mp,  width, height);
				}
//				requestLayout();
			}
		}
	};
	OnPreparedListener mPreparedListener = new OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			Log.d("onPrepared");
			mCurrentState = STATE_PREPARED;
			// mTargetState = STATE_PLAYING;

			// Get the capabilities of the player for this stream
			// TODO mCanPause

			if (mOnPreparedListener != null)
				mOnPreparedListener.onPrepared(mMediaPlayer);
			// if (mMediaController != null)
			// mMediaController.setEnabled(true);
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
//			mVideoAspectRatio = mp.getVideoAspectRatio();

			long seekToPosition = mSeekWhenPrepared;
			if (seekToPosition != 0)
				seekTo(seekToPosition);

			if (mVideoWidth != 0 && mVideoHeight != 0) {
				setVideoSize(mVideoWidth, mVideoHeight);
//				setVideoSampleAspectRatio(mVideoWidth, mVideoHeight);
				if (mSurfaceWidth == mVideoWidth
						&& mSurfaceHeight == mVideoHeight) {
					if (mTargetState == STATE_PLAYING) {
						start();
						if (shownHideListener != null)
							shownHideListener.show();
					} else if (!isPlaying()
							&& (seekToPosition != 0 || getCurrentPosition() > 0)) {
						if (shownHideListener != null)
							shownHideListener.show(0);
					}
				}
			} else if (mTargetState == STATE_PLAYING) {
				start();
			}
		}
	};
	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
								   int h) {
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
			if (mMediaPlayer != null && isValidState && hasValidSize) {
				if (mSeekWhenPrepared != 0)
					seekTo(mSeekWhenPrepared);
				start();
				if (shownHideListener != null) {
					if (shownHideListener.isShowing())
						shownHideListener.hide();
					shownHideListener.show();
				}
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND
					&& mTargetState == STATE_RESUME) {
				mMediaPlayer.setDisplay(mSurfaceHolder);
				resume();
			} else {
				openVideo();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mSurfaceHolder = null;
			if (shownHideListener != null)
				shownHideListener.hide();
			release(true);
		}
	};
	private Uri mUri;
	private long mDuration;
	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;
	//	private float mAspectRatio = 0;
	// private int mVideoLayout = VIDEO_LAYOUT_ORIGIN;
	private SurfaceHolder mSurfaceHolder = null;
	private MediaPlayer mMediaPlayer = null;
	private int mVideoWidth;
	private int mVideoHeight;
	//	private float mVideoAspectRatio;
	private int mVideoChroma = MediaPlayer.VIDEOCHROMA_RGBA;
	private boolean mHardwareDecoder = false;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	// private WhtVideoView mMediaController;
	private View mMediaBufferingIndicator;
	private OnCompletionListener mOnCompletionListener;
	private OnPreparedListener mOnPreparedListener;
	private OnErrorListener mOnErrorListener;
	private OnSeekCompleteListener mOnSeekCompleteListener;
	private OnTimedTextListener mOnTimedTextListener;
	private OnInfoListener mOnInfoListener;
	private OnBufferingUpdateListener mOnBufferingUpdateListener;
	private OnCachingUpdateListener mOnCachingUpdateListener;
	private ShownHideListener shownHideListener;



	private OnVideoSizeChangedListener mSizeChangedCallback;
	private int mCurrentBufferPercentage;
	private long mSeekWhenPrepared; // recording the seek position while
	// preparing
	private Context mContext;
	private Map<String, String> mHeaders;
	private int mBufSize = 1024*1024;
	private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			Log.d("onCompletion");
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mTargetState = STATE_PLAYBACK_COMPLETED;
			if (shownHideListener != null)
				shownHideListener.hide();
			if (mOnCompletionListener != null)
				mOnCompletionListener.onCompletion(mMediaPlayer);
		}
	};
	private OnErrorListener mErrorListener = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			Log.d("Error: %d, %d", framework_err, impl_err);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			if (shownHideListener != null)
				shownHideListener.hide();

			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err,
						impl_err))
					return true;
			}


			return true;
		}
	};
	private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mCurrentBufferPercentage = percent;
			if (mOnBufferingUpdateListener != null)
				mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
		}
	};
	private OnInfoListener mInfoListener = new OnInfoListener() {
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			Log.d("onInfo: (%d, %d)", what, extra);
			if(MediaPlayer.MEDIA_INFO_UNKNOW_TYPE == what){
				Log.e(" VITAMIO--TYPE_CHECK  stype  not include  onInfo mediaplayer unknow type ");
			}

			if(MediaPlayer.MEDIA_INFO_FILE_OPEN_OK == what){
				long buffersize=mMediaPlayer.audioTrackInit();
				mMediaPlayer.audioInitedOk(buffersize);
			}
			if (mOnInfoListener != null) {
				mOnInfoListener.onInfo(mp, what, extra);
			} else if (mMediaPlayer != null) {
				if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
					mMediaPlayer.pause();
					if (mMediaBufferingIndicator != null)
						mMediaBufferingIndicator.setVisibility(View.VISIBLE);
				} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
					mMediaPlayer.start();
					if (mMediaBufferingIndicator != null)
						mMediaBufferingIndicator.setVisibility(View.GONE);
				}
			}
			return true;
		}
	};
	private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
		@Override
		public void onSeekComplete(MediaPlayer mp) {
			Log.d("onSeekComplete");
			if (mOnSeekCompleteListener != null)
				mOnSeekCompleteListener.onSeekComplete(mp);
		}
	};
	private OnTimedTextListener mTimedTextListener = new OnTimedTextListener() {
		@Override
		public void onTimedTextUpdate(byte[] pixels, int width, int height) {
			Log.i("onSubtitleUpdate: bitmap subtitle, %dx%d", width, height);
			if (mOnTimedTextListener != null)
				mOnTimedTextListener.onTimedTextUpdate(pixels, width, height);
		}

		@Override
		public void onTimedText(String text) {
			Log.i("onSubtitleUpdate: %s", text);
			if (mOnTimedTextListener != null)
				mOnTimedTextListener.onTimedText(text);
		}
	};

	private boolean mChangeParentView;

	public VideoView(Context context) {
		super(context);
		initVideoView(context);
	}

	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initVideoView(context);
	}

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoView(context);
	}


	@SuppressWarnings("deprecation")
	private void initVideoView(Context ctx) {
		mContext = ctx;
		mVideoWidth = 0;
		mVideoHeight = 0;
		mChangeParentView = false;
		mMeasureHelper = new MeasureHelper(this);
		getHolder().setFormat(PixelFormat.RGBA_8888); // PixelFormat.RGB_565
		getHolder().addCallback(mSHCallback);
		// this value only use Hardware decoder before Android 2.3
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				&& mHardwareDecoder) {
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
		if (ctx instanceof Activity)
			((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	public boolean isValid() {
		return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
	}

	public void setVideoPath(String path) {
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri uri) {
		setVideoURI(uri, null);
	}

	public void setVideoURI(Uri uri, Map<String, String> headers) {
		mUri = uri;
		mHeaders = headers;
		if (mUri != null) {
			mSeekWhenPrepared = 0;
			openVideo();
			requestLayout();
			invalidate();
		}
	}
	public void stopPlayback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;
		}
	}
//	public void stopPlayback() {
//		if (mMediaPlayer != null) {
//
//			long pos = mMediaPlayer.getCurrentPosition();
//			if (pos > 0)
//				mSeekWhenPrepared = pos;
//			mMediaPlayer.release();
//			mMediaPlayer = null;
//			mDuration = -1;
//			mCurrentState = STATE_IDLE;
//			mTargetState = STATE_IDLE;
//
//		}
//	}

	public void reset() {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
		}

		if (shownHideListener != null)
			shownHideListener.hide();

	}


	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null
				|| !Vitamio.isInitialized(mContext))
			return;

		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);

		release(false);
		try {
			mDuration = -1;
			mCurrentBufferPercentage = 0;
			mMediaPlayer = new MediaPlayer(mContext, mHardwareDecoder);
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
			mMediaPlayer.setOnTimedTextListener(mTimedTextListener);
			mMediaPlayer.setOnCachingUpdateListener(mOnCachingUpdateListener);
			HashMap<String, String> options = new HashMap<String, String>();
			options.put("rtsp_transport", "tcp"); // udp
			//	options.put("user-agent", "userAgent");
			//	options.put("cookies", "cookies");
			options.put("analyzeduration", "1000000");
			mMediaPlayer.setDataSource(mContext, mUri, options);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setBufferSize(mBufSize);
//			mMediaPlayer.setUseCache(true);
//			mMediaPlayer.setCacheDirectory(FileTools.getVideoFileDir());
			mMediaPlayer
					.setVideoChroma(mVideoChroma == MediaPlayer.VIDEOCHROMA_RGB565 ? MediaPlayer.VIDEOCHROMA_RGB565
							: MediaPlayer.VIDEOCHROMA_RGBA);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			mCurrentState = STATE_PREPARING;
			attachMediaController();
		} catch (IOException ex) {
			Log.e("Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer,
					MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		} catch (IllegalArgumentException ex) {
			Log.e("Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer,
					MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
	}

	// public void setMediaController(WhtVideoView controller) {
	// if (mMediaController != null)
	// mMediaController.hide();
	// mMediaController = controller;
	// attachMediaController();
	// }

	public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
		if (mMediaBufferingIndicator != null)
			mMediaBufferingIndicator.setVisibility(View.GONE);
		mMediaBufferingIndicator = mediaBufferingIndicator;
	}

	private void attachMediaController() {
		// if (mMediaPlayer != null && mMediaController != null) {
		// mMediaController.setMediaPlayer(this);
		// View anchorView = this.getParent() instanceof View ? (View)
		// this.getParent() : this;
		// mMediaController.setAnchorView(anchorView);
		// mMediaController.setEnabled(isInPlaybackState());
		//
		// if (mUri != null) {
		// List<String> paths = mUri.getPathSegments();
		// String name = paths == null || paths.isEmpty() ? "null" :
		// paths.get(paths.size() - 1);
		// mMediaController.setFileName(name);
		// }
		// }
	}
	public void setSizeChangedListener(OnVideoSizeChangedListener sizeChangedListener) {
		mSizeChangedCallback = sizeChangedListener;
	}
	public void setOnPreparedListener(OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	public void setOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	public void setOnErrorListener(OnErrorListener l) {
		mOnErrorListener = l;
	}

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
		mOnBufferingUpdateListener = l;
	}

	public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
		mOnSeekCompleteListener = l;
	}

	public void setOnTimedTextListener(OnTimedTextListener l) {
		mOnTimedTextListener = l;
	}

	public void setOnInfoListener(OnInfoListener l) {
		mOnInfoListener = l;
	}

	private void release(boolean cleartargetstate) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			if (cleartargetstate)
				mTargetState = STATE_IDLE;
		}
	}

	public void releaseSurface() {
		if (mMediaPlayer!=null)
			mMediaPlayer.releaseDisplay();
	}


	public void start() {
		if (isInPlaybackState()) {
			mMediaPlayer.start();
			mCurrentState = STATE_PLAYING;
		}
		mTargetState = STATE_PLAYING;
	}

	public void pause() {
		if (isInPlaybackState()) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mTargetState = STATE_PAUSED;
	}

	public void suspend() {
		if (isInPlaybackState()) {
			release(false);
			mCurrentState = STATE_SUSPEND_UNSUPPORTED;
			Log.d("Unable to suspend video. Release MediaPlayer.");
		}
	}

	public void resume() {
		if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
			mTargetState = STATE_RESUME;
		} else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
			openVideo();
		}
	}

	public long getDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0)
				return mDuration;
			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	public long getCurrentPosition() {
		if (isInPlaybackState())
			return mMediaPlayer.getCurrentPosition();
		return 0;
	}

	public long getBufferProgress() {
		if (isInPlaybackState())
			return mMediaPlayer.getBufferProgress();
		return 0;
	}

	public void seekTo(long msec) {
		if (isInPlaybackState()) {
			mMediaPlayer.seekTo(msec);
			mSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	public boolean isPlaying() {
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}

	public int getBufferPercentage() {
		if (mMediaPlayer != null)
			return mCurrentBufferPercentage;
		return 0;
	}

	public void setVolume(float leftVolume, float rightVolume) {
		if (mMediaPlayer != null)
			mMediaPlayer.setVolume(leftVolume, rightVolume);
	}

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

//	public float getVideoAspectRatio() {
//		return mVideoAspectRatio;
//	}

	/**
	 * Must set before {@link #setVideoURI}
	 *
	 * @param chroma
	 */
	public void setVideoChroma(int chroma) {
		getHolder().setFormat(
				chroma == MediaPlayer.VIDEOCHROMA_RGB565 ? PixelFormat.RGB_565
						: PixelFormat.RGBA_8888); // PixelFormat.RGB_565
		mVideoChroma = chroma;
	}

	public void setHardwareDecoder(boolean hardware) {
		mHardwareDecoder = hardware;
	}

	public void setVideoQuality(int quality) {
		if (mMediaPlayer != null)
			mMediaPlayer.setVideoQuality(quality);
	}

	public void setBufferSize(int bufSize) {
		mBufSize = bufSize;
	}

	public boolean isBuffering() {
		if (mMediaPlayer != null)
			return mMediaPlayer.isBuffering();
		return false;
	}

	public String getMetaEncoding() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getMetaEncoding();
		return null;
	}

	public void setMetaEncoding(String encoding) {
		if (mMediaPlayer != null)
			mMediaPlayer.setMetaEncoding(encoding);
	}

	public SparseArray<MediaFormat> getAudioTrackMap(String encoding) {
		if (mMediaPlayer != null)
			return mMediaPlayer.findTrackFromTrackInfo(
					TrackInfo.MEDIA_TRACK_TYPE_AUDIO,
					mMediaPlayer.getTrackInfo(encoding));
		return null;
	}

	public int getAudioTrack() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getAudioTrack();
		return -1;
	}

	public void setAudioTrack(int audioIndex) {
		if (mMediaPlayer != null)
			mMediaPlayer.selectTrack(audioIndex);
	}

	public void setTimedTextShown(boolean shown) {
		if (mMediaPlayer != null)
			mMediaPlayer.setTimedTextShown(shown);
	}

	public void setTimedTextEncoding(String encoding) {
		if (mMediaPlayer != null)
			mMediaPlayer.setTimedTextEncoding(encoding);
	}

	public int getTimedTextLocation() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getTimedTextLocation();
		return -1;
	}

	public void addTimedTextSource(String subPath) {
		if (mMediaPlayer != null)
			mMediaPlayer.addTimedTextSource(subPath);
	}

	public String getTimedTextPath() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getTimedTextPath();
		return null;
	}

	public void setSubTrack(int trackId) {
		if (mMediaPlayer != null)
			mMediaPlayer.selectTrack(trackId);
	}

	public int getTimedTextTrack() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getTimedTextTrack();
		return -1;
	}

	public SparseArray<MediaFormat> getSubTrackMap(String encoding) {
		if (mMediaPlayer != null)
			return mMediaPlayer.findTrackFromTrackInfo(
					TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT,
					mMediaPlayer.getTrackInfo(encoding));
		return null;
	}

	protected boolean isInPlaybackState() {
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR
				&& mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	/**
	 * 设置播放速度
	 *
	 * @param f
	 */
	public void setPlaybackSpeed(float f) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setPlaybackSpeed(f);
		}

	}

	public void setShownHideListener(ShownHideListener l) {
		shownHideListener = l;

	}

	public void beginChangeParentView() {
		mChangeParentView = true;

	}

	public void endChangeParentView() {
		mChangeParentView = false;

	}

	@Override
	protected void onAttachedToWindow() {
		if (mChangeParentView) {
			return;
		}
		super.onAttachedToWindow();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		if (mChangeParentView) {
			return;
		}
		super.onWindowVisibilityChanged(visibility);
	}
	@Override
	protected void onDetachedFromWindow() {
		if (mChangeParentView) {
			return;
		}
		super.onDetachedFromWindow();
	}

//	public View getView() {
//		return this;
//	}

//	public boolean shouldWaitForResize() {
//		return true;
//	}

	public void setOnCachingUpdateListener(OnCachingUpdateListener l) {
		this.mOnCachingUpdateListener = l;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mMeasureHelper.getMeasuredWidth(),
				mMeasureHelper.getMeasuredHeight());
	}

	public void setAspectRatio(int aspectRatio) {
		mMeasureHelper.setAspectRatio(aspectRatio);
		requestLayout();

	}

	public void setVideoSize(int videoWidth, int videoHeight) {
		if (videoWidth > 0 && videoHeight > 0) {
			mMeasureHelper.setVideoSize(videoWidth, videoHeight);
			getHolder().setFixedSize(videoWidth, videoHeight);
			requestLayout();
		}
	}

	public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
		if (videoSarNum > 0 && videoSarDen > 0) {
			mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
			requestLayout();
		}
	}

	public void requestVideoLayout() {
		requestLayout();

	}

}