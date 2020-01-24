package c.kevin.splash;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button btnTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar=findViewById(R.id.progressBar);
        btnTest = findViewById(R.id.btnTest);

        haveInternetConnection();
        btnTest.setVisibility(View.INVISIBLE);
        btnTest.postDelayed(()->
                        btnTest.setVisibility(View.VISIBLE)
                ,2500);
        btnTest.postDelayed(()->
                progressBar.setVisibility(View.INVISIBLE)
                ,2500);
        btnTest.setOnClickListener(v->{
            getUrl();
        });

    }

    protected void getUrl() {
        Intent intent = new Intent(MainActivity.this,SplashActivity.class);
        // get the info on url and add to db
        // https://api.androidhive.info/json/movies.json
        new MovieAsyncTask(progressBar).execute();
        SQLiteDatabase database = new MoviesDbHelper(this).getWritableDatabase();
        // if have a dooble info i want to delete the dooble
        database.execSQL("DELETE FROM Movies WHERE rowid not in (SELECT min (rowid) from Movies group by title)");
        startActivity(intent);
        finish();
    }

    private void haveInternetConnection(){
        NetworkInfo networkInfo = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        // not connect
        if (networkInfo==null || !networkInfo.isConnected()){
            Toast.makeText(this, "NOT CONECTION", Toast.LENGTH_SHORT).show();
        }
    }


}
