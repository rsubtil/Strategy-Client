package appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import encryption.Encryptor;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.Callable;
import messages.login.LoginAttemptMessage;
import messages.login.LoginResultMessage;

public class NetworkAppState extends AbstractAppState implements MessageListener<Client>, ClientStateListener {
    
    // Variables
    // Essential
    private SimpleApplication app;
    private AppStateManager stateManager;
    private GameplayAppState gameplayAppState;
    
    // Network
    private Client client;
    private boolean isConnected;
    
    // GUI
    private MenuAppState menuAppState;
    
    public NetworkAppState(GameplayAppState gameplayAppState) {
        this.gameplayAppState = gameplayAppState;
    }
    
@Override
    public void initialize(AppStateManager stateManager, Application app) {
        // Initializes essential variables
        this.app = (SimpleApplication)app;
        this.stateManager = stateManager;
        
        // Gets the app state controlling the GUI
        menuAppState = stateManager.getState(MenuAppState.class);
        
        // Continus to initialize
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        
    }

    @Override
    public void cleanup() {
        // Closes active client
        try {
            client.close();
            
        } catch(Exception e) {
            System.out.println("Weird exception!");
            e.printStackTrace();
        }
        client = null;
        
        // Continues to cleanup
        super.cleanup();
    }
    
    private void connectToServer() throws IOException {
        client = Network.connectToServer("localhost", 1000);
        client.start();
        client.addMessageListener(this);
        client.addClientStateListener(this);
        long startMillis = System.currentTimeMillis();
        while (!client.isConnected()) {
            // Waits for client to fully connect to prevent serialization problems
        }
        System.out.println("Connection took " + (System.currentTimeMillis() - startMillis) + " milliseconds to start");
    }
    
    @Override
    public void clientConnected(Client c) {
        isConnected = true;
    }
    
    @Override
    public void clientDisconnected(Client c, DisconnectInfo info) {
        isConnected = false;
    }
    
    public void loginIntoServer(String username, String password) {
        // Made it final to use it in the enqueue method.
        final LoginAttemptMessage login = new LoginAttemptMessage(username, Encryptor.encrypt(password));
        
        // Connect to server
        try {
            connectToServer();
        } catch(ConnectException ce) {
            menuAppState.loginResult(4);
            client = null;
        } catch(IOException ioe) {
            client = null;
            ioe.printStackTrace();
        }
        
        if(client != null) {
            // Enqueues the connection methods to another thread to
            // not lock the main game loop.   
            app.enqueue(new Callable<Object>() {
                public Object call() throws Exception {
                        client.send(login);
                        return null;
                }
            });           
        /*} else {
            try {
                client.send(login);
            } catch(IllegalStateException ise) {
                client = null;
                loginIntoServer(username, password);
            }*/
        }
    }
    
    public void messageReceived(Client source, Message message) {
        if(message instanceof LoginResultMessage) {
            LoginResultMessage loginResult = (LoginResultMessage)message;
            menuAppState.loginResult(loginResult.getResult());
        }
    }
}
