package com.topwise.plugin;

import android.os.CountDownTimer;

public class CardTimer extends CountDownTimer {

    public interface TickTimerListener {
        public void onFinish();

        public void onTick(long leftTime);
    }

    private TickTimerListener listener;

    public void setTimeCountListener(TickTimerListener listener) {
        this.listener = listener;
    }

    public CardTimer(long timeout, long tickInterval) {
        super(timeout * 1000, tickInterval * 1000);
    }

    @Override
    public void onFinish() {
        if (listener != null)
            listener.onFinish();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (listener != null)
            listener.onTick(millisUntilFinished / 1000);
    }

}