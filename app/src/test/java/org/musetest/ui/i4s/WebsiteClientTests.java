package org.musetest.ui.i4s;

import org.junit.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class WebsiteClientTests
    {
    @Test
    public void ping()
        {
        Assert.assertTrue("Is the site down?", I4sClient.get().ping());
        }
    }


