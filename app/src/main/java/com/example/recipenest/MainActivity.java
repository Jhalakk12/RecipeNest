package com.example.recipenest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipenest.Adapters.RandomRecipeAdapter;
import com.example.recipenest.Listeners.RandomRecipeResponseListener;
import com.example.recipenest.Listeners.RecipeClickListener;
import com.example.recipenest.Models.RandomRecipeApiResponse;
import com.example.recipenest.Models.Recipe;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private final RecipeClickListener recipeClickListener = id ->
            startActivity(new Intent(MainActivity.this, RecipeDetailsActivity.class)
                    .putExtra("id", id));
    ImageView likeImage;
   // SharedPreferences sharedPreferences;

    FirebaseFirestore db ;
    FirebaseAuth auth ;
    FirebaseUser user ;
    String userId ;
    ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;
    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            dialog.dismiss();
            randomRecipeAdapter = new RandomRecipeAdapter(MainActivity.this, response.recipes, recipeClickListener);

            if (recyclerView != null) {
                recyclerView.setAdapter(randomRecipeAdapter);
            }
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    List<String> tags = new ArrayList<>();
    SearchView searchView;
    ImageView icHomeImage;

    private void updateLikeImage(boolean isLiked) {
        likeImage.setImageResource(isLiked ? R.drawable.ic_liked : R.drawable.ic_unliked);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure layout is set first

        FirebaseApp.initializeApp(this);
         db = FirebaseFirestore.getInstance();
         auth = FirebaseAuth.getInstance();
         user = auth.getCurrentUser();
        // Check if user is null before accessing UID
        if (user != null) {
            userId = user.getUid();
        } else {
            Log.e("FirebaseAuth", "User is not logged in!");
        }



        ImageView likeIcon = findViewById(R.id.like_icon);
        likeIcon.setOnClickListener(v -> fetchLikedRecipes());


        // Initialize UI components safely
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");

        searchView = findViewById(R.id.searchView_home);
        recyclerView = findViewById(R.id.recycler_random);
        icHomeImage = findViewById(R.id.ic_home_image);

        // Check for null views
        if (searchView == null || recyclerView == null || icHomeImage == null) {
            Log.e("Debug", "One or more views are null!");
        }

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    tags.clear();
                    tags.add(query);
                    manager.getRandomRecipes(randomRecipeResponseListener, tags);
                    dialog.show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        if (icHomeImage != null) {
            icHomeImage.setOnClickListener(this::showPopupMenu);
        }

        manager = new RequestManager(this);

        // Initialize RecyclerView
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        }

        loadDefaultRecipes();
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void loadDefaultRecipes() {
        Resources res = getResources();
        String[] categories = res.getStringArray(R.array.tags);

        if (categories.length > 1 && !categories[1].isEmpty()) {
            tags.clear();
            tags.add(categories[1]);
            manager.getRandomRecipes(randomRecipeResponseListener, tags);
            dialog.show();
            Toast.makeText(this, "Fetching default recipes: " + categories[0], Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: No categories found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        Resources res = getResources();
        String[] categories = res.getStringArray(R.array.tags);

        for (String category : categories) {
            if (!category.isEmpty()) {
                popupMenu.getMenu().add(category);
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedCategory = Objects.requireNonNull(item.getTitle()).toString();
            tags.clear();
            tags.add(selectedCategory);
            manager.getRandomRecipes(randomRecipeResponseListener, tags);
            dialog.show();

            Toast.makeText(MainActivity.this, "Fetching recipes for: " + selectedCategory, Toast.LENGTH_SHORT).show();
            return true;
        });

        popupMenu.show();
    }

    private void fetchLikedRecipes() {


        db.collection("recipes")
                .whereEqualTo("isLikedByUsers." + userId, true) // Fetch only liked recipes
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> likedRecipes = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        likedRecipes.add(recipe);
                    }

                    // Update RecyclerView with liked recipes
                    randomRecipeAdapter = new RandomRecipeAdapter(MainActivity.this, likedRecipes, recipeClickListener);
                    recyclerView.setAdapter(randomRecipeAdapter);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching liked recipes", e));
    }
    public void toggleFavorite(String recipeId, boolean isLiked) {
        DocumentReference recipeRef = db.collection("recipes").document(recipeId);

        if (isLiked) {
            recipeRef.update("isLikedByUsers." + userId, true)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Recipe liked successfully"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error liking recipe", e));
        } else {
            recipeRef.update("isLikedByUsers." + userId, FieldValue.delete())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Recipe unliked successfully"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error unliking recipe", e));
        }
    }
}

