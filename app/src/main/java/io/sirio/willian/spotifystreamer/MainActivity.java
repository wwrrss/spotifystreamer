package io.sirio.willian.spotifystreamer;

import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;
import java.util.ArrayList;

import io.sirio.willian.spotifystreamer.fragments.FragmentPlayer;
import io.sirio.willian.spotifystreamer.fragments.FragmentSearch;
import io.sirio.willian.spotifystreamer.fragments.FragmentTopTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class MainActivity extends ActionBarActivity implements FragmentSearch.OnArtistClickListener,FragmentTopTrack.OnTrackClickListener {
    private boolean mTwoPane=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.topTracksContainer)!=null){
            mTwoPane=true;
        }else{
            if(savedInstanceState==null){

                Fragment fragmentSearch = new FragmentSearch();
                FragmentManager fm =getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.mainLinearLayout, fragmentSearch);
                ft.commit();
            }

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

    @Override
    public void onArtistItemClick(String artistId,String artistName) {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        Fragment fragmentTopTrack = new FragmentTopTrack();
        Bundle bundle = new Bundle();
        bundle.putString("artistId",artistId);
        fragmentTopTrack.setArguments(bundle);
        if(mTwoPane){
            ft.replace(R.id.topTracksContainer,fragmentTopTrack);
        }else{

            ft.replace(R.id.mainLinearLayout,fragmentTopTrack);
        }
        ft.addToBackStack("topTrack");
        ft.commit();
        getSupportActionBar().setTitle("Top 10 Tracks");
        getSupportActionBar().setSubtitle(artistName);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle("");
    }

    @Override
    public void onTrackClick(Tracks tracks,int position) {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        FragmentPlayer fp = new FragmentPlayer();
        Bundle bundle = new Bundle();
        ArrayList<Tracks> tracksList = new ArrayList<>();
        tracksList.add(tracks);
        bundle.putSerializable("tracks",tracksList);
        bundle.putInt("position",position);
        fp.setArguments(bundle);

        if(mTwoPane){
            fp.show(ft,"PLAYER");
        }else{
            ft.replace(R.id.mainLinearLayout,fp);
            ft.addToBackStack("fp");
            ft.commit();
        }

    }
}
