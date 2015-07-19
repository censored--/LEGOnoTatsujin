package censored.legonotatsujin;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.List;

/**
 * Created by censored on 2015/07/18.
 */
public class Sound {
    int[] soundSrcs = {R.raw.c4,R.raw.d4,R.raw.e4,R.raw.f4,R.raw.g4/*440Hz*/,R.raw.a4,R.raw.b4,R.raw.c5};
    int[] soundIDs;
    SoundPool sounds;
    public Sound(Context context){
        sounds = new SoundPool(soundSrcs.length, AudioManager.STREAM_MUSIC,0);
        soundIDs = new int[soundSrcs.length];
        for (int k = 0; k < soundSrcs.length; k++){
            soundIDs[k] = sounds.load(context,soundSrcs[k],1);
        }
    }
    public int play(int id){
        return sounds.play(soundIDs[id],1.0F,1.0F,0,0,1.0F);
    }
    public void stop(int id){
        sounds.stop(id);
    }
}
