package org.musetest.ui.extensions;

import javafx.scene.*;
import javafx.scene.control.*;
import org.musetest.extensions.install.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExtensionInstallLogDisplayPanel
    {
    public ExtensionInstallLogDisplayPanel(ExtensionInstallLog log)
        {
        _text.setId(MESSAGES_TEXT_ID);
        log.addMessageListener(message ->
            {
            if (_text.getText().length() > 0)
                _text.appendText("\n");
            _text.appendText(message.getText());
            _text.positionCaret(_text.getText().length() - message.getText().length()); // prevent from scrolling to end of text
            });
        }

    public Node getNode()
        {
        return _text;
        }

    TextArea _text = new TextArea();

    public final static String MESSAGES_TEXT_ID = "omue-eildp";
    }


