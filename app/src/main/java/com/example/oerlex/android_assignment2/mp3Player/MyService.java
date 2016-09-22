package com.example.oerlex.android_assignment2.mp3Player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.example.oerlex.android_assignment2.R;


/**
 * Created by Oerlex on 20.09.2016.
 */
public class MyService extends Service implements MediaPlayer.OnPreparedListener {
    private Song currentSong;
    private MusicBinder musicBinder = null;
    private MediaPlayer mediaPlayer = null;


    @Override
    public void onCreate(){
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        musicBinder = new MusicBinder();
    }

    @Override
    public IBinder onBind(Intent intent){
        return musicBinder;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return true;
    }

    /**
     * Uses mediaPlayer to play the selected song.
     * The sequence of media player operations is crucial for it to work.
     * @param song
     */
    public void play(final Song song){
        if(song == null) return;
        try{
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();

            mediaPlayer.reset(); // reset the resource of player
            mediaPlayer.setDataSource(this, Uri.parse(song.getPath()));
            mediaPlayer.prepare(); // prepare the resource
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() // handle the completion
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    play(song.getNext());
                }
            });
            currentSong = song;
            mediaPlayer.start();

            // assign the song name to songName
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MP3Player.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this)
                    .setContentIntent(pi)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentTitle("Music is playing..")
                    .setContentText("Right here").build();
            startForeground(1, notification);
        }
        catch(Exception e)
        {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public class MusicBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}

