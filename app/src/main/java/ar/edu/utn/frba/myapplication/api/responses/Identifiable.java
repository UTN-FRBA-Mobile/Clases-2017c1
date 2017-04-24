package ar.edu.utn.frba.myapplication.api.responses;

import java.io.Serializable;

/**
 * Created by emanuel on 10/4/17.
 */

public class Identifiable implements Serializable {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
