package com.example.myapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddEditTodoActivity extends AppCompatActivity {

    public static final String EXTRA_ID =
            "com.example.myapplication.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.myapplication.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.example.myapplication.EXTRA_DESCRIPTION";
    public static final String EXTRA_TIME =
            "com.example.myapplication.EXTRA_TIME";
    public static final String EXTRA_PRIORITY =
            "com.example.myapplication.EXTRA_PRIORITY";
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;


    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;

    private Button image_button;
    private ImageView imageView;
    private ImageButton mVoiceBtn;

    private int day;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;


    public static final String TAG = "AddEditTodoActivity";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        image_button = findViewById(R.id.image_button);
        imageView = (ImageView) findViewById(R.id.todo_imageView);
        mVoiceBtn = findViewById(R.id.voiceBtn);
        mDisplayDate = findViewById(R.id.edit_text_time);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddEditTodoActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        mDateSetListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "/" + Integer.valueOf(month+1) + "/" + dayOfMonth;
                mDisplayDate.setText(date);
            }
        };

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)){
            setTitle("Edit Todo");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            mDisplayDate.setText(intent.getStringExtra(EXTRA_TIME));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        }else{
            setTitle("Add Todo");
        }


        String query = "";
        if (getIntent().getAction() != null && getIntent().getAction().equals("com.google.android.gms.actions.SEARCH_ACTION")) {
            query = getIntent().getStringExtra(SearchManager.QUERY);
        }


    }

    private void saveTodo(){
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String time = mDisplayDate.getText().toString();
        int priority = numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this,
                    "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_TIME, time);
        data.putExtra(EXTRA_PRIORITY, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1){
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_todo:
                saveTodo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void selectImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }else{
                    Toast.makeText(this, "Permission denied..!", Toast.LENGTH_SHORT).show();
                }
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageView.setImageURI(data.getData());
        }

        switch(requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null!=data){
                    //get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //set to text view
                    editTextTitle.setText(result.get(0));
                }
                break;
        }
    }


    public void recordVoice(View view) {
        speak();
    }

    private void speak() {

        //intent to show speech to text dialog
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");

        //start intent
        try{
            // in there was no error
            // show dialog
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            //if there was some error
            //get message of error and show
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
