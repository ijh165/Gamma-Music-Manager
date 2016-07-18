package com.teamgamma.musicmanagementsystem.util;

import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Song;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerConstants;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.ui.MusicPlayerPlaybackQueueUI;
import com.teamgamma.musicmanagementsystem.ui.PromptUI;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Class to hold some helper function for building UIs.
 */
public class UserInterfaceUtils {

    // Constants
    public static final String SELECTED_BACKGROUND_COLOUR = "-fx-background-color: #BFDCF5;";

    public static final int VERTICAL_SPACING = 5;
    public static final int HORIZONTAL_ELEMENT_SPACING = 5;
    public static final int MAX_HEIGHT = 400;
    public static final int PREF_HEIGHT = 175;

    /**
     * Function to create a UI indication when mousing over something.
     *
     * @param element  The element to apply UI effect on.
     */
    public static void createMouseOverUIChange(final Node element, String defaultStyle) {
        element.setOnMouseEntered(event -> element.setStyle(SELECTED_BACKGROUND_COLOUR));
        element.setOnMouseExited(event -> element.setStyle(defaultStyle));
    }

    /**
     * Helper function to create a button that displays the image passed in.
     *
     * @param pathToIcon The path to the image to use.
     * @return The button with the image being used.
     */
    public static Button createIconButton(String pathToIcon) {
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent");
        button.setGraphic(createImageViewForImage(pathToIcon));
        return button;
    }

    /**
     * Helper function to convert a path to a image to a actual image you can use.
     *
     * @param imagePath The path to a image.
     * @return A ImageView that contains the image that is passed in.
     */
    public static ImageView createImageViewForImage(String imagePath) {
        // Replace path separator to correct OS.
        imagePath = imagePath.replace("\\", File.separator);
        imagePath = imagePath.replace("/", File.separator);

        // Idea for background image from http://stackoverflow.com/questions/29984228/javafx-button-background-image
        return new ImageView(imagePath);
    }

    /**
     * Helper function to convert the duration obejct to a human readable format. The format is like the following MM:SS
     *
     * @param duration The duration to convert.
     * @return A human readable string of the duration.
     */
    public static String convertDurationToTimeString(Duration duration) {
        String timeString = "";

        double seconds = duration.toSeconds();
        int minutes = 0;
        while ((seconds - MusicPlayerConstants.SECONDS_IN_MINUTE) >= 0) {
            minutes++;
            seconds -= MusicPlayerConstants.SECONDS_IN_MINUTE;
        }
        timeString = minutes + ":";

        long leftOverSeconds = (int) seconds;
        if (leftOverSeconds < 10) {
            // Add on so it looks like 0:05 rather than 0:5
            timeString += "0";
        }
        timeString += leftOverSeconds;
        return timeString;
    }

    /**
     * Delete a song that has been selected by the user.
     *
     * @param fileToDelete song selected
     */
    public static void deleteFileAction(SongManager model,
                                        MusicPlayerManager musicPlayerManager,
                                        DatabaseManager databaseManager,
                                        File fileToDelete) {
        //confirmation dialog
        if (fileToDelete.isDirectory()) {
            if (!PromptUI.recycleLibrary(fileToDelete)) {
                return;
            }
        } else {
            if (!PromptUI.recycleSong(fileToDelete)) {
                return;
            }
        }

        //try to actually delete (retry if FileSystemException happens)
        final int NUM_TRIES = 2;
        for (int i = 0; i < NUM_TRIES; i++) {
            try {
                model.deleteFile(fileToDelete);
                break;
            } catch (IOException ex) {
                musicPlayerManager.stopSong();
                musicPlayerManager.removeSongFromHistory(musicPlayerManager.getCurrentSongPlaying());

                if (musicPlayerManager.isThereANextSong()) {
                    musicPlayerManager.playNextSong();
                } else if (!musicPlayerManager.getHistory().isEmpty()) {
                    musicPlayerManager.playPreviousSong();
                } else {
                    musicPlayerManager.unloadSong();
                }

                if (i == 1) { //if this exception still thrown after retry (for debugging)
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                PromptUI.customPromptError("Error", null, "Exception: \n" + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }

        musicPlayerManager.notifyNewSongObservers();
        musicPlayerManager.notifyQueingObserver();
        musicPlayerManager.notifyChangeStateObservers();

        databaseManager.removeLibrary(fileToDelete.getAbsolutePath()); //only succeed if fileToDelete is library folder
    }

    /**
     * Function to apply a black boarder for the node passed in.
     *
     * @param element The element to style
     */
    public static void applyBlackBoarder(Node element) {
        final String cssDefault = "-fx-border-color: black;\n";
        element.setStyle(cssDefault);
    }

    /**
     * Function to set the button passed in to change its image when mouse over and exit.
     *
     * @param button                The button to set.
     * @param mouseOverImagePath    The path to the image to show when mouse is over the button.
     * @param mouseExitImagePath    The path to image to show when mouse leaves the button.
     */
    public static void setMouseOverImageChange(Button button, String mouseOverImagePath, String mouseExitImagePath) {
        button.setOnMouseEntered(event -> button.setGraphic(new ImageView(mouseOverImagePath)));
        button.setOnMouseExited(event -> button.setGraphic(new ImageView(mouseExitImagePath)));
    }

    /**
     * Interface to abstract the logic needed to build a row for displaying a collection of songs. This will be used
     * so that we can have less duplicate code for displaying songs from a collection.
     */
    public interface ILabelAction {
        HBox createRow(Song songForRow, int songIndex);
    }

    /**
     * Function to create UI element that will hold a list of songs in it based on the collection passed in.
     *
     * @param listOfSongs   The list of songs to display.s
     * @return A scrollable UI element that contains all the songs in the collection.
     */
    public static ScrollPane createUIList(Collection<Song> listOfSongs, ILabelAction rowCreation) {
        ScrollPane wrapper = new ScrollPane();

        VBox allSongs = new VBox();

        allSongs.setSpacing(VERTICAL_SPACING);

        int songNumber = 1;
        for (Song song : listOfSongs) {
            HBox row = rowCreation.createRow(song, songNumber);
            String baseStyle = row.getStyle();

            row.setOnMouseEntered(event -> row.setStyle("-fx-background-color: lightgray"));
            row.setOnMouseExited(event -> row.setStyle(baseStyle));

            HBox.setHgrow(row, Priority.ALWAYS);
            row.setFillHeight(true);
            row.setSpacing(HORIZONTAL_ELEMENT_SPACING);
            allSongs.getChildren().add(row);

            songNumber++;
        }
        allSongs.setPrefHeight(PREF_HEIGHT);
        allSongs.setMaxHeight(MAX_HEIGHT);
        allSongs.setFillWidth(true);

        wrapper.setContent(allSongs);
        return wrapper;
    }

    /**
     * Helper function to create a titled pane for the accordion
     *
     * @param title The title of the accordion
     * @param songs The collection of songs that is wanted to be displayed.
     * @param action The action to take when building a row.
     * @return  A TitlePane with the title and collection of songs displayed.
     */
    public static TitledPane createTitlePane(String title, Collection<Song> songs, ILabelAction action) {
        TitledPane titlePane = new TitledPane(title, UserInterfaceUtils.createUIList(songs, action));
        titlePane.setAnimated(true);
        titlePane.setCollapsible(true);
        titlePane.setExpanded(false);
        return titlePane;
    }
}
