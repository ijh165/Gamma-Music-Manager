package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.misc.Actions;
import com.teamgamma.musicmanagementsystem.misc.ContextMenuConstants;
import com.teamgamma.musicmanagementsystem.model.*;
import com.teamgamma.musicmanagementsystem.musicplayer.MusicPlayerManager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.MenuItem;


import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

/**
 * UI class for list of songs in center of application
 */
public class ContentListUI extends StackPane {
    private SongManager m_model;
    private MusicPlayerManager m_musicPlayerManager;
    private DatabaseManager m_databaseManager;
    private TableView<Song> m_table;
    private ContextMenu m_contextMenu;

    public ContentListUI(SongManager model, MusicPlayerManager musicPlayerManager, DatabaseManager databaseManager) {
        super();
        m_model = model;
        m_musicPlayerManager = musicPlayerManager;
        m_databaseManager = databaseManager;
        m_table = new TableView<>();
        m_contextMenu = new ContextMenu();
        updateTable();
        setCssStyle();
        registerAsCenterFolderObserver();
    }

    /**
     * Register as a observer to changes for the folder selected to be displayed here
     */
    private void registerAsCenterFolderObserver() {
        m_model.addSongManagerObserver(new SongManagerObserver() {
            @Override
            public void librariesChanged() {
                clearTable();
                updateTable();
            }

            @Override
            public void centerFolderChanged() {
                clearTable();
                updateTable();
            }

            @Override
            public void rightFolderChanged() {
                /* Do nothing */
            }

            @Override
            public void songChanged() {
                /* Do nothing */
            }

            @Override
            public void fileChanged() {
                clearTable();
                updateTable();
            }

            @Override
            public void leftPanelOptionsChanged() {
                /* Do nothing */
            }
        });
    }

    private void setEmptyText(TableColumn fileNameCol, TableColumn titleCol, TableColumn artistCol, TableColumn albumCol, TableColumn genreCol, TableColumn ratingCol) {
        m_table.getColumns().addAll(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
        m_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        m_table.setPlaceholder(new Label("Choose a folder to view its contents"));
        this.getChildren().add(m_table);

    }

    private void clearTable() {
        System.out.println("Clearing list...");
        m_table.getItems().clear();
        this.getChildren().clear();
    }

    private void updateTable() {
        m_table = new TableView<>();
        TableColumn<Song, File> fileNameCol = new TableColumn<>("File Name");
        fileNameCol.setMinWidth(80);
        TableColumn<Song, String> titleCol = new TableColumn<>("Title");
        titleCol.setMinWidth(60);
        TableColumn<Song, String> artistCol = new TableColumn<>("Artist");
        artistCol.setMinWidth(60);
        TableColumn<Song, String> albumCol = new TableColumn<>("Album");
        albumCol.setMinWidth(60);
        TableColumn<Song, String> genreCol = new TableColumn<>("Genre");
        genreCol.setMinWidth(60);
        TableColumn<Song, Integer> ratingCol = new TableColumn<>("Rating");
        ratingCol.setMinWidth(20);

        if (m_model.getM_selectedCenterFolder() == null) {
            setEmptyText(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
        } else {
            System.out.println("Updating m_table...");
            m_table = new TableView<>();
            m_table.setEditable(true);

            setTableRowMouseEvents();
            setTableColumnAttributes(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);

            List<Song> songs = m_model.getCenterPanelSongs();
            if (songs.isEmpty()){
                m_table.setPlaceholder(new Label("No songs in folder"));
            } else {
                m_table.setItems(FXCollections.observableArrayList(songs));
                showOrHideTableColumns(fileNameCol, titleCol, artistCol, albumCol, genreCol, ratingCol);
            }
            this.getChildren().add(m_table);

            // Scrolls through list
            ScrollPane scrollpane = new ScrollPane();
            scrollpane.setFitToWidth(true);
            scrollpane.setFitToHeight(true);
            scrollpane.setPrefSize(500, 500);
            scrollpane.setContent(m_table);
        }
    }

    private void showOrHideTableColumns(TableColumn<Song, File> fileNameCol, TableColumn<Song, String> titleCol, TableColumn<Song, String> artistCol, TableColumn<Song, String> albumCol, TableColumn<Song, String> genreCol, TableColumn<Song, Integer> ratingCol) {
        m_table.setTableMenuButtonVisible(true);
        // fileName and artist default columns for centerListUI
        fileNameCol.setVisible(true);
        artistCol.setVisible(true);

        titleCol.setVisible(false);
        albumCol.setVisible(false);
        genreCol.setVisible(false);
        ratingCol.setVisible(false);
    }

    private void setTableColumnAttributes(TableColumn<Song, File> fileNameCol,
                                          TableColumn<Song, String> titleCol,
                                          TableColumn<Song, String> artistCol,
                                          TableColumn<Song, String> albumCol,
                                          TableColumn<Song, String> genreCol,
                                          TableColumn<Song, Integer> ratingCol) {



        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("m_fileName"));

        titleCol.setCellValueFactory(new PropertyValueFactory<>("m_title"));

        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        titleCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Song, String> t) {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setTitle(t.getNewValue());
            }
        });

        artistCol.setCellValueFactory(new PropertyValueFactory<>("m_artist"));
        artistCol.setCellFactory(TextFieldTableCell.forTableColumn());
        artistCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setArtist(t.getNewValue());
                    }
                }
        );

        albumCol.setCellValueFactory(new PropertyValueFactory<>("m_album"));
        albumCol.setCellFactory(TextFieldTableCell.forTableColumn());
        albumCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setAlbum(t.getNewValue());
                    }
                }
        );

        genreCol.setCellValueFactory(new PropertyValueFactory<>("m_genre"));
        genreCol.setCellFactory(TextFieldTableCell.forTableColumn());
        genreCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, String> t) {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setGenre(t.getNewValue());
                    }
                }
        );

        ratingCol.setCellValueFactory(new PropertyValueFactory<>("m_rating"));
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        ratingCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Song, Integer>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Song, Integer> t) {
                        try {
                            t.getTableView().getItems().get(t.getTablePosition().getRow()).setRating(t.getNewValue());
                        } catch (IllegalArgumentException ex) {
                            PromptUI.customPromptError("Error", "", "Rating should be in range 0 to 5");
                            m_model.notifyCenterFolderObservers();
                        }
                    }
                }
        );

        m_table.getColumns().add(fileNameCol);
        m_table.getColumns().add(titleCol);
        m_table.getColumns().add(artistCol);
        m_table.getColumns().add(albumCol);
        m_table.getColumns().add(genreCol);
        m_table.getColumns().add(ratingCol);
        m_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setTableRowMouseEvents() {
        m_table.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
            @Override
            public TableRow<Song> call(TableView<Song> param) {
                TableRow<Song> row = new TableRow<>();

                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Song selectedSong = row.getItem();
                        if (selectedSong != null && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            m_musicPlayerManager.playSongRightNow(selectedSong);
                        } else if (event.getButton() == MouseButton.PRIMARY) {
                            m_contextMenu.hide();
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            m_contextMenu.hide();
                            m_contextMenu = generateContextMenu(row.getItem());
                            m_contextMenu.show(m_table, event.getScreenX(), event.getScreenY());
                        }
                    }
                });

                row.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Song selectedItem = row.getItem();

                        System.out.println("Drag detected on " + selectedItem.getM_file());

                        //update model
                        m_model.setM_fileToMove(selectedItem.getM_file());

                        //update drag board
                        Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
                        dragBoard.setDragView(row.snapshot(null, null));
                        ClipboardContent content = new ClipboardContent();
                        content.put(DataFormat.PLAIN_TEXT, selectedItem.getM_file().getAbsolutePath());
                        dragBoard.setContent(content);

                        mouseEvent.consume();
                    }
                });

                row.setOnDragOver(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        System.out.println("Drag over on center");
                        dragEvent.acceptTransferModes(TransferMode.MOVE);
                        dragEvent.consume();
                    }
                });

                row.setOnDragDropped(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        System.out.println("Drag dropped on center");

                        //move to the selected center folder
                        File fileToMove = m_model.getM_fileToMove();
                        File destination = m_model.getM_selectedCenterFolder();
                        try {
                            m_model.moveFile(fileToMove, destination);
                        } catch (FileAlreadyExistsException ex) {
                            PromptUI.customPromptError("Error", null, "The following file or folder already exist!\n" + ex.getMessage());
                        } catch (AccessDeniedException ex) {
                            PromptUI.customPromptError("Error", null, "AccessDeniedException: \n" + ex.getMessage());
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            PromptUI.customPromptError("Error", null, "IOException: \n" + ex.getMessage());
                            ex.printStackTrace();
                        }

                        m_model.setM_fileAction(Actions.ADD);
                        m_model.notifyFileObservers();
                        dragEvent.consume();
                    }
                });

                row.setOnDragDone(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent dragEvent) {
                        System.out.println("Drag done");
                        m_model.setM_fileToMove(null);
                        dragEvent.consume();
                    }
                });

                return row;
            }
        });
    }

    private ContextMenu generateContextMenu(Song selectedSong) {
        ContextMenu contextMenu = new ContextMenu();

        //copy option
        MenuItem copy = new MenuItem(ContextMenuConstants.COPY);
        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (selectedSong != null) {
                    m_model.setM_fileToCopy(selectedSong.getM_file());
                }
            }
        });

        //paste option
        MenuItem paste = new MenuItem(ContextMenuConstants.PASTE);
        paste.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (selectedSong != null) {
                    File dest = m_model.getM_selectedCenterFolder();
                    if (!dest.isDirectory()) {
                        PromptUI.customPromptError("Not a directory!", "", "Please select a directory as the paste target.");
                        return;
                    }
                    try {
                        m_model.copyToDestination(dest);
                        m_model.setM_fileAction(Actions.PASTE);
                        m_model.notifyFileObservers();
                    } catch (FileAlreadyExistsException ex) {
                        PromptUI.customPromptError("Error", "", "The following file or folder already exist!\n" + ex.getMessage());
                    } catch (IOException ex) {
                        PromptUI.customPromptError("Error", "", "IOException: " + ex.getMessage());
                    } catch (Exception ex) {
                        PromptUI.customPromptError("Error", "", "Exception: " + ex.getMessage());
                    }
                }
            }
        });

        //delete option
        MenuItem delete = new MenuItem(ContextMenuConstants.DELETE);
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (selectedSong != null) {
                    File fileToDelete = selectedSong.getM_file();
                    //confirmation dialog
                    if (!PromptUI.customPromptConfirmation(
                            "Deleting " + (fileToDelete.isDirectory() ? "folder" : "file"),
                            null,
                            "Are you sure you want to permanently delete \"" + fileToDelete.getName() + "\"?")) {
                        return;
                    }
                    //try to actually delete (retry if FileSystemException happens)
                    for(int i=0; i<2; i++) {
                        try {
                            m_model.deleteFile(fileToDelete);
                            break;
                        } catch (FileSystemException ex) {
                            m_musicPlayerManager.stopSong();
                            if (i==1) { //if this exception still thrown after retry (for debugging)
                                ex.printStackTrace();
                            }
                        } catch (Exception ex) {
                            PromptUI.customPromptError("Error", null, "Exception: \n" + ex.getMessage());
                            ex.printStackTrace();
                            break;
                        }
                    }
                }
            }
        });

        //edit properties option
        MenuItem editProperties = new MenuItem(ContextMenuConstants.EDIT_PROPERTIES);
        editProperties.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PromptUI.editMetadata(selectedSong);
                m_model.notifyCenterFolderObservers();
            }
        });

        //add to playlist option
        MenuItem addToPlaylist = new MenuItem(ContextMenuConstants.ADD_TO_PLAYLIST);
        addToPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Adding to playlist...");
                System.out.println(selectedSong.getM_fileName());
                // TODO: remove this once we can create Playlists
                //m_model.notifyPlaylistSongsObservers();

                // TODO: Uncomment this section when we have create Playlist working
                // TODO: verify if it works (this is just a rough version)
                // TODO: get playlist name from user input (prompt)
                /*String playlistName = "playlist";
                boolean songAddedSuccess = m_model.addToPlaylist(selectedSong, playlistName);

                if (!songAddedSuccess){
                    // TODO: show prompt with error
                }*/
            }
        });

        //create playlist option
        MenuItem createPlaylist = new MenuItem(ContextMenuConstants.CREATE_NEW_PLAYLIST);
        createPlaylist.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Playlist playlist = new Playlist("Playlist");
                List<Playlist> playlistStorage = new ArrayList<>();
                playlistStorage.add(playlist);
                System.out.println("Created New Playlist");
                m_model.notifyPlaylistSongsObservers();

            }
        });

        contextMenu.getItems().addAll(copy, paste, delete, editProperties, addToPlaylist, createPlaylist);
        contextMenu.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // Disable paste if nothing is chosen to be copied
                if (m_model.getM_fileToCopy() == null) {
                    paste.setDisable(true);
                    paste.setStyle("-fx-text-fill: gray;");
                } else {
                    paste.setDisable(false);
                    paste.setStyle("-fx-text-fill: black;");
                }

                // Disable copy, edit, delete, addToPlaylist if no song is selected in table
                if (selectedSong == null) {
                    copy.setDisable(true);
                    copy.setStyle("-fx-text-fill: gray;");
                    delete.setDisable(true);
                    delete.setStyle("-fx-text-fill: gray;");
                    editProperties.setDisable(true);
                    editProperties.setStyle("-fx-text-fill: gray;");
                    addToPlaylist.setDisable(true);
                    addToPlaylist.setStyle("-fx-text-fill: gray;");
                    createPlaylist.setDisable(true);
                    createPlaylist.setStyle("-fx-text-fill: gray;");
                }
            }
        });

        return contextMenu;
    }

    private void setCssStyle() {
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
