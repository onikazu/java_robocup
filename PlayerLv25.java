//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//パス地点を評価してキックをする
public class PlayerLv25 extends PlayerLv24 {
    //メンバ:
    private boolean m_debugLv25 = false;
    //==============================================
    //キックの目標位置
    protected void setKickTarget() {
        //いままで開発した内容を実行する
        super.setKickTarget();
        if (m_listPlayer == null)
            return;
        int t = m_iTime;
        int i;
        DecimalFormat f = new DecimalFormat("###0.00");
        //デバッグ用出力を作る
        if (m_strTeamName.startsWith("PlayerLv25")) {
            System.out.println();
            System.out.println("==========================");
            System.out.print("チーム=" + m_strTeamName);
            System.out.print(" 背番号 " + m_iNumber + " 時刻" + m_iTime);
            System.out.println(" 視覚情報" + m_iVisualTime);
            for (i = 0; i < m_listPlayer.size(); i++) {
                String player = m_listPlayer.get(i).toString();
                System.out.print(player);
                double X = getParam(player, "x", 1);
                double Y = getParam(player, "y", 1);
                double Dir = getDirection(m_dX[t], m_dY[t], X, Y);
                double Dist = getDistance(m_dX[t], m_dY[t], X, Y);
                System.out.println(
                    " Dir=" + f.format(Dir) + " Dist=" + f.format(Dist));
            }
        }
        //パスコースの候補を探すため選手リストを総当りする
        for (i = 0; i < m_listPlayer.size(); i++) {
            String player1 = m_listPlayer.get(i).toString();
            double friendDir = OUT_OF_RANGE;
            double friendDist = OUT_OF_RANGE;
            double max_score = 0.0;
            if (player1.indexOf("friend") > -1) {
                //味方プレイヤーを選ぶ
                double friendX = getParam(player1, "x", 1);
                double friendY = getParam(player1, "y", 1);
                friendDir = getDirection(m_dX[t], m_dY[t], friendX, friendY);
                friendDist = getDistance(m_dX[t], m_dY[t], friendX, friendY);
                if (friendDist < 5.0 || friendDist > 45.0)
                    friendDist = friendDir = OUT_OF_RANGE;
                //味方プレイヤーの地点でのパスコースの評価を行う
                double score = getPassValue(m_dX[t], m_dY[t], friendX, friendY);
                if (score > max_score) {
                    //評価が最大ならば採用する
                    m_dKickX[t] = friendX;
                    m_dKickY[t] = friendY;
                    m_iKickTime[t] = m_iTime + getPassCount(friendDist);
                    max_score = score;
                }
                //パス地点の候補をデバッグ用に表示する
                if (m_strTeamName.startsWith("PlayerLv25")) {
                    System.out.print("パス候補");
                    System.out.print(
                        " 背番号 " + (int) getParam(player1, "number", 1));
                    System.out.print(" 位置(" + f.format(friendX));
                    System.out.print(" ," + f.format(friendY) + ")");
                    System.out.print(" 距離 " + f.format(friendDist));
                    System.out.print(" 評価 " + f.format(score));
                    System.out.println();
                }
            }
        }
    }
    //==============================================
    //距離から選手が移動できるカウントを計算する
    protected int getMoveCount(double dist, double v0) {
        //dist=距離
        //v0=初速度
        int count = 1;
        double d = 0.0;
        double v = v0;
        while (dist > d && count < 100) {
            d = d + v;
            v = v * player_decay + 100.0 * dash_power_rate;
            count++;
        }
        return count;
    }
    //==============================================
    //距離からボールが移動するカウントを計算する
    protected int getPassCount(double dist) {
        //dist=距離
        if (dist > 50.0)
            return 100;
        double v = 100.0 * kick_power_rate;
        double d = 0;
        int count = 1;
        while (dist > d && count < 100) {
            d = d + v;
            v = v * ball_decay;
            count++;
        }
        return count;
    }
    //==============================================
    //パスの価値を計算する
    protected double getPassValue(double x0, double y0, double x1, double y1) {
        //(x0,y0)ボールの座標
        //(x1,y1)パス目標
        int t = m_iTime;
        double value = 0.0;
        //相手ゴールに近いほど価値を高くする
        double goalX, goalY;
        if (m_strSide.startsWith("r")) {
            goalX = -52.5;
            goalY = 0.0;
        } else {
            goalX = 52.5;
            goalY = 0.0;
        }
        value += 100.0 - getDistance(goalX, goalY, x1, y1);
        //パスの途中のコースに敵がいたら価値を0にする
        double passDist = getDistance(x0, y0, x1, y1);
        double passDir = getDirection(x0, y0, x1, y1);
        int j;
        for (j = 0; j < m_listPlayer.size(); j++) {
            String player2 = m_listPlayer.get(j).toString();
            if (player2.indexOf("enemy") > -1) {
                double enemyX = getParam(player2, "x", 1);
                double enemyY = getParam(player2, "y", 1);
                double enemyDir = getDirection(x0, y0, enemyX, enemyY);
                double enemyDist = getDistance(x0, y0, enemyX, enemyY);
                double diff = Math.abs(normalizeAngle(enemyDir - passDir));
                if (diff < 10.0 && enemyDist < passDist) {
                    value = 0.0;
                }
            }
        }
        return value;
    }
    //==============================================
    //視覚メッセージを解析する
    protected void analyzeVisualMessage(String message) {
        //前回まで開発した内容を実行する
        super.analyzeVisualMessage(message);
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv25[] player = new PlayerLv25[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            if (i < 11)
                teamname += "Left";
            else
                teamname += "Right";
            player[i] = new PlayerLv25();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[9].m_debugLv25 = true;
        System.out.println("試合への登録終了");
    }
}
