package com.example.walterademiluyi.washingtonposttest.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.walterademiluyi.washingtonposttest.controllers.Controller;
import com.example.walterademiluyi.washingtonposttest.models.Post;
import com.example.walterademiluyi.washingtonposttest.utils.HelperUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class PostLoader extends AsyncTaskLoader<ArrayList<Post>> {
    private static final boolean DEBUG = false;

    final HelperUtils.Configuration mLastConfig = new HelperUtils.Configuration();

    ArrayList<Post> posts;
    HashMap<String, Post> postHashMap;
    Controller controller;

    public PostLoader(Context c) {
        super(c);
        controller = new Controller(c);
    }

    public void dispose() {
        if (postHashMap != null) {
            postHashMap.clear();
            postHashMap = null;
        }
        if (posts != null) {
            posts.clear();
            posts = null;
        }
        if (controller != null) {
            controller.dispose();
            controller = null;
        }
    }

    @Override
    public ArrayList<Post> loadInBackground() {
        ArrayList<Post> posts = controller.getPosts(false);
        return posts;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(ArrayList<Post> posts) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (posts != null) {
                onReleaseResources(posts);
            }
        }

        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post lhs, Post rhs) {
                return lhs.toString().compareTo(rhs.toString());
            }
        });

        ArrayList<Post> oldposts = posts;

        this.posts = posts;


        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(posts);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldposts != null) {
            onReleaseResources(oldposts);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        boolean fromCache;

        if (posts == null) {
            posts = controller.getPosts(true);
            if (posts != null) fromCache = true;
        }

        if (posts != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(posts);
        }

        // Has something interesting in the configuration changed since we
        // last built the app list?
        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || posts == null || configChange) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(ArrayList<Post> posts) {
        super.onCanceled(posts);

        // At this point we can release the resources associated with 'catalogs'
        // if needed.
        onReleaseResources(posts);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (posts != null) {
            onReleaseResources(posts);
            posts = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(ArrayList<Post> product) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
        //if (product != null) product.clear();
    }

    public Post getPostById(String postId) {
        return controller.getPostById(postId);
    }
}