package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.misc.TreeViewItem;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage files in the program.
 */
public class FileManager {
    /**
     * Generate list of Song objects based on path
     *
     * @param pathToDirectory: the directory path
     * @return ArrayList of Song objects
     */
    public static List<Song> generateSongs(String pathToDirectory) {
        List<Song> listOfSongs = new ArrayList<>();

        // Get all music files in the path (including subdirectories)
        File path = new File(pathToDirectory);
        List<File> musicFiles = getMusicFiles(path);

        // Create Song object and add to array list
        for (File musicFile : musicFiles) {
            listOfSongs.add(new Song(musicFile.getAbsolutePath()));
        }

        return listOfSongs;
    }

    /**
     * Helper function to find music files in a directory
     *
     * @param path: the directory path
     * @return ArrayList of File objects
     */
    private static List<File> getMusicFiles(File path) {
        List<File> musicFiles = new ArrayList<>();
        File[] files = path.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String[] extensions = new String[]{".mp3"};
                    if (isAccept(file, extensions)) {
                        musicFiles.add(file);
                    }
                } else {
                    musicFiles.addAll(getMusicFiles(file));
                }
            }
        }

        return musicFiles;
    }

    /**
     * Move file to the destDir
     *
     * @param fileToMove: File to be moved
     * @param destDir:    File object with path to destination directory
     * @return true if file is moved successfully
     * @throws IOException
     */
    public static boolean moveFile(File fileToMove, File destDir) throws IOException {
        if (fileToMove.getParent().equals(destDir.getAbsolutePath())) {
            throw new IOException("Source and destination folders are the same!");
        }

        Path sourceFilePath = fileToMove.toPath();
        Path destDirPath = destDir.toPath();
        Path destFilePath = destDirPath.resolve(sourceFilePath.getFileName());
        Path resultPath = Files.move(fileToMove.toPath(), destFilePath);
        return (resultPath.getParent().equals(destDir.toPath()));
    }

    /**
     * Remove file from file system
     *
     * @param fileToRemove: file to be removed
     * @return true if file is removed successfully
     * @throws Exception
     */
    public static boolean removeFile(File fileToRemove) throws Exception {
        return deleteFolderOrFile(fileToRemove);
    }

    private static boolean deleteFolderOrFile(File path) {
        if (path.exists()) {
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            deleteFolderOrFile(f);
                        } else {
                            if (!f.delete()) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return (path.delete());
    }

    /**
     * Copy fileToCopy to destDir
     *
     * @param fileToCopy: File to be copied (one file only)
     * @param destDir:    File object with path to destination directory
     * @return true if path of new file destination equals destDir path, false otherwise
     * @throws IOException
     * @throws InvalidPathException
     */
    public static boolean copyFile(File fileToCopy, File destDir) throws IOException, InvalidPathException {
        Path sourceFilePath = fileToCopy.toPath();
        Path destDirPath = destDir.toPath();
        Path destFilePath = destDirPath.resolve(sourceFilePath.getFileName());
        Path resultPath = Files.copy(fileToCopy.toPath(), destFilePath);
        return (resultPath.getParent().equals(destDir.toPath()));
    }

    /**
     * Copy src to dest recursively
     *
     * @param src:  File object with path to file or directory to be copied
     * @param dest: File object with path to destination directory
     * @return true if path of new file destination equals destinationDir path, false otherwise
     * @throws IOException
     * @throws InvalidPathException
     */
    public static boolean copyFilesRecursively(File src, File dest) throws IOException, InvalidPathException {
        assert dest.isDirectory();
        if (src.isDirectory() && src.equals(dest)) {
            throw new IOException("Cannot copy a directory to itself!");
        } else if (src.isDirectory() && dest.getAbsolutePath().contains(src.getAbsolutePath())) {
            throw new IOException("Cannot copy a directory into its subfolder!");
        } else if (dest.getAbsolutePath().equals(src.getParent())){
            throw new IOException("Source and destination folders are the same!");
        }
        if (!copyFile(src, dest)) { //one of the files failed to be copied
            return false;
        }
        File[] children = src.listFiles();
        if (children != null) {
            for (File child : children) {
                File nextDest = new File(dest.toPath() + File.separator + src.getName());
                copyFilesRecursively(child, nextDest);
            }
        }
        return true;
    }

    /**
     * File filter for finding music files
     *
     * @param file
     * @param extensions
     * @return true if file is accepted, false otherwise
     */
    private static boolean isAccept(File file, String[] extensions) {
        for (String extension : extensions) {
            if (file.getName().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
