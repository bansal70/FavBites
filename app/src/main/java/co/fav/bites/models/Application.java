package co.fav.bites.models;

import android.content.Context;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import co.fav.bites.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

@ReportsCrashes(formUri = "", mailTo = "rishav.orem@gmail.com")
public final class Application extends android.app.Application {

    public static String Font_Text = "fonts/Futura.ttf";

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Font_Text)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}