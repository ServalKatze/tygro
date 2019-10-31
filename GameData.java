
public class GameData {
    public Player player;
    public TileMap tileMap;
    public Camera camera;
    public String statusMsg;
    public String enemyMsg;
    public int level;
    public byte charSel;
    public boolean isFinalLevel;
    
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
        isFinalLevel = false;
        player = new Player();
        if(tileMap == null)
            tileMap = new TileMap(10, 10);
        else 
            tileMap.reinit(10, 10);
        camera = new Camera();
        statusMsg = Messages.greeting;
        level = 0;
        charSel = 0;
        generateMap();
        enemyMsg = tileMap.enemyCount + " enemies";
    }
    
    public void generateMap() {
        // TODO: level sets map size and enemy types+level
        if(level == 5) {
            generateFinalMap();
            return;
        }
        
        // parameters level 1
        byte tileMapHeight = 10;
        byte tileMapWidth = 10;
        
        byte maxRoomWidth = 4;
        byte maxRoomHeight = 4;
        
        byte gridWidth = tileMapWidth / (maxRoomWidth);
        byte gridHeight = tileMapHeight / (maxRoomHeight);
        
        byte roomCenterX = maxRoomWidth / 2;
        byte roomCenterY = maxRoomHeight / 2;
        
        boolean isPlayerPlaced = false;
        byte maxEnemies = 50;
        byte numEnemies = 0;
        byte maxEnemyType = 2;
        
        if(level == 1) {
            tileMapHeight = 20;
            tileMapWidth = 10;
            maxEnemyType = 3;
            maxRoomWidth = 8;
        } else if (level == 2) {
            tileMapHeight = 20;
            tileMapWidth = 20;
            maxEnemyType = 4;
            maxRoomWidth = 8;
            maxRoomHeight= 5;
        } else if (level > 2) {
            // TODO: Tygro level
            maxEnemyType = 4;
            tileMapHeight = 20;
            tileMapWidth = 20;
            maxRoomWidth = 8;
            maxRoomHeight= 5;
        }
        
        tileMap.reinit(tileMapWidth, tileMapHeight);
        
        // draw rooms
        for(byte row=0; row<gridHeight; row = row + 1) {
            for(byte col=0; col<gridWidth; col = col + 1) {
                
                // random room size
                byte roomWidth =  (byte) Math.random(2, maxRoomWidth);
                byte roomHeight = (byte) Math.random(2, maxRoomHeight);
                
                byte startCol = 1 + col * (maxRoomWidth) + ((maxRoomWidth - roomWidth) / 2);
                byte startRow = 1 + row * (maxRoomHeight) + ((maxRoomHeight - roomHeight) / 2);
    
                byte enemiesInRoom = 0;
                
                // set room tiles
                for(byte roomRow=0; roomRow<roomHeight; roomRow = roomRow + 1) {
                    for(byte roomCol=0; roomCol<roomWidth; roomCol = roomCol + 1) {
                        tileMap.setTile((byte) startRow + roomRow, 
                            (byte) startCol + roomCol, false);
                    
                        if(isPlayerPlaced && numEnemies < maxEnemies && (enemiesInRoom <= 1 || Math.random(1, enemiesInRoom) == 0)) {
                            // TODO: place some enemies within room
                            numEnemies = numEnemies + 1; 
                            enemiesInRoom = enemiesInRoom + 1;
                            tileMap.addEnemy(Math.random(1, maxEnemyType), (byte) startCol + roomCol, (byte) startRow + roomRow);
                        }
                        
                    }
                }
                
                // player starts in first room
                if(!isPlayerPlaced) {
                    player.setCoord((byte) startCol, (byte) startRow);
                    tileMap.addObject(player.tileObj);
                    isPlayerPlaced = true;
                }
                
            }
        }
        
        enemyMsg = tileMap.enemyCount + " enemies";
        
        // draw connecting corridor grid
        // horizontal lines
        for(int row=roomCenterY; row<tileMapHeight - 1; row += maxRoomHeight) {
            for(int col=1; col<tileMapWidth - 1; col += 1) {
                tileMap.setTile((byte) row, (byte) col, false);
            }
        }
        
        // vertical lines
        for(int col=roomCenterX; col<tileMapWidth -1; col += maxRoomWidth) {
            for(int row=1; row<tileMapHeight -1; row += 1) {
                tileMap.setTile((byte) row, (byte) col, false);
            }
        }
        
        // TODO: place doors on every "junction" 
        // A) ... B) #o#  C) #.  D) .#
        //    #o#    ...     o.     .o
        //                   #.     .#
        // Hint: These can only occur on the horizontal/vertical lines
    }
    
    public void generateFinalMap() {
        byte roomSize = 10;
        isFinalLevel = true;
        
        tileMap.reinit(roomSize + 1, roomSize + 1);
        
        // just one big room
        for(int i = 0; i < roomSize; i++) {
            for(int j=0; j < roomSize; j++) {
                tileMap.setTile(j, i, false);
            }
        }
        
        // player starts on bottom center
        player.setCoord(roomSize / 2, roomSize - 1);
        tileMap.addObject(player.tileObj);
        
        // !!! T Y G R O !!!
        tileMap.addEnemy(4, roomSize / 2 - 1, 1);
        
        // Potions
        tileMap.addObject(new TileObject(4, 0, roomSize - 1));
        tileMap.addObject(new TileObject(4, roomSize - 1, roomSize - 1));

    }
    
    /*
    // Testmap
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
        for (int i = 0; i < 2; i++) {
            tileMap.addEnemy(1, i + 7, 6);
        }
        
        // add enemy - zombies
        for (int i = 0; i < 2; i++) {
            tileMap.addEnemy(2, i + 7, 7);
        }
        
        // add enemy - demons
        for (int i = 0; i < 2; i++) {
            tileMap.addEnemy(3, i + 7, 8);
        }
        
        // add item - health potion
        for(int i = 0; i < 3; i++) {
            tileMap.addObject(new TileObject(4, 2 + i, 7));
        }
        
        // add stairs down 
        tileMap.addObject(new TileObject(6, 7, 7));
    }
    */
}