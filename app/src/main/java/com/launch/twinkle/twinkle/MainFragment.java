package com.launch.twinkle.twinkle;

import java.util.Arrays;
import java.util.List;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;


public class MainFragment extends Fragment {
  private static final String TAG = MainFragment.class.getSimpleName();

  private UiLifecycleHelper uiHelper;
  private TextView username;
  private LoginButton authButton;

  private final List<String> permissions;

  public MainFragment() {
    permissions = Arrays.asList("public_profile,email,user_friends,user_status");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    uiHelper = new UiLifecycleHelper(getActivity(), callback);
    uiHelper.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main2, container, false);

    authButton = (LoginButton) view.findViewById(R.id.authButton);
    authButton.setFragment(this);
    authButton.setReadPermissions(permissions);
      authButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
          @Override
          public void onUserInfoFetched(GraphUser user) {
              if (user != null) {
                  username.setText("You are currently logged in as " + user.getName());
              } else {
                  username.setText("You are not logged in.");
              }
          }
      });

      username = (TextView) view.findViewById(R.id.username);

    initTempButton(view);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();

    // For scenarios where the main activity is launched and user
    // session is not null, the session state change notification
    // may not be triggered. Trigger it if it's open/closed.
    Session session = Session.getActiveSession();
    if (session != null &&
            (session.isOpened() || session.isClosed()) ) {
      onSessionStateChange(session, session.getState(), null);
    }

    uiHelper.onResume();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    uiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onPause() {
    super.onPause();
    uiHelper.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    uiHelper.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }

  private void initTempButton(View view) {
    Button clickButton = (Button) view.findViewById(R.id.temp_button);
    clickButton.setOnClickListener( new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        ChatFragment chatFragment = new ChatFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, chatFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
      }
    });
  }

  private void onSessionStateChange(Session session, SessionState state,
                                    Exception exception) {
    if (state.isOpened()) {
      Log.i(TAG, "Logged in...");
    } else if (state.isClosed()) {
      Log.i(TAG, "Logged out...");
    }
  }

  private Session.StatusCallback callback = new Session.StatusCallback() {
    @Override
    public void call(Session session, SessionState state,
                     Exception exception) {
      onSessionStateChange(session, state, exception);
    }
  };

}
