package com.teamgamma.musicmanagementsystem.model;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

/**
 * Underlying data structure for a Song. A File plus data additional data.
 * Credits to http://www.jthink.net/jaudiotagger/ for reading writing the song metadata
 */
public class Song {
    private File m_file;
    private String m_fileName;
    private String m_title;
    private String m_artist;
    private String m_album;
    private String m_genre;
    private String m_rating;

    public Song(String pathToFile) {
        m_file = new File(pathToFile);
        m_fileName = m_file.getName();
        try {
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();

            //add new tag to file if tag is empty
            if (tag == null) {
                tag = new ID3v24Tag();
                tag.setField(FieldKey.TITLE, "");
                tag.setField(FieldKey.ARTIST, "");
                tag.setField(FieldKey.ALBUM, "");
                tag.setField(FieldKey.GENRE, "");
                tag.setField(FieldKey.RATING, "");
                file.setTag(tag);
                AudioFileIO.write(file);
            }

            //parse metadata tags to attributes
            m_title = tag.getFirst(FieldKey.TITLE);
            m_artist = tag.getFirst(FieldKey.ARTIST);
            m_album = tag.getFirst(FieldKey.ALBUM);
            m_genre = tag.getFirst(FieldKey.GENRE);
            String ratingInMetadata = tag.getFirst(FieldKey.RATING);
            m_rating = Integer.toString(
                    convertRatingToFiveStarScale(ratingInMetadata.equals("") ? 0 : Integer.parseInt(ratingInMetadata))
            );
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    //credits to https://github.com/soc/jaudiotagger/blob/master/src/main/java/org/jaudiotagger/tag/id3/reference/MediaMonkeyPlayerRating.java
    private static int convertRatingFromFiveStarScale(int value) {
        if (value < 0 || value > 5)
            throw new IllegalArgumentException("convertRatingFromFiveStarScale() accepts values from 0 to 5 not: " + value);

        int newValue = 0;
        switch (value) {
            case 0:
                break;

            case 1:
                newValue = 1;
                break;

            case 2:
                newValue = 64;
                break;

            case 3:
                newValue = 128;
                break;

            case 4:
                newValue = 196;
                break;

            case 5:
                newValue = 255;
                break;

        }

        return newValue;
    }

    //credits to https://github.com/soc/jaudiotagger/blob/master/src/main/java/org/jaudiotagger/tag/id3/reference/MediaMonkeyPlayerRating.java
    private static int convertRatingToFiveStarScale(int value) {
        int newValue = 0;
        if (value <= 0)
            newValue = 0;
        else if (value <= 1)
            newValue = 1;
        else if (value <= 8)
            newValue = 0;
        else if (value <= 18)
            newValue = 1;
        else if (value <= 28)
            newValue = 1;
        else if (value <= 28)
            newValue = 1;
        else if (value <= 28)
            newValue = 1;
        else if (value <= 28)
            newValue = 1;
        else if (value <= 29)
            newValue = 2;
        else if (value <= 39)
            newValue = 1;
        else if (value <= 49)
            newValue = 1;
        else if (value <= 113)
            newValue = 2;
        else if (value <= 167)
            newValue = 3;
        else if (value <= 218)
            newValue = 4;
        else
            newValue = 5;

        return newValue;
    }

    public File getM_file() {
        return m_file;
    }

    public String getM_fileName() {
        return m_fileName;
    }

    public String getM_title() {
        return m_title;
    }

    public String getM_artist() {
        return m_artist;
    }

    public String getM_album() {
        return m_album;
    }

    public String getM_genre() {
        return m_genre;
    }

    public String getM_rating() {
        return m_rating;
    }

    public void setTitle(String title) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.TITLE, title);
            AudioFileIO.write(file);
            //update object attr
            m_title = title;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    public void setArtist(String artist) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.ARTIST, artist);
            AudioFileIO.write(file);
            //update object attr
            m_artist = artist;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    public void setAlbum(String album) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.ALBUM, album);
            AudioFileIO.write(file);
            //update object attr
            m_album = album;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    public void setGenre(String genre) {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.GENRE, genre);
            AudioFileIO.write(file);
            //update object attr
            m_genre = genre;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }

    public void setRating(int rating) throws IllegalArgumentException {
        try {
            //update metadata
            AudioFile file = AudioFileIO.read(m_file);
            Tag tag = file.getTag();
            tag.setField(FieldKey.RATING, Integer.toString(convertRatingFromFiveStarScale(rating)));
            AudioFileIO.write(file);
            //update object attr
            m_rating = Integer.toString(rating);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace(); //for now
        }
    }
}