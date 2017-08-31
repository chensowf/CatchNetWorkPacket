package org.qi.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    boolean isStart = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButton(View v)
    {
        Intent intent = CatchPackServer.prepare(this);
        if(intent != null)
        {
            startActivityForResult(intent, 11);
        }
        else
        {
            startService(new Intent(this,CatchPackServer.class));
            isStart = true;
        }
    }

    public void onButton1(View v)
    {
        if(isStart)
        {
            CatchPackServer.Instance.stopCatchPacket();
            stopService(new Intent(this, CatchPackServer.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == 11) {
                startService(new Intent(this, CatchPackServer.class));
                isStart = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isStart)
        {
            CatchPackServer.Instance.stopCatchPacket();
            stopService(new Intent(this, CatchPackServer.class));
        }
    }
}
