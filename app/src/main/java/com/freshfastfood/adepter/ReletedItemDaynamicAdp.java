package com.freshfastfood.adepter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.freshfastfood.R;
import com.freshfastfood.database.DatabaseHelper;
import com.freshfastfood.database.MyCart;
import com.freshfastfood.model.ProductItem;
import com.freshfastfood.retrofit.APIClient;
import com.freshfastfood.utils.SessionManager;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.freshfastfood.utils.SessionManager.currncy;

public class ReletedItemDaynamicAdp extends RecyclerView.Adapter<ReletedItemDaynamicAdp.ViewHolder> {
    private List<ProductItem>  mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context mContext;
    DatabaseHelper helper;
    SessionManager sessionManager;
    public ReletedItemDaynamicAdp(Context context, List<ProductItem> data, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.mClickListener = itemClickListener;
        sessionManager=new SessionManager(mContext);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        helper = new DatabaseHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.releteditem_daynemic_custome, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.priceoofer.setPaintFlags(holder.priceoofer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ProductItem datum = mData.get(position);
        if (datum.getStock() == 0) {
            holder.lvlOutofstock.setVisibility(View.VISIBLE);
        } else {
            holder.lvlOutofstock.setVisibility(View.GONE);
        }
        Glide.with(mContext).load(APIClient.baseUrl + "/" + datum.getProductImage()).thumbnail(Glide.with(mContext).load(R.drawable.lodingimage)).into(holder.imgIcon);
        holder.txtTitle.setText("" + datum.getProductName());
        if (datum.getmDiscount() > 0) {
            double  res = (Double.parseDouble(datum.getPrice().get(0).getProductPrice()) / 100.0f)* datum.getmDiscount();
            res = Integer.parseInt(datum.getPrice().get(0).getProductPrice()) - res;
            holder.priceoofer.setText(sessionManager.getStringData(currncy)  + datum.getPrice().get(0).getProductPrice());
            holder.txtPrice.setText(sessionManager.getStringData(currncy)  + new DecimalFormat("##.##").format(res));
            holder.lvlOffer.setVisibility(View.VISIBLE);
            holder.txtOffer.setText(datum.getmDiscount() + "% Off");
        } else {
            holder.lvlOffer.setVisibility(View.GONE);
            holder.priceoofer.setVisibility(View.GONE);
            holder.txtPrice.setText(sessionManager.getStringData(currncy)  + datum.getPrice().get(0).getProductPrice());
        }
        int qrt = helper.getCard(datum.getId(), datum.getPrice().get(0).getProductPrice());
        if (qrt >= 1) {
            holder.lvlCardbg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_red_shape));
            holder.imgCard.setImageDrawable(mContext.getDrawable(R.drawable.ic_minus_rounded));
        } else {
            holder.lvlCardbg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_green_plus));
            holder.imgCard.setImageDrawable(mContext.getDrawable(R.drawable.ic_plus_rounded));
        }
        holder.lvlCardbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qrt = helper.getCard(datum.getId(), datum.getPrice().get(0).getProductPrice());
                if (qrt >= 1) {
                    helper.deleteRData(datum.getId(),datum.getPrice().get(0).getProductPrice());
                    holder.lvlCardbg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_green_plus));
                    holder.imgCard.setImageDrawable(mContext.getDrawable(R.drawable.ic_plus_rounded));
                } else {

                    MyCart myCart = new MyCart();
                    myCart.setPid(datum.getId());
                    myCart.setImage(datum.getProductImage());
                    myCart.setTitle(datum.getProductName());
                    myCart.setWeight(datum.getPrice().get(0).getProductType());
                    myCart.setCost(datum.getPrice().get(0).getProductPrice());
                    myCart.setQty("1");
                    myCart.setDiscount(datum.getmDiscount());
                    Log.e("INsert", "--> " + helper.insertData(myCart));
                    holder.lvlCardbg.setBackground(mContext.getResources().getDrawable(R.drawable.bg_red_shape));
                    holder.imgCard.setImageDrawable(mContext.getDrawable(R.drawable.ic_minus_rounded));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.txt_offer)
        TextView txtOffer;
        @BindView(R.id.price)
        TextView txtPrice;
        @BindView(R.id.priceoofer)
        TextView priceoofer;
        @BindView(R.id.lvl_offer)
        LinearLayout lvlOffer;
        @BindView(R.id.lvl_click)
        LinearLayout lvlClick;
        @BindView(R.id.img_icon)
        ImageView imgIcon;
        @BindView(R.id.lvl_cardbg)
        LinearLayout lvlCardbg;
        @BindView(R.id.img_card)
        ImageView imgCard;
        @BindView(R.id.lvl_outofstock)
        LinearLayout lvlOutofstock;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            lvlClick.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(mData.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(ProductItem productItem, int position);
    }
}