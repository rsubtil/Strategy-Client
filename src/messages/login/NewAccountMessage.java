package messages.login;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/*
 * NewAccount Message
 *
 * The NewAccount message is used to create a new account. The password is
 * unencrypted.
 *
 */

@Serializable
public class NewAccountMessage extends AbstractMessage {
    
    // Variables
    private String username;
    private String password;
    
    public NewAccountMessage() {
        
    }
    
    public NewAccountMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
}