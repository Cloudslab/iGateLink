package org.cloudbus.foggatewaylib.aneka;

import com.manjrasoft.aneka.Property;

public class FTPStorageBucket extends StorageBucket {
    public final static int DEFAULT_FTP_PORT = 21;

    private String user;
    private String password;
    private String host;
    private int port;

    public FTPStorageBucket(String name, String user, String password, String host, int port){
        super("FTP", name);
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public FTPStorageBucket(String name, String user, String password, String host){
        this(name, user, password, host, DEFAULT_FTP_PORT);
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    protected void fillProperty(int index, Property property) {
        switch (index){
            case 1:
                property.setNameProperty("Username");
                property.setValue(user);
                break;
            case 2:
                property.setNameProperty("Password");
                property.setValue(password);
                break;
            case 3:
                property.setNameProperty("Host");
                property.setValue(host);
                break;
            case 4:
                property.setNameProperty("Port");
                property.setValue(String.valueOf(port));
                break;
            default:
                super.fillProperty(index, property);
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
