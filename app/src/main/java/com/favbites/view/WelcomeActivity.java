package com.favbites.view;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.favbites.R;
import com.favbites.model.FBPreferences;
import com.favbites.view.adapters.MyViewPagerAdapter;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    ViewPager viewPager;
    MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    TextView[] dots;
    private int[] layouts;
    TextView tvSignUp, tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_welcome);

        /*if (FBPreferences.readBoolean(this, "is_First")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }*/

        initViews();
    }

    public void initViews() {
        FBPreferences.putBoolean(this, "is_First", true);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvLogin.setPaintFlags(tvLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvSignUp.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3};

        // adding bottom dots
        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter(this, layouts);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts.length - 1) {
                // last page
                tvSignUp.setVisibility(View.VISIBLE);
                tvLogin.setVisibility(View.VISIBLE);
            } else {
                // still pages are left
                tvSignUp.setVisibility(View.INVISIBLE);
                tvLogin.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignUp:
                startActivity(new Intent(this, SignUpActivity.class));
                break;

            case R.id.tvLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
