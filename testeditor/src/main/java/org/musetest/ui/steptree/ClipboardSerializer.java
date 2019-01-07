package org.musetest.ui.steptree;

import com.fasterxml.jackson.databind.*;
import org.musetest.core.step.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * These methods de/serialize things to/from string format so they can be placed on the clipboard.
 * Serializing directly worked fine pre-Java9. In Java9+, it worked fine in development, but not when
 * deployed/installed. Reason is unknown. Serializing to string works, hence this somewhat hacky fix.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ClipboardSerializer
    {
    public static String listOfLongsToString(List<Long> longs)
        {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < longs.size(); i++)
            {
            if (i > 0)
                builder.append(",");
            builder.append(longs.get(i).toString());
            }
        return builder.toString();
        }

    public static String listOfStepsToString(List<StepConfiguration> steps)
        {
        try
            {
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            mapper.writeValue(outstream, steps);
            return outstream.toString();
            }
        catch (IOException e)
            {
            LOG.error("Unable to serialize step list to string", e);
            return null;
            }
        }

    public static List<Long> listOfLongsfromString(String all)
        {
        String[] strings = all.split(",");
        List<Long> ints = new ArrayList<>();
        for (String s : strings)
            ints.add(Long.parseLong(s));
        return ints;
        }

    public static List<StepConfiguration> listOfStepsfromString(String all)
        {
        try
            {
            ObjectMapper mapper = new ObjectMapper();
            StepConfiguration[] steps = mapper.readValue(all, StepConfiguration[].class);
            List<StepConfiguration> steplist = new ArrayList<>();
            Collections.addAll(steplist, steps);
            return steplist;
            }
        catch (Exception e)
            {
            LOG.error("Unable to deserialize StepConfigurations from the string: all", e);
            return null;
            }
        }

    private final static Logger LOG = LoggerFactory.getLogger(ClipboardSerializer.class);
    }