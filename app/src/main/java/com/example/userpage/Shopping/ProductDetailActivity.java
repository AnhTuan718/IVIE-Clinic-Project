package com.example.userpage.Shopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.userpage.Model.Product;
import com.example.userpage.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView productImage;
    private TextView productName, productPrice, productDescription, quantityText;
    private ImageButton backButton, cartButton;
    private ImageButton increaseButton, decreaseButton;
    private Button addToCartButton, buyNowButton;
    private SharedPreferences sharedPreferences;
    private static final String CART_PREFS = "CartPrefs";
    private static final String CART_KEY = "Cart";
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(CART_PREFS, MODE_PRIVATE);

        // Khởi tạo các view
        backButton = findViewById(R.id.backButton);
        cartButton = findViewById(R.id.cartButton);
        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        quantityText = findViewById(R.id.quantityText);
        increaseButton = findViewById(R.id.increaseButton);
        decreaseButton = findViewById(R.id.decreaseButton);
        addToCartButton = findViewById(R.id.addToCartButton);
        buyNowButton = findViewById(R.id.buyNowButton);

        // Nhận dữ liệu từ Intent
        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            productName.setText(product.getProductName());
            productPrice.setText(String.format("%,.0f VNĐ", product.getPrice()));
            productImage.setImageResource(product.getImageResource());
            productDescription.setText("Đây là mô tả chi tiết cho sản phẩm " + product.getProductName() + ". Sản phẩm chất lượng cao, phù hợp với nhu cầu sử dụng hàng ngày.");
        }

        // Sự kiện nút quay lại
        backButton.setOnClickListener(v -> finish());

        // Sự kiện nút giỏ hàng
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Sự kiện tăng số lượng
        increaseButton.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        // Sự kiện giảm số lượng
        decreaseButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });

        // Sự kiện nút thêm vào giỏ hàng
        addToCartButton.setOnClickListener(v -> {
            addToCart(product);
            Toast.makeText(this, "Đã thêm " + quantity + " " + product.getProductName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
            // Tùy chọn: Chuyển đến CartActivity sau khi thêm
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Sự kiện nút mua ngay
        buyNowButton.setOnClickListener(v -> {
            addToCart(product);
            Toast.makeText(this, "Đã thêm " + quantity + " " + product.getProductName() + " và chuẩn bị thanh toán", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent); // Chuyển đến giỏ hàng để thanh toán
        });
    }

    private void addToCart(Product product) {
        String cartJson = sharedPreferences.getString(CART_KEY, "[]");
        JSONArray cartArray;
        try {
            cartArray = new JSONArray(cartJson);
            System.out.println("Current cart JSON: " + cartJson);
        } catch (JSONException e) {
            cartArray = new JSONArray();
        }

        boolean productExists = false;
        for (int i = 0; i < cartArray.length(); i++) {
            try {
                JSONObject item = cartArray.getJSONObject(i);
                if (item.getString("productName").equals(product.getProductName())) {
                    int existingQuantity = item.getInt("quantity");
                    item.put("quantity", existingQuantity + quantity);
                    productExists = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!productExists) {
            try {
                JSONObject newItem = new JSONObject();
                newItem.put("productName", product.getProductName());
                newItem.put("price", product.getPrice());
                newItem.put("quantity", quantity);
                newItem.put("imageResource", product.getImageResource());
                cartArray.put(newItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CART_KEY, cartArray.toString());
        editor.apply();
        System.out.println("New cart JSON: " + cartArray.toString());
    }
}