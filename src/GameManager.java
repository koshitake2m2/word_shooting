

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class GameManager {

    private ConsoleView view;
    private Model model;
    private final static Random random = new Random(System.currentTimeMillis());
    private final static int WIDTH = 80;
    private final static int HEIGHT = 24;
    private final static int LINE_OF_DEFENSE_X = 10;
    private final static String WORDS_TXT = "words.txt";
    private final static String RANKING_DATA_LIST_CSV = "ranking_data_list.csv";
    private final static String INSTRUCTIONS_TXT = "instructions.txt";
    private final static int COUNT_OF_LANES = 3;
    private final static int NUM_OF_PLAYER_HP = 5;
    private final static int TIME_LIMIT = 180;

    private int player_hp;
    private int total_score;
    private int next_word_index;
    private LinkedList<Word> selectable_words;
    private LinkedList<Word> words;
    private int select_word_index;
    private Word target_word;
    private int target_lane_n;
    private LinkedList<Bullet> bullets;
    private ArrayList<RankingData> ranking_data_list;
    private ArrayList<String> instructions;
    // 時間
    private long startTime;
    private long endTime;

    // コンストラクタ
    public GameManager(){
        resetGameManager();
    }

    // 全ての属性をリセット
    public void resetGameManager(){
        player_hp = NUM_OF_PLAYER_HP;
        total_score = 0;
        next_word_index = 0;
        select_word_index = 0;
        words = new LinkedList<Word>();
        readWords();
        target_lane_n = 0;
        selectable_words = setSelectableWord();
        target_word = selectable_words.get(select_word_index);
        bullets = setBullets();
        ranking_data_list = new ArrayList<RankingData>();
        instructions = new ArrayList<String>();
        setInstructions();
        startTime = System.currentTimeMillis();
    }

    // file_nameから１行ずつ読み込んでLinkedListに格納していき、それを返す
    public void readWords(){
        LinkedList<String> strings = new LinkedList<String>();
        try (BufferedReader in = new BufferedReader(new FileReader(new File(WORDS_TXT))) ) {
            String line;
            while((line = in.readLine()) != null){
                strings.add(line);
            }
            in.close();
            Collections.shuffle(strings);
            for (String str : strings) {
                Word w = new Word(str, WIDTH, 12, -1, 0);
                words.add(w);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // 説明書を読み込む
    public void setInstructions(){
        try (BufferedReader in = new BufferedReader(new FileReader(new File(INSTRUCTIONS_TXT))) ) {
            String line;
            while((line = in.readLine()) != null){
                instructions.add(line);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    // getter
    public int getPlayerHP(){ return player_hp; }
    public int getTotalScore() { return total_score; }
    public int getSelectWordIndex() { return select_word_index; }
    public int getNextWordIndex() { return next_word_index; }
    public LinkedList<Word> getSelectableWords() { return selectable_words; }
    public LinkedList<Word> getWords() { return words; }
    public Word getTargetWord(){ return target_word; }
    public int getTargetLaneN(){
        return target_lane_n;
    }
    public LinkedList<Bullet> getBullets(){ return bullets; }
    public ArrayList<RankingData> getRankingDataList(){ return ranking_data_list; }
    public ArrayList<String> getInstructions(){ return instructions; }

    // 最初に行う操作：レーンに出現する文字列をセット
    public LinkedList<Word> setSelectableWord(){
        LinkedList<Word> new_selectable_words = new LinkedList<Word>();
        for(int i = 0; i < COUNT_OF_LANES; i++) {
            Word word = words.get(i);
            setTimeToMove(word);
            word.setLaneN(i);
            next_word_index++;
            new_selectable_words.add(word);
        }
        return new_selectable_words;
    }

    // 文字列の移動時間をセット
    public void setTimeToMove(Word word){
        int ttm = 1;
        if (next_word_index < COUNT_OF_LANES) {
            ttm = word.getLength()/2+1;
        } else {
            ttm = word.getLength()/(total_score/30000+2)+1;
        }
        word.resetTimeToMove(ttm);
    }

    // 最初に行う操作：打つ前に空の弾丸をセット
    public LinkedList<Bullet> setBullets(){
        LinkedList<Bullet> new_bullets = new LinkedList<Bullet>();
        for(int i = 0; i < COUNT_OF_LANES; i++){
            Bullet bullet = new Bullet('*', -1, -1, 0, 0, 0);
            new_bullets.add(bullet);
        }
        return new_bullets;
    }

    // Nレーンの文字列を新しい文字列に更新する
    public void makeNewWordInLane(int n){
        next_word_index = (next_word_index+1) % words.size();
        Word new_word = words.get(next_word_index);
        new_word.setLaneN(n);
        setTimeToMove(new_word);
        selectable_words.set(n, new_word);
    }

    // 選択可能文字列の更新
    public void updateSelectableWord(){
        for (int i= 0; i < COUNT_OF_LANES; i++){
            Word word = selectable_words.get(i);
            word.update();
            // 文字列がすべて打ち終わっていたら新しい文字列へ
            if(word.finishWord()){
                // 弾丸発射
                shootBulletInLane(i);
                word.incrementCharIndex();
            }
            // 文字列の先頭が防衛線に到達した時
            if(word.getX() <= LINE_OF_DEFENSE_X){
                player_hp--;
                this.makeNewWordInLane(i);
            }
        }
        target_word = selectable_words.get(select_word_index);
    }

    // 選択文字列を変更
    public void moveTargetLine(char c){
        switch(c){
            case '(':
            case '<':
            case '[':
                if(select_word_index == 0) break;
                select_word_index--;
                target_lane_n--;
                break;
            case ')':
            case '>':
            case ']':
                if(select_word_index == 2) break;
                select_word_index++;
                target_lane_n++;
                break;
        }
        target_word = selectable_words.get(select_word_index);
    }

    // 弾丸の位置を更新
    public void updateBullets(){
        for (int i = 0; i < COUNT_OF_LANES; i++){
            Bullet b = bullets.get(i);
            b.update();
            Word word = selectable_words.get(i);
            if(b.getX() >= word.getX()){
                total_score += 1000 * word.getLength();
                this.makeNewWordInLane(i);
                b.resetBullet(-1, 0, 0, 0, i);
            }
        }
    }

    // 弾丸を発射
    public void shootBulletInLane(int n){
        Bullet b = bullets.get(n);
        b.resetBullet(LINE_OF_DEFENSE_X, 0, 1, 0, n);
    }

    // 現在の日付時間取得
    public String getDateTime(){
        LocalDateTime d = LocalDateTime.now();
        DateTimeFormatter dtf =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String s = dtf.format(d);
        return s;
    }

    // ランキングのファイルを読み込んでランキングを更新
    public void setRankingData(){
        RankingData ranking_data_this_time = new RankingData(total_score, getDateTime());
        String ranking_data_line = ranking_data_this_time.makeRankingDataFormat();
        ranking_data_list.add(ranking_data_this_time);

        String file_name = RANKING_DATA_LIST_CSV;

        // ファイルがあったらファイルを読み込んでランキングを作成し、今回の結果をファイルに書き込む
        try (BufferedReader in = new BufferedReader(new FileReader(new File(file_name))) ) {
            String line;
            while((line = in.readLine()) != null){
                String[] strs = line.split(",");
                RankingData rd = new RankingData(Integer.parseInt(strs[0]), strs[1]);
                ranking_data_list.add(rd);
            }
            in.close();

            // テキストのデータをソートする
            ranking_data_list.sort((o1, o2) -> o2.getScore() - o1.getScore());

            // 今回のデータの書き込み
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file_name, true)));
                pw.println(ranking_data_line);
                pw.close();
            } catch (IOException ee) {
                ee.printStackTrace();
                System.exit(-1);
            }

        // ファイルが存在しなかったらファイルを作成し、今回の結果を書き込む
        } catch (FileNotFoundException e) {
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file_name)));
                pw.println(ranking_data_line);
                pw.close();
            } catch (IOException ee) {
                ee.printStackTrace();
                System.exit(-1);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    // 残り時間を取得
    public int getRemainingTime(){
        endTime = System.currentTimeMillis();
        int elapsed_time = (int)(endTime - startTime)/1000;
        int remaining_time = TIME_LIMIT - elapsed_time;
        return remaining_time;
    }

    // テスト用main関数
    public static void main(String[] args) {
        GameManager gm = new GameManager();
//        for (Word w : gm.getWords()) {
//            System.out.println(w.getStr());
//        }
        gm.setRankingData();
        for (RankingData rd : gm.getRankingDataList()) {
            System.out.println(rd.makeRankingDataFormat());
        }
        for(String str: gm.getInstructions()){
            System.out.println(str);
        }

    }



}
