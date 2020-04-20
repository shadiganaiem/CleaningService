package com.cleaningservice.cleaningservice.Worker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.GlideApp;
import com.cleaningservice.cleaningservice.ImagePickerActivity;
import com.cleaningservice.cleaningservice.MainActivity;
import com.cleaningservice.cleaningservice.Customer.ProfileActivity;
import com.cleaningservice.cleaningservice.R;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import Authentications.Preferences;
import Models.Customer;
import Models.Employee;
import Models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static Authentications.Preferences.GetLoggedInUserID;
import static Authentications.Preferences.isCustomer;

public class WorkerProfile  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private  DrawerLayout drawer;
    private ApplicationDbContext _context = null;
    private Handler mainhandler = new Handler();
    private Handler secondhandler = new Handler();
    private Customer finalCustomer;
    private Employee finalEmployee;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;

    @BindView(R.id.img_profile)
    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toollbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        loadProfileDefault();



        Toolbar toolbar2 = findViewById(R.id.sidebar);
        setSupportActionBar(toolbar2);

        drawer=findViewById(R.id.drawer_layout);

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar2,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TextView name = findViewById(R.id.name);
        TextView userType = findViewById(R.id.user_type);
        TextView mail = findViewById(R.id.emaill);
        TextView phone =  findViewById(R.id.phonenum);

        if(GetLoggedInUserID(this)!=0){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    User user =_context.GetUserDetails(GetLoggedInUserID(WorkerProfile.this));
                    Employee employee = null;
                    Customer customer = null;
                    if(isCustomer(WorkerProfile.this)) {
                        customer = _context.GetCustomer(user.CustomerId);
                    }
                    else
                    {
                        employee = _context.GetEmployee(user.EmployeeId);
                    }
                    finalCustomer = customer;
                    finalEmployee = employee;
                    mainhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(finalCustomer != null) {
                                name.setText(finalCustomer.Lastname+" "+finalCustomer.Firstname);
                                userType.setText("Job Provider");
                                mail.setText(finalCustomer.Email);
                                phone.setText(finalCustomer.Phone);
                            }
                            else{
                                name.setText(finalEmployee.Lastname+" "+finalEmployee.Firstname);
                                userType.setText("Employee");
                                mail.setText(finalEmployee.Email);
                                phone.setText(finalEmployee.Phone);
                            }
                        }
                    });

                    byte[] profileImage = _context.GetProfileImage(user.ID);
                    Drawable bitmap = new BitmapDrawable(BitmapFactory.decodeByteArray(profileImage, 0, profileImage.length));
                    //GlideApp.with(ProfileActivity.this).load(_context.GetProfileImage(user.ID))
                    //      .into(imgProfile);
                    secondhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            imgProfile.setImageDrawable(bitmap);
                            imgProfile.setColorFilter(ContextCompat.getColor(WorkerProfile.this, android.R.color.transparent));
                        }
                    });
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        GlideApp.with(this).load(url)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadProfileDefault() {
        GlideApp.with(this).load(R.drawable.baseline_account_circle_black_48)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, R.color.darkblue));
    }

    @OnClick({R.id.img_plus, R.id.img_profile})
    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(WorkerProfile.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(WorkerProfile.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    //Update In DB
                    if(_context.UpdateProfileImage(Preferences.GetLoggedInUserID(getApplicationContext()),bitmap));
                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkerProfile.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void Logout(View view){
        Preferences.Logout(this);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.navigation_profile:
                    break;
                case R.id.navigation_favlist:
                    Intent intent2 = new Intent(WorkerProfile.this, Favorites.class);
                    startActivity(intent2);
                    break;
                case R.id.navigation_myjobs:
                    Intent intent4 = new Intent(WorkerProfile.this, JobFormRequests.class);
                    startActivity(intent4);
                    break;
                case R.id.navigation_employee_home:
                    Intent intent5 = new Intent(WorkerProfile.this, Home.class);
                    startActivity(intent5);
                    break;
            }
        return false;
    }
}
