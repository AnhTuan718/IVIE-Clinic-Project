package com.example.userpage.Shopping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.userpage.Adapter.PhotoAdapter;
import com.example.userpage.Model.Photo;
import com.example.userpage.Model.Product;
import com.example.userpage.R;
import com.example.userpage.Shopping.CartActivity;
import com.example.userpage.Shopping.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment {

    private ViewPager2 viewPager;
    private EditText searchBar;
    private ImageButton cartButton;
    private GridView productGrid;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private static final int AUTO_SLIDE_DELAY = 3000;
    private List<Product> fullProductList; // Danh sách sản phẩm đầy đủ
    private ProductAdapter productAdapter; // Adapter để cập nhật GridView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        // Initialize views
        viewPager = view.findViewById(R.id.viewPager);
        searchBar = view.findViewById(R.id.searchBar);
        cartButton = view.findViewById(R.id.cartButton);
        productGrid = view.findViewById(R.id.productGrid);

        // Setup ViewPager2
        PhotoAdapter photoAdapter = new PhotoAdapter(getActivity(), getListPhoto());
        viewPager.setAdapter(photoAdapter);
        setupAutoSlide();

        // Cart button listener
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });

        // Setup GridView with product items
        fullProductList = getListProducts(); // Lưu danh sách đầy đủ
        productAdapter = new ProductAdapter(fullProductList);
        productGrid.setAdapter(productAdapter);

        // Add click listener for GridView items
        productGrid.setOnItemClickListener((parent, view1, position, id) -> {
            Product selectedProduct = productAdapter.getItem(position); // Lấy sản phẩm từ adapter
            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
            intent.putExtra("product", selectedProduct);
            startActivity(intent);
        });

        // Add search functionality to searchBar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không cần xử lý
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString()); // Lọc sản phẩm khi văn bản thay đổi
            }
        });

        return view;
    }

    private void setupAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int totalItem = viewPager.getAdapter().getItemCount();
                if (currentItem < totalItem - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    viewPager.setCurrentItem(0);
                }
                handler.postDelayed(this, AUTO_SLIDE_DELAY);
            }
        };
        handler.postDelayed(runnable, AUTO_SLIDE_DELAY);
    }

    private List<Photo> getListPhoto() {
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.quang_cao_ivie2));
        list.add(new Photo(R.drawable.quang_cao_ivie));
        return list;
    }

    private List<Product> getListProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("Medicine flu", 50000.0, R.drawable.medicine_flu));
        productList.add(new Product("Medical mask", 30000.0, R.drawable.mask));
        productList.add(new Product("Blood pressure monitor", 1500000.0, R.drawable.blood_pressure_monitor));
        productList.add(new Product("Vitamin C", 120000.0, R.drawable.vitamin_c));
        return productList;
    }

    // Hàm lọc sản phẩm dựa trên từ khóa tìm kiếm
    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        query = query.toLowerCase().trim(); // Chuyển về chữ thường và loại bỏ khoảng trắng thừa

        if (query.isEmpty()) {
            filteredList.addAll(fullProductList); // Nếu không có từ khóa, hiển thị toàn bộ danh sách
        } else {
            for (Product product : fullProductList) {
                if (product.getProductName().toLowerCase().contains(query)) {
                    filteredList.add(product); // Thêm sản phẩm khớp với từ khóa
                }
            }
        }

        productAdapter.updateList(filteredList); // Cập nhật danh sách trong adapter
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }

    // ProductAdapter for GridView
    private class ProductAdapter extends BaseAdapter {
        private List<Product> products;

        public ProductAdapter(List<Product> products) {
            this.products = new ArrayList<>(products); // Sao chép danh sách ban đầu
        }

        // Phương thức cập nhật danh sách sản phẩm
        public void updateList(List<Product> newList) {
            products.clear();
            products.addAll(newList);
            notifyDataSetChanged(); // Thông báo adapter cập nhật giao diện
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Product getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
            }

            Product product = products.get(position);

            ImageView productImage = convertView.findViewById(R.id.productImage);
            TextView productName = convertView.findViewById(R.id.productName);
            TextView productPrice = convertView.findViewById(R.id.productPrice);

            productImage.setImageResource(product.getImageResource());
            productName.setText(product.getProductName());
            productPrice.setText(String.format("%,.0f VNĐ", product.getPrice()));

            return convertView;
        }
    }
}