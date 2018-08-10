//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//ボールキープを行う
public class PlayerLv26 extends PlayerLv25 {
    //メンバ:
    private boolean m_debugLv26 = false;
    protected int m_iPassTime = (int) OUT_OF_RANGE;
    protected boolean m_flagKeep = false;
    protected int m_iSayCount = 0; //連続したSayコマンドの禁止カウンタ
    protected double m_dPassX, m_dPassY; //パス目標の座標	
    //==============================================
    //座標がフィールド内にあるか調べる
    protected boolean checkInField(double x, double y) {
        if (Math.abs(x) < 57.5 && Math.abs(y) < 37.0)
            return true;
        else
            return false;
    }
    //==============================================
    //トラップメッセージを発信する
    protected void sayTrapMessage() {
        //トラップメッセージの作成
        int t = m_iTime;
        DecimalFormat f = new DecimalFormat("00");
        String str = "t " + f.format(m_dX[t + 1]) + " " + f.format(m_dY[t + 1]);
        String command = "(say \"" + str + "\")";
        if (!checkInField(m_dX[t + 1], m_dY[t + 1]))
            command = "";
        //発言カウントが0の時にメッセージを発信する
        if (m_iSayCount == 0) {
            send(command);
            m_iSayCount = 10; //トラップメッセージの間隔
            //デバッグ用出力
            if (m_strTeamName.startsWith("PlayerLv26")) {
                System.out.println();
                System.out.println("=======================");
                System.out.print("時刻 " + m_iTime + " ");
                System.out.print(
                    m_strTeamName + " " + m_iNumber + " " + command);
            }
        }
    }
    //==============================================
    //キックメッセージを発信する
    protected void sayKickMessage() {
        //キック目標座標を発声する
        int t = m_iTime;
        DecimalFormat f = new DecimalFormat("00");
        String command =
            "(say \"k "
                + f.format(m_dKickX[t])
                + " "
                + f.format(m_dKickY[t])
                + "\")";
        send(command);
        //デバッグ用出力
        if (m_strTeamName.startsWith("PlayerLv26")) {
            System.out.println();
            System.out.println("=======================");
            System.out.print("時刻 " + m_iTime + " ");
            System.out.print(m_strTeamName + " " + m_iNumber + " " + command);
        }
    }
    //==============================================
    //パス目標メッセージを発信する
    protected void sayPassMessage() {
        //パス目標座標を発信する
        int t = m_iTime;
        DecimalFormat f = new DecimalFormat("00");
        String command =
            "(say \"p " + f.format(m_dX[t]) + " " + f.format(m_dY[t]) + "\")";
        send(command);
        if (m_strTeamName.startsWith("PlayerLv26")) {
            System.out.print(" 発信 = " + command);
        }
    }
    //==============================================
    //ボールキープできる可能性を検査する
    protected boolean checkKeepable() {
        int t = m_iTime;
        if (m_listPlayer == null)
            return true;
        int i;
        //視界に見えている全ての選手に対して以下を行う
        for (i = 0; i < m_listPlayer.size(); i++) {
            //味方選手以外の選手が3,0m未満にいないか検査する
            String player = m_listPlayer.get(i).toString();
            if (player.indexOf("enemy") > -1) {
                double x = getParam(player, "x", 1);
                double y = getParam(player, "y", 1);
                double dist = getDistance(m_dX[t], m_dY[t], x, y);
                if (dist < 3.0) {
                    return false;
                }
            }
        }
        return true;
    }
    //==============================================
    //キックの目標を決定する
    protected void setKickTarget() {
        //いままで開発した内容を実行する
        super.setKickTarget();
        //ボールキープできる可能性を検査する
        m_flagKeep = checkKeepable();
        int t = m_iTime;
        boolean flagPlayOn = m_strPlayMode.startsWith("play_on");
        if (m_flagKeep == true && flagPlayOn == true) {
            //ボールをキープできるキック目標を設定する
            //自分の視線に入るようにキックする
            int next = (t + 1) % GAME_LENGTH;
            predict(t, next);
            m_dKickX[t] =
                m_dX[next] + 0.7 * Math.cos(Math.toRadians(m_dNeck[t]));
            m_dKickY[t] =
                m_dY[next] + 0.7 * Math.sin(Math.toRadians(m_dNeck[t]));
            m_iKickTime[t] = t + 1;
            //ボールキープ中の時はトラップメッセージを発言する
            sayTrapMessage();
        } else if (checkFresh(m_iPassTime) && checkKickable()) {
            //パスを行う
            m_dKickX[t] = m_dPassX;
            m_dKickY[t] = m_dPassY;
            double dist =
                getDistance(m_dBallX[t], m_dBallY[t], m_dPassX, m_dPassY);
            m_iKickTime[t] = m_iTime + getPassCount(dist);
            //キックメッセージを発言する
            sayKickMessage();
        }
    }
    //==============================================
    //パスが可能かを検査する
    protected boolean checkPassable(String player) {
        int t = m_iTime;
        //トラップ中の味方から自分へのパスの価値の情報を集める
        double x = getParam(player, "x", 1);
        double y = getParam(player, "y", 1);
        double value = getPassValue(x, y, m_dX[t], m_dY[t]);
        //パスを出す選手位置の価値を計算する
        double d = 0.0;
        if (m_strSide.startsWith("r")) {
            d = 100.0 - getDistance(x, y, -52.0, 0);
        } else {
            d = 100.0 - getDistance(x, y, 52.0, 0);
        }
        d = Math.max(d, 0.0);
        //パスの価値が現在のボールの位置の価値より大きいか検査する
        if (value > d)
            return true;
        else
            return false;
    }
    //==============================================
    //トラップ成功の聴覚メッセージを解析する
    protected void analyzeTrapMessage(int speaker_number, String content) {
        //トラップ成功の聴覚メッセージかどうか検査する
        if (!content.startsWith("t"))
            return;
        //デバッグ用出力
        if (m_strTeamName.startsWith("PlayerLv26")) {
            int t = m_iTime;
            System.out.println();
            System.out.print("時刻 " + t + " ");
            System.out.print(" 背番号" + m_iNumber);
            System.out.print(" 着信 = " + content);
        }
        //視界にある選手に対して以下の検査を行う
        boolean result = false;
        int i;
        for (i = 0; i < m_listPlayer.size(); i++) {
            String player = m_listPlayer.get(i).toString();
            int number = (int) getParam(player, "number", 1);
            //トラップ中の味方にパスが可能か検査する
            if (number == speaker_number && checkPassable(player) == true) {
                //パスのための座標を発信する
                sayPassMessage();
            }
        }
    }
    //==============================================
    //パス地点を示す聴覚メッセージを解析する
    protected void analyzePassMessage(String content) {
        int t = m_iTime;
        //メッセージの検査
        if (content.startsWith("p")) {
            //デバッグ用出力
            if (m_strTeamName.startsWith("PlayerLv26")) {
                System.out.println();
                System.out.print("時刻 " + m_iTime + " ");
                System.out.print(m_strTeamName + " " + m_iNumber);
                System.out.print(" パス目標着信 = " + content);
            }
            //パス地点の格納
            String contentEx = "(" + content + ")";
            m_dPassX = getParam(contentEx, "p", 1);
            m_dPassY = getParam(contentEx, "p", 2);
            m_iPassTime = m_iTime;
        }
    }
    //==============================================
    //聴覚メッセージを解析する
    protected void analyzeAuralMessage(String message) {
        //前回まで開発した内容の実行
        super.analyzeAuralMessage(message);
        int t = m_iTime;
        //プレイヤーの声の処理
        /*messageの例 = "(hear 42 2 opp "t 34 02"*/
        /*messageの例 = "(hear 23 -88 our 7 "t 19 01")*/
        int index0 = message.indexOf("our");
        if (index0 > -1) {
            //味方プレイヤーの背番号
            int speaker_number = (int) getParam(message, "hear", 4);
            //味方プレイヤーの声の内容
            int index1 = message.indexOf("\"", index0 + 3);
            int index2 = message.indexOf("\"", index1 + 1);
            String content = message.substring(index1 + 1, index2);
            //トラップ成功の聴覚メッセージを解析する
            analyzeTrapMessage(speaker_number, content);
            //パス地点を示す聴覚メッセージを解析する
            analyzePassMessage(content);
        }
    }
    //==============================================
    //行動を決定する
    protected void play() {
        //前回まで開発した内容の実行
        super.play();
        //発声カウンタを減らす
        m_iSayCount--;
        if (m_iSayCount < 0)
            m_iSayCount = 0;
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv26[] player = new PlayerLv26[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            if (i < 11)
                teamname += "Left";
            else
                teamname += "Right";
            player[i] = new PlayerLv26();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[9].m_debugLv26 = true;
        System.out.println("試合への登録終了");
    }
}
