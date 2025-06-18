package com.example.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class InputActivity extends AppCompatActivity {

    EditText edtShopName, edtItems;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input);

        edtShopName = findViewById(R.id.edtShopName);
        edtItems = findViewById(R.id.edtItems);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopName = edtShopName.getText().toString().trim();
                String itemsText = edtItems.getText().toString().trim();
                String[] itemLines = itemsText.split("\n");

                StringBuilder query = new StringBuilder();

                try {
                    query.append("shopName=").append(URLEncoder.encode(shopName, "UTF-8"));
                    int index = 1;
                    for (String line : itemLines) {
                        String[] parts = line.split(",");
                        if (parts.length == 3) {
                            String itemName = URLEncoder.encode(parts[0].trim(), "UTF-8");
                            String quantity = parts[1].trim();
                            String price = parts[2].trim();
                            query.append("&item").append(index++).append("=")
                                    .append(itemName).append(",").append(quantity).append(",").append(price);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // Gửi dữ liệu sang InvoiceActivity
                Intent intent = new Intent(InputActivity.this, InvoiceActivity.class);
                intent.putExtra("billQuery", query.toString());
                startActivity(intent);
            }
        });
    }
}
