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

import java.util.ArrayList;
import java.util.Collections;
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
    private Paint pGrid,pLEGO,pSihlhouette,pCusor;
    final float OFFSETX = 60,OFFSETY = 60;
    final float BLOCKX = 30,BLOCKY = 36;
    final float SCROLL_SPEED =  BLOCKX / 20;
    static final int CELL_WIDTH = 16,CELL_HEIGHT = 15;
    Block [][] cells;
    float[] cursor;
    List<Block> blocks;

    class Block{
        public float x,y;
        public int blocksize;
        Block(int blockSize,float left,float top){
            blocksize = blockSize;
            this.x = left;
            this.y = top;
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
        blocks = Collections.synchronizedList(new ArrayList<Block>());
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        requestFocus();

        cells = new Block[CELL_WIDTH][CELL_HEIGHT];

        pCusor = new Paint();
        pCusor.setColor(Color.RED);
        pCusor.setStyle(Paint.Style.STROKE);
        pCusor.setStrokeWidth(2);
        cursor = new float[2];

        pLEGO = new Paint();
        pLEGO.setColor(Color.BLUE);
        pLEGO.setStyle(Paint.Style.FILL);

        pSihlhouette = new Paint();
        pSihlhouette.setColor(Color.BLACK);
        pSihlhouette.setStyle(Paint.Style.STROKE);

        pGrid = new Paint();
        pGrid.setColor(Color.rgb(200,200,200));

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        x = getWidth();
        y = getHeight();
        cursor[0] = OFFSETX - SCROLL_SPEED * 3000/20;
        cursor[1] = OFFSETY + BLOCKY * (CELL_HEIGHT - 1);
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
        for (int i = 0; i <= CELL_WIDTH; i++){
            c.drawLine(OFFSETX + i * BLOCKX,OFFSETY,OFFSETX + i * BLOCKX, OFFSETY + BLOCKY * CELL_HEIGHT,pGrid);
        }
        for (int j = 0; j <= CELL_HEIGHT; j++){
            c.drawLine(OFFSETX,OFFSETY + j * BLOCKY,OFFSETX + BLOCKX * CELL_WIDTH, OFFSETY + j * BLOCKY,pGrid);
        }
        synchronized (blocks) {
            for (Block block : blocks) {
                for (int k = 0; k < block.blocksize; k++){
                    c.drawRect((float)(block.x + (BLOCKX / 5.0) + k * BLOCKX),
                            (float) (block.y - (BLOCKY / 6.0)),
                            (float)(block.x + ((BLOCKX * 4) / 5.0) + k * BLOCKX),
                            block.y,pLEGO);
                    c.drawRect((float)(block.x + (BLOCKX / 5.0) + k * BLOCKX),
                            (float) (block.y - (BLOCKY / 6.0)),
                            (float)(block.x + ((BLOCKX * 4) / 5.0) + k * BLOCKX),
                            block.y,pSihlhouette);
                }
                c.drawRect(block.x, block.y, block.x + BLOCKX * block.blocksize, block.y + BLOCKY, pLEGO);
                c.drawRect(block.x, block.y, block.x + BLOCKX * block.blocksize, block.y + BLOCKY, pSihlhouette);
            }
        }
        c.drawRect(cursor[0], cursor[1], cursor[0] + BLOCKX, cursor[1] + BLOCKY, pCusor);
        if (cursor[1] >= OFFSETY)
            c.drawRect(cursor[0] - (x - 2 * OFFSETX), cursor[1] - BLOCKY, cursor[0] + BLOCKX - (x - 2 * OFFSETX), cursor[1], pCusor);
        c.drawRect(cursor[0] + (x - 2 * OFFSETX), cursor[1] + BLOCKY, cursor[0] + BLOCKX + (x - 2 * OFFSETX), cursor[1] + 2 *BLOCKY, pCusor);
        holder.unlockCanvasAndPost(c);
    }

    private void startnow() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (cursor[0] >= x - OFFSETX) {
                    if (cursor[1] >= OFFSETY + BLOCKY) {
                        cursor[0] += SCROLL_SPEED - (x - 2 * OFFSETX);
                        cursor[1] -= BLOCKY;
                    }
                } else {
                    cursor[0] += SCROLL_SPEED;
                }
                draw();
            }
        }, 100, 20, TimeUnit.MILLISECONDS);
    }

    public boolean addBlock(int blocksize){
        try {
            synchronized (blocks) {
                int nearestCellx = Math.round((cursor[0] - OFFSETX) / BLOCKX);
                int nearestCelly = Math.round((cursor[1] - OFFSETY) / BLOCKY);
                if (nearestCellx >= 0 && nearestCellx + blocksize - 1 < CELL_WIDTH && cells[nearestCellx][nearestCelly] == null) {
                    Block block = new Block(blocksize, nearestCellx * BLOCKX + OFFSETX, nearestCelly * BLOCKY + OFFSETY);
                    for (int k = nearestCellx; k < nearestCellx + blocksize && k < CELL_WIDTH; k++) {
                        cells[k][nearestCelly] = block;
                    }
                    blocks.add(block);
                    return true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}