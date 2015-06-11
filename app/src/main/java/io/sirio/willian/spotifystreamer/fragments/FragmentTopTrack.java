package io.sirio.willian.spotifystreamer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.sirio.willian.spotifystreamer.R;
import io.sirio.willian.spotifystreamer.adapters.TopTrackListAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Created by william on 6/9/15.
 */
public class FragmentTopTrack extends Fragment {
    private TopTrackListAdapter topTrackListAdapter;
    private Tracks tracks;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_tracks,container,false);
        ListView listView = (ListView) view.findViewById(R.id.fragmentTopTracksListView);
        topTrackListAdapter = new TopTrackListAdapter(getActivity(),R.layout.top_track_row_item);
        listView.setAdapter(topTrackListAdapter);
        ///if saved...
        if(savedInstanceState!=null&&savedInstanceState.containsKey("data")){
             ArrayList<Tracks> trackses =(ArrayList<Tracks>) savedInstanceState.getSerializable("data");
             tracks = trackses.get(0);
             topTrackListAdapter.addAll(tracks.tracks);
        }else{
            String artistId = getArguments().getString("artistId");
            new FetchTracks().execute(new Object[]{artistId});
        }

        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(tracks!=null){
            //tracks it's not serializable so...
            ArrayList<Tracks> trackses=new ArrayList<>();
            trackses.add(tracks);
            outState.putSerializable("data",trackses);
        }
    }

    private class FetchTracks extends AsyncTask<Object,Object,Object> {
        public FetchTracks() {
            super();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String artistId = params[0].toString();
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            Map<String,Object> options = new HashMap<>();
            options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
            Tracks artistTopTrack;

            try{
                 artistTopTrack = spotifyService.getArtistTopTrack(artistId,options);
            }catch (RetrofitError e){
                //error
                 artistTopTrack = null;
            }


            return artistTopTrack;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(o!=null){
                Tracks topTracks = (Tracks) o;
                tracks = topTracks;
                topTrackListAdapter.clear();
                topTrackListAdapter.addAll(topTracks.tracks);
            }else{
                Toast.makeText(getActivity(),"Sorry there was a error :(",Toast.LENGTH_SHORT).show();
            }

        }
    }

}
