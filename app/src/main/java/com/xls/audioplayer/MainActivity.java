package com.xls.audioplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xls.lib_ijkplayer.AudioIjkPlayer;
import com.xls.lib_ijkplayer.SimpleAudioStateListener;

import java.util.logging.Logger;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MainActivity extends AppCompatActivity {

    private AudioIjkPlayer mPlayer;
    private TextView tvDuration;
    private TextView tvCurrentTime;
    private TextView tvRemainingTime;

    private ProgressBar pbBuffering;
    private ImageView ivPlay;
    private SeekBar seekBar;
    private RelativeLayout rlPlayGroup;
    private String mAudioUrl = "https://zm-chat-lessons.oss-cn-hangzhou.aliyuncs.com/93e835a1d6614e3eb0c9cbe5db603e19/audio.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPlayer();
        setListener();
    }



    private void initView(){
        tvDuration = findViewById(R.id.media_tv_duration);
        tvCurrentTime =findViewById(R.id.tv_current_time);
        tvRemainingTime =findViewById(R.id.tv_remaining_time);
        pbBuffering = findViewById(R.id.pb_buffering);
        ivPlay = findViewById(R.id.iv_play);
        seekBar = findViewById(R.id.seekbar);
        rlPlayGroup =findViewById(R.id.rl_play_group);
    }

    private void initPlayer(){
        if(mPlayer==null){
            mPlayer = AudioIjkPlayer.newInstance();
            mPlayer.setAudioListener(new SimpleAudioStateListener(){
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    pbBuffering.setVisibility(View.INVISIBLE);
                    ivPlay.setVisibility(View.VISIBLE);
                    long duration = mPlayer.getDuration();
                    tvDuration.setText(DateTimeUtils.getRecordTime((int) (duration/1000)));
                    tvCurrentTime.setText(DateTimeUtils.getRecordTime(0)+"-"+DateTimeUtils.getRecordTime((int) (duration/1000)));
                    tvRemainingTime.setText("-"+DateTimeUtils.getRecordTime((int) (duration/1000)));
                }

                @Override
                public void onComplete(IMediaPlayer iMediaPlayer) {
                    ivPlay.setVisibility(View.VISIBLE);
                    pbBuffering.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCurrentPositionChanged(long currentPosition) {
                    long duration = mPlayer.getDuration();
                    long remain = duration - currentPosition;
                    if(remain<0){
                        remain = 0;
                    }
                    int progress = Math.round((1f*currentPosition/duration) * seekBar.getMax());
                    seekBar.setProgress(progress);
                    tvCurrentTime.setText(DateTimeUtils.getRecordTime((int)(currentPosition/1000)));
                    tvRemainingTime.setText("-"+DateTimeUtils.getRecordTime((int) (remain/1000)));
                }

                @Override
                public void onInfoBuffer(boolean startBuffer) {
                    if(startBuffer){
                        //开始缓冲
                        pbBuffering.setVisibility(View.VISIBLE);
                        ivPlay.setVisibility(View.GONE);
                    }else {
                        //缓冲结束
                        pbBuffering.setVisibility(View.INVISIBLE);
                        ivPlay.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
                    int bufferingProgress = (int) (1f * percent / 100 * seekBar.getMax());
                    seekBar.setSecondaryProgress(bufferingProgress);
                }

                @Override
                public void onError(IMediaPlayer iMediaPlayer, int what, int extra) {
                    ToastUtils.show(MainActivity.this,"解析音频文件出错，("+what+","+extra+")");
                }
            });
            mPlayer.setPlayStatusCallBack(new AudioIjkPlayer.PlayStatusCallBack() {
                @Override
                public void onPlayStatusChanged(int audioStatus) {
                    switch (audioStatus){
                        case AudioIjkPlayer.AUDIO_STATUS_PLAYING:
                            ivPlay.setImageResource(R.mipmap.cg_record_stop);
                            break;
                        case AudioIjkPlayer.AUDIO_STATUS_PAUSE:
                            ivPlay.setImageResource(R.mipmap.cg_record_play);
                            break;
                        case AudioIjkPlayer.AUDIO_STATUS_STOP:
                            ivPlay.setImageResource(R.mipmap.cg_record_play);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }


    private void setListener(){
        rlPlayGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.getPlayStatus()== AudioIjkPlayer.AUDIO_STATUS_STOP) {
                    //点击播放
                    pbBuffering.setVisibility(View.VISIBLE);
                    ivPlay.setVisibility(View.GONE);

                    if(TextUtils.isEmpty(mAudioUrl)){
                        ToastUtils.show(MainActivity.this,"音频文件不存在，url为空");
                        return;
                    }
                    mPlayer.startPlay(mAudioUrl);
                } else if(mPlayer.getPlayStatus()== AudioIjkPlayer.AUDIO_STATUS_PAUSE){
                    //点击暂停
                    mPlayer.start();
                } else {
                    mPlayer.pause();
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mPlayer!=null){
                    long duration = mPlayer.getDuration();
                    int progress = seekBar.getProgress();
                    long time = (long) (1f * progress / seekBar.getMax() * duration);
                    mPlayer.seekTo(time);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayer!=null){
            mPlayer.destroy();
        }
    }
}
