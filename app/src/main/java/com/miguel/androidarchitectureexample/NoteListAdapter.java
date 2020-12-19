package com.miguel.androidarchitectureexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {
    //This list needs to be initialized here, because the live data can't be loaded into a null list.
    private List<Note> mNoteList = new ArrayList<>();
    OnItemClickListener listener;

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView mNoteTitle;
        TextView mNoteDescription;
        TextView mNotePriority;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            mNoteTitle = itemView.findViewById(R.id.text_view_note_title);
            mNoteDescription = itemView.findViewById(R.id.text_view_note_description);
            mNotePriority = itemView.findViewById(R.id.text_view_note_priority);

            //Update the Note clicked
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION)
                    listener.onItemClick(mNoteList.get(position));
            });
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = mNoteList.get(position);

        //Assigning to views
        holder.mNoteTitle.setText(currentNote.getTitle());
        holder.mNoteDescription.setText(currentNote.getDescription());
        holder.mNotePriority.setText(String.valueOf(currentNote.getPriority()));
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public Note getNoteAt(int position) {
        return mNoteList.get(position);
    }

    //On callback the database receives three notes. Will create a function to receive those notes
    public void setNotes(List<Note> notes) {
        this.mNoteList = notes;
        //Will change the way to update the recycler view later. There are better notify methods.
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
