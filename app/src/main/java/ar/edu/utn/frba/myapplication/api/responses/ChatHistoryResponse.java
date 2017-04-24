package ar.edu.utn.frba.myapplication.api.responses;

import java.util.List;

import ar.edu.utn.frba.myapplication.api.responses.event.MessageEvent;

/**
 * Created by emanuel on 24/4/17.
 */

public class ChatHistoryResponse extends BaseResponse {

    private String latest;
    private List<MessageEvent> messages;

    public String getLatest() {
        return latest;
    }

    public List<MessageEvent> getMessages() {
        return messages;
    }
}
