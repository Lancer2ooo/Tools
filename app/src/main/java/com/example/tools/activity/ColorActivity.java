package com.example.tools.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tools.R;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * author : Lancer
 * e-mail : lancer2ooo@qq.com
 * version: 1.0
 * 颜色转换
 */

public class ColorActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout beforeLay;//转换之后的布局
    private LinearLayout resultLay;//转换结果布局
    private ImageView ivClearTx;//清空输入框按钮
    private ImageView ivCopyTx;//复制转换的结果
    private TextView color;//转换
    EditText ed_color_content;//输入的颜色
    TextView tv_color_result;//转换的结果
    private NiceSpinner spColorType;//输入类型选择下拉框

    private ClipboardManager myClipboard;//复制文本
    private ClipData myClip; //剪辑数据

    //配置初始数据
    private List<String> data = new LinkedList<>(Arrays.asList(
            "十六进制 → RGB","RGB → 十六进制"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        initView();
    }
    private void initView(){
        beforeLay = findViewById(R.id.color_before_lay);
        resultLay = findViewById(R.id.color_result_lay);
        ivClearTx = findViewById(R.id.iv_color_clear_tx);
        ivCopyTx  =findViewById(R.id.iv_color_copy_tx);
        color = findViewById(R.id.tv_color);
        ed_color_content = findViewById(R.id.ed_color_content);
        tv_color_result = findViewById(R.id.tv_color_result);
        spColorType = findViewById(R.id.sp_colorType);

        //点击事件
        ivClearTx.setOnClickListener(this);
        ivCopyTx.setOnClickListener(this);
        color.setOnClickListener(this);

        editTextListener();

        //获取系统粘贴板服务
        myClipboard = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);

        spColorType.attachDataSource(data);
        spinnerListener();//下拉框选择监听
    }

    //后期添加RGB to 十六进制时需要用到
    private void spinnerListener() {
        spColorType.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                switch (position){
                    case 0://RGB to hex
                        break;
                    case 1://hex toRGB
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_color_clear_tx://清空输入框
                ed_color_content.setText("");//清除文本
                ivClearTx.setVisibility(View.GONE);//清除数据之后隐藏按钮
                break;
            case R.id.iv_color_copy_tx://复制翻译后的结果
                String inviteCode = tv_color_result.getText().toString();
                myClip = ClipData.newPlainText("text", inviteCode);
                myClipboard.setPrimaryClip(myClip);
                showMsg("复制成功");
                break;
            case R.id.tv_color://转换
                color();//转换
                break;
            default:
                break;
        }
    }

    /**
     * 输入监听
     */
    private void editTextListener(){
        ed_color_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ivClearTx.setVisibility(View.VISIBLE);//显示清除按钮
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClearTx.setVisibility(View.VISIBLE);//显示清除按钮
            }

            @Override
            public void afterTextChanged(Editable s) {
                ivClearTx.setVisibility(View.VISIBLE);//显示清除按钮

                String content = ed_color_content.getText().toString().trim();
                if (content.isEmpty()) {//为空
                    resultLay.setVisibility(View.GONE);
                    color.setVisibility(View.VISIBLE);
                    beforeLay.setVisibility(View.GONE);
                    ivClearTx.setVisibility(View.GONE);
                }
            }
        });
    }

    //颜色转换逻辑
    private void color() {
        //获取输入的内容
        String inputTx = ed_color_content.getText().toString().trim();
        //判断输入内容是否为空
        if (!inputTx.isEmpty() || !"".equals(inputTx)) {//不为空
            color.setText("转换中...");
            color.setEnabled(false);//不可更改，同样就无法点击
            hex2RGB(inputTx);
        } else {//为空
            showMsg("请输入内容！");
        }
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //十六进制转换为RGB
    private int[] hex2RGB(String hexStr){
        if (!(ed_color_content.getText().toString().trim().length() ==7)){
            Toast.makeText(ColorActivity.this,"请输入带#号的七位十六进制数",Toast.LENGTH_LONG).show();
            color.setText("转换");
            color.setEnabled(true);
        }else {
            int[] rgb = new int[3];
            rgb[0] = Integer.valueOf(hexStr.substring( 1, 3 ), 16);
            rgb[1] = Integer.valueOf(hexStr.substring( 3, 5 ), 16);
            rgb[2] = Integer.valueOf(hexStr.substring( 5, 7 ), 16);
            String RGB = rgb[0] +","+ rgb[1] +","+ rgb[2];
//            Toast.makeText(ColorActivity.this,RGB,Toast.LENGTH_LONG).show();
            tv_color_result.setText(RGB);
            resultLay.setVisibility(View.VISIBLE);
            beforeLay.setVisibility(View.GONE);
            color.setText("转换");
            color.setEnabled(true);
            return rgb;
        }
        return null;
    }

    //RGB转换为十六进制
    public String rgb2Hex(int r,int g,int b){
        return String.format("#%02X%02X%02X", r,g,b);
    }
}