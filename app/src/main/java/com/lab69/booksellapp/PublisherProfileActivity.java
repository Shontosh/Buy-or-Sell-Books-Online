package com.lab69.booksellapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.lab69.booksellapp.Adapter.MyFotoAdapter;
import com.lab69.booksellapp.Adapter.PostAdapter;
import com.lab69.booksellapp.Model.Post;

import java.util.List;

public class PublisherProfileActivity extends AppCompatActivity {

    ImageView image_profile, options;
    TextView posts, fullname, bio, username;
    Button edit_profile;
    FirebaseUser firebaseUser;
    String profileid;

    private RecyclerView recyclerView;
    private MyFotoAdapter myFotoAdapter;
    private List<Post> postList;

    ImageButton my_fotos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_profile);
    }
}