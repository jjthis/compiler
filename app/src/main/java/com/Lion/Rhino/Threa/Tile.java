package com.Lion.Rhino.Threa;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.faendir.rhino_android.RhinoAndroidHelper;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Tile extends TileService {

    static Context ctx;
    TextView button;
    LinearLayout layout;
    WindowManager[] wManager = new WindowManager[6];
    ScrollView[] scroll = new ScrollView[6];
    WindowManager.LayoutParams[] wLayoutParams = new WindowManager.LayoutParams[6];
    static TextView[] btn = new TextView[6];
    String inp = "";
    static String bres;
    double btnPressTime = 0;
    double btnPressTime2 = 0;
    boolean LC_Check = false;
    boolean LC_Check3 = false;
    boolean LC_Check2 = false;
    int lastMemo=1;
    RhinoAndroidHelper MyContext;
    ScriptableObject MyScope;
    Function MyResponder;
    org.mozilla.javascript.Context MyRealCon;
    TextView btnm = null;

    LinearLayout layoutm;
    TextView[] btnms = new TextView[2];
    String ett = "";
    TextView btm = null;
    LinearLayout laym = null;
    TextView[] btms = new TextView[4];
    TextView[] mes = new TextView[20];

    public double dp(int d) {
        return Math.ceil(d * ctx.getResources().getDisplayMetrics().density);
    }


    public void onClick() {
        if( wManager[0] != null) return;
        ctx = this;
        MyContext = new RhinoAndroidHelper(this);
        initializeJS();

        AppScript1();
    }

    public void runOnUiThread(Runnable r) {
        new Handler().post(r);
    }

    public void AppScript() {
        try {
            button = new TextView(ctx);
            layout = new LinearLayout(ctx);
            layout.setOrientation(1);
            button.setText("eval");
            button.setTextSize(18);
            button.setWidth(330);
            button.setHeight(120);
            button.setGravity(Gravity.CENTER);
            button.setTextColor(Color.rgb(43, 113, 226));
            android.graphics.drawable.GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.rgb(220, 223, 232));
            gd.setCornerRadius(15);
            button.setBackground(gd);
            //button.setBackgroundDrawable(new RippleDrawable(ColorStateList.valueOf(Color.WHITE), new ColorDrawable(Color.rgb(63, 81, 181)), null));
            layout.addView(button);

            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!LC_Check) LC_Check = true;
                    return true;
                }
            });
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    if (LC_Check) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                wLayoutParams[0].x = (int) (e.getRawX() - wLayoutParams[0].width / 2 - 150);
                                wLayoutParams[0].y = (int) (e.getRawY() - wLayoutParams[0].height / 2 - 100);
                                wLayoutParams[0].gravity = Gravity.LEFT | Gravity.TOP;
                                wManager[0].updateViewLayout(scroll[0], wLayoutParams[0]);
                                break;
                            case MotionEvent.ACTION_UP:
                                LC_Check = false;
                                break;
                        }
                    }
                    return false;
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() > btnPressTime2 + 500) {
                        //1
                        if (btn[0] == null)
                            openMenu();
                        else
                            menuClose();

                    }
                    if (System.currentTimeMillis() > btnPressTime + 500) {
                        btnPressTime = System.currentTimeMillis();
                        //1클릭
                        return;
                    }

                    if (System.currentTimeMillis() <= btnPressTime + 500) {
                        if (System.currentTimeMillis() > btnPressTime2 + 500) {
                            btnPressTime2 = System.currentTimeMillis();
                            //투클릭
                            return;
                        }
                        //쓰리클릭
                        Toast.makeText(ctx, "버튼이 사라집니다", Toast.LENGTH_SHORT).show();
                        closew();
                    }
                }
            });
            scroll[0] = new ScrollView(ctx);
            wManager[0] = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            wLayoutParams[0] = new WindowManager.LayoutParams(-2, -2, WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);
            wLayoutParams[0].gravity = Gravity.LEFT | Gravity.TOP;
            wLayoutParams[0].x = 361;
            wLayoutParams[0].y = 796;

            /*
TYPE_SYSTEM_ERROR
TYPE_SYSTEM_OVERLAY
TYPE_SYSTEM_ALERT
TYPE_PRIORITY_PHONE
TYPE_TOAST
TYPE_PHONE
*/
//TYPE_STATUS_BAR_OVERLAY
            scroll[0].addView(layout);
            scroll[0].setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            gd = new GradientDrawable(); gd.setColor(Color.WHITE); gd.setCornerRadius(15); scroll[0].setBackground(gd);
            scroll[0].setBackground(gd);
//scroll[0].setBackgroundColor(android.graphics.Color.argb(2000, 0, 500, 2000));
            wManager[0].addView(scroll[0], wLayoutParams[0]);
            wManager[0].updateViewLayout(scroll[0], wLayoutParams[0]);
        } catch (Exception e) {
e.printStackTrace();
        }
    }

    public void initializeJS() {
        //System.gc();

        org.mozilla.javascript.Context rhino = MyContext.enterContext();
        MyRealCon = rhino;
        rhino.setOptimizationLevel(-1);
        rhino.setLanguageVersion(org.mozilla.javascript.Context.VERSION_ES6);
        try {
            ScriptableObject top = new ImporterTopLevel(rhino);
            ScriptableObject scope = (ScriptableObject) rhino.initStandardObjects(top);
            MyScope = (scope);
            //addMethod
            ScriptableObject.putProperty(scope, "ctx", ctx);
            scope.defineFunctionProperties(new String[]{"cls", "bsend", "print", "bres"}, function.class,
                    ScriptableObject.DONTENUM);


            //assets
            //InputStream iss = am.open("MoreMath.js");
            //rhino.evaluateReader(scope, new InputStreamReader(iss), "", 1, null);
            //Main

            Function str = null;
            if (MyResponder == null)
                str = rhino.compileFunction(scope, "function response(str){return eval(str)+'';}", "MyScript", 0, null);
            else str = MyResponder;
            //public void str = rhino.compilepublic void(scope, "public void response(str){return eval(str)+'';}", "MyScript", 0, null);
            MyResponder = (str);

            //str.exec(rhino, scope);
            //Object obj = str.get("response", (Scriptable) scope);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStack = sw.toString();
            e.printStackTrace();
            //return sStack;
        }
    }

    public void openMenu() {

        wLayoutParams[0].flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wLayoutParams[0].flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wLayoutParams[0].flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        btn[0] = new android.widget.TextView(ctx);
        btn[0].setTextSize(18);
        btn[0].setGravity(Gravity.CENTER);
        btn[1] = new android.widget.EditText(ctx);
//btn[2].setTextIsSelectable(false);
        btn[1].setText(inp);
        btn[1].setMaxLines(10);
        btn[1].setMaxWidth(900);
        btn[1].getBackground().clearColorFilter();
        btn[1].getBackground().setColorFilter(Color.rgb(174, 177, 186), android.graphics.PorterDuff.Mode.SRC_ATOP);
        btn[1].setTextColor(Color.rgb(174, 177, 186));
        btn[2] = new android.widget.TextView(ctx);
        btn[2].setTextIsSelectable(true);
        btn[2].setMaxWidth(900);
        btn[2].setMaxLines(16);
        btn[2].setTextColor(Color.rgb(174, 177, 186));
        btn[4] = new android.widget.TextView(ctx);
        btn[4].setText("");
        btn[4].setTextIsSelectable(true);
        btn[4].setMaxWidth(900);
        btn[4].setTextColor(Color.rgb(174, 177, 186));
        btn[4].setMaxLines(4);
        btn[0].setText("running");
        btn[0].setTextColor(Color.rgb(221, 62, 22));

//btn[1].setInputType(android.text.InputType.TYPE_CLASS_TEXT);

        btn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    inp = btn[1].getText() + "";
                    bres = "";

                    String strs = null;
                    try {
                        MyContext.enterContext();
                        try {
                            strs = org.mozilla.javascript.Context.toString(MyResponder.call(MyRealCon, MyScope, MyScope, new Object[]{btn[1].getText().toString()}));

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
                                btn[2].setText(finalStrs);
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
                                btn[2].setText(sStack);
                                e.printStackTrace();
                            }
                        });
                    }

//                    btn[2].setText(eval(btn[1].getText()+"")+"");
                    btn[4].setText(bres.replaceFirst("\n", ""));
                } catch (Exception e) {
                    btn[2].append(e + "");
                    e.printStackTrace();
                }
            }
        });

        btn[4].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((ClipboardManager) ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE)).setText(btn[4].getText() + "");
                Toast.makeText(ctx, "복사", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        btn[2].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((ClipboardManager) ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE)).setText(btn[2].getText() + "");
                Toast.makeText(ctx, "복사", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        btn[1].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((ClipboardManager) ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE)).setText(btn[1].getText() + "");
                Toast.makeText(ctx, "복사", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        btn[3] = new android.widget.TextView(ctx);
        btn[3].setTextSize(18);
        btn[3].setGravity(Gravity.CENTER);
        btn[3].setText("show tool");
        btn[3].setTextColor(Color.rgb(221, 62, 22));
        btn[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wManager[1] == null)
                    AppScript1();
            }
        });

        btn[5] = new android.widget.TextView(ctx);
        btn[5].setTextSize(18);
        btn[5].setGravity(Gravity.CENTER);
        btn[5].setText("초기화");
        btn[5].setTextColor(Color.rgb(221, 62, 22));
        btn[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inp = "";
                bres = "";
                btn[1].setText("");
                btn[2].setText("");
                btn[4].setText("");
            }
        });
        layout.addView(btn[3]);
        layout.addView(btn[4]);
        layout.addView(btn[1]);
        layout.addView(btn[0]);
        layout.addView(btn[2]);
        layout.addView(btn[5]);

        wManager[0].updateViewLayout(scroll[0], wLayoutParams[0]);

    }

    ;

    public void menuClose() {
        inp = btn[1].getText() + "";
        wLayoutParams[0].flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wLayoutParams[0].flags &= ~WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        wManager[0].updateViewLayout(scroll[0], wLayoutParams[0]);
        for (TextView a : btn) {
            layout.removeView(a);
        }
        btn = new TextView[6];
    }

    public void closew() {
        wManager[0].removeView(scroll[0]);
        wManager[0] = null;
    }

    public void closew1() {
        wManager[1].removeView(scroll[1]);
        wManager[1] = null;
    }

    public void closew2() {
        wManager[2].removeView(scroll[2]);
        wManager[2] = null;
    }

    public void AppScript1() {
        try {
            btnm = new TextView(ctx);
            layoutm = new LinearLayout(ctx);
            layoutm.setOrientation(1);
            btnm.setText("tool");
            btnm.setTextSize(18);
            btnm.setWidth(330);
            btnm.setHeight(120);
            btnm.setGravity(Gravity.CENTER);
            btnm.setTextColor(Color.rgb(43, 113, 226));
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.rgb(220, 223, 232));
            gd.setCornerRadius(15);
            btnm.setBackground(gd);
            //btnm.setBackgroundDrawable(new RippleDrawable(ColorStateList.valueOf(Color.WHITE), new ColorDrawable(Color.rgb(220, 223, 232)), null));
            layoutm.addView(btnm);

            LC_Check2 = false;
            btnm.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!LC_Check2) LC_Check2 = true;
                    return true;
                }
            });
            btnm.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    if (LC_Check2) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                wLayoutParams[1].x = (int) (e.getRawX() - wLayoutParams[1].width / 2 - 150);
                                wLayoutParams[1].y = (int) (e.getRawY() - wLayoutParams[1].height / 2 - 100);
                                wLayoutParams[1].gravity = Gravity.LEFT | Gravity.TOP;
                                wManager[1].updateViewLayout(scroll[1], wLayoutParams[1]);
                                break;
                            case MotionEvent.ACTION_UP:
                                LC_Check2 = false;
                                break;
                        }
                    }
                    return false;
                }
            });
            btnm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() > btnPressTime2 + 500) {
                        //1,2클릭
                        if (btnms[0] == null)
                            openMenum();
                        else
                            menuClosem();
                    }
                    if (System.currentTimeMillis() > btnPressTime + 500) {
                        btnPressTime = System.currentTimeMillis();
                        //1클릭
                        return;
                    }

                    if (System.currentTimeMillis() <= btnPressTime + 500) {
                        if (System.currentTimeMillis() > btnPressTime2 + 500) {
                            btnPressTime2 = System.currentTimeMillis();
                            //투클릭
                            return;
                        }
                        //쓰리클릭
                        Toast.makeText(ctx, "버튼이 사라집니다", Toast.LENGTH_SHORT).show();

                        closew1();
                    }
                }
            });
            int pad = -1;
            layoutm.setPadding(pad, pad, pad, pad);
            scroll[1] = new ScrollView(ctx);
            wManager[1] = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            wLayoutParams[1] = new WindowManager.LayoutParams(-2, -2, WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);
/*
TYPE_SYSTEM_ERROR
TYPE_SYSTEM_OVERLAY
TYPE_SYSTEM_ALERT
TYPE_PRIORITY_PHONE
TYPE_TOAST
TYPE_PHONE
*/
//TYPE_STATUS_BAR_OVERLAY
            scroll[1].addView(layoutm);
            scroll[1].setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            gd = new GradientDrawable();
            gd.setColor(Color.WHITE);
            gd.setCornerRadius(15);
            scroll[1].setBackground(gd);
//scroll[1].setBackgroundDrawable(new RippleDrawable(ColorStateList.valueOf(Color.WHITE), new ColorDrawable(Color.rgb(255, 255, 255)), null));
            wLayoutParams[1].gravity = Gravity.LEFT | Gravity.TOP;
            wLayoutParams[1].x = 361;
            wLayoutParams[1].y = 796;
            wManager[1].addView(scroll[1], wLayoutParams[1]);
        } catch (Exception e) {
e.printStackTrace();
        }
    }

    public void openMenum() {
        btnms[0] = new android.widget.TextView(ctx);
        btnms[0].setTextSize(18);
        btnms[0].setGravity(Gravity.CENTER);
        btnms[0].setText("eval");
        btnms[0].setTextColor(Color.rgb(221, 62, 22));
        btnms[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wManager[0] == null)
                    AppScript();
            }
        });

        btnms[1] = new android.widget.TextView(ctx);
        btnms[1].setTextSize(18);
        btnms[1].setGravity(Gravity.CENTER);
        btnms[1].setText("memo");
        btnms[1].setTextColor(Color.rgb(221, 62, 22));
        btnms[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wManager[2] == null)
                    AppScript2();
            }
        });

        for (TextView a : btnms)
            layoutm.addView(a);
        wManager[1].updateViewLayout(scroll[1], wLayoutParams[1]);

    }

    public void menuClosem() {
        for (TextView a : btnms)
            layoutm.removeView(a);
        btnms = new TextView[2];
    }

    public void AppScript2() {
        try {

            btm = new TextView(ctx);
            laym = new LinearLayout(ctx);
            laym.setOrientation(1);
            btm.setText("memo");
            btm.setTextSize(18);
            btm.setWidth(330);
            btm.setHeight(120);
            btm.setGravity(Gravity.CENTER);
            btm.setTextColor(Color.rgb(43, 113, 226));
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.rgb(220, 223, 232));
            gd.setCornerRadius(15);
            btm.setBackground(gd);
            //btm.setBackgroundDrawable(new RippleDrawable(ColorStateList.valueOf(Color.WHITE), new ColorDrawable(Color.rgb(63, 81, 181)), null));
            laym.addView(btm);

            LC_Check3 = false;
            btm.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!LC_Check3) LC_Check3 = true;
                    return true;
                }
            });

            btm.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    if (LC_Check3) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                wLayoutParams[2].x = (int) (e.getRawX() - wLayoutParams[2].width / 2 - 150);
                                wLayoutParams[2].y = (int) (e.getRawY() - wLayoutParams[2].height / 2 - 100);
                                wLayoutParams[2].gravity = Gravity.LEFT | Gravity.TOP;
                                wManager[2].updateViewLayout(scroll[2], wLayoutParams[2]);
                                break;
                            case MotionEvent.ACTION_UP:
                                LC_Check3 = false;
                                break;
                        }
                    }
                    return false;
                }
            });


            btm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() > btnPressTime2 + 500) {
                        //1,2클릭
                        if (btms[0] == null)
                            openMenu1();
                        else {
                            ett = btms[1].getText() + "";
                            menuClose1();
                        }
                    }
                    if (System.currentTimeMillis() > btnPressTime + 500) {
                        btnPressTime = System.currentTimeMillis();
                        //1클릭
                        return;
                    }

                    if (System.currentTimeMillis() <= btnPressTime + 500) {
                        if (System.currentTimeMillis() > btnPressTime2 + 500) {
                            btnPressTime2 = System.currentTimeMillis();
                            //투클릭
                            return;
                        }
                        //쓰리클릭
                        Toast.makeText(ctx, "버튼이 사라집니다", Toast.LENGTH_SHORT).show();

                        closew2();
                    }
                }
            });
            int pad = -1;
            laym.setPadding(pad, pad, pad, pad);
            scroll[2] = new ScrollView(ctx);
            wManager[2] = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            wLayoutParams[2] = new WindowManager.LayoutParams(-2, -2, WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);
/*
TYPE_SYSTEM_ERROR
TYPE_SYSTEM_OVERLAY
TYPE_SYSTEM_ALERT
TYPE_PRIORITY_PHONE
TYPE_TOAST
TYPE_PHONE
TYPE_APPLICATION_PANEL
*/
//TYPE_STATUS_BAR_OVERLAY
            scroll[2].addView(laym);
            scroll[2].setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            gd = new GradientDrawable();
            gd.setColor(Color.WHITE);
            gd.setCornerRadius(15);
            scroll[2].setBackground(gd);
//scroll[2].setBackgroundColor(android.graphics.Color.argb(2000, 0, 500, 2000));
            wLayoutParams[2].gravity = Gravity.LEFT | Gravity.TOP;
            wLayoutParams[2].x = 361;
            wLayoutParams[2].y = 796;
            wManager[2].addView(scroll[2], wLayoutParams[2]);
        } catch (Exception e) {
e.printStackTrace();
        }
    }

    ;

    public void openMenu1() {

        wLayoutParams[2].flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wLayoutParams[2].flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wLayoutParams[2].flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        btms[0] = new android.widget.TextView(ctx);
        btms[0].setTextSize(18);
        btms[0].setGravity(Gravity.CENTER);
        btms[1] = new android.widget.EditText(ctx);
        btms[1].setMaxLines(10);
        btms[1].setMaxWidth(900);
        btms[1].setText(ett);
        btms[1].setTextColor(Color.rgb(174, 177, 186));
        btms[1].getBackground().clearColorFilter();
        btms[1].getBackground().setColorFilter(Color.rgb(174, 177, 186), android.graphics.PorterDuff.Mode.SRC_ATOP);
        btms[0].setText("save");
        btms[0].setTextColor(Color.rgb(221, 62, 22));

        btms[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btms[1].getText() + "" != "") {
                    saveFile("sdcard/memo/memo.txt", "\n" + (btms[1].getText() + "").replaceAll("\n", "::::::"), true)
                    ;
                    lastMemo++;
                    mes[lastMemo] = (new android.widget.TextView(ctx));
                    int btl = lastMemo;
                    mes[btl].setId(btl);
                    mes[btl].setText(btms[1].getText() + "");
                    mes[btl].setTextSize(18);
                    mes[btl].setMaxWidth(900);
                    mes[btl].setTextColor(Color.rgb(174, 177, 186));
                    mes[btl].setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ((ClipboardManager) ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE)).setText(mes[v.getId()].getText() + "");
                            Toast.makeText(ctx, "복사", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
                    mes[btl].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() > btnPressTime + 500) {
                                btnPressTime = System.currentTimeMillis();
                                //1클릭
                                return;
                            }

                            if (System.currentTimeMillis() <= btnPressTime + 500) {
                                if (System.currentTimeMillis() > btnPressTime2 + 500) {
                                    btnPressTime2 = System.currentTimeMillis();
                                    //투클릭
                                    return;
                                }
                                //쓰리클릭
                                Toast.makeText(ctx, "삭제", Toast.LENGTH_SHORT).show();
                                saveFile("sdcard/memo/memo.txt", readFile("sdcard/memo/memo.txt").replaceFirst("\n" + (mes[v.getId()].getText() + "").replaceAll("\n", "::::::"), ""), false)
                                ;
                                laym.removeView(mes[v.getId()]);
                                mes[v.getId()] = null;
                            }
                        }
                    });


                    laym.addView(mes[btl], 4);
                    wManager[2].updateViewLayout(scroll[2], wLayoutParams[2]);
                    btms[1].setText("");
                }
            }
        });

        btms[3] = new android.widget.TextView(ctx);
        btms[3].setTextSize(18);
        btms[3].setGravity(Gravity.CENTER);
        btms[3].setText("show tool");
        btms[3].setTextColor(Color.rgb(221, 62, 22));
        btms[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wManager[1] == null)
                    AppScript1();
            }
        });

        laym.addView(btms[3]);
        laym.addView(btms[0]);
        laym.addView(btms[1]);

        if (readFile("sdcard/memo/memo.txt") != null) {
            String[] mem1 = readFile("sdcard/memo/memo.txt").split("\n");
            for (int a = 1; a < mem1.length; a++) {
                int btl = a;
                lastMemo = a;
                mes[btl] = new TextView(ctx);
                mes[btl].setId(btl);
                mes[btl].setText(mem1[a].replaceAll("::::::", "\n"));
                mes[btl].setTextSize(18);
                mes[btl].setTextColor(Color.rgb(174, 177, 186));
                mes[btl].setMaxWidth(900);
                mes[btl].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ((ClipboardManager) ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE)).setText(mes[v.getId()].getText() + "");
                        Toast.makeText(ctx, mes[v.getId()].getText() + "", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                mes[btl].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (System.currentTimeMillis() > btnPressTime + 500) {
                            btnPressTime = System.currentTimeMillis();
                            //1클릭
                            return;
                        }

                        if (System.currentTimeMillis() <= btnPressTime + 500) {
                            if (System.currentTimeMillis() > btnPressTime2 + 500) {
                                btnPressTime2 = System.currentTimeMillis();
                                //투클릭
                                return;
                            }
                            //쓰리클릭

                            Toast.makeText(ctx, "삭제", Toast.LENGTH_SHORT).show();
                            saveFile("sdcard/memo/memo.txt", (readFile("sdcard/memo/memo.txt") + "").replaceFirst("\n" + (mes[v.getId()].getText() + "").replaceAll("\n", "::::::"), ""), false)
                            ;
                            laym.removeView(mes[v.getId()]);
                            mes[v.getId()] = null;

                        }
                    }
                });


                laym.addView(mes[btl], 4);
            }
        }


        wManager[2].updateViewLayout(scroll[2], wLayoutParams[2]);

    }

    ;

    public void menuClose1() {
        try {
            wLayoutParams[2].flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wLayoutParams[2].flags &= ~WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            wManager[2].updateViewLayout(scroll[2], wLayoutParams[2]);
            for (TextView a : btms)
                laym.removeView(a);
            for (int a=1;a<lastMemo+1;a++) {
                laym.removeView(mes[a]);
            }
            mes = new TextView[20];
            btms = new TextView[4];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String readFile(String path) {
        try {
            File file = new java.io.File(path);
            if (!(file.exists())) return "";
            FileInputStream fis = new java.io.FileInputStream(file);
            InputStreamReader isr = new java.io.InputStreamReader(fis);
            BufferedReader br = new java.io.BufferedReader(isr);
            String s = br.readLine();
            String read = "";
            while ((read = br.readLine()) != null) {
                s += "\n" + read;/*Log.debug(read);*/
            }
            fis.close();
            isr.close();
            br.close();
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return e + "";
        } catch (IOException e) {
            e.printStackTrace();
            return e + "";
        }
    }

    public void saveFile(String path, String content, Boolean bool) {
        try {
            File file = new java.io.File(path);
            if (!file.exists()) {
                return;
            }
            FileWriter fw = new java.io.FileWriter(file, bool);
            BufferedWriter bw = new java.io.BufferedWriter(fw);
            String str = new java.lang.String(content);
            bw.write(str);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ;

    public static class function {

        @JSStaticFunction
        public static void print(String str) {
            Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
        }

        @JSStaticFunction
        public static String bres() {
            return bres;
        }


        @JSStaticFunction
        public static void cls() {
            btn[1].setText("");
        }

        @JSStaticFunction
        public static void bsend(String strings) {
                bres += "\n" + strings;
        }


//        @JSStaticFunction
//        public void runOnUiThread(Runnable runnable) {
//
//        }
    }
}
