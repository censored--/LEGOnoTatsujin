package censored.legonotatsujin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by censored on 15/07/18.
 */
public class Keyboard extends View {
    private double x,y;
    Paint[] pColor;
    int[] colors = {Color.rgb(255,0,0),
                    Color.rgb(255,127,0),
                    Color.rgb(255,255,0),
                    Color.rgb(127,255,0),
                    Color.rgb(0,255,0),
                    Color.rgb(0,255,255),
                    Color.rgb(0,127,255),
                    Color.rgb(0,0,255)};

    public Keyboard(Context context) {
        super(context);
    }

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Keyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected  void onDraw(Canvas canvas){
        super.onDraw(canvas);
        init();
        canvas.drawColor(Color.BLACK);
        for (int k = 0; k < 8; k++){
            Log.d("onDraw", "draw at "+x*k/8);
            canvas.drawRect((float) x * k / 8, (float) 0, (float) x * (k + 1) / 8, (float) y, pColor[k]);
        }
    }

    void init(){
        setFocusable(true);
        x = getWidth();
        y = getHeight();
        pColor = new Paint[8];
        for (int k = 0; k < 8; k++){
            pColor[k] = new Paint();
            pColor[k].setColor(colors[k]);
        }
    }
}
