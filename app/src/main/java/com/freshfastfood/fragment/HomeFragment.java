package com.freshfastfood.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.freshfastfood.R;
import com.freshfastfood.activity.HomeActivity;
import com.freshfastfood.activity.ItemDetailsActivity;
import com.freshfastfood.adepter.CategoryAdp;
import com.freshfastfood.adepter.ReletedItemAdp;
import com.freshfastfood.adepter.ReletedItemDaynamicAdp;
import com.freshfastfood.database.DatabaseHelper;
import com.freshfastfood.model.BannerItem;
import com.freshfastfood.model.CatItem;
import com.freshfastfood.model.DynamicData;
import com.freshfastfood.model.Home;
import com.freshfastfood.model.ProductItem;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.AutoScrollViewPager;
import com.freshfastfood.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

import static com.freshfastfood.activity.HomeActivity.homeActivity;
import static com.freshfastfood.activity.HomeActivity.txtNoti;
import static com.freshfastfood.utils.SessionManager.aboutUs;
import static com.freshfastfood.utils.SessionManager.contactUs;
import static com.freshfastfood.utils.SessionManager.currncy;
import static com.freshfastfood.utils.SessionManager.iscart;
import static com.freshfastfood.utils.SessionManager.oMin;
import static com.freshfastfood.utils.SessionManager.privacy;
import static com.freshfastfood.utils.SessionManager.razKey;
import static com.freshfastfood.utils.SessionManager.tax;
import static com.freshfastfood.utils.SessionManager.tremcodition;
import static com.freshfastfood.utils.Utiles.productItems;


public class HomeFragment extends Fragment implements CategoryAdp.RecyclerTouchListener, ReletedItemAdp.ItemClickListener, GetResult.MyListener, ReletedItemDaynamicAdp.ItemClickListener {
    @BindView(R.id.viewPager)
    AutoScrollViewPager viewPager;
    @BindView(R.id.tabview)
    TabLayout tabview;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.recycler_releted)
    RecyclerView recyclerReleted;
    @BindView(R.id.lvl_selected)
    LinearLayout lvlSelected;
    Unbinder unbinder;
    @BindView(R.id.scl_main)
    ScrollView sclMain;
    @BindView(R.id.lvl_mantanasmode)
    LinearLayout lvlMantanasmode;
    private Context mContext;
    CategoryAdp adapter;
    ReletedItemAdp adapterReletedi;
    List<CatItem> categoryList;
    List<BannerItem> bannerDatumList;
    public HomeFragment homeListFragment;
    SessionManager sessionManager;
    User user;
    List<DynamicData> dynamicDataList = new ArrayList<>();
    ReletedItemAdp reletedItemAdp;
    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        bannerDatumList = new ArrayList<>();
        sessionManager = new SessionManager(mContext);
        databaseHelper = new DatabaseHelper(getActivity());

        homeListFragment = this;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(mContext);
        mLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerReleted.setLayoutManager(mLayoutManager1);
        categoryList = new ArrayList<>();
        adapter = new CategoryAdp(mContext, categoryList, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapterReletedi = new ReletedItemAdp(mContext, productItems, this);
        recyclerReleted.setItemAnimator(new DefaultItemAnimator());
        recyclerReleted.setAdapter(adapterReletedi);
        user = sessionManager.getUserDetails("");
        getHome();
        return view;
    }

    private void setJoinPlayrList(LinearLayout lnrView, List<DynamicData> dataList) {

        lnrView.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_home_item, null);
            TextView itemTitle = view.findViewById(R.id.itemTitle);
            RecyclerView recycler_view_list = view.findViewById(R.id.recycler_view_list);
            itemTitle.setText(dataList.get(i).getTitle());
            ReletedItemDaynamicAdp itemAdp = new ReletedItemDaynamicAdp(mContext, dataList.get(i).getDynamicItems(), this);
            recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recycler_view_list.setAdapter(itemAdp);
            lnrView.addView(view);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClickItem(String titel, int position) {
        homeActivity.showMenu();
        Bundle args = new Bundle();
        args.putInt("id", position);
        args.putString("titel", titel);
        Fragment fragment = new SubCategoryFragment();
        fragment.setArguments(args);
        HomeActivity.getInstance().callFragment(fragment);
    }

    @Override
    public void onLongClickItem(View v, int position) {
        Log.e("posiotn", "" + position);
    }

    @Override
    public void onItemClick(ProductItem productItem, int position) {
        mContext.startActivity(new Intent(mContext, ItemDetailsActivity.class).putExtra("MyClass", productItem).putParcelableArrayListExtra("MyList", productItem.getPrice()));
    }

    @OnClick({R.id.txt_viewll, R.id.txt_viewllproduct})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_viewll:
                CategoryFragment fragment = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("arraylist", (Serializable) categoryList);
                fragment.setArguments(bundle);
                HomeActivity.getInstance().callFragment(fragment);
                break;
            case R.id.txt_viewllproduct:
                PopularFragment fragmentp = new PopularFragment();
                HomeActivity.getInstance().callFragment(fragmentp);
                break;
            default:

                break;
        }
    }


    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        List<BannerItem> bannerDatumList;
        LayoutInflater layoutInflater;

        public MyCustomPagerAdapter(Context context, List<BannerItem> bannerDatumList) {
            this.context = context;
            this.bannerDatumList = bannerDatumList;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return bannerDatumList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.item_banner, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Glide.with(mContext).load(APIClient.baseUrl + "/" + bannerDatumList.get(position).getBimg()).placeholder(R.drawable.empty).into(imageView);
            container.addView(itemView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bannerDatumList.get(position).getCid().equalsIgnoreCase("0")) {
                        homeActivity.showMenu();
                        Bundle args = new Bundle();
                        args.putInt("id", Integer.parseInt(bannerDatumList.get(position).getCid()));
                        Fragment fragment = new SubCategoryFragment();
                        fragment.setArguments(args);
                        HomeActivity.getInstance().callFragment(fragment);
                    }
                }
            });
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private void getHome() {
        HomeActivity.custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getHome((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "homepage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().setdata();
        HomeActivity.getInstance().setFrameMargin(60);
        HomeActivity.getInstance().serchviewShow();
        if (user != null)
            HomeActivity.getInstance().titleChange("Hello " + user.getName());

        if (dynamicDataList != null) {
            setJoinPlayrList(lvlSelected, dynamicDataList);
        }
        if (reletedItemAdp != null) {
            reletedItemAdp.notifyDataSetChanged();
        }
        if (iscart) {
            iscart = false;
            CardFragment fragment = new CardFragment();
            HomeActivity.getInstance().callFragment(fragment);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("homepage")) {
                HomeActivity.custPrograssbar.closePrograssBar();
                bannerDatumList = new ArrayList<>();
                categoryList = new ArrayList<>();
                Gson gson = new Gson();
                Home home = gson.fromJson(result.toString(), Home.class);
                if (home.getResultHome().getMainData().getMaintaince() == 1) {
                    sclMain.setVisibility(View.GONE);
                    lvlMantanasmode.setVisibility(View.VISIBLE);
                    databaseHelper.deleteCard();
                    HomeActivity.getInstance().hideActionbar();
                    return;

                }
                categoryList.addAll(home.getResultHome().getCatItems());
                adapter = new CategoryAdp(mContext, categoryList, this);
                recyclerView.setAdapter(adapter);

                bannerDatumList.addAll(home.getResultHome().getBannerItems());
                MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(mContext, bannerDatumList);
                viewPager.setAdapter(myCustomPagerAdapter);
                viewPager.startAutoScroll();
                viewPager.setInterval(3000);
                viewPager.setCycle(true);
                viewPager.setStopScrollWhenTouch(true);
                tabview.setupWithViewPager(viewPager, true);

                reletedItemAdp = new ReletedItemAdp(mContext, home.getResultHome().getProductItems(), this);
                recyclerReleted.setAdapter(reletedItemAdp);
                if (home.getResultHome().getRemainNotification() <= 0) {
                    txtNoti.setVisibility(View.GONE);
                } else {
                    txtNoti.setVisibility(View.VISIBLE);
                    txtNoti.setText("" + home.getResultHome().getRemainNotification());
                }
                sessionManager.setStringData(currncy, home.getResultHome().getMainData().getCurrency());
                sessionManager.setStringData(privacy, home.getResultHome().getMainData().getPrivacyPolicy());
                sessionManager.setStringData(aboutUs, home.getResultHome().getMainData().getAboutUs());
                sessionManager.setStringData(contactUs, home.getResultHome().getMainData().getContactUs());
                sessionManager.setStringData(tremcodition, home.getResultHome().getMainData().getTerms());
                sessionManager.setIntData(oMin, home.getResultHome().getMainData().getoMin());
                sessionManager.setStringData(razKey, home.getResultHome().getMainData().getRazKey());
                sessionManager.setStringData(tax, home.getResultHome().getMainData().getTax());
                HomeActivity.getInstance().setTxtWallet(home.getResultHome().getWallet()+"");
                productItems = home.getResultHome().getProductItems();
                dynamicDataList = home.getResultHome().getDynamicData();
                setJoinPlayrList(lvlSelected, dynamicDataList);
            }

        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
