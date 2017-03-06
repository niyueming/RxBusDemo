package net.nym.rxbusdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import net.nym.rxbusdemo.event.StartWorkEvent;
import net.nym.rxbusdemo.event.StopWorkEvent;
import net.nym.rxbuslibrary.RxBus;

import java.io.IOException;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

        WorkerThread sWorkerThread = new WorkerThread();
        sWorkerThread.start();
        System.out.println("Press the enter key to start and stop work!");

        int i = 0 ;
        for (; ; ) {
            System.out.println(i++);
            if (!sWorkerThread.isRunning()) {
                RxBus.getInstance().post(new StartWorkEvent());
            } else {
                RxBus.getInstance().post(new StopWorkEvent());
            }
        }
    }


    public static class WorkerThread extends Thread {

        private Disposable mStartWorkSubscription;
        private Disposable mStopWorkSubscription;
        private boolean mRunning;

        @Override
        public void run() {
            mStartWorkSubscription = RxBus.getInstance().register(StartWorkEvent.class, new Consumer<StartWorkEvent>() {
                @Override
                public void accept(StartWorkEvent startWorkEvent) throws Exception {
                    mRunning = true;
                    synchronized (this) {
                        notify();
                    }
                    System.out.println("startWorkEvent");
                }
            });
            mStopWorkSubscription = RxBus.getInstance().register(StopWorkEvent.class, new Consumer<StopWorkEvent>() {
                @Override
                public void accept(StopWorkEvent stopWorkEvent) throws Exception {
                    mRunning = false;

                    System.out.println("stopWorkEvent");
                }
            });

            for (; ; ) {
                if (Thread.interrupted()) {
                    cleanUp();
                    return;
                }

                if (!mRunning) {
                    System.out.println("Stopping work");

                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                        cleanUp();
                        return;
                    }

                    System.out.println("Starting work");
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    cleanUp();
                    return;
                }

                doWork();
            }
        }

        private void doWork() {
            System.out.print(".");
        }

        private void cleanUp() {
            mStartWorkSubscription.dispose();
            mStopWorkSubscription.dispose();
        }

        public boolean isRunning() {
            return mRunning;
        }
    }
}
