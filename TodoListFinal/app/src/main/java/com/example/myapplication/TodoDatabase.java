package com.example.myapplication;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Calendar;

@Database(entities = {Todo.class}, version = 3)
public abstract class TodoDatabase extends RoomDatabase {

    private static TodoDatabase instance;
    public abstract TodoDao todoDao();


    //only one thread at a time
    public static synchronized TodoDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TodoDatabase.class, "todo_database")
                    .fallbackToDestructiveMigration()//to increment the database to new version
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private TodoDao todoDao;

        private PopulateDbAsyncTask(TodoDatabase db){
            todoDao = db.todoDao();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            todoDao.insert(new Todo("Your todo name", "todo description", String.valueOf(Calendar.DATE), 1));
            todoDao.insert(new Todo("Your todo name2", "todo description2", String.valueOf(Calendar.DATE), 2));
            todoDao.insert(new Todo("Your todo name3", "todo description3", String.valueOf(Calendar.DATE), 3));
            todoDao.insert(new Todo("Your todo name4", "todo description4", String.valueOf(Calendar.DATE), 4));
            return null;
        }
    }
}
