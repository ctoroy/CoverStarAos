package com.shinleeholdings.coverstar.chatting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.shinleeholdings.coverstar.R;

public class ChattingRoomLayout extends FrameLayout {

    public ChattingRoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_chattingroom, this, true);
    }

    public interface ViewResizeEventListener {
        void onResized();
    }

    private ViewResizeEventListener viewResizeEventListener;

    public void setOnResizeEventListener(ViewResizeEventListener listener) {
        viewResizeEventListener = listener;
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        if (viewResizeEventListener != null) {
            viewResizeEventListener.onResized();
        }
    }
}
