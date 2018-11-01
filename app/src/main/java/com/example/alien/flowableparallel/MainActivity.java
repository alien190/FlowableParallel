package com.example.alien.flowableparallel;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FlowableParallelResults";
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startParallel();
    }

    @SuppressLint("CheckResult")
    private void startParallel() {
        Flowable<Integer> ids = Flowable.fromCallable(this::getId)
                .repeat()
                .take(100)
                .map(this::slowLoadSeq)
                .parallel(4)
                .runOn(Schedulers.io())
                .map(this::slowLoadParallel)
                .sequential();


        ids.subscribe(this::showResults);
    }

    private Integer getId() {
        mCount++;
        return mCount;
        //return (int) (100 * Math.random());
    }

    private Integer slowLoadParallel(int id) {
        Log.d(TAG, "slowLoad in parallel: " + id);
        try {
            Thread.sleep(2000);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return id;
    }

    private Integer slowLoadSeq(int id) {
        Log.d(TAG, "slowLoad in seq: " + id);
        try {
            Thread.sleep(500);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return id;
    }

    private void showResults(int id) {
        Log.d(TAG, "showResults: " + id);
    }
}
