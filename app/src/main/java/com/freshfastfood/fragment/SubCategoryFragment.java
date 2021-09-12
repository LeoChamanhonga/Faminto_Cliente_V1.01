package com.freshfastfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freshfastfood.R;
import com.freshfastfood.activity.HomeActivity;
import com.freshfastfood.adepter.SubCategoryAdp;
import com.freshfastfood.model.SubCategory;
import com.freshfastfood.model.SubcatItem;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;

import static com.freshfastfood.activity.HomeActivity.homeActivity;

public class SubCategoryFragment extends Fragment implements GetResult.MyListener, SubCategoryAdp.RecyclerTouchListener {
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;
    @BindView(R.id.txt_notfound)
    TextView txtNotfound;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.txt_titel)
    TextView txtTitel;
    SubCategoryAdp adapter;
    List<SubcatItem> categoryList;
    Unbinder unbinder;
    SessionManager sessionManager;
    User user;
    int cid = 0;

    public SubCategoryFragment() {
        // Required empty public constructor
    }

    public static SubCategoryFragment newInstance(String param1, String param2) {
        SubCategoryFragment fragment = new SubCategoryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subcategory, container, false);
        unbinder = ButterKnife.bind(this, view);
        Bundle b = getArguments();
        cid = b.getInt("id");
        String titel = b.getString("titel");
        if (titel != null) {
            txtTitel.setText("" + titel);
        } else {
            txtTitel.setVisibility(View.GONE);
        }
        HomeActivity.getInstance().setFrameMargin(60);
        categoryList = new ArrayList<>();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails("");
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getSubCategory();
        return view;
    }

    @Override
    public void onClickItem(View v, int cid, int scid) {
        homeActivity.showMenu();
        Bundle args = new Bundle();
        args.putInt("cid", cid);
        args.putInt("scid", scid);
        Fragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        HomeActivity.getInstance().callFragment(fragment);
    }

    @Override
    public void onLongClickItem(View v, int position) {
    }

    private void getSubCategory() {
        HomeActivity.custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("category_id", cid);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getSubcategory((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            HomeActivity.custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1") && result.toString() != null) {
                Gson gson = new Gson();
                SubCategory category = gson.fromJson(result.toString(), SubCategory.class);
                if (category.getResult().equalsIgnoreCase("true")) {
                    categoryList = category.getData();
                    if (categoryList.size() != 0) {
                        adapter = new SubCategoryAdp(getActivity(), categoryList, this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        lvlNotfound.setVisibility(View.VISIBLE);
                        txtNotfound.setText("" + category.getResponseMsg());
                    }
                } else {
                    lvlNotfound.setVisibility(View.VISIBLE);
                    txtNotfound.setText("" + category.getResponseMsg());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().serchviewShow();
        HomeActivity.getInstance().setFrameMargin(60);
        if (user != null)
            HomeActivity.getInstance().titleChange("Hello " + user.getName());

    }
}
