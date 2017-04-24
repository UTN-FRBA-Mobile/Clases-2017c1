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

public interface Session {

    SelfUser getSelf();
    User getMe();
    Team getTeam();
    List<Channel> getChannels();
    List<User> getUsers();
    List<IM> getIMs();

    Chat findChat(Identifiable destination);
    User findUser(String userId);
}
