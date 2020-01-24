package c.kevin.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    RecyclerView rvMovies;
    Button btnAdd;
    Button btnRefresh;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        btnAdd = findViewById(R.id.btnAdd);
        rvMovies = findViewById(R.id.rvMovies);
        btnRefresh=findViewById(R.id.btnRefresh);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
            startActivity(intent);
        });

    }

    private void viewRecyclerViewMovies() {
        MovieAdapter movieAdapter = new MovieAdapter(read());
        rvMovies.setAdapter(movieAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMovies.setLayoutManager(linearLayoutManager);
    }


    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

        // value
        private List<Movie> movies;
        // ctor
        MovieAdapter(List<Movie> movies) {
            this.movies=movies;
        }

        @NonNull
        @Override
        // get xml and view
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v= inflater.inflate(R.layout.movie_item,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // set the info
            Movie movie = movies.get(position);
            holder.tvTitle.setText(movie.getTitle());
            holder.tvReleaseYear.setText(String.valueOf(movie.getReleaseYear()));

            Picasso.get().load(movie.getImage())
                    //befor charge
                    .placeholder(R.drawable.ic_placeholder)
                    //if error
                    .error(R.drawable.ic_placeholder)
                    .into(holder.ivPoster);
            // on click
            holder.itemView.setOnClickListener(v -> {
                // send info in the fragment
                MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
                Bundle bundle = new Bundle();
                String title = movie.getTitle();
                String image = movie.getImage();
                String rating = String.valueOf(movie.getRating());
                String releaseYear = String.valueOf(movie.getReleaseYear());
                String genre = String.valueOf(movie.getGenre());

                // ------------- probleme dans le array il ne transmet pas les donner ------------
                bundle.putString("title",title);
                bundle.putString("image",image);
                bundle.putString("rating",rating);
                bundle.putString("releaseYear",releaseYear);
                bundle.putString("genre",genre);

                movieDetailsFragment.setArguments(bundle);
                movieDetailsFragment.show(getSupportFragmentManager(),"movieDetailsFragment");
            });
        }

        // film size
        @Override
        public int getItemCount() {
            return movies.size();
        }

        // set one item
        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView ivPoster;
            TextView tvTitle;
            TextView tvReleaseYear;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPoster = itemView.findViewById(R.id.ivPoster);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvReleaseYear = itemView.findViewById(R.id.tvReleaseYear);
            }
        }
    }

    private List<Movie> read() {
        // init the helper
        List<Movie>movieList=new ArrayList<>();
        SQLiteDatabase database = new MoviesDbHelper(this).getWritableDatabase();
        // select all db
        Cursor cursor = database.rawQuery("SELECT * FROM Movies",null);
        boolean first = cursor.moveToFirst();
        // database empty
        if (!first){
            btnRefresh.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
                startActivity(intent);
            });
            return movieList;
        }else {
            btnRefresh.setVisibility(View.INVISIBLE);
        }
        int titleIndex = cursor.getColumnIndex("title");
        int imageIndex=cursor.getColumnIndex("image");
        int ratingIndex=cursor.getColumnIndex("rating");
        int releaseYearIndex = cursor.getColumnIndex("releaseYear");

        do {
            String title = cursor.getString(titleIndex);
            String image = cursor.getString(imageIndex);
            double rating=cursor.getDouble(ratingIndex);
            int releaseYear = cursor.getInt(releaseYearIndex);
            String[] genreArray = cursor.getColumnNames();
            ArrayList<String> genre = new ArrayList<>();
            Collections.addAll(genre, genreArray);

            movieList.add(new Movie(title,image,rating,releaseYear,genre));
        }while (cursor.moveToNext());
        cursor.close();
        return movieList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewRecyclerViewMovies();
    }
}
