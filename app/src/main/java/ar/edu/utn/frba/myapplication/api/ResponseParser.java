package ar.edu.utn.frba.myapplication.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ar.edu.utn.frba.myapplication.api.responses.event.Event;
import ar.edu.utn.frba.myapplication.api.responses.event.EventAdapter;

/**
 * Created by emanuel on 10/4/17.
 */

public class ResponseParser {

    public static final ResponseParser instance = new ResponseParser();

    final Gson gson;

    ResponseParser() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Event.class, new EventAdapter());
        gson = builder.create();
    }

    public Event parseEvent(String jsonString) {
        return gson.fromJson(jsonString, Event.class);
    }

    public <T> T parse(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }
}

