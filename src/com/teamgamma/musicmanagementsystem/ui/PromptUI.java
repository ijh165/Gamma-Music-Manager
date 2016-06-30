package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.model.FileManager;
import com.teamgamma.musicmanagementsystem.model.Playlist;
import com.teamgamma.musicmanagementsystem.model.Song;

import com.teamgamma.musicmanagementsystem.model.SongManager;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;


/**
 * Various prompts for UI
 */
public class PromptUI {

    // ---------------------- Custom Prompts

    /**
     * Custom information prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title       of prompt
     * @param headerText  (optional)
     * @param bodyMessage within prompt
     */
    public static void customPromptInformation(String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);

        alert.showAndWait();
    }

    /**
     * Custom confirmation prompt for use. Contains "OK" and "Cancel" buttons
     *
     * @param title       of prompt
     * @param headerText  (optional)
     * @param bodyMessage within prompt
     * @return false if user clicks "Cancel"
     */
    public static boolean customPromptConfirmation(String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);

        Optional<ButtonType> result = alert.showAndWait();
        return (result.isPresent() && result.get() == ButtonType.OK);
    }

    /**
     * Custom warning prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title       of prompt
     * @param headerText  (optional)
     * @param bodyMessage within prompt
     */
    public static void customPromptWarning(String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);

        alert.showAndWait();
    }

    /**
     * Custom error prompt for use. Note that this prompt only contains a single "OK" button
     *
     * @param title       of prompt
     * @param headerText  (optional)
     * @param bodyMessage within prompt
     */
    public static void customPromptError(String title, String headerText, String bodyMessage) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(bodyMessage);

        alert.showAndWait();
    }

    // ---------------------- Initialization

    /**
     * Initial welcome prompt for first time startup. Browse button allows user
     * to browse directories and choose a folder
     *
     * @return set directory for master panel
     */
    public static String initialWelcome() {

        Dialog dialog = new Dialog<>();
        dialog.setTitle("Welcome!");
        dialog.setHeaderText(null);
        // TEMPORARY
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "gamma-logo.png"), 88, 20, false, false)));

        dialog.setContentText("Welcome to the Music Management System. Before " +
                "beginning, please select a media library.");
        ButtonType browse = new ButtonType("Browse");
        dialog.getDialogPane().getButtonTypes().addAll(browse, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == browse) {
            DirectoryChooser directory = new DirectoryChooser();
            File selectedFile = directory.showDialog(null);
            if (selectedFile != null) {
                return selectedFile.getAbsolutePath();
            }
        }
        return null;
    }

    // ----------------------  Error Prompts

    /**
     * File not found in program (copy)
     *
     * @param missingFile not found
     * @return true if user wishes to delete file reference
     */
    public static boolean fileNotFoundCopy(File missingFile) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("File Not Found");
        alert.setHeaderText("An error occured while copying \"" + missingFile.getName() + "\":");
        alert.setContentText("The file " + missingFile.getAbsolutePath() + " is not found. Delete " +
                "its reference?");

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        return (result.isPresent() && result.get() == deleteReference);

    }

    /**
     * File not found in program (move)
     *
     * @param missingFile not found
     * @return true if user wishes to delete file reference
     */
    public static boolean fileNotFoundMove(File missingFile) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("File Not Found");
        alert.setHeaderText("An error occured while moving \"" + missingFile.getName() + "\":");
        alert.setContentText("The file " + missingFile.getAbsolutePath() + " is not found. Delete " +
                "its reference?");

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        return (result.isPresent() && result.get() == deleteReference);

    }

    /**
     * File copied is attempting to paste into a song file as its destination (instead of a folder)
     *
     * @param copiedFile      copied
     * @param destinationFile paste
     */
    public static void invalidPasteDestination(File copiedFile, File destinationFile) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Copy Error");
        alert.setHeaderText("An error occured while pasting \"" + copiedFile.getName() + "\":");
        alert.setContentText("The file cannot be pasted into the media file " +
                destinationFile.getName() + ". Please paste into a folder instead.");

        alert.showAndWait();
    }


    /**
     * File failed to rename
     *
     * @param file renamed
     */
    public static void failedToRename(File file) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Rename Error");
        alert.setHeaderText(null);
        alert.setContentText("The file \"" + file + "\" could not be renamed.");

    }

    /**
     * Unknown crash; could be used in else statement for error checking
     */
    public static void unexpectedCrash() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Unexpected Crash");
        alert.setHeaderText(null);
        alert.setContentText("Something has caused the program to crash unexpectedly.");

        alert.showAndWait();
        System.exit(0);
    }

    // ---------------------- Information Prompts

    /**
     * File exists in directory, after copy attempt
     *
     * @param duplicate file
     * @return 0 if user clicks cancel
     */
    public static int fileAlreadyExists(File duplicate) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("File Name Exists");
        alert.setHeaderText(null);
        alert.setContentText("The file " + duplicate.getAbsolutePath() + " already exists in the folder.");

        ButtonType replace = new ButtonType("Replace Existing");
        ButtonType rename = new ButtonType("Rename Current");
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(replace, rename, cancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == replace) {
            return 1;
        } else if (result.isPresent() && result.get() == rename) {
            fileRenameDuplicate(duplicate);
            return 2;
        } else {
            return 0;
        }
    }

    // ---------------------- Text Prompts

    /**
     * Prompt when user clicks Add New Library button
     *
     * @return user's library name (null if user cancels)
     */
    public static String addNewLibrary() {
        DirectoryChooser directory = new DirectoryChooser();
        File selectedFile = directory.showDialog(null);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }

        return null;
    }

    /**
     * Prompt for editing song metadata
     *
     * @param song file for editing
     */
    public static void editMetadata(Song song) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Song Info");
        dialog.setHeaderText(song.getM_title() + "\n" +
                song.getM_artist() + "\n" +
                song.getM_album());

        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "edit-metadata.png"))));

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField title = new TextField();
        title.setText(song.getM_title());
        title.setPrefWidth(200);
        TextField artist = new TextField();
        artist.setText(song.getM_artist());
        TextField album = new TextField();
        album.setText(song.getM_album());
        TextField genre = new TextField();
        genre.setText(song.getM_genre());

        ChoiceBox<String> rating = new ChoiceBox<>();
        rating.getItems().addAll("No rating", "1", "2", "3", "4", "5");
        if (song.getM_rating() == 0) {
            rating.getSelectionModel().select("No rating");
        } else if (song.getM_rating() == 1) {
            rating.getSelectionModel().select("1");
        } else if (song.getM_rating() == 2) {
            rating.getSelectionModel().select("2");
        } else if (song.getM_rating() == 3) {
            rating.getSelectionModel().select("3");
        } else if (song.getM_rating() == 4) {
            rating.getSelectionModel().select("4");
        } else if (song.getM_rating() == 5) {
            rating.getSelectionModel().select("5");
        } else {
            throw new IllegalArgumentException("File rating is out of range!");
        }

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Artist:"), 0, 1);
        grid.add(artist, 1, 1);
        grid.add(new Label("Album:"), 0, 2);
        grid.add(album, 1, 2);
        grid.add(new Label("Genre:"), 0, 3);
        grid.add(genre, 1, 3);
        grid.add(new Label("Rating:"), 0, 4);
        grid.add(rating, 1, 4);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == okButton) {
            song.setTitle(title.getText());
            song.setArtist(artist.getText());
            song.setAlbum(album.getText());
            song.setGenre(genre.getText());

            String ratingStr = rating.getValue();
            song.setRating(ratingStr.equals("No rating") ? 0 : Integer.parseInt(ratingStr));
        }
    }

    // ---------------------- Confirmation Prompts

    /**
     * Delete folder and contents
     *
     * @param folder to delete
     */
    public static void deleteFolder(File folder) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete folder and all contents:");
        alert.setContentText("Are you sure you want do delete the folder " + folder.getName() + "?");

        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == deleteReference) {
            folder.delete();
        }
    }

    /**
     * Delete song
     *
     * @param mediaFile to delete
     */
    public static void deleteSong(File mediaFile) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want do delete " + mediaFile.getName() + "?");
        ButtonType deleteReference = new ButtonType("Yes");
        ButtonType cancel = new ButtonType("No");

        alert.getButtonTypes().setAll(deleteReference, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == deleteReference) {
            mediaFile.delete();
        }
    }

    /**
     * Corrupted or invalid .mp3 file. Leaves user no choice but to delete the file
     *
     * @param corruptedFile detected in the system
     * @return true if file has been deleted
     */
    public static boolean invalidMediaFile(File corruptedFile) {
        Dialog dialog = new Dialog();
        dialog.setTitle("Invalid media file");
        dialog.setHeaderText("\"" + corruptedFile.getName() + "\":");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "missing-song.png"))));
        dialog.setContentText("The program has detected that this file is either corrupted or an invalid MP3 file.");
        ButtonType okButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton);

        dialog.showAndWait();

        return corruptedFile.delete();
    }


    /**
     * Renames folder. Keeps track of "_n" suffix of file if more duplicates found, and increments n
     * (shown as the default value for the text box)
     *
     * @param duplicate file
     */
    public static Path fileRenameDuplicate(File duplicate) {
        int numIndex = 2;
        String fileNameFull = duplicate.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        String lastChar = fileNameFull.substring(beforeExtension - 1, beforeExtension);
        String extension = fileNameFull.substring(beforeExtension);
        // TODO FIX INCREMENT FOR LAST CHARACTER
        if (!Character.isLetter(lastChar.charAt(0))) {
            numIndex = Character.getNumericValue(lastChar.charAt(0)) + 1;
            fileNameFull = fileNameFull.substring(0, beforeExtension - 2) +
                    fileNameFull.substring(beforeExtension);
            beforeExtension -= 2;
        }

        TextInputDialog dialog = new TextInputDialog(fileNameFull.substring(0,
                beforeExtension) + "_" + numIndex);
        dialog.setTitle("Name Already Exists");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "rename-file-exists.png"))));
        dialog.setHeaderText("The file name \"" + duplicate.getName() + "\" already exists in the folder!");
        dialog.setContentText("Rename the file to:");

        Optional<String> result = dialog.showAndWait();

        try {
            Path source = Paths.get(duplicate.getAbsolutePath());
            if (result.isPresent()) {
                String parentDirectory = duplicate.getParent();
                File newName = new File(parentDirectory + File.separator + result.get() + extension);
                if (result.get().isEmpty()) {
                    return fileRenameRetry(duplicate);
                } else if (newName.exists()) {
                    return fileRenameDuplicate(newName);
                } else if (containsIllegalChar(result.get())) {
                    return fileRenameInvalidChar(duplicate);
                }
                return Files.move(source, source.resolveSibling(result.get() + extension));
            }
        } catch (IOException e) {
            failedToRename(duplicate);
        }

        return null;
    }

    /**
     * Renames folder. Keeps track of "_n" suffix of file if more duplicates found, and increments n
     * (shown as the default value for the text box)
     *
     * @param duplicate folder
     */
    public static Path folderRenameDuplicate(File duplicate) {
        int numIndex = 2;
        String folderNameFull = duplicate.getName();
        int lastCharIndex = folderNameFull.length() - 1;

        // TODO FIX INCREMENT FOR LAST CHARACTER
        if (!Character.isLetter(folderNameFull.charAt(lastCharIndex))) {
            numIndex = Character.getNumericValue(lastCharIndex) + 1;
        }

        TextInputDialog dialog = new TextInputDialog(folderNameFull + "_" + numIndex);
        dialog.setTitle("Name Already Exists");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "rename-folder-exists.png"))));
        dialog.setHeaderText("The folder name \"" + duplicate.getName() + "\" already exists in the directory!");
        dialog.setContentText("Rename the folder to:");

        Optional<String> result = dialog.showAndWait();

        try {
            Path source = Paths.get(duplicate.getAbsolutePath());
            if (result.isPresent()) {
                String parentDirectory = duplicate.getParent();
                File newName = new File(parentDirectory + File.separator + result.get());
                if (result.get().isEmpty()) {
                    return folderRenameRetry(duplicate);
                } else if (newName.exists()) {
                    return folderRenameDuplicate(newName);
                } else if (containsIllegalChar(result.get())) {
                    return folderRenameInvalidChar(duplicate);
                }
                return Files.move(source, source.resolveSibling(result.get()));
            }
        } catch (IOException e) {
            failedToRename(duplicate);
        }

        return null;
    }

    /**
     * Renames file or a library folder
     *
     * @param fileToRename file to rename
     */
    public static Path fileRename(File fileToRename) {
        // Rename library
        if (fileToRename.isDirectory()) {
            String fileNameFull = fileToRename.getName();
            TextInputDialog dialog = new TextInputDialog(fileNameFull);
            dialog.setTitle("Rename Library");
            dialog.setHeaderText(fileNameFull + ":");
            dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                    "rename-library.png"))));
            dialog.setContentText("Rename the library to:");
            Optional<String> result = dialog.showAndWait();

            try {
                Path source = Paths.get(fileToRename.getAbsolutePath());
                if (result.isPresent()) {
                    String parentDirectory = fileToRename.getParent();
                    File newName = new File(parentDirectory + File.separator + result.get());
                    if (result.get().isEmpty()) {
                        return folderRenameRetry(fileToRename);
                    } else if (newName.exists()) {
                        return folderRenameDuplicate(newName);
                    } else if (containsIllegalChar(result.get())) {
                        return folderRenameInvalidChar(fileToRename);
                    }
                    return Files.move(source, source.resolveSibling(result.get()));
                }
            } catch (IOException e) {
                failedToRename(fileToRename);
            }
            // Rename media file
        } else {
            String fileNameFull = fileToRename.getName();
            int beforeExtension = fileNameFull.lastIndexOf('.');
            String fileName = fileNameFull.substring(0, beforeExtension);
            String extension = fileNameFull.substring(beforeExtension);

            TextInputDialog dialog = new TextInputDialog(fileName);
            dialog.setTitle("Rename Media File");
            dialog.setHeaderText(fileNameFull + ":");
            dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                    "rename-song.png"))));
            dialog.setContentText("Rename the file to:");

            Optional<String> result = dialog.showAndWait();

            try {
                Path source = Paths.get(fileToRename.getAbsolutePath());
                if (result.isPresent()) {
                    String parentDirectory = fileToRename.getParent();
                    File newName = new File(parentDirectory + File.separator + result.get() + extension);
                    if (result.get().isEmpty()) {
                        return fileRenameRetry(fileToRename);
                    } else if (newName.exists()) {
                        return fileRenameDuplicate(newName);
                    } else if (containsIllegalChar(result.get())) {
                        return fileRenameInvalidChar(fileToRename);
                    }
                    return Files.move(source, source.resolveSibling(result.get() + extension));
                }
            } catch (IOException e) {
                failedToRename(fileToRename);
            }
        }

        return null;
    }

    /**
     * Check for illegal character
     *
     * @param toExamine file name to examine
     */
    private static boolean containsIllegalChar(String toExamine) {
        Pattern pattern = Pattern.compile("[<>:\"/\\|?*]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }

    /**
     * Rename file after invalid character is found on previous rename attempt
     *
     * @param fileToRename file to rename
     */
    private static Path fileRenameInvalidChar(File fileToRename) {
        String fileNameFull = fileToRename.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        String fileName = fileNameFull.substring(0, beforeExtension);
        String extension = fileNameFull.substring(beforeExtension);

        TextInputDialog dialog = new TextInputDialog(fileName);
        dialog.setTitle("Rename Media File");
        dialog.setHeaderText("The song name \"" + fileNameFull + "\" cannot contain any of the following characters:\n" +
                "< > : \" / \\ | ? *");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "rename-song.png"))));
        dialog.setContentText("Rename the file to:");

        Optional<String> result = dialog.showAndWait();

        try {
            Path source = Paths.get(fileToRename.getAbsolutePath());
            if (result.isPresent()) {
                String parentDirectory = fileToRename.getParent();
                File newName = new File(parentDirectory + File.separator + result.get() + extension);
                if (result.get().isEmpty()) {
                    return fileRenameRetry(fileToRename);
                } else if (newName.exists()) {
                    return fileRenameDuplicate(newName);
                } else if (containsIllegalChar(result.get())) {
                    return fileRenameInvalidChar(fileToRename);
                }
                return Files.move(source, source.resolveSibling(result.get() + extension));
            }
        } catch (IOException e) {
            failedToRename(fileToRename);
        }

        return null;
    }

    /**
     * Rename library after invalid character is found on previous rename attempt
     *
     * @param folderToRename file to rename
     */
    private static Path folderRenameInvalidChar(File folderToRename) {
        String folderName = folderToRename.getName();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Library");
        dialog.setHeaderText("The library name \"" + folderName + "\" cannot contain any of the following characters:\n" +
                "< > : \" / \\ | ? *");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "rename-library.png"))));
        dialog.setContentText("Rename the file to:");

        Optional<String> result = dialog.showAndWait();

        try {
            Path source = Paths.get(folderToRename.getAbsolutePath());
            if (result.isPresent()) {
                String parentDirectory = folderToRename.getParent();
                File newName = new File(parentDirectory + File.separator + result.get());
                if (result.get().isEmpty()) {
                    return folderRenameRetry(folderToRename);
                } else if (newName.exists()) {
                    return folderRenameDuplicate(newName);
                } else if (containsIllegalChar(result.get())) {
                    return folderRenameInvalidChar(newName);
                } else if (containsIllegalChar(result.get())) {
                    return folderRenameInvalidChar(folderToRename);
                }
                return Files.move(source, source.resolveSibling(result.get()));
            }
        } catch (IOException e) {
            failedToRename(folderToRename);
        }

        return null;
    }

    /**
     * Renames file after previous rename attempt has blank in text box
     */
    private static Path fileRenameRetry(File fileToRename) {
        String fileNameFull = fileToRename.getName();
        int beforeExtension = fileNameFull.lastIndexOf('.');
        String fileName = fileNameFull.substring(0, beforeExtension);
        String extension = fileNameFull.substring(beforeExtension);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Media File");
        dialog.setHeaderText("Please enter at least one character \n to rename \"" + fileName + "\":");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "rename-song.png"))));
        dialog.setContentText("Rename the file to:");

        Optional<String> result = dialog.showAndWait();

        try {
            Path source = Paths.get(fileToRename.getAbsolutePath());
            if (result.isPresent()) {
                String parentDirectory = fileToRename.getParent();
                File newName = new File(parentDirectory + File.separator + result.get() + extension);
                if (result.get().isEmpty()) {
                    return fileRenameRetry(fileToRename);
                } else if (newName.exists()) {
                    return fileRenameDuplicate(newName);
                } else if (containsIllegalChar(result.get())) {
                    return fileRenameInvalidChar(fileToRename);
                }
                return Files.move(source, source.resolveSibling(result.get() + extension));
            }
        } catch (IOException e) {
            failedToRename(fileToRename);
        }

        return null;
    }

    private static Path folderRenameRetry(File folderToRename) {
        String folderName = folderToRename.getName();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Media File");
        dialog.setHeaderText("Please enter at least one character \n to rename \"" + folderName + "\":");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "rename-library.png"))));
        dialog.setContentText("Rename the file to:");

        Optional<String> result = dialog.showAndWait();

        try {
            Path source = Paths.get(folderToRename.getAbsolutePath());
            if (result.isPresent()) {
                String parentDirectory = folderToRename.getParent();
                File newName = new File(parentDirectory + File.separator + result.get());
                if (result.get().isEmpty()) {
                    return folderRenameRetry(folderToRename);
                } else if (newName.exists()) {
                    return folderRenameDuplicate(newName);
                } else if (containsIllegalChar(result.get())) {
                    return folderRenameInvalidChar(newName);
                } else if (containsIllegalChar(result.get())) {
                    return folderRenameInvalidChar(folderToRename);
                }
                return Files.move(source, source.resolveSibling(result.get()));
            }
        } catch (IOException e) {
            failedToRename(folderToRename);
        }

        return null;
    }

    /**
     * Prompt to create new playlist
     *
     * @return playlistName, otherwise null if user clicks cancel
     */
    public static String createNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Playlist");

        dialog.setHeaderText(null);
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "add-playlist.png"))));
        dialog.setContentText("New playlist:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String playlistName = result.get();
            while (playlistName != null && playlistName.isEmpty()) {
                playlistName = createNewPlaylistRetry();
            }
            return playlistName;
        }
        return null;
    }

    /**
     * Prompt to create new playlist after previous attempt has blank text box
     *
     * @return playlistName, otherwise null if user clicks cancel
     */
    private static String createNewPlaylistRetry() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Playlist");

        dialog.setHeaderText("Please enter at least one character for the playlist name:");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "add-playlist.png"))));
        dialog.setContentText("New playlist:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * Prompt to edit playlist after previous add playlist attempt has blank text box
     *
     * @param playlistToEdit to edit
     * @return newPlaylistName, or null if user clicks cancel
     */
    public static String editPlaylist(Playlist playlistToEdit) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Playlist");

        dialog.setHeaderText("Rename \"" + playlistToEdit.getM_playlistName() + "\":");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "rename-playlist.png"))));
        dialog.setContentText("Rename playlist:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newPlaylistName = result.get();
            while (newPlaylistName != null && newPlaylistName.isEmpty()) {
                newPlaylistName = editPlaylistRetry(playlistToEdit);
            }
            return newPlaylistName;
        }
        return null;
    }

    /**
     * Prompt to edit playlist
     *
     * @param playlistToEdit to edit
     * @return newPlaylistName, or null if user clicks cancel
     */
    private static String editPlaylistRetry(Playlist playlistToEdit) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Playlist");

        dialog.setHeaderText("Please enter at least one character for the playlist name:");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "rename-playlist.png"))));
        dialog.setContentText("Rename playlist \"" + playlistToEdit.getM_playlistName() + "\":");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
    
    /**
     * Prompt to remove playlist
     *
     * @param playlistToRemove to remove
     * @return true if user confirms prompt to delete playlist
     */
    public static boolean removePlaylist(Playlist playlistToRemove) {
        Dialog dialog = new Dialog();
        dialog.setTitle("Remove Playlist");
        dialog.setHeaderText("\"" + playlistToRemove.getM_playlistName() + "\":");
        dialog.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("res" + File.separator +
                "remove-playlist.png"))));
        dialog.setContentText("Are you sure you want to remove this playlist?");
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Optional result = dialog.showAndWait();

        return result.isPresent() && result.get() == okButton;
    }

    /**
     * Prompt to add a song to a  playlist with a drop down choice box
     *
     * @param playlists list of playlists
     * @return selected playlist the user chooses, null if user cancels
     */
    public static Playlist removePlaylistSelection(List<Playlist> playlists) {
        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(playlists.get(0), playlists);
        dialog.setTitle("Remove Playlist");
        dialog.setGraphic(new ImageView("res" + File.separator + "remove-playlist.png"));
        dialog.setContentText("Select a playlist:");

        Optional<Playlist> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * Prompt to add a song to a  playlist
     *
     * @param playlists list of playlists
     * @param songToAdd  song that is added to selected playlist
     * @return selected playlist the user chooses, null if user cancels
     */
    public static Playlist addSongToPlaylist(List<Playlist> playlists, Song songToAdd) {
        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(playlists.get(0), playlists);
        dialog.setTitle("Add to Playlist");
        dialog.setHeaderText("Add \"" + songToAdd.getM_title() + "\" to playlist:");
        dialog.setGraphic(new ImageView("res" + File.separator + "add-song-playlist.png"));
        dialog.setContentText("Select a playlist:");

        Optional<Playlist> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * Prompt to remove a song from a  playlist
     *
     * @param playlist song is being removed from
     * @param songName song that is added to selected playlist
     * @return true if user confirms dialog, false otherwise
     */
    public static boolean removeSongFromPlaylist(Playlist playlist, Song songName) {
        Dialog dialog = new Dialog();
        dialog.setTitle("Remove from Playlist");
        dialog.setHeaderText("Remove \"" + songName.getM_title() + "\" from " + playlist.getM_playlistName() + ":");
        dialog.setGraphic(new ImageView("res" + File.separator + "remove-song-playlist.png"));
        dialog.setContentText("Are you sure you want to remove this song from the playlist?");
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        Optional result = dialog.showAndWait();
        return result.isPresent() && result.get() == okButton;
    }

}