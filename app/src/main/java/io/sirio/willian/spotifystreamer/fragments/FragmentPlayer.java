package io.sirio.willian.spotifystreamer.fragments;

import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.sirio.willian.spotifystreamer.R;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by william on 6/14/15.
 */
public class FragmentPlayer extends DialogFragment {
    @InjectView(R.id.tAlbumName) TextView tAlbumName;
    @InjectView(R.id.tArtistName) TextView tArtistName;
    @InjectView(R.id.tMusicName) TextView tMusicName;
    @InjectView(R.id.imageAlbum) ImageView imageViewAlbum;
    @InjectView(R.id.seekBar)SeekBar seekBar;
    @InjectView(R.id.tMusicStart) TextView tMusicStart;
    @InjectView(R.id.tMusicEnd) TextView tMusicEnd;
    @InjectView(R.id.buttonBack) ImageButton buttonBack;
    @InjectView(R.id.buttonPlay) ImageButton buttonPlay;
    @InjectView(R.id.buttonNext) ImageButton buttonNext;

    private MediaPlayer mediaPlayer;
    private boolean isPrepared=false;
    private boolean isPlaying=false;
    private int position;
    private Track currentTrack;
    private Tracks tracks;
    private long currentTrackPreviewDuration;
    private Thread threadPosition;
    private int lastPositionBeforeOrientationChange=-1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mediaPlayer = new MediaPlayer();
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onStop(){
        super.onStop();
        mediaPlayer.release();
        mediaPlayer =null;

    }
    @Override
    public void onDestroy(){
        super.onDestroy();

    }
    private void setCurrentTrack(){
      currentTrack = tracks.tracks.get(position);
      tAlbumName.setText(currentTrack.album.name);
      tMusicName.setText(currentTrack.name);
      tArtistName.setText(currentTrack.artists.get(0).name);

      if(currentTrack.album.images.size()>0){
          if(currentTrack.album.images.get(0).url!=null)
              Picasso.with(getActivity()).load(currentTrack.album.images.get(0).url).resize(200,200).into(imageViewAlbum);
      }
      if(mediaPlayer.isPlaying()){
          mediaPlayer.stop();
      }
      mediaPlayer.reset();
      isPrepared=false;
      isPlaying=false;
      tMusicStart.setText("0:00");
        //play it!
      buttonPlay.performClick();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mediaPlayer.isPlaying()){
            outState.putSerializable("lastPosition",mediaPlayer.getCurrentPosition());
            outState.putInt("position",position);

        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            lastPositionBeforeOrientationChange = savedInstanceState.getInt("lastPosition");
            position = savedInstanceState.getInt("position");
        }else{

            position = getArguments().getInt("position");
        }
        mediaPlayer = new MediaPlayer();
        View view = inflater.inflate(R.layout.fragment_play_music,container,false);
        ButterKnife.inject(this,view);
        final ArrayList<Tracks> trackses = (ArrayList<Tracks>) getArguments().getSerializable("tracks");

        tracks = trackses.get(0);
        currentTrack = tracks.tracks.get(position);
        tAlbumName.setText(currentTrack.album.name);
        tMusicName.setText(currentTrack.name);
        tArtistName.setText(currentTrack.artists.get(0).name);
        //tMusicEnd.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(currentTrack.duration_ms),TimeUnit.MILLISECONDS.toSeconds(currentTrack.duration_ms)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTrack.duration_ms))));
        if(currentTrack.album.images.size()>0){
            if(currentTrack.album.images.get(0).url!=null)
                Picasso.with(getActivity()).load(currentTrack.album.images.get(0).url).resize(200,200).into(imageViewAlbum);
        }

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(!isPlaying){

                        buttonPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));

                        if(isPrepared){
                            mediaPlayer.start();
                        }else{
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(getActivity(), Uri.parse(currentTrack.preview_url));
                            mediaPlayer.prepareAsync();
                        }
                        isPlaying=true;
                    }else{
                        buttonPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                        mediaPlayer.pause();
                        isPlaying=false;
                    }


                } catch ( IOException e) {
                    e.printStackTrace();

                }
            }
        });
        //play right now!
        buttonPlay.performClick();
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if(position==tracks.tracks.size()){
                    position=0;
                }
                setCurrentTrack();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if (position == -1) {
                    position = tracks.tracks.size() - 1;
                }
                setCurrentTrack();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                if(lastPositionBeforeOrientationChange>0){
                    mp.seekTo(lastPositionBeforeOrientationChange);
                    lastPositionBeforeOrientationChange=-1;
                }
                mp.start();

                threadPosition = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            try{
                                Thread.sleep(1000);
                                seekBar.setMax((int)currentTrackPreviewDuration);
                                seekBar.setProgress(mp.getCurrentPosition());
                                tMusicStart.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition()),TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition()))));


                            }catch (Exception e){

                            }
                        }
                    }
                });
                threadPosition.start();
                isPrepared=true;
                currentTrackPreviewDuration=mp.getDuration();

                tMusicEnd.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(currentTrackPreviewDuration),TimeUnit.MILLISECONDS.toSeconds(currentTrackPreviewDuration)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTrackPreviewDuration))));

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                buttonPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                mp.reset();
                isPrepared=false;
                isPlaying=false;
                if(threadPosition!=null && threadPosition.isAlive()){
                    threadPosition.interrupt();
                    threadPosition=null;
                }
                tMusicStart.setText("0:00");
            }

        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(seekBar.getProgress());
                }

            }
        });

        return view;
    }
}
