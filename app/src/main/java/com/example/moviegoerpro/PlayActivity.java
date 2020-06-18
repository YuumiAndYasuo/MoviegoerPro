package com.example.moviegoerpro;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.moviegoerpro.Base.PlayLink;
import com.example.moviegoerpro.Base.VideoDetail;
import com.example.moviegoerpro.Base.VideoInfo;
import com.example.moviegoerpro.Utils.HttpUtil;
import com.example.moviegoerpro.Utils.ParseHtml;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PlayActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;
    private myGestureDetector myGestureDetector;
    MediaController mediaController;
    boolean screen_vertical=true;
    private int curIndex;               //切出页面时的播放进度

    private ImageView coverimg;
    private VideoView videoView;
    private TextView videotype;
    private TextView videoname;
    private TextView videodirector;
    private TextView videoactors;
    private TextView videoarea;
    private TextView videolanguage;
    private TextView videointroduce;
    private TextView playnum;
/**
 * 香港卫视：http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8
 * CCTV1高清：http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8
 * CCTV3高清：http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8
 * CCTV5高清：http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8
 * CCTV5+高清：http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8
 * CCTV6高清：http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8
 *         String url="http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";
 *         String url="https://yushou.qitu-zuida.com/20181027/18788_57310fe4/index.m3u8";
 *         String url="https://douban.donghongzuida.com/share/8bf1211fd4b7b94528899de0a43b9fb3";
 *         String url="http://vip.zuiku8.com/1810/喜羊羊与灰太狼之虎虎生威.HD1280高清国语中字版.mp4";
 * 苹果提供的测试源（点播）：http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8
 */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent=getIntent();
        final String videoDetailUrl=intent.getStringExtra("url");

        HttpUtil.get(videoDetailUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayActivity.this,"加载视频失败，请刷新重试",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String html=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //获取视频详细信息
                        final VideoDetail videoDetail=ParseHtml.parsedetailinfo(html);
                        //获取各个控件
                        coverimg=(ImageView)findViewById(R.id.videocover);
                        videoView=findViewById(R.id.videoview);
                        videotype=findViewById(R.id.videotype);
                        videoname=findViewById(R.id.videoname);
                        videoactors=findViewById(R.id.videoactors);
                        videodirector=findViewById(R.id.videodirector);
                        videoarea=findViewById(R.id.videoarea);
                        videolanguage=findViewById(R.id.videolanguage);
                        videointroduce=findViewById(R.id.videointroduce);
                        playnum=findViewById(R.id.playnum);

                        //设置播放列表
                        final GridLayout gridLayout=(GridLayout)findViewById(R.id.gridlayout);
                        GridLayout.LayoutParams params = null;
                        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                        final ArrayList<PlayLink> tmp=videoDetail.getPlaylists_mp4();
                        int length=tmp.size();
                        int i,j,k=0;
                        for(i=0;i<length/4+1;i++){
                            for(j=0;j<4;j++){
                                if(k>=length){
                                    break;
                                }
                                final Button button=new Button(PlayActivity.this);
                                button.setText(tmp.get(k).getVideonum());
                                button.setWidth((screenWidth-60)/4);
                                if(k==0){
                                    button.setBackgroundColor(Color.parseColor("#fb7299"));
                                }
//                                button.
                                //设置行
                                GridLayout.Spec rowSpec=GridLayout.spec(i);
                                //设置列
                                GridLayout.Spec columnSpec=GridLayout.spec(j);
                                params=new GridLayout.LayoutParams(rowSpec,columnSpec);
                                params.setMargins(5, 5, 5, 5);
                                final int index=k;

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("**********************", "onClick: "+tmp.get(index).getVideourl());
                                        HttpProxyCacheServer proxyCacheServer= com.example.moviegoerpro.Utils.App.getProxy(getApplicationContext());
                                        String proxyUrl=proxyCacheServer.getProxyUrl(tmp.get(index).getVideourl());
                                        videoView.setVideoPath(proxyUrl);
                                        for (int i = 0; i < gridLayout.getChildCount(); i++) {//清空所有背景色
                                            Button b = (Button) gridLayout.getChildAt(i);
                                            b.setBackgroundColor(Color.parseColor("#d6d7d7"));
                                        }
                                        //设置选中的按钮背景色
                                        button.setBackgroundColor(Color.parseColor("#fb7299"));
                                        playnum.setText(tmp.get(index).getVideonum());
                                        Toast.makeText(PlayActivity.this,"正在加载"+tmp.get(index).getVideonum(),Toast.LENGTH_SHORT).show();
                                        videoView.requestFocus();
                                        videoView.start();
                                    }
                                });
                                gridLayout.addView(button,params);
                                k++;
                            }
                        }
                        //设置各控件内容
                        Glide.with(PlayActivity.this).load(videoDetail.getCoverimage()).into(coverimg);
                        videoView.setClickable(true);
                        //使用代理缓存视频，提高播放效果
                        HttpProxyCacheServer proxyCacheServer= com.example.moviegoerpro.Utils.App.getProxy(getApplicationContext());
                        String proxyUrl=proxyCacheServer.getProxyUrl(videoDetail.getPlaylists_mp4().get(0).getVideourl());
                        videoView.setVideoPath(proxyUrl);
                        videotype.setText(videoDetail.getVideotype());
                        videoname.setText(videoDetail.getVideoname());
                        videodirector.setText(videoDetail.getDirector());
                        videoactors.setText(videoDetail.getActors());
                        videoarea.setText(videoDetail.getArea());
                        videolanguage.setText(videoDetail.getLanguage());
                        videointroduce.setText("\u3000\u3000" +videoDetail.getIntroduce());


                        // 创建MediaController对象
                        mediaController = new MediaController(PlayActivity.this);
                        //VideoView与MediaController建立关联
                        videoView.setMediaController(mediaController);
                        videoView.requestFocus();
//                        videoView.seekTo(300);
                        videoView.pause();
                    }
                });


            }
        });

        myGestureDetector=new myGestureDetector();
        gestureDetector=new GestureDetector(getApplicationContext(),myGestureDetector);

        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaController != null && videoView.isPlaying())
        {
            curIndex = videoView.getCurrentPosition();
            videoView.pause();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(videoView!=null){
            videoView.seekTo(curIndex);
            videoView.requestFocus();
            videoView.start();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //正常播放
    private void convertToPortScreen() {
        screen_vertical=true;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置videoView竖屏播放
        VideoView videoView=findViewById(R.id.videoview);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip2px(PlayActivity.this, 198f));
        params.setMargins(dip2px(PlayActivity.this,0),dip2px(PlayActivity.this,0),
                dip2px(PlayActivity.this,0),dip2px(PlayActivity.this,0));
        //params.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoView.setLayoutParams(params);
    }

    //全屏播放
    private void convertToLandScreen() {
        screen_vertical=false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置videoView全屏播放
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置videoView横屏播放
        VideoView videoView=findViewById(R.id.videoview);
        //设置全屏
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        videoView.setLayoutParams(layoutParams);
        videoView.requestFocus();
    }

    //监听返回键,全屏模式下退出全屏，否则关闭这个activity
    @Override
    public void onBackPressed() {
        if(!screen_vertical){
            convertToPortScreen();
        }else {
            this.finish();
        }
    }

    /**
     * 播放器的控制
     * 双击控制         播放/暂停
     * 长按控制         全屏/正常
     * 左划右划         快退/快进
     * 左边上下         亮度调整
     * 右边上下         音量调整
     */
    public class myGestureDetector extends GestureDetector.SimpleOnGestureListener {
        AudioManager audioManager=(AudioManager)getSystemService(Service.AUDIO_SERVICE);
        float FLIP_DISTANCE = 50;               //高于此数值的滑动才触发滑动事件
        VideoView videoView =findViewById(R.id.videoview);

        public myGestureDetector() {
            super();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Toast.makeText(BaseApplication.getContext(),"长按",Toast.LENGTH_SHORT).show();
            if(screen_vertical){//切换到全屏播放
                convertToLandScreen();
            }else {//退出全屏播放
                convertToPortScreen();
            }

            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > FLIP_DISTANCE) {//快退
                Toast.makeText(BaseApplication.getContext(),"快退"+(swipe_time(e1.getX(),e2.getX())/1000)+"秒",Toast.LENGTH_SHORT).show();
                int seektime=videoView.getCurrentPosition()-swipe_time(e1.getX(),e2.getX());
                if(seektime<0){
                    seektime=0;
                }
                videoView.seekTo(seektime);
                return true;
            }
            if (e2.getX() - e1.getX() > FLIP_DISTANCE) {//快进
                Toast.makeText(BaseApplication.getContext(),"快进"+(swipe_time(e2.getX(),e1.getX())/1000)+"秒",Toast.LENGTH_SHORT).show();
                int seektime=videoView.getCurrentPosition()+swipe_time(e2.getX(),e1.getX());
                if(seektime>videoView.getDuration()){
                    seektime=videoView.getDuration();
                }
                videoView.seekTo(seektime);
                return true;
            }
            if (e1.getY() - e2.getY() > FLIP_DISTANCE) {//增加音量或亮度
                int width=getScreenWidth(BaseApplication.getContext());
                int height=getScreenHeight(BaseApplication.getContext());

                int currentvolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int changedvolume=currentvolume+(int) ((e1.getY()-e2.getY())/150);
                if(e1.getX()<width/2.0){    //左半屏   更改亮度
                    float brightness=(e1.getY()-e2.getY())/height;
                    if(brightness>1.0){
                        brightness=1.0f;
                    }else if(brightness<0.15){
                        brightness=0.15f;
                    }
                    Toast.makeText(BaseApplication.getContext(),"当前亮度："+(int)(brightness*100)+"%",Toast.LENGTH_SHORT).show();
                    Window window=getWindow();
                    WindowManager.LayoutParams layoutParams=window.getAttributes();
                    layoutParams.screenBrightness=brightness;
                    window.setAttributes(layoutParams);
                }else{                      //右半屏   增加音量
                    if(changedvolume>audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)){
                        changedvolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    }
                    Toast.makeText(BaseApplication.getContext(),"当前音量："+changedvolume,Toast.LENGTH_SHORT).show();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,changedvolume,AudioManager.FLAG_PLAY_SOUND);
                }
                return true;
            }
            if (e2.getY() - e1.getY() > FLIP_DISTANCE) {//减小音量或亮度
                int width=getScreenWidth(BaseApplication.getContext());
                int height=getScreenHeight(BaseApplication.getContext());

                int currentvolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int changedvolume=currentvolume-(int) ((e2.getY()-e1.getY())/150);
                if(e2.getX()<width/2.0){    //左半屏   更改亮度
                    float brightness=(e2.getY()-e1.getY())/height;
                    if(brightness>1.0){
                        brightness=1.0f;
                    }else if(brightness<0.15){
                        brightness=0.05f;
                    }
                    Toast.makeText(BaseApplication.getContext(),"当前亮度："+(int)(brightness*100)+"%",Toast.LENGTH_SHORT).show();
                    Window window=getWindow();
                    WindowManager.LayoutParams layoutParams=window.getAttributes();
                    layoutParams.screenBrightness=brightness;
                    window.setAttributes(layoutParams);
                }else{                      //右半屏   减少音量
                    if(changedvolume<0){
                        changedvolume=0;
                    }
                    Toast.makeText(BaseApplication.getContext(),"当前音量："+changedvolume,Toast.LENGTH_SHORT).show();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,changedvolume,AudioManager.FLAG_PLAY_SOUND);
                }
                return true;
            }

            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            Toast.makeText(BaseApplication.getContext(),"双击",Toast.LENGTH_SHORT).show();
            if(videoView.isPlaying()){
                Toast.makeText(BaseApplication.getContext(),"暂停播放",Toast.LENGTH_SHORT).show();
                videoView.pause();
            }else {
                Toast.makeText(BaseApplication.getContext(),"继续播放",Toast.LENGTH_SHORT).show();
                videoView.start();
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            return super.onContextClick(e);
        }

        //返回快进或快退多少毫米
        public int swipe_time(double x1,double x2){
            return (int) ((x1-x2)*100);
        }
        //获取屏幕宽度
        int getScreenWidth(Context context){
            WindowManager windowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics=new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }

        //获取屏幕高度
        int getScreenHeight(Context context){
            WindowManager windowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics=new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

}


