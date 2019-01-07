package org.musetest.ui.extensions;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.musetest.extensions.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExtensionInfoListPanel
    {
    public ExtensionInfoListPanel()
        {
        _box = new VBox(5);
        _box.setFillWidth(true);

        _scroller = new ScrollPane(_box);
        _scroller.setFitToWidth(true);
        }

    public Node getNode()
        {
        return _scroller;
        }

    public void customizePanels(PanelCustomizer customizer)
        {
        _customizer = customizer;
        }

    public void setInfo(List<ExtensionInfo> list)
        {
        Platform.runLater(() ->
            {
            _box.getChildren().clear();
            int index = 0;
            for (ExtensionInfo info : list)
                {
                ExtensionInfoPanel subpanel = new ExtensionInfoPanel();
                Node subpanel_node = subpanel.getNode();
                subpanel_node.setId(getSubpanelId(index++));
                _box.getChildren().add(subpanel_node);
                VBox.setMargin(subpanel_node, new Insets(5));
                subpanel.setInfo(info);

                if (_customizer != null)
                    _customizer.customizePanel(subpanel, info);
                }
            });
        }

    public static String getSubpanelId(int index)
        {
        return "omue-eilp" + index;
        }

    private ScrollPane _scroller;
    private VBox _box;
    private PanelCustomizer _customizer;

    public interface PanelCustomizer
        {
        void customizePanel(ExtensionInfoPanel panel, ExtensionInfo info);
        }
    }


