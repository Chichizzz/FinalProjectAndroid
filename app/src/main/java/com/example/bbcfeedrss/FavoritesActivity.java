package com.example.bbcfeedrss;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bbcfeedrss.NewsItem;
import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private ListView favoritesListView;
    private ArrayList<NewsItem> favoritesList;
    private ArrayAdapter<NewsItem> adapter; // Change to ArrayAdapter
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Initialize views
        favoritesListView = findViewById(R.id.favoritesListView);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get all favorite news items from the database
        favoritesList = databaseHelper.getAllFavorites();

        // Set up the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoritesList); // Use ArrayAdapter

        // Set the adapter to the ListView
        favoritesListView.setAdapter(adapter);

        // Set click listener for the ListView items
        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked news item
                NewsItem newsItem = favoritesList.get(position);

                // Create an intent to open the NewsDetailsActivity
                Intent intent = new Intent(FavoritesActivity.this, NewsDetailsActivity.class);
                intent.putExtra("title", newsItem.getTitle());
                intent.putExtra("description", newsItem.getDescription());
                intent.putExtra("pubDate", newsItem.getPubDate());
                intent.putExtra("mediaUrl", newsItem.getMediaUrl());
                intent.putExtra("link", newsItem.getLink());
                startActivity(intent);
            }
        });

        // Set long click listener for the ListView items to delete favorites
        favoritesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked news item
                NewsItem newsItem = favoritesList.get(position);

                // Delete the news item from favorites
                databaseHelper.deleteFavorite(newsItem.getId());

                // Remove the news item from the list and update the adapter
                favoritesList.remove(position);
                adapter.notifyDataSetChanged();

                return true;
            }
        });
    }
}
