package com.teamgamma.musicmanagementsystem.ui;

import com.teamgamma.musicmanagementsystem.SongManager;
import javafx.scene.layout.StackPane;

public class BaseUI extends StackPane{
    SongManager model;

    public BaseUI(SongManager model){
        this.model = model;

        setCssStyle();
    }

    /*private void registerAsObserver() {

    }*/

    private void setCssStyle(){
        final String cssDefault = "-fx-border-color: black;\n";
        this.setStyle(cssDefault);
    }
}
