package com.miguel.androidarchitectureexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends ListAdapter<Note, NoteListAdapter.NoteViewHolder> {
    //This list needs to be initialized here, because the live data can't be loaded into a null list.
    OnItemClickListener listener;

    protected NoteListAdapter() {
        super(DIFF_CALLBACK);
    }

    private static DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            //for two items to be the same, the contents don't have to be the same but it has to be the same entry in the database
            return oldItem.getId()==newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            // Here we only return true if nothing in the same item changed. If this returns false then the adapter will update this item
            if(newItem.getTitle().equals(oldItem.getTitle()) && newItem.getDescription().equals(oldItem.getDescription())
            && newItem.getPriority()==oldItem.getPriority()){
                return true;
            }

            return false;
        }
    };

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
                    listener.onItemClick(getItem(position));
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
        //getItem is a function from ListAdapter, and acesses the array list passed to it on every change.
        Note currentNote = getItem(position);

        //Assigning to views
        holder.mNoteTitle.setText(currentNote.getTitle());
        holder.mNoteDescription.setText(currentNote.getDescription());
        holder.mNotePriority.setText(String.valueOf(currentNote.getPriority()));
    }
    public Note getNoteAt(int position) {
        return getItem(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
