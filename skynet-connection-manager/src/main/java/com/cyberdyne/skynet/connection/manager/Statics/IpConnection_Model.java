package com.cyberdyne.skynet.connection.manager.Statics;

public class IpConnection_Model
{

    String Ip;
    String Key;
    String Protocol;

    public IpConnection_Model()
    {
    }

    public IpConnection_Model(String ip, String key,String Protocol)
    {
        this.Ip = ip;
        this.Key = key;
        this.Protocol = Protocol;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getProtocol() {
        return Protocol;
    }

    public void setProtocol(String protocol) {
        Protocol = protocol;
    }
}
