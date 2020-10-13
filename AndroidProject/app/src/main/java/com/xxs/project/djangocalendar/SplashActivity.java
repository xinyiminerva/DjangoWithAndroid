package com.xxs.project.djangocalendar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.wangyuwei.particleview.ParticleView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splash_text_view)
    ParticleView splashTextView;
//    private ProgressDialog mProcessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        splashTextView.startAnim();
        splashTextView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
//        showProcessDialog();
//        OkHttpClient client = new OkHttpClient();
//        final Request request = new Request.Builder()
//                .url("http://192.168.10.103:8000/mobileCalendar/getAllEvent")
//                .get()
//                .build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                hideProcessDialog();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String res = response.body().string();
//                hideProcessDialog();
//                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                intent.putExtra("data", res);
//                startActivity(intent);
//                finish();
//            }
//        });
    }


//    private void showProcessDialog() {
//        if (mProcessDialog == null) {
//            mProcessDialog = new ProgressDialog(this);
//            mProcessDialog.setMessage("Loading……");
//            mProcessDialog.setCancelable(false);
//        }
//        mProcessDialog.show();
//    }
//
//    private void hideProcessDialog() {
//        if (mProcessDialog == null) {
//            return;
//        }
//        if (mProcessDialog.isShowing()) {
//            mProcessDialog.dismiss();
//        }
//    }
}
