//
//  Copyright (c) 2014 VK.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
//  the Software, and to permit persons to whom the Software is furnished to do so,
//  subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
//  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
//  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
//  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.vk.sdk.api.httpClient;

import android.os.Handler;
import android.os.Looper;

import com.vk.sdk.api.VKError;

/**
 * Class for executing any kind of asynchronous operation
 */
public abstract class VKAbstractOperation {

	public enum VKOperationState {
        Created,
        Ready,
        Executing,
        Paused,
        Finished
    }

    /**
     * Listener called after operation finished
     */
    private VKOperationCompleteListener mCompleteListener;
    /**
     * Current operation state. Checked by stateTransitionIsValid function
     */
    private VKOperationState mState = VKOperationState.Created;
    /**
     * Flag for cancel
     */
    private boolean mCanceled = false;

    /**
     * Handler for notify main thread
     */
    private static Handler mMainThreadHandler;

    /**
     * Return handler for main loop
     *
     * @return Main loop handler
     */
    protected static Handler getMainThreadHandler() {
        if (mMainThreadHandler == null) {
            mMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return mMainThreadHandler;
    }

    public VKAbstractOperation() {
        setState(VKOperationState.Ready);
    }

    /**
     * Entry point for operation
     */
    public abstract void start();

    /**
     * Cancels current operation and finishes it
     */
    public void cancel() {
        mCanceled = true;
        setState(VKOperationState.Finished);
    }

    /**
     * Finishes current operation. Will call onComplete() function for completeListener
     */
    public void finish() {

        if (mCompleteListener != null) {
            postInMainQueue(new Runnable() {
                @Override
                public void run() {
                    mCompleteListener.onComplete();
                }
            });
        }
    }

    /**
     * Set complete listener for current operation
     *
     * @param listener Complete listener
     */
    protected void setCompleteListener(VKOperationCompleteListener listener) {
        mCompleteListener = listener;
    }

    /**
     * Sets operation state. Checks validity of state transition
     *
     * @param state New operation state
     */
    protected void setState(VKOperationState state) {
        if (isStateTransitionInvalid(mState, state, mCanceled)) {
            return;
        }
        mState = state;
        if (mState == VKOperationState.Finished) {
            finish();
        }
    }

    /**
     * Checks validity of state transition
     *
     * @param fromState   Old state (current operation state)
     * @param toState     New state, sets by developer
     * @param isCancelled Flag of cancelation
     * @return Result of validation
     */
    private boolean isStateTransitionInvalid(VKOperationState fromState, VKOperationState toState,
                                             boolean isCancelled) {
        switch (fromState) {
            case Ready:
                switch (toState) {
                    case Paused:
                    case Executing:
                        return false;

                    case Finished:
                        return !isCancelled;

                    default:
                        return true;
                }

            case Executing:
                switch (toState) {
                    case Paused:
                    case Finished:
                        return false;

                    default:
                        return true;
                }

            case Finished:
                return true;

            case Paused:
                return toState != VKOperationState.Ready;

            default:
                return false;
        }
    }

    /**
     * Post runnable in main loop
     *
     * @param r Runnable to post
     */
    public static void postInMainQueue(Runnable r) {
        getMainThreadHandler().post(r);
    }

    /**
     * Post runnable in main loop with delay
     *
     * @param r            Runnable to post
     *
     */
    public static void postInMainQueueDelayed(Runnable r) {
        getMainThreadHandler().postDelayed(r, (long) 300);
    }

    public static interface VKOperationCompleteListener {
        public void onComplete();
    }

    public static abstract class VKAbstractCompleteListener<OperationType,ResponseType> {
        public abstract void onComplete(OperationType operation, ResponseType response);
        public abstract void onError(OperationType operation, VKError error);
    }
}
