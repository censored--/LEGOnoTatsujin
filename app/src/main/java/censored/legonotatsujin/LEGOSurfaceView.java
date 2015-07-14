package censored.legonotatsujin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by censored on 2015/07/14.
 */
public class LEGOSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private float x, y;

    public LEGOSurfaceView(Context context) {
        super(context);
        init();
    }

    public LEGOSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LEGOSurfaceView(Context context, AttributeSet attrs,
                           int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        x = getWidth();
        y = getHeight();
        draw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void draw() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.WHITE);
        p.setTextSize(32);
        c.drawText(x+"x"+y,x/2, y/2, p);
        holder.unlockCanvasAndPost(c);
    }
}