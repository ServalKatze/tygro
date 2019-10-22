class Player extends Enemy {
    public Attribute experience;
    public Attribute manaPoints;
    public Attribute hitPoints;
    public Attribute attackRating;
    public Attribute defenseRating;
    
    /*
    public Attribute strength;
    public Attribute intelligence;
    public Attribute agility;
    public Attribute vitality;
    */

    public Player() {
        level = 1;
        tileObj = new TileObject(0, 0, 0);
        experience = new Attribute(0, 100);
        
        hitPoints = new Attribute(20, 20);
        manaPoints = new Attribute(20, 20);
        
        attackRating = new Attribute(5, 5);
        defenseRating = new Attribute(5, 5);
        
        /*
        strength = new Attribute(2, 2);
        intelligence = new Attribute(2, 2);
        agility = new Attribute(2, 2);
        vitality = new Attribute(2, 2);
        */
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
    /*
    public String getStats() {
        return "    HP" + hitPoints.getStatLine(6) 
                    + "   MP"  + manaPoints.getStatLine(6) 
                    + "   AT" + attackRating.getPaddedValue(true, true, 5) + "\n"
             + "    ST" + strength.getPaddedValue(true, true, 4) 
                    + "  IN" + intelligence.getPaddedValue(true, true, 4) 
                    + "    AG" + agility.getPaddedValue(true, true, 4) 
                    + "  VI" + vitality.getPaddedValue(true, true, 4) 
                    + "    DF" + defenseRating.getPaddedValue(true, true, 5)+ "\n"
                    + "  L 9999999/9999999       99   99        $ 999999999";
        ;
        
    }
    */
}