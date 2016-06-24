package k.javine.mybluetooth;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import k.javine.mybluetooth.tasks.ReadDataThread;

/**
 * Created by KuangYu on 2016/6/24 0024.
 */
public class InitActivity extends Activity {

    @Bind(R.id.rl_beats)
    RelativeLayout rl_beats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        ButterKnife.bind(this);
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation zoomInAnimation = new ScaleAnimation(1,1.2f,1,1.2f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomInAnimation.setDuration(300);
        zoomInAnimation.setRepeatCount(Animation.INFINITE);
        zoomInAnimation.setRepeatMode(Animation.REVERSE);
        zoomInAnimation.setStartOffset(300);
        rl_beats.startAnimation(zoomInAnimation);
    }
}
