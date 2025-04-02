package nickrout.lenslauncher.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import nickrout.lenslauncher.model.Item;

public class LensView extends View {
    private List<Item> mItems;
    private int mSelectedItem = -1;
    private Paint mPaint;
    private Paint mHighlightPaint;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public LensView(Context context) {
        super(context);
        init(context, null);
    }

    public LensView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LensView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(36f);
        
        mHighlightPaint = new Paint();
        mHighlightPaint.setColor(Color.argb(60, 255, 255, 255)); // Semi-transparent white
    }

    public void setItems(List<Item> items) {
        mItems = items;
        invalidate();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (mItems == null || mItems.isEmpty()) {
            return;
        }

        // Draw all items
        for (int i = 0; i < mItems.size(); i++) {
            drawItem(canvas, i);
        }

        // Draw selection highlight
        if (mSelectedItem >= 0 && mSelectedItem < mItems.size()) {
            Rect bounds = getItemBounds(mSelectedItem);
            canvas.drawRect(bounds, mHighlightPaint);
        }
    }

    private void drawItem(Canvas canvas, int position) {
        Item item = mItems.get(position);
        Rect bounds = getItemBounds(position);
        
        // Draw icon
        Drawable icon = item.getIcon();
        if (icon != null) {
            icon.setBounds(bounds);
            icon.draw(canvas);
        }
        
        // Draw label
        String label = item.getLabel();
        if (label != null) {
            float textWidth = mPaint.measureText(label);
            float textX = bounds.centerX() - (textWidth / 2);
            float textY = bounds.bottom + 50; // Adjust as needed
            canvas.drawText(label, textX, textY, mPaint);
        }
    }

    private Rect getItemBounds(int position) {
        // Simple grid layout - adjust as needed for your app
        int width = getWidth();
        int height = getHeight();
        int itemsPerRow = 4; // Adjust based on your needs
        int itemWidth = width / itemsPerRow;
        int row = position / itemsPerRow;
        int col = position % itemsPerRow;
        
        return new Rect(
            col * itemWidth,
            row * itemWidth,
            (col + 1) * itemWidth,
            (row + 1) * itemWidth
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mSelectedItem = findClosestItem(event.getX(), event.getY());
                invalidate();
                return true;
                
            case MotionEvent.ACTION_UP:
                if (mSelectedItem >= 0 && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mItems.get(mSelectedItem));
                }
                mSelectedItem = -1;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private int findClosestItem(float x, float y) {
        if (mItems == null) return -1;
        
        for (int i = 0; i < mItems.size(); i++) {
            Rect bounds = getItemBounds(i);
            if (bounds.contains((int)x, (int)y)) {
                return i;
            }
        }
        return -1;
    }
}
