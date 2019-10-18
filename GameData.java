
public class GameData {
    public Player player;
    public TileMap tileMap;
    public Camera camera;
    public String statusMsg;
    
    private static GameData instance;
    
    private GameData() {}
    
    public static GameData getInstance() {
        if (GameData.instance == null) {
            GameData.instance = new GameData();
            GameData.instance.init();
        }
        return GameData.instance;
    }
    
    public void init() {
        player = new Player();
        tileMap = new TileMap(10, 10);
        camera = new Camera();
        statusMsg = Messages.greeting;
        generateMap();
    }
    
    // TODO map generator!
    public void generateMap() {
        
        byte width = 40;
        byte height = 40;
        tileMap = new TileMap(width, height);
        
        // place tiles
        //tileMap = new TileMap();
        for (int i = 1; i < width; i++) {
            tileMap.setTile(1, i, true);
            tileMap.setTile(height - 1, i + 1, true);
            tileMap.setTile(i, 1, true);
            tileMap.setTile(i, width - 1, true);
        }

        for (int i = 2; i < width - 1; i++) {
            for (int j = 2; j < height - 1; j++) {
                tileMap.setTile(j, i, false);
            }
        }
        // always place a player object!
        player.setCoord(3, 3);
        tileMap.addObject(player.tileObj);

        // testing...

        // adding Chest
        tileMap.addObject(new TileObject(1, 5, 5));

        // adding door 
        tileMap.addObject(new TileObject(2, 4, 4));

        // add enemy - skeleton
        tileMap.addEnemy(1, 6, 6);
        for (int i = 0; i < 5; i++) {
            tileMap.addEnemy(1, 7 + i, 6);
        }
    }
}