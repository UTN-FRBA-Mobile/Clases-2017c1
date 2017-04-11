package ar.edu.utn.frba.myapplication.api.responses;

/**
 * Created by emanuel on 10/4/17.
 */

public class Channel extends Identifiable {

    private boolean is_member;
    private int unread_count;

    public boolean isMember() {
        return is_member;
    }

    public int getUnreadCount() {
        return unread_count;
    }
}
