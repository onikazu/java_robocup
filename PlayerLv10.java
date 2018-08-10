import java.util.*;
import java.text.*;
//==============================================
//守備位置へ移動する
class PlayerLv10 extends PlayerLv09 {
    protected boolean m_debugLv10 = false;
    protected LinkedList m_listCommand;
    public PlayerLv10() {
        super();
        m_listCommand = new LinkedList();
    }
    //==============================================
    //角度を計算する
    protected double getDirection(double x0, double y0, double x1, double y1) {
        //x1-x0が0に近く、計算誤差が大きくなりすぎるのを防ぐ
        if (Math.abs(x1 - x0) < 0.1) {
            if (y1 - y0 > 0) return 90.0;
            else             return -90.0;
        } else return Math.toDegrees(Math.atan2(y1 - y0, x1 - x0));
    }
    //==============================================
    //命令を作る(ボールが遠いとき)
    protected String getCommandAsDefence(String message,double ballDist,double ballDir) {
        super.getCommandAsDefence(message, ballDist, ballDir);
        //距離が近いときは何もしない
        int i;
        double dist = getDistance(m_dDefenceX, m_dDefenceY, m_dX, m_dY);
        if (dist < 2.0)
            return "";
        if (m_dNeck == OUT_OF_RANGE)
            return "";
        //回転する角度を計算する
        double dir = getDirection(m_dX, m_dY, m_dDefenceX, m_dDefenceY);
        double moment = normalizeAngle(dir - m_dNeck);
        if (m_debugLv10) {
            DecimalFormat f = new DecimalFormat("###0.00");
            System.out.println();
            System.out.print("X=" + f.format(m_dX) + " Y=" + f.format(m_dY));
            System.out.print(" ballX=" + f.format(m_dBallX)
                           + " ballY=" + f.format(m_dBallY));
            System.out.print(" defX=" + f.format(m_dDefenceX)
                           + " defY=" + f.format(m_dDefenceY));
            System.out.print(" Dir=" + f.format(dir));
            System.out.print(" Neck=" + f.format(m_dNeck));
            System.out.print(" moment=" + f.format(moment));
        }
        //回転角度が小さすぎるか検査する
        if (Math.abs(moment) < 20.0) {
            //回転せず前進する
            return "(dash 60)";
        }
        //真後ろに目標地点があるか検査する
        else if (Math.abs(moment) > 160.0) {
            //回転せず後進する
            m_listCommand.clear();
            for(i=0;i<4;i++) m_listCommand.addLast("(dash -40)");
            return "(dash -30)";
        }
        //そうでなければ
        else {
            //回転して前進する
            m_listCommand.clear();
            for(i=0;i<6;i++) m_listCommand.addLast("(dash 70)");
            return "(turn " + moment + ")";
        }
    }
    //==============================================
    //ボールを蹴る
    protected String kick(String message) {
        //ゴールが見えるときは緊急避難的に蹴る
        String goal = "(g l)";
        if (m_strSide.startsWith("r")) goal = "(g r)";
        if (message.indexOf(goal) > -1) return "(kick 100 180)";
        return super.kick(message);
    }
    //==============================================
    //命令を作る
    protected void play(String message) {
        //コマンドの予約がなければコマンドの予約を作成する
        if (m_listCommand.size() == 0) {
            super.play(message);
        }
    }
    //==============================================
    //サーバから受け取ったメッセージを処理する
    protected void analyzeMessage(String message) {
        //いままで開発した内容を実行する
        super.analyzeMessage(message);
        if (message.startsWith("(sense")) {
            //体調メッセージを受信したら、コマンドの予約があるか検査する
            if (m_listCommand.size() > 0) {
                //コマンドの予約を先頭から一つ選び実行する
                String command = m_listCommand.removeFirst().toString();
                send(command);
            }
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv10[] player1 = new PlayerLv10[11];
        Thread[] thread1 = new Thread[11];
        int i;
        for (i = 0; i < 11; i++) {
            String teamname = "Lv10";
            player1[i] = new PlayerLv10();
            thread1[i] = new Thread(player1[i]);
            player1[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread1[i].start();
        }
        PlayerLv09[] player2 = new PlayerLv09[11];
        Thread[] thread2 = new Thread[11];
        for (i = 0; i < 11; i++) {
            String teamname = "Lv09";
            player2[i] = new PlayerLv09();
            thread2[i] = new Thread(player2[i]);
            player2[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread2[i].start();
        }
        player1[1].m_debugLv10 = true;
        System.out.println("試合への登録終了");
    }
}
