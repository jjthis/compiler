package com.Lion.Rhino.Threa;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.faendir.rhino_android.RhinoAndroidHelper;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Synchronizer;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;


import static com.Lion.Rhino.Threa.MainActivity.MyRealCon;

public class Debug extends AppCompatActivity {

    private static CheckBox checkBox, checkBox2;
    private static ArrayList<String> titleArr = new ArrayList<String>();
    private static ArrayList<Boolean> isMine = new ArrayList<Boolean>();
    private static ArrayList<Boolean> isOpen = new ArrayList<Boolean>();
    private static BaseAdapter customBaseAdapter;
    Menu mMenuItem;
    ClearEditText editText1;
    EditText editText;
    Button btn;
    static ListView listView;
    SharedPreferences mPref;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getSharedPreferences("Debug", MODE_PRIVATE);
//        titleArr = new ArrayList<String>();
//        isMine = new ArrayList<Boolean>();
        RelativeLayout relativeLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        LinearLayout layouts = new LinearLayout(this);
        editText1 = new ClearEditText(this);
        checkBox = new CheckBox(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editText1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    editText1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                }
                else {
                    editText1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    editText1.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }
            }
        });
        checkBox.setChecked(mPref.getBoolean("Enter", false));
        checkBox.setText("Enter키로 전송");
        checkBox.setLayoutParams(layoutParams);

        checkBox2 = new CheckBox(this);
        checkBox2.setChecked(mPref.getBoolean("undefined", true));
        checkBox2.setText("Undefined 허용");
        checkBox2.setLayoutParams(layoutParams);
        layouts.addView(checkBox2);
        layouts.addView(checkBox);


        LinearLayout lay = new LinearLayout(this);
        TextView textView = new TextView(this);
        textView.setText("리스너 이름 : ");
        textView.setTextColor(Color.BLACK);
        textView.setPaddingRelative(3, 3, 3, 3);
        textView.setTextSize(18);
        editText = new EditText(this);
        editText.setText(mPref.getString("Listener", "Debug"));
        editText.setLayoutParams(layoutParams);
        editText.setHint("리스너 이름");
        lay.addView(textView);
        lay.addView(editText);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.addView(layouts);
        layout.addView(lay);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // You might want to tweak these to WRAP_CONTENT
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relativeLayout.addView(layout, lp);
        layout.setId(1);

        listView = new ListView(this);

        customBaseAdapter = new BaseAdapter() {
            // Return list view item count.
            @Override
            public int getCount() {
                return titleArr.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int itemIndex, View itemView, ViewGroup viewGroup) {

                if (isMine.get(itemIndex))
                    itemView = LayoutInflater.from(Debug.this).inflate(R.layout.my_message, null);
                else
                    itemView = LayoutInflater.from(Debug.this).inflate(R.layout.their_message, null);

                // Find related view object inside the itemView.
                TextView titleView = (TextView) itemView.findViewById(R.id.message_body);

                // Set background color by row number.
                final String title = titleArr.get(itemIndex);
                titleView.setText(title);

                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOpen.get(itemIndex)) {
                            isOpen.set(itemIndex, false);
                            titleView.setMaxLines(7);
                            titleView.setEllipsize(TextUtils.TruncateAt.END);
                        } else {
                            isOpen.set(itemIndex, true);
                            titleView.setMaxLines(Integer.MAX_VALUE);
                            titleView.setEllipsize(null);
                        }
                    }
                });
                titleView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                        ClipData clip = (ClipData) ClipData.newPlainText("Rhino", titleView.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(Debug.this, "복사되었습니다.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                if (isOpen.get(itemIndex)) {
                    titleView.setMaxLines(Integer.MAX_VALUE);
                    titleView.setEllipsize(null);
                } else {
                    titleView.setMaxLines(7);
                    titleView.setEllipsize(TextUtils.TruncateAt.END);
                }


                return itemView;
            }
        };
        listView.setAdapter(customBaseAdapter);
        listView.setDivider(null);
        listView.setSelector(android.R.color.transparent);
        listView.setItemsCanFocus(true);
        listView.setDividerHeight(0);


        LinearLayout layout1 = new LinearLayout(this);
        layout1.setId(2);
        //layout1.setBackgroundColor(Color.WHITE);
        editText1.setLayoutParams(layoutParams);
        editText1.setMaxLines(6);
        editText1.setHint("Enter Parameter");
        editText1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText1.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (checkBox.isChecked()) {
                            btn.callOnClick();
                        }

                        return checkBox.isChecked();
                    }
                });
        editText1.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        btn = new Button(this);
        TypedValue outValue = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        btn.setBackgroundResource(outValue.resourceId);
        btn.setText("send");

        btn.setClickable(true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText1.getText().toString();
                if (str.length() == 0) return;
                titleArr.add(str);
                isMine.add(true);
                isOpen.add(false);
                customBaseAdapter.notifyDataSetChanged();
                listView.setSelection(customBaseAdapter.getCount() - 1);

                editText1.setText("");
                final String[] strs = {"존재하지 않는 리스너"};
                String listen = editText.getText().toString();
                if (MainActivity.scomp==70||MainActivity.MyScope.has(listen, MainActivity.MyScope)) {
                    if(MainActivity.scomp==70){
                        try {
                            final V8Object v8Console = new V8Object(MainActivity.v8);
                            v8Console.registerJavaMethod(new replier2(), "reply", "reply", new Class<?>[] { Object.class });
                            strs[0]=MainActivity.v8.executeJSFunction(listen, new Object[]{str, v8Console}).toString();
                        }catch (Exception e) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            String sStack = sw.toString();
                            e.printStackTrace();
                            strs[0] = sStack;
                        }
                        if (strs[0].length() == 0) return;
                        if (strs[0].equals("undefined") && !checkBox2.isChecked()) return;
                        titleArr.add(strs[0]);
                        isMine.add(false);
                        isOpen.add(false);
                        customBaseAdapter.notifyDataSetChanged();
                        listView.setSelection(customBaseAdapter.getCount() - 1);
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Context scope = MainActivity.MyContext.enterContext();
                            try {
                                Function debug = new Synchronizer((Function) MainActivity.MyScope.get(listen, MainActivity.MyScope));
                                strs[0] = Context.toString(debug.call(scope, MainActivity.MyScope, MainActivity.MyScope, new Object[]{str, new replier()}));
                            } catch (Exception e) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                e.printStackTrace(pw);
                                String sStack = sw.toString();
                                e.printStackTrace();
                                strs[0] = sStack;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (strs[0].length() == 0) return;
                                    if (strs[0].equals("undefined") && !checkBox2.isChecked()) return;
                                    titleArr.add(strs[0]);
                                    isMine.add(false);
                                    isOpen.add(false);
                                    customBaseAdapter.notifyDataSetChanged();
                                    listView.setSelection(customBaseAdapter.getCount() - 1);
                                }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(Debug.this, listen + " (이)가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        layout1.addView(editText1);
        layout1.addView(btn);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // You might want to tweak these to WRAP_CONTENT
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addView(layout1, lp2);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE, 2);
        params.addRule(RelativeLayout.BELOW, 1);
        listView.setLayoutParams(params);
        relativeLayout.addView(listView);
        setContentView(relativeLayout);


    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("Enter", checkBox.isChecked());
        editor.putBoolean("undefined", checkBox2.isChecked());
        editor.putString("Listener", editText.getText().toString());
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        mMenuItem = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_trash:
                titleArr = new ArrayList<String>();
                isMine = new ArrayList<Boolean>();
                isOpen = new ArrayList<Boolean>();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customBaseAdapter.notifyDataSetChanged();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class replier {
        @JSStaticFunction
        public void reply(String strs) {
            if (strs.length() == 0) return;
            if (strs.equals("undefined") && !checkBox2.isChecked()) return;
            titleArr.add(strs);
            isMine.add(false);
            isOpen.add(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    customBaseAdapter.notifyDataSetChanged();
                    listView.setSelection(customBaseAdapter.getCount() - 1);
                }
            });

        }
    }
    public static class replier2 {
        public void reply(Object str) {
            String strs=str.toString();
            if (strs.length() == 0) return;
            if (strs.equals("undefined") && !checkBox2.isChecked()) return;
            titleArr.add(strs);
            isMine.add(false);
            isOpen.add(false);
            customBaseAdapter.notifyDataSetChanged();
            listView.setSelection(customBaseAdapter.getCount() - 1);
        }
    }
}
