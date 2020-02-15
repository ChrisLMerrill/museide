package org.museautomation.ui.ide.navigation.resources;

import javafx.scene.*;
import javafx.scene.control.*;
import org.museautomation.core.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ProjectNavigatorAdditionalButtonProvider
    {
    List<Button> getButtons(MuseProject project, Node navigator_node);
    }

