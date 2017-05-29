package ar.edu.utn.frba.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import ar.edu.utn.frba.myapplication.storage.Preferences;

public class NotificationsFragment extends Fragment {
    private MainFragment.OnFragmentInteractionListener mListener;
    private Preferences preferences;
    private EditText topicEditText;
    private EditText tokenTextView;
    private EditText userIdTextView;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topicEditText = (EditText)view.findViewById(R.id.topic);
        tokenTextView = (EditText) view.findViewById(R.id.token);
        userIdTextView = (EditText) view.findViewById(R.id.userId);

        view.findViewById(R.id.suscribe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = topicEditText.getText().toString();
                subscribe(topic);
                mListener.showToast("Subscribed to " + topic);
            }
        });
        view.findViewById(R.id.unsuscribe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = topicEditText.getText().toString();
                unsubscribe(topic);
                mListener.showToast("Unsubscribed from " + topic);
            }
        });

        String userId = preferences.getUserId();
        userIdTextView.setText(userId);

        String token = FirebaseInstanceId.getInstance().getToken();
        tokenTextView.setText(token);
    }

    private void unsubscribe(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    private void subscribe(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        preferences = Preferences.get(context);
        if (context instanceof MainFragment.OnFragmentInteractionListener) {
            mListener = (MainFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
