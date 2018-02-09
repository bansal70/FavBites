package co.fav.bites.views.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import co.fav.bites.R;
import co.fav.bites.models.APIClient;
import co.fav.bites.models.APIInterface;
import co.fav.bites.models.Utils;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class AppBaseActivity extends AppCompatActivity {

    public Context mContext;
    private Dialog dialog;
    public final int PERMISSION_REQUEST_CODE = 1001;
    public final int REQUEST_IMAGE_CAPTURE = 1;
    public final int PERMISSION_LOCATION_CODE = 1021;
    public final int REQUEST_PRINT_RECEIPT = 3;
    public APIInterface apiService;

    public static final int REQUEST_CONNECT_DEVICE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = AppBaseActivity.this;

        dialog = Utils.showDialog(mContext);

        apiService = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }


    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean isInternetActive() {
        ConnectivityManager conMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        NetworkInfo info = conMgr.getActiveNetworkInfo();

        if (info != null && info.isConnected() && info.isAvailable()) {
            return true;
        } else {
            showToast(getString(R.string.error_internet_connection));
            return false;
        }
    }

    public void serverError(Throwable throwable) {
        dismissDialog();
        showToast(getString(R.string.error_server));
        if (throwable instanceof HttpException) {
            Response<?> response = ((HttpException) throwable).response();
            Timber.e(response.message());
        }
    }

    public void serverError() {
        dismissDialog();
        showToast(getString(R.string.error_server));
    }



    public void locationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSION_REQUEST_CODE || !hasAllPermissionsGranted(grantResults)) {
            showToast(getString(R.string.error_permissions_denied));
        }

    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mItem) {
        switch (mItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(mItem);
    }

}