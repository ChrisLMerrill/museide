package org.museautomation.ui.extend.components;

import javafx.css.*;
import javafx.scene.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class InputValidation
    {
    public static boolean isShowingValid(Node control)
        {
        return !control.getPseudoClassStates().contains(InputValidation.ERROR_PSEUDO_CLASS);
        }

    public static boolean isShowingError(Node control)
        {
        return control.getPseudoClassStates().contains(InputValidation.ERROR_PSEUDO_CLASS);
        }

    public static void setValid(Node control, boolean valid)
        {
        control.pseudoClassStateChanged(InputValidation.ERROR_PSEUDO_CLASS, !valid);
        }

    private static PseudoClass ERROR_PSEUDO_CLASS = new PseudoClass()
        {
        @Override
        public String getPseudoClassName()
            {
            return "error";
            }

        @Override
        public boolean equals(Object obj)
            {
            return obj instanceof PseudoClass && getPseudoClassName().equals(((PseudoClass)obj).getPseudoClassName());
            }
        };
    }


