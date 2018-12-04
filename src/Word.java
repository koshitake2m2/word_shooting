

// 単語をあらわすクラス
public class Word {
    // 単語の文字列
    private String str;
    // 次タイプするべき文字の場所を示す文字列の添字部分
    private int next_char_index;
    // 座標および移動距離
    private int x;
    private int y;
    private int dx;
    private int dy;
    // 移動にかかる時間
    private int time_to_move;
    private int time_count;
    // 配置されたレーンの番号
    private int lane_n;

    private final static int WIDTH = 80;
    private final static int HEIGHT = 24;

    // コンストラクタ
    public Word(String str, int x,int y,int dx,int dy){
        this.str = str;
        next_char_index = 0;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.time_to_move = 2;
        this.time_count = 0;
        this.lane_n = 0;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void resetY(int y){
        this.y = y;
    }
    public String getStr(){
        return this.str;
    }
    public int getLength(){
        return str.length();
    }
    public int getNextCharIndex(){
        return next_char_index;
    }
    public void incrementCharIndex(){
        next_char_index++;
    }
    public void resetTimeToMove(int ttm){
        this.time_to_move = ttm;
    }
    public void setLaneN(int n){
        this.lane_n = n;
        int y = HEIGHT/2-3;
        switch(n){
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
        resetY(y);
    }

    public boolean nextCharIs(char c){
        if (next_char_index < getLength() && str.charAt(next_char_index) == c) {
            next_char_index++;
            return true;
        } else {
            return false;
        }
    }
    public boolean finishWord(){
        return next_char_index == str.length();
    }

    public void update(){
        time_count++;
        if( time_count == time_to_move ) {
            time_count = 0;
            x += dx;
            y += dy;
        }
        if (!insideOfScreen(WIDTH, HEIGHT)) {
        }
    }


    public boolean insideOfScreen(int w, int h){
        return 0<=x && x<w && 0<=y && y<h;
    }

}
