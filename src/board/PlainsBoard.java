/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package board;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/* public class PlainsBoard extends Board
 * 
 *          \/
 * 
 * This is a board situated in the Plains. This class is
 * simply used to distinguish different types of Boards.
 * One could simple create a Board object with the right
 * model's name
 */

// TODO: Use the board's name to get the model of it

public class PlainsBoard extends Board {
    
    public static final String NAME = "PlainsBoard";
    
    // Construtores
    public PlainsBoard(String boardDir, AssetManager assetManager, Node rootNode, ViewPort viewPort) {
        super(boardDir, assetManager, rootNode, viewPort);
    }
}
