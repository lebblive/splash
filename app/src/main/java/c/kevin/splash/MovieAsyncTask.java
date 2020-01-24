package c.kevin.splash;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieAsyncTask extends AsyncTask<Void,Void, List<Movie>> {

    // i don't want for my recycler depend on mainActivity
    private WeakReference<View> view;
    private ArrayList<Movie> movies=new ArrayList<>();

    //ctor
    MovieAsyncTask(View view){
        this.view = new WeakReference<>(view);
    }
    // code that run in the backgound
    @Override
    public List<Movie>  doInBackground(Void... voids) {

        try {
            // convert the JSON on String use the read methode
            InputStream in = new URL("https://api.androidhive.info/json/movies.json")
                    .openConnection()
                    .getInputStream();
            String json=read(in);
            JSONArray jsonArray = new JSONArray(json);
            ArrayList<Integer> movieYears = new ArrayList<>();
            // get the content
            for (int i = 0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getString("title");
                String image = jsonObject.getString("image");
                double rating=jsonObject.getDouble("rating");
                int releaseYear=jsonObject.getInt("releaseYear");

                JSONArray genreArray = jsonObject.getJSONArray("genre");
                ArrayList<String> genre = new ArrayList<>();

                //change the jsonArray to normal
                for (int a=0;a<genreArray.length();a++){
                    genre.add(genreArray.getString(a));
                }

                // add in the array
                movieYears.add(releaseYear);
                //range from oldest to youngest
                Collections.sort(movieYears,Collections.reverseOrder());

                // add in movie in the order

                if (movies.size()<jsonArray.length()){
                    movies.add(new Movie(title,image,rating,movieYears.get(i),genre));
                }
                       //todo: rajouter un tempp danimation
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @SuppressLint("Recycle")
    private void adDataBase(){

        // db
        SQLiteDatabase db = new MoviesDbHelper(view.get().getContext()).getWritableDatabase();
        // get all
        Cursor cursor = db.rawQuery("SELECT * FROM Movies",null);
        ContentValues contentValues = new ContentValues();
        // get each obj
        for (int i = 0; i<movies.size();i++) {
            int id = cursor.getCount();
            String title = movies.get(i).getTitle();
            String image = movies.get(i).getImage();
            Double rating = movies.get(i).getRating();
            int releaseYear = movies.get(i).getReleaseYear();
            ArrayList<String> genre = movies.get(i).getGenre();

            contentValues.put("title", title);
            contentValues.put("image", image);
            contentValues.put("rating", rating);
            contentValues.put("releaseYear", releaseYear);
            contentValues.put("genre", String.valueOf(genre));

            if (movies.size()!=id){
                db.insert("Movies",null,contentValues);
            }

            cursor.close();
        }
    }
    // update
    // code that run on the ui trhead
        @Override
        public void onPostExecute(List <Movie> movies) {
            adDataBase();
        }

    // helper but not related to asynctask
    private String read(InputStream in) throws IOException{
        String line;
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader( new InputStreamReader(in))){
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
        }
        return sb.toString();
    }
}

