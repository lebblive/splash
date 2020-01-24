package c.kevin.splash;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MoviesDbHelper extends SQLiteOpenHelper {

    // ctor
    public MoviesDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // oblgation context for autorisation
    MoviesDbHelper(Context context){
        super(context,"MoviesDb",null,1);
    }

    // just the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE Movies(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "title TEXT, "+
                "image TEXT, "+
                "rating DOUBLE, "+
                "releaseYear INT, "+
                "genre TEXT"+
                ");"
        );
    }

    // if i want to update version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE Movies;");
        onCreate(db);
    }
}
