package com.miguel.androidarchitectureexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "onChanged";
    private NoteViewModel noteViewModel;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddNoteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddNoteBtn = findViewById(R.id.add_note_btn);
        // Setting up the Recycler View
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final NoteListAdapter adapter = new NoteListAdapter();
        mRecyclerView.setAdapter(adapter);



        //How to ask android for the view model
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        //Observe the live data from the ViewModel, without maintaining it here.
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update recycler view
                adapter.setNotes(notes);
                for(int i=0;i<notes.size();i++){
                    Log.d(LOG_TAG, "Added " + notes.get(i).getTitle(),null);
                }
            }
        });


       mAddNoteBtn.setOnClickListener(view -> {
           Intent i = new Intent(MainActivity.this, AddNoteActivity.class);
           startActivityForResult(i, AddNoteActivity.ADD_NOTE_INTENT);
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==AddNoteActivity.ADD_NOTE_INTENT && resultCode==RESULT_OK){
            String newTitle = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String newDescription = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
            int newPriority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 10); //Defaults priority to lowest level if not provided

            // Add new Note in the database
            Note newNote = new Note(newTitle, newDescription, newPriority);
            noteViewModel.insert(newNote);

            // User Feed-Back
            Toast.makeText(this, "Note Added!", Toast.LENGTH_SHORT).show();
        } else{
            // Result was not RESULT_OK, but RESULT_CANCELED which means the note was not saved
            Toast.makeText(this, "Note not Saved :(", Toast.LENGTH_SHORT).show();
        }
    }
}