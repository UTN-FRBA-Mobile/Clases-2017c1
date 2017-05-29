package ar.edu.utn.frba.myapplication.api.requests;

public class UserPushRegistration {
    public String user;
    public String type;
    public String token;

    public UserPushRegistration(String userId, String firebaseToken) {
        this.user = userId;
        this.type = "android";
        this.token = firebaseToken;
    }
}
