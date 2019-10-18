import femto.mode.HiRes16Color;
import femto.Game;
import femto.State;
import femto.input.Button;
import femto.palette.Pico8;
import femto.font.TIC80;

class MapScreen extends State {
    HiRes16Color screen;
    TileMap tileMap;
    Camera camera;
    String statusMsg;
    Player player;
    byte curMoveDir;
    byte moveTimer;

    public void preinit() {
        statusMsg = "";
        curMoveDir = 0;
        moveTimer = 0;
    }

    public void init() {
        screen = new HiRes16Color(Pico8.palette(), TIC80.font());
        GameData data = GameData.getInstance();
        
        tileMap = data.tileMap;
        camera = data.camera;
        statusMsg = data.statusMsg;
        player = data.player;
    }

    /// @return true if attacked entity is dead
    public boolean attack(Player player, Enemy enemy, boolean isPlayerAttack) {
        if (isPlayerAttack) {
            int maxDamage = player.attackRating.curValue - enemy.defenseRating.curValue;
            if (maxDamage < 1) maxDamage = 1;
            // roll damage between 1 and maxDamage
            enemy.hitPoints.increase(-maxDamage);
            return enemy.isDead();
        } else {
            int maxDamage = enemy.attackRating.curValue - player.defenseRating.curValue;
            if (maxDamage < 1) maxDamage = 1;
            // roll damage between 1 and maxDamage
            player.hitPoints.increase(-maxDamage);
            return player.isDead();
        }

        return false;
    }

    public void updateLogic() {
        if (player.moveDir > 0) {
            byte xCoord = player.tileObj.xCoord;
            byte yCoord = player.tileObj.yCoord;

            switch (player.moveDir) {
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

            TileObject obj = tileMap.getObject(yCoord, xCoord);
            if (obj != null) {
                switch (obj.type) {
                    // CHEST
                    case 1:
                        // TODO: generate and add loot from chest to player inventory
                        tileMap.remObject(obj);
                        statusMsg = Messages.loot;
                        break;
                        // DOOR 
                    case 2:
                        tileMap.remObject(obj);
                        statusMsg = Messages.door;
                        break;
                        // ENEMY
                    case 3:
                        if (attack(player, (Enemy) obj.data, true)) {
                            statusMsg = Messages.enemyDead;
                            tileMap.remObject(obj);
                            // loot, ep
                        } else {
                            statusMsg = Messages.enemy;
                        }
                        // yes that should be done further down.. just testing, k?
                        //if(attack(player, (Enemy) obj.data, false)) {
                        // you're ded. 
                        //tileMap.remObject(player.tileObj);
                        //}
                        break;
                    default:
                        statusMsg = Messages.blocked;
                        break;
                }

            } else if (tileMap.isBlocking(yCoord, xCoord)) {
                statusMsg = Messages.blocked;
            } else {
                player.tileObj.move(player.moveDir);
            }

            player.stop();

            // player moved - update enemies
            // foreach visible enemy
            // if next to player, then attack
            // otherwise move a non-blocking tile closer to player 

            obj = tileMap.firstObj;
            while (obj != null) {
                if (obj.isEnemy() && obj.isOnScreen(camera)) {
                    byte xDiff = obj.xCoord - player.tileObj.xCoord;
                    byte yDiff = obj.yCoord - player.tileObj.yCoord;
                    byte moveDir = 0;
                    byte newX = obj.xCoord + (xDiff > 0 ? -1 : 1);
                    byte newY = obj.yCoord + (yDiff > 0 ? -1 : 1);

                    // TODO: xDiff=0 and yDiff = 1 -> next to player ==> attack
                    if (xDiff == 0 && (yDiff == 1 || yDiff == -1) ||
                        yDiff == 0 && (xDiff == 1 || xDiff == -1)) {
                        if (attack(player, (Enemy) obj.data, false)) {
                            // you're ded. 
                            tileMap.remObject(player.tileObj);
                        }
                    } else if (xDiff != 0 && !tileMap.isBlocking(obj.yCoord, newX)) {
                        moveDir = xDiff > 0 ? 3 : 4;
                    } else if (yDiff != 0 && !tileMap.isBlocking(newY, obj.xCoord)) {
                        moveDir = yDiff > 0 ? 1 : 2;
                    } else if (moveDir == 0) {
                        newX = obj.xCoord + (xDiff < 0 ? -1 : 1);
                        newY = obj.yCoord + (yDiff < 0 ? -1 : 1);

                        if (!tileMap.isBlocking(obj.yCoord, newX)) {
                            moveDir = xDiff < 0 ? 3 : 4;
                        } else if (!tileMap.isBlocking(newY, obj.xCoord)) {
                            moveDir = yDiff < 0 ? 1 : 2;
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

        if (Button.B.justPressed())
            //Game.changeState(new Main());
            Game.changeState(new InventoryScreen());
        else if (player.isDead())
            statusMsg = Messages.youDied;
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
                player.moveUp();
            else if (curMoveDir == 2)
                player.moveDown();
            else if (curMoveDir == 3)
                player.moveLeft();
            else if (curMoveDir == 4)
                player.moveRight();
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
        camera.xPos = player.tileObj.xCoord - 7;
        camera.yPos = player.tileObj.yCoord - 5;

        tileMap.draw(camera, screen);

        // status line
        screen.setTextColor(3);
        screen.setTextPosition(2, 168);
        screen.print(statusMsg);

        // player exp
        // FIXME: probably no need for floats here, but eh... 
        float width = 219.0 * player.experience.getRatio();
        screen.drawRect(0, 161, 219, 2, 1, true);
        screen.drawHLine(1, 162, (int) width, 3);

        // player hp
        screen.drawRect(0, 163, 109, 3, 11, true);
        width = 109.0 * player.hitPoints.getRatio();
        screen.drawHLine(1, 164, (int) width, 9);
        screen.drawHLine(1, 165, (int) width, 9);

        // player mp/stamina
        screen.drawRect(110, 163, 109, 3, 13, true);
        width = 109.0 * player.manaPoints.getRatio();
        screen.drawHLine(111, 164, (int) width, 12);
        screen.drawHLine(111, 165, (int) width, 12);

        screen.setTextPosition(0, 0);
        screen.print(java.lang.Runtime.getRuntime().freeMemory());
        //screen.print(screen.fps();

        screen.flush();
    }

    public void shutdown() {
        screen = null;
    }
}