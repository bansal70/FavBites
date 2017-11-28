package co.fav.bites.views.adapters;

/*
 * Created by rishav on 9/5/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.fav.bites.R;
import co.fav.bites.models.Utils;
import co.fav.bites.models.beans.UserPostsData;

public class FragmentPostsAdapter extends RecyclerView.Adapter<FragmentPostsAdapter.ViewHolder>{
    private Context context;
    private List<UserPostsData.Data> postsList;

    public FragmentPostsAdapter(Context context, List<UserPostsData.Data> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @Override
    public FragmentPostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_fragment_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FragmentPostsAdapter.ViewHolder holder, int position) {
        UserPostsData.Data data = postsList.get(position);
        UserPostsData.Comment comment = data.comment;
        UserPostsData.Restaurant restaurant = data.restaurant;
        UserPostsData.User user = data.user;

        String name = user.fname + " " + user.lname;
        holder.tvUser.setText(name);
        holder.tvCreated.setText(comment.created);
        if (comment.comment.isEmpty())
            holder.tvComment.setVisibility(View.GONE);
        else
            holder.tvComment.setVisibility(View.VISIBLE);
        holder.tvComment.setText(comment.comment);
        holder.tvRestaurant.setText(restaurant.name);
        holder.tvAddress.setText(restaurant.streetAddress);

        if (!user.image.isEmpty())
        Picasso.with(context)
                .load(user.image)
                .fit()
                .transform(Utils.imageTransformation())
                .placeholder(R.color.colorHome)
                .into(holder.imgUser);

        Glide.with(context)
                .load(comment.image)
                .crossFade()
                .fitCenter() // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .into(holder.imgPost);

        Glide.with(context)
                .load(restaurant.logoUrl)
                .crossFade()
                .into(holder.imgRestaurant);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgUser, imgPost, imgRestaurant;
        private TextView tvUser, tvCreated, tvComment, tvRestaurant, tvAddress;
        private ViewHolder(View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            imgPost = itemView.findViewById(R.id.imgPost);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvCreated = itemView.findViewById(R.id.tvCreated);
            tvComment = itemView.findViewById(R.id.tvComment);

            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
}
