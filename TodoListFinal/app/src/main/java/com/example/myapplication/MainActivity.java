package com.example.myapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_TODO_RESULT = 1;

    private TodoViewModel todoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddTodo = findViewById(R.id.button_add_todo);
        buttonAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
                startActivityForResult(intent, ADD_TODO_RESULT);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TodoListAdapter adapter = new TodoListAdapter();
        recyclerView.setAdapter(adapter);

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        todoViewModel.getAllTodos().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(@Nullable List<Todo> todos) {
                //update our RecyclerView
//                Toast.makeText(MainActivity.this,
//////                        "OnChanged", Toast.LENGTH_LONG).show();
                adapter.setTodos(todos);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TODO_RESULT && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddTodoActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddTodoActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddTodoActivity.EXTRA_PRIORITY, 1);

            Todo todo = new Todo(title, description, priority);
            todoViewModel.insert(todo);

            Toast.makeText(this, "Todo activity saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Todo not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
