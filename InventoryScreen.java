import femto.mode.HiRes16Color;
import femto.Game;
import femto.State;
import femto.input.Button;
import femto.palette.Pico8;
import femto.font.TIC80;

class InventoryScreen extends State {
    HiRes16Color screen;
    String statusMsg;
    byte curMoveDir;
    byte moveTimer;
    GameData data;

    public void preinit() {
        statusMsg = "";
        curMoveDir = 0;
        moveTimer = 0;
    }

    public void init() {
        screen = new HiRes16Color(Pico8.palette(), TIC80.font());
        statusMsg = "Inventory";
        data = GameData.getInstance();
    }
    
    public void updateEvents() {
        if (Button.B.justPressed())
            Game.changeState(new MapScreen());
    }
    
    public void updateLogic() {
        
    }
    
    public void update() {
        // check inputs 
        updateEvents();

        // update game logic stuff 
        updateLogic();

        // draw stuff
        screen.clear(0);

        // status line
        screen.setTextColor(3);
        screen.setTextPosition(2, 168);
        screen.print(statusMsg);

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
        screen.print(java.lang.Runtime.getRuntime().freeMemory());
        //screen.print(screen.fps();

        screen.flush();
    }
}