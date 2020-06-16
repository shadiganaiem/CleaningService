package com.cleaningservice.cleaningservice.Customer;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;
import com.cleaningservice.cleaningservice.Validator;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Authentications.Preferences;
import Models.JobForm;
import butterknife.OnClick;

import static Authentications.Preferences.GetLoggedInUserID;


public class FindCleanerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private TextView dateText;
    private TextView dateText2;
    private TextView cityText;
    private TextView addressText;
    private String startDate;
    private String endDate;
    private DrawerLayout drawer;
    private int roomNum;
    private float budget;
    private String address =null;
    private String city = null;
    private String description = "";
    private ApplicationDbContext _context = null;
    private static final String TAG = FindCleanerActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;
    private ArrayList<Bitmap> images =new ArrayList<Bitmap>();
    //private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private Validator _validator = null;
    private Date date1=null;
    private Date date2=null;
    private TextInputEditText budgt;
    private Boolean status;
    private Handler mainHandler = new Handler();


    //Google Places API Client
    private PlacesClient placesClient;

    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_find_cleaner);

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Toolbar toolbar2 = findViewById(R.id.sidebar);
        setSupportActionBar(toolbar2);

        drawer=findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar2,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        try {
            GooglePlacesApiConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dateText = findViewById(R.id.date_text);
        dateText2= findViewById(R.id.date_text2);
        addressText = findViewById(R.id.street);
        cityText = findViewById(R.id.city);
        linearLayout = findViewById(R.id.linearLayout3);

        _validator = new Validator();

        findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.show_dialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog2();
            }
        });


       // imgProfile = findViewById(R.id.img_profile2);

    }

    /**
     *
     * @param menuItem
     * @return
     * changes activities using the side bar
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.navigation_profile:
                Intent intent = new Intent(FindCleanerActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                Intent intent2 = new Intent(FindCleanerActivity.this, FavoritesListActivity.class);
                startActivity(intent2);
                break;
            case R.id.navigation_findcleaner:
                break;
            case R.id.navigation_myjobs:
                Intent intent4 = new Intent(FindCleanerActivity.this, MyJobsActivity.class);
                startActivity(intent4);
                break;
            case R.id.navigation_notifications:
                Intent intent5 = new Intent(FindCleanerActivity.this, NotificationsActivity.class);
                startActivity(intent5);
                break;
            case R.id.navigation_signout:
                Preferences.Logout(this);
                Intent intent6 = new Intent(FindCleanerActivity.this, MainActivity.class);
                startActivity(intent6);
                break;
            case R.id.change_language:
                showLanguageDialog();
                break;
        }
        return false;
    }

    private void showLanguageDialog() {
        final String[] languagesList = { "العربية", "עברית" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.chooseLanguage));
        builder.setSingleChoiceItems(languagesList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    setLocale("ar");
                    recreate();
                }
                else if (which ==1){
                    setLocale("he");
                    recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang","");
        setLocale(lang);
    }

    /**
     * close or open side bar
     */
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * shows the Datepicker dialog
     */
    public void showDatePickerDialog(){
        flag=0;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * shows the Datepicker dialog
     */
    public void showDatePickerDialog2(){
        flag=1;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     *
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     * changes dates format
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month+=1;
        String date =  month +  "/" + dayOfMonth + "/" + year;
        if(flag==0) {
            try {
                date1 =new Date();
               // DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                date1 =  new SimpleDateFormat("MM/dd/yyyy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateText.setText(date);
        }
        else{
            try {
                date2 =new Date();
                date2 =  new SimpleDateFormat("MM/dd/yyyy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateText2.setText(date);
        }
    }

    /**
     * Google Places API connect and initialize
     */
    public void GooglePlacesApiConnect() throws IOException {
        String ApiKey = Util.GetProperty("api.googleplaces",getApplicationContext());

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),ApiKey);
        }
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment=
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        Geocoder geocoder;
        geocoder = new Geocoder(this);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng= place.getLatLng();

             //   Log.i("placesAPI","onPlaceSelected: "+latLng.latitude+"\n"+latLng.longitude);

                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String saddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String scity = addresses.get(0).getLocality();
                    //String state = addresses.get(0).getAdminArea();
                    //String country = addresses.get(0).getCountryName();
                    //String postalCode = addresses.get(0).getPostalCode();
                    //String knownName = addresses.get(0).getFeatureName();
                    String[] add = saddress.split(",");
                    address=add[0];
                    city=scity;
                    cityText.setText(city);
                    addressText.setText(address);
                } catch (IOException e) {
                    e.printStackTrace();
                }




            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


    }

    /**
     *
     * @param view
     * @throws Exception
     * save the form data in the data base
     */
    public void Publish(View view) throws Exception {
        String regex = "([0-9]*[.])?[0-9]+";
        budgt = findViewById(R.id.budget);
        status = true;


        if(!_validator.InputValidate(budgt,regex)) {
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'> ערך לא חוקי</font>");
            budgt.setError(errorMsg);
            status = false;
        }

            Compare_dates();
            if (address==null || city==null){
                TextView ci = findViewById(R.id.city);
                TextView st = findViewById(R.id.street);
                android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>כתובת ועיר הם שדות חובה</font>");
                ci.setError(errorMsg);
                st.setError(errorMsg);
                status = false;
            }

        if(status) {
            budget = Float.parseFloat((budgt).getText().toString());
            startDate = dateText.getText().toString();
            endDate = dateText2.getText().toString();
            TextInputEditText desc = findViewById(R.id.Description);
            description = desc.getText().toString();

            Spinner spinner = (Spinner) findViewById(R.id.spinner2);
            String text = spinner.getSelectedItem().toString();
            roomNum = Integer.parseInt(text);
            int customerId = _context.GetCustomerIdByUserID(GetLoggedInUserID(this));
            JobForm jobForm = new JobForm(
                    customerId,
                    roomNum,
                    city,
                    address,
                    budget,
                    description
            );
            new Thread(){
                public void run() {
                    int jobFormId = _context.InsertJobForm(jobForm, startDate, endDate);
                   // int jobFormId = 60;
                    if (jobFormId > 0) {
                                if (!_context.InsertImage(jobFormId, images)) {
                                    try {
                                        throw new Exception("problem aquired while attempting to upload images to the database");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                    }
                    else {
                        try {
                            throw new Exception("Form ID is not valid");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
            Intent intent = new Intent(FindCleanerActivity.this, MyJobsActivity.class);
            startActivity(intent);
    }
    }

    /**
     * check dates ellegability
     */
    private void Compare_dates() {
        Date todayDate= new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String strDate = dateFormat.format(todayDate);
        try {
            todayDate =  new SimpleDateFormat("MM/dd/yyyy").parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextView first = findViewById(R.id.date_text);
        TextView second = findViewById(R.id.date_text2);

        if(date1==null || todayDate.after(date1)){
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>תאריך כבר עבר או לא קיים</font>");
            first.setError(errorMsg);
            status= false;
        }
        if (date2==null || todayDate.after(date2) ){
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>תאריך כבר עבר או לא קיים</font>");
            second.setError(errorMsg);
            status= false;
        }
        if(date1==null || date2==null || date2.before(date1)) {
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>תאריך סיום לא יכול להיות לפני תאריך התחלה</font>");
            first.setError(errorMsg);
            second.setError(errorMsg);
            status= false;
        }
    }

    @OnClick({R.id.addPhotos})
    public void onProfileImageClick(View view) {
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

    private void loadProfile(String url) {
            ImageView imgView= new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imgView.setLayoutParams(lp);
           // Log.d(TAG, "Image cache path: " + element);
            GlideApp.with(this).load(url).override(500)
                    .into(imgView);
            imgView.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
            linearLayout.addView(imgView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                        images.add(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
        Intent intent = new Intent(FindCleanerActivity.this, ImagePickerActivity.class);
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
        Intent intent = new Intent(FindCleanerActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FindCleanerActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }


    //navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}


