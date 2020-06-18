package com.example.moviegoerpro;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviegoerpro.Adapter.VideoAdapter;
import com.example.moviegoerpro.Base.VideoInfo;
import com.example.moviegoerpro.Utils.*;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class indexActivity extends AppCompatActivity {
    EditText search_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        search_edit=(EditText) findViewById(R.id.search_edit);
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event!=null&& KeyEvent.KEYCODE_ENTER==event.getKeyCode()&&KeyEvent.ACTION_DOWN==event.getAction()){
                    search_video();
                    return true;
                }
                return false;
            }
        });

        final Button search_btn=findViewById(R.id.search_button);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_video();
            }
        });
        search_video();
    }

    private void search_video(){
        search_edit=(EditText) findViewById(R.id.search_edit);

        String keyword=search_edit.getText().toString();
        Toast.makeText(indexActivity.this,"搜索词："+keyword,Toast.LENGTH_SHORT).show();
        RequestBody requestBody=new FormBody.Builder()
                .add("m","vod-search")
                .add("wd",keyword)
                .add("submit","search")
                .build();
        HttpUtil.post("http://www.zuidazy5.com/index.php?m=vod-search", requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(indexActivity.this,"搜索失败，请重新搜索",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String html=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                                TextView test=(TextView) findViewById(R.id.test);
//                                test.setText(html);
                        final ArrayList<VideoInfo> videolist=ParseHtml.parsebaseinfo(html);
                        VideoAdapter videoAdapter=new VideoAdapter(indexActivity.this,R.layout.videolist,videolist);
                        ListView listView=(ListView)findViewById(R.id.videolist);
                        listView.setAdapter(videoAdapter);

                        //监听listview中的每一项
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                VideoInfo videoInfo = videolist.get(position);
                                Toast.makeText(indexActivity.this,videoInfo.getVideoname(),Toast.LENGTH_SHORT).show();
                                //从当前页面跳转到播放页面，并将选中视频信息传递过去
                                Intent intent=new Intent(indexActivity.this,PlayActivity.class);
                                intent.putExtra("url","http://www.zuidazy5.com"+videoInfo.getVideourl());
                                startActivity(intent);
                            }
                        });
//                                Toast.makeText(indexActivity.this,"成功"+html,Toast.LENGTH_LONG).show();
                    }
                });

//                        Log.d("tag", "onResponse: "+html);
            }
        });
    }
}
