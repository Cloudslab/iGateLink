package org.cloudbus.foggatewaylib.aneka;

import com.manjrasoft.aneka.Property;

import org.cloudbus.foggatewaylib.utils.SimpleFTPClient;

/**
 * Representation of an FTP storage bucket.
 *
 * @author Riccardo Mancini
 */
public class FTPStorageBucket extends StorageBucket {
    public final static int DEFAULT_FTP_PORT = 21;

    private String user;
    private String password;
    private String host;
    private int port;

    /**
     * Constructs a new storage bucket with the given properties.
     *
     * @param name the name of this buckes (also referred as id).
     * @param user the user name to authenticate in the FTP server.
     * @param password the password to to authenticate in the FTP server.
     * @param host the host (domain or ip address) of the FTP server.
     * @param port the port at which the server is listening.
     */
    public FTPStorageBucket(String name, String user, String password, String host, int port){
        super("FTP", name);
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * Constructs a new storage bucket with the given properties, using the default port (21).
     *
     * @param name the name of this bucket (also referred as id).
     * @param user the user name to authenticate in the FTP server.
     * @param password the password to to authenticate in the FTP server.
     * @param host the host (domain or ip address) of the FTP server.
     * @see #FTPStorageBucket(String, String, String, String, int)
     */
    public FTPStorageBucket(String name, String user, String password, String host){
        this(name, user, password, host, DEFAULT_FTP_PORT);
    }

    /**
     * Returns the number of properties. Override this method to update the number of properties.
     *
     * @see StorageBucket#getPropertyCount()
     */
    @Override
    public int getPropertyCount() {
        return 5;
    }

    /**
     * Fills the given {@code property} knowing its {@code index}.
     * When overriding this class, do not handle case 0 but just call this method in the default
     * case.
     *
     * @see StorageBucket#fillProperty(int, Property)
     */
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

    /**
     * Returns the username.
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Creates a new {@link SimpleFTPClient} instance copying the parameters from this bucket.
     */
    public SimpleFTPClient buildFTPClient(){
        return new SimpleFTPClient(getUser(), getPassword(), getHost(), getPort());
    }
}
