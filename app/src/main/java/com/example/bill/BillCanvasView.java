package com.example.bill;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillCanvasView extends View {

    // ==== Kích thước & Định dạng ====
    private static final int PADDING = 30;
    private static final int TITLE_TEXT_SIZE = 32;
    private static final int HEADER_TEXT_SIZE = 22;
    private static final int BODY_TEXT_SIZE = 18;
    private static final int FOOTER_TEXT_SIZE = 16;
    private static final int LINE_SPACING = 40;
    private static final int ITEM_SPACING = 35;
    private static final int DIVIDER_HEIGHT = 2;

    // ==== Màu sắc ====
    private static final int COLOR_PRIMARY = Color.parseColor("#00796B");          // Màu chủ đạo
    private static final int COLOR_TEXT = Color.parseColor("#212121");             // Màu chữ chính
    private static final int COLOR_SECONDARY_TEXT = Color.parseColor("#757575");   // Màu chữ phụ
    private static final int COLOR_DIVIDER = Color.parseColor("#BDBDBD");          // Màu gạch kẻ

    // ==== Biến ====
    private Paint paint;
    private List<BillItem> items = new ArrayList<>();
    private String shopName, shopAddress, shopPhone;
    private String invoiceNumber, currentDate;
    private Bitmap cachedBitmap;
    private boolean isCached = false;

    // ==== Constructor ====
    public BillCanvasView(Context context) {
        super(context);
        init();
    }

    public BillCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Thông tin mặc định
        shopName = "Cửa hàng Thực Phẩm Xanh";
        shopAddress = "123 Nguyễn Du, Q1, TP.HCM";
        shopPhone = "Hotline: 1900 1234";
        invoiceNumber = generateInvoiceNumber();
        currentDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault()).format(new Date());

        // Sản phẩm mẫu
        addItem("Nước khoáng Lavie 500ml", 2, 15000);
        addItem("Bánh mì sandwich Việt Nam", 1, 20000);
        addItem("Trà xanh Không Độ", 3, 10000);
        addItem("Snack khoai tây vị BBQ", 5, 12000);
    }

    // ==== Giao diện ====
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = calculateTotalHeight();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isCached && cachedBitmap != null && !cachedBitmap.isRecycled()) {
            canvas.drawBitmap(cachedBitmap, 0, 0, paint);
        } else {
            drawInvoice(canvas);
        }
    }

    private void drawInvoice(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        int y = PADDING;

        y = drawHeader(canvas, y);
        y = drawShopInfo(canvas, y);
        y = drawItems(canvas, y);
        y = drawTotal(canvas, y);
        drawFooter(canvas, y);
    }

    private int drawHeader(Canvas canvas, int y) {
        paint.setColor(COLOR_PRIMARY);
        canvas.drawRect(0, 0, getWidth(), y + LINE_SPACING * 3, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(TITLE_TEXT_SIZE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        drawCenteredText(canvas, "HÓA ĐƠN BÁN HÀNG", y += LINE_SPACING);

        paint.setTextSize(BODY_TEXT_SIZE);
        paint.setTypeface(Typeface.DEFAULT);
        drawCenteredText(canvas, invoiceNumber, y += LINE_SPACING);
        drawCenteredText(canvas, currentDate, y += LINE_SPACING);

        return y + ITEM_SPACING;
    }
    public void setShopInfo(String name, String address, String phone) {
        this.shopName = name;
        this.shopAddress = address;
        this.shopPhone = phone;
        resetCache();
        requestLayout();
        invalidate();
    }

    private int drawShopInfo(Canvas canvas, int y) {
        paint.setColor(COLOR_TEXT);
        paint.setTextSize(HEADER_TEXT_SIZE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        drawCenteredText(canvas, shopName, y += LINE_SPACING);

        paint.setTextSize(BODY_TEXT_SIZE);
        paint.setTypeface(Typeface.DEFAULT);
        drawCenteredText(canvas, shopAddress, y += LINE_SPACING);
        drawCenteredText(canvas, shopPhone, y += LINE_SPACING);

        paint.setColor(COLOR_DIVIDER);
        canvas.drawLine(PADDING, y += ITEM_SPACING / 2, getWidth() - PADDING, y, paint);

        return y + ITEM_SPACING;
    }

    private int drawItems(Canvas canvas, int y) {
        paint.setTextSize(BODY_TEXT_SIZE);
        paint.setColor(COLOR_PRIMARY);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        float col1 = PADDING;
        float col2 = getWidth() * 0.55f;
        float col3 = getWidth() * 0.7f;
        float col4 = getWidth() - PADDING;

        canvas.drawText("Sản phẩm", col1, y, paint);
        canvas.drawText("SL", col2, y, paint);
        canvas.drawText("Đơn giá", col3, y, paint);
        canvas.drawText("Thành tiền", col4 - paint.measureText("Thành tiền"), y, paint);

        paint.setColor(COLOR_DIVIDER);
        canvas.drawLine(PADDING, y += LINE_SPACING, getWidth() - PADDING, y, paint);
        y += ITEM_SPACING / 2;

        paint.setColor(COLOR_TEXT);
        paint.setTypeface(Typeface.DEFAULT);

        for (BillItem item : items) {
            drawMultiLineText(canvas, item.name, col1, y, (int)(col2 - col1 - 5));
            canvas.drawText(String.valueOf(item.quantity), col2, y, paint);
            canvas.drawText(formatCurrency(item.price), col3, y, paint);
            canvas.drawText(formatCurrency(item.total()), col4 - paint.measureText(formatCurrency(item.total())), y, paint);
            y += LINE_SPACING;
        }

        paint.setColor(COLOR_DIVIDER);
        canvas.drawLine(PADDING, y, getWidth() - PADDING, y, paint);

        return y + ITEM_SPACING;
    }

    private int drawTotal(Canvas canvas, int y) {
        paint.setColor(COLOR_TEXT);
        paint.setTextSize(HEADER_TEXT_SIZE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("TỔNG CỘNG:", PADDING, y, paint);
        String total = formatCurrency(calculateTotal());
        canvas.drawText(total, getWidth() - PADDING - paint.measureText(total), y, paint);

        paint.setColor(COLOR_PRIMARY);
        canvas.drawLine(PADDING, y + ITEM_SPACING / 2, getWidth() - PADDING, y + ITEM_SPACING / 2, paint);

        return y + LINE_SPACING;
    }

    private void drawFooter(Canvas canvas, int y) {
        paint.setColor(COLOR_SECONDARY_TEXT);
        paint.setTextSize(FOOTER_TEXT_SIZE);
        paint.setTypeface(Typeface.DEFAULT);

        drawCenteredText(canvas, "Cảm ơn quý khách đã mua hàng!", y += ITEM_SPACING);
        drawCenteredText(canvas, "Hệ thống siêu thị Thực Phẩm Xanh", y + LINE_SPACING);
    }

    // ==== Công cụ hỗ trợ ====
    private void drawCenteredText(Canvas canvas, String text, float y) {
        float x = (getWidth() - paint.measureText(text)) / 2;
        canvas.drawText(text, x, y, paint);
    }

    private void drawMultiLineText(Canvas canvas, String text, float x, float y, int maxWidth) {
        if (paint.measureText(text) <= maxWidth) {
            canvas.drawText(text, x, y, paint);
            return;
        }
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (paint.measureText(line + " " + word) <= maxWidth) {
                line.append(" ").append(word);
            } else {
                canvas.drawText(line.toString().trim(), x, y, paint);
                y += LINE_SPACING * 0.9f;
                line = new StringBuilder(word);
            }
        }
        if (line.length() > 0) {
            canvas.drawText(line.toString().trim(), x, y, paint);
        }
    }

    private String formatCurrency(int amount) {
        return String.format(Locale.getDefault(), "%,dđ", amount);
    }

    private int calculateTotal() {
        int total = 0;
        for (BillItem item : items) {
            total += item.total();
        }
        return total;
    }

    private int calculateTotalHeight() {
        int base = PADDING * 2 + LINE_SPACING * 10;
        int itemsHeight = items.size() * LINE_SPACING;
        for (BillItem item : items) {
            if (item.name.length() > 20) itemsHeight += LINE_SPACING * 0.4f;
        }
        return base + itemsHeight + 150;
    }

    private String generateInvoiceNumber() {
        return "HD-" + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date());
    }

    // ==== Public ====
    public void addItem(String name, int quantity, int price) {
        items.add(new BillItem(name, quantity, price));
        resetCache();
        requestLayout();
        invalidate();
    }

    public void clearItems() {
        items.clear();
        resetCache();
        requestLayout();
        invalidate();
    }

    public Bitmap getInvoiceBitmap() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            measure(MeasureSpec.makeMeasureSpec(1080, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(calculateTotalHeight(), MeasureSpec.EXACTLY));
            layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        if (isCached && cachedBitmap != null) return cachedBitmap;

        try {
            cachedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(cachedBitmap);
            drawInvoice(canvas);
            isCached = true;
            return cachedBitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void resetCache() {
        if (cachedBitmap != null) {
            cachedBitmap.recycle();
            cachedBitmap = null;
        }
        isCached = false;
    }
    public void loadFromQueryString(String query) {
        clearItems(); // Xóa dữ liệu cũ
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length != 2) continue;

            String key = keyValue[0];
            String value = java.net.URLDecoder.decode(keyValue[1], java.nio.charset.StandardCharsets.UTF_8);

            if (key.equals("shopName")) {
                setShopInfo(value, shopAddress, shopPhone); // Sử dụng địa chỉ/sđt mặc định
            } else if (key.startsWith("item")) {
                String[] parts = value.split(",");
                if (parts.length == 3) {
                    try {
                        String name = parts[0].trim();
                        int qty = Integer.parseInt(parts[1].trim());
                        int price = Integer.parseInt(parts[2].trim());
                        addItem(name, qty, price);
                    } catch (NumberFormatException e) {
                        // Bỏ qua lỗi định dạng
                    }
                }
            }
        }



    // Cập nhật ngày giờ và số hóa đơn mới
        this.currentDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault()).format(new Date());
        this.invoiceNumber = generateInvoiceNumber();
    }

    @Override
    protected void onDetachedFromWindow() {
        resetCache();
        super.onDetachedFromWindow();
    }

    // ==== Inner Class ====
    private static class BillItem {
        String name;
        int quantity;
        int price;

        BillItem(String name, int quantity, int price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        int total() {
            return quantity * price;
        }
    }
}


