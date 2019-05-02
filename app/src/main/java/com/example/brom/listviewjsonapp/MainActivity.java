package com.example.brom.listviewjsonapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


// Create a new class, Mountain, that can hold your JSON data

// Create a ListView as in "Assignment 1 - Toast and ListView"

// Retrieve data from Internet service using AsyncTask and the included networking code

// Parse the retrieved JSON and update the ListView adapter

// Implement a "refresh" functionality using Android's menu system


public class MainActivity extends AppCompatActivity {
    private ArrayList<Mountain> mountainArrayList=new ArrayList<>(); //En ny ArrayList för bergen att vara i.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayAdapter<Mountain> adapter=new ArrayAdapter<Mountain>(this,R.layout.list_item_textview,R.id.list_item_textview,mountainArrayList);
        final ListView my_listview=(ListView) findViewById(R.id.list_item_textview);
        my_listview.setAdapter(adapter);

        my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //När man klickar på ett item körs metoden.
                Toast.makeText(getApplicationContext(), mountainArrayList.get(position).info(), Toast.LENGTH_LONG).show(); //En toast med informationen om just det item som tryckts på skrivs ut.
            }
        });
        new FetchData().execute(); //Datan hämtas in först.
    }
 @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
@Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            mountainArrayList.clear(); //Tömmer ArrayListen.
            new FetchData().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private class FetchData extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            // These two variables need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a Java string.
            String jsonStr = null;

            try {
                // Construct the URL for the Internet service
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in
                // attempting to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }
        }
        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);


            // This code executes after we have received our data. The String object o holds
            // the un-parsed JSON string or is null if we had an IOException during the fetch.

            // Implement a parsing code that loops through the entire JSON and creates objects
            // of our newly created Mountain class.
            Log.d("Mountain",o);

            try{
                JSONArray mtarray = new JSONArray(o); //Skapar en array för bergen att itereras in i.
                Log.d("Mountain",mtarray.get(0).toString());

                for (int i = 0; i < mtarray.length();i++){ //Itererar in bergen till arrayen.
                    JSONObject mountain = mtarray.getJSONObject(i);
                    String name = mountain.getString("name");
                    int height = mountain.getInt("size");
                    String location = mountain.getString("location");
                    mountainArrayList.add(new Mountain(name,location,height)); //Lägger till varje berg som går genom loopen.

                }
                ArrayAdapter<Mountain> adapter=new ArrayAdapter<Mountain>(getApplicationContext(),R.layout.list_item_textview,R.id.list_item_textview,mountainArrayList);
                final ListView my_listview=(ListView) findViewById(R.id.list_item_textview);
                my_listview.setAdapter(adapter);
            }catch (JSONException e){
                Log.e("donk","E:"+e.getMessage());
            }



            }

        }

    }


