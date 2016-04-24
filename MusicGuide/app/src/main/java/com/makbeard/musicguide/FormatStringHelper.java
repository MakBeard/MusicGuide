package com.makbeard.musicguide;

/**
 * Класс-helper для форматичрования числительных
 */
public class FormatStringHelper {

    /**
     * Метод преобразовывает int в строку с правильным окончанием
     * @param count числительное
     * @param variantsArray список варинатов окончаний
     * @return отформатированная строка
     */
    private static String getFormattedString(int count, String[] variantsArray) {
        String numeral;
        int i = count % 100;
        if (i >= 11 && i <= 19) {
            numeral = variantsArray[2];
        } else {
            i = count % 10;
            switch (i) {
                case 0:
                    numeral = variantsArray[2];
                    break;
                case 1:
                    numeral = variantsArray[0];
                    break;
                case 2:
                case 3:
                case 4:
                    numeral = variantsArray[1];
                    break;
                default:
                    numeral = variantsArray[2];
            }
        }

        return count + numeral;
   }

    /**
     * Метод для получения отформатированной строки песен
     * @param count числительное песен
     * @return отформатированную строку
     */
    public static String getFormattedTracks(int count) {
        return getFormattedString(count, new String[] {" песня"," песни"," песен"});
    }

    /**
     * Метод для получения отформатированной строки альбомов
     * @param count числтельное альбомы
     * @return отформатированную строку
     */
    public static String getFormattedAlbums(int count) {
        return getFormattedString(count, new String[] {" альбом", " альбома", " альбомов"});
    }
}
