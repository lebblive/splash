package c.kevin.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;


public class CameraActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    //value
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private ArrayList<Movie> movies=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        int currentApiVersion = Build.VERSION.SDK_INT;
        // check version
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }else{
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();


        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }else{
                requestPermission();
            }
        }
    }

    //stop camera
    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                }else{
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        // fix bug with version
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            showMessageOKCancel(
                                    (dialog, which) -> requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA));
                        }
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(CameraActivity.this)
                .setMessage("You need to allow access to both the permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        String myResult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");

        getScan(myResult);

            builder.setPositiveButton("OK", (dialog, which) -> scannerView.resumeCameraPreview(CameraActivity.this));
        // add to db
        builder.setNeutralButton("Add to data base", (dialog, which) -> {
            adDataBase();
            // come back on movies view
            Intent intent =new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(intent);
            finish();
        });

            // get the scan view
            builder.setMessage(result.getText());
            AlertDialog alert1 = builder.create();
            alert1.show();
        }
        //get the all
    public void getScan (String result){
        try {
            String json=read(result);
            // get json
            JSONObject object = new JSONObject(json);
            String title = object.getString("title");
            String image = object.getString("image");
            double rating=object.getDouble("rating");
            int releaseYear=object.getInt("releaseYear");

            JSONArray genreArray = object.getJSONArray("genre");
            ArrayList<String> genre = new ArrayList<>();

            // change the jsonArray on Json
            for (int a=0;a<genreArray.length();a++){
                genre.add(genreArray.getString(a));
            }
            if (movies.size()<object.length()){
                //add an object is the moovie
                movies.add(new Movie(title,image,rating,releaseYear,genre));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // add info in db
    private void adDataBase() {
        SQLiteDatabase db = new MoviesDbHelper(CameraActivity.this).getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM Movies",null);

        // delete double into my db
        db.execSQL("DELETE FROM Movies WHERE rowid not in (SELECT min (rowid) from Movies group by title)");

        ContentValues contentValues = new ContentValues();
        // get each obj
        for (int i = 0; i<movies.size();i++) {

            int id = cursor.getCount();
            String title = movies.get(i).getTitle();
            String image = movies.get(i).getImage();
            Double rating = movies.get(i).getRating();
            int releaseYear = movies.get(i).getReleaseYear();
            ArrayList<String> genre = movies.get(i).getGenre();

            // for more security use cententValues

            contentValues.put("title", title);
            contentValues.put("image", image);
            contentValues.put("rating", rating);
            contentValues.put("releaseYear", releaseYear);
            contentValues.put("genre", String.valueOf(genre));

            if (movies.size()!= id){
                // add object in db
                db.insert("Movies",null,contentValues);

            }
        }

    }

    // for reader
    private String read(String myResult) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new StringReader(myResult))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
