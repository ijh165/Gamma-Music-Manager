package com.teamgamma.musicmanagementsystem;

import java.util.List;

/**
 * Class to maintain a library in a system.
 */
public class Library {

    private List<Song> m_songList;

    private String m_rootDir;

    public Library(String folderPath) {
        m_rootDir = folderPath;
        m_songList = new FileManager().generateSongs(folderPath);
    }

    public boolean addSong(Song songToAdd) {
        try {
            return m_songList.add(songToAdd);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeSong(Song songToRemove) {
        try {
            FileManager fileManager = new FileManager();
            return fileManager.removeFile(songToRemove.getM_file());
        } catch(Exception e) {
            // if any error occurs
            e.printStackTrace();
        }
        return false;
    }

    public boolean copySong(Song songToCopy, String pathToDest) {
        return false;
    }

    public Song getSong(String songName) {
        for (Song song : m_songList) {
            if(song.getM_songName().equals(songName)) return song;
        }
        return null;
    }

    public Song getSong(Song song) {
        if(m_songList.contains(song)) {
            int index = m_songList.indexOf(song);
            return m_songList.get(index);
        }
        return null;
    }

    public List<Song> getM_songList() {
        return m_songList;
    }

    public String getM_rootDir() {
        return m_rootDir;
    }
}
