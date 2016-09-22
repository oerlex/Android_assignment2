package com.example.oerlex.android_assignment2.mp3Player;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import com.example.oerlex.android_assignment2.R;

/**
 * A simple MP3 player skeleton for 2DV606 Assignment 2.
 *
 * Created by Oleksandr Shpak in 2013.
 * Ported to Android Studio by Kostiantyn Kucher in 2015.
 * Last modified by Kostiantyn Kucher on 04/04/2016.
 */
public class MP3Player extends AppCompatActivity {

    // This is an oversimplified approach which you should improve
    // Currently, if you exit/re-enter activity, a new instance of player is created
    // and you can't, e.g., stop the playback for the previous instance,
    // and if you click a song, you will hear another audio stream started
    public final MediaPlayer mediaPlayer = new MediaPlayer();
    private MyService service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the layout
        setContentView(R.layout.activity_mp3_player);

        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // Initialize the list of songs
        final ListView listView = (ListView) findViewById(R.id.list_view);
        final ArrayList<Song> songs = songList();

        listView.setAdapter(new PlayListAdapter(this, songs));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long arg3)
            {
                service.play(songs.get(pos));
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName cName, IBinder binder) {
            service = ((MyService.MusicBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName cName) {
            service = null;
        }
    };

    public void playPauseMusic(View view){
        if (service.getMediaPlayer().isPlaying()) {
            service.getMediaPlayer().pause();
        } else {
            try {
                service.getMediaPlayer().start();
            } catch (Exception e) {
                //There is no selected song
            }
        }
    }

    public void nextSong(View view){
        if (service.getCurrentSong() != null)
            service.play(service.getCurrentSong().getNext());
    }

    public void previousSong(View view){
        if (service.getCurrentSong() != null)
            service.play(service.getCurrentSong().getPrevious());
    }

    private class PlayListAdapter extends ArrayAdapter<Song>{
        public PlayListAdapter(Context context, ArrayList<Song> objects){
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View row, ViewGroup parent){
            Song data = getItem(position);

            row = getLayoutInflater().inflate(R.layout.layout_row, parent, false);

            TextView name = (TextView) row.findViewById(R.id.label);
            name.setText(String.valueOf(data));
            row.setTag(data);

            return row;
        }
    }

    /**
     * Checks the state of media storage. True if mounted;
     * @return
     */
    private boolean isStorageAvailable(){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Reads song list from media storage.
     * @return
     */
    private ArrayList<Song> songList(){
        ArrayList<Song> songs = new ArrayList<Song>();

        if(!isStorageAvailable()){
            Toast.makeText(this, R.string.nosd, Toast.LENGTH_SHORT).show();
            return songs;
        }
        Cursor music = getContentResolver().query( // using content resolver to read music from media storage
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.IS_MUSIC + " > 0 ",
                null, null
        );

        if (music.getCount() > 0){
            music.moveToFirst();
            Song previous = null;
            do{
                Song song = new Song(music.getString(0), music.getString(1), music.getString(2), music.getString(3));

                if (previous != null) {
                    previous.setNext(song);
                    song.setPrevious(previous);
                }
                previous = song;
                songs.add(song);
            }
            while(music.moveToNext());

            previous.setNext(songs.get(0));
        }
        music.close();

        songs.get(0).setPrevious(songs.get(songs.size()-1));
        songs.get(songs.size()-1).setNext(songs.get(0));
        return songs;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mp3_player, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_player:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
