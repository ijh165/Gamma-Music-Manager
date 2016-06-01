package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import com.teamgamma.musicmanagementsystem.Song;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerObserver;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Class for Music Player MainUI. Acts as the controller for the media player.
 */
public class MusicPlayerUI extends BorderPane {


    public static final int SECONDS_IN_MINUTE = 60;

    public MusicPlayerUI(MusicPlayerManager manager){
        super();

        VBox topWrapper = new VBox();

        topWrapper.getChildren().add(makeSongTitleHeader(manager));

        HBox musicFileBox = new HBox();
        Label songPathHeader = new Label("Song Path");
        TextField songPath = new TextField("Enter Path To Song");
        Button addSong = createIconButton("res\\ic_playlist_add_black_48dp_1x.png");
        addSong.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.placeSongOnPlaybackQueue(new Song(songPath.getText()));
            }
        });
        musicFileBox.getChildren().addAll(songPathHeader, songPath, addSong);
        topWrapper.getChildren().add(musicFileBox);

        topWrapper.getChildren().addAll(createProgressBarBox(manager));
        this.setTop(topWrapper);

        HBox playbackControls = new HBox();
        playbackControls.setAlignment(Pos.CENTER);

        Button previousSong = createIconButton("res\\ic_skip_previous_black_48dp_1x.png");
        previousSong.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.playPreviousSong();
            }
        });
        previousSong.setAlignment(Pos.CENTER_LEFT);
        playbackControls.getChildren().add(previousSong);

        ToggleButton playPauseButton = new ToggleButton();
        playPauseButton.setStyle("-fx-background-color: transparent");
        playPauseButton.setGraphic(createImageViewForImage("res\\ic_play_arrow_black_48dp_1x.png"));
        playPauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                boolean isPlaying = playPauseButton.isSelected();
                if (isPlaying){
                    try {
                        if (manager.isSomethingPlaying()) {
                            manager.resume();
                        } else {
                            manager.placeSongOnPlaybackQueue(new Song(songPath.getText()));
                        }
                    } catch (Exception e){
                        // Failed to load the song so do not update the image.
                        return;
                    }
                    playPauseButton.setGraphic(createImageViewForImage("res\\ic_pause_black_48dp_1x.png"));
                } else {
                    playPauseButton.setGraphic(createImageViewForImage("res\\ic_play_arrow_black_48dp_1x.png"));
                    manager.pause();
                }
            }
        });

        playPauseButton.setAlignment(Pos.CENTER);
        playbackControls.getChildren().add(playPauseButton);

        Button skipButton = createIconButton("res\\ic_skip_next_black_48dp_1x.png");
        skipButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.playNextSong();
            }
        });
        playbackControls.getChildren().add(skipButton);



        this.setCenter(playbackControls);

        HBox otherControlBox = createOtherOptionsBox(manager);
        this.setBottom(otherControlBox);

        setCssStyle();
    }

    private HBox createOtherOptionsBox(final MusicPlayerManager manager) {
        HBox otherControlBox = new HBox();
        Button volumeUpButton = createIconButton("res\\ic_volume_up_black_48dp_1x.png");
        volumeUpButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.increaseVolume();
            }
        });

        Button volumeDownButton = createIconButton("res\\ic_volume_down_black_48dp_1x.png");
        volumeDownButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.decreaseVolume();
            }
        });

        ToggleButton repeatSongButton = new ToggleButton();
        repeatSongButton.setGraphic(createImageViewForImage("res\\ic_repeat_black_48dp_1x.png"));
        repeatSongButton.setStyle("-fx-background-color: transparent");
        repeatSongButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                boolean isSelected = repeatSongButton.isSelected();
                manager.setRepeat(isSelected);
                if (isSelected){
                    repeatSongButton.setStyle("-fx-background-color: lightgray");
                } else{
                    repeatSongButton.setStyle("-fx-background-color: transparent");
                }
            }
        });

        otherControlBox.getChildren().addAll(volumeDownButton, volumeUpButton, repeatSongButton);
        return otherControlBox;
    }

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }

    private VBox createProgressBarBox(final MusicPlayerManager manager) {
        VBox musicPlayerProgress = new VBox();

        HBox progressWrapper = new HBox();
        Label songStartLable = new Label("0:00");

        HBox songTimesWrapper = new HBox();
        Label currentTimeLabel = createHeadingLabel("0:00");
        Label constantLabel = createHeadingLabel("\\");
        Label songEndTimeText = createHeadingLabel("0:00");
        songTimesWrapper.getChildren().addAll(currentTimeLabel, constantLabel, songEndTimeText);
        songTimesWrapper.setAlignment(Pos.CENTER_RIGHT);

        Label songEndTimeProgressBar = new Label("0:00");
        Label songEndTimeSeekBar = new Label("0:00");

        // Set up an observer to update the songEndTime based on what song is being played.
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                String endTimeString = convertDurationToTimeString(manager.getEndTime());
                songEndTimeProgressBar.setText(endTimeString);
                songEndTimeSeekBar.setText(endTimeString);
                songEndTimeText.setText(endTimeString);
            }
        });

        // Resizing from https://docs.oracle.com/javafx/2/api/javafx/scene/layout/HBox.html
        ProgressBar songPlaybar = new ProgressBar();
        songPlaybar.setMaxWidth(Double.MAX_VALUE);
        songPlaybar.setProgress(0);
        progressWrapper.getChildren().addAll(songStartLable, songPlaybar, songEndTimeProgressBar);
        HBox.setHgrow(songPlaybar, Priority.ALWAYS);


        HBox playbackSliderWrapper = new HBox();
        Slider playbackSlider = new Slider(0, 1.0, 0);
        playbackSlider.setBlockIncrement(0.01);
        HBox.setHgrow(playbackSlider, Priority.ALWAYS);
        playbackSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                manager.seekSongTo(playbackSlider.getValue());
            }
        });
        playbackSliderWrapper.getChildren().addAll(new Label("0:00"), playbackSlider, songEndTimeSeekBar);

        // Setup the observer pattern stuff for UI updates to the current play time.
        manager.registerSeekObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                Duration currentPlayTime = manager.getCurrentPlayTime();

                double progress = currentPlayTime.toMillis() / manager.getEndTime().toMillis();
                songPlaybar.setProgress(progress);
                playbackSlider.setValue(progress);
                // Have to run this one later. Odd that progress bar did not have this problem.
                // http://stackoverflow.com/questions/29449297/java-lang-illegalstateexception-not-on-fx-application-thread-currentthread-t
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentTimeLabel.setText(convertDurationToTimeString(currentPlayTime));
                    }
                });

            }
        });

        musicPlayerProgress.getChildren().addAll(progressWrapper, playbackSliderWrapper, songTimesWrapper);
        return musicPlayerProgress;
    }

    private String convertDurationToTimeString(Duration endtime) {
        String endingTime = "";

        double seconds = endtime.toSeconds();
        int minutes = 0;
        while ((seconds - SECONDS_IN_MINUTE) >= 0){
            minutes++;
            seconds -= SECONDS_IN_MINUTE;
        }
        endingTime = minutes + ":";

        long leftOverSeconds = Math.round(seconds);
        if (leftOverSeconds < 10){
            // Add on so it looks like 0:05 rather than 0:5
            endingTime += "0";
        }

        endingTime += leftOverSeconds;

        return endingTime;
    }

    private HBox makeSongTitleHeader(final MusicPlayerManager manager) {
        HBox songTitleWrapper = new HBox();
        Label songTitleHeader = new Label("Song Currently Playing : ");
        Label songTitle = new Label("");

        // Set up an observer that will update the name of the song when a new song is played.
        manager.registerNewSongObserver(new MusicPlayerObserver() {
            @Override
            public void updateUI() {
                songTitle.setText(manager.getCurrentSongPlaying().getM_songName());
            }
        });
        songTitleWrapper.getChildren().addAll(songTitleHeader, songTitle);

        return songTitleWrapper;
    }

    private ImageView createImageViewForImage(String imagePath){
        // Idea for background image from http://stackoverflow.com/questions/29984228/javafx-button-background-image
        return new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(imagePath)));
    }

    private Button createIconButton(String pathToIcon){
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent");
        button.setGraphic(createImageViewForImage(pathToIcon));
        return button;
    }

    private Label createHeadingLabel(String textForLabel){
        Label label = new Label(textForLabel);
        label.setFont(new Font(20));
        return label;
    }
}
