package co.fav.bites.views;

import android.os.Bundle;
import android.view.View;

import co.fav.bites.R;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void backScreen(View v) {
        finish();
    }
}
