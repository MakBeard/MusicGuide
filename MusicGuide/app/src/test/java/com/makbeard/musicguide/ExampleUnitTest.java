package com.makbeard.musicguide;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    /**
     * Тест корректного форматирования числительных
     * @throws Exception
     */
    @Test
    public void getFormattedTracksTest() throws Exception {
        assertTrue(FormatStringHelper.getFormattedTracks(1).equals("1 песня"));
        assertTrue(FormatStringHelper.getFormattedTracks(2).equals("2 песни"));
        assertTrue(FormatStringHelper.getFormattedTracks(5).equals("5 песен"));
        assertTrue(FormatStringHelper.getFormattedTracks(20).equals("20 песен"));
        assertTrue(FormatStringHelper.getFormattedTracks(23).equals("23 песни"));
        assertTrue(FormatStringHelper.getFormattedTracks(100).equals("100 песен"));
    }

    /**
     * Тест корректного форматирования числительных
     * @throws Exception
     */
    @Test
    public void getFormattedAlbumsTest() throws Exception {
        assertTrue(FormatStringHelper.getFormattedAlbums(1).equals("1 альбом"));
        assertTrue(FormatStringHelper.getFormattedAlbums(2).equals("2 альбома"));
        assertTrue(FormatStringHelper.getFormattedAlbums(5).equals("5 альбомов"));
        assertTrue(FormatStringHelper.getFormattedAlbums(20).equals("20 альбомов"));
        assertTrue(FormatStringHelper.getFormattedAlbums(100).equals("100 альбомов"));
    }
}