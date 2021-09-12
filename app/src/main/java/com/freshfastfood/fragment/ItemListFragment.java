package com.freshfastfood.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.freshfastfood.R;
import com.freshfastfood.activity.HomeActivity;
import com.freshfastfood.adepter.ItemAdp;
import com.freshfastfood.database.DatabaseHelper;
import com.freshfastfood.database.MyCart;
import com.freshfastfood.model.Product;
import com.freshfastfood.model.ProductItem;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

import static com.freshfastfood.utils.SessionManager.currncy;
import static com.freshfastfood.utils.SessionManager.iscart;

public class ItemListFragment extends Fragment implements GetResult.MyListener {
    @BindView(R.id.lvlbacket)
    LinearLayout lvlbacket;
    @BindView(R.id.txt_item)
    TextView txtItem;
    @BindView(R.id.txt_notfound)
    TextView txtNotfound;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;
    @BindView(R.id.txt_price)
    TextView txtPrice;
    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    Unbinder unbinder;
    ItemAdp itemAdp;
    List<ProductItem> productDataList;
    int cid = 0;
    int scid = 0;
    SessionManager sessionManager;
     StaggeredGridLayoutManager gridLayoutManager;
    public static ItemListFragment itemListFragment;

    public static ItemListFragment getInstance() {
        return itemListFragment;
    }

    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        HomeActivity.getInstance().setFrameMargin(60);
        Bundle b = getArguments();
        databaseHelper = new DatabaseHelper(getActivity());
        itemListFragment = this;
        sessionManager = new SessionManager(getActivity());
        cid = b.getInt("cid");
        scid = b.getInt("scid");
        myRecyclerView.setHasFixedSize(true);
        productDataList = new ArrayList<>();
        gridLayoutManager = new StaggeredGridLayoutManager(1, 1);
        myRecyclerView.setLayoutManager(gridLayoutManager);
        itemAdp = new ItemAdp(getActivity(), productDataList);
        myRecyclerView.setAdapter(itemAdp);
        if (cid == 0) {
            String keyword = b.getString("search");
            if (keyword.trim().length() != 0) {
                getSearch(keyword);
            } else {
                getFragmentManager().popBackStackImmediate();
            }
        } else {
            getProduct();
        }
        Cursor res = databaseHelper.getAllData();
        if (res.getCount() == 0) {
            lvlbacket.setVisibility(View.GONE);
        } else {
            lvlbacket.setVisibility(View.VISIBLE);
            updateItem();
        }
        return view;
    }

    public void updateItem() {
        try {
            Cursor res = databaseHelper.getAllData();
            if (res.getCount() == 0) {
                lvlbacket.setVisibility(View.GONE);
            } else {
                lvlbacket.setVisibility(View.VISIBLE);

                double totalRs = 0;
                double ress = 0;
                int totalItem = 0;
                while (res.moveToNext()) {
                    MyCart rModel = new MyCart();
                    rModel.setCost(res.getString(5));
                    rModel.setQty(res.getString(6));
                    rModel.setDiscount(res.getInt(7));
                    ress = (Double.parseDouble(res.getString(5)) * rModel.getDiscount()) / 100;
                    ress = Double.parseDouble(res.getString(5)) - ress;
                    double temp = Double.parseDouble(res.getString(6)) * ress;
                    totalItem = totalItem + Integer.parseInt(res.getString(6));
                    totalRs = totalRs + temp;
                }

                txtItem.setText(totalItem + " Items");
                txtPrice.setText(sessionManager.getStringData(currncy) + new DecimalFormat("##.##").format(totalRs));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getProduct() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cid", cid);
            jsonObject.put("sid", scid);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getGetProduct((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
            HomeActivity.custPrograssbar.prograssCreate(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSearch(String key) {
        Log.e("searchKey===", key + "");

        HomeActivity.custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyword", key);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getSearch((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        HomeActivity.custPrograssbar.closePrograssBar();
        JSONObject jsonObject;
        try {
            Gson gson = new Gson();
            jsonObject = new JSONObject(result.toString());
            Product product = gson.fromJson(jsonObject.toString(), Product.class);
            if (product.getResult().equals("true")) {
                productDataList.clear();
                productDataList.addAll(product.getData());
                lvlNotfound.setVisibility(View.GONE);

            } else {
                lvlbacket.setVisibility(View.GONE);
                lvlNotfound.setVisibility(View.VISIBLE);
                txtNotfound.setText("" + product.getResponseMsg());
            }
            itemAdp.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.img_cart);
        if (item != null)
            item.setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().serchviewShow();
        HomeActivity.getInstance().setFrameMargin(60);
        if (iscart) {
            iscart = false;
            CardFragment fragment = new CardFragment();
            HomeActivity.getInstance().callFragment(fragment);
        } else if (itemAdp != null) {
            itemAdp.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.txt_gocart)
    public void onViewClicked() {
        CardFragment fragment = new CardFragment();
        HomeActivity.getInstance().callFragment(fragment);
    }
}
