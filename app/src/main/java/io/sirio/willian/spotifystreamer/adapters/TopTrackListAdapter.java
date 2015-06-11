package io.sirio.willian.spotifystreamer.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.sirio.willian.spotifystreamer.R;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by william on 6/9/15.
 */
public class TopTrackListAdapter extends ArrayAdapter<Track> {
    public TopTrackListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopTrackViewHolder topTrackViewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.top_track_row_item,parent,false);
            topTrackViewHolder = new TopTrackViewHolder();
            topTrackViewHolder.imageView = (ImageView) convertView.findViewById(R.id.topTrackImageView);
            topTrackViewHolder.textView1 = (TextView) convertView.findViewById(R.id.topTrackText1);
            topTrackViewHolder.textView2 = (TextView) convertView.findViewById(R.id.topTrackText2);
            convertView.setTag(topTrackViewHolder);
        }else {
            topTrackViewHolder = (TopTrackViewHolder) convertView.getTag();
        }
        Track track = getItem(position);
        if(track.album.images.size()>0){
            Picasso.with(getContext()).load(track.album.images.get(0).url).resize(100,100).into(topTrackViewHolder.imageView);
        }
        topTrackViewHolder.textView1.setText(track.name);
        topTrackViewHolder.textView2.setText(track.album.name);

        return convertView;
    }
    public static class TopTrackViewHolder{
        ImageView imageView;
        TextView textView1;
        TextView textView2;
    }

}
