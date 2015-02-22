package com.kiwiwearables.kiwilibsample;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import com.google.android.gms.wearable.Wearable;
import com.kiwiwearables.kiwilib.Kiwi;
import com.kiwiwearables.kiwilib.KiwiCallback;
import com.kiwiwearables.kiwilib.LoggingOptions;
import com.kiwiwearables.kiwilib.SensorUnits;


public class DataLoggerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DataCollectFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public static class DataCollectFragment extends Fragment {

        private Node mWearNode;
        private GoogleApiClient mClient;
        private boolean started = false;
        private Kiwi mKiwi;

        public DataCollectFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            setupKiwi();

            setupUi(rootView);
            return rootView;
        }

        private void setupUi(final View rootView) {
            final EditText motionName = (EditText) rootView.findViewById(R.id.exercise);
            final EditText age = (EditText) rootView.findViewById(R.id.age);
            final EditText height = (EditText) rootView.findViewById(R.id.height);
            final EditText skill = (EditText) rootView.findViewById(R.id.skill);
            final RadioGroup handednessRadio = (RadioGroup) rootView.findViewById(R.id.handedness);
            final EditText speed = (EditText) rootView.findViewById(R.id.speed);
            final EditText length = (EditText) rootView.findViewById(R.id.length);

            final Button startButton = (Button) rootView.findViewById(R.id.start);

            startButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Wearable.NodeApi.getConnectedNodes(mClient).setResultCallback(new ResultCallback<GetConnectedNodesResult>() {
                        @Override
                        public void onResult(GetConnectedNodesResult getConnectedNodesResult) {
                            mWearNode = getConnectedNodesResult.getNodes().get(0);

                            if (!started) {
                                Wearable.MessageApi.sendMessage(mClient, mWearNode.getId(), "START_SENSOR_REQUEST", new byte[0]).setResultCallback(new ResultCallback<SendMessageResult>() {
                                    @Override
                                    public void onResult(SendMessageResult sendMessageResult) {
                                        if (sendMessageResult.getStatus().isSuccess()) {
                                            started = true;
                                            startButton.setText("Stop");
                                        }
                                    }
                                });
                            } else {
                                Wearable.MessageApi.sendMessage(mClient, mWearNode.getId(), "STOP_SENSOR_REQUEST", new byte[0]).setResultCallback(new ResultCallback<SendMessageResult>() {
                                    @Override
                                    public void onResult(SendMessageResult sendMessageResult) {
                                        if (sendMessageResult.getStatus().isSuccess()) {
                                            started = false;
                                            startButton.setText("Start");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });

            Button submitDataButton = (Button) rootView.findViewById(R.id.submit_data);
            submitDataButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (motionName.getText().toString().isEmpty()) {
                        motionName.setError("Exercise name cannot be empty for data capture");
                        return;
                    }
                    if (age.getText().toString().isEmpty()) {
                        age.setError("Age cannot be empty for data capture");
                        return;
                    }
                    if (height.getText().toString().isEmpty()) {
                        height.setError("Height cannot be empty for data capture");
                        return;
                    }
                    if (skill.getText().toString().isEmpty()) {
                        skill.setError("Skill level cannot be empty for data capture");
                        return;
                    }

                    String handedness = ((RadioButton) rootView.findViewById(handednessRadio.getCheckedRadioButtonId())).getText().toString();

                    HashMap<String, String> extras = new HashMap<>();
                    extras.put("ball_speed", speed.getText().toString());
                    extras.put("ball_length", length.getText().toString());

                    mKiwi.stopDataCapture(motionName.getText().toString(),
                                          age.getText().toString(),
                                          height.getText().toString(),
                                          skill.getText().toString(),
                                          handedness,
                                          extras);
                }
            });
        }

        private void setupKiwi() {
            mKiwi = Kiwi.with(getActivity());
            mKiwi.initUser("testing@kiwiwearables.com", "testing123", new KiwiCallback() {
                @Override
                public void onUserInit() {
                    mKiwi.setDataCaptureEnabled(true);
                    mKiwi.setWebSocketOption(LoggingOptions.LOG_ONLY);
                    mKiwi.setSensorUnits(SensorUnits.MS2_AND_RPS);
                }
            });

            mClient = new Builder(getActivity()).addApi(Wearable.API).build();
            mClient.connect();
        }
    }
}
