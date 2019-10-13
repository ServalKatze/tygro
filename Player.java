class Player extends Enemy {
    public Attribute experience;
    public Attribute manaPoints;

    public Player() {
        level = 1;
        tileObj = new TileObject(0, 0, 0);
        experience = new Attribute(0, 100);
        hitPoints = new Attribute(20, 20);
        manaPoints = new Attribute(20, 20);
        attackRating = new Attribute(5, 5);
        defenseRating = new Attribute(5, 5);
    }

    public boolean addExperience(byte addVal) {
        byte newValue = experience.curValue + addVal;
        if (newValue >= experience.maxValue) {
            // levelup!
            level++;
            return true;
        }

        experience.increase(addVal);
        return false;
    }
}