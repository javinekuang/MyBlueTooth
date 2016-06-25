package k.javine.mybluetooth;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import k.javine.mybluetooth.tasks.ReadDataThread;

/**
 * Created by KuangYu on 2016/6/24 0024.
 */
public class InitActivity extends Activity {
    private static final int ANIMATION_ZOOM_IN = 0X03;
    private static final int ANIMATION_ZOOM_OUT = 0X04;

    @Bind(R.id.rl_beats)
    RelativeLayout rl_beats;
    @Bind(R.id.iv_stroke)
    ImageView iv_stroke;
    @Bind(R.id.iv_stroke_second)
    ImageView iv_stroke_second;

    ScaleAnimation zoomOutAnimation,zoomInAnimation;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ANIMATION_ZOOM_IN:
                    rl_beats.startAnimation(zoomInAnimation);
                    break;
                case ANIMATION_ZOOM_OUT:
                    rl_beats.startAnimation(zoomOutAnimation);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        ButterKnife.bind(this);
        initAnimation();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        mHandler.sendEmptyMessage(ANIMATION_ZOOM_OUT);
                        Thread.sleep(1600);
                        mHandler.sendEmptyMessage(ANIMATION_ZOOM_IN);
                        Thread.sleep(1600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    class AnimationThread extends Thread{

        private boolean isCancel = false;



        @Override
        public void run() {
            while (!isCancel){
                try {
                    mHandler.sendEmptyMessage(ANIMATION_ZOOM_OUT);
                    Thread.sleep(1600);
                    mHandler.sendEmptyMessage(ANIMATION_ZOOM_IN);
                    Thread.sleep(1600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 圆盘缩放动画
     */
    private void initAnimation() {
        zoomOutAnimation = new ScaleAnimation(1,0.7f,1,0.7f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomOutAnimation.setDuration(1500);
        zoomOutAnimation.setInterpolator(new DecelerateInterpolator());
        zoomOutAnimation.setFillAfter(true);

        zoomInAnimation = new ScaleAnimation(0.7f,1.0f,0.7f,1.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomInAnimation.setDuration(300);
        zoomInAnimation.setInterpolator(new AccelerateInterpolator());
        zoomInAnimation.setFillAfter(true);
        zoomInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startStrokeAnimation(iv_stroke);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startStrokeAnimation(iv_stroke_second);
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 启动圆圈扩散的动画
     * @param view
     */
    private void startStrokeAnimation(final View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(view,"scaleX",1.0f,7.0f);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(view,"scaleY",1.0f,7.0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
        AnimatorSet set = new AnimatorSet();
        set.play(alphaAnimator).with(scaleXAnimation).with(scaleYAnimation);
        set.setDuration(1000);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }
}
