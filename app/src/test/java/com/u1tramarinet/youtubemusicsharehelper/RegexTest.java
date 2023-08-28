package com.u1tramarinet.youtubemusicsharehelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
    @Test
    public void test() {
        String input = "アーティスト の タイトル を YouTube で見る";
        String regex = "(.*?)( の )(.*?)( を YouTube で見る)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        assertTrue(matcher.matches());
        String title = matcher.group(3);
        String artist = matcher.group(1);

        assertEquals("タイトル", title);
        assertEquals("アーティスト", artist);
    }
}
