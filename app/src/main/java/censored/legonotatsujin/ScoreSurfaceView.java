package censored.legonotatsujin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ScoreSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private float x, y;
    final float OFFSET = 210;
    final float BLOCKX = 105;
    float cursor;
    final int BEATRATE = 25;
    final float SCROLL_SPEED =BLOCKX / BEATRATE;
    private Paint pBaseLine,pSeparate,pTapPoint,pInternalBlock,pBlock;
    private double[] separators;
    private boolean[] cells;
    public List<Block> blocks;
    boolean scrollLocked;
    static public class Block {
        public float blockSize,start,end;
        static public final int BLOCKX = 105;
        public Block(int blocksize,float startPoint){
            start = startPoint;
            end = BLOCKX * blocksize + startPoint;
            blockSize = blocksize;
        }
    }
    public LEGOSurfaceView BlockView;

    public ScoreSurfaceView(Context context) {
        super(context);
        init();
    }

    public ScoreSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScoreSurfaceView(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        requestFocus();

        pBaseLine = new Paint();
        pBaseLine.setStyle(Paint.Style.FILL);
        pBaseLine.setColor(Color.rgb(200, 200, 200));
        pBaseLine.setStrokeWidth(3);

        pSeparate = new Paint();
        pSeparate.setStyle(Paint.Style.FILL);
        pSeparate.setColor(Color.BLACK);
        pSeparate.setStrokeWidth(3);

        pTapPoint = new Paint();
        pTapPoint.setStyle(Paint.Style.STROKE);
        pTapPoint.setColor(Color.RED);
        pTapPoint.setStrokeWidth(3);

        pBlock = new Paint();
        pBlock.setStyle(Paint.Style.STROKE);
        pBlock.setColor(Color.BLACK);
        pBlock.setStrokeWidth(5);

        pInternalBlock = new Paint();
        pInternalBlock.setStyle(Paint.Style.FILL);
        pInternalBlock.setColor(Color.WHITE);

        separators = new double[LEGOSurfaceView.CELL_WIDTH *LEGOSurfaceView.CELL_HEIGHT + 1];
        cells = new boolean[LEGOSurfaceView.CELL_WIDTH * LEGOSurfaceView.CELL_HEIGHT];
        for (int k = 0; k < cells.length; k++){
            separators[k] = SCROLL_SPEED * 3000 / 20 + OFFSET + k * BLOCKX;
            cells[k] = false;
        }
        separators[cells.length] = SCROLL_SPEED * 3000 / 20 + OFFSET + cells.length * BLOCKX;
        cursor = - SCROLL_SPEED * 3000/20;
        blocks = Collections.synchronizedList(new ArrayList<Block>());

        scrollLocked = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        x = getWidth();
        y = getHeight();
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
        c.drawColor(Color.WHITE);
        c.drawLine(0, y / 2, x, y / 2, pBaseLine);
        c.drawLine(0, y * 3 / 4, x, y * 3 / 4,pBaseLine);
        for (int k = 0; k < separators.length; k++) {
            if (separators[k]>= 0 && x >= separators[k]) {
                if (k % LEGOSurfaceView.CELL_WIDTH == 0)
                    c.drawLine((float) separators[k], y / 2, (float) separators[k], y * 3 / 4, pSeparate);
                else
                    c.drawLine((float) separators[k], y / 2, (float) separators[k], y * 3 / 4, pBaseLine);

            }
        }
        for (Block block : blocks) {
            for (int k = 0; k < block.blockSize; k++) {
                c.drawRect(block.start + BLOCKX / 5 + BLOCKX * k, y / 2 - BLOCKX / 5,
                        block.start + BLOCKX * 4 / 5 + BLOCKX * k, y / 2, pBlock);
            }
            c.drawRect(block.start, y / 2, block.end, y * 3 / 4, pInternalBlock);
            c.drawRect(block.start, y / 2, block.end, y * 3 / 4, pBlock);
        }
        c.drawRect(OFFSET,y/2,OFFSET + BLOCKX,y*3/4,pTapPoint);
        holder.unlockCanvasAndPost(c);
    }

    private void startnow() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (scrollLocked) {
                    scrollBlocks((float) (direction * 105 / 1000));
                }else {
                    Log.d("startnow",""+separators[separators.length]) ;
                    scrollBlocks(SCROLL_SPEED);
                }
                draw();

            }
        }, 100, 20, TimeUnit.MILLISECONDS);
    }

    private void scrollBlocks(float scrollSpeed) {
        for (int k = 0; k < separators.length; k++) {
            separators[k] -= scrollSpeed;
        }
        cursor += scrollSpeed;
        synchronized (blocks) {
            for (Block block : blocks) {
                    block.start -= scrollSpeed;
                    block.end -= scrollSpeed;
            }
        }
    }

    public void addBlock(int blocksize) {
        try {
            synchronized (blocks) {
                int nearestSeparator = Math.round(cursor / BLOCKX) - blocksize + 1;
                Log.d("addBlock", "cells[" + nearestSeparator + "]=" + cells[nearestSeparator]);
                if (!cells[nearestSeparator]) {
                    ScoreSurfaceView.Block block = new ScoreSurfaceView.Block(blocksize, (float) separators[nearestSeparator]);
                    blocks.add(block);
                    for (int k = 0; k < blocksize; k++)
                        cells[nearestSeparator + k] = true;
                }else if (!cells[nearestSeparator+1]){
                    ScoreSurfaceView.Block block = new ScoreSurfaceView.Block(blocksize, (float) separators[nearestSeparator+1]);
                    blocks.add(block);
                    for (int k = 0; k < blocksize; k++)
                        cells[nearestSeparator + 1 + k] = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    double xTouched,direction;
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xTouched = event.getX();
                scrollLocked = true;
                if (BlockView != null){
                    BlockView.scrollLocked = true;
                    BlockView.direction = (float)direction;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                direction = event.getX() - xTouched;
                if (BlockView!= null){
                    BlockView.direction = (float) direction;
                }
                break;
            case MotionEvent.ACTION_UP:
                direction = 0;
                scrollLocked = false;
                if (BlockView != null){
                    BlockView.direction = 0;
                    BlockView.scrollLocked = false;
                }
                break;
            default:
                break;
        }

        return true;
    }
}
