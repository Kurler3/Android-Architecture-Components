package com.miguel.androidarchitectureexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListAdapter.OnItemClickListener {
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
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);



        //How to ask android for the view model
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        //Observe the live data from the ViewModel, without maintaining it here.
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update recycler view
                adapter.submitList(notes);
                for(int i=0;i<notes.size();i++){
                    Log.d(LOG_TAG, "Added " + notes.get(i).getTitle(),null);
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(mRecyclerView);

       mAddNoteBtn.setOnClickListener(view -> {
           Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);
           startActivityForResult(i, AddEditNoteActivity.ADD_NOTE_REQUEST);
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== AddEditNoteActivity.ADD_NOTE_REQUEST && resultCode==RESULT_OK){
            String newTitle = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String newDescription = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int newPriority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 10); //Defaults priority to lowest level if not provided

            // Add new Note in the database
            Note newNote = new Note(newTitle, newDescription, newPriority);
            noteViewModel.insert(newNote);

            // User Feed-Back
            Toast.makeText(this, "Note Added!", Toast.LENGTH_SHORT).show();
        }else if(requestCode==AddEditNoteActivity.EDIT_NOTE_REQUEST && resultCode==RESULT_OK){
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if(id==-1){
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String editedTitle = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String editedDescription = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int editedPriority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 10);

            Note newNote = new Note(editedTitle, editedDescription, editedPriority);
            newNote.setId(id);

            noteViewModel.update(newNote);
            Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
        }
        else{
            // Result was not RESULT_OK, but RESULT_CANCELED which means the note was not saved
            Toast.makeText(this, "Note not Saved :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete_all_notes_main_menu:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onItemClick(Note note) {
        // Reusing the activity that allows adding notes for editing
        Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);

         // Send the clicked note's information to the activity
        i.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
        i.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
        i.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
        i.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());

        startActivityForResult(i, AddEditNoteActivity.EDIT_NOTE_REQUEST);
    }
}