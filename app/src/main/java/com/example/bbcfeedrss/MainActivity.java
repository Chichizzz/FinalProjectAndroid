package com.example.bbcfeedrss;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> titles;
    private ArrayList<String> descriptions;
    private ArrayList<String> links;
    private ArrayList<String> pubDates; // New ArrayList for pubDate
    private ArrayList<String> mediaUrls; // New ArrayList for media thumbnail URLs

    private Button refreshButton;
    private Button favoritesButton; // Add favoritesButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        listView = findViewById(R.id.listView);
        refreshButton = findViewById(R.id.refreshButton);
        favoritesButton = findViewById(R.id.favoritesButton); // Initialize favoritesButton

        // Initialize ArrayLists
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();
        links = new ArrayList<>();
        pubDates = new ArrayList<>(); // Initialize pubDates ArrayList
        mediaUrls = new ArrayList<>(); // Initialize mediaUrls ArrayList

        // Set item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch news details activity when an item is clicked
                Intent intent = new Intent(MainActivity.this, NewsDetailsActivity.class);
                // Pass necessary information to NewsDetailsActivity
                intent.putExtra("title", titles.get(position));
                intent.putExtra("description", descriptions.get(position));
                intent.putExtra("pubDate", pubDates.get(position));
                intent.putExtra("mediaUrl", mediaUrls.get(position));
                intent.putExtra("link", links.get(position));
                startActivity(intent);
            }
        });

        // Handle refresh button click
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast
                Toast.makeText(MainActivity.this, "Refreshing news...", Toast.LENGTH_SHORT).show();
                // Execute AsyncTask again for fetching news headlines
                new ProcessInBackground().execute();
            }
        });

        // Handle favorites button click
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open FavoritesActivity
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });

        // Fetch news headlines when the app starts
        new ProcessInBackground().execute();
    }

    // AsyncTask class for fetching news headlines in the background
    public class ProcessInBackground extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Busy loading rss feed! Please wait!");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (eventType == XmlPullParser.START_TAG) {
                            String tagName = xpp.getName();
                            if (tagName.equalsIgnoreCase("item")) {
                                insideItem = true;
                            } else if (insideItem) {
                                if (tagName.equalsIgnoreCase("title")) {
                                    titles.add(xpp.nextText());
                                } else if (tagName.equalsIgnoreCase("description")) {
                                    descriptions.add(xpp.nextText());
                                } else if (tagName.equalsIgnoreCase("link")) {
                                    links.add(xpp.nextText());
                                } else if (tagName.equalsIgnoreCase("pubDate")) {
                                    pubDates.add(xpp.nextText());
                                } else if (tagName.equalsIgnoreCase("media:thumbnail")) {
                                    // Check if the tag has a namespace
                                    String mediaUrl = xpp.getAttributeValue(null, "url");
                                    mediaUrls.add(mediaUrl);
                                }
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Dismiss progress dialog
            progressDialog.dismiss();
            // Set up adapter for ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, titles);
            listView.setAdapter(adapter);
        }
    }

    // Method to get InputStream from URL
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}
