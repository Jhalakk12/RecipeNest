package com.example.recipenest.Listeners;

import com.example.recipenest.Models.RandomRecipeApiResponse;

public interface RandomRecipeResponseListener {
    void didFetch(RandomRecipeApiResponse response, String message);

    void didError(String message);
}
