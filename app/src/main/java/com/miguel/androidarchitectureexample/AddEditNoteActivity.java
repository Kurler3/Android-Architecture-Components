package com.miguel.androidarchitectureexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 2;
    public static final int EDIT_NOTE_REQUEST = 3;
    public static final String EXTRA_ID = "NewNoteID";
    public static final String EXTRA_TITLE = "NewNoteTitle";
    public static final String EXTRA_DESCRIPTION = "NewNoteDescription";
    public static final String EXTRA_PRIORITY = "NewNotePriority";

    EditText mTitleInput, mDescriptionInput;
    NumberPicker mPriorityPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mTitleInput = findViewById(R.id.add_note_title_input);
        mDescriptionInput = findViewById(R.id.add_note_description_input);
        mPriorityPicker = findViewById(R.id.priority_number_picker);

        mPriorityPicker.setMinValue(1);
        mPriorityPicker.setMaxValue(10);

        //Sets the back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        Intent i = getIntent();
        if (i.hasExtra(EXTRA_ID)) {
            setTitle("Edit Note");
            mTitleInput.setText(i.getStringExtra(EXTRA_TITLE));
            mDescriptionInput.setText(i.getStringExtra(EXTRA_DESCRIPTION));
            mPriorityPicker.setValue(i.getIntExtra(EXTRA_PRIORITY, 10));
        } else setTitle("Add Note");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                //Save the note
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        String title = mTitleInput.getText().toString();
        String description = mDescriptionInput.getText().toString();
        int priority = mPriorityPicker.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please Insert The Title and Description", Toast.LENGTH_SHORT).show();
            return;
        }
        // Passes the data back to the caller activity, which then is handled (added to the database and the recycler view)
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id!=-1) data.putExtra(EXTRA_ID, id);

        setResult(RESULT_OK, data);
        finish();
    }

}