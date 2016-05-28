package com.teamgamma.musicmanagementsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a playlist
 */
public class Playlist {
    private String m_playlistName;
    private List<Song> m_songList;

    public Playlist(String playlistName) {
        m_playlistName = playlistName;
        m_songList = new ArrayList<>();
    }

    public boolean addSong(Song songToAdd) {
        return m_songList.add(songToAdd);
    }

    public boolean removeSong(Song songToAdd) {
        return m_songList.remove(songToAdd);
    }
}