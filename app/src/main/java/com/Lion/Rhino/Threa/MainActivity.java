package com.Lion.Rhino.Threa;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.faendir.rhino_android.RhinoAndroidHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.StackStyle;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static org.mozilla.javascript.ScriptableObject.DONTENUM;
import static org.mozilla.javascript.ScriptableObject.PERMANENT;

public class MainActivity extends AppCompatActivity {

    static ClearEditText edit;
    static boolean isConsole = false;
    static RhinoAndroidHelper MyContext;
    static ScriptableObject MyScope;
    static Context MyRealCon;
    private static android.content.Context ctx;
    private static TextView text;
    private String  inputText;
    int comp = 1;
    Function MyResponder;
    SharedPreferences mPref;
    @SuppressLint("HandlerLeak")
    Handler mTimer = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if (comp == 1) runJS(edit.getText().toString());
            else if (comp == 0) compile(edit.getText().toString());
            else if(comp==70) runV8(edit.getText().toString());
            else if(comp==71) runNode(edit.getText().toString());
            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String str = langs(edit.getText().toString()).replaceAll("(?i)jdoodle", "main");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text.setText(str);
                            }
                        });
                    }
                }).start();
            }
            mTimer.removeMessages(0);

        }
    };
    ScrollView scroll;
    private SharedPreferences share;
    static V8 v8;
    private static boolean isPrint;

    public void runV8(final String str) {
        try {
            //System.gc();
            isPrint = false;
            text.setText("");
            Object obj = v8.executeScript(str);
            //v8Console.release();
            //v8.terminateExecution();
            if (!isPrint)
                text.setText(String.valueOf(obj));
            //v8.terminateExecution();
        } catch (final Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStack = sw.toString();
            text.setText(sStack);
            e.printStackTrace();

        }
    }

    public void runNode(final String str) {
    }



    public void runJS(final String str) {
        String strs = "";
        try {
            isConsole = false;
            MyContext.enterContext();
            try {
                strs = Context.toString(MyRealCon.evaluateString(MyScope, str, "Rhino", 1, null));
                //MyResponder.call(MyRealCon, MyScope, MyScope, new Object[]{str}));
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStack = sw.toString();
                e.printStackTrace();
                strs = sStack;
            }

            final String finalStrs = strs;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.append(finalStrs);
                }
            });
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String sStack = sw.toString();
                    text.setText(sStack);
                    e.printStackTrace();
                }
            });
        }
    }
    private static Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private static Thread.UncaughtExceptionHandler mCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(ctx, ex.toString(), Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();

            mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
        }
    };

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mCaughtExceptionHandler);
        ctx = getApplicationContext();
        share = getSharedPreferences("Cache", Activity.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                    "com.Lion.Rhino.Threa", "rhino",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager  manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        v8 = V8.createV8Runtime("global");
        final V8Object v8Console = new V8Object(v8);
        v8.add("console", v8Console);
        v8Console.registerJavaMethod(new console2(), "log", "log", new Class<?>[] { Object.class });
        v8.registerJavaMethod(new Printer(), "alert", "alert", new Class<?>[]{Object.class});
        v8.registerJavaMethod(new Printer(), "print", "print", new Class<?>[]{Object.class});
        v8.registerJavaMethod(new Printer(), "getApplicationContext", "getApplicationContext", new Class<?>[0]);
        v8.add("author","jthis");

        RhinoException.setStackStyle(StackStyle.V8);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.enableDefaults();
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
//        final V8 v8 = V8.createV8Runtime();
//        final V8Object v8Console = new V8Object(v8);
//        v8.add("console", v8Console);
//        v8Console.registerJavaMethod(new console(), "log", "log", new Class<?>[] { Object.class });
//        v8.registerJavaMethod(new Printer(), "alert", "alert", new Class<?>[]{Object.class});
//        v8.registerJavaMethod(new Printer(), "print", "print", new Class<?>[]{Object.class});

        scroll = new ScrollView(this);
        scroll.setNestedScrollingEnabled(false);
        //scroll.setScrollbarFadingEnabled(false);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Multilingual Compiler");

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor("#3f51b5"));
        setSupportActionBar(toolbar);


        edit = new ClearEditText(this);
        edit.setHint("Your Code");
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("autoUpdate", true))
            edit.setText(share.getString("Text", ""));

        final ScrollingView scrollView = new ScrollingView(this);
        scrollView.addView(edit);
        scrollView.setMaxLines(16);
        Button btn = new Button(this);
        btn.setTransformationMethod(null);
        btn.setText("Run");
        text = new TextView(this);
        text.setTextIsSelectable(true);
        text.setMinHeight(1400);
        Button btn2 = new Button(this);
        btn2.setTransformationMethod(null);
        btn2.setText("Focus Up");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll.fullScroll(View.FOCUS_UP);
                scroll.scrollTo(0, 0);
            }
        });

        layout.addView(toolbar);
        layout.addView(scrollView);
        layout.addView(btn);
        layout.addView(text);
        layout.addView(btn2);

        scroll.addView(layout);


        setContentView(scroll);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer.sendEmptyMessage(0);
                text.setText("");
                edit.requestFocus();

                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                if (edit.getText().toString().length() > 0)
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences.Editor editor = share.edit();
                            editor.putString("Text", edit.getText().toString()); // key, value를 이용하여 저장하는 형태
                            editor.apply();
                        }
                    }).start();

            }
        });
        setMyContext(new RhinoAndroidHelper(this));
        initializeJS(this);
    }


    public static class Printer {
        @SuppressLint("SetTextI18n")
        public void print(final Object msg) {
            Toast.makeText(ctx, String.valueOf(msg), Toast.LENGTH_SHORT).show();
        }
        public android.content.Context getApplicationContext(){
            return ctx;
        }
        public void alert(final Object msg) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
            dialog.setTitle("결과");

            dialog.setMessage(String.valueOf(msg));
            dialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu1, menu);
        return true;
    }
    static int scomp;
    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(ctx, "ㅇㅇ", Toast.LENGTH_SHORT).show();
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        text.setTextSize(Integer.parseInt(mPref.getString("userNameOpen", "18")));
        comp = Integer.parseInt(mPref.getString("program", "1"));
        scomp=comp;
        edit.setUseClearIcon(mPref.getBoolean("clearButton", true));
        inputText=mPref.getString("input", "");
    }

    //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_debug:
                intent = new Intent(this, Debug.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onActionModeStarted(android.view.ActionMode mode) {
        //mode.getMenu().clear();
        Menu menus = mode.getMenu();
        onInitializeMenu(menus);
        super.onActionModeStarted(mode);
    }

    private List<ResolveInfo> getSupportedActivities() {
        PackageManager packageManager =
                MainActivity.this.getPackageManager();
        return
                packageManager.queryIntentActivities(createProcessTextIntent(),
                        0);
    }

    private Intent createProcessTextIntent() {
        return new Intent()
                .setAction(Intent.ACTION_PROCESS_TEXT)
                .setType("text/plain");
    }

    private Intent createProcessTextIntentForResolveInfo(ResolveInfo info) {
        return createProcessTextIntent()
                .putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
                .setClassName(info.activityInfo.packageName,
                        info.activityInfo.name);
    }

    public void onInitializeMenu(Menu menu) {
        int menuItemOrder = 100;
        for (ResolveInfo resolveInfo : getSupportedActivities()) {
            menu.add(Menu.NONE, Menu.NONE,
                    menuItemOrder++,
                    resolveInfo.loadLabel(this.getPackageManager()))//.setIcon(resolveInfo.loadIcon(getPackageManager()))
                    .setIntent(createProcessTextIntentForResolveInfo(resolveInfo))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (edit.getText().toString().length() > 0)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor = share.edit();
                    editor.putString("Text", edit.getText().toString()); // key, value를 이용하여 저장하는 형태
                    editor.apply();
                }
            }).start();
    }

    public static void setMyContext(RhinoAndroidHelper str) {
        MyContext = str;
    }

    public static void setMyScope(ScriptableObject str) {
        MyScope = str;
    }

    public void setMyResponder(Function str) {
        this.MyResponder = str;
    }

    public static void initializeJS(android.content.Context ctx) {

        //System.gc();

        Context rhino = MyContext.enterContext();
        MyRealCon = rhino;
        rhino.setOptimizationLevel(-1);
        rhino.setLanguageVersion(Context.VERSION_ES6);
        try {
            ScriptableObject top = new ImporterTopLevel(rhino);
            ScriptableObject scope = (ScriptableObject) rhino.initStandardObjects(top);
            setMyScope(scope);

            //addMethod
            ScriptableObject.putProperty(scope, "ctx", ctx);
            ScriptableObject.putProperty(scope, "printTable", text);
            ScriptableObject.putProperty(scope, "scriptTable", edit);
//            ScriptableObject.putProperty(scope, "console", new console());
            ScriptableObject.defineClass(scope, console.class);
            scope.defineFunctionProperties(new String[]{"print", "loadScript","require"}, function.class, DONTENUM);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStack = sw.toString();
            e.printStackTrace();
            //return sStack;
        }
    }

    private void compile(String src) {
        try {
            Globals globals = JsePlatform.standardGlobals();

            globals.set("print", CoerceJavaToLua.coerce(new Print()));
            LuaValue chunk = globals.load(src);
            chunk.call();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStack = sw.toString();
            text.setText(sStack);
        }
    }


    public String langs(String strs) {
        try {
            URL url = new URL("https://www.jdoodle.com/engine/execute");
            URLConnection con = url.openConnection();
            StringBuilder str2 = new StringBuilder();
            if (con != null) {
                ((HttpURLConnection) con).setRequestMethod("POST");
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//                con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
                con.setRequestProperty("x-requested-with", "XMLHttpRequest");
                con.setRequestProperty("referer", "https://www.jdoodle.com/online-compiler-c++17/");
                con.setRequestProperty("kurukku-kuri","e593934a-54c5-41af-a4ec-ac4efcd8f748");
                con.setRequestProperty("referer", "https://www.jdoodle.com" + Arrays.languages[comp - 2][0]);
                con.setRequestProperty("Cookie", "SESSION=8b6872b0-10fe-49a7-8850-52dd8fcf90ca");
                /////{
                //  "script": "var x = 10;\nvar y = 25;\nvar z = x + y;\n\nprint('Sum of ',x,' and ',y,' is ',z);",
                //  "args": null,
                //  "stdin": null,
                //  "language": "rhino",
                //  "versionIndex": 2,
                //  "libs": [],
                //  "projectKey": 1001,
                //  "hasInputFiles": false
                //}
                String inp = "{\"script\":\"" + strs.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"").replace("\t","\\t")
                        + "\",\"args\":null,\"stdin\":\""+inputText.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")+"\",\"language\":\"" + Arrays.languages[comp - 2][1].toLowerCase() +
                        "\",\"libs\":[],\"versionIndex\":" + Integer.parseInt(mPref.getString("version", "0")) +
                        ",\"projectKey\": 1001,\"hasInputFiles\": false };";
                Log.i("dsd", "langs: " + inp);
                OutputStream os = con.getOutputStream();
                os.write(inp.getBytes());
                os.flush();
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                str2 = new StringBuilder(br.readLine());
                String line = "";
                while ((line = br.readLine()) != null) {
                    str2.append("\n").append(line);
                }
                isr.close();
                br.close();
                ((HttpURLConnection) con).disconnect();
                JSONObject object = new JSONObject(String.valueOf(str2));
                return object.getString("output");
            }
            return "null";
        } catch (MalformedURLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }

    }


    private static class Print extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue msg) {
            Toast.makeText(ctx, msg.tojstring(), Toast.LENGTH_SHORT).show();
            return NIL;
        }
    }


    public static class function {
        @JSStaticFunction
        public static void print(String str) {
            Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
        }

        @JSStaticFunction
        public static Object loadScript(String str,boolean getScope) throws IOException {
            Context cx = Context.enter();
            Scriptable scope = getScope?MyScope:new ImporterTopLevel(cx);
            cx.setLanguageVersion(200);
            cx.evaluateReader(scope, new FileReader(str), "SubScript", 1, PERMANENT | DONTENUM);
            return scope;
        }

        @JSStaticFunction
        public static Object require(String str,boolean newScope) throws IOException {
            Context cx=Context.enter();
            ScriptableObject scope = newScope?new ImporterTopLevel(cx):MyScope;
            cx.setLanguageVersion(200);
            ScriptableObject exports = (ScriptableObject) cx.newObject(scope);
            ScriptableObject module = (ScriptableObject) cx.newObject(scope);
            module.defineProperty("exports", exports, PERMANENT | DONTENUM);
            scope.defineProperty("exports", exports, PERMANENT | DONTENUM);
            scope.defineProperty("module", module, PERMANENT | DONTENUM);
            cx.evaluateReader(scope, new FileReader(str), "SubScript", 1, PERMANENT | DONTENUM);
            return exports;
        }


    }

    public static final class console extends ScriptableObject {
        @SuppressLint("SetTextI18n")

        @JSStaticFunction
        public static String log(final String str) {
            text.append(str + "\n");
            if (!isConsole) isConsole = true;
            return "";
        }

        @Override
        public String getClassName() {
            return "console";
        }
    }
    public static class console2{
        @SuppressLint("SetTextI18n")
        public void log(final Object msg) {
            isPrint = true;
            text.setText(text.getText().toString() + msg + "\n");
        }
    }
};

