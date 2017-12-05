package ie.dkit.rssnewsfeed;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyJobService extends JobService {

    Context context = this;
    NewsService myService = new NewsService(this);
    boolean isBound = false;


    private static final String TAG = "SyncService";

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(context, "JobService created", Toast.LENGTH_LONG).show();
        Log.i(TAG, "JobService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(context, "JobService destroyed", Toast.LENGTH_LONG).show();
        //Log.i(TAG, "JobService destroyed");
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        Toast.makeText(context, "job started", Toast.LENGTH_LONG).show();
        Log.i(TAG, "on start job: " + params.getJobId());
        // starts the News Service service
        Intent intent = new Intent(this,NewsService.class);
        startService(intent);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params)
    {
        Toast.makeText(context, "job stopped", Toast.LENGTH_LONG).show();
        if(isBound)
        {
            unbindService(myConnection);
            isBound = false;
        }
        return true;
    }

    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NewsService.MyLocalBinder binder = (NewsService.MyLocalBinder) service;
            myService = binder.getService();
            isBound = true;
            Toast.makeText(getApplicationContext(), "ServieConnected()", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "ServiceDisconnected()", Toast.LENGTH_SHORT).show();
            isBound = false;
        }
    };



}
