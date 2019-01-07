package org.musetest.ui.extend.components;

import org.junit.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class WordWrapperTests
    {
    @Test
    public void noWrapRequired()
        {
        Assert.assertEquals(1, countLines(new WordWrapper("pretty short").wrapAfter(80)));
        }

    @Test
    public void wrapAfterSpace()
        {
        String[] lines = extractLines(new WordWrapper("here is some text, and then wrap").wrapAfter(18));
        Assert.assertEquals(2, lines.length);
        Assert.assertTrue(lines[0].endsWith("text,"));
        Assert.assertTrue(lines[1].startsWith("and"));
        }

    @Test
    public void wrapOnFirstChar()
        {
        String[] lines = extractLines(new WordWrapper("here is some text, and then wrap").wrapAfter(20));
        Assert.assertEquals(2, lines.length);
        Assert.assertTrue(lines[0].endsWith("text,"));
        Assert.assertTrue(lines[1].startsWith("and"));
        }

    @Test
    public void moreThanTwoLines()
        {
        String[] lines = extractLines(new WordWrapper("here is some text, and then wrap").wrapAfter(10));
        Assert.assertEquals(3, lines.length);
        }

    private int countLines(String target)
        {
        int breaks = 0;
        char[] chars = target.toCharArray();
        for (int i = 0; i < chars.length; i++)
            if (chars[i] == '\n')
                breaks++;
        return breaks + 1;
        }

    private String[] extractLines(String target)
        {
        StringTokenizer tokenizer = new StringTokenizer(target, "\n");
        List<String> lines = new ArrayList<>();
        while (tokenizer.hasMoreTokens())
            lines.add(tokenizer.nextToken());
        return lines.toArray(new String[lines.size()]);
        }
    }


