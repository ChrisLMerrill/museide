package org.museautomation.ui.extend.components;

import org.junit.jupiter.api.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class WordWrapperTests
    {
    @Test
    void noWrapRequired()
        {
        Assertions.assertEquals(1, countLines(new WordWrapper("pretty short").wrapAfter(80)));
        }

    @Test
    void wrapAfterSpace()
        {
        String[] lines = extractLines(new WordWrapper("here is some text, and then wrap").wrapAfter(18));
        Assertions.assertEquals(2, lines.length);
        Assertions.assertTrue(lines[0].endsWith("text,"));
        Assertions.assertTrue(lines[1].startsWith("and"));
        }

    @Test
    void wrapOnFirstChar()
        {
        String[] lines = extractLines(new WordWrapper("here is some text, and then wrap").wrapAfter(20));
        Assertions.assertEquals(2, lines.length);
        Assertions.assertTrue(lines[0].endsWith("text,"));
        Assertions.assertTrue(lines[1].startsWith("and"));
        }

    @Test
    void moreThanTwoLines()
        {
        String[] lines = extractLines(new WordWrapper("here is some text, and then wrap").wrapAfter(10));
        Assertions.assertEquals(3, lines.length);
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
        return lines.toArray(new String[0]);
        }
    }


