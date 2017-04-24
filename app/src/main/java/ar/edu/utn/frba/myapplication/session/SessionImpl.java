package ar.edu.utn.frba.myapplication.session;

import java.util.List;

import ar.edu.utn.frba.myapplication.api.responses.Channel;
import ar.edu.utn.frba.myapplication.api.responses.Chat;
import ar.edu.utn.frba.myapplication.api.responses.IM;
import ar.edu.utn.frba.myapplication.api.responses.Identifiable;
import ar.edu.utn.frba.myapplication.api.responses.SelfUser;
import ar.edu.utn.frba.myapplication.api.responses.Team;
import ar.edu.utn.frba.myapplication.api.responses.User;

/**
 * Created by emanuel on 10/4/17.
 */

public class SessionImpl implements Session {

    private SelfUser self;
    private User me;
    private Team team;
    private List<Channel> channels;
    private List<User> users;
    private List<IM> ims;

    public SelfUser getSelf() {
        return self;
    }

    public void setSelf(SelfUser self) {
        this.self = self;
        this.me = null;
    }

    @Override
    public User getMe() {
        if (me == null && users != null && self != null) {
            for (User user : users) {
                if (user.getId().equals(self.getId())) {
                    me = user;
                }
            }
        }
        return me;
    }

    @Override
    public Chat findChat(Identifiable destination) {
        if (destination instanceof Chat) {
            return (Chat) destination;
        }
        if (destination instanceof User) {
            User user = (User) destination;
            for (IM im : ims) {
                if (im.getUser().equals(user.getId())) {
                    return im;
                }
            }
        }
        return null;
    }

    @Override
    public User findUser(String userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        this.me = null;
    }

    @Override
    public List<IM> getIMs() {
        return ims;
    }

    public void setIMs(List<IM> ims) {
        this.ims = ims;
    }
}
