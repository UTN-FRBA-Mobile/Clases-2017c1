package ar.edu.utn.frba.myapplication.api.responses.event;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by emanuel on 25/9/16.
 */
public class EventAdapter implements JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject =  json.getAsJsonObject();
        if (jsonObject.get("reply_to") != null) {
            return context.deserialize(json, ResponseEvent.class);
        }
        JsonElement typeObj = jsonObject.get("type");
        String type = typeObj != null ? typeObj.getAsString() : null;
        Class<? extends Event> clazz = classForType(type != null ? type : "unknown");
        return context.deserialize(json, clazz);
    }

    private Class<? extends Event> classForType(String type) {
        switch (type) {
            case "message":
                return MessageEvent.class;
            case "user_typing":
                return UserTypingEvent.class;
            default:
                return UnknownEvent.class;
        }
    }


}
