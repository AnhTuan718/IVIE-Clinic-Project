package com.example.userpage.Shopping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.example.userpage.Adapter.PhotoAdapter;
import com.example.userpage.Adapter.ProductAdapter;
import com.example.userpage.Model.Photo;
import com.example.userpage.Model.Product;
import com.example.userpage.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProductListActivity extends AppCompatActivity {
    private GridView productGrid;
    private ProductAdapter adapter;
    private List<Product> productList;

    private EditText searchBar;

    private ViewPager2 viewPager;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnable;

    private static final int AUTO_SLIDE_DELAY = 3000; //3 giay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        // Khởi tạo toolbar
        ImageButton backButton = findViewById(R.id.backButton);
        EditText searchBar = findViewById(R.id.searchBar);
        ImageButton cartButton = findViewById(R.id.cartButton);

        // Khởi tạo ViewPager2
        viewPager = findViewById(R.id.viewPager);
        PhotoAdapter photoAdapter = new PhotoAdapter(this, getListPhoto());
        viewPager.setAdapter(photoAdapter);
        setupAutoSlide();


        // Khởi tạo GridView
        productGrid = findViewById(R.id.productGrid);
        productList = new ArrayList<>();

        // Thêm dữ liệu mẫu
        productList.add(new Product("Ambroxol ống", 5.000,R.drawable.ambroxol));
        productList.add(new Product("Enevon c vỉ", 30.000, R.drawable.enervon));
        productList.add(new Product("LiveSpo NAVAX kids bình xịt", 185.000, R.drawable.livespo_navax_kids));
        productList.add(new Product("Kem bôi ngoài da Towders Cream", 75.000, R.drawable.towders_cream));
        productList.add(new Product("Chai xịt ngoài da Towders Spray 100ml", 185.000, R.drawable.towder_spray));
        productList.add(new Product("Durapil", 725.000, R.drawable.durapil_30));
        productList.add(new Product("Lipitor 20 mg", 300.000, R.drawable.lipitor_20_mg));
        productList.add(new Product("Calcium corbiere extra", 190.000, R.drawable.calcium_corbiere));
        productList.add(new Product("Dung dịch nhỏ mắt Solution hộp 10ml", 72.000, R.drawable.solution_eyes));


        // Khởi tạo adapter và gán cho GridView
        adapter = new ProductAdapter(this, productList);
        productGrid.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn vào sản phẩm
        productGrid.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product",productList.get(position));
            startActivity(intent);
        });

        // Xử lý sự kiện cho nút back
        backButton.setOnClickListener(v -> finish());

        cartButton.setOnClickListener(v -> {
            // Xử lý khi nhấn nút giỏ hàng
            Toast.makeText(this, "Mở giỏ hàng", Toast.LENGTH_SHORT).show();
        });
        // Xử lý sự kiện khi người dùng nhập vào ô tìm kiếm
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString());
            }
        });
    }
    // Hàm tự động chuyển slide
    private void setupAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int totalItems = viewPager.getAdapter().getItemCount();
                if (currentItem < totalItems - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    viewPager.setCurrentItem(0); // Quay lại slide đầu tiên
                }
                handler.postDelayed(this, AUTO_SLIDE_DELAY);
            }
        };
        handler.postDelayed(runnable, AUTO_SLIDE_DELAY); // Bắt đầu tự động chuyển slide
    }
    // Hàm lấy danh sách ảnh quảng cáo
    private List<Photo> getListPhoto() {
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.quangcao_omron));
        list.add(new Photo(R.drawable.quangcao_samngoclinh));
        return list;
    }
    //Hàm lọc sản phẩm dựa trên tên
    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        String normalizedQuery = normalizeString(query).toLowerCase().trim();

        for (Product product : productList) {
            String normalizedProductName = normalizeString(product.getProductName()).toLowerCase();
            if (normalizedProductName.contains(normalizedQuery)) {
                filteredList.add(product);
            }
        }
        adapter = new ProductAdapter(this, filteredList);
        productGrid.setAdapter(adapter);
    }
    // Hàm chuẩn hóa chuỗi tiếng Việt (loại bỏ dấu)
    private String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
