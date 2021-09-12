package com.freshfastfood.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.freshfastfood.R;
import com.freshfastfood.database.DatabaseHelper;
import com.freshfastfood.fragment.AddressFragment;
import com.freshfastfood.fragment.CardFragment;
import com.freshfastfood.fragment.HomeFragment;
import com.freshfastfood.fragment.ItemListFragment;
import com.freshfastfood.fragment.MyOrderFragment;
import com.freshfastfood.fragment.OrderSumrryFragment;
import com.freshfastfood.model.User;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.android.material.appbar.AppBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.freshfastfood.fragment.ItemListFragment.itemListFragment;
import static com.freshfastfood.fragment.OrderSumrryFragment.isorder;
import static com.freshfastfood.utils.SessionManager.currncy;
import static com.freshfastfood.utils.SessionManager.login;
import static com.freshfastfood.utils.Utiles.isSelect;


public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.ed_search)
    EditText edSearch;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.img_cart)
    ImageView imgCart;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.lvl_actionsearch)
    LinearLayout lvlActionsearch;
    @BindView(R.id.lvl_home)
    LinearLayout lvlHome;
    @BindView(R.id.lvl_mainhome)
    LinearLayout lvlMainhome;
    @BindView(R.id.txt_actiontitle)
    TextView txtActiontitle;
    @BindView(R.id.txt_logintitel)
    TextView txtLogintitel;
    @BindView(R.id.fragment_frame)
    FrameLayout fragmentFrame;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.lvl_hidecart)
    LinearLayout lvlHidecart;
    @BindView(R.id.txt_mob)
    TextView txtMob;
    @BindView(R.id.txtfirstl)
    TextView txtfirstl;

    @BindView(R.id.txt_wallet)
    TextView txtWallet;
    @BindView(R.id.txt_email)
    TextView txtEmail;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.myprofile)
    LinearLayout myprofile;
    @BindView(R.id.myoder)
    LinearLayout myoder;
    @BindView(R.id.address)
    LinearLayout address;
    @BindView(R.id.feedback)
    LinearLayout feedback;
    @BindView(R.id.contect)
    LinearLayout contect;
    @BindView(R.id.logout)
    LinearLayout logout;
    @BindView(R.id.about)
    LinearLayout about;
    @BindView(R.id.tramscondition)
    LinearLayout tramscondition;
    @BindView(R.id.privecy)
    LinearLayout privecy;
    @BindView(R.id.termcondition)
    LinearLayout termcondition;
    @BindView(R.id.drawer)
    LinearLayout drawer;
    @BindView(R.id.rlt_cart)
    RelativeLayout rltCart;
    @BindView(R.id.rlt_noti)
    RelativeLayout rltNoti;
    @BindView(R.id.txt_countcard)
    TextView txtCountcard;
    @BindView(R.id.img_noti)
    ImageView imgNoti;
    User user;
    public static TextView txt_countcard;
    public static HomeActivity homeActivity = null;
    public static TextView txtNoti;
    public static CustPrograssbar custPrograssbar;
    Fragment fragment1 = null;
    DatabaseHelper databaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        txtNoti = findViewById(R.id.txt_noti);
        custPrograssbar = new CustPrograssbar();
        databaseHelper = new DatabaseHelper(HomeActivity.this);
        sessionManager = new SessionManager(HomeActivity.this);
        user = sessionManager.getUserDetails("");
        homeActivity = this;
        setDrawer();
    }

    public static HomeActivity getInstance() {
        return homeActivity;
    }

    public void showMenu() {
        rltNoti.setVisibility(View.GONE);
        rltCart.setVisibility(View.VISIBLE);
    }

    public void setFrameMargin(int top) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) lvlMainhome.getLayoutParams();
        params.setMargins(0, top, 0, 0);
        lvlMainhome.setLayoutParams(params);
    }

    public void setTxtWallet(String wallet) {
        if (sessionManager.getBooleanData(login)) {
            txtWallet.setVisibility(View.VISIBLE);
        }else {
            txtWallet.setVisibility(View.GONE);
        }
        txtWallet.setText(sessionManager.getStringData(currncy) + wallet);
    }

    @SuppressLint("SetTextI18n")
    private void setDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_icon);
        char first = user.getName().charAt(0);
        Log.e("first", "-->" + first);
        txtfirstl.setText("" + first);

        txtMob.setText("" + user.getMobile());
        txtEmail.setText("" + user.getEmail());

        titleChange();
        txt_countcard = findViewById(R.id.txt_countcard);
        Cursor res = databaseHelper.getAllData();
        if (res.getCount() == 0) {
            txt_countcard.setText("0");
        } else {
            txt_countcard.setText("" + res.getCount());
        }
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).addToBackStack("HomePage").commit();
        addTextWatcher();
        if (sessionManager.getBooleanData(login)) {
            txtLogintitel.setText("Logout");
        } else {
            txtLogintitel.setText("Login");

        }
    }

    public EditText passThisEditText() {
        return edSearch;
    }

    private void addTextWatcher() {
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edSearch.getText().toString().trim().length() == 0) {

                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
                    if (fragment instanceof HomeFragment && fragment.isVisible()) {
                        Log.e("no", "jsd");
                    } else {
                        getSupportFragmentManager().popBackStackImmediate();

                    }
                }
            }
        });
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edSearch.getText().toString().trim().length() != 0) {
                        Bundle args;
                        Fragment fragment;
                        args = new Bundle();
                        args.putInt("id", 0);
                        args.putString("search", edSearch.getText().toString().trim());
                        fragment = new ItemListFragment();
                        fragment.setArguments(args);
                        callFragment(fragment);
                    } else {
                        fragment1 = null;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
            if (fragment instanceof HomeFragment && fragment.isVisible()) {
                finish();
            } else if (fragment instanceof OrderSumrryFragment && fragment.isVisible() && isorder) {
                isorder = false;
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
            } else if (fragment instanceof MyOrderFragment && fragment.isVisible()) {
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
            }

            if (fragment instanceof OrderSumrryFragment && fragment.isVisible()) {
                edSearch.setText("");
                lvlActionsearch.setVisibility(View.GONE);
            } else if (fragment instanceof AddressFragment && fragment.isVisible()) {
                edSearch.setText("");
            } else {
                edSearch.setText("");
            }
            if (itemListFragment == null) {
                rltNoti.setVisibility(View.VISIBLE);
                rltCart.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setdata() {
        rltNoti.setVisibility(View.VISIBLE);
        rltCart.setVisibility(View.VISIBLE);
    }

    public void hideActionbar() {
        appBarLayout.setVisibility(View.GONE);
        lvlHidecart.setVisibility(View.GONE);
        drawer.setVisibility(View.GONE);
    }

    public void serchviewHide() {
        lvlActionsearch.setVisibility(View.GONE);
    }

    public void serchviewShow() {
        lvlActionsearch.setVisibility(View.VISIBLE);
    }

    public void titleChange(String s) {
        txtActiontitle.setText(s);
    }

    public void titleChange() {
        txtActiontitle.setText("Hello " + user.getName());
    }


    public void callFragment(Fragment fragmentClass) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragmentClass).addToBackStack("adds").commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @OnClick({R.id.img_close, R.id.myprofile, R.id.myoder, R.id.address, R.id.feedback, R.id.contect, R.id.logout, R.id.about, R.id.privecy, R.id.img_search, R.id.img_cart, R.id.img_noti, R.id.lvl_home, R.id.share, R.id.termcondition})
    public void onViewClicked(View view) {
        Fragment fragment;
        Bundle args;
        switch (view.getId()) {
            case R.id.img_close:
                break;
            case R.id.lvl_home:
                lvlActionsearch.setVisibility(View.VISIBLE);
                titleChange();
                edSearch.setText("");
                fragment = new HomeFragment();
                callFragment(fragment);
                break;
            case R.id.myprofile:
                titleChange();

                if (sessionManager.getBooleanData(login)) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
                break;
            case R.id.myoder:
                titleChange();

                if (sessionManager.getBooleanData(login)) {
                    lvlActionsearch.setVisibility(View.GONE);
                    fragment = new MyOrderFragment();
                    callFragment(fragment);
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
                break;
            case R.id.address:
                titleChange();

                if (sessionManager.getBooleanData(login)) {
                    lvlActionsearch.setVisibility(View.GONE);
                    isSelect = false;
                    fragment = new AddressFragment();
                    callFragment(fragment);
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
                break;
            case R.id.feedback:
                titleChange();

                if (sessionManager.getBooleanData(login)) {
                    startActivity(new Intent(HomeActivity.this, FeedBackActivity.class));

                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
                break;
            case R.id.contect:
                titleChange();

                startActivity(new Intent(HomeActivity.this, ContectusActivity.class));
                break;
            case R.id.logout:
                if (sessionManager.getBooleanData(login)) {
                    sessionManager.logoutUser();
                    databaseHelper.deleteCard();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
                break;
            case R.id.img_search:
                if (edSearch.getText().toString().trim().length() != 0) {
                    args = new Bundle();
                    args.putInt("id", 0);
                    args.putString("search", edSearch.getText().toString().trim());
                    fragment = new ItemListFragment();
                    fragment.setArguments(args);
                    callFragment(fragment);
                } else {
                    fragment1 = null;
                }
                break;
            case R.id.img_cart:
                txtActiontitle.setVisibility(View.VISIBLE);
                rltNoti.setVisibility(View.GONE);
                rltCart.setVisibility(View.VISIBLE);
                txtActiontitle.setText("MyCart");
                fragment = new CardFragment();
                callFragment(fragment);
                break;
            case R.id.img_noti:
                titleChange();

                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
                break;
            case R.id.share:
                shareApp();
                break;
            case R.id.about:
                titleChange();

                startActivity(new Intent(HomeActivity.this, AboutsActivity.class));
                break;
            case R.id.privecy:
                titleChange();

                startActivity(new Intent(HomeActivity.this, PrivecyPolicyActivity.class));
                break;
            case R.id.termcondition:
                titleChange();

                startActivity(new Intent(HomeActivity.this, TramsAndConditionActivity.class));
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public static void notificationCount(int i) {

        if (i <= 0) {
            txtNoti.setVisibility(View.GONE);
        } else {
            txtNoti.setVisibility(View.VISIBLE);
            txtNoti.setText("" + i);
        }
    }


}
