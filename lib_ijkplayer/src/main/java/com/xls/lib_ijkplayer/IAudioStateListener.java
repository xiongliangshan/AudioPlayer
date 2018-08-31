package com.xls.lib_ijkplayer;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public interface IAudioStateListener {

    void onComplete(IMediaPlayer iMediaPlayer);

    void onPrepared(IMediaPlayer iMediaPlayer);

    void onSeekComplete(IMediaPlayer iMediaPlayer);

    void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent);

    void onInfoBuffer(boolean startBuffer);

    void onInfo(IMediaPlayer iMediaPlayer, int what, int extra);

    void onCurrentPositionChanged(long currentPosition);//播放中进度改变

    void onError(IMediaPlayer iMediaPlayer, int what, int extra);
}
