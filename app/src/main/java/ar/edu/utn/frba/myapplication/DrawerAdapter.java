package ar.edu.utn.frba.myapplication;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frba.myapplication.api.responses.Channel;
import ar.edu.utn.frba.myapplication.api.responses.Identifiable;
import ar.edu.utn.frba.myapplication.api.responses.User;
import ar.edu.utn.frba.myapplication.session.Session;

/**
 * Created by emanuel on 10/4/17.
 */

class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    interface Listener {
        void selectChat(Identifiable channel);
    }

    private Context context;
    private LayoutInflater layoutInflater;
    private SparseArray<Class<? extends ViewHolder>> viewHolderTypes = new SparseArray<>();
    private List<Item> items = new ArrayList<>();
    private Listener listener;

    DrawerAdapter(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
        layoutInflater = LayoutInflater.from(context);
        viewHolderTypes.put(R.layout.nav_header_main, HeaderViewHolder.class);
        viewHolderTypes.put(R.layout.drawer_list_item, ListItemViewHolder.class);
    }

    void update(Session session, boolean connecting) {
        items.clear();
        items.add(new HeaderItem(context, session, connecting));
        if (session != null) {
            for (Channel channel: session.getChannels()) {
                items.add(new ChatItem(channel));
            }
            for (User user: session.getUsers()) {
                items.add(new ChatItem(user));
            }
        }
        else {

        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            View view = layoutInflater.inflate(viewType, parent, false);
            // El constructor tiene un DrawerAdapter implícito por ser inner class.
            Constructor<? extends ViewHolder> constructor = viewHolderTypes.get(viewType).getConstructor(DrawerAdapter.class, View.class);
            return constructor.newInstance(this, view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        items.get(position).fillViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private abstract class Item {
        @LayoutRes abstract int getViewType();
        abstract void fillViewHolder(@NonNull ViewHolder holder);
    }

    private class HeaderItem extends Item {

        private final int iconResource;
        private final CharSequence mainText;
        private final CharSequence secondaryText;

        HeaderItem(Context context, Session session, boolean connecting) {
            if (connecting) {
                // Conectando
                iconResource = R.drawable.ic_profile;
                mainText = context.getText(R.string.connecting);
                secondaryText = null;
            }
            else if (session != null) {
                // En Sesión
                iconResource = R.drawable.ic_action_name;
                mainText = session.getMe().getName();
                secondaryText = session.getMe().getProfile().getEmail();
            } else {
                // Sin Sesión
                iconResource = R.drawable.ic_profile;
                mainText = context.getText(R.string.default_drawer_message);
                secondaryText = context.getText(R.string.default_drawer_mail);
            }
        }

        @Override
        int getViewType() {
            return R.layout.nav_header_main;
        }

        @Override
        void fillViewHolder(@NonNull ViewHolder holder) {
            HeaderViewHolder vh = (HeaderViewHolder)holder;
            vh.headerImageView.setImageResource(iconResource);
            vh.headerMainText.setText(mainText);
            vh.headerSecondaryText.setText(secondaryText);
        }
    }

    private class ChatItem extends Item {

        private String prefix;
        private Identifiable chat;

        ChatItem(Channel channel) {
            this.prefix = "#";
            this.chat = channel;
        }

        ChatItem(User user) {
            this.prefix = "@";
            this.chat = user;
        }

        @Override
        int getViewType() {
            return R.layout.drawer_list_item;
        }

        @Override
        void fillViewHolder(@NonNull ViewHolder holder) {
            ListItemViewHolder vh = (ListItemViewHolder)holder;
            vh.textView.setText(prefix + chat.getName());
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.selectChat(chat);
                }
            });
        }
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class HeaderViewHolder extends ViewHolder {

        final ImageView headerImageView;
        final TextView headerMainText;
        final TextView headerSecondaryText;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerImageView = (ImageView)itemView.findViewById(R.id.imageView);
            headerMainText = (TextView)itemView.findViewById(R.id.nav_main_text);
            headerSecondaryText = (TextView)itemView.findViewById(R.id.nav_sec_text);
        }
    }

    private class ListItemViewHolder extends ViewHolder {

        final TextView textView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.textView);
        }
    }
}
