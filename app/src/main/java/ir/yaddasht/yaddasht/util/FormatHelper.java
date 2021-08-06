package ir.yaddasht.yaddasht.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

///convert numbers to persian
public class FormatHelper {

    private static String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};


    public static String toPersianNumber(String text) {
        if (text.length() == 0) {
            return "";
        }
        String out = "";
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out += persianNumbers[number];
            } else if (c == '٫') {
                out += '،';
            } else {
                out += c;
            }

//            return out;
        }
        return out;
    }

    public static String formatTimeToPersian(Calendar time){
        Date dt = time.getTime();
        SimpleDateFormat cFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return toPersianNumber(cFormat.format(dt));
    }
}