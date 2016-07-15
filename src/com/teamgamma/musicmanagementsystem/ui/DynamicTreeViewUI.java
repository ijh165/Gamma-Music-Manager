package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.Actions;
import com.teamgamma.musicmanagementsystem.misc.TreeViewUtil;
import com.teamgamma.musicmanagementsystem.model.DatabaseManager;
import com.teamgamma.musicmanagementsystem.model.Library;
import com.teamgamma.musicmanagementsystem.model.SongManager;
import com.teamgamma.musicmanagementsystem.model.SongManagerObserver;
import com.teamgamma.musicmanagementsystem.misc.CustomTreeCell;
import com.teamgamma.musicmanagementsystem.misc.TreeViewItem;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DynamicTreeViewUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private TreeView<TreeViewItem> m_tree;

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
     * @param dynamicTreeViewExpandedPaths
     */
    private void updateTreeView(List<String> dynamicTreeViewExpandedPaths) {
        System.out.println("updating treeview...");

        if (m_model.getM_rightFolderSelected() == null) {
            this.getChildren().add(new Label("Choose a folder to view"));
        } else {
            m_tree = createTrees(dynamicTreeViewExpandedPaths);
            this.getChildren().add(m_tree);
            setTreeCellFactory();
        }
    }

    /**
     *  Set a tree cell factory for the file tree
     */
    private void setTreeCellFactory() {
        System.out.println("setting cell factory...");
        m_tree.setCellFactory(new Callback<TreeView<TreeViewItem>, TreeCell<TreeViewItem>>() {
            @Override
            public TreeCell<TreeViewItem> call(TreeView<TreeViewItem> arg) {
                // custom m_tree cell that defines a context menu for the root m_tree item
                return new CustomTreeCell(m_model, m_musicPlayerManager, m_databaseManager, m_tree, false);
            }
        });
    }

    /**
     * Register as a Song Manager Observer and define actions upon receiving notifications
     */
    private void registerAsObserver() {
        m_model.addSongManagerObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                clearTreeView();
                updateTreeView(null);
            }

            @Override
            public void centerFolderChanged() {

            }

            @Override
            public void rightFolderChanged() {
                System.out.println("File changed in treeview");
                clearTreeView();
                updateTreeView(null);
            }

            @Override
            public void songChanged() {

            }

            @Override
            public void fileChanged(Actions action, File file) {
                System.out.println("File changed in treeview");
                updateFiles(action, file);
            }

            @Override
            public void leftPanelOptionsChanged() {
                /* Do nothing */
            }
        });
    }

    /**
     * Update the files to show in this tree
     * @param fileAction
     * @param file
     */
    private void updateFiles(Actions fileAction, File file) {
        try {
            if (fileAction != null && fileAction != Actions.NONE) {
                if (m_model.getM_rightFolderSelected() == null) {
                    this.getChildren().add(new Label("Choose a folder to view"));
                } else {
                    TreeViewUtil.updateTreeItems(fileAction, file, m_tree, m_model);
                    m_model.setM_rightPanelFileAction(Actions.NONE);
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
    private TreeView<TreeViewItem> createTrees(List<String> dynamicTreeViewExpandedPaths) {
        File dummyRootFile = new File(System.getProperty("user.dir"));
        TreeItem<TreeViewItem> root = new TreeItem<>(new TreeViewItem(dummyRootFile, true));

        TreeItem<TreeViewItem> rootItem = TreeViewUtil.generateTreeItems(
                m_model.getM_rightFolderSelected(), m_model.getM_rightFolderSelected().getAbsolutePath(), false, dynamicTreeViewExpandedPaths
        );

        rootItem.setExpanded(true);
        if (rootItem.getValue() != null) {
            System.out.println("Added new root path:" + rootItem.toString());
        }
        root.getChildren().add(rootItem);

        TreeView<TreeViewItem> tree = new TreeView<>(root);
        tree.setShowRoot(false);
        return tree;
    }

    private void setPaneStyle() {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        UserInterfaceUtils.applyBlackBoarder(this);
    }

    /**
     * Get list of file paths that are expanded in this tree
     * @return
     */
    public List<String> getExpandedPaths() {
        if (m_tree != null) {
            return TreeViewUtil.getExpandedPaths(m_tree);
        } else {
            return null;
        }
    }
}
