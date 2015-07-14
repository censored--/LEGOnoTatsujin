package censored.legonotatsujin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by censored on 2015/07/14.
 */
public class LEGOSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private float x, y;
    private List<Block> blocks;//list of where blocks are located and how long each block size is.
    private int cell[][];
    private final int cellx = 16,celly = 9;

    private class Block{
        int blocksize, startx,starty;
        Block(int blockSize,int startX,int startY){
            blocksize = blockSize;
            startx = startX;
            starty = startY;
        }
    }

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
        blocks = new ArrayList<>();
        cell = new int[cellx][celly];
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
        c.drawColor(Color.WHITE);
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.WHITE);
        p.setTextSize(32);
        c.drawText(x+"x"+y,x/2, y/2, p);
        holder.unlockCanvasAndPost(c);
    }

    private void startnow() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                draw();

            }
        }, 100, 20, TimeUnit.MILLISECONDS);
    }
}