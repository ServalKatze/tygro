import java.util.Arrays;
import femto.mode.HiRes16Color;


class TileMap {
    byte width;
    byte height;
    int mapSize;
    public int enemyCount;
    public TileObject tygro;

    private static final ImgWall imgWall = new ImgWall();
    private static final ImgGround imgGround = new ImgGround();
    private static final ImgGround2 imgGround2 = new ImgGround2();
    private static final ImgChest imgChest = new ImgChest();
    private static final ImgDoor imgDoor = new ImgDoor();
    private static final ImgHealthPotion imgHpPot = new ImgHealthPotion();
    private static final ImgManaPotion imgMpPot = new ImgManaPotion();
    private static final ImgStairs imgStairs = new ImgStairs();
    
    // players
    public static final ImgPlayer1 imgPlayer = new ImgPlayer1();
    public static final ImgPlayer2 imgPlayer2 = new ImgPlayer2();
    public static final ImgPlayer3 imgPlayer3 = new ImgPlayer3();
    
    // enemies
    private static final ImgSkelli1 imgSkelli1 = new ImgSkelli1();
    private static final ImgSkelli2 imgSkelli2 = new ImgSkelli2();
    private static final ImgSkelli3 imgSkelli3 = new ImgSkelli3();
    private static final ImgSkelli4 imgSkelli4 = new ImgSkelli4();
    private static final ImgSkelli5 imgSkelli5 = new ImgSkelli5();
    private static final ImgSkelli6 imgSkelli6 = new ImgSkelli6();
    private static final ImgSkelli7 imgSkelli7 = new ImgSkelli7();
    
    private static final ImgZombie1 imgZombie1 = new ImgZombie1();
    private static final ImgZombie2 imgZombie2 = new ImgZombie2();
    
    private static final ImgDemon1 imgDemon1 = new ImgDemon1();
    private static final ImgDemon2 imgDemon2 = new ImgDemon2();
    
    
    private static final ImgTygro imgTygro = new ImgTygro();
    

    boolean[] terrainData;
    TileObject firstObj;
    public TileMap(byte width, byte height) {
        this.width = width;
        this.height = height;
        this.enemyCount = 0;
        this.tygro = null;
        mapSize = width * height;
        terrainData = new boolean[mapSize];
        for (int i = 0; i < mapSize; i++) {
            terrainData[i] = false;
        }

        firstObj = null;
    }
    
    public void reinit(byte width, byte height) {
        while(firstObj != null) {
            remObject(firstObj);
        }
        this.tygro = null;
        this.width = width;
        this.height = height;
        this.enemyCount = 0;
        mapSize = width * height;
        terrainData = new boolean[mapSize];
        for (int i = 0; i < mapSize; i++) {
            terrainData[i] = false;
        }
    }

    // object management... 
    public boolean addObject(TileObject obj) {
        if(isBlocking(obj.yCoord, obj.xCoord))
            return false;
        
        if (firstObj == null) {
            firstObj = obj;
        } else {
            obj.next = firstObj;
            firstObj.prev = obj;
            firstObj = obj;
        }
        
        return true;
    }

    public void remObject(TileObject obj) {

        if(obj == tygro)
            tygro = null;
        
        if(obj.isEnemy())
            enemyCount--;

        if (obj.prev != null) {
            obj.prev.next = obj.next;
        }

        if (obj.next != null) {
            obj.next.prev = obj.prev;
        }

        if (obj == firstObj) {
            firstObj = obj.next;
        }

        obj.next = null;
        obj.prev = null;
        obj = null;
    }

    public TileObject getObject(byte row, byte col) {
        TileObject obj = firstObj;
        while (obj != null) {
            if (obj.xCoord == col && obj.yCoord == row)
                return obj;
            obj = obj.next;
        }
        
        if(tygro != null) {
            if ((tygro.xCoord == col-1 && tygro.yCoord == row) ||
                (tygro.xCoord == col && tygro.yCoord == row-1) ||
                (tygro.xCoord == col-1 && tygro.yCoord == row-1)
                )
                return tygro;
        }

        return null;
    }

    // enemy management... a fancy linkedlist class would be nicer...
    public void addEnemy(byte enemyType, byte xCoord, byte yCoord) {
        switch (enemyType) {
            case 2: // ZOMBEEEHS
                addEnemy(new Enemy(2, 20, 2, 2), (byte) Math.random(18, 20), xCoord, yCoord);
                break;
            
            case 3: // DEMONS!!!
                addEnemy(new Enemy(3, 30, 5, 5), (byte) Math.random(20,  22), xCoord, yCoord);
                break;
            
            case 4: // TYGRO!!!
                addEnemy(new Enemy(4, 200, 30, 50), 100, xCoord, yCoord);
                break;
            
            case 1:
            default:
                addEnemy(new Enemy(1, 10, 1, 1), (byte) Math.random(10, 17), xCoord, yCoord);
                break;
            
        }
    }

    public void addEnemy(Enemy enemy, byte tileType, byte xCoord, byte yCoord) {
        TileObject to = new TileObject(tileType, xCoord, yCoord, enemy);
        if(tileType == 100)
            tygro = to;
        if(addObject(to))
            enemyCount++;
    }

    // tile management...
    private int getIdx(byte row, byte col) {
        return row * this.height + col;
    }

    public void setTile(byte row, byte col, boolean isWall) {
        int idx = getIdx(row, col);
        if (idx < this.mapSize)
            this.terrainData[idx] = !isWall;
    }

    public boolean getTile(byte row, byte col) {
        if (row >= this.height || col >= this.width || row < 0 || col < 0)
            return false;

        int idx = getIdx(row, col);
        if (idx < this.mapSize)
            return terrainData[idx];
        return false;
    }

    public boolean isBlocking(byte row, byte col) {
        boolean t = getTile(row, col);
        return !t || getObject(row, col) != null;
    }

    public void draw(Camera camera, HiRes16Color screen) {
        int y = 0;
        for (int row = camera.yPos; row < camera.viewHeight + camera.yPos; row++) {
            int x = 6;
            for (int col = camera.xPos; col < camera.viewWidth + camera.xPos; col++) {
                boolean t = getTile(row, col);
                if (t) {
                    if(col % 2 == 0)
                        if(row % 2 == 0)
                            imgGround.draw(screen, x, y);
                        else 
                            imgGround2.draw(screen, x, y);
                    else 
                        if(row % 2 == 0)
                            imgGround2.draw(screen, x, y);
                        else
                            imgGround.draw(screen, x, y);
                } else {
                    if (getTile(row - 1, col) || getTile(row + 1, col) || 
                        getTile(row, col - 1) || getTile(row, col + 1) || 
                        getTile(row - 1, col - 1) || getTile(row + 1, col - 1) || 
                        getTile(row - 1, col + 1) || getTile(row + 1, col + 1)) {

                        imgWall.draw(screen, x, y); // TODO: only draw wall if one floor tile is near it
                    }
                }
                x += imgWall.width();
            }
            y += imgWall.height();
        }

        TileObject obj = firstObj;
        while (obj != null) {
            if (obj.isOnScreen(camera)) {
                int x = (obj.xCoord - camera.xPos) * 16 + 6;
                int y = (obj.yCoord - camera.yPos) * 16;
                switch (obj.type) {
                    case 0:
                        if(GameData.getInstance().charSel == 0)
                            imgPlayer.draw(screen, x, y);
                        else if(GameData.getInstance().charSel == 1)
                            imgPlayer2.draw(screen, x, y);
                        else 
                            imgPlayer3.draw(screen, x, y);
                        break;
                    case 1:
                        imgChest.draw(screen, x, y);
                        break;
                    case 2:
                        imgDoor.draw(screen, x, y);
                        break;
                    case 3:
                        imgSkelli1.draw(screen, x, y);
                        break;
                    case 4:
                        imgHpPot.draw(screen, x, y);
                        break;
                    case 5: 
                        imgMpPot.draw(screen, x, y);
                        break;
                    case 6:
                        imgStairs.draw(screen, x, y);
                        break;
                        
                    case 10:
                        imgSkelli1.draw(screen, x, y);
                        break;
                    case 11:
                        imgSkelli2.draw(screen, x, y);
                        break;
                    case 12:
                        imgSkelli3.draw(screen, x, y);
                        break;
                    case 13:
                        imgSkelli4.draw(screen, x, y);
                        break;
                    case 14:
                        imgSkelli5.draw(screen, x, y);
                        break;
                    case 15:
                        imgSkelli6.draw(screen, x, y);
                        break;
                    case 16:
                        imgSkelli7.draw(screen, x, y);
                        break;

                    case 18:
                        imgZombie1.draw(screen, x, y);
                        break;
                    case 19:
                        imgZombie2.draw(screen, x, y);
                        break;
                        
                    case 20:
                        imgDemon1.draw(screen, x, y);
                        break;
                    case 21:
                        imgDemon2.draw(screen, x, y);
                        break;
                        
                    case 100:
                        // Tygro is a bit bigger, so drawing may be more comlicated..
                        imgTygro.draw(screen, x, y);
                        break;

                        
                    default:
                        break;  
                    
                    // stupid code.. how did this happen? o__O
                }

            }
            obj = obj.next;
        }
    }

}