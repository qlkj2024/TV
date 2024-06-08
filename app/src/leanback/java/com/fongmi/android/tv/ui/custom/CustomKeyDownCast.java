package com.fongmi.android.tv.ui.custom;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.Constant;
import com.fongmi.android.tv.utils.KeyUtil;

public class CustomKeyDownCast extends GestureDetector.SimpleOnGestureListener {

    private final GestureDetector detector;
    private final Listener listener;
    private boolean changeSpeed;
    private int holdSecond;
    private boolean isMoveAdd;

    public static CustomKeyDownCast create(Activity activity) {
        return new CustomKeyDownCast(activity);
    }

    private CustomKeyDownCast(Activity activity) {
        this.detector = new GestureDetector(activity, this);
        this.listener = (Listener) activity;
    }

    public boolean onTouchEvent(MotionEvent e) {
        return detector.onTouchEvent(e);
    }

    public boolean hasEvent(KeyEvent event) {
        return KeyUtil.isEnterKey(event) || KeyUtil.isUpKey(event) || KeyUtil.isDownKey(event) || KeyUtil.isLeftKey(event) || KeyUtil.isRightKey(event);
    }

    public boolean onKeyDown(KeyEvent event) {
        check(event);
        return true;
    }

    private void check(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyUtil.isLeftKey(event)) {
            listener.onSeeking(subTime());
        } else if (event.getAction() == KeyEvent.ACTION_DOWN && KeyUtil.isRightKey(event)) {
            listener.onSeeking(addTime());
        } else if (event.getAction() == KeyEvent.ACTION_UP && (KeyUtil.isLeftKey(event) || KeyUtil.isRightKey(event))) {
            App.post(() -> listener.onSeekTo(getDelta()), 250);
        } else if (event.getAction() == KeyEvent.ACTION_UP && KeyUtil.isUpKey(event)) {
            if (changeSpeed) listener.onSpeedEnd();
            else listener.onKeyUp();
            changeSpeed = false;
        } else if (event.getAction() == KeyEvent.ACTION_UP && KeyUtil.isDownKey(event)) {
            listener.onKeyDown();
        } else if (event.getAction() == KeyEvent.ACTION_UP && KeyUtil.isEnterKey(event)) {
            listener.onKeyCenter();
        } else if (event.isLongPress() && KeyUtil.isUpKey(event)) {
            listener.onSpeedUp();
            changeSpeed = true;
        }
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent e) {
        listener.onDoubleTap();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
        listener.onSingleTap();
        return true;
    }

    private int addTime() {
        isMoveAdd = true;
        return getDelta();
    }

    private int subTime() {
        isMoveAdd = false;
        return getDelta();
    }

    private int getDelta() {
        int delta = (int) Math.max(1, holdSecond * holdSecond * 0.25) * Constant.INTERVAL_SEEK;
        holdSecond += 1;
        return isMoveAdd ? delta : -delta;
    }

    public void resetTime() {
        holdSecond = 0;
    }

    public interface Listener {

        void onSeeking(int time);

        void onSeekTo(int time);

        void onSpeedUp();

        void onSpeedEnd();

        void onKeyUp();

        void onKeyDown();

        void onKeyCenter();

        void onSingleTap();

        void onDoubleTap();
    }
}
