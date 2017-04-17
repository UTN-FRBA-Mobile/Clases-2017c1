package ar.edu.utn.frba.myapplication.api.responses;

/**
 * Created by emanuel on 17/4/17.
 */

public class AuthRevokeResponse extends BaseResponse {

    private boolean revoked;

    public boolean isRevoked() {
        return revoked;
    }
}
