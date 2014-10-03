package kiwihackathon.com.samplekiwiapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.kiwiwearables.app.models.SensorReading;
import com.kiwiwearables.app.services.IKiwiBinder;


public class MyActivity extends Activity {
    private static final String TAG = MyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String TAG = PlaceholderFragment.class.getSimpleName();

        IKiwiBinder mKiwiService;

        private boolean mBound;

        private ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "service connected");
                mKiwiService = IKiwiBinder.Stub.asInterface(service);
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "service disconnected");
                mBound = false;
            }
        };

        @Override
        public void onDestroy() {
            if (mBound) {
                getActivity().unbindService(mConnection);
                mBound = false;
            }
            super.onDestroy();
        }


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            Button sendDataButton = (Button) rootView.findViewById(R.id.send_data_button);
            Button fetchMotionsButton = (Button) rootView.findViewById(R.id.fetch_motions_button);
            Button changeMotionButton = (Button) rootView.findViewById(R.id.change_motion_button);

            sendDataButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBound) {
                        SensorReading reading = new SensorReading(new float[] {5.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, "test", "12afzaltest");
                        try {
                            mKiwiService.sendData(reading);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Not bound to service", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            final List<Motion> motions = new ArrayList<Motion>();

            fetchMotionsButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor c = getActivity().getContentResolver().query(Uri.parse("content://com.kiwiwearables.app.provider/motions"), null, null, null, null);
                    if (c != null) {
                        while (c.moveToNext()) {
                            Motion motion = Motion.fromCursor(c);
                            motions.add(motion);
                        }

                        c.close();
                    }

                    for (Motion motion : motions) {
                        Log.d(TAG, "" + motion.getAccWeight());
                    }
                }
            });

            changeMotionButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (motions.size() > 0) {
                        Motion motion = motions.get(0);
                        motion.setAccWeight(11.0f);
                        motion.sendMotion(getActivity());
                    }
                }
            });

            Intent intent = new Intent(IKiwiBinder.class.getName());
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            return rootView;
        }
    }
}
