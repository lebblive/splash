package c.kevin.splash;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.squareup.picasso.Picasso;


public class MovieDetailsFragment extends AppCompatDialogFragment {

    private ImageView ivPoster;
    private TextView tvTitle;
    private TextView tvRating;
    private TextView tvReleaseYear;
    private TextView tvGenre;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and fixe size

        View v = inflater.inflate(R.layout.fragment_movie_details, container, false);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.87);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        v.setLayoutParams(lp);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout(view);
        getDetail();
    }

    private void getDetail() {
        // get the info on Activity
        Bundle bundle = getArguments();
        if (bundle!=null){
            String title = bundle.getString("title");
            String image = bundle.getString("image");
            String rating = bundle.getString("rating");
            String releaseYear = bundle.getString("releaseYear");
            String genre = bundle.getString("genre");
            tvTitle.setText(title);
            tvRating.setText(rating);
            tvReleaseYear.setText(releaseYear);
            tvGenre.setText(genre);

            Picasso.get().load(image)
                    //beffor the charge
                    .placeholder(R.drawable.ic_placeholder)
                    //if it's impossible to read
                    .error(R.drawable.ic_placeholder)
                    .into(ivPoster);
        }
    }
    private void layout(View view) {
        tvTitle=view.findViewById(R.id.tvTitle);
        tvRating=view.findViewById(R.id.tvRating);
        tvReleaseYear=view.findViewById(R.id.tvReleaseYear);
        tvGenre=view.findViewById(R.id.tvGenre);
        ivPoster=view.findViewById(R.id.ivPoster);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
