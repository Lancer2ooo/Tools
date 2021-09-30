package com.example.tools.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.example.tools.R;
import com.example.tools.widget.BlurTransformation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author : Lancer
 * e-mail : lancer2ooo@qq.com
 * date   : 2021/9/27
 * desc   :
 * version: 1.0
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    LinearLayout linearLayout;
    FloatingActionButton floatingActionButton;

    Button button_calculator;
    Button button_color;
    Button button_logistics;
    Button button_translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置应用全屏显示
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        //设置应用全屏显示 沉浸式任务栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            WindowInsetsController insetsController = getWindow().getInsetsController();
//            if (insetsController != null) {
//                getWindow().setStatusBarColor(Color.TRANSPARENT);
//            }
//        } else {
//            getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//            );
//        }

        imageView = findViewById(R.id.back_img);
        sendRequestWithHttpURLConnection();

        //设置时间布局的点击事件
        linearLayout = findViewById(R.id.top_clock);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"complete",Toast.LENGTH_LONG).show();
                Intent intent_to_full = new Intent(MainActivity.this,FullScreenClockActivity.class);
                startActivity(intent_to_full);
            }
        });

        //给悬浮按钮设置点击事件
        floatingActionButton = findViewById(R.id.float_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoticeDialog("作者：Lancer \n\nGithub：\n\nhttps://github.com/Lancer2ooo/Tools \n\n欢迎访问",MainActivity.this);
            }
        });

        allItemController();
    }

    private void sendRequestWithHttpURLConnection() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parseJSONWithJSONObject(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    //    JSON的解析
    private void parseJSONWithJSONObject(String jsonData) {
        try {
            // JSONArray jsonArray = new JSONArray(jsonData);

            JSONArray jsonArray = new JSONObject(jsonData).getJSONArray("images");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");

                Log.d("MainActivity", "url is " + url);
                String url1="http://cn.bing.com"+url;
                showResponse(url1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //毛玻璃效果
                MultiTransformation multiTransformation = new MultiTransformation(
                        new BlurTransformation(MainActivity.this,10)
                );

                // 在这里进行UI操作，将结果显示到界面上
                Glide.with(MainActivity.this)
                        .load(response)
                        .dontAnimate()
                        .transform(multiTransformation)
                        .into(imageView);
            }
        });

    }

    //设置弹窗
    public void showNoticeDialog(String data, Context mContext) {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("关于");
        builder.setMessage(data);
        builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    public void allItemController() {
        button_calculator = findViewById(R.id.calculator);
        button_color = findViewById(R.id.color);
        button_logistics = findViewById(R.id.logistics);
        button_translate = findViewById(R.id.translate);

        button_calculator.setOnClickListener(this);
        button_color.setOnClickListener(this);
        button_logistics.setOnClickListener(this);
        button_translate.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.calculator:
                Intent intent_to_calculator = new Intent(MainActivity.this,CalculatorActivity.class);
                startActivity(intent_to_calculator);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                // Toast.makeText(MainActivity.this,"complete_calculator",Toast.LENGTH_LONG).show();
                break;
            case R.id.color:
                Toast.makeText(MainActivity.this,"complete_color",Toast.LENGTH_LONG).show();
                break;
            case R.id.logistics:
                Intent intent_to_logistics = new Intent(MainActivity.this,LogisticsActivity.class);
                startActivity(intent_to_logistics);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//                Toast.makeText(MainActivity.this,"complete_logistics",Toast.LENGTH_LONG).show();
                break;
            case R.id.translate:
                Intent intent_to_translate = new Intent(MainActivity.this,TranslateActivity.class);
                startActivity(intent_to_translate);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
        }
    }
}