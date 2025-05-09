package com.cyberdyne.skynet.connection.manager.Models;

public class Users_Model
{

    long id;
    String useename;
    String password;
    String datetime;

    public Users_Model(String useename, String password, String datetime)
    {
        this.id = id;
        this.useename = useename;
        this.password = password;
        this.datetime = datetime;
    }

    public Users_Model(long id, String useename, String password, String datetime)
    {
        this.id = id;
        this.useename = useename;
        this.password = password;
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUseename() {
        return useename;
    }

    public void setUseename(String useename) {
        this.useename = useename;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
