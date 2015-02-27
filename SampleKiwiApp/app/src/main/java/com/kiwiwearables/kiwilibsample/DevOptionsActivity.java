package com.kiwiwearables.kiwilibsample;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import com.google.android.gms.wearable.Wearable;
import com.kiwiwearables.kiwilib.DetectionCallback;
import com.kiwiwearables.kiwilib.DetectionInfo;
import com.kiwiwearables.kiwilib.Kiwi;
import com.kiwiwearables.kiwilib.LoggingOptions;
import com.kiwiwearables.kiwilib.Motion;
import com.kiwiwearables.kiwilib.RefreshMotionCallback;
import com.kiwiwearables.kiwilib.SensorUnits;

/**
 * Created by afzal on 15-02-21.
 */
public class DevOptionsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DevOptionsFragment())
                    .commit();
        }
    }

    public static class DevOptionsFragment extends Fragment {

        private static final String TAG = DevOptionsFragment.class.getSimpleName();

        private Kiwi mKiwi;
        private GoogleApiClient mClient;
        private Node mWearNode;
        private boolean started;
        private List<Motion> mMotions;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_devoptions, container, false);

            setHasOptionsMenu(true);
            setupKiwi();

            setupUi(rootView);
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_main, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_refresh:
                    mKiwi.refreshMotions(new RefreshMotionCallback() {
                        @Override
                        public void onMotionRefresh(List<Motion> motions) {
                            mMotions = motions;
                            enableFirstMotion();
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }
                    });
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        private void setupKiwi() {
            mKiwi = Kiwi.with(getActivity());

            // fetch list of motions from the web
            mMotions = mKiwi.getMotions();
            enableFirstMotion();
            mKiwi.setCallback(new DetectionCallback() {
                @Override
                public void onMotionDetected(DetectionInfo detectionInfo) {
                    Log.d(TAG, detectionInfo.motion.motionName + " at " + detectionInfo.score);
                }
            });

            mKiwi.setWebSocketOption(LoggingOptions.LOG_ENABLED);
            mKiwi.setSensorUnits(SensorUnits.MS2_AND_RPS);

            mClient = new Builder(getActivity()).addApi(Wearable.API).build();
            mClient.connect();
        }

        // if there are any motions present, enable the first one
        private void enableFirstMotion() {
            if (mMotions.size() > 0) {
                List<String> enabledMotions = new ArrayList<>();
                // enable the first motion in the motion list
                enabledMotions.add(mMotions.get(0).motionId);
                mKiwi.setEnabledMotions(enabledMotions);
            }
        }

        /**
         * Clicking the start button sends a message to the wear app
         * to send its sensor data to WearListenerService on the phone app.
         *
         * There, that data is sent to the library for motion recognition
         */
        private void setupUi(View rootView) {
            final Button startButton = (Button) rootView.findViewById(R.id.start);

            startButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Wearable.NodeApi.getConnectedNodes(mClient).setResultCallback(new ResultCallback<GetConnectedNodesResult>() {
                        @Override
                        public void onResult(GetConnectedNodesResult getConnectedNodesResult) {
                            if (getConnectedNodesResult.getNodes().size() > 0) {
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
                        }
                    });
                }
            });
        }
    }
}
