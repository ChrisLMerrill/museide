package org.museautomation.ui.extend.edit.stack;

import javafx.scene.*;
import org.museautomation.ui.extend.edit.*;

/**
 * Implement this interface to use the editor in a EditorStack.
 *
 * note: Must override toString() to provide editor for title.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface StackableEditor extends Validatable
    {
    /**
     * Return the UI for this editor.
     */
    Node getNode();

    /**
     * Provides access to the stack. Use this for pushing new sub-editors.
     */
    void setStack(EditorStack stack);

    /**
     * Will be called when the EditorStack returns the focus to this editor.
     */
    void requestFocus();

    /**
     * Will be called with this editor is displayed. This is when data should be moved from the underlying
     * model into the UI elements.
     */
    void activate();

    /**
     * @return True if the state of the data is valid for saving.
     */
    boolean isValid();
    }


