package com.example.bill;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BillPrintApp";
    private BillCanvasView billView;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo handler cho main thread
        mainHandler = new Handler(Looper.getMainLooper());

        try {
            // Tạo và hiển thị bill view
            billView = new BillCanvasView(this);
            setContentView(billView);

            // Đợi view ready rồi in
            schedulePrintJob();

        } catch (Exception e) {
            Log.e(TAG, "Khởi tạo thất bại: " + e.getMessage());
            showToast("Không thể khởi tạo ứng dụng");
            finish();
        }
    }

    private void schedulePrintJob() {
        billView.post(() -> {
            if (billView.getWidth() == 0 || billView.getHeight() == 0) {
                // Nếu view chưa sẵn sàng, thử lại sau 500ms
                mainHandler.postDelayed(this::schedulePrintJob, 500);
                return;
            }

            // View đã sẵn sàng, thực hiện in
            new Thread(this::captureAndPrint).start();
        });
    }

    private void captureAndPrint() {
        try {
            // 1. Tạo bitmap từ view
            Bitmap bitmap = createBitmapFromView();

            // 2. Thực hiện in
            printBitmap(bitmap);

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Hết bộ nhớ khi tạo bitmap");
            showToastOnUiThread("Lỗi: Hết bộ nhớ khi tạo hóa đơn");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi không xác định: " + e.getMessage());
            showToastOnUiThread("Lỗi khi in hóa đơn");
        } finally {
            System.gc();
        }
    }

    private Bitmap createBitmapFromView() {
        int width = billView.getWidth();
        int height = billView.getHeight();

        if (width <= 0 || height <= 0) {
            throw new IllegalStateException("Kích thước view không hợp lệ");
        }

        // Sử dụng RGB_565 để tiết kiệm bộ nhớ
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        billView.draw(canvas);
        return bitmap;
    }

    private void printBitmap(Bitmap bitmap) {
        try {
            // Code in ấn thực tế sẽ được thêm ở đây
            // Ví dụ với iMin SDK:
            // IminPrintUtils printer = IminPrintUtils.getInstance(this);
            // printer.initPrinter(IminPrintUtils.PrintConnectType.SPI);
            // printer.printBitmap(bitmap, 1);
            // printer.printAndLineFeed();
            // printer.partialCut();

            showToastOnUiThread("Đã gửi lệnh in thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi máy in: " + e.getMessage());
            showToastOnUiThread("Lỗi máy in: " + e.getMessage());
        } finally {
            bitmap.recycle();
        }
    }

    private void showToastOnUiThread(String message) {
        mainHandler.post(() -> showToast(message));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

