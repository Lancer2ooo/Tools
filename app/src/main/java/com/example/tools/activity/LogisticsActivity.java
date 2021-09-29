package com.example.tools.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tools.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * author : Lancer
 * e-mail : lancer2ooo@qq.com
 * date   : 2021/9/28
 * desc   :
 * version: 1.0
 * 物流查询
 */

public class LogisticsActivity extends ListActivity{
    private Button query;
    private EditText postId;
    private ListView lv;
    private Spinner spinner;

    String from[]={"time","context"};
    int to[]={R.id.time,R.id.context};

    ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>();
    HashMap<String,String> map=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics);

        postId=(EditText)findViewById(R.id.postId);
        query=(Button)findViewById(R.id.query);
        lv=(ListView)findViewById(android.R.id.list);
        spinner=(Spinner)findViewById(R.id.spinner);

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    //发送请求
    private void sendRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient=new DefaultHttpClient();
                    //处理快递单号
                    String queryString=checkPostid();
                    HttpGet httpGet=new HttpGet(queryString);
                    HttpResponse httpResponse=httpClient.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        //这里表示如果请求和响应都成功了的话
                        HttpEntity entity=httpResponse.getEntity();
                        String response= EntityUtils.toString(entity,"utf-8");
                        Log.d("JSON response",response);
                        parseJSON(response);
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //解析JSON数据
    private void parseJSON(String jsonData){
        try{
            //我们需要解析数组data的数据，其中time和context就是需要的字段
            JSONArray jsonArray=new JSONObject(jsonData).getJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String time=jsonObject.getString("time");
                String context=jsonObject.getString("context");
                //Log.d("data","time is"+time);
                //Log.d("context","context is"+context);
                map= new HashMap<String,String>();
                map.put("time",time);
                map.put("context",context);
                list.add(map);
            }
            //在子线程里向ListView里添加数据
            new Thread(){
                public void run(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //每次查询之前把ListView里的数据清除掉-还没实现
                            SimpleAdapter simpleAdapter=new SimpleAdapter(LogisticsActivity.this,list,R.layout.logistics_item_list,from,to);
                            setListAdapter(simpleAdapter);
                            //如果对话框里还没有数据，提醒用户
                            if(lv.getCount()==0){
                                Toast.makeText(LogisticsActivity.this,"单号不能为空",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //选择快递公司
    private String checkPostid(){
        //基础链接
        String baseStr="http://www.kuaidi100.com/query?type=";
        String type="";
        String postidNumber=postId.getText().toString().trim();
        String kuaidiCompany=spinner.getSelectedItem().toString().trim();
        Log.d("post id:",postidNumber);
        Log.d("kuaidiCompany id:",kuaidiCompany);
        if(kuaidiCompany.equals("申通")){
            type = "shentong";
        }else if(kuaidiCompany.equals("EMS")){
            type = "ems";
        }else if(kuaidiCompany.equals("顺丰")){
            type = "shunfeng";
        }else if(kuaidiCompany.equals("圆通")){
            type = "yuantong";
        }else if(kuaidiCompany.equals("中通")){
            type = "zhongtong";
        }else if (kuaidiCompany.equals("韵达")){
            type = "yunda";
        }else if (kuaidiCompany.equals("天天")){
            type = "tiantian";
        }else if (kuaidiCompany.equals("汇通")){
            type = "huitongkuaidi";
        }else if (kuaidiCompany.equals("全峰")){
            type = "quanfengkuaidi";
        }else if (kuaidiCompany.equals("德邦")){
            type = "debangwuliu";
        }else if(kuaidiCompany.equals("宅急送")){
            type = "zhaijisong";
        }
        Log.d("url",baseStr + type + "&postid=" + postidNumber);
        return baseStr + type + "&postid=" + postidNumber;
    }
}