package messages.login;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class LoginAttemptMessage extends AbstractMessage {
    
    private String username;
    private String password;
    
    public LoginAttemptMessage() {
        
    }
    
    public LoginAttemptMessage(String username, String password) {
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
