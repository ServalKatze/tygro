/*
import femto.mode.HiRes16Color;
import femto.Game;
import femto.State;
import femto.input.Button;
import femto.palette.Pico8;

class InventoryScreen extends State {
    HiRes16Color screen;
    byte curMoveDir;
    byte moveTimer;
    GameData data;
    String statLines;
    String eqLines;
    String selLines;
    boolean updateStats;

    public void preinit() {
        curMoveDir = 0;
        moveTimer = 0;
        updateStats = true;
    }

    public void init() {
        screen = new HiRes16Color(Pico8.palette(), femto.font.Tight.font());

        data = GameData.getInstance();
        
        statLines =   "    999999/999999       999999/999999          99999\n"
                    + "    9999   9999         9999   9999            99999\n"
                    + "    9999999/9999999       99   99          999999999";
        
        eqLines = ""
            + "    99999     99999\n"
            + "    9999      9999\n"
            + "    9999      9999\n"
            + "    99999     99999\n"
            + "    9999999   Legendary";

        selLines = ""
            + "                         "
            + "    99999     99999\n"
            + "                         "
            + "    9999      9999\n"
            + "                         "
            + "    9999      9999\n" 
            + "                         "
            + "    99999     99999\n"
            + "                         "
            + "    9999999   Legendary";
            
    }
    
    public void updateEvents() {
        if (Button.B.justPressed())
            Game.changeState(new MapScreen());
    }
    
    public void updateLogic() {
        if(updateStats) {
            updateStats = false;
            statLines = data.player.getStats();
        }
    }
    
    public void update() {
        // check inputs 
        updateEvents();

        // update game logic stuff 
        updateLogic();

        // draw stuff
        screen.clear(0);
        
        // equpiment slots
        int x = 13;
        int y = 5;
        for(byte i=0; i<3; i = i + 1) {
            screen.drawRect(x, y, 15, 15, 5, true);
            y = y + 17;
        }
        
        x = 30;
        y = 5;
        for(byte i=0; i<3; i = i + 1) {
            screen.drawRect(x, y, 15, 15, 5, true);
            y += 17;
        }
        
        // inventory slots
        y = 4;
        for(byte row = 0; row < 3; row = row + 1) {
            x = 64;
            for(byte col = 0; col < 5; col = col + 1) {
                screen.drawRect(x, y, 15, 15, 1, true);
                x += 33;
            }
            y += 18;
        }

        
        // player stats
        screen.drawRect(9, 60, 204, 39, 1, true);
        drawText(12, 63, Messages.invCurStats, 3);
        drawText(0, 72, statLines, 3);
        
        // equipped item stats 
        screen.drawRect(9, 102, 102, 58, 1, true);
        drawText(12, 105, Messages.invEqItem, 3);
        drawText(0, 115, eqLines, 3);
        
        // selected item stats 
        screen.drawRect(111, 102, 103, 58, 1, true);
        drawText(114, 105, Messages.invSelItem, 3);
        drawText(0, 115, selLines, 3);
        
        // status line
        drawText(2, 168, Messages.invHint, 3);

        screen.setTextPosition(0, 0);
        screen.print(java.lang.Runtime.getRuntime().freeMemory());
        screen.flush();
    }
    
    private void drawText(int x, int y, String text, byte color) {
        screen.setTextColor(color);
        screen.setTextPosition(x, y);
        screen.print(text);
    }
}
*/