
public class ConsoleView {
    private Model model;
    private GameManager gm;
    private char[][] screen; // screen[x][y]
    private int width;
    private int height;

    private final static int WIDTH = 80;
    private final static int HEIGHT = 24;
    private final static int LINE_OF_DEFENSE_X = 10;

    // コンストラクタ
    public ConsoleView(Model m) {
        this(m, WIDTH, HEIGHT);
    }
    public ConsoleView(Model m, int w, int h) {
        model = m;
        gm = model.getGM();
        width = w;
        height = h;
        screen = new char[width][height]; // (x, y)
        clear();
    }
    // スタート画面
    public void drawStart(){
        clear();
        drawTitle();
        drawInstructions();
        drawSelectBottun();
        paint();
    }
    // リザルト画面
    public void drawResult(){
        clear();
        drawTitle();
        drawTotalScoreInResult();
        drawRankingData();
        drawSelectBottun();
        paint();
    }

    // 画面上にタイトルを配置
    public void drawTitle(){
        drawFramedString(" word shooting ", '#', width/2-6, 2);
    }
    // 画面上にセレクトボタンを配置
    public void drawSelectBottun(){
        drawFramedString("start Game : g", '\'', width/4-3, height-2);
        drawFramedString("Exit : x", '\'', width*3/4-3, height-2);
    }
    // 画面上にトータルスコアを配置
    public void drawTotalScoreInResult() {
        drawString(String.format("Total Score : %d", gm.getTotalScore()), width/2-10, 6);
    }
    // 画面上に説明文を表示
    public void drawInstructions(){
        int x = 5;
        int y = 5;
        int i = 0;
        for(String output_line: gm.getInstructions()) {
            drawString(output_line, x, y+i);
            i++;
        }
    }

    // 画面上にランキングを配置
    public void drawRankingData(){
        int x = width/2-15;
        int y= 10;
        String output_line = String.format("%2s %11s %s", "", "Score", "Date");

        drawString("Ranking",width/2-3, y-2);
        drawString(output_line, x, y);
        int i = 1;
        for (RankingData rd: gm.getRankingDataList()){
            output_line = String.format("%2d %11d %s", i, rd.getScore(), rd.getDate());
            drawString(output_line, x, y+i);
            i++;
            if(i > 8){
                break;
            }
        }
    }

    // ゲーム画面更新
    public void drawGame(){
        clear();
        drawPlayerHP();
        drawRemainingTime();
        drawTotalScore();
        drawTargetWord();
        drawLane();
        drawTargetLane();
        drawWords();
        drawBullets();
        paint();
    }

    // 画面クリア
    public void clear(){
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                screen[x][y] = ' ';
            }
        }
    }
    // 画面描画
    public void paint(){
        for (int y = 0; y < height; y++){
            String str = "";
            for (int x = 0; x < width; x++){
                str += String.valueOf(screen[x][y]);
            }
            System.out.println(str);
        }
    }
    // 画面上の指定の座標に文字を配置
    public void put(char c, int x, int y){
        if(0<=x && x<width && 0<=y && y<height){
            screen[x][y] = c;
        }else{
            //System.out.printf("(%d, %d) is range out", x, y);
        }
    }
    // 画面上に文字列を配置
    public void drawString(String s, int x, int y){
        for (int i = 0; i < s.length(); i++){
            put(s.charAt(i), x+i, y);
        }
    }
    // 画面上に長方形を配置
    public void drawRect(char c, int x, int y, int w, int h){
        for (int i = 0; i < w; i++){
            for (int j=0; j < h; j++){
                if(i == 0 || i == w-1 || j == 0 || j == h-1){
                    put(c, x+i, y+j);
                }
            }
        }
    }
    // 画面上に長方形に囲まれた文字を配置
    public void drawFramedString(String s, char c, int x, int y){
        drawString(s, x, y);
        drawRect(c, x-1, y-1, s.length()+2, 3);
    }

    // 画面上にプレイヤーの体力を配置
    public void drawPlayerHP(){
        int x = 1;
        int y = 1;
        int w = 20;
        int h = 3;
        drawRect('\'', x, y, w, h);
        drawString("HP : ", x+2, y+1);
        for (int i = 1; i <= gm.getPlayerHP(); i++){
            drawString("* ", x+6+2*i, y+1);
        }
    }

    // 画面上に残り時間を表示
    public void drawRemainingTime(){
        int x = width/2-10;
        int y = 1;
        int w = 20;
        int h = 3;
        int seconds = gm.getRemainingTime();
        int minutes = seconds/60;
        seconds = seconds - minutes * 60;
        drawRect('\'', x, y, w, h);
        drawString(String.format("Time : %02d:%02d", minutes, seconds), x+2, y+1);
    }

    // 画面上にトータルスコアを配置
    public void drawTotalScore(){
        int x = width-21;
        int y = 1;
        int w = 20;
        int h = 3;
        String total_score = String.valueOf(gm.getTotalScore());
        drawRect('\'', x, y, w, h);
        drawString(String.format("Score : %s", total_score), x+2, y+1);
    }

    // 画面上にタイプ対象の文字列を配置
    public void drawTargetWord(){
        int x = width/2-20;
        int y = height-6;
        int w = 40;
        int h = 5;
        Word word = gm.getTargetWord();
        // ボックスの囲い
        drawRect('\'', x, y, w, h);
        // タイプ対象の文字列を配置
        drawString(word.getStr(), width/2-word.getLength()/2, y+2);
        // タイプする次の文字の下に印を配置
        if(word.getNextCharIndex() < word.getLength()){
            drawString("=", width/2-word.getLength()/2+word.getNextCharIndex(), y+3);
        }

    }

    // 画面上にレーンと防衛線を配置
    public void drawLane(){
        int x = width/2-20;
        int y = height/2-2;
        int w = width;
        int h = 1;

        drawRect('-', 0, y-3, width, 1);
        drawRect('-', 0, y  , width, 1);
        drawRect('-', 0, y+3, width, 1);
        drawRect('-', 0, y+6, width, 1);
        drawRect('#', LINE_OF_DEFENSE_X, y-3, 1, 10);
    }

    // 画面上に対象レーンの印を配置
    public void drawTargetLane(){
        int x = 5;
        int y = height/2-3;
        int target_lane_n = gm.getTargetLaneN();
        switch(target_lane_n) {
            case 0:
                y-=1;
                break;
            case 1:
                y+=2;
                break;
            case 2:
                y+=5;
                break;
        }
        drawString("->", x, y);
    }

    // 画面上に文字列を配置
    public void drawWord(Word word){
        int x = word.getX();
        int y = word.getY();
        drawString(word.getStr(), x, y);

    }

    // 画面上に選択可能な文字列を配置
    public void drawWords(){
        for(Word word : gm.getSelectableWords()){
            drawWord(word);
        }
    }

    // 画面上に弾丸を配置
    public void drawBullets(){
        for(Bullet b : gm.getBullets()){
            drawBullet(b);
        }
    }

    // 画面上に弾丸を配置
    public void drawBullet(Bullet b){
        int x = b.getX();
        int y = b.getY();
        put(b.getC(), x, y);
    }

}
