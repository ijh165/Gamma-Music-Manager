package com.teamgamma.musicmanagementsystem.model;

import com.teamgamma.musicmanagementsystem.util.FileTreeUtil;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to maintain a library in a system.
 */
public class Library {
    private TreeItem<Item> m_treeRoot;

    /**
     * Constructor
     *
     * @param folderPath: root path to folder
     */
    public Library(String folderPath) {
        File rootDir = new File(folderPath);
        m_treeRoot = FileTreeUtil.generateTreeItems(rootDir, rootDir.getAbsolutePath(), null);
    }

    /**
     * Constructor
     *
     * @param folderPath: root path to folder
     * @param expandedPaths: list of expanded paths if exist
     */
    public Library(String folderPath, List<String> expandedPaths) {
        File rootDir = new File(folderPath);
        m_treeRoot = FileTreeUtil.generateTreeItems(rootDir, rootDir.getAbsolutePath(), expandedPaths);
    }

    /**
     * Get node containing item in this Library
     *
     * @param item: Song object to retrieve
     * @return Node if found, null if not found
     */
    public TreeItem<Item> getNode(Item item) {
        return FileTreeUtil.searchTreeItem(m_treeRoot, item);
    }

    /**
     * Get List of Song objects in Library
     *
     * @return List of Song objects in Library
     */
    public List<Song> getSongs() {
        return getSongs(m_treeRoot);
    }

    /**
     * Recursively fetch all songs under this node
     *
     * @return List of Song objects in Library
     */
    private List<Song> getSongs(TreeItem<Item> node) {
        List<Song> songs = new ArrayList<>();
        if(node.getValue() instanceof Song) {
            songs.add( (Song) node.getValue() );
        }

        List<TreeItem<Item>> children = node.getChildren();
        for (TreeItem<Item> child : children) {
            songs.addAll(getSongs(child));
        }

        return songs;
    }

    /**
     * Get root directory path of Library
     *
     * @return String to root directory
     */
    public String getRootDirPath() {
        return m_treeRoot.getValue().getFile().getAbsolutePath();
    }

    /**
     * Get root directory file of Library
     *
     * @return File of root directory
     */
    public File getRootDir() {
        return m_treeRoot.getValue().getFile();
    }

    /**
     * Getter for m_treeRoot
     */
    public TreeItem<Item> getM_treeRoot() {
        return m_treeRoot;
    }

    /*public Song getSong(File fileToMove) {
        return getSong(m_treeRoot, fileToMove);
    }

    private Song getSong(TreeItem<Item> node, File fileToMove) {
        if (node.getValue().getFile().getAbsolutePath().equals(fileToMove.getAbsolutePath())) {
            return (Song) node.getValue();
        }

        Song song = null;
        List<TreeItem<Item>> children = node.getChildren();
        for (TreeItem<Item> child : children) {
            song = getSong(child, fileToMove);
        }

        return song;
    }*/
}
