package com.example.moviegoerpro.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * 全局唯一的视频缓存代理
 */
public class App extends Application {
    private static HttpProxyCacheServer proxyCacheServer;
    public static HttpProxyCacheServer getProxy(Context context){
//        App app=(App) context.getApplicationContext();
//        return app.proxyCacheServer==null?(app.proxyCacheServer=app.newProxy()):app.proxyCacheServer;
        return App.proxyCacheServer==null?(App.proxyCacheServer=new HttpProxyCacheServer(context.getApplicationContext())):App.proxyCacheServer;
    }
    private HttpProxyCacheServer newProxy(){
        return new HttpProxyCacheServer(this);
    }
}
