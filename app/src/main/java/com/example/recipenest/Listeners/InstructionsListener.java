package com.example.recipenest.Listeners;

import com.example.recipenest.Models.InstructionsResponse;

import java.util.List;

public interface InstructionsListener {
    void didFetch(List<InstructionsResponse> response, String message);

    void didError(String message);
}
