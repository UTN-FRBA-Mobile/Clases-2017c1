package ar.edu.utn.frba.myapplication.api.responses;

/**
 * Created by emanuel on 10/4/17.
 */

public class BaseResponse {

    private boolean ok;
    private String error;

    public boolean isOk() {
        return ok;
    }

    public String getError() {
        return error;
    }
}
