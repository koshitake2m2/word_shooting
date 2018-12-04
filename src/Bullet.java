
public class Bullet {
    // 飛ばす文字列
    private char c;
    // 座標および移動距離
    private int x;
    private int y;
    private int dx;
    private int dy;
    // 配置されたレーンの番号
    private int lane_n;

    private final static int WIDTH = 80;
    private final static int HEIGHT = 24;

    public Bullet(char c, int x,int y,int dx,int dy, int lane_n){
        this.c = c;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.lane_n = lane_n;
    }
    // getter and setter
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void resetY(int y){
        this.y = y;
    }
    public char getC(){
        return this.c;
    }
    public int getLaneN(){
        return this.lane_n;
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
    // 弾丸の座標および移動距離をリセット
    public void resetBullet(int x, int y, int dx, int dy, int lane_n){
        this.x = x;
        this.dx = dx;
        this.dy = dy;
        setLaneN(lane_n);
    }

    // 弾丸の位置を更新
    public void update(){
        x += dx;
        y += dy;
        if (!insideOfScreen(WIDTH, HEIGHT)) {
        }
    }
    // 弾丸が画面内にあるかチェック
    public boolean insideOfScreen(int w, int h){
        return 0<=x && x<w && 0<=y && y<h;
    }
}
