import java.util.Arrays;
import femto.mode.HiRes16Color;


class TileMap {
    byte width;
    byte height;
    int mapSize;

    private static final ImgWall imgWall = new ImgWall();
    private static final ImgGround imgGround = new ImgGround();
    private static final ImgGround2 imgGround2 = new ImgGround2();
    private static final ImgTyger imgPlayer = new ImgTyger();
    private static final ImgChest imgChest = new ImgChest();
    private static final ImgDoor imgDoor = new ImgDoor();
    private static final ImgEnemy imgEnemy = new ImgEnemy();
    private static final ImgHealthPotion imgHpPot = new ImgHealthPotion();
    private static final ImgManaPotion imgMpPot = new ImgManaPotion();

    boolean[] terrainData;
    TileObject firstObj;
    public TileMap(byte width, byte height) {
        this.width = width;
        this.height = height;
        mapSize = width * height;
        terrainData = new boolean[mapSize];
        for (int i = 0; i < mapSize; i++) {
            terrainData[i] = false;
        }

        firstObj = null;
    }

    // object management... 
    public void addObject(TileObject obj) {
        if (firstObj == null) {
            firstObj = obj;
        } else {
            obj.next = firstObj;
            firstObj.prev = obj;
            firstObj = obj;
        }
    }

    public void remObject(TileObject obj) {

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
        return null;
    }

    // enemy management... a fancy linkedlist class would be nicer...
    public void addEnemy(byte enemyType, byte xCoord, byte yCoord) {
        switch (enemyType) {
            case 1:
            default:
                addEnemy(new Enemy(1, 10, 1, 2), 3, xCoord, yCoord);
                break;
        }
    }

    public void addEnemy(Enemy enemy, byte tileType, byte xCoord, byte yCoord) {
        TileObject to = new TileObject(tileType, xCoord, yCoord, enemy);
        addObject(to);
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
                    if (getTile(col - 1, row) || getTile(col + 1, row) || getTile(col, row - 1) || getTile(col, row + 1) || getTile(col - 1, row - 1) || getTile(col + 1, row - 1) || getTile(col - 1, row + 1) || getTile(col + 1, row + 1)) {

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
                        imgPlayer.draw(screen, x, y);
                        break;
                    case 1:
                        imgChest.draw(screen, x, y);
                        break;
                    case 2:
                        imgDoor.draw(screen, x, y);
                        break;
                    case 3:
                        imgEnemy.draw(screen, x, y);
                        break;
                    case 4:
                        imgHpPot.draw(screen, x, y);
                        break;
                    case 5: 
                        imgMpPot.draw(screen, x, y);
                        break;
                    default:
                        break;
                }

            }
            obj = obj.next;
        }
    }

}