
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class Model {

    private ConsoleView view;
    private ConsoleController controller;
    private final static Random random = new Random(System.currentTimeMillis());

    private final static int WIDTH = 80;
    private final static int HEIGHT = 24;

    private GameManager gm;
    private String which_show;

    public Model() {
        gm = new GameManager();
        // viewの前にgmを初期化しなければならない
        view = new ConsoleView(this);
        controller = new ConsoleController(this);
        which_show = "start";
    }

    public synchronized void process(String event) {
        // Enterキー入力の時は event == "" のため省く
        if(event.length() != 0){
            switch(which_show){
                case "start":
                    showStart(event);
                    break;
                case "game":
                    showGame(event);
                    break;
                case "result":
                    showResult(event);
                    break;
            }
        }
    }
    public void run() throws IOException {
        controller.run();
    }

    // GameManager
    public GameManager getGM(){
        return gm;
    }
    public void setWhichShow(String str){
        which_show = str;
    }

    // スタート画面
    public void showStart(String event){
        if (!event.equals("TIME_ELAPSED")){
            char typeChar = event.charAt(0);
            switch(typeChar){
                case 'g':
                    which_show = "game";
                    break;
                case 'x':
                    exitGame();
                    break;
            }
        }
        view.drawStart();
    }

    // リザルト画面
    public void showResult(String event){
        if (!event.equals("TIME_ELAPSED")){
            char typeChar = event.charAt(0);
            switch(typeChar){
                case 'g':
                    gm.resetGameManager();
                    which_show = "game";
                    break;
                case 'x':
                    exitGame();
                    break;
            }
        }
        view.drawResult();
    }

    // ゲーム画面
    public void showGame(String event){
        if (event.equals("TIME_ELAPSED")){
            gm.updateSelectableWord();
            gm.updateBullets();
            if(gm.getPlayerHP() <= 0
            || gm.getRemainingTime() <= 0){
                gm.setRankingData();
                which_show = "result";
            }
        } else {
            char typeChar = event.charAt(0);
            if(typeChar == '('
            || typeChar == '<'
            || typeChar == '['
            || typeChar == ')'
            || typeChar == '>'
            || typeChar == ']'
            ){
                gm.moveTargetLine(typeChar);
            }else{
                gm.getTargetWord().nextCharIs(typeChar);
            }
        }
        view.drawGame();
    }

    // 終了
    public void exitGame(){
        System.exit(0);
    }

    // main
    public static void main(String[] args) throws InterruptedException, IOException {
        Model model = new Model();
        model.run();

    }
    // testFunc()
    public static Boolean func(){
        return true;
    }

}
