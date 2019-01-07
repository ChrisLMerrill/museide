package org.musetest.ui.extend.edit;

/**
 * An in-progress edit operation, from the perspective of the initiating UI widget. The implemented should
 * execute the necessary UI changes when the edit is completed or cancelled.
 *
 * This abstracts the editor from it's container. As an example, it prevents an editor from needing
 * to know anything about trees (and cells and items) when it is used in a tree. Besides reducing bleed-thru of
 * unnecessary knowledge, this will hopefully allow the editor to be re-used in other contexts.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface EditInProgress<T>
    {
    void cancel();
    void commit(T target);

    class NoopEdit implements EditInProgress<Object>
        {
        public void cancel() {}
        public void commit(Object target) {}
        }
    }



