package org.musetest.ui.settings;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UserSettings extends BaseSettingsFile
    {
    /**
     * This is required for Jackson support, but should not be used other than unit tests.
     * Instead, call UserSettings.get().
     */
    public UserSettings()
        {
        _user_id = UUID.randomUUID().toString();
        }

    public String getUserId()
        {
        return _user_id;
        }

    /**
     * This is required for Jackson support. The user id is determinted automatically. This method
     * should not be called outside of a unit test.
     */
    @SuppressWarnings("unused")
    private void setUserId(String user_id)
        {
        _user_id = user_id;
        }

    private String _user_id;

    public static UserSettings get()
        {
        if (SETTINGS == null)
            SETTINGS = (UserSettings) load(UserSettings.class, FILENAME, null);
        return SETTINGS;
        }

    private static UserSettings SETTINGS = null;
    private final static String FILENAME = ".user";
    }


