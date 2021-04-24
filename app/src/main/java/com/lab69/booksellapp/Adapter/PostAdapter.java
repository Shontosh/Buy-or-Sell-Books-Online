package com.lab69.booksellapp.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lab69.booksellapp.Fragments.PostDetailsFragment;
import com.lab69.booksellapp.Model.Post;
import com.lab69.booksellapp.Model.User;
import com.lab69.booksellapp.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter() {
    }

    public PostAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sample_post_item, parent, false);
        return new PostAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPosts.get(position);
        Glide.with(mContext).load(post.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.post_image);

        if (post.getTitle().equals("")) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(post.getTitle());
        }
        if (post.getPrice().equals("")) {
            holder.price.setVisibility(View.GONE);
        } else {
            holder.price.setVisibility(View.VISIBLE);
            holder.price.setText(post.getPrice());
        }

        publisherInfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher());


        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.putString("publisherid", post.getPublisher());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailsFragment()).commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView image_profile, post_image, fav, comment;
        public TextView username, price, publisher, title, comments;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            price = itemView.findViewById(R.id.post_price);
            title = itemView.findViewById(R.id.post_title);
           // post_image.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

         /*SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("postid", post.getPostid());
            editor.apply();
//Put the value
          /*  PostDetailsFragment ldf = new PostDetailsFragment ();
            Bundle args = new Bundle();
            args.putString("YourKey", post.getPostid());
            ldf.setArguments(args);

//Inflate the fragment
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).commit();*/
           //((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailsFragment()).commit();

         /*  Intent intent=new Intent(mContext, PostDetailsActivity.class);
            intent.putExtra("post_image_id",mPosts.get(getAdapterPosition()).getPostid());
            intent.putExtra("post_publisher_id",mPosts.get(getAdapterPosition()).getPublisher());
            mContext.startActivity(intent);*/


        }
    }

        private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userid);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




}
