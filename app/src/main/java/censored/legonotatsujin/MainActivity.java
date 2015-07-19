package censored.legonotatsujin;

import android.app.Activity;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements View.OnTouchListener{
    Keyboard keyboard;
    ScoreSurfaceView scoreSurfaceView;
    LEGOSurfaceView BlockView;
    List<ScoreSurfaceView.Block> blocks;
    SurfaceHolder scoreSurfaceHolder;
    Sound sound;
    HashMap<Integer,IdInfo> IdHashMap;
    final int BEATRATE = 25;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreSurfaceView = (ScoreSurfaceView) findViewById(R.id.ScoreView);
        BlockView =(LEGOSurfaceView) findViewById(R.id.BlockView);
        scoreSurfaceView.BlockView = BlockView;
        blocks = scoreSurfaceView.blocks;
        scoreSurfaceHolder = scoreSurfaceView.getHolder();
        keyboard = (Keyboard) findViewById(R.id.keyboard);
        keyboard.setOnTouchListener(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        IdHashMap = new HashMap<>();
        sound = new Sound(this);
    }
    /*
    @Override
    protected void onResume(){
    }
    @Override
    protected void onPause(){

    }*/

    public boolean onTouch(View v,MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int pointerId = event.getPointerId(pointerIndex);
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        int note = (int)(x/(v.getWidth()+1)*8);
        if (IdHashMap.containsKey(pointerId))
            note = IdHashMap.get(pointerId).note;
        else {
            IdHashMap.put(pointerId, new IdInfo(note,0L,0,0));
        }
        IdInfo idinfo = IdHashMap.get(pointerId);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                idinfo.time = System.currentTimeMillis();
                idinfo.soundId = sound.play(note);
                Log.d("onTouch", "ACTION_DOWN:" + pointerId + ":" + note);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                idinfo.soundId = sound.play(note);
                Log.d("onTouch", "ACTION_POINTER_DOWN:"+pointerId+":" + note);
                break;
            case MotionEvent.ACTION_UP:
                int time = (int)Math.round(1.0*(System.currentTimeMillis() - idinfo.time)
                        /(BEATRATE*20))%Integer.MAX_VALUE + 1;
                if (time <= 1)time = 0;
                else if (time >= 5) time = 6;
                if (time > 0 && BlockView.addBlock(time)){
                    scoreSurfaceView.addBlock(time);
                }
                Log.d("onTouch", "ACTION_UP:" + pointerId + ":" + note + ":" + time);
                sound.stop(idinfo.soundId);;
                IdHashMap.remove(pointerId);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("onTouch", "ACTION_POINTER_UP:" + pointerId + ":" + note);
                sound.stop(idinfo.soundId);;
                IdHashMap.remove(pointerId);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("onTouch","ACTION_MOVE" +pointerId+":" +note);
            default:
                break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class IdInfo{
        public int note, position, soundId;
        public long time;
        IdInfo (int note_,long time_,int position_,int soundId_){
            note = note_;
            time = time_;
            position = position_;
            soundId = soundId_;
        }
    }
}
