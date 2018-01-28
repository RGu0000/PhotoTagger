package com.RGu0000;

//http://code.makery.ch/blog/javafx-dialogs-official/

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;

import java.util.*;

public class DialogWindows {

    public static void error(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("No tags found!");
        alert.setContentText("Seems like you haven't tagged any image");

        alert.showAndWait();
    }

    public static void aboutDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setStyle(" -fx-max-width:1000px; -fx-max-height: 500px; -fx-pref-width: 800px; -fx-pref-height: 200px;");
        alert.setTitle("About dialog");
        alert.setHeaderText(null);
        alert.setContentText("One of my first apps. Free of charge for everyone :) \n Should you find any bug, please do not hesitate to get in touch with me!\n\n"+
                "This app binds tags to the images found in the selected directory (tags can be the names of your friends / numbers of race competitors) \n" +
                "and enables you to create separate directories for each tag and copy all the binded images there");
        alert.showAndWait();
    }

    public static void helpDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setStyle(" -fx-max-width:1000px; -fx-max-height: 500px; -fx-pref-width: 600px; -fx-pref-height: 300px;");
        alert.setTitle("Help dialog");
        alert.setHeaderText(null);
        alert.setContentText("Type the tag into text area and press Space or Enter to confirm.\n"+
                "You made a mistake? Simply use \" - \" sign and tag to delete it! (-11 will delete tag 11)\n"+
                "Press \"Remove all tags\" to start over.\n"+
                "Use Left and Right arrows to jump between photos. \n"+
                "Have you ran into low quality image? Press \"Delete button\" to get rid of it!\n\n"+
                "Menu->Save (or Ctrl+S) saves your current progress using hidden .txt file in target directory\n"+
                "       that will load automatically and add tags next time you turn on the App.\n"+
                "Menu->Export 1 tag enables you to choose just one tag to be exported, while\n"+
                "Menu->Export all will perform the copy action for all tags binded with images");
        alert.showAndWait();
    }

    public static String chooseTagDialog(List<String> tags) {
        List<String> choices = new ArrayList<>(tags);


        ChoiceDialog<String> dialog = new ChoiceDialog<>(tags.get(0), choices);
        dialog.setTitle("Choose the tag to be exported");
        dialog.setHeaderText("Which tag should be exported?");
        dialog.setContentText("Choose your tag:");


        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            return result.get();
        }
        return "";
    }
}
