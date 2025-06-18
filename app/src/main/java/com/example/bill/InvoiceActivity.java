package com.example.bill;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class InvoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoice);

        // Lấy query từ InputActivity
        String query = getIntent().getStringExtra("billQuery");

        // Tạo BillCanvasView và thêm vào layout
        BillCanvasView billView = new BillCanvasView(this);
        if (query != null) {
            billView.loadFromQueryString(query); // Bạn cần có hàm này trong BillCanvasView
        }

        LinearLayout container = findViewById(R.id.main);
        container.addView(billView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }
}
