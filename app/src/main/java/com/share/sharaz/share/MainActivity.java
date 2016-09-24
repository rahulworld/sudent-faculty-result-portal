package com.share.sharaz.share;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button student,faculty,chatuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        student= (Button) findViewById(R.id.btn_student);
        faculty= (Button) findViewById(R.id.btn_faculty);
        chatuser= (Button) findViewById(R.id.btn_chat);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i1);
            }
        });
        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(MainActivity.this,FacultyLogin.class);
                startActivity(i1);
            }
        });
        chatuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(MainActivity.this,chatUserActivity.class);
                startActivity(i1);
            }
        });
    }
}
