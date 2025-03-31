package com.example.userpage.Shopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Adapter.CartAdapter;
import com.example.userpage.Model.CartItem;
import com.example.userpage.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartUpdateListener {
    private RecyclerView cartRecyclerView;
    private TextView totalPriceText;
    private Button checkoutButton;
    private ImageButton backButton;
    private SharedPreferences sharedPreferences;
    private static final String CART_PREFS = "CartPrefs";
    // Use the same key as in ProductDetailActivity
    private static final String CART_KEY = "Cart";
    private List<CartItem> cartItems;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(CART_PREFS, MODE_PRIVATE);

        // Khởi tạo các view
        backButton = findViewById(R.id.backButton);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        checkoutButton = findViewById(R.id.checkoutButton);

        // Thiết lập RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = getCartItems();
        cartAdapter = new CartAdapter(this, cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);

        // Cập nhật tổng tiền
        updateTotalPrice();

        // Xử lý nút quay lại
        backButton.setOnClickListener(v -> finish());

        // Xử lý nút thanh toán
        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
            // Xóa giỏ hàng sau khi thanh toán (tùy chọn)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CART_KEY, "[]");
            editor.apply();
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh cart items when activity resumes
        refreshCartItems();
    }

    private void refreshCartItems() {
        cartItems.clear();
        cartItems.addAll(getCartItems());
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }
    private List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        String cartJson = sharedPreferences.getString(CART_KEY, "[]");
        try {
            JSONArray cartArray = new JSONArray(cartJson);
            for (int i = 0; i < cartArray.length(); i++) {
                JSONObject item = cartArray.getJSONObject(i);
                String name = item.getString("productName");
                double priceDouble = item.getDouble("price"); // Handle as double since Product uses double
                int price = (int) priceDouble; // Convert to int for CartItem
                int quantity = item.getInt("quantity");
                int imageResource = item.getInt("imageResource");
                items.add(new CartItem(name, price, quantity, imageResource));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void updateTotalPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText(String.format("Tổng tiền: %,d VNĐ", total));
    }

    @Override
    public void onCartUpdated() {
        updateTotalPrice();
    }
}