package org.museautomation.ui.ide;

public class BuildVersion
	{
	public static String getBuildVersion()
		{
		return BuildVersion.class.getPackage().getImplementationVersion();
		}
	}
