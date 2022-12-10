package com.Lion.Rhino.Threa;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class copy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        Intent intent = getIntent();

        String str = intent.getStringExtra("Data");

        ClipData clip = ClipData.newPlainText("label", str);

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "copied", Toast.LENGTH_SHORT).show();
        finish();
    }

}
