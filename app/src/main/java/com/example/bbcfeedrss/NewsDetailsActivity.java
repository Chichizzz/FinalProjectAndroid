package com.example.bbcfeedrss;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsDetailsActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView pubDateTextView;
    private ImageView imageView;
    private Button readMoreButton;
    private Button favoriteButton;
    private NewsItem newsItem;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        pubDateTextView = findViewById(R.id.pubDateTextView);
        imageView = findViewById(R.id.imageView);
        readMoreButton = findViewById(R.id.readMoreButton);
        favoriteButton = findViewById(R.id.favoriteButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get data from Intent
        Intent intent = getIntent();
        final String title = intent.getStringExtra("title");
        final String description = intent.getStringExtra("description");
        final String pubDate = intent.getStringExtra("pubDate");
        final String mediaUrl = intent.getStringExtra("mediaUrl");
        final String link = intent.getStringExtra("link");

        // Create a NewsItem object
        newsItem = new NewsItem(-1, title, description, pubDate, mediaUrl, link);

        // Set data to views
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        pubDateTextView.setText(pubDate);

        // Load image using AsyncTask
        new DownloadImageTask(imageView).execute(mediaUrl);

        // Check if the news item is already a favorite
        if (databaseHelper.isFavorite(newsItem)) {
            favoriteButton.setText("Remove from Favorites");
        } else {
            favoriteButton.setText("Add to Favorites");
        }

        // Set click listener for the favorite button
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseHelper.isFavorite(newsItem)) {
                    // Remove from favorites
                    databaseHelper.deleteFavorite(newsItem.getId());
                    favoriteButton.setText("Add to Favorites");
                } else {
                    // Add to favorites
                    boolean success = databaseHelper.addFavorite(newsItem.getTitle(), newsItem.getDescription(), newsItem.getPubDate(), newsItem.getMediaUrl(), newsItem.getLink());
                    if (success) {
                        favoriteButton.setText("Remove from Favorites");
                    }
                }
            }
        });

        // Set click listener for the "Read More" button
        readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the news link in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}
