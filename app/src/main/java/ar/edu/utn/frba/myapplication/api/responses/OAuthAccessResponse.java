package ar.edu.utn.frba.myapplication.api.responses;

/**
 * Created by emanuel on 17/4/17.
 */

public class OAuthAccessResponse extends BaseResponse {
    private String access_token;
    private String scope;

    public String getAccessToken() {
        return access_token;
    }

    public String getScope() {
        return scope;
    }
}
