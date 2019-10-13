class Enemy {
    public TileObject tileObj;
    public byte level;
    public byte moveDir;
    public Attribute hitPoints;
    public Attribute attackRating;
    public Attribute defenseRating;

    public Enemy() {}

    public Enemy(byte level, byte hp, byte atk, byte def) {
        this.level = level;
        this.hitPoints = new Attribute(hp, hp);
        this.attackRating = new Attribute(atk, atk);
        this.defenseRating = new Attribute(def, def);
    }

    public boolean isDead() {
        return hitPoints.curValue <= 0;
    }

    public void setCoord(byte xCoord, byte yCoord) {
        tileObj.xCoord = xCoord;
        tileObj.yCoord = yCoord;
    }

    public void moveUp() {
        moveDir = 1;
    }

    public void moveDown() {
        moveDir = 2;
    }

    public void moveLeft() {
        moveDir = 3;
    }

    public void moveRight() {
        moveDir = 4;
    }

    public void stop() {
        moveDir = 0;
    }
}