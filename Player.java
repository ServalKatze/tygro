 class Player extends Enemy {
    public Attribute experience;
    public Attribute manaPoints;
    
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
            level = level + 1;
            
            // increase attributes!
            hitPoints.maxValue = hitPoints.maxValue + 10;
            manaPoints.maxValue = manaPoints.maxValue+ 5;
            
            // heal!
            hitPoints.curValue = hitPoints.maxValue;
            manaPoints.curValue = hitPoints.maxValue;
            
            // new exp requirements
            experience.curValue = newValue - experience.maxValue;
            experience.maxValue = level * 15;
            if(experience.curValue > experience.maxValue) 
                experience.curValue = experience.maxValue - 1;
            
            return true;
        } 
        
        experience.increase(addVal);
        return false;
    }
    
    public boolean applyHealthPotion() {
        int min = hitPoints.maxValue / 4;
        int max = hitPoints.maxValue / 3;
        hitPoints.increase((byte) Math.random(min, max));
        return true;  // maybe the player could be blocke d from using potions..?
    }
    
    public boolean applyManaPotion() {
        int min = manaPoints.maxValue / 4;
        int max = manaPoints.maxValue / 3;
        manaPoints.increase((byte) Math.random(min, max));
        return true;
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