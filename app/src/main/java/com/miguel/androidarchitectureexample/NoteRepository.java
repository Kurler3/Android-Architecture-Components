package com.miguel.androidarchitectureexample;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> noteList;

    public NoteRepository(Application application){
                NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
                noteDao = noteDatabase.noteDao();
                noteList = noteDao.getAllNotes();
    }
    public void insert(Note note){
        new InsertAsyncTask(noteDao).execute(note);
    }
    public void update(Note note){
        new UpdateAsyncTask(noteDao).execute(note);
    }
    public void delete(Note note){
        new DeleteAsyncTask(noteDao).execute(note);
    }
    public void deleteAllNotes(){
        new DeleteAllAsyncTask(noteDao).execute();
    }
    public LiveData<List<Note>> getAllNotes(){
        return noteList;
    }
    public static class InsertAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;
        private InsertAsyncTask(NoteDao dao) {
            this.noteDao = dao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }
    public static class UpdateAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;
        private UpdateAsyncTask(NoteDao dao){
            this.noteDao = dao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }
    public static class DeleteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;
        private DeleteAsyncTask(NoteDao dao){
            this.noteDao = dao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }
    public static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void>{
        private NoteDao noteDao;
        private DeleteAllAsyncTask(NoteDao dao){
            this.noteDao = dao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }
}
