package com.RGu0000;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Controller  {

    private Model model=new Model();

    @FXML private Button directoryBut;
    @FXML private TextField tagInput;
    @FXML private Label labelTags;
    @FXML private ListView<String> listView;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Hyperlink hyperlink;
    @FXML private ImageView imageDisplayed;
    @FXML private Button deleteButton;
    @FXML private Button removeButton;
    @FXML private MenuItem exportTag;
    @FXML private MenuItem exportAll;


    private String selectedImage;


    private TreeSet<String> tags;

    @FXML
    private void initialize() {
        tagInput.setDisable(true);
        nextButton.setDisable(true);
        previousButton.setDisable(true);
        deleteButton.setDisable(true);
        removeButton.setDisable(true);
        exportTag.setDisable(true);
        exportAll.setDisable(true);




        hyperlink.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue.equals(newValue) && !hyperlink.getText().equals("")){
                refreshImageList();
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if(listView.getSelectionModel().getSelectedIndex()!=-1) {
                        selectedImage = listView.getSelectionModel().getSelectedItem();
                        imageDisplayed.setImage(new Image(model.getImageToDisplay(selectedImage)));
                        tags = new TreeSet<>(model.getTags(selectedImage));
                        labelTags.setText(tags
                                .stream()
                                .collect(Collectors.joining(", ")));

                        tagInput.clear();
                        tagInput.requestFocus();
                    }
                }
        );
    }



    public void keyListener(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
            String input = tagInput.getText().trim().toLowerCase();
            if(!input.equals("") && !input.equals(" ")){
                if(input.startsWith("-")){
                    tags.remove(input.substring(1));
                } else {
                    tags.add(input);
                }
                labelTags.setText(tags
                        .stream()
                        .collect(Collectors.joining(", ")));

            } tagInput.clear();
        }
        if(event.getCode() == KeyCode.LEFT ) {
            this.previousButton.fire();
        }
        if(event.getCode() == KeyCode.RIGHT ) {
            this.nextButton.fire();
        }

    }

    public void deleteTagsButtonPressed(ActionEvent ae){
        tags.clear();
        model.deleteTags(listView.getSelectionModel().getSelectedItem());
        labelTags.setText("");
        tagInput.requestFocus();
    }

    public void deleteImage(ActionEvent ae){

        model.deleteImage(listView.getSelectionModel().getSelectedItem());
        model.refresh();
        refreshImageList();
        listView.refresh();
        tagInput.requestFocus();
    }


    public void nextButtonPressed(ActionEvent ae){
        model.setTags(listView.getSelectionModel().getSelectedItem(),tags);
        listView.getSelectionModel().selectNext();
    }

    public void previousButtonPressed(ActionEvent ae){
        model.setTags(listView.getSelectionModel().getSelectedItem(),tags);
        listView.getSelectionModel().selectPrevious();
    }

    public void hyperlinkEntered(ActionEvent ae){
        try {
            Desktop.getDesktop().open(new File(model.getDirectory()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        hyperlink.setVisited(false);
        tagInput.requestFocus();
    }

    public void chooseListView(MouseEvent ae){
        tagInput.requestFocus();
    }

    public void refreshImageList(){
        //model.refresh();
        if(!model.getListView().isEmpty()) {
            ObservableList<String> names = FXCollections.observableArrayList(model.getListView());
            listView.setItems(names.sorted());
            listView.getSelectionModel().selectFirst();
            tagInput.setDisable(false);
            tagInput.requestFocus();
        } else {
            listView.setItems(null);
            labelTags.setText("");
            tagInput.setDisable(true);
            imageDisplayed.setImage(new Image(model.defaultImage()));
        }
    }

    public void directoryButton(ActionEvent ae){
        directoryBut.setDisable(true);
        model.chooseDirectory();

        hyperlink.setText(model.getDirectory());
        directoryBut.setDisable(false);
        if(!hyperlink.getText().equals("")){
            hyperlink.setDisable(false);

            nextButton.setDisable(false);
            previousButton.setDisable(false);
            deleteButton.setDisable(false);
            removeButton.setDisable(false);
            exportTag.setDisable(false);
            exportAll.setDisable(false);

        }
        tagInput.requestFocus();
    }

    public void save(){
        if(listView.getSelectionModel().getSelectedIndex()!=-1){
            model.setTags(listView.getSelectionModel().getSelectedItem(),tags);
        }
        tagInput.clear();
        model.save();
    }

    public void exit(){
        System.exit(0);
    }
    public void about(){ DialogWindows.aboutDialog();}
    public void help(){ DialogWindows.helpDialog();}

    public void exportAll(){
        save();
        model.export("");
        tagInput.requestFocus();
    }

    public void exportTag(){
        save();
        model.callDialog();
        tagInput.requestFocus();
    }


}
