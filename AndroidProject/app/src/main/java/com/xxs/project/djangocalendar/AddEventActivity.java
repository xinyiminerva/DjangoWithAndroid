package com.xxs.project.djangocalendar;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AddEventActivity extends AppCompatActivity {


    @BindView(R.id.other_fab_circle)
    FloatingActionButton mFabCircle;
    @BindView(R.id.edit_title)
    EditText editTitle;
    @BindView(R.id.edit_content)
    EditText editContent;
    @BindView(R.id.edit_exp_date)
    EditText editExpDate;
    @BindView(R.id.edit_due_date)
    EditText editDueDate;
    @BindView(R.id.add_event_btn)
    CircularProgressButton addEventBtn;
    @BindView(R.id.other_tv_container)
    LinearLayout mTvContainer;
    @BindView(R.id.other_rl_container)
    RelativeLayout mRlContainer;
    SelectedDate mSelectedExpDate;
    SelectedDate mSelectedDueDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        AndroidBug5497Workaround.assistActivity(this);
        ButterKnife.bind(this);
        editExpDate.setShowSoftInputOnFocus(false);
        editDueDate.setShowSoftInputOnFocus(false);
        initAnimation();
//        disableShowSoftInput();
//        setupWindowAnimations();
    }

    private void initAnimation() {
        setupEnterAnimation();
        setupExitAnimation();
    }

    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.arc_motion);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        getWindow().setSharedElementEnterTransition(transition);
    }

    private void animateRevealShow() {
        GuiUtils.animateRevealShow(
                this, mRlContainer,
                mFabCircle.getWidth() / 2, R.color.colorAccent,
                new GuiUtils.OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    private void initViews() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(300);
            mTvContainer.setVisibility(View.VISIBLE);
            mTvContainer.startAnimation(animation);
            setTitle("Add Event");
        });
    }

    private void setupExitAnimation() {
        Fade fade = new Fade();
        fade.setDuration(300);
        getWindow().setReturnTransition(fade);
    }

    @Override
    public void onBackPressed() {
        GuiUtils.animateRevealHide(
                this, mRlContainer,
                mFabCircle.getWidth() / 2, R.color.colorAccent,
                new GuiUtils.OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                        defaultBackPressed();
                    }

                    @Override
                    public void onRevealShow() {

                    }
                });
    }

    private void defaultBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        addEventBtn.dispose();
    }

    private void addNewEvent() {
        if (TextUtils.isEmpty(editTitle.getText().toString())) {
            Toast.makeText(this, "Please fill event title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editContent.getText().toString())) {
            Toast.makeText(this, "Please fill event content", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editExpDate.getText().toString())) {
            Toast.makeText(this, "Please fill event expected date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editDueDate.getText().toString())) {
            Toast.makeText(this, "Please fill event due date", Toast.LENGTH_SHORT).show();
            return;
        }
        addEventBtn.startAnimation();
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("event_title", editTitle.getText().toString())
                .add("event_category", "Test")
                .add("event_text", editContent.getText().toString())
                .add("due_date", editDueDate.getText().toString())
                .add("expected_date", editExpDate.getText().toString())
                .add("is_finished", "false")
                .build();
        final Request request = new Request.Builder()
                .url(String.format("%s8000/mobileCalendar/addNewEvent", getString(R.string.local_host)))
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                addEventBtn.stopAnimation();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                if (res.equals("Create new event success")) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long time1 = TimeUtils.string2Millis(editExpDate.getText().toString(), format);
                    long time2 = TimeUtils.string2Millis(editDueDate.getText().toString(), format);
                    CalendarReminderUtils.addCalendarEvent(AddEventActivity.this, editTitle.getText().toString(), editContent.getText().toString(),time1, time2, 1);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addEventBtn.revertAnimation();
                    }
                }, 1500);

            }
        });
    }

    @OnClick(R.id.add_event_btn)
    public void onViewClicked() {
        addNewEvent();
    }

    @OnClick({R.id.edit_exp_date, R.id.edit_due_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_exp_date: {
                SublimePickerFragment pickerFrag = new SublimePickerFragment();
                pickerFrag.setCallback(new SublimePickerFragment.Callback() {
                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {
                        mSelectedExpDate = selectedDate;
                        updateInfoView(true, hourOfDay, minute);
                    }
                });
                SublimeOptions options = new SublimeOptions();
                int displayOptions = 0;
                displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
                displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
                options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);
                options.setDisplayOptions(displayOptions);
                options.setCanPickDateRange(false);
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", options);
                pickerFrag.setArguments(bundle);

                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getSupportFragmentManager(), "SUBLIME_PICKER");
                break;
            }
            case R.id.edit_due_date: {
                SublimePickerFragment pickerFrag = new SublimePickerFragment();
                pickerFrag.setCallback(new SublimePickerFragment.Callback() {
                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {
                        mSelectedDueDate = selectedDate;
                        updateInfoView(false, hourOfDay, minute);
                    }
                });
                SublimeOptions options = new SublimeOptions();
                int displayOptions = 0;
                displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
                displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
                options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);
                options.setDisplayOptions(displayOptions);
                options.setCanPickDateRange(false);
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", options);
                pickerFrag.setArguments(bundle);
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getSupportFragmentManager(), "SUBLIME_PICKER");
                break;
            }
        }
    }

    private void updateInfoView(boolean isExp, int hour, int minute) {
        String hourStr;
        String minuteStr;
        if (hour>=10) {
            hourStr = String.format("%d", hour);
        } else {
            hourStr = String.format("0%d", hour);
        }
        if (minute>=10) {
            minuteStr = String.format("%d", minute);
        } else {
            minuteStr = String.format("0%d", minute);
        }
        if (mSelectedExpDate != null && isExp) {
            editExpDate.setText(applyBoldStyle("")
                    .append(String.valueOf(mSelectedExpDate.getStartDate().get(Calendar.YEAR)))
                    .append("-")
                    .append(mSelectedExpDate.getStartDate().get(Calendar.MONTH) < 9 ? "0" + String.valueOf(mSelectedExpDate.getStartDate().get(Calendar.MONTH)  + 1):String.valueOf(mSelectedExpDate.getStartDate().get(Calendar.MONTH)  + 1))
                    .append("-")
                    .append(mSelectedExpDate.getStartDate().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(mSelectedExpDate.getStartDate().get(Calendar.DAY_OF_MONTH)):String.valueOf(mSelectedExpDate.getStartDate().get(Calendar.DAY_OF_MONTH)))
                    .append(String.format(" %s:%s:00", hourStr, minuteStr))
            );
        }
        if (mSelectedDueDate != null && !isExp) {
            editDueDate.setText(applyBoldStyle("")
                    .append(String.valueOf(mSelectedDueDate.getStartDate().get(Calendar.YEAR)))
                    .append("-")
                    .append(mSelectedDueDate.getStartDate().get(Calendar.MONTH) < 9 ? "0" + String.valueOf(mSelectedDueDate.getStartDate().get(Calendar.MONTH)  + 1):String.valueOf(mSelectedDueDate.getStartDate().get(Calendar.MONTH)  + 1))
                    .append("-")
                    .append(mSelectedDueDate.getStartDate().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + String.valueOf(mSelectedDueDate.getStartDate().get(Calendar.DAY_OF_MONTH)):String.valueOf(mSelectedDueDate.getStartDate().get(Calendar.DAY_OF_MONTH)))
                    .append(String.format(" %s:%s:00", hourStr, minuteStr))
            );
        }
    }

    private SpannableStringBuilder applyBoldStyle(String text) {
        SpannableStringBuilder ss = new SpannableStringBuilder(text);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
}