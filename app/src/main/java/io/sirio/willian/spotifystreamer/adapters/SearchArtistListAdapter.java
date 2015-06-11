package io.sirio.willian.spotifystreamer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.sirio.willian.spotifystreamer.R;
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by william on 6/7/15.
 */
public class SearchArtistListAdapter extends ArrayAdapter<Artist> {


    public SearchArtistListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchItemViewHolder searchItemViewHolder;
        Artist artist = getItem(position);
        if(convertView==null){
            convertView =LayoutInflater.from(getContext()).inflate(R.layout.search_row_item,parent,false);
            searchItemViewHolder = new SearchItemViewHolder();
            searchItemViewHolder.imageView= (ImageView) convertView.findViewById(R.id.searchItemImage);
            searchItemViewHolder.textView =(TextView) convertView.findViewById(R.id.searchItemTitle);
            convertView.setTag(searchItemViewHolder);

        }else{
            searchItemViewHolder =(SearchItemViewHolder) convertView.getTag();
        }
        searchItemViewHolder.textView.setText(artist.name);
        //get the first url image of the artist if the artist object have one
        if(artist.images.size()>0){
            Picasso.with(getContext()).load(artist.images.get(0).url).resize(100,100).into(searchItemViewHolder.imageView);
        }
        return convertView;
    }
    static class SearchItemViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
