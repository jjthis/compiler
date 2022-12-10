package com.Lion.Rhino.Threa;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private PreferenceScreen screen;
    private ListPreference mprogram, mfont, mver;
    private EditTextPreference mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_settings);
        screen = getPreferenceScreen();

        //인자로 전달되는 Key값을 가지는 Preference 항목의 인스턴스를 가져옴
        //굳이 여러곳에서 사용하지 않는 이상에는 이런식으로 객체화 시킬필요는 없는듯
        mprogram = (ListPreference) screen.findPreference("program");
        mfont = (ListPreference) screen.findPreference("userNameOpen");
        mver = (ListPreference) screen.findPreference("version");
        mInput=(EditTextPreference)screen.findPreference("input");

        //변화 이벤트가 일어났을 시 동작
        mprogram.setOnPreferenceChangeListener(this);
        mfont.setOnPreferenceChangeListener(this);
        mver.setOnPreferenceChangeListener(this);
        mInput.setOnPreferenceChangeListener(this);
    }


    public void viewDialog(String str) {
        final EditText input = new EditText(this);
        final AlertDialog alert = new AlertDialog.Builder(this).setPositiveButton("Search", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                viewDialog(input.getText().toString());
                // Do something with value!
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        }).create();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(40, 40, 40, 0);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(0, 0, 0, 40);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp3.setMargins(40, 40, 40, 0);
        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp3.setMargins(40, 40, 40, 40);

        alert.setTitle("All Languages");
        // Set an EditText view to get user input
        final LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(1);
        final ScrollingView scrollingView = new ScrollingView(this);
        final LinearLayout lay2 = new LinearLayout(this);
        lay2.setOrientation(1);
        input.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                alert.dismiss();
                viewDialog(input.getText().toString());
                return false;
            }
        });
        input.setMaxLines(2);
        lay.addView(input);
        input.setLayoutParams(lp);
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        for (int str2 = 0; str2 < Arrays.langs.length; str2++) {
            if (str.length() == 0 || Arrays.langs[str2].toLowerCase().contains(str)) {
                TextView tt = new TextView(this);
                String tx = Arrays.langs[str2];
                tt.setMaxLines(6);
                tt.setEllipsize(TextUtils.TruncateAt.END);
                tt.setTypeface(null, Typeface.BOLD);
                tt.setId(str2);
                final SpannableStringBuilder sps = new SpannableStringBuilder(tx);
                sps.setSpan(new RelativeSizeSpan(1.2f), 0, tx.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sps.setSpan(new ForegroundColorSpan(Color.BLUE), 0, tx.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tt.append(sps);
                tt.setLayoutParams(lp2);
                tt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView tts = (TextView) v;
                        if (tts.getMaxLines() == 6) {
                            tts.setMaxLines(Integer.MAX_VALUE);
                            tts.setEllipsize(null);
                        } else {
                            tts.setMaxLines(6);
                            tts.setEllipsize(TextUtils.TruncateAt.END);
                        }
                    }
                });
                tt.setBackgroundResource(backgroundResource);
                tt.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        copyAndPaste(v.getId());
                        return false;
                    }
                });
                lay2.addView(tt);
                lay2.setLayoutParams(lp3);
            }
        }
        scrollingView.addView(lay2);
        scrollingView.setLayoutParams(lp3);
        lay.addView(scrollingView);
        lay.setLayoutParams(lp4);
        alert.setView(lay);


        alert.show();
    }

    public void copyAndPaste(final int id) {
        int strokeWidth = 5; // 5px not dp
        //int roundRadius = 10; // 15px not dp
        int strokeColor = Color.parseColor("#FF4081");
        int fillColor = Color.WHITE;

        final AlertDialog dialog = new AlertDialog.Builder(this).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int parm = 20;
        param.leftMargin = 30;
        param.rightMargin = 30;
        param.topMargin = 40;

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setStroke(strokeWidth, strokeColor);
        final LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(1);
        Button btn = new Button(this);
        btn.setBackground(gd);
        btn.setLayoutParams(param);
        btn.setTransformationMethod(null);
        btn.setText("Copy My Clipboard");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cop = Arrays.langs[id];
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("lang", cop.substring(cop.indexOf("\n") + 2)); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                clipboardManager.setPrimaryClip(clipData);
                //복사가 되었다면 토스트메시지 노출
                Toast.makeText(getApplicationContext(), "복사되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        Button btn1 = new Button(this);
        btn1.setBackground(gd);
        btn1.setLayoutParams(param);
        btn1.setTransformationMethod(null);
        btn1.setText("Paste My Code");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cop = Arrays.langs[id];
                MainActivity.edit.setText(cop.substring(cop.indexOf("\n") + 2));
                Toast.makeText(getApplicationContext(), "붙여넣었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        lay.addView(btn);
        lay.addView(btn1);
        dialog.setTitle("선택");
        dialog.setView(lay);
        dialog.show();
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("load"))
            startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("text/*"), "Choose an text"), 1);
        else if (preference.getKey().equals("help"))
            viewDialog("");
        else if(preference.getKey().equals("exam")){
            if(mprogram.findIndexOfValue(mprogram.getValue())-2>=0)
                MainActivity.edit.setText(Sample.code[mprogram.findIndexOfValue(mprogram.getValue())-2]);
            Toast.makeText(getApplicationContext(), "붙여넣었습니다.", Toast.LENGTH_SHORT).show();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            MainActivity.edit.setText(readFile(data.getData()).toString());
            finish();
            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        }
    }


    public StringBuilder readFile(Uri ur) {
        StringBuilder texts = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(Objects.requireNonNull(ur.getPath()))));
            String line;

            while ((line = br.readLine()) != null) {
                texts.append(line).append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            //You'll need to add proper error handling here
        }
        return texts;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummary();
    }


    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = (String) newValue;
        if (preference == mfont) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            mfont.setSummary(index >= 0 ? listPreference.getEntries()[index]
                    : null);    // entries 값 대신 이에 해당하는 entryValues값 set
        } else if (preference == mprogram) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            mprogram.setSummary(index >= 0 ? listPreference.getEntries()[index]
                    : null);    // entries 값 대신 이에 해당하는 entryValues값 set
        } else if (preference == mver) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            mver.setSummary(index >= 0 ? listPreference.getEntries()[index]
                    : null);    // entries 값 대신 이에 해당하는 entryValues값 set
        } else if(preference == mInput){
            mInput.setSummary(value.split("\n")[0]);
        }
        return true;
    }


    private void updateSummary() {
        mver.setSummary(mver.getEntry());
        mfont.setSummary(mfont.getEntry());
        mprogram.setSummary(mprogram.getEntry());
        if(mInput.getText()!=null)
            mInput.setSummary(mInput.getText().split("\n")[0]);
        else mInput.setSummary("");
    }


}
