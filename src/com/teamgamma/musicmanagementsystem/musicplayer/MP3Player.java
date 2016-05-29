package com.teamgamma.musicmanagementsystem.musicplayer;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.teamgamma.musicmanagementsystem.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Class to play a MP3 using JavaFX MediaPlayer class.
 */
public class MP3Player implements IMusicPlayer {

    // Constants for volume control
    public static final double VOLUME_CHANGE = 0.1;
    public static final double MAX_VOLUME = 1.0;
    public static final int MIN_VOLUME = 0;

    private Song m_currentSong;

    private MediaPlayer m_player;

    private MusicPlayerManager m_manager;

    private boolean m_repeatFlag = false;

    public MP3Player(MusicPlayerManager manager){
        m_manager = manager;
    }

    @Override
    public void playSong(Song songToPlay) {
        m_currentSong = songToPlay;

        // Stop any player if there is one going.
        if (null != m_player) {
            m_player.dispose();
        }

        m_player = new MediaPlayer(new Media(songToPlay.getM_file().toURI().toString()));
        repeatSong(m_repeatFlag);
        m_player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                m_manager.moveToNextSong();
            }
        });


        m_player.setOnReady(new Runnable() {
            @Override
            public void run() {
                // Need to notify observers when the player is ready so the data given will be correct.
                m_manager.notifyNewSongObservers();
            }
        });
        m_player.play();

    }

    @Override
    public void pauseSong() {
        m_player.pause();
    }

    @Override
    public void resumeSong() {
        m_player.play();
    }

    @Override
    public void increaseVolume() {
        double currentVolume = m_player.getVolume();
        if (currentVolume < MAX_VOLUME) {
            currentVolume += VOLUME_CHANGE;
        }
        m_player.setVolume(currentVolume);
    }

    @Override
    public void decreaseVolume(){
        double currentVolume = m_player.getVolume();
        if (currentVolume > MIN_VOLUME) {
            currentVolume -= VOLUME_CHANGE;
        }
        m_player.setVolume(currentVolume);
    }

    @Override
    public void repeatSong(boolean repeatSong){
        m_repeatFlag = repeatSong;
        if (repeatSong) {
            m_player.setCycleCount(MediaPlayer.INDEFINITE);
        }
        else {
            m_player.setCycleCount(1);

        }
    }

    @Override
    public void setOnSongFinishAction(Runnable action) {
        m_player.setOnEndOfMedia(action);
    }

    @Override
    public void setOnErrorAction(Runnable action) {
        m_player.setOnError(action);
    }

    public MediaPlayer getMusicPlayer(){
        return m_player;
    }

    public Duration getEndTime(){
        return m_player.getCycleDuration();

    }

}
