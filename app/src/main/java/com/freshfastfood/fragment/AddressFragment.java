package com.freshfastfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freshfastfood.R;
import com.freshfastfood.activity.AddressActivity;
import com.freshfastfood.activity.HomeActivity;
import com.freshfastfood.model.Address;
import com.freshfastfood.model.AddressData;
import com.freshfastfood.model.RestResponse;
import com.freshfastfood.model.User;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.retrofit.GetResult;
import com.freshfastfood.utils.CustPrograssbar;
import com.freshfastfood.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import static com.freshfastfood.utils.SessionManager.address1;
import static com.freshfastfood.utils.Utiles.isRef;
import static com.freshfastfood.utils.Utiles.isSelect;
import static com.freshfastfood.utils.Utiles.seletAddress;

public class AddressFragment extends Fragment implements GetResult.MyListener {
    @BindView(R.id.txt_notfound)
    TextView txtNotfound;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;
    @BindView(R.id.recycle_address)
    RecyclerView recycleAddress;
    @BindView(R.id.btn_addaddress)
    TextView btnAddaddress;
    User user;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    int positionAdd;
    List<Address> addressList;
    SelectAdrsAdapter adapter;

    public static AddressFragment newInstance() {
        AddressFragment fragment = new AddressFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        ButterKnife.bind(this, view);
        user = new User();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails("");
        custPrograssbar = new CustPrograssbar();
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
        recycleAddress.setLayoutManager(recyclerLayoutManager);
        getAddress();
        return view;
    }

    @OnClick(R.id.btn_addaddress)
    public void onViewClicked() {
        isRef = false;
        startActivity(new Intent(getActivity(), AddressActivity.class));
    }

    public class SelectAdrsAdapter extends
            RecyclerView.Adapter<SelectAdrsAdapter.ViewHolder> {
        private List<Address> addressList;

        private int lastSelectedPosition = -1;

        public SelectAdrsAdapter(List<Address> addressList) {
            this.addressList = addressList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selectaddress_item, parent, false);
            ViewHolder viewHolder =
                    new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            Address address = addressList.get(position);
            holder.offerSelect.setChecked(lastSelectedPosition == position);
            holder.txtAddressful.setText(address.getHno() + "," + address.getSociety() + "," + address.getArea() + "," + address.getLandmark() + "," + address.getName());
            holder.txtAddressname.setText("" + address.getType());
            if (!isSelect) {
                holder.offerSelect.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.offer_select)
            RadioButton offerSelect;
            @BindView(R.id.txt_addressname)
            TextView txtAddressname;
            @BindView(R.id.txt_addressful)
            TextView txtAddressful;
            @BindView(R.id.txt_change)
            ImageView txtChange;
            @BindView(R.id.txt_delete)
            ImageView txtDelete;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                offerSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedPosition = getAdapterPosition();
                        seletAddress = lastSelectedPosition;
                        sessionManager.setAddress(address1, addressList.get(getAdapterPosition()));
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
                txtDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        lastSelectedPosition = getAdapterPosition();
                        positionAdd = lastSelectedPosition;
                        deleteAddress(addressList.get(lastSelectedPosition).getId());
                    }
                });
                txtChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), AddressActivity.class).putExtra("MyClass", addressList.get(getAdapterPosition())));
                    }
                });
            }
        }
    }

     void deleteAddress(String aid) {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("aid", aid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().deleteAddress((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAddress() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getAddress((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        custPrograssbar.closePrograssBar();
        if (callNo.equalsIgnoreCase("address")) {
            Gson gson = new Gson();
            try {
                AddressData addressData = gson.fromJson(result.toString(), AddressData.class);
                if (addressData.getResult().equalsIgnoreCase("true")) {
                    if (addressData.getResultData().size() != 0) {
                        lvlNotfound.setVisibility(View.GONE);

                        addressList = addressData.getResultData();
                        adapter = new SelectAdrsAdapter(addressList);
                        recycleAddress.setAdapter(adapter);
                    }
                } else {
                    lvlNotfound.setVisibility(View.VISIBLE);
                    txtNotfound.setText("" + addressData.getResponseMsg());
                }
            } catch (Exception e) {
                e.toString();
            }
        } else if (callNo.equalsIgnoreCase("2")) {
            try {
                Gson gson = new Gson();
                RestResponse restResponse = gson.fromJson(result.toString(), RestResponse.class);
                Toast.makeText(getActivity(), restResponse.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (restResponse.getResult().equalsIgnoreCase("true")) {
                    addressList.remove(positionAdd);
                    if(addressList.size()==0){
                        lvlNotfound.setVisibility(View.VISIBLE);
                        txtNotfound.setText("Address Not Found!!");
                    }
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.toString();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().serchviewHide();
        HomeActivity.getInstance().setFrameMargin(0);
        if (isRef) {
            isRef = false;
            getAddress();
        }
    }
}
