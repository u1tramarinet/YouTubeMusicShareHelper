package com.u1tramarinet.youtubemusicsharehelper;

import static org.junit.Assert.assertEquals;

import com.u1tramarinet.youtubemusicsharehelper.util.TextUtil;

import org.junit.Test;

public class TextUtilTest {
    @Test
    public void createHashTag() {
        String input = "AA%BB & CC";
        String output = TextUtil.createHashTag(input);
        assertEquals("#AA_BB #CC", output);
    }
}
