package ir.yaddasht.yaddasht.util;

import android.content.Context;

public class DpToPx {
    public static int convert(int dp,Context context) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
