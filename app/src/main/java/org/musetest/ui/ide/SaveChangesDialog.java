package org.musetest.ui.ide;

import javafx.scene.control.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class SaveChangesDialog
    {
    public static boolean createShowAndWait(SaveChangesHandler saver, DiscardChangesHandler abandoner)
        {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Save changes?");
        alert.setContentText("There are unsaved changes to one or more resources.\nWould you like to save the changes or discard them?");

        ButtonType save = new ButtonType("Save");
        ButtonType revert = new ButtonType("Discard");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(save, revert, cancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == save)
            saver.onSaveChanges();
        else if (result.isPresent() && result.get() == revert)
            abandoner.onDiscardChanges();
        else
            return false;

        return true;
        }

    public interface SaveChangesHandler
        {
        void onSaveChanges();
        }
    public interface DiscardChangesHandler
        {
        void onDiscardChanges();
        }
    }


