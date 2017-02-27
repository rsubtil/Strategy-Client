package messages.login;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class LoginResultMessage extends AbstractMessage {
    
    /* The result may be:
     *
     * 1 - Login successfull
     * 2 - Username has been taken
     * 3 - Username/Password not valid
     */
    public int result;
    
    public LoginResultMessage() {
        
    }
    
    public LoginResultMessage(int result) {
        this.result = result;
    }
    
    public int getResult() {
        return result;
    }
}
