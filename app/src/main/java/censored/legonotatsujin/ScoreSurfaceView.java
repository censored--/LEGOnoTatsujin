package censored.legonotatsujin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by censored on 2015/07/14.
 */
public class ScoreSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private float x, y;
    final int offset = 105;
    private Paint pBaseLine,pTapPoint,pInternalBlock,pBlock;
    private int[] separators;
    public List<Block> blocks;
    static public class Block {
        static public final int offset = 105;
        public int blockSize,start,end;
        public Block(int blocksize,int startPoint){
            start = startPoint;
            end = offset * blocksize + startPoint;
            blockSize = blocksize;
        }
    }

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

        separators = new int[10];
        for (int k = 0; k < separators.length; k++){
            separators[k] = offset*k;
        }

        blocks = Collections.synchronizedList(new ArrayList<Block>());
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
        for (int separator : separators)
            c.drawLine(separator,y/2,separator,y*3/4,pBaseLine);
        for (Block block : blocks) {
            for (int k = 0; k < block.blockSize; k++) {
                c.drawRect(block.start + offset / 5 + offset * k, y / 2 - offset / 5,block.start + offset * 4 / 5 + offset * k, y / 2, pBlock);
            }
            c.drawRect(block.start, y / 2, block.end, y * 3 / 4, pInternalBlock);
            c.drawRect(block.start, y / 2, block.end, y * 3 / 4, pBlock);
        }
        c.drawRect(105,y/2,210,y*3/4,pTapPoint);
        holder.unlockCanvasAndPost(c);
    }

    private void startnow() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (int k = 0; k < separators.length; k++) {
                    separators[k] = (separators[k] - 3 < 0) ? ((int) x + separators[k] - 3) : (separators[k] - 3);
                }
                try {
                    checkBlocks();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                draw();

            }
        }, 100, 20, TimeUnit.MILLISECONDS);
    }

    private void checkBlocks() {
        synchronized (blocks) {
            List<Block> removelist = new ArrayList<>();
            for (Block block : blocks) {
                if (block.end - 3 < 0)
                    removelist.add(block);
                else {
                    block.start -= 3;
                    block.end -= 3;
                }
            }
            for (Block removeblock : removelist)
                blocks.remove(removeblock);
        }
    }

    public void addBlock(int blocksize){
        synchronized (blocks){
            int nearestSeparator = 0;
            int distance = Math.abs(separators[0] - offset);
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
    }
}
