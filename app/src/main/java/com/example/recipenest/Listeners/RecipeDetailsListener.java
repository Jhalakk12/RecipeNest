package com.example.recipenest.Listeners;

import com.example.recipenest.Models.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponse response, String message);

    void didError(String message);
}
