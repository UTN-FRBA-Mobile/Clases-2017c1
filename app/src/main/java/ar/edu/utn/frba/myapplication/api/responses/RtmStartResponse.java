package ar.edu.utn.frba.myapplication.api.responses;

import java.util.List;

/**
 * Created by emanuel on 10/4/17.
 */

public class RtmStartResponse extends BaseResponse {

    private SelfUser self;
    private Team team;
    private List<Channel> channels;
    private List<User> users;
    private List<IM> ims;
    private String url;

    public SelfUser getSelf() {
        return self;
    }

    public Team getTeam() {
        return team;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<IM> getIMs() {
        return ims;
    }

    public String getUrl() {
        return url;
    }
}
