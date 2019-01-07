package org.musetest.ui.valuesource.map;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UniqueString
	{
	/**
	 * Generate a suffix number N for a string in the format '{base}N' where base is the parameter
	 * and N is a number. The resulting string '{base}{suffix}' will not exist in the excluded set.
	 *
	 * @param base The beginning of the generated string
	 * @param excluded List of excluded names
	 */
	public static int generateSuffix(String base, Set<String> excluded)
		{
		int i = 1;
		while (true)
			{
			String candidate = base + i;
			if (!excluded.contains(candidate))
				return i;
			i++;
			}
		}

	}


