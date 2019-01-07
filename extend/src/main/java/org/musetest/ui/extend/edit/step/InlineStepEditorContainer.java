package org.musetest.ui.extend.edit.step;

/**
 * Responsible for choosing and switching between the quick and full editors within the containing node.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface InlineStepEditorContainer
    {
    void moreEditOptionsRequested();
    void lessEditOptionsRequested();
    }