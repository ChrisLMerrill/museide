package org.musetest.ui.settings;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class EnvironmentSettings extends BaseSettingsFile
    {
    /**
     * This is required for Jackson support, but should not be used other than unit tests.
     * Instead, call EnvironmentSettings.get().
     */
    protected EnvironmentSettings()
        {
        }

    public String getWebsiteHostname()
        {
        if (_website_hostname == null)
            return "ide4selenium.com";
        return _website_hostname;
        }

    @SuppressWarnings("unused")  // needed for Jackson
    private void setWebsiteHostname(String hostname)
        {
        _website_hostname = hostname;
        }

    public String getLogLevel()
        {
        if (_log_level == null)
            return "ERROR";
        return _log_level;
        }

    @SuppressWarnings("unused")  // needed for Jackson
    private void setLogLevel(String level)
        {
        _log_level = level;
        }

    @SuppressWarnings("unused")
    private String _website_hostname;

    @SuppressWarnings("unused")
    private String _log_level;

    @Override
    protected boolean shouldSave()
        {
        return false;
        }

    public static EnvironmentSettings get()
        {
        if (SETTINGS == null)
            SETTINGS = (EnvironmentSettings) load(EnvironmentSettings.class, FILENAME, null);
        return SETTINGS;
        }

    private static EnvironmentSettings SETTINGS = null;
    private final static String FILENAME = ".env";
    }


