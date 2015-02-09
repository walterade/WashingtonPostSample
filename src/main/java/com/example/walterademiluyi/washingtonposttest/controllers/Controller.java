package com.example.walterademiluyi.washingtonposttest.controllers;

import android.content.Context;

import com.example.walterademiluyi.washingtonposttest.R;
import com.example.walterademiluyi.washingtonposttest.http.WebRequest;
import com.example.walterademiluyi.washingtonposttest.models.Post;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by walterademiluyi on 2/8/15.
 */
public class Controller {
    Context context;
    static ArrayList<Post> posts;
    static HashMap<String, Post> postHashMap;

    public Controller(Context c) {
        context = c;
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
        context = null;
    }

    public ArrayList<Post> getPosts(boolean fromCache) {
        if (fromCache) {
            return posts;
        }

        WebRequest wr = new WebRequest().get(context.getString(R.string.POSTS_URL));
        switch (wr.go()) {
            case 200:
                JsonObject o = new Gson().fromJson(wr.getResponse(), JsonElement.class).getAsJsonObject();
                JsonArray a = o.getAsJsonArray("posts");
                Type type = new TypeToken<List<Post>>() {}.getType();
                posts = new Gson().fromJson(a, type);

                postHashMap = new HashMap<>();
                //add the posts to a hashmap for easy id based lookup
                for (int i = 0; i < posts.size(); i++) {
                    Post p = posts.get(i);
                    postHashMap.put( p.getId(), p );
                }
                return posts;
        }

        return posts;
    }

    public Post getPostById(String postId) {
        if (postHashMap != null) return postHashMap.get(postId);
        return null;
    }
}
