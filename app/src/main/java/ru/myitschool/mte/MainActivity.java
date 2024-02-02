package ru.myitschool.mte;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Timer;

import ru.myitschool.mte.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button btnStart, btnStop;

    FragmentManager fm;
    FragmentTransaction ft;
    FirstFragment ff;
    ProceedingFragment pf;
    ChangeFragmentThread changeFragmentThread;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnStart = binding.content.startBtn;
        btnStop = binding.content.stopBtn;

        fm = getSupportFragmentManager();
        ff = new FirstFragment();
        pf = new ProceedingFragment();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String strMessage = msg.obj.toString();
                ft = fm.beginTransaction();
                //Log.d("Message", strMessage);
                if(strMessage.equals("true")){
                    ft.replace(R.id.output_fragment, ff);
                    ft.commit();
                }
                else {
                    ft.replace(R.id.output_fragment, pf);
                    ft.commit();
                }
                }
        };

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragmentThread = new ChangeFragmentThread(handler);
                changeFragmentThread.setRunning(true);
                changeFragmentThread.start();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean retry = true;
                changeFragmentThread.setRunning(false);
                while (retry) {
                    try {
                        changeFragmentThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
class ChangeFragmentThread extends Thread{
    Handler handler;
    boolean isFirstFragment = true;
    boolean isRunning = false;
    public ChangeFragmentThread(Handler handler){
         this.handler = handler;
    }
    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        while (isRunning){
            Message msg = new Message();
            msg.obj = Boolean.toString(isFirstFragment);
            handler.sendMessage(msg);
            isFirstFragment = !isFirstFragment;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}