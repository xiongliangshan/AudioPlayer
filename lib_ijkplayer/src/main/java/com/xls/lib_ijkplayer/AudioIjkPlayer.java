package com.xls.lib_ijkplayer;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class AudioIjkPlayer extends BaseAudioPlayer implements IMediaPlayer.OnPreparedListener,IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnInfoListener,IMediaPlayer.OnCompletionListener,IMediaPlayer.OnSeekCompleteListener,IMediaPlayer.OnErrorListener {

    private static final String TAG = "AudioIjkPlayer";

    private IjkMediaPlayer mIjkMediaPlayer;
    private Timer mTimer;
    private IAudioStateListener mListener;
    private Handler mAudioHandler;
    private long mDuration = 0;
    private int lastBufferValue = 0;
    private boolean isAutoPlay = true;
    private int currentStatus = AUDIO_STATUS_STOP;
    private PlayStatusCallBack playStatusCallBack;

    /**
     * 私有无参构造方法
     */
    private AudioIjkPlayer(){
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        if(mIjkMediaPlayer==null){
            mIjkMediaPlayer = new IjkMediaPlayer();
            mIjkMediaPlayer.setVolume(1.0f, 1.0f);
            mIjkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mIjkMediaPlayer.setLooping(false);
            setAutoPlay(false);
            setScreenOnWhilePlaying(true);
            IjkMediaPlayer.native_setLogLevel(BuildConfig.DEBUG ? IjkMediaPlayer.IJK_LOG_DEBUG : IjkMediaPlayer.IJK_LOG_SILENT);
        }
        if(mAudioHandler==null){
            mAudioHandler = new AudioHandler(this);
        }
        initListener();
    }


    /**
     * 初始化监听器
     */
    private void initListener() {
        mIjkMediaPlayer.setOnInfoListener(this);
        mIjkMediaPlayer.setOnCompletionListener(this);
        mIjkMediaPlayer.setOnErrorListener(this);
        mIjkMediaPlayer.setOnPreparedListener(this);
        mIjkMediaPlayer.setOnBufferingUpdateListener(this);
        mIjkMediaPlayer.setOnSeekCompleteListener(this);
    }


    /**
     * 创建新的实例
     * @return
     */
    public static AudioIjkPlayer newInstance() {
        return new AudioIjkPlayer();
    }


    /**
     * 设置监听器
     * @param mListener
     */
    public void setAudioListener(IAudioStateListener mListener) {
        this.mListener = mListener;
    }


    /**
     * 获取监听器
     * @return
     */
    public IAudioStateListener getListener() {
        return mListener;
    }

    public void setPlayStatusCallBack(PlayStatusCallBack playStatusCallBack) {
        this.playStatusCallBack = playStatusCallBack;
    }

    public int getPlayStatus() {
        return currentStatus;
    }


    /**
     * 设置当前的播放状态
     * @param status
     */
    public void setPlayStatus(int status) {
        Log.i(TAG,"setPlayStatus,status = "+status);
        if(currentStatus!=status){
            this.currentStatus = status;
            if(playStatusCallBack !=null){
                playStatusCallBack.onPlayStatusChanged(status);
            }
        }

    }



    /**
     * 初始化定时器
     */
    private void initTimer(){
        if(mTimer==null){
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mAudioHandler.obtainMessage(MEDIA_PROGRESS).sendToTarget();
            }
        },0,1000);
    }


    /**
     * 释放定时器资源
     */
    private void cancelTimer(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }


    /**
     *---------------------状态回调------------------------
     */

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        Log.d(TAG,"onBufferingUpdate,percent = "+percent);
        if(lastBufferValue!=percent){
            lastBufferValue = percent;
            if (mListener != null) {
                mListener.onBufferingUpdate(iMediaPlayer, percent);
            }
        }
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        Log.i(TAG,"onCompletion, status = "+getPlayStatus());
        reset();
        stop();
        if(mListener!=null){
            mListener.onComplete(iMediaPlayer);
            mListener.onCurrentPositionChanged(0);
        }
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
        if (mListener != null) {
            mListener.onError(iMediaPlayer, what, extra);
        }
        switch (what) {
            case IMediaPlayer.MEDIA_ERROR_IO:
                Log.e(TAG,"本地文件或网络流错误");
                break;
            case IMediaPlayer.MEDIA_ERROR_MALFORMED:
                Log.e(TAG,"比特流不符合相关的编码标准和文件规范");
                break;
            case IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.e(TAG,"播放错误");
                break;
            case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.e(TAG,"服务器错误");
                break;
            case IMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.e(TAG,"超时");
                break;
            case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.e(TAG,"未知错误");
                break;
            case IMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.e(TAG,"不支持的功能");
                break;
            default:
                Log.e(TAG,"what="+what+"|extra="+extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        if (mListener != null) {
            mListener.onInfo(iMediaPlayer, what, extra);
        }
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Log.w(TAG,"错误交叉");
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.i(TAG,"缓冲结束");
                if(mListener!=null){
                    mListener.onInfoBuffer(false);
                }
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.i(TAG,"开始缓冲");
                if(mListener!=null){
                    mListener.onInfoBuffer(true);
                }
                break;
            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                break;
            case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                break;
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                break;
            case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                break;
            case IMediaPlayer.MEDIA_INFO_UNKNOWN:
                Log.i(TAG,"未知的信息");
                break;
            case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                break;
            default:
                Log.i(TAG,"what="+what+"|extra="+extra);
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mDuration = iMediaPlayer.getDuration();
        Log.i(TAG,"onPrepared,mDuration = "+mDuration);
        if(mListener!=null){
            mListener.onPrepared(iMediaPlayer);
        }
        if(!isAutoPlay){
            iMediaPlayer.start();
            setPlayStatus(AUDIO_STATUS_PLAYING);
        }
        initTimer();
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        Log.i(TAG,"onSeekComplete");
        if (mListener != null) {
            mListener.onSeekComplete(iMediaPlayer);
        }
    }

    /**
     *---------------------状态回调------------------------
     */





    /**
     * ---------------------操作方法---------------------
     */

    /**
     * 获取总时长
     * @return
     */
    public long getDuration(){
        if (mIjkMediaPlayer != null) {
            if (mDuration != 0) {
                return mDuration;
            }
            mDuration = mIjkMediaPlayer.getDuration();
            return mDuration;
        }
        return 0;
    }

    /**
     * 获取当前进度
     * @return
     */
    public long getCurrentPosition(){
        if(mIjkMediaPlayer!=null){
            return mIjkMediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying(){
        if(mIjkMediaPlayer!=null){
            return mIjkMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 快进到指定的位置
     * @param msec
     */
    public void seekTo(long msec){
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.seekTo(msec);
        }
    }

    /**
     * 设置播放的时候保持屏幕点亮
     * @param screenOn
     */
    public void setScreenOnWhilePlaying(boolean screenOn){
        if(mIjkMediaPlayer!=null){
            mIjkMediaPlayer.setScreenOnWhilePlaying(screenOn);
        }
    }


    /**
     * 设置在资源准备完成时是否自动播放
     * @param autoPlay
     */
    public void setAutoPlay(boolean autoPlay){
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", autoPlay ? 1 : 0);
            isAutoPlay = autoPlay;
        }
    }


    /**
     * 同一个资源第一次开始播放
     * @param url
     */
    public void startPlay(String url) {
        if (mIjkMediaPlayer != null) {
            try {
                mIjkMediaPlayer.setDataSource(url);
                mIjkMediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 播放
     */
    public void start(){
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.start();
            setPlayStatus(AUDIO_STATUS_PLAYING);
        }
    }

    /**
     * 暂停
     */
    public void pause(){
        if(mIjkMediaPlayer!=null){
            mIjkMediaPlayer.pause();
            setPlayStatus(AUDIO_STATUS_PAUSE);
        }
    }

    /**
     * 停止
     */
    public void stop(){
        if(mIjkMediaPlayer!=null){
            mIjkMediaPlayer.stop();
            setPlayStatus(AUDIO_STATUS_STOP);
        }
        cancelTimer();
    }

    /**
     * 重置
     */
    public void reset(){
        if(mIjkMediaPlayer!=null){
            mIjkMediaPlayer.reset();
        }
    }

    /**
     * 释放
     */
    public void release(){
        if(mIjkMediaPlayer!=null){
            mIjkMediaPlayer.release();
        }
    }

    /**
     * 销毁
     */
    public void destroy(){
        if (mIjkMediaPlayer != null) {
            setPlayStatus(AUDIO_STATUS_STOP);
            cancelTimer();
            release();
            setAudioListener(null);
            setPlayStatusCallBack(null);
            mIjkMediaPlayer = null;
            IjkMediaPlayer.native_profileEnd();
        }
    }

    /**
     * ---------------------操作方法---------------------
     */


    /**
     * 定时更新进度
     */
    private static class AudioHandler extends android.os.Handler{
        private WeakReference<AudioIjkPlayer> mPlayer;

        public AudioHandler(AudioIjkPlayer audioIjkPlayer) {
            super(Looper.getMainLooper());
            this.mPlayer = new WeakReference<>(audioIjkPlayer);
        }

        @Override
        public void handleMessage(Message msg) {
            AudioIjkPlayer audioIjkPlayer = mPlayer.get();
            if(audioIjkPlayer==null || audioIjkPlayer.getListener()==null){
                return;
            }
            switch (msg.what) {
                case MEDIA_PROGRESS:
                    audioIjkPlayer.getListener().onCurrentPositionChanged(audioIjkPlayer.getCurrentPosition());
                    break;
                default:
                    break;
            }
        }
    }



    public interface PlayStatusCallBack {
        void onPlayStatusChanged(int audioStatus);
    }
}
