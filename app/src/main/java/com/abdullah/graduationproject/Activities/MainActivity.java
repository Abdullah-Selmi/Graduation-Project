package com.abdullah.graduationproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.abdullah.graduationproject.Fragments.AdviserFragment;
import com.abdullah.graduationproject.Fragments.FavoriteFragment;
import com.abdullah.graduationproject.Fragments.FruitsAndVegetablesFragment;
import com.abdullah.graduationproject.Fragments.HomeFragment;
import com.abdullah.graduationproject.Fragments.SeedsFragment;
import com.abdullah.graduationproject.Fragments.ToolsFragment;
import com.abdullah.graduationproject.Fragments.WaterFragment;
import com.abdullah.graduationproject.Fragments.WorkerFragment;
import com.abdullah.graduationproject.LogInActivities.LoginActivity;
import com.abdullah.graduationproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    View HomeView;
    TextView loginNavHeaderTextView;
    Context context;
    CircleImageView UserProfilePictureImageView;
    TextView UserNameTextView;
    Intent toLoginActivity, toProfileActivity;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findHomeViews();
        ReadyForDrawer(savedInstanceState);
        if (SaveSharedPreference.getLogIn(context).equals("true")) {
            Toast.makeText(context, "مرحباً " + SaveSharedPreference.getFirstName(context), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findHomeViews();
        setAppLocale("ar");
        CheckTheState();
        CheckFragment();
        if (SaveSharedPreference.getLogIn(context).equals("true")) {
            navigationView = findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_delete_account).setVisible(true);
        } else {
            navigationView = findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_delete_account).setVisible(false);
        }
    }

    private void CheckFragment() {
        if (SaveSharedPreference.getFragment(this).equals("1")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FavoriteFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_favorite);
        } else if (SaveSharedPreference.getFragment(this).equals("2")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WaterFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_water);
        } else if (SaveSharedPreference.getFragment(this).equals("3")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FruitsAndVegetablesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_fruits_and_vegetables);
        } else if (SaveSharedPreference.getFragment(this).equals("4")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SeedsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_seeds);
        } else if (SaveSharedPreference.getFragment(this).equals("5")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ToolsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_tools);
        } else if (SaveSharedPreference.getFragment(this).equals("6")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WorkerFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_worker);
        } else if (SaveSharedPreference.getFragment(this).equals("7")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AdviserFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_Adviser);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void CheckTheState() {
        if (SaveSharedPreference.getLogIn(context).equals("true")) {
            loginNavHeaderTextView.setText("تسجيل الخروج");
            loginNavHeaderTextView.setTextColor(getResources().getColor(R.color.Red));
            UserNameTextView.setText(SaveSharedPreference.getFirstName(context) + " " + SaveSharedPreference.getLastName(context));
            UserNameTextView.setVisibility(View.VISIBLE);
            if (MainActivity.SaveSharedPreference.getImage(this).equals("")) {
                UserProfilePictureImageView.setImageDrawable(getResources().getDrawable(R.drawable.profiledefault));
            } else {
                Picasso.get().load(MainActivity.SaveSharedPreference.getImage(this)).into(UserProfilePictureImageView);
            }
        } else {
            Logout();
        }
    }

    public void setAppLocale(String localCode) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(new Locale(localCode.toLowerCase()));
        } else {
            configuration.locale = new Locale(localCode.toLowerCase());
        }
        resources.updateConfiguration(configuration, metrics);
    }

    private void ReadyForDrawer(Bundle savedInstanceState) {
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        toolbar.setTitle(R.string.app_name);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (!SaveSharedPreference.getFragmentCheck(this).equals("")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            SaveSharedPreference.setFragmentCheck(this, "");
        } else if (SaveSharedPreference.getFragmentCheck(this).equals("")) {
            drawerLayout.closeDrawer(GravityCompat.START);
            AlertDialog dialog = ExitDialog();
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (SaveSharedPreference.getLogIn(context).equals("true")) {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    SaveSharedPreference.setFragment(this, "");
                    SaveSharedPreference.setFragmentCheck(this, "");
                    startActivity(toProfileActivity);
                    break;
                case R.id.nav_favorite:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FavoriteFragment()).commit();
                    SaveSharedPreference.setFragmentCheck(this, "1");
                    break;
            }
        } else {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    SaveSharedPreference.setFragment(this, "");
                    SaveSharedPreference.setFragmentCheck(this, "");
                    startActivity(toLoginActivity);
                    break;
                case R.id.nav_favorite:
                    SaveSharedPreference.setFragment(this, "");
                    SaveSharedPreference.setFragmentCheck(this, "");
                    startActivity(toLoginActivity);
                    break;
            }
        }
        switch (item.getItemId()) {
            case R.id.nav_home:
                SaveSharedPreference.setFragment(this, "");
                SaveSharedPreference.setFragmentCheck(this, "");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_water:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new WaterFragment()).commit();
                SaveSharedPreference.setFragmentCheck(this, "2");
                break;
            case R.id.nav_fruits_and_vegetables:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FruitsAndVegetablesFragment()).commit();
                SaveSharedPreference.setFragmentCheck(this, "3");
                break;
            case R.id.nav_seeds:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SeedsFragment()).commit();
                SaveSharedPreference.setFragmentCheck(this, "4");
                break;
            case R.id.nav_tools:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ToolsFragment()).commit();
                SaveSharedPreference.setFragmentCheck(this, "5");
                break;
            case R.id.nav_worker:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new WorkerFragment()).commit();
                SaveSharedPreference.setFragmentCheck(this, "6");
                break;
            case R.id.nav_Adviser:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AdviserFragment()).commit();
                SaveSharedPreference.setFragmentCheck(this, "7");
                break;
            case R.id.nav_contact_us:
                Toast.makeText(this, "Go to Contact Us web page", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about_us:
                SaveSharedPreference.setFragment(this, "");
                SaveSharedPreference.setFragmentCheck(this, "");
                Intent toAboutUsActivity = new Intent(this, AboutUsActivity.class);
                startActivity(toAboutUsActivity);
                break;
            case R.id.nav_delete_account:
                AlertDialog dialog = DeleteAccountDialog();
                dialog.show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void findHomeViews() {
        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        context = this;
        HomeView = navigationView.getHeaderView(0);
        drawerLayout = findViewById(R.id.drawerLayout);
        loginNavHeaderTextView = HomeView.findViewById(R.id.loginNavHeaderTextView);
        UserProfilePictureImageView = HomeView.findViewById(R.id.UserProfilePictureImageView);
        UserNameTextView = HomeView.findViewById(R.id.UserNameTextView);
        toLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
        toProfileActivity = new Intent(MainActivity.this, ProfileActivity.class);
    }

    public void loginNavHeaderTextViewClicked(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (SaveSharedPreference.getLogIn(context).equals("true")) {
            AlertDialog dialog = LogoutDialog();
            dialog.show();
        } else {
            Intent toLoginActivity = new Intent(this, LoginActivity.class);
            startActivity(toLoginActivity);
        }
    }

    public void UserProfilePictureImageViewClicked(View view) {
        if (SaveSharedPreference.getLogIn(context).equals("true")) {
            startActivity(toProfileActivity);
            navigationView.setCheckedItem(R.id.nav_profile);
        } else {
            startActivity(toLoginActivity);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public static class SaveSharedPreference {
        static final String PREF_LOGIN = "LogIn";
        static final String PREF_FIRST_NAME = "FirstName";
        static final String PREF_LAST_NAME = "LastName";
        static final String PREF_LOCATION = "Location";
        static final String PREF_Age = "Age";
        static final String PREF_PHONE_NUMBER = "PhoneNumber";
        static final String PREF_ROLE = "Role";
        static final String PREF_PASSWORD = "Password";
        static final String PREF_PE = "PE";
        static final String PREF_PP = "PP";
        static final String PREF_SKILLS = "Skills";
        static final String PREF_ABOUT = "About";
        static final String PREF_CODE = "Code";
        static final String PREF_IMAGE = "Image";
        static final String PREF_CV = "CV";
        static final String PREF_RATING = "Rating";
        static final String PREF_FAVORITE_COUNTER = "FavoriteCounter";
        static final String PREF_ITEMS_COUNTER = "ItemsCounter";
        static final String PREF_POSTS_COUNTER = "PostsCounter";
        static final String PREF_FRAGMENT = "Fragment";
        static final String PREF_FRAGMENT_CHECK = "FragmentCheck";

        static SharedPreferences getSharedPreferences(Context ctx) {
            return PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        public static void setLogIn(Context ctx, String login) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_LOGIN, login);
            editor.apply();
        }

        public static String getLogIn(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_LOGIN, "");
        }

        public static void setFirstName(Context ctx, String firstname) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_FIRST_NAME, firstname);
            editor.apply();
        }

        public static String getFirstName(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_FIRST_NAME, "");
        }

        public static void setLastName(Context ctx, String lastname) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_LAST_NAME, lastname);
            editor.apply();
        }

        public static String getLastName(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_LAST_NAME, "");
        }

        public static void setLocation(Context ctx, String location) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_LOCATION, location);
            editor.apply();
        }

        public static String getLocation(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_LOCATION, "");
        }

        public static void setAge(Context ctx, String age) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_Age, age);
            editor.apply();
        }

        public static String getAge(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_Age, "");
        }

        public static void setPhoneNumber(Context ctx, String phonenumber) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_PHONE_NUMBER, phonenumber);
            editor.apply();
        }

        public static String getPhoneNumber(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_PHONE_NUMBER, "");
        }

        public static void setRole(Context ctx, String role) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_ROLE, role);
            editor.apply();
        }

        public static String getRole(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_ROLE, "");
        }

        public static void setPassword(Context ctx, String password) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_PASSWORD, password);
            editor.apply();
        }

        public static String getPassword(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_PASSWORD, "");
        }

        public static void setPE(Context ctx, String PE) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_PE, PE);
            editor.apply();
        }

        public static String getPE(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_PE, "");
        }

        public static void setPP(Context ctx, String PP) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_PP, PP);
            editor.apply();
        }

        public static String getPP(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_PP, "");
        }

        public static void setSkills(Context ctx, String skills) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_SKILLS, skills);
            editor.apply();
        }

        public static String getSkills(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_SKILLS, "");
        }

        public static void setAbout(Context ctx, String about) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_ABOUT, about);
            editor.apply();
        }

        public static String getAbout(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_ABOUT, "");
        }

        public static void setCode(Context ctx, String code) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_CODE, code);
            editor.apply();
        }

        public static String getCode(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_CODE, "");
        }

        public static void setImage(Context ctx, String image) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_IMAGE, image);
            editor.apply();
        }

        public static String getImage(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_IMAGE, "");
        }

        public static void setCV(Context ctx, String cv) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_CV, cv);
            editor.apply();
        }

        public static String getCV(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_CV, "");
        }

        public static void setRating(Context ctx, String rating) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_RATING, rating);
            editor.apply();
        }

        public static String getRating(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_RATING, "");
        }

        public static void setFavoriteCounter(Context ctx, String favoritecounter) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_FAVORITE_COUNTER, favoritecounter);
            editor.apply();
        }

        public static String getFavoriteCounter(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_FAVORITE_COUNTER, "0");
        }

        public static void setItemsCounter(Context ctx, String itemscounter) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_ITEMS_COUNTER, itemscounter);
            editor.apply();
        }

        public static String getItemsCounter(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_ITEMS_COUNTER, "0");
        }

        public static void setPostsCounter(Context ctx, String postscounter) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_POSTS_COUNTER, postscounter);
            editor.apply();
        }

        public static String getPostsCounter(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_POSTS_COUNTER, "0");
        }

        public static void setFragment(Context ctx, String fragment) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_FRAGMENT, fragment);
            editor.apply();
        }

        public static String getFragment(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_FRAGMENT, "");
        }

        public static void setFragmentCheck(Context ctx, String fragmentCheck) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_FRAGMENT_CHECK, fragmentCheck);
            editor.apply();
        }

        public static String getFragmentCheck(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_FRAGMENT_CHECK, "");
        }
    }

    private AlertDialog LogoutDialog() {
        final AlertDialog LogoutDialog = new AlertDialog.Builder(this)
                .setTitle("تسجيل الخروج")
                .setMessage("هل تريد إكمال تسجيل الخروج ؟")
                .setIcon(R.drawable.ic_log_out)
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        Logout();
                        navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        menu.findItem(R.id.nav_delete_account).setVisible(false);
                        Toast.makeText(context, "تم تسجيل الخروج", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        LogoutDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                LogoutDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Red));
            }
        });

        return LogoutDialog;
    }

    private AlertDialog ExitDialog() {
        final AlertDialog LogoutDialog = new AlertDialog.Builder(this)
                .setTitle("خروج")
                .setMessage("هل تريد الخروج من التطبيق ؟")
                .setIcon(R.drawable.ic_log_out)
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        LogoutDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                LogoutDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Red));
            }
        });
        return LogoutDialog;
    }

    public void Clickable(boolean b) {
        if (b) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public boolean Connected() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    private AlertDialog DeleteAccountDialog() {
        final AlertDialog LogoutDialog = new AlertDialog.Builder(this)
                .setTitle("حذف الحساب")
                .setMessage("عند حذفك للحساب سوف يتم حذف جميع البيانات المرتبطة بذلك الحساب\n هل تريد المتابعة ؟")
                .setIcon(R.drawable.ic_delete_account)
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        if (Connected()) {
                            DeleteAccount();
                        } else {
                            Toast.makeText(context, R.string.InternetConnectionMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        LogoutDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                LogoutDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Red));
            }
        });
        return LogoutDialog;
    }

    private void DeleteAccount() {
        Clickable(false);
        DeleteFavorite();
    }

    private void DeleteFavorite() {
        db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(this))
                .collection(getString(R.string.FavoriteCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                                        .collection(getString(R.string.FavoriteCollection)).document(document.getId()).delete();

                            }
                            DeletePhoto();
                        } else {
                            Toast.makeText(context, "حدث خطأ أثناء حذف الحساب", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void DeletePhoto() {
        if (UserProfilePictureImageView.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.profiledefault).getConstantState()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference photoRef = storageRef.child("uploads").child(SaveSharedPreference.getPhoneNumber(MainActivity.this));
            photoRef.delete();
        }
        if (SaveSharedPreference.getRole(this).equals("2")) {
            DeleteItemsPhoto();
        } else if (SaveSharedPreference.getRole(this).equals("3")) {
            DeleteCV();
        } else if (SaveSharedPreference.getRole(this).equals("4")) {
            DeleteWorkerRating();
        } else {
            DeleteUser();
        }
    }

    private void DeleteItemsPhoto() {
        db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(this))
                .collection(getString(R.string.ItemsCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference photoRef = storageRef.child(getString(R.string.ItemsCollection))
                                        .child(document.getId());
                                photoRef.delete();
                            }
                            DeleteItemsRating();
                        } else {
                            Toast.makeText(MainActivity.this, "حدث خطأ, يرجى المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void DeleteItemsRating() {
        db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                .collection(getString(R.string.ItemsCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                                        .collection(getString(R.string.ItemsCollection)).document(document.getId())
                                        .collection(getString(R.string.ReviewCollection))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                        db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                                                                .collection(getString(R.string.ItemsCollection)).document(document.getId())
                                                                .collection(getString(R.string.ReviewCollection)).document(document1.getId())
                                                                .delete();
                                                    }
                                                }
                                            }
                                        });
                            }
                            DeleteItemsRatingCollection();
                        }
                    }
                });
    }

    private void DeleteItemsRatingCollection() {
        db.collection(getString(R.string.ItemsCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(getString(R.string.ItemsCollection)).document(document.getId())
                                        .collection(getString(R.string.ReviewCollection))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                        db.collection(getString(R.string.ItemsCollection)).document(document.getId())
                                                                .collection(getString(R.string.ReviewCollection)).document(document1.getId())
                                                                .delete();
                                                    }
                                                }
                                            }
                                        });
                            }
                            DeleteItems();
                        }
                    }
                });
    }

    private void DeleteItems() {
        db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                .collection(getString(R.string.ItemsCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                                        .collection(getString(R.string.ItemsCollection)).document(document.getId())
                                        .delete();
                            }
                            DeleteItemsCollection();
                        }
                    }
                });
    }

    private void DeleteItemsCollection() {
        db.collection(getString(R.string.ItemsCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(getString(R.string.ItemsCollection)).document(document.getId())
                                        .delete();
                            }
                            DeleteUser();
                        }
                    }
                });
    }

    private void DeleteCV() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference deleteRef = storageRef.child("CVs").child(SaveSharedPreference.getCV(MainActivity.this));
        deleteRef.delete();
        DeleteAdviserRating();
    }

    private void DeleteAdviserRating() {
        db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(this))
                .collection(getString(R.string.RatingCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                                        .collection(getString(R.string.RatingCollection))
                                        .document(document.getId()).delete();
                            }
                            DeletePosts();
                        } else {
                            Toast.makeText(context, "حدث خطأ أثناء حذف الحساب", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void DeletePosts() {
        db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(this))
                .collection(getString(R.string.PostsCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                                        .collection(getString(R.string.PostsCollection)).document(document.getId()).delete();
                            }
                            DeleteUser();
                        } else {
                            Toast.makeText(context, "حدث خطأ أثناء حذف الحساب", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void DeleteWorkerRating() {
        db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(this))
                .collection(getString(R.string.RatingCollection))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(getString(R.string.UsersCollection)).document(SaveSharedPreference.getPhoneNumber(MainActivity.this))
                                        .collection(getString(R.string.RatingCollection))
                                        .document(document.getId()).delete();
                            }
                            DeleteUser();
                        } else {
                            Toast.makeText(context, "حدث خطأ أثناء حذف الحساب", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void DeleteUser() {
        db.collection(getString(R.string.UsersCollection))
                .document(SaveSharedPreference.getPhoneNumber(this))
                .delete();
        Logout();
        navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_delete_account).setVisible(false);
        Clickable(true);
        Toast.makeText(MainActivity.this, "تم حذف الحساب بنجاح!", Toast.LENGTH_SHORT).show();
    }

    private void Logout() {
        SaveSharedPreference.setLogIn(context, "false");
        SaveSharedPreference.setFirstName(context, "");
        SaveSharedPreference.setLastName(context, "");
        SaveSharedPreference.setLocation(context, "");
        SaveSharedPreference.setAge(context, "");
        SaveSharedPreference.setRole(context, "");
        SaveSharedPreference.setPassword(context, "");
        SaveSharedPreference.setPhoneNumber(context, "");
        SaveSharedPreference.setImage(context, "");
        SaveSharedPreference.setCV(context, "");
        SaveSharedPreference.setFavoriteCounter(context, "0");
        SaveSharedPreference.setPostsCounter(context, "0");
        SaveSharedPreference.setItemsCounter(context, "0");
        SaveSharedPreference.setFragment(context, "");
        SaveSharedPreference.setRating(context, "");
        SaveSharedPreference.setAbout(context, "");
        SaveSharedPreference.setCode(context, "");
        SaveSharedPreference.setPE(context, "");
        SaveSharedPreference.setPP(context, "");
        SaveSharedPreference.setSkills(context, "");
        loginNavHeaderTextView.setText("تسجيل الدخول");
        loginNavHeaderTextView.setTextColor(getResources().getColor(R.color.colorAccent));
        UserProfilePictureImageView.setImageDrawable(getResources().getDrawable(R.drawable.profiledefault));
        UserNameTextView.setText("");
        UserNameTextView.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }
}