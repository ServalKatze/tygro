import femto.mode.HiRes16Color;
import femto.Game;
import femto.State;
import femto.input.Button;
import femto.palette.Pico8;
import femto.font.Tight;

class MapScreen extends State {
    HiRes16Color screen;
    byte curMoveDir;
    byte moveTimer;
    GameData data;
    byte tygroTicker;

    public void preinit() {
        curMoveDir = 0;
        moveTimer = 0;
        tygroTicker = 10;
    }

    public void init() {
        screen = new HiRes16Color(Pico8.palette(), Tight.font());
        data = GameData.getInstance();
        //data.generateMap(1);
    }

    /// @return true if attacked entity is dead
    public boolean attack(Player player, Enemy enemy, boolean isPlayerAttack) {
        if (isPlayerAttack) {
            int maxDamage = player.attackRating.curValue - enemy.defenseRating.curValue;
            if (maxDamage < 1) maxDamage = 1;
            // roll damage between 1 and maxDamage
            enemy.hitPoints.increase(-(Math.random(1, maxDamage + 1)));
            return enemy.isDead();
        } else {
            int maxDamage = enemy.attackRating.curValue - player.defenseRating.curValue;
            if (maxDamage < 1) maxDamage = 1;
            // roll damage between 1 and maxDamage
            player.hitPoints.increase(-(Math.random(1, maxDamage + 1)));
            return player.isDead();
        }

        return false;
    }
    
    public void updateLogic() {
        if (data.player.moveDir > 0) {
            byte xCoord = data.player.tileObj.xCoord;
            byte yCoord = data.player.tileObj.yCoord;

            switch (data.player.moveDir) {
                case 1:
                    yCoord--;
                    break;
                case 2:
                    yCoord++;
                    break;
                case 3:
                    xCoord--;
                    break;
                case 4:
                    xCoord++;
                    break;
                default:
                    break;
            }

            TileObject obj = data.tileMap.getObject(yCoord, xCoord);
            
            if (obj != null) {
                switch (obj.type) {
                    case 1:
                        // CHEST
                        // TODO: generate and add loot from chest to player inventory
                        data.tileMap.remObject(obj);
                        data.statusMsg = Messages.loot;
                        break;
                    case 2:
                        // DOOR 
                        data.tileMap.remObject(obj);
                        data.statusMsg = Messages.door;
                        break;
                    case 3: 
                    case 10: case 11: case 12: case 13: case 14: case 15: 
                    case 16: case 17: case 18: case 19: case 20: case 21: 
                    case 100:  // TYGRO is HUUUUGE!!
                        // ENEMY
                        if (attack(data.player, (Enemy) obj.data, true)) {
                            data.statusMsg = Messages.enemyDead;
                            data.tileMap.remObject(obj);
                            data.enemyMsg = data.tileMap.enemyCount + " enemies";
                            if(obj.type >= 100) {
                                data.statusMsg = "The mighty Tygro has been slain!";
                            }
                            if(data.tileMap.enemyCount <= 0) {
                                data.tileMap.addObject(new TileObject(6, xCoord, yCoord));
                            } else if(Math.random(1, 5) == 2) {
                                data.tileMap.addObject(new TileObject(4, xCoord, yCoord));
                            }
                            
                            if(data.player.addExperience(obj.data.level*10)) {
                                data.statusMsg = "Levelup! You are now level " + (int) data.player.level + "!";
                            }
                            // loot, ep
                        } else {
                            data.statusMsg = Messages.enemy;
                        }
                        break;
                    case 4: 
                        // HEALTH POTION
                        data.tileMap.remObject(obj);
                        data.player.applyHealthPotion();
                        data.statusMsg = Messages.potion;
                        break;
                    case 5: 
                        // MANA POTION
                        data.tileMap.remObject(obj);
                        data.player.applyManaPotion();
                        data.statusMsg = Messages.potion;
                        break;
                    case 6:
                        // STAIRS/PORTALS
                        if(data.isFinalLevel) {
                            Game.changeState(new EndScreen());
                        } else {
                            data.statusMsg = "Translocating to next level..";
                            data.level = data.level + 1;
                            data.generateMap();
                        }
                        break;
                    default:
                        data.statusMsg = Messages.blocked;
                        break;
                }

            } else if (data.tileMap.isBlocking(yCoord, xCoord)) {
                data.statusMsg = Messages.blocked;
            } else {
                data.player.tileObj.move(data.player.moveDir);
            }

            data.player.stop();

            // player moved - update enemies
            // foreach visible enemy
            // if next to player, then attack
            // otherwise move a non-blocking tile closer to player 

            obj = data.tileMap.firstObj;
            
            while (obj != null) {
                if (obj.isEnemy() && obj.isOnScreen(data.camera)) {
                    byte xDiff = obj.xCoord - data.player.tileObj.xCoord;
                    byte yDiff = obj.yCoord - data.player.tileObj.yCoord;
                    byte moveDir = 0;
                    byte newX = obj.xCoord + (xDiff > 0 ? -1 : 1);
                    byte newY = obj.yCoord + (yDiff > 0 ? -1 : 1);

                    // xDiff=0 and yDiff = 1 -> next to player ==> attack
                    if(obj == data.tileMap.tygro) {
                        if(newX > obj.xCoord) newX = newX + 1;
                        if(newY > obj.yCoord) newY = newY + 1;
                        
                        // attack 
                        if (xDiff == 0 && (yDiff == -2 || yDiff == 1) ||
                            yDiff == 0 && (xDiff == -2 || xDiff == 1) ||
                            xDiff == -1 && (yDiff == -2 || yDiff == 1) ||
                            yDiff == -1 && (xDiff == -2 || xDiff == 1)
                            
                            ) {
                            if (attack(data.player, (Enemy) obj.data, false)) {
                                // you're ded. 
                                data.tileMap.remObject(data.player.tileObj);
                            }
                        }  else if (xDiff != 0 && 
                                    !data.tileMap.isBlocking(obj.yCoord, newX) && 
                                    !data.tileMap.isBlocking(obj.yCoord + 1, newX)) {
                            moveDir = xDiff > 0 ? 3 : 4;
                        } else if (yDiff != 0 && 
                                    !data.tileMap.isBlocking(newY, obj.xCoord) && 
                                    !data.tileMap.isBlocking(newY, obj.xCoord + 1)) {
                            moveDir = yDiff > 0 ? 1 : 2;
                        }
                        tygroTicker = tygroTicker + 1;
                        if(tygroTicker > 10 && data.tileMap.enemyCount < 10) {
                            for(int i=0; i<10 && data.tileMap.enemyCount < 10; i++) {
                                if(!data.tileMap.isBlocking(0, i))
                                    data.tileMap.addEnemy(Math.random(1, 4), i, 0);
                            }

                            tygroTicker = 0;
                            data.statusMsg = "Tygro summons new minions!";
                            data.enemyMsg = data.tileMap.enemyCount + " enemies";
                        }

                    } else {
                        if (xDiff == 0 && (yDiff == 1 || yDiff == -1) ||
                            yDiff == 0 && (xDiff == 1 || xDiff == -1)) {
                            if (attack(data.player, (Enemy) obj.data, false)) {
                                // you're ded. 
                                data.tileMap.remObject(data.player.tileObj);
                            }
                        } else if (xDiff != 0 && !data.tileMap.isBlocking(obj.yCoord, newX)) {
                            moveDir = xDiff > 0 ? 3 : 4;
                        } else if (yDiff != 0 && !data.tileMap.isBlocking(newY, obj.xCoord)) {
                            moveDir = (yDiff > 0) ? 1 : 2;
                        } else if (moveDir == 0) {
                            newX = obj.xCoord + (xDiff < 0 ? -1 : 1);
                            newY = obj.yCoord + (yDiff < 0 ? -1 : 1);
    
                            if (!data.tileMap.isBlocking(obj.yCoord, newX)) {
                                moveDir = xDiff < 0 ? 3 : 4;
                            } else if (!data.tileMap.isBlocking(newY, obj.xCoord)) {
                                moveDir = yDiff < 0 ? 1 : 2;
                            }
                        }
                    }

                    obj.move(moveDir);
                }

                obj = obj.next;
            }

        }
    }


    public void updateEvents() {
        byte newMoveDir = 0;

        if (Button.B.justPressed()) {
            // maybe later...
            //Game.changeState(new Main());
            //Game.changeState(new InventoryScreen());
        }
        else if (data.player.isDead())
            data.statusMsg = Messages.youDied;
        else if (Button.Up.isPressed())
            newMoveDir = 1;
        else if (Button.Down.isPressed())
            newMoveDir = 2;
        else if (Button.Left.isPressed())
            newMoveDir = 3;
        else if (Button.Right.isPressed())
            newMoveDir = 4;

        if (newMoveDir == curMoveDir) {
            moveTimer++;
        } else {
            moveTimer = 15;
            curMoveDir = newMoveDir;
        }

        if (moveTimer >= 15) {
            moveTimer = 0;
            if (curMoveDir == 1)
                data.player.moveUp();
            else if (curMoveDir == 2)
                data.player.moveDown();
            else if (curMoveDir == 3)
                data.player.moveLeft();
            else if (curMoveDir == 4)
                data.player.moveRight();
        }
    }

    public void update() {
        // check inputs 
        updateEvents();

        // update game logic stuff 
        updateLogic();

        // draw stuff
        screen.clear(0);

        // center camera on player 
        data.camera.xPos = data.player.tileObj.xCoord - 7;
        data.camera.yPos = data.player.tileObj.yCoord - 5;

        data.tileMap.draw(data.camera, screen);

        // status line
        screen.setTextColor(3);
        screen.setTextPosition(2, 168);
        screen.print(data.statusMsg);

        // player exp
        // FIXME: probably no need for floats here, but eh... 
        float width = 219.0 * data.player.experience.getRatio();
        screen.drawRect(0, 161, 219, 2, 1, true);
        screen.drawHLine(1, 162, (int) width, 3);

        // player hp
        screen.drawRect(0, 163, 109, 3, 11, true);
        width = 109.0 * data.player.hitPoints.getRatio();
        screen.drawHLine(1, 164, (int) width, 9);
        screen.drawHLine(1, 165, (int) width, 9);

        // player mp/stamina
        screen.drawRect(110, 163, 109, 3, 13, true);
        width = 109.0 * data.player.manaPoints.getRatio();
        screen.drawHLine(111, 164, (int) width, 12);
        screen.drawHLine(111, 165, (int) width, 12);

        screen.setTextPosition(0, 0);
        screen.print(data.enemyMsg);
        
        screen.setTextPosition(0, 10);
        screen.print(java.lang.Runtime.getRuntime().freeMemory());

        screen.flush();
    }

    public void shutdown() {
        screen = null;
    }
}