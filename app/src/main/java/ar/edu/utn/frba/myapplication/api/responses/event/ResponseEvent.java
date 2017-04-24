package ar.edu.utn.frba.myapplication.api.responses.event;

/**
 * Created by emanuel on 24/4/17.
 */

public class ResponseEvent extends Event {

    private boolean ok;
    private String reply_to;

    public boolean isOk() {
        return ok;
    }

    public String getReplyTo() {
        return reply_to;
    }
}
