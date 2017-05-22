package ar.edu.utn.frba.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ar.edu.utn.frba.myapplication.api.Callback;
import ar.edu.utn.frba.myapplication.api.responses.Chat;
import ar.edu.utn.frba.myapplication.api.responses.ChatHistoryResponse;
import ar.edu.utn.frba.myapplication.api.responses.User;
import ar.edu.utn.frba.myapplication.api.responses.event.Event;
import ar.edu.utn.frba.myapplication.api.responses.event.MessageEvent;
import ar.edu.utn.frba.myapplication.api.responses.event.UserTypingEvent;
import ar.edu.utn.frba.myapplication.service.RTMService;
import ar.edu.utn.frba.myapplication.session.Session;
import ar.edu.utn.frba.myapplication.storage.ImageLoader;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    private static final String ARG_CHAT = "chat";
    private static final String MESSAGES = "messages";

    private Chat chat;
    private List<MessageEvent> messages = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private ChatAdapter adapter;
    private Handler handler = new Handler();
    private Runnable removeUserTypingRunnable = new Runnable() {
        @Override
        public void run() {
            userTypingTextView.setVisibility(View.GONE);
        }
    };

    private RecyclerView messagesView;
    private EditText messageEditText;
    private View sendButton;
    private TextView userTypingTextView;
    private ProgressBar progressBar;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(Chat chat) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHAT, chat);
        fragment.setArguments(args);
        return fragment;
    }

    public void changeChat(Chat chat) {
        this.chat = chat;
        messages.clear();
        adapter.notifyDataSetChanged();
        loadHistory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            chat = (Chat) savedInstanceState.getSerializable(ARG_CHAT);
            MessageEvent[] savedMessages = (MessageEvent[]) savedInstanceState.getSerializable(MESSAGES);
            messages.clear();
            Collections.addAll(messages, savedMessages);
        }
        else if (getArguments() != null) {
            chat = (Chat) getArguments().getSerializable(ARG_CHAT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messagesView = (RecyclerView) view.findViewById(R.id.messagesView);
        messageEditText = (EditText) view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        userTypingTextView = (TextView) view.findViewById(R.id.userTypingTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        adapter = new ChatAdapter(messages);
        messagesView.setAdapter(adapter);
        messagesView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sendButton.setEnabled(s.length() > 0);
            }
        });
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage(chat, messageEditText.getText().toString());
                messageEditText.setText(null);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getContext().registerReceiver(eventReceiver, new IntentFilter(RTMService.NewEventIntentAction));
        loadHistory();
    }

    void loadHistory() {
        RTMService service = mListener != null ? mListener.getRtmService() : null;
        if (service != null) {
            progressBar.setVisibility(View.VISIBLE);
            service.retrieveChatHistory(chat, new Callback<ChatHistoryResponse>() {
                @Override
                public void onSuccess(final ChatHistoryResponse response) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            messages.clear();
                            if (response.isOk() && response.getMessages() != null) {
                                messages.addAll(response.getMessages());
                            }
                            adapter.notifyDataSetChanged();
                            messagesView.scrollToPosition(0);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_CHAT, chat);
        outState.putSerializable(MESSAGES, messages.toArray(new MessageEvent[]{}));
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(eventReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void addMessageIfRequired(MessageEvent message) {
        if (chat.getId().equals(message.getChannel())) {
            int index = messages.indexOf(message);
            if (index == -1) {
                messages.add(0, message);
                adapter.notifyItemInserted(0);
            }
            else {
                messages.set(index, message);
                adapter.notifyItemChanged(index);
            }
            messagesView.scrollToPosition(0);
        }
    }

    void showUserTypingIfRequired(UserTypingEvent event) {
        if (chat.getId().equals(event.getChannel())) {
            handler.removeCallbacks(removeUserTypingRunnable);
            User user = findUser(event.getUser());
            String userName = user != null ? user.getName() : event.getUser();
            userTypingTextView.setText(getString(R.string.user_typing, userName));
            userTypingTextView.setVisibility(View.VISIBLE);
            handler.postDelayed(removeUserTypingRunnable, 3000);
        }
    }

    private User findUser(String userId) {
        RTMService service = mListener.getRtmService();
        Session session = service != null ? service.getSession() : null;
        return session != null ? session.findUser(userId) : null;
    }

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Event event = (Event) intent.getSerializableExtra(RTMService.EventExtraKey);
            if (event instanceof MessageEvent) {
                addMessageIfRequired((MessageEvent)event);
            }
            else if (event instanceof UserTypingEvent) {
                showUserTypingIfRequired((UserTypingEvent)event);
            }
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        RTMService getRtmService();
        void sendMessage(Chat chat, String message);
    }

    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        private LayoutInflater inflater;
        private List<MessageEvent> messages;

        ChatAdapter(List<MessageEvent> messages) {
            inflater = LayoutInflater.from(getContext());
            this.messages = messages;
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            MessageEvent message = messages.get(position);
            if (message.getTs() == null) {
                return R.layout.chat_message_pending_item;
            }
            return R.layout.chat_message_item;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(viewType, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(messages.get(position));
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView profilePictureView;
            private TextView userTextView;
            private TextView timeTextView;
            private TextView messageTextView;

            ViewHolder(View itemView) {
                super(itemView);
                profilePictureView = (ImageView)  itemView.findViewById(R.id.profileImageView);
                userTextView = (TextView) itemView.findViewById(R.id.userTextView);
                timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
                messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            }

            void bind(MessageEvent message) {
                User user = findUser(message.getUser());
                if (user != null) {
                    ImageLoader.instance.loadImage(user.getProfile().getImage_24(), profilePictureView);
                }
                userTextView.setText(user != null ? user.getName() : message.getUser());
                timeTextView.setText(message.getTs());
                messageTextView.setText(message.getText());
            }
        }
    }
}
