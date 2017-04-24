package ar.edu.utn.frba.myapplication.api.responses.event;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by emanuel on 25/9/16.
 */
public class Event implements Serializable {

    private String type;
    private String ts;
    private Date dateTime;

    Event() {
    }

    Event(String type, String ts) {
        this.type = type;
        this.ts = ts;
    }

    public String getType() {
        return type;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        if (this.ts == null) {
            this.ts = ts;
        }
        else {
            throw new RuntimeException("No se puede cambiar.");
        }
    }

    public Date getDateTime() {
        if (dateTime == null) {
            dateTime = new Date();
        }
        return dateTime;
    }

    @Override
    public String toString() {
        return "Evento: " + type;
    }
}
