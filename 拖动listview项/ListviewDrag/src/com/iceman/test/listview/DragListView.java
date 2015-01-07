
package com.iceman.test.listview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DragListView<T> extends ListView {

    private ImageView dragImageView;// ����ק�����ʵ����һ��ImageView

    private T moveitem;

    private int dragPosition;// ��ָ���µ�ʱ���б�����listview�е�λ��

    private int savePosition;// ���ڽ����б�����

    private int movePosition;// ��ָ�϶���ʱ�򣬵�ǰ�϶������б��е�λ��

    private int dragPoint;// �ڵ�ǰ�������е�λ��

    private int dragOffset;// ��ǰ��ͼ����Ļ�ľ���(����ֻʹ����y������)

    private WindowManager windowManager;// windows���ڿ�����

    private WindowManager.LayoutParams windowParams;// ���ڿ�����ק�����ʾ�Ĳ���

    private ViewGroup itemView;

    private IDragCallback<T> mSelectAdapter;

    private int mEventY = 0;

//    private String[] mReplaceString = new String[] {"", ""};
    private T mReplaceItem;
    List<T> mReplaceItemList;
    
    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mReplaceItemList=new ArrayList<T>();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mSelectAdapter = (IDragCallback<T>) getAdapter();
    }

    // ����touch�¼�����ʵ���Ǽ�һ�����
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            dragPosition = pointToPosition(x, y);// �жϵ�ǰxyֵ
                                                 // �Ƿ���item��
                                                 // �����
                                                 // ���ظ�item��position
                                                 // ���� ����
                                                 // INVALID_POSITION��-1��
            if (dragPosition == AdapterView.INVALID_POSITION) {
                return super.onInterceptTouchEvent(ev);
            }
            savePosition = dragPosition;
            movePosition = dragPosition;
            moveitem = mSelectAdapter.getT(dragPosition);
            itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());// ��ȡ��ǰ�����view
            dragPoint = y - itemView.getTop();// �������-view���ϱ߽�
            dragOffset = (int) (ev.getRawY() - y);// ������Ļ�е�y����-listview�е�y����,��ƫ����
            View dragger = itemView.findViewById(mSelectAdapter.getMoveItemViewId());
            if (dragger != null && x > dragger.getLeft() && x < dragger.getRight()) {
                itemView.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
                mSelectAdapter.remove(moveitem);
                mSelectAdapter.insert(mReplaceItem, dragPosition);
                startDrag(bm, y);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * �����¼�
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (dragImageView != null && dragPosition != INVALID_POSITION) {
            switch (action) {
                case MotionEvent.ACTION_UP:
                    stopDrag();
                    insertLastData(movePosition);
                    mEventY = 0;
                    itemView.destroyDrawingCache();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) ev.getY();
                    mEventY = moveY;
                    onDrag(moveY, (int) ev.getRawY());
                    break;
                case MotionEvent.ACTION_DOWN:
                    break;
            }
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mEventY != 0) {
            if (mEventY != 0 && mEventY <= 0) {
                setSelectionFromTop(getFirstVisiblePosition(), getChildAt(0).getTop() + 3);
            } else if (mEventY >= getHeight()) {
                setSelectionFromTop(getFirstVisiblePosition(), getChildAt(0).getTop() - 3);
            }
        }
    }

    /**
     * ׼���϶�����ʼ���϶����ͼ��
     * 
     * @param bm
     * @param y
     */
    public void startDrag(Bitmap bm, int y) {
        stopDrag();

        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;

        windowParams.y = y - dragPoint + dragOffset;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;

        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(imageView, windowParams);
        dragImageView = imageView;
    }

    /**
     * ֹͣ�϶���ȥ���϶����ͷ��
     */
    public void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void insertLastData(int position) {
        T dragItem = mSelectAdapter.getT(position);
        mSelectAdapter.remove(dragItem);
        mSelectAdapter.insert(moveitem, position);
    }

    /**
     * �϶�ִ�У���Move������ִ��
     */
    public void onDrag(int y, int rawY) {
        if (y < 0) { // �����ϱ߽磬��Ϊ��Сֵλ��0
            y = 0;
        } else if (y > getHeight()) {
            y = getHeight();

        }
        postInvalidate();
        if (dragImageView != null) {
            windowParams.alpha = 0.8f;
            // windowParams.y = y - dragPoint + dragOffset;
            windowParams.y = rawY - dragPoint;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
        // Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            movePosition = tempPosition;
            onDrop(y);
        }

    }

    public void onDrop(int y) { // �����϶���λ�����б��з���
        if (movePosition > savePosition) {// ��������ƶ�
            ChangeItemDown(savePosition, movePosition);
            savePosition = movePosition;
        } else if (movePosition < savePosition) {// ��������ƶ�
            ChangeItemUp(savePosition, movePosition);
            savePosition = movePosition;
        }
    }

    private void ChangeItemDown(int save, int move) {  // �����϶�
        T itemStr = mSelectAdapter.getT(move);
        mSelectAdapter.remove(mSelectAdapter.getT(save));
        mSelectAdapter.remove(itemStr);
        mSelectAdapter.insert(itemStr, save);
        mSelectAdapter.insert(mReplaceItem, move);
    }

    private void ChangeItemUp(int save, int move) {  // �����϶�
        T itemStr = mSelectAdapter.getT(move);
        mSelectAdapter.remove(mSelectAdapter.getT(save));
        mSelectAdapter.remove(itemStr);
        mSelectAdapter.insert(mReplaceItem, move);
        mSelectAdapter.insert(itemStr, save);
    }
    
    public interface IDragCallback<T> {
    	public void remove(T t);
    	public void insert(T t, int position);
    	public T getT(int position);
    	public int getMoveItemViewId();
    	
    }
}
