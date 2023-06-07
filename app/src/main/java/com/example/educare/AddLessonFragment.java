package com.example.educare;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;

public class AddLessonFragment extends Fragment {

    TextView Start;
    TextView End;

    Button Add;
    Button[] days = new Button[7];
    int day = 0;
    int startHour;
    int startMinute;
    int endHour;
    int endMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_lesson, container, false);

        days[0] = view.findViewById(R.id.FRBTNDay1);
        days[1] = view.findViewById(R.id.FRBTNDay2);
        days[2] = view.findViewById(R.id.FRBTNDay3);
        days[3] = view.findViewById(R.id.FRBTNDay4);
        days[4] = view.findViewById(R.id.FRBTNDay5);
        days[5] = view.findViewById(R.id.FRBTNDay6);
        days[6] = view.findViewById(R.id.FRBTNDay7);
        for (int i = 0; i<7; i++)
            days[i].setOnClickListener(dayOnClick);

        Start = view.findViewById(R.id.FRTVStart);
        End = view.findViewById(R.id.FRTVEnd);

        Start.setOnClickListener(timeOnClick);
        End.setOnClickListener(timeOnClick);

        Add = view.findViewById(R.id.FRBTNAddLesson);
        Add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day == 0){
                    Toast.makeText(getActivity(), "need to chose a day", Toast.LENGTH_SHORT).show();
                    return;
                }
                AddClassFragment.AddLesson(day,startHour,startMinute,endHour,endMinute);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                AddClassFragment fragment = new AddClassFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FContainerClassesT,fragment)
                        .addToBackStack(null).commit();
            }
        });

        return view;
    }

    OnClickListener timeOnClick = new OnClickListener() {
        int hour, minute;
        @Override
        public void onClick(View view) {
            TimePickerDialog.OnTimeSetListener  onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;
                    if (view == Start)
                        Start.setText("Start:  "+hour+":"+minute);
                    if (view == End)
                        End.setText("End:    "+hour+":"+minute);
                }
            };

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),onTimeSetListener,hour,minute,true);

            if (view == Start)
                timePickerDialog.setTitle("Start");
            if (view == End)
                timePickerDialog.setTitle("End");
            timePickerDialog.show();
        }
    };

    OnClickListener dayOnClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i = 0; i < 7; i++) {
                if (view == days[i]) {
                    day = i + 1;
                    days[i].setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green));
                } else {
                    days[i].setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.purple_500));
                }
            }
        }

    };
}