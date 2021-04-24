package com.lab69.booksellapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lab69.booksellapp.CommentActivity;
import com.lab69.booksellapp.Model.Post;
import com.lab69.booksellapp.Model.User;
import com.lab69.booksellapp.R;


public class PostDetailsFragment extends Fragment {
    String postid, publisherid;
    private Button profile_button, call_button;
    String phone_number, post_publisher_value;
    private ImageView post_image;

    private ImageView fav_btn, cmnt_btn,more;
    private TextView post_title, post_description, post_price, post_comments;

    private FirebaseUser firebaseUser;

    private static final int REQUEST_CALL = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid = preferences.getString("postid", "none");
        publisherid = preferences.getString("publisherid", "none");


        // value = getArguments().getString("YourKey");

        post_image = view.findViewById(R.id.details_post_image);
        post_title = view.findViewById(R.id.details_title);
        post_price = view.findViewById(R.id.details_price);
        post_description = view.findViewById(R.id.details_description);
        profile_button = view.findViewById(R.id.profile_id);

        call_button = view.findViewById(R.id.call_author_id);
        fav_btn = view.findViewById(R.id.save);
        cmnt_btn = view.findViewById(R.id.comment);
        more = view.findViewById(R.id.more);
        post_comments = view.findViewById(R.id.no_comments);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();




        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(getContext(),view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.delete:
                                final String id = postid;
                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child(postid).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    deleteNotifications(id, firebaseUser.getUid());
                                                }
                                            }
                                        });
                                return true;
                            default:
                                return false;
                        }

                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!publisherid.equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });




        fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fav_btn.getTag() != null && fav_btn.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(postid).setValue(true);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(postid).removeValue();

                }
            }
        });


        cmnt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra("postid", postid);
                intent.putExtra("publisherid", publisherid);
                startActivity(intent);


            }
        });

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", publisherid);
                editor.apply();
                ((FragmentActivity) getActivity()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();


            }
        });

        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        post_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra("postid", postid);
                intent.putExtra("publisherid", publisherid);
                startActivity(intent);


            }
        });

        readPosts();
        userInfo();
        getCommetns(postid, post_comments);
        isSaved(postid, fav_btn);


        return view;
    }



    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                                        ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container,new PostDetailsFragment()).commit();

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void makePhoneCall() {

        if (phone_number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + phone_number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

            }

        } else {
            Toast.makeText(getContext(), "Phone Number isn't Valid", Toast.LENGTH_SHORT).show();
        }

    }

    private void readPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);

                Glide.with((getContext())).load(post.getPostimage()).into(post_image);

                if (post.getTitle().equals("")) {
                    post_title.setVisibility(View.GONE);
                } else {
                    post_title.setVisibility(View.VISIBLE);
                    post_title.setText(post.getTitle());
                }
                if (post.getDescription().equals("")) {
                    post_description.setVisibility(View.GONE);
                } else {
                    post_description.setVisibility(View.VISIBLE);
                    post_description.setText(post.getDescription());
                }
                if (post.getPrice().equals("")) {
                    post_price.setVisibility(View.GONE);
                } else {
                    post_price.setVisibility(View.VISIBLE);
                    post_price.setText(post.getPrice());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getCommetns(String postId, final TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.setText("View All " + dataSnapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void isSaved(final String postid, final ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.ic_fav_red);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("save");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                if (user.getPhone() != null) {
                    phone_number = user.getPhone().toString().trim();
                } else {
                    phone_number = "0000";
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}