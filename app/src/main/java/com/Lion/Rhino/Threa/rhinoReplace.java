package com.Lion.Rhino.Threa;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.faendir.rhino_android.RhinoAndroidHelper;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.Lion.Rhino.Threa.MainActivity.MyRealCon;
import static com.Lion.Rhino.Threa.MainActivity.MyScope;

public class rhinoReplace extends AppCompatActivity {
    SharedPreferences mPref;
    boolean bol = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        CharSequence str=null;
        try {
            str = getIntent()
                    .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            if (str == null) {
                str = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                bol = true;
            }
        } catch (Exception e) {
            Log.i("thisthis", e + "");
            finish();
        }
        ScrollView scrollView = new ScrollView(this);
        final EditText edit1 = new EditText(this);
        scrollView.addView(edit1);
        edit1.setText(mPref.getString("Replace", "function textReplace(str) {\nreturn 0;\n}"));
        edit1.setHint("Find Content");
        edit1.setTextColor(Color.BLACK);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);//, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        String finalStr = str+"";
        dialog.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(MainActivity.MyContext == null) {
                    MainActivity.setMyContext(new RhinoAndroidHelper(rhinoReplace.this));
                    MainActivity.initializeJS(rhinoReplace.this);
                }
                MainActivity.MyContext.enterContext();

                MyRealCon.evaluateString(MyScope, edit1.getText().toString(), "Rhinos", 1, null);
                String strs = "";
                if (MainActivity.MyScope.has("textReplace", MainActivity.MyScope)) {
                    try {
                        Function debug = (Function) MainActivity.MyScope.get("textReplace", MainActivity.MyScope);
                        strs = Context.toString(debug.call(MyRealCon, MainActivity.MyScope, MainActivity.MyScope, new Object[]{finalStr}));
                    } catch (Exception e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        String sStack = sw.toString();
                        e.printStackTrace();
                        Toast.makeText(rhinoReplace.this, sStack, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(rhinoReplace.this, "textReplace 가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences.Editor editor = mPref.edit();
                editor.putString("Replace", edit1.getText().toString()); // key, value를 이용하여 저장하는 형태
                editor.apply();
                if (strs.length() == 0 || strs.equals("undefined")) {
                    finish();
                }
                if(bol){
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", strs);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(rhinoReplace.this, "copied", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Intent intent = new Intent();
                try {
                    intent.putExtra(Intent.EXTRA_PROCESS_TEXT, strs);
                } catch (Exception e) {
                    Log.i("thisthis", e + "");
                    finish();
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setView(scrollView, 50, 40, 50, 0);
        dialog.setTitle("Length");
        dialog.setCancelable(false);
        AlertDialog alert = dialog.create();
        alert.show();
    }
}
