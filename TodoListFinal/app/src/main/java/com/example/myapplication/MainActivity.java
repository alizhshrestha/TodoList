package com.example.myapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_TODO_REQUEST = 1;
    public static final int EDIT_TODO_REQUEST = 2;

    private TodoViewModel todoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddTodo = findViewById(R.id.button_add_todo);
        buttonAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditTodoActivity.class);
                startActivityForResult(intent, ADD_TODO_REQUEST);
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
                adapter.submitList(todos);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                todoViewModel.delete(adapter.getTodoAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Todo deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new TodoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Todo todo) {
                Intent intent = new Intent(MainActivity.this, AddEditTodoActivity.class);
                intent.putExtra(AddEditTodoActivity.EXTRA_ID, todo.getId());
                intent.putExtra(AddEditTodoActivity.EXTRA_TITLE, todo.getName());
                intent.putExtra(AddEditTodoActivity.EXTRA_DESCRIPTION, todo.getDescription());
                intent.putExtra(AddEditTodoActivity.EXTRA_TIME, todo.getTime());
                intent.putExtra(AddEditTodoActivity.EXTRA_PRIORITY, todo.getPriority());
                startActivityForResult(intent, EDIT_TODO_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TODO_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddEditTodoActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditTodoActivity.EXTRA_DESCRIPTION);
            String time = data.getStringExtra(AddEditTodoActivity.EXTRA_TIME);
            int priority = data.getIntExtra(AddEditTodoActivity.EXTRA_PRIORITY, 1);

            Todo todo = new Todo(title, description,time, priority);

            todoViewModel.insert(todo);

            Toast.makeText(this, "Todo activity saved", Toast.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_TODO_REQUEST && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditTodoActivity.EXTRA_ID, -1);

            if (id == -1){
                Toast.makeText(this, "Todo can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditTodoActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditTodoActivity.EXTRA_DESCRIPTION);
            String time = data.getStringExtra(AddEditTodoActivity.EXTRA_TIME);
            int priority = data.getIntExtra(AddEditTodoActivity.EXTRA_PRIORITY, 1);

            Todo todo = new Todo(title, description, time, priority);
            todo.setId(id);

            todoViewModel.update(todo);

            Toast.makeText(this, "Todo updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Todo not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_todos:
                todoViewModel.deleteAllTodos();
                Toast.makeText(this, "All todos deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
