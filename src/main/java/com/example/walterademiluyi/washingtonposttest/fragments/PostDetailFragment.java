package com.example.walterademiluyi.washingtonposttest.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.walterademiluyi.washingtonposttest.R;
import com.example.walterademiluyi.washingtonposttest.controllers.Controller;
import com.example.walterademiluyi.washingtonposttest.loaders.PostLoader;
import com.example.walterademiluyi.washingtonposttest.models.Post;

import java.util.ArrayList;

/**
 * A fragment representing a single Post detail screen.
 * This fragment is either contained in a {@link com.example.walterademiluyi.washingtonposttest.activities.PostListActivity}
 * in two-pane mode (on tablets) or a {@link com.example.walterademiluyi.washingtonposttest.activities.PostDetailActivity}
 * on handsets.
 */
public class PostDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private String postId;
    //private TextView detail;
    private WebView detail;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            postId = getArguments().getString(ARG_ITEM_ID);
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_detail, container, false);

        //detail = ((TextView) rootView.findViewById(R.id.post_detail));
        detail = ((WebView) rootView.findViewById(R.id.post_detail));

        return rootView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new PostLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        ArrayList<Post> posts = (ArrayList<Post>) data;

        PostLoader postLoader = (PostLoader) loader;
        update(postLoader.getPostById(postId));
    }

    @Override
    public void onLoaderReset(Loader loader) {
        loader.abandon();
    }

    public void update(Post post) {
        ArrayList<Post> posts = new Controller(getActivity()).getPosts(true);

        // Show the dummy content as text in a TextView.
        if (post != null) {
            detail.loadData(post.getContent(), "text/html", "utf-8");
            //detail.setText(Html.fromHtml(post.getContent()));
        }

    }
}
