package censored.legonotatsujin;

import android.app.Activity;
import android.graphics.Canvas;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{

    Button button2,button3,button4,button6;
    ScoreSurfaceView scoreSurfaceView;
    List<ScoreSurfaceView.Block> blocks;
    SurfaceHolder scoreSurfaceHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button6 = (Button) findViewById(R.id.button6);
        scoreSurfaceView = (ScoreSurfaceView) findViewById(R.id.ScoreView);
        blocks = scoreSurfaceView.blocks;
        scoreSurfaceHolder = scoreSurfaceView.getHolder();
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button6.setOnClickListener(this);
    }
    public void onClick(View v) {
        if (v == button2){
            scoreSurfaceView.addBlock(2);
        } else if (v == button3){
            scoreSurfaceView.addBlock(3);
        } else if (v == button4) {
            scoreSurfaceView.addBlock(4);
        } else if (v == button6){
            scoreSurfaceView.addBlock(6);
        }
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
}
