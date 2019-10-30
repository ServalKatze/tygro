
import femto.mode.HiRes16Color;
import femto.Game;
import femto.State;
import femto.input.Button;
import femto.palette.Pico8;

class EndScreen extends State {
    HiRes16Color screen;
    GameData data;

    public void preinit() {
    }

    public void init() {
        screen = new HiRes16Color(Pico8.palette(), femto.font.Tight.font());
        data = GameData.getInstance();
    }
    
    public void updateEvents() {
        //if (Button.B.justPressed()) {
        //    Game.changeState(new Main());
        //}
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
        
        drawText(60, 25, "Congratulations! You win!", 5);
        
        int x = 102;
        int y = 80;
        if(GameData.getInstance().charSel == 0)
            GameData.getInstance().tileMap.imgPlayer.draw(screen, x, y);
        else if(GameData.getInstance().charSel == 1)
            GameData.getInstance().tileMap.imgPlayer2.draw(screen, x, y);
        else 
            GameData.getInstance().tileMap.imgPlayer3.draw(screen, x, y);
        
        screen.flush();
    }
    
    private void drawText(int x, int y, String text, byte color) {
        screen.setTextColor(color);
        screen.setTextPosition(x, y);
        screen.print(text);
    }
}
