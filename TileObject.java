class TileObject {
    public byte type;
    public byte xCoord;
    public byte yCoord;
    public TileObject next;
    public TileObject prev;
    public Enemy data;

    public TileObject(byte type, byte xCoord, byte yCoord) {
        this.type = type;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.prev = null;
        this.next = null;
        this.data = null;
    }

    public TileObject(byte type, byte xCoord, byte yCoord, Enemy data) {
        this.type = type;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.prev = null;
        this.next = null;
        this.data = data;
    }

    public void moveLeft() {
        xCoord--;
    }

    public void moveRight() {
        xCoord++;
    }

    public void moveUp() {
        yCoord--;
    }

    public void moveDown() {
        yCoord++;
    }

    public void move(byte moveDir) {
        switch (moveDir) {
            case 1:
                moveUp();
                break;
            case 2:
                moveDown();
                break;
            case 3:
                moveLeft();
                break;
            case 4:
                moveRight();
                break;
            default:
                break;
        }
    }

    public boolean isOnScreen(Camera camera) {
        return xCoord >= camera.xPos && xCoord <= camera.xPos + camera.viewWidth &&
            yCoord >= camera.yPos && yCoord < camera.yPos + camera.viewHeight;
    }

    public boolean isEnemy() {
        return this.data != null;
    }
}