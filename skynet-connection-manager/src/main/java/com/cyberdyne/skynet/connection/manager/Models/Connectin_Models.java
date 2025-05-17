package com.cyberdyne.skynet.connection.manager.Models;

public class Connectin_Models
{

    long id;
    String key;
    String protocol;
    long create_user_id;
    boolean status;
    String connection_hash;

    public Connectin_Models() {
    }

    public Connectin_Models(long id, String key, String protocol, long create_user_id, boolean status,String connection_hash) {
        this.id = id;
        this.key = key;
        this.protocol = protocol;
        this.create_user_id = create_user_id;
        this.status = status;
        this.connection_hash = connection_hash;
    }

    public Connectin_Models(String protocol, long create_user_id, boolean status,String connection_hash) {
        this.protocol = protocol;
        this.create_user_id = create_user_id;
        this.status = status;
        this.connection_hash = connection_hash;
    }

    public Connectin_Models(String key, String protocol, long create_user_id, boolean status,String connection_hash) {
        this.key = key;
        this.protocol = protocol;
        this.create_user_id = create_user_id;
        this.status = status;
        this.connection_hash = connection_hash;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public long getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(long create_user_id) {
        this.create_user_id = create_user_id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getConnection_hash() {
        return connection_hash;
    }

    public void setConnection_hash(String connection_hash) {
        this.connection_hash = connection_hash;
    }
}
