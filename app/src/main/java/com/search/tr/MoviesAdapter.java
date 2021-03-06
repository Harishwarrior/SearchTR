package com.search.tr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder>
        implements Filterable {
    private List<Movie> movieList;
    private List<Movie> movieListFiltered;
    private MoviesAdapterListener listener;

    private MovieDetailsParser movieDetailsParser;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, fullName, year, quality;
        ImageView thumbnail;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            fullName = view.findViewById(R.id.full_name);
            thumbnail = view.findViewById(R.id.thumbnail);
            year = view.findViewById(R.id.recycler_view_year_text);
            quality = view.findViewById(R.id.recycler_view_quality_text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected movie in callback
                    listener.onMovieSelected(movieListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    MoviesAdapter(List<Movie> movieList, MoviesAdapterListener listener) {
        this.listener = listener;
        this.movieList = movieList;
        this.movieListFiltered = movieList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Movie movie = movieListFiltered.get(position);

        movieDetailsParser = new MovieDetailsParser(movie.getName());

        holder.name.setText(movie.getNormalized_name().toUpperCase());
        holder.fullName.setText(movie.getName());
        holder.year.setText(movieDetailsParser.getYear());
        holder.quality.setText(movieDetailsParser.getQuality());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(holder.itemView.getContext()).load(movie.getThumbNailUrl()).apply(options).into(holder.thumbnail);


    }

    @Override
    public int getItemCount() {
        return movieListFiltered ==null ? 0: movieListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    movieListFiltered = movieList;
                } else {
                    List<Movie> filteredList = new ArrayList<>();
                    for (Movie row : movieList) {

                        if (row.getNormalized_name().toLowerCase().contains(charString.toLowerCase()) || row.getUrl().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    movieListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = movieListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                movieListFiltered = (ArrayList<Movie>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface MoviesAdapterListener {
        void onMovieSelected(Movie movie);
    }
}
