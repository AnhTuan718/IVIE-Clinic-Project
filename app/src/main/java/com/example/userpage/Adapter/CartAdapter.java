package com.example.userpage.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Model.CartItem;
import com.example.userpage.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private SharedPreferences sharedPreferences;
    private static final String CART_PREFS = "CartPrefs";
    private static final String CART_KEY = "Cart";
    private OnCartUpdateListener onCartUpdateListener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartUpdateListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        this.onCartUpdateListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.cartItemName.setText(cartItem.getProductName());
        holder.cartItemPrice.setText(String.format("%,d VNĐ", cartItem.getPrice()));
        holder.quantityText.setText(String.valueOf(cartItem.getQuantity()));
        holder.cartItemImage.setImageResource(cartItem.getImageResource());

        // Xử lý tăng giảm số lượng
        holder.increaseButton.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            updateCartInPrefs();
            notifyDataSetChanged();
            onCartUpdateListener.onCartUpdated();
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                updateCartInPrefs();
                notifyDataSetChanged();
                onCartUpdateListener.onCartUpdated();
            }
        });

        // Xử lý nút xóa sản phẩm
        holder.deleteButton.setOnClickListener(v -> {
            removeItem(position);
            Toast.makeText(context, "Đã xóa " + cartItem.getProductName() + " khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
    // Phương thức xóa item
    private void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            updateCartInPrefs();
            onCartUpdateListener.onCartUpdated();
        }
    }

    private void updateCartInPrefs() {
        JSONArray cartArray = new JSONArray();
        for (CartItem item : cartItems) {
            try {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("productName", item.getProductName());
                jsonItem.put("price", item.getPrice());
                jsonItem.put("quantity", item.getQuantity());
                jsonItem.put("imageResource", item.getImageResource());
                cartArray.put(jsonItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CART_KEY, cartArray.toString());
        editor.apply();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImage;
        TextView cartItemName, cartItemPrice, quantityText;
        ImageButton increaseButton, decreaseButton;
        ImageButton deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.cartItemImage);
            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemPrice = itemView.findViewById(R.id.cartItemPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}


