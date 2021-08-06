package ir.yaddasht.yaddasht.model.retro;

public class User {
    private String _id;
    private String name;
    private String email;
    private String password;

    private String uniqueString;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String name ,String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getUniqueString() {
        return uniqueString;
    }

    public void setUniqueString(String uniqueString) {
        this.uniqueString = uniqueString;
    }
}
