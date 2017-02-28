package board;

import squares.Tree;
import squares.Square;
import squares.Grass;
import squares.Rock;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.water.SimpleWaterProcessor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import squares.Hole;
import squares.Lake;
import util.Methods;

/*
 * public class Board
 * 
 *          \/
 * 
 * This class represents the game's board. It has a Node where you
 * add everything and Arrays of the squares. It has various
 * methods, such as getSquare(), destroySquare(), createBoard(),
 * etc...
 */

public class Board {
    
    // VARIABLES
    // Board
    private final Node boardNode; // Internal Node to control the board more easily
    private final String boardDir; // String used for directory of model and other things
    
    // Squares
    private final Node squaresNode; // Internal Node to manage the squares more easily
    private final BatchNode grassNode;
    private final BatchNode rockNode;
    private final BatchNode holeNode;
    private final BatchNode lakeNode;
    private final BatchNode waterNode;
    private final BatchNode treeNode;
    private final BatchNode brokenRockNode;
    private final BatchNode brokenTreeNode;
    
    // Water
    private final SimpleWaterProcessor water; // Processor to add "nice" water to the lakes
    private final ViewPort viewPort; // For internal use with the processor
    
    // Arrays
    // TODO: Organize the square's sides, giving it name or color, not number 1 and 2
    private Square[][] squares; // Array of left squares
    private Square[][] squares2; // Array of right squares
    
    // Misc
    private final AssetManager assetManager; // To load models
    private Random random;
    private long seed;
    
    public Board(String boardDir, AssetManager assetManager, Node rootNode, ViewPort viewPort) {
        // Init the variables needed
        this.boardDir = boardDir;
        this.boardNode = new Node("Board: " + this.boardDir);
        
        this.squaresNode = new Node("Squares");
        this.squares = new Square[7][7];
        this.squares2 = new Square[7][7];
        this.assetManager = assetManager;
        
        // Load the board's model according to the given String
        this.boardNode.attachChild(this.assetManager.loadModel("Models/" + this.boardDir + ".j3o"));
        // Attaches it to the rootNode to make it visible
        rootNode.attachChild(this.boardNode);
        
        // Inits the squares nodes
        grassNode = new BatchNode("Grass");
        rockNode = new BatchNode("Rock");
        holeNode = new BatchNode("Hole");
        lakeNode = new BatchNode("Lake");
        waterNode = new BatchNode("Water");
        treeNode = new BatchNode("Tree");
        brokenRockNode = new BatchNode("Broken Rock");
        brokenTreeNode = new BatchNode("Broken Tree");
        
        // Gets the viewPort and configure the processor
        this.viewPort = viewPort;
        water = new SimpleWaterProcessor(assetManager);
        water.setReflectionScene(rootNode);
        //TODO: The light position is not constant. We need to have a way to get the sun's direction
        water.setLightPosition(new Vector3f(-1, -1, -1));
        this.viewPort.addProcessor(water);
        
        // Adds the nodes to boardNode to make them visible
        boardNode.attachChild(grassNode);
        boardNode.attachChild(rockNode);
        boardNode.attachChild(holeNode);
        boardNode.attachChild(lakeNode);
        boardNode.attachChild(waterNode);
        boardNode.attachChild(treeNode);
        boardNode.attachChild(brokenRockNode);
        boardNode.attachChild(brokenTreeNode);
        
        // Baralha o tabuleiro
        // DEBUG - prints the number of boards created until a valid one was found.
        // DEBUG : int count = 0;
        createBoard();
        // DEBUG : count++;
        while(!validateBoard()) {
            //System.out.println("Tabuleiro não válido. Tentativas:" + contagem);
            resetBoard();
            createBoard();
            // DEBUG : count++;
        }
        // DEBUG : System.out.println("Depois de " + count + " criações, houve um válido.");
    }
    // Methods
    // Method to return a square, used later to change terrain
    public Square getSquare(int x, int z) {
        return squares[x][z];
    }
    
    // Call it to destroy a house in the given coords.
    public boolean destroySquare(int x, int z) {
        // Get the square
        Square destroyedSquare = squares[x][z];
        // If it's a rock or tree, destroy it. Or else, return false
        //TODO: Instead of returning false, return UnsupportedOperationException, since it should be impossible to destroy other squares.
        if(destroyedSquare instanceof Tree) {
            destroyedSquare.destroySquare();
            squares[x][z] = null;
            return true;
        } else if(destroyedSquare instanceof Rock) {
            destroyedSquare.destroySquare();
            squares[x][z] = null;
            return true;
        } else {
            return false;
        }
    }
    
    private void createBoard() {
        this.createBoard(-1);
    }
    
    // Creates a board with a seed
    private void createBoard(long seed) {
        // Creating Random with seed
        if(seed < 0) {
            this.seed = System.nanoTime();
        } else {
            this.seed = seed;
        }
        this.random = new Random(this.seed);
        // LEFT BOARD
        // Internal variables for the num of each squares and offsets
        int trees = 3;
        int holes = 4;
        int rocks = 4;
        int lakes = 2;
        int offsetX = -16;
        int offsetZ = -6;
        
        // For each square's variable, add a house in an empty space
        for(int i = 0; i < trees; i++) {
            int x = random.nextInt(6) + 1;
            int z = random.nextInt(7);
            if(squares[x][z] == null) {
                Square square = new Tree(x, offsetX, z, offsetZ, assetManager.loadModel("Models/T_" + Tree.NAME + ".j3o"));
                treeNode.attachChild(square.getSquareNode());
                squares[x][z] = square;
            } else {
                i--;
            }
        }
        for(int i = 0; i < rocks; i++) {
            int x = random.nextInt(6) + 1;
            int z = random.nextInt(7);
            if(squares[x][z] == null) {
                Square square = new Rock(x, offsetX, z, offsetZ, assetManager.loadModel("Models/T_" + Rock.NAME + ".j3o"));
                rockNode.attachChild(square.getSquareNode());
                squares[x][z] = square;
            } else {
                i--;
            }
        }
        for(int i = 0; i < holes; i++) {
            int x = random.nextInt(6) + 1;
            int z = random.nextInt(7);
            if(squares[x][z] == null) {
                Square square = new Hole(x, offsetX, z, offsetZ, assetManager.loadModel("Models/T_" + Hole.NAME + ".j3o"));
                holeNode.attachChild(square.getSquareNode());
                squares[x][z] = square;
            } else {
                i--;
            }
        }
        
        // This one has more lines because of the water
        for(int i = 0; i < lakes; i++) {
            int x = random.nextInt(6) + 1;
            int z = random.nextInt(7);
            if(squares[x][z] == null) {
                Square square = new Lake(x, offsetX, z, offsetZ, assetManager.loadModel("Models/T_" + Lake.NAME + ".j3o"));
                Spatial flatWater = water.createWaterGeometry(2, 2);
                flatWater.setMaterial(water.getMaterial());
                flatWater.setLocalTranslation(x * 2 + offsetX - 1, -0.1f, z * 2 + offsetZ + 1);
                lakeNode.attachChild(square.getSquareNode());
                //waterNode.attachChild(flatWater);
                squares[x][z] = square;
            } else {
                i--;
            }
        }
        
        for(int x = 0; x < 7; x++) {
            for(int z = 0; z < 7; z++) {
                if(squares[x][z] == null) {
                    Square square = new Grass(x, offsetX, z, offsetZ, assetManager.loadModel("Models/T_" + Grass.NAME + ".j3o"));
                    grassNode.attachChild(square.getSquareNode());
                    squares[x][z] = square;
                }
            }
        }
        
        // LEFT BOARD
        // It's just the same thing but with other names
        int trees2 = 3;
        int holes2 = 4;
        int rocks2 = 4;
        int lakes2 = 2;
        int offsetX2 = 4;
        int offsetZ2 = -6;
        
        for(int i = 0; i < trees2; i++) {
            int x = random.nextInt(6);
            int z = random.nextInt(7);
            if(squares2[x][z] == null) {
                Square square = new Tree(x, offsetX2, z, offsetZ2, assetManager.loadModel("Models/T_" + Tree.NAME + ".j3o"));
                treeNode.attachChild(square.getSquareNode());
                squares2[x][z] = square;
            } else {
                i--;
            }
        }
        for(int i = 0; i < rocks2; i++) {
            int x = random.nextInt(6);
            int z = random.nextInt(7);
            if(squares2[x][z] == null) {
                Square square = new Rock(x, offsetX2, z, offsetZ2, assetManager.loadModel("Models/T_" + Rock.NAME + ".j3o"));
                rockNode.attachChild(square.getSquareNode());
                squares2[x][z] = square;
            } else {
                i--;
            }
        }
        for(int i = 0; i < holes2; i++) {
            int x = random.nextInt(6);
            int z = random.nextInt(7);
            if(squares2[x][z] == null) {
                Square square = new Hole(x, offsetX2, z, offsetZ2, assetManager.loadModel("Models/T_" + Hole.NAME + ".j3o"));
                holeNode.attachChild(square.getSquareNode());
                squares2[x][z] = square;
            } else {
                i--;
            }
        }
        for(int i = 0; i < lakes2; i++) {
            int x = random.nextInt(6);
            int z = random.nextInt(7);
            if(squares2[x][z] == null) {
                Square square = new Lake(x, offsetX2, z, offsetZ2, assetManager.loadModel("Models/T_" + Lake.NAME + ".j3o"));
                Spatial flatWater = water.createWaterGeometry(2, 2);
                flatWater.setMaterial(water.getMaterial());
                flatWater.setLocalTranslation(x * 2 + offsetX2 - 1, -0.1f, z * 2 + offsetZ2 + 1);
                lakeNode.attachChild(square.getSquareNode());
                //waterNode.attachChild(flatWater);
                squares2[x][z] = square;
            } else {
                i--;
            }
        }
        
        for(int x = 0; x < 7; x++) {
            for(int z = 0; z < 7; z++) {
                if(squares2[x][z] == null) {
                    Square square = new Grass(x, offsetX2, z, offsetZ2, assetManager.loadModel("Models/T_" + Grass.NAME + ".j3o"));
                    grassNode.attachChild(square.getSquareNode());
                    squares2[x][z] = square;
                }
            }
        }
        
        // Batches the node to improve speed and performance
        grassNode.batch();
        rockNode.batch();
        holeNode.batch();
        lakeNode.batch();
        waterNode.batch();
        treeNode.batch();
        brokenRockNode.batch();
        brokenTreeNode.batch();
    }
    
    //  Validates the board using a grouping algorithm
    private boolean validateBoard() {
        // LEFT BOARD
        // Internal variables for controling groups
        boolean firstValid, secondValid;
        int lastGroup = 0;
        int numGroups = 0;
        // Gets all the grass squares to an ArrayList for easier use
        ArrayList<Grass> grasses = new ArrayList<Grass>();
        for(int x = 0; x < 7; x++) {
            for(int z = 0; z < 7; z++) {
                if(squares[x][z] instanceof Grass) {
                    grasses.add((Grass)squares[x][z]);
                }
            }
        }
        // Creates a map to give a grass a group
        LinkedHashMap<Grass, Integer> grassMap = new LinkedHashMap<Grass, Integer>();
        for(Grass grass : grasses) {
            // If there is no grass at the map, add it as a new group
            if(grassMap.get(grass) == null) {
                lastGroup++;
                grassMap.put(grass, lastGroup);
                numGroups++;
            }
            // Gets all adjacents grass squares and cleans the array of null's
            ArrayList<Grass> nearGrass = new ArrayList<Grass>();
            nearGrass.add((Grass)Methods.getSquareByName("Square: " + (grass.getX()+1) + "-" + (grass.getZ()), grasses));
            nearGrass.add((Grass)Methods.getSquareByName("Square: " + (grass.getX()-1) + "-" + (grass.getZ()), grasses));
            nearGrass.add((Grass)Methods.getSquareByName("Square: " + (grass.getX()) + "-" + (grass.getZ()+1), grasses));
            nearGrass.add((Grass)Methods.getSquareByName("Square: " + (grass.getX()) + "-" + (grass.getZ()-1), grasses));
            Methods.cleanArray(nearGrass);
            // If it is empty, returns false
            if(nearGrass.isEmpty()) {
                return false;
            } else {
                // For each adjacent grass it will check if it has group or not.
                // In case it doesn't have a group, add it to the selected grass group.
                for(Grass adjacentGrass : nearGrass) {
                    if(grassMap.get(adjacentGrass) == null) {
                        grassMap.put(adjacentGrass, grassMap.get(grass));
                        // If it's from a different group, concatenate them
                    } else if(!grassMap.get(adjacentGrass).equals(grassMap.get(grass))) {
                        int joiningGroup = grassMap.get(grass);
                        for(Grass grassMapa : grassMap.keySet()) {
                            if(grassMap.get(grassMapa).equals(joiningGroup)) {
                                grassMap.put(grassMapa, grassMap.get(adjacentGrass));
                            }
                        }
                        // A group was concatenated, so minus one group xD
                        numGroups--;
                        lastGroup = numGroups;
                    }
                }
            }
        }
        // If there is no more than one group, the first one is valid
        if(numGroups > 1) {
            return false;
        } else {
            firstValid = true;
        }
        
        // RIGHT BOARD
        // The same thing
        int lastGroup2 = 0;
        int numGroups2 = 0;
        ArrayList<Grass> grass2 = new ArrayList<Grass>();
        for(int x = 0; x < 7; x++) {
            for(int z = 0; z < 7; z++) {
                if(squares2[x][z] instanceof Grass) {
                    grass2.add((Grass)squares2[x][z]);
                }
            }
        }
        LinkedHashMap<Grass, Integer> grassMap2 = new LinkedHashMap<Grass, Integer>();
        for(Grass grass : grass2) {
            if(grassMap2.get(grass) == null) {
                lastGroup2++;
                grassMap2.put(grass, lastGroup2);
                numGroups2++;
            }
            ArrayList<Grass> nearGrass = new ArrayList<Grass>();
            nearGrass.add((Grass)Methods.getSquareByName("Square: " + (grass.getX()+1) + "-" + (grass.getZ()), grass2));
            nearGrass.add((Grass)Methods.getSquareByName("Square: " + (grass.getX()-1) + "-" + (grass.getZ()), grass2));
            nearGrass.add((Grass)Methods.getSquareByName("Square: " + (grass.getX()) + "-" + (grass.getZ()+1), grass2));
            nearGrass.add((Grass)Methods.getSquareByName("Square: " + (grass.getX()) + "-" + (grass.getZ()-1), grass2));
            Methods.cleanArray(nearGrass);
            if(nearGrass.isEmpty()) {
                return false;
            } else {
                for(Grass adjacentGrass : nearGrass) {
                    if(grassMap2.get(adjacentGrass) == null) {
                        grassMap2.put(adjacentGrass, grassMap2.get(grass));
                    } else if(!grassMap2.get(adjacentGrass).equals(grassMap2.get(grass))) {
                        int joiningGroup = grassMap2.get(grass);
                        for(Grass grassMapa : grassMap2.keySet()) {
                            if(grassMap2.get(grassMapa).equals(joiningGroup)) {
                                grassMap2.put(grassMapa, grassMap2.get(adjacentGrass));
                            }
                        }
                        numGroups2--;
                        lastGroup2 = numGroups2;
                    }
                }
            }
        }
        if(numGroups2 > 1) {
            return false;
        } else {
            secondValid = true;
        }
        
        // Returns if the two are valid or not
        return firstValid && secondValid;
    }
    
    public long getMapSeed() {
        // TODO: Return a final seed so people can't change this value.
        //       It's only this way for debugging
        return seed;
    }
    
    // Resets the board's variables in case the map isn't valid.
    private void resetBoard() {
        squaresNode.detachAllChildren();
        grassNode.detachAllChildren();
        rockNode.detachAllChildren();
        treeNode.detachAllChildren();
        holeNode.detachAllChildren();
        lakeNode.detachAllChildren();
        waterNode.detachAllChildren();
        brokenTreeNode.detachAllChildren();
        brokenRockNode.detachAllChildren();
        squares = new Square[7][7];
        squares2 = new Square[7][7];
    }
   
    // Method issued by the main code to create a new board. Before, the code would simply create a new Board,
    // but it wasn't efficient. It also broken the water processor a lot xD
    public void newBoard() {
        this.newBoard(-1);
    }
    
    int count;
    public void newBoard(long seed) {
        resetBoard();
        createBoard(seed);
        // DEBUG : count++;
        count++;
        while(!validateBoard()) {
            resetBoard();
            createBoard();
            // DEBUG : count++;
            count++;
        }
    }
    
    public void benchmarkBoard() {
        count = 0;
        System.out.println("Benchmarking board algorithm...");
        for(int i = 0; i < 250; i++) {
            newBoard();
        }
        System.out.println("Benchmark complete");
        System.out.println("------------------");
        System.out.println();
        System.out.println("Number of boards          : 250");
        System.out.println("Number attemps            : " + count);
        System.out.println("Average attemps per board : " + (count / 250f) + "\n");
        count = 0;
    }
}
