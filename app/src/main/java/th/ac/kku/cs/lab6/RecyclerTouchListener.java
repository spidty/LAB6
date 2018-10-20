package th.ac.kku.cs.lab6;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListener implements RecyclerView.OnClickListener {
    private ClickListener clickListener;
    private GestureDetector gestureDetector;

    @Override
    public void onClick(View v) {

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public RecyclerTouchListener(Context context,
                                 final RecyclerView recyclerView,
                                 final ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }


                    public void OnLongPress(MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && clickListener != null) {
                            clickListener.onLongClick(child,
                                    recyclerView.getChildAdapterPosition(child));
                        }
                    }
                });
    }

    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView,
                                         @NonNull MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(),
                motionEvent.getY());
        if (child != null && clickListener != null &&
                gestureDetector.onTouchEvent(motionEvent)) {
            clickListener.onClick(child,
                    recyclerView.getChildAdapterPosition(child));

        }

        return false;


    }
}
