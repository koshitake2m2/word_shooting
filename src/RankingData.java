
public class RankingData {
    private int score;
    private String date;

    // コンストラクタ
    public RankingData(int score, String date){
        this.score = score;
        this.date = date;
    }
    // getter
    public int getScore(){
        return score;
    }
    public String getDate(){
        return date;
    }
    public String makeRankingDataFormat(){
        return String.valueOf(getScore()) + "," + getDate();
    }
}
