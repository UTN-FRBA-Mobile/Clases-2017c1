package ar.edu.utn.frba.myapplication.api.responses.event;

/**
 * Created by emanuel on 25/9/16.
 */
public class MessageEvent extends Event {

    private int id;
    private String channel;
    private String user;
    private String text;

    public MessageEvent(int id, String channel, String user, String type, String text) {
        super(type, null);
        this.id = id;
        this.channel = channel;
        this.user = user;
        this.text = text;
    }

    public String getChannel() {
        return channel;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Mensaje: " + user + " - " + text;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MessageEvent) {
            return equalsToMessage((MessageEvent)obj);
        }
        return super.equals(obj);
    }

    boolean equalsToMessage(MessageEvent message) {
        return (id != 0 && id == message.id) ||
               (getTs() != null && getTs().equals(message.getTs()));
    }
}
