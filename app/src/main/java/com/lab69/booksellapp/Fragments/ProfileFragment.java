package com.lab69.booksellapp.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lab69.booksellapp.Adapter.MyFotoAdapter;
import com.lab69.booksellapp.EditProfileActivity;
import com.lab69.booksellapp.Model.Post;
import com.lab69.booksellapp.Model.User;
import com.lab69.booksellapp.OptionActivity;
import com.lab69.booksellapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    ImageView image_profile, options;
    TextView posts, fullname, bio, username,phone;
    Button edit_profile;

    private List<String> mySaves;

    private RecyclerView recyclerView_Saves;
    private MyFotoAdapter myFotoAdapter_Saves;
    private List<Post> postList_Saves;

    FirebaseUser firebaseUser;
    String profileid;

    private RecyclerView recyclerView;
    private MyFotoAdapter myFotoAdapter;
    private List<Post> postList;

    TextView my_fotos, saved_fotos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        posts = view.findViewById(R.id.posts);

        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        edit_profile = view.findViewById(R.id.edit_profile);
        username = view.findViewById(R.id.username);
        phone = view.findViewById(R.id.phone_no_id);
        my_fotos = view.findViewById(R.id.my_fotos);
        saved_fotos = view.findViewById(R.id.saved_fotos);
        options = view.findViewById(R.id.options);

        recyclerView=view.findViewById(R.id.recycler_view_save);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        postList=new ArrayList<>();
        myFotoAdapter=new MyFotoAdapter(getContext(),postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerView_Saves=view.findViewById(R.id.recycler_view);
        recyclerView_Saves.setHasFixedSize(true);
        LinearLayoutManager layoutManager_saves=new GridLayoutManager(getContext(),3);
        recyclerView_Saves.setLayoutManager(layoutManager_saves);
        postList_Saves=new ArrayList<>();
        myFotoAdapter_Saves=new MyFotoAdapter(getContext(),postList_Saves);
        recyclerView_Saves.setAdapter(myFotoAdapter_Saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_Saves.setVisibility(View.GONE);

        userInfo();
        getNrPosts();
        myFotos();
        mysaves();


        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        } else {
            edit_profile.setVisibility(View.GONE);
            saved_fotos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")){

                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else {
                    edit_profile.setVisibility(View.GONE);
                }
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), OptionActivity.class);
                startActivity(intent);
            }
        });


        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_Saves.setVisibility(View.GONE);

            }
        });
        saved_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_Saves.setVisibility(View.VISIBLE);

            }
        });


        return view;
    }



    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                phone.setText(user.getPhone());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myFotos(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot datasnapshot: snapshot.getChildren())
                {
                    Post post=datasnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid))
                    {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mysaves()
    {
        mySaves=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    mySaves.add(dataSnapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList_Saves.clear();
                for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                    Post post=dataSnapshot.getValue(Post.class);

                    for (String id : mySaves){
                        if (post.getPostid().equals(id)){
                            postList_Saves.add(post);
                        }
                    }
                }
                myFotoAdapter_Saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}