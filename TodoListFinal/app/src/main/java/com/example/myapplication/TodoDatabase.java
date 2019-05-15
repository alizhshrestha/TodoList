package com.example.myapplication;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Todo.class}, version = 1)
public abstract class TodoDatabase extends RoomDatabase {

    private static TodoDatabase instance;
    public abstract TodoDao todoDao();


    //only one thread at a time
    public static synchronized TodoDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TodoDatabase.class, "todo_database")
                    .fallbackToDestructiveMigration()//to increment the database to new version
                    .build();
        }
        return instance;
    }
}
