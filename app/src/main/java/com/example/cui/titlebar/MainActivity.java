package com.example.cui.titlebar;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            AssetManager assets = getAssets();
            in = assets.open("01.txt");
            out = new ByteArrayOutputStream();
            int len = -1;
            byte buffer[] = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"),
                    out.toByteArray());
            String filename = "02.txt";
            MultipartBody multipartBody = new MultipartBody.Builder("---")
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("p", "555")
                    .addFormDataPart("file", filename, body).build();
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url("http://192.168.58.66:8080/uploadfile/uploadServlet")
                    .post(multipartBody)
                    .build();
            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("TAG", "onResponse: onFailure");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.e("TAG", "onResponse: success");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
