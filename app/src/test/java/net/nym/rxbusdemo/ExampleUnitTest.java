package net.nym.rxbusdemo;

import net.nym.rxbusdemo.event.StartWorkEvent;
import net.nym.rxbusdemo.event.StopWorkEvent;
import net.nym.rxbuslibrary.RxBus;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() throws IOException {
        MainActivity.WorkerThread sWorkerThread = new MainActivity.WorkerThread();
        sWorkerThread.start();
        System.out.println("Press the enter key to start and stop work!");

        for (; ; ) {
//            System.in.read();
            if (!sWorkerThread.isRunning()) {
                RxBus.getInstance().post(new StartWorkEvent());
            } else {
                RxBus.getInstance().post(new StopWorkEvent());
            }
        }
    }
}