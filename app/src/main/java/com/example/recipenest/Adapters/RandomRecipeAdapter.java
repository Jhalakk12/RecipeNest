package com.example.recipenest.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipenest.Listeners.RecipeClickListener;
import com.example.recipenest.Models.Recipe;
import com.example.recipenest.R;
import com.google.firebase.firestore.FieldValue;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;



public class RandomRecipeAdapter extends RecyclerView.Adapter<RandomRecipeViewHolder> {
    Context context;
    List<Recipe> list;
    RecipeClickListener listener;

    public RandomRecipeAdapter(Context context, List<Recipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RandomRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RandomRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RandomRecipeViewHolder holder, int position) {
        Recipe recipe = list.get(position);

        holder.textView_title.setText(recipe.title);
        holder.textView_title.setSelected(true);
        holder.textView_likes.setText(recipe.aggregateLikes + " Likes");
        holder.textView_servings.setText(recipe.servings + " Servings");
        holder.textView_time.setText(recipe.readyInMinutes + " Minutes");
        Picasso.get().load(recipe.image).into(holder.imageView_food);


        if (holder.likeImage != null) {
            holder.likeImage.setImageResource(recipe.isLiked ? R.drawable.ic_liked : R.drawable.ic_unliked);
            // Handle like button click
            holder.likeImage.setOnClickListener(v -> toggleLike(recipe, holder));
        }
        else{
            Log.d("like image null ", "likeimage is coming null");
        }


        holder.random_list_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });
    }
    private void toggleLike(Recipe recipe, RandomRecipeViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("recipes").document(String.valueOf(recipe.id));

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get current like status
                Boolean isCurrentlyLiked = documentSnapshot.getBoolean("isLikedByUsers." + userId);
                if (isCurrentlyLiked == null) isCurrentlyLiked = false;

                int newLikeCount = documentSnapshot.getLong("aggregateLikes") != null
                        ? documentSnapshot.getLong("aggregateLikes").intValue() + (isCurrentlyLiked ? -1 : 1)
                        : (isCurrentlyLiked ? -1 : 1);

                recipe.isLiked = !isCurrentlyLiked;
                recipe.aggregateLikes = newLikeCount;

                // Update Firestore with like/unlike changes
                Map<String, Object> updates = new HashMap<>();
                updates.put("aggregateLikes", newLikeCount);

                if (recipe.isLiked) {
                    updates.put("isLikedByUsers." + userId, true);
                } else {
                    updates.put("isLikedByUsers." + userId, FieldValue.delete()); // Remove from favorites
                }

                docRef.update(updates)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Like status updated"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error updating like status", e));

                // Refresh UI
                holder.likeImage.setImageResource(recipe.isLiked ? R.drawable.ic_liked : R.drawable.ic_unliked);
                holder.textView_likes.setText(recipe.aggregateLikes + " Likes");

                // Remove from UI if unliked
                if (!recipe.isLiked) {
                    list.remove(recipe);
                    notifyDataSetChanged();
                }

            } else {
                // Handle new recipe entry if not found in Firestore
                Map<String, Object> newRecipeData = new HashMap<>();
                newRecipeData.put("id", recipe.id);
                newRecipeData.put("title", recipe.title);
                newRecipeData.put("image", recipe.image);
                newRecipeData.put("aggregateLikes", 1);
                newRecipeData.put("isLikedByUsers", Collections.singletonMap(userId, true));

                recipe.isLiked = true;
                recipe.aggregateLikes = 1;

                docRef.set(newRecipeData)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "New recipe created"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error creating recipe document", e));

                holder.likeImage.setImageResource(R.drawable.ic_liked);
                holder.textView_likes.setText("1 Likes");
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching recipe document", e));
    }
//    private void toggleLike(Recipe recipe, RandomRecipeViewHolder holder) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        String userId = auth.getCurrentUser().getUid();
//        DocumentReference docRef = db.collection("recipes").document(String.valueOf(recipe.id));
//
//        docRef.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                // Document exists, update like status
//                boolean isCurrentlyLiked = documentSnapshot.getBoolean("isLikedByUsers." + userId);
//                int newLikeCount = documentSnapshot.getLong("aggregateLikes").intValue() + (isCurrentlyLiked ? -1 : 1);
//
//                recipe.isLiked = !isCurrentlyLiked;
//                recipe.aggregateLikes = newLikeCount;
//
//                holder.likeImage.setImageResource(recipe.isLiked ? R.drawable.ic_liked : R.drawable.ic_unliked);
//                holder.textView_likes.setText(recipe.aggregateLikes + " Likes");
//
//                // Update Firestore
//                docRef.update("aggregateLikes", newLikeCount, "isLikedByUsers." + userId, recipe.isLiked)
//                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Like status updated"))
//                        .addOnFailureListener(e -> Log.e("Firestore", "Error updating like status", e));
//            } else {
//                // Document does not exist, create new entry
//                Map<String, Object> newRecipeData = new HashMap<>();
//                newRecipeData.put("id", recipe.id);
//                newRecipeData.put("title", recipe.title);
//                newRecipeData.put("image", recipe.image);
//                newRecipeData.put("aggregateLikes", recipe.aggregateLikes+1);
//                newRecipeData.put("isLikedByUsers", Collections.singletonMap(userId, true));
//
//               recipe.isLiked = true;
//                recipe.aggregateLikes = recipe.aggregateLikes+1;
//                holder.likeImage.setImageResource(R.drawable.ic_liked);
//                holder.textView_likes.setText(recipe.aggregateLikes + " Likes");
//
//                docRef.set(newRecipeData)
//                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "New recipe created"))
//                        .addOnFailureListener(e -> Log.e("Firestore", "Error creating recipe document", e));
//            }
//        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching recipe document", e));
//    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

class RandomRecipeViewHolder extends RecyclerView.ViewHolder {
    CardView random_list_container;
    TextView textView_title, textView_servings, textView_likes, textView_time;
    ImageView imageView_food, likeImage;

    public RandomRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_servings = itemView.findViewById(R.id.textView_servings);
        textView_likes = itemView.findViewById(R.id.textView_likes);
        textView_time = itemView.findViewById(R.id.textView_time);
        imageView_food = itemView.findViewById(R.id.imageView_food);
        likeImage = itemView.findViewById(R.id.like_image); // Add like button

    }
}