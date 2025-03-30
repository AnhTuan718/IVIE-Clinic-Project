public void navigateToLogin() {
    try {
        if (isFinishing() || isDestroyed()) {
            Log.e("NavigationActivity", "Activity is finishing or destroyed");
            return;
        }

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        // Đảm bảo chạy trên main thread
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.e("NavigationActivity", "Error starting LoginActivity: " + e.getMessage());
                showError("Không thể mở trang đăng nhập");
            }
        });
    } catch (Exception e) {
        Log.e("NavigationActivity", "Error preparing login navigation: " + e.getMessage());
        showError("Không thể mở trang đăng nhập");
    }
}

doctorsRef = FirebaseDatabase.getInstance().getReference().child("doctors");