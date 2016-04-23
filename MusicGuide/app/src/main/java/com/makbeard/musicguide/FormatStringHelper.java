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
        String numeral = "";
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

    /*


    /**
     * Функция возвращает окончание для множественного числа слова на основании числа и массива окончаний
     * @param  iNumber Integer Число на основе которого нужно сформировать окончание
     * @param  aEndings Array Массив слов или окончаний для чисел (1, 4, 5),
     *         например ['яблоко', 'яблока', 'яблок']
     * @return String

    function getNumEnding(iNumber, aEndings)
    {
        var sEnding, i;
        iNumber = iNumber % 100;
        if (iNumber>=11 && iNumber<=19) {
            sEnding=aEndings[2];
        }
        else {
            i = iNumber % 10;
            switch (i)
            {
                case (1): sEnding = aEndings[0]; break;
                case (2):
                case (3):
                case (4): sEnding = aEndings[1]; break;
                default: sEnding = aEndings[2];
            }
        }
        return sEnding;
    }





     /**
     * Возвращает единицу измерения с правильным окончанием
     *
     * @param {Number} num      Число
     * @param {Object} cases    Варианты слова {nom: 'час', gen: 'часа', plu: 'часов'}
     * @return {String}

    function units(num, cases) {
        num = Math.abs(num);

        var word = '';

        if (num.toString().indexOf('.') > -1) {
            word = cases.gen;
        } else {
            word = (
                    num % 10 == 1 && num % 100 != 11
                            ? cases.nom
                            : num % 10 >= 2 && num % 10 <= 4 && (num % 100 < 10 || num % 100 >= 20)
                            ? cases.gen
                            : cases.plu
            );
        }

        return word;
    }
*/

}
