package org.musetest.ui.ide.navigation;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class RecentProject
    {
    @SuppressWarnings("unused")  // required for serialization
    public RecentProject()
        {
        }

    public RecentProject(String location)
        {
        this.location = location;
        }

    @SuppressWarnings("unused")  // required for serialization
    public String getLocation()
        {
        return location;
        }

    @SuppressWarnings("unused")  // required for serialization
    public void setLocation(String location)
        {
        this.location = location;
        }

    String location;
    }


