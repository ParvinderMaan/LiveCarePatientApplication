package com.app.patlivecare.helper

import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.MotionEvent

/*

    @author Parvinder Maan
 */
abstract class LiveCareGestureDetector : GestureDetector.OnGestureListener,
    OnDoubleTapListener {
    private var isGestureEnable = true // by default
    override fun onDown(e: MotionEvent): Boolean {
        // Log.d("Gesture ", " onDown");
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return if (isGestureEnable) {
            onSingleTap(e)
        } else false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        //  Log.d("Gesture ", " onSingleTapUp");
        return true
    }

    override fun onShowPress(e: MotionEvent) {
//        Log.d("Gesture ", " onShowPress");
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        //    Log.d("Gesture ", " onDoubleTap");
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        //  Log.d("Gesture ", " onDoubleTapEvent");
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        // Log.d("Gesture ", " onLongPress");
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
//        Log.d("Gesture ", " onScroll");
//        if (e1.getY() < e2.getY()) {
//            Log.d("Gesture ", " Scroll Down");
//        }
//        if (e1.getY() > e2.getY()) {
//            Log.d("Gesture ", " Scroll Up");
//        }
        return false
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
//        if (e1.getX() < e2.getX()) {
//            Log.d("Gesture ", "Left to Right swipe: " + e1.getX() + " - " + e2.getX());
//            Log.d("Speed ", String.valueOf(velocityX) + " pixels/second");
//        }
//        if (e1.getX() > e2.getX()) {
//            Log.d("Gesture ", "Right to Left swipe: " + e1.getX() + " - " + e2.getX());
//            Log.d("Speed ", String.valueOf(velocityX) + " pixels/second");
//        }
//        if (e1.getY() < e2.getY()) {
//            Log.d("Gesture ", "Up to Down swipe: " + e1.getX() + " - " + e2.getX());
//            Log.d("Speed ", String.valueOf(velocityY) + " pixels/second");
//        }
//        if (e1.getY() > e2.getY()) {
//            Log.d("Gesture ", "Down to Up swipe: " + e1.getX() + " - " + e2.getX());
//            Log.d("Speed ", String.valueOf(velocityY) + " pixels/second");
//        }
        return false
    }

    /*

         */
    abstract fun onSingleTap(e: MotionEvent?): Boolean
    fun setGestureEnable(isGestureEnable: Boolean) {
        this.isGestureEnable = isGestureEnable
    }
}