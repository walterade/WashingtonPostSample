package com.example.walterademiluyi.washingtonposttest.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.walterademiluyi.washingtonposttest.R;
import com.example.walterademiluyi.washingtonposttest.models.Post;

import java.util.List;

/**
 * Created by walterademiluyi on 2/8/15.
 */
public class PostAdapter extends ArrayAdapter<Post> {
    public PostAdapter(Context context) {
        super(context, 0);
    }

    List<Post> items;

    private class ViewHolder {
        int position;
        public TextView post;
        public TextView date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder vh = null;

        int type = getItemViewType(position);
        Post p = getItem(position);

        if (v != null) {
            vh = (ViewHolder) v.getTag();
        }

        if (v == null) {
            vh = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_post, parent, false);

            vh.date = (TextView) v.findViewById(R.id.post_date);
            vh.post = (TextView) v.findViewById(R.id.post_title);

            v.setTag(vh);
        }

        if (position != vh.position) {
            vh.position = position;
        }

        vh.date.setText(p.getDate().replace(' ', '\n'));
        vh.post.setText(Html.fromHtml( p.getTitle() ));

        return v;
    }

}