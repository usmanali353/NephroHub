package usmanali.nephrohub;

/**
 * Created by SAJIDCOMPUTERS on 10/26/2017.
 */

public class User {
    private String name;
    private String email;

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    private String registration_number;
    public User(String name, String email, String password, String phone,String registration_number) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.registration_number=registration_number;
    }

    private String password;
    private String phone;





 public User(){

 }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
