package org.qi.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void onButton(View v)
    {
        Intent intent = CatchPackServer.prepare(this);
        if(intent != null)
        {
            startActivityForResult(intent, 11);
        }
        else
        {
            startService(new Intent(this,CatchPackServer.class));
        }
    }
}
