package ar.edu.utn.frba.myapplication.api.responses.event;

/**
 * Created by emanuel on 25/9/16.
 */
public class MessageEvent extends Event {

    private String channel;
    private String user;
    private String text;

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
}
