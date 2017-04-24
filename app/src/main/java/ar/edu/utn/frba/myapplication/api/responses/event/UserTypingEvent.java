package ar.edu.utn.frba.myapplication.api.responses.event;

/**
 * Created by emanuel on 24/4/17.
 */

public class UserTypingEvent extends Event {

    private String channel;
    private String user;

    public String getChannel() {
        return channel;
    }

    public String getUser() {
        return user;
    }
}
