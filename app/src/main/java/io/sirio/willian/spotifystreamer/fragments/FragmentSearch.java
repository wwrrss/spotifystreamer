package io.sirio.willian.spotifystreamer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.Serializable;
import java.util.ArrayList;

import io.sirio.willian.spotifystreamer.R;
import io.sirio.willian.spotifystreamer.adapters.SearchArtistListAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * Created by william on 6/7/15.
 */
public class FragmentSearch extends Fragment {
    private SearchArtistListAdapter searchArtistListAdapter;
    private ListView listViewSearch;
    private ArrayList<Artist> artistArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistArrayList=new ArrayList<>();
        Log.i("c","create");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("data",artistArrayList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstance){
        searchArtistListAdapter = new SearchArtistListAdapter(getActivity(),R.layout.search_row_item);
            if(savedInstance!=null && savedInstance.containsKey("data")){
                Serializable s = savedInstance.getSerializable("data");
                artistArrayList = (ArrayList<Artist>) s;
                searchArtistListAdapter.addAll(artistArrayList);
            }

            View view = inflater.inflate(R.layout.fragment_search,container,false);
            SearchView searchView = (SearchView) view.findViewById(R.id.fragmentSearchSearchView);
            searchView.setIconifiedByDefault(false);
            searchView.setQueryHint(getString(R.string.search_hint));
            listViewSearch = (ListView) view.findViewById(R.id.fragmentSearchListView);
            listViewSearch.setAdapter(searchArtistListAdapter);
            listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Artist artist = (Artist) parent.getItemAtPosition(position);
                    ((OnArtistClickListener) getActivity()).onArtistItemClick(artist.id, artist.name);
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    new TaskFetch().execute(new Object[]{s});
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
            /*
            editSearch.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    TextView textView = (TextView) v;
                    if(textView.getText().length()>3){
                        String searchTerm = textView.getText().toString();
                        new TaskFetch().execute(new Object[]{searchTerm});
                    }
                    return false;
                }
            });*/
            return view;





    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setInitialSavedState(SavedState state) {
        super.setInitialSavedState(state);
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private class TaskFetch extends AsyncTask<Object,Object,Object>{
        @Override
        protected Object doInBackground(Object... params) {
            String searchTerm = params[0].toString();
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            ArtistsPager artistsPager;
            //added try catch :)
            try{
                artistsPager = spotifyService.searchArtists(searchTerm);
            }catch (RetrofitError e){
                return null;
            }
          return artistsPager.artists.items;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(o!=null){
                ArrayList<Artist> artists = (ArrayList<Artist>) o;
                if(artists.size()>0){
                    searchArtistListAdapter.clear();
                    searchArtistListAdapter.addAll(artists);
                    searchArtistListAdapter.notifyDataSetChanged();
                    artistArrayList.clear();
                    artistArrayList.addAll(artists);
                }else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Sorry, we couldn't find anything, :( type again",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }else{
                Toast.makeText(getActivity(),"Sorry there was a error :(",Toast.LENGTH_SHORT).show();
            }


        }
    }

    public interface OnArtistClickListener{
        public void onArtistItemClick(String artistId,String artistName);
    }


}
