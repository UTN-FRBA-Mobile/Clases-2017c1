package ar.edu.utn.frba.myapplication.api.responses;

/**
 * Created by emanuel on 10/4/17.
 */

public class Profile {

    private String first_name;
    private String last_name;
    private String real_name;
    private String email;
    private String image_24;
    private String presence;

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getRealName() {
        return real_name;
    }

    public String getEmail() {
        return email;
    }

    public String getImage_24() {
        return image_24;
    }

    public String getPresence() {
        return presence;
    }
}
