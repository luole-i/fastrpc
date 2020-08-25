package cn.luolei.rpc.client;

import com.sun.deploy.net.HttpResponse;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) throws IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        long start = System.currentTimeMillis();
        for(int i = 1 ; i<=10000;i++){
            httpRequest();
            if(i==100){
                System.out.println(System.currentTimeMillis()-start);
            }
            if(i==500){
                System.out.println(System.currentTimeMillis()-start);
            }
            if(i==1000){
                System.out.println(System.currentTimeMillis()-start);
            }
            if(i==2000){
                System.out.println(System.currentTimeMillis()-start);
            }
            if(i==5000){
                System.out.println(System.currentTimeMillis()-start);
            }
            if(i==10000){
                System.out.println(System.currentTimeMillis()-start);
            }
        }
    }
    public static void httpRequest(){
        String url = "http://127.0.0.1:8080/test";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Response response= call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
