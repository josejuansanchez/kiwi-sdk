package com.kiwiwearables.kiwilibsample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kiwiwearables.kiwilib.Kiwi;
import com.kiwiwearables.kiwilib.KiwiCallback;

/**
 * Created by afzal on 15-02-21.
 */
public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

    public static class LoginFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);

            setupUi(rootView);
            return rootView;
        }

        private void setupUi(View rootView) {
            final EditText usernameEt = (EditText) rootView.findViewById(R.id.username);
            final EditText passwordEt = (EditText) rootView.findViewById(R.id.password);

            Button loginButton = (Button) rootView.findViewById(R.id.login);
            loginButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = usernameEt.getText().toString();
                    String password = passwordEt.getText().toString();
                    Kiwi.with(getActivity()).initUser(username, password, new KiwiCallback() {
                        @Override
                        public void onUserInit() {
                            startActivity(new Intent(getActivity(), DevOptionsActivity.class));
                            getActivity().finish();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Toast.makeText(getActivity(), "Error occured on login", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            Button signupButton = (Button) rootView.findViewById(R.id.signup);
            signupButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.kiwiwearables.com"));
                    startActivity(intent);
                }
            });
        }
    }
}
