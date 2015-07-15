package censored.legonotatsujin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by censored on 2015/07/14.
 */
//600 * 600 -> 480 = 30 * 16 * 540 = 36 * 15
public class LEGOSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private float x, y;
    private Paint pBackGround,pGrid,pCusor;
    final float SCROLL_SPEED =  (float) 30.0 / 10;
    final float OFFSETX = 60,OFFSETY = 90;
    final float BLOCKX = 30,BLOCKY = 36;
    final int CELL_WIDTH = 16,CELL_HEIGHT = 15;
    int [][] cells;
    float[] cursor;

    class Block{
        public float start,end;
        public int blocksize;
        Block(int blocksize,float start,float end){

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

        cells = new int[CELL_WIDTH][CELL_HEIGHT];

        pCusor = new Paint();
        pCusor.setColor(Color.RED);
        pCusor.setStyle(Paint.Style.STROKE);
        pCusor.setStrokeWidth(2);
        cursor = new float[2];
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        x = getWidth();
        y = getHeight();
        cursor[0] = OFFSETX;
        cursor[1] = y - BLOCKY;
        draw();
        startnow();
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
        c.drawRect(cursor[0], cursor[1], cursor[0] + BLOCKX, cursor[1] + BLOCKY, pCusor);
        holder.unlockCanvasAndPost(c);
    }

    private void startnow() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (cursor[0] >= x - OFFSETX - BLOCKX) {
                    if (cursor[1] >= OFFSETY) {
                        cursor[0] -= (x - 2 * OFFSETX - BLOCKX);
                        cursor[1] -= BLOCKY;
                    }
                } else {
                    cursor[0] += SCROLL_SPEED;
                }
                draw();
            }
        }, 100, 20, TimeUnit.MILLISECONDS);
    }

    /*public void addBlock(int blocksize){
        synchronized (blocks){
            int nearestSeparator = 0;
            float distance = Math.abs(separators[0] - offset);
            for (int k = 1; k < separators.length; k++)
                if (Math.abs(separators[k] - offset)<distance){
                    distance = Math.abs(separators[k] - offset);
                    nearestSeparator = k;
                }
            if (blocks.isEmpty() || blocks.get(blocks.size()-1).end < Block.offset*3/2) {
                ScoreSurfaceView.Block block = new ScoreSurfaceView.Block(blocksize,separators[nearestSeparator]);
                blocks.add(block);
            }
        }
    }*/
}