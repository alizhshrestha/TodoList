package com.example.myapplication;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity (tableName = "todo_table")
public class Todo {

    @PrimaryKey (autoGenerate = true)
    private int id;
    
    private String name;

    private String description;

    private String time;

    private int priority;

    public Todo(String name, String description, String time, int priority) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
