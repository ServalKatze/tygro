import femto.mode.HiRes16Color;
import femto.Game;
import femto.State;
import femto.input.Button;
import femto.palette.Pico8;
import femto.font.Tight;


class Main extends State {
    HiRes16Color screen;

    // start the game with an initial state
    public static void main(String[] args) {
        Game.run(Tight.font(), new Main());
    }

    // Avoid allocation in a State's constructor.
    // Allocate on init instead.
    void init() {
        screen = new HiRes16Color(Pico8.palette(), Tight.font());
    }

    // Might help in certain situations
    void shutdown() {
        screen = null;
    }

    // update is called by femto.Game every frame
    void update() {
        screen.clear(0);

        // Change to a new state when A is pressed
        if (Button.A.justPressed()) {
            Game.changeState(new MapScreen());
        }

        screen.setTextColor(5);
        screen.setTextPosition(95, 55);
        screen.print(Messages.title);
        
        GameData.getInstance().tileMap.imgPlayer.draw(screen, 79, 70);
        GameData.getInstance().tileMap.imgPlayer2.draw(screen, 104, 70);
        GameData.getInstance().tileMap.imgPlayer3.draw(screen, 129, 70);

        screen.setTextColor(3);
        screen.setTextPosition(0, 0);
        //screen.print(java.lang.Runtime.getRuntime().freeMemory());

        // Update the screen with everything that was drawn
        screen.flush();
    }

}