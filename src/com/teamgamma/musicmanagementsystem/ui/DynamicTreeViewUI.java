package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.util.*;
import com.teamgamma.musicmanagementsystem.util.FileTreeUtils;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DynamicTreeViewUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private TreeView<Item> m_tree;

    public DynamicTreeViewUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager databaseManager,
                             List<String> dynamicTreeViewExpandedPaths) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        setPaneStyle();
        registerAsObserver();

        updateTreeView(dynamicTreeViewExpandedPaths);
    }

    /**
     * Update the tree view and load previously expanded paths if applicable
     * @param dynamicTreeViewExpandedPaths list of expanded paths
     */
    private void updateTreeView(List<String> dynamicTreeViewExpandedPaths) {
        System.out.println("updating treeview...");

        if (m_model.getM_rightFolderSelected() == null) {
            this.getChildren().add(new Label("Choose a folder to view"));
        } else {
            m_tree = createTrees(m_model.getM_libraries(), dynamicTreeViewExpandedPaths);
            this.getChildren().add(m_tree);
            setTreeCellFactory();
        }
    }

    /**
     *  Set a tree cell factory for the file tree
     */
    private void setTreeCellFactory() {
        System.out.println("setting cell factory...");
        m_tree.setCellFactory(arg -> new CustomTreeCell(m_model, m_musicPlayerManager, m_databaseManager, m_tree, false));
    }

    /**
     * Register as a Song Manager Observer and define actions upon receiving notifications
     */
    private void registerAsObserver() {
        m_model.addLibraryObserver((FileActions fileActions) -> {
            clearTreeView();
            updateTreeView(null);
        });
        m_model.addRightFolderObserver((FileActions fileActions) -> {
            System.out.println("Right folder changed in treeview");
            clearTreeView();
            updateTreeView(null);
        });
        m_model.addFileObserver((FileActions fileActions) -> {
            System.out.println("File changed in treeview");
            updateFiles(fileActions);
        });
    }

    /**
     * Update the files to show in this tree
     * @param fileActions the file actions
     */
    private void updateFiles(FileActions fileActions) {
        try {
            for (Pair<Action, File> fileAction: fileActions) {
                Action action = fileAction.getKey();
                if (fileAction != null && action != Action.NONE) {
                    if (m_model.getM_rightFolderSelected() == null) {
                        this.getChildren().add(new Label("Choose a folder to view"));
                    } else {
                        FileTreeUtils.updateTreeItems(m_model, m_tree, action, fileAction.getValue());
                        m_model.setM_rightPanelFileAction(Action.NONE);
                    }
                }
            }
        } catch (IOException ex) {
            PromptUI.customPromptError("Error", null, "IOException: \n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Clear the tree
     */
    private void clearTreeView() {
        //m_tree.setRoot(null);
        System.out.println("clearing treeview...");
        this.getChildren().clear();
    }

    /**
     * Construct the m_tree view and show previously expanded folders if applicable
     *
     * @return TreeView<String>
     */
    private TreeView<Item> createTrees(List<Library> libraries, List<String> dynamicTreeViewExpandedPaths) {
        if (!libraries.isEmpty()) {
            File dummyRootFile = new File(libraries.get(0).getRootDirPath());
            TreeItem<Item> root = new TreeItem<>(new Folder(dummyRootFile, true));

            TreeItem<Item> rootItem = FileTreeUtils.copyTree(m_model.search(m_model.getM_rightFolderSelected()));
            rootItem.setExpanded(true);

            FileTreeUtils.setTreeExpandedState(rootItem, dynamicTreeViewExpandedPaths);

            if (rootItem.getValue() != null) {
                System.out.println("Added new root path:" + rootItem.toString());
            }

            root.getChildren().add(rootItem);

            TreeView<Item> tree = new TreeView<>(root);
            tree.setShowRoot(false);

            return tree;
        }
        return null;
    }

    private void setPaneStyle() {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        UserInterfaceUtils.applyBlackBoarder(this);
    }

    /**
     * Get list of file paths that are expanded in this tree
     *
     * @return list of expanded paths
     */
    public List<String> getExpandedPaths() {
        if (m_tree != null) {
            return FileTreeUtils.getExpandedPaths(m_tree);
        } else {
            return null;
        }
    }
}
