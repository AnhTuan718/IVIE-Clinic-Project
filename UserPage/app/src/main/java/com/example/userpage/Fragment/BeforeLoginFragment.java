private void navigateToLogin() {
    try {
        if (!isAdded() || getActivity() == null) {
            Log.e(TAG, "Fragment is not attached to Activity");
            isNavigating = false;
            return;
        }

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        
        // Thêm try-catch cho startActivity
        try {
            startActivity(intent);
            // Reset navigation flag after successful navigation
            mainHandler.postDelayed(() -> isNavigating = false, 500);
        } catch (Exception e) {
            Log.e(TAG, "Error starting LoginActivity: " + e.getMessage());
            showError("Không thể mở trang đăng nhập");
            isNavigating = false;
        }
    } catch (Exception e) {
        Log.e(TAG, "Error in navigateToLogin: " + e.getMessage());
        showError("Có lỗi xảy ra, vui lòng thử lại");
        isNavigating = false;
    }
} 