package co.fav.bites.views.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*
 * Created by rishav on 9/5/2017.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(CalligraphyContextWrapper.wrap(context));
    }
}
