package com.xxs.project.djangocalendar;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ramotion.foldingcell.FoldingCell;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private static final int RC_CALENDAR_PERM = 124;
    @BindView(R.id.add_fab_btn)
    FloatingActionButton addFabBtn;
    @BindView(R.id.mainListView)
    ListView mainListView;
    private List<EventModal> mList = new ArrayList<>();
    private List<EventModal> undoList = new ArrayList<>();
    private ProgressDialog mProcessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        String[] perms = {Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "calendar permission rquest", RC_CALENDAR_PERM, perms);
        }
        getData();
    }

    private void getData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProcessDialog();
            }
        });
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(String.format("%s8000/mobileCalendar/getAllEvent", getString(R.string.local_host)))
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProcessDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideProcessDialog();
                final String res = response.body().string();
                Gson gson = new Gson();
                mList = gson.fromJson(res, new TypeToken<List<EventModal>>() {
                }.getType());
                for (EventModal modal : mList) {
                    if (!modal.getFields().isIs_finished()) {
                        undoList.add(modal);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final EventAdapter adapter = new EventAdapter(MainActivity.this, undoList);
                        mainListView.setAdapter(adapter);
                        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                                // toggle clicked cell state
                                ((FoldingCell) view).toggle(false);
                                // register in adapter that state for selected cell is toggled
                                adapter.registerToggle(pos);
                            }
                        });
                    }
                });
            }
        });
    }

    private String parseTimeStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


    private void createEventByIntent() {
        //Use Intent to insert calendar
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 11, 19, 7, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());
        calendar.set(2018, 11, 19, 10, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis());
        calendarIntent.putExtra(CalendarContract.Events.TITLE, "Test");
        calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Location");
        calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Test");
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, CalendarContract.EXTRA_EVENT_ALL_DAY);
        startActivity(calendarIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @OnClick(R.id.add_fab_btn)
    public void onViewClicked() {
        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation(this, addFabBtn, addFabBtn.getTransitionName());
        startActivity(new Intent(this, AddEventActivity.class), options.toBundle());
    }

    private void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = new ProgressDialog(this);
            mProcessDialog.setMessage("Loading……");
            mProcessDialog.setCancelable(false);
        }
        mProcessDialog.show();
    }

    private void hideProcessDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }
}
