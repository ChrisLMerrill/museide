package org.musetest.ui.extend.components;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class WordWrapper
    {
    public WordWrapper(String target)
        {
        if (target == null)
            throw new IllegalArgumentException("'target' parameter cannot be null");
        _target = target;
        }

    public String wrapAfter(int line_length)
        {
        StringBuilder builder = new StringBuilder();
        int wrap_point = line_length - 1;

        int current = 0;
        int break_at = getNextBreak(current, wrap_point);
        while (break_at > 0)
            {
            builder.append(_target.substring(current, break_at));
            builder.append('\n');
            current = break_at + 1;
            break_at = getNextBreak(current, wrap_point);
            }

        builder.append(_target.substring(current));

        return builder.toString();
        }

    private int getNextBreak(int start, int length)
        {
        boolean hit_word_break = false;
        if (start + length > _target.length())
            return -1;

        if (_target.charAt(start + length - 1) == ' ' && _target.charAt(start + length) != ' ')
            return start + length - 1;

        for (int i = start + length; i < _target.length(); i++)
            {
            if (_target.charAt(i) == ' ')
                hit_word_break = true;
            else if (hit_word_break)
                return i - 1;
            }
        return -1;
        }

    private final String _target;
    }


