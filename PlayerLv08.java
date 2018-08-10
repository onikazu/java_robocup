//==============================================
//パッケージの読み込み
import java.awt.geom.*; //Point2D.Doubleを使うため
//==============================================
//自分の座標を計算する
class PlayerLv08 extends PlayerLv07 {
    //メンバ
    protected String[] m_strFlagName;
    protected double[] m_dFlagX, m_dFlagY;
    protected double m_dX, m_dY, m_dNeck;
    protected boolean m_debugLv08 = false;
    //==============================================
    //コンストラクタ
    public PlayerLv08() {
        //前回まで開発した内容を実行する
        //==============================================
        super();
        //旗の名前・X座標・Y座標のデータを初期化する
        m_strFlagName = new String[55];
        m_dFlagX = new double[55];
        m_dFlagY = new double[55];
        int i = 0;
        m_strFlagName[i] ="g r";     m_dFlagX[i] = 52.5	;m_dFlagY[i] = 0.0   ;i++;
        m_strFlagName[i] ="g l";     m_dFlagX[i] = -52.5;m_dFlagY[i] = 0.0   ;i++;
        m_strFlagName[i] ="f c t";   m_dFlagX[i] = 0.0	;m_dFlagY[i] = -34.0 ;i++;
        m_strFlagName[i] ="f c b";   m_dFlagX[i] = 0.0	;m_dFlagY[i] = +34.0 ;i++;
        m_strFlagName[i] ="f c";     m_dFlagX[i] = 0.0	;m_dFlagY[i] = 0.0   ;i++;
        m_strFlagName[i] ="f p l t"; m_dFlagX[i] = -36.0;m_dFlagY[i] = -20.16;i++;
        m_strFlagName[i] ="f p l b"; m_dFlagX[i] = -36.0;m_dFlagY[i] =  20.16;i++;
        m_strFlagName[i] ="f p l c"; m_dFlagX[i] = -36.0;m_dFlagY[i] =  0.0  ;i++;
        m_strFlagName[i] ="f p r t"; m_dFlagX[i] =  36.0;m_dFlagY[i] = -20.16;i++;
        m_strFlagName[i] ="f p r b"; m_dFlagX[i] =  36.0;m_dFlagY[i] =  20.16;i++;
        m_strFlagName[i] ="f p r c"; m_dFlagX[i] =  36.0;m_dFlagY[i] =   0.0;i++;
        m_strFlagName[i] ="f g l t"; m_dFlagX[i] = -52.5;m_dFlagY[i] = -7.01;i++;
        m_strFlagName[i] ="f g l b"; m_dFlagX[i] = -52.5;m_dFlagY[i] =  7.01;i++;
        m_strFlagName[i] ="f g r t"; m_dFlagX[i] =  52.5;m_dFlagY[i] = -7.01;i++;
        m_strFlagName[i] ="f g r b"; m_dFlagX[i] =  52.5;m_dFlagY[i] =  7.01;i++;
        m_strFlagName[i] ="f t l 50";m_dFlagX[i] = -50.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t l 40";m_dFlagX[i] = -40.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t l 30";m_dFlagX[i] = -30.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t l 20";m_dFlagX[i] = -20.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t l 10";m_dFlagX[i] = -10.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t 0";   m_dFlagX[i] =   0.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t r 10";m_dFlagX[i] =  10.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t r 20";m_dFlagX[i] =  20.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t r 30";m_dFlagX[i] =  30.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t r 40";m_dFlagX[i] =  40.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f t r 50";m_dFlagX[i] =  50.0;m_dFlagY[i] = -39.0;i++;
        m_strFlagName[i] ="f b l 50";m_dFlagX[i] = -50.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b l 40";m_dFlagX[i] = -40.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b l 30";m_dFlagX[i] = -30.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b l 20";m_dFlagX[i] = -20.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b l 10";m_dFlagX[i] = -10.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b 0";   m_dFlagX[i] =   0.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b r 10";m_dFlagX[i] =  10.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b r 20";m_dFlagX[i] =  20.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b r 30";m_dFlagX[i] =  30.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b r 40";m_dFlagX[i] =  40.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f b r 50";m_dFlagX[i] =  50.0;m_dFlagY[i] =  39.0;i++;
        m_strFlagName[i] ="f l t 30";m_dFlagX[i] = -57.5;m_dFlagY[i] = -30.0;i++;
        m_strFlagName[i] ="f l t 20";m_dFlagX[i] = -57.5;m_dFlagY[i] = -20.0;i++;
        m_strFlagName[i] ="f l t 10";m_dFlagX[i] = -57.5;m_dFlagY[i] = -10.0;i++;
        m_strFlagName[i] ="f l 0";   m_dFlagX[i] = -57.5;m_dFlagY[i] =   0.0;i++;
        m_strFlagName[i] ="f l b 10";m_dFlagX[i] = -57.5;m_dFlagY[i] =  10.0;i++;
        m_strFlagName[i] ="f l b 20";m_dFlagX[i] = -57.5;m_dFlagY[i] =  20.0;i++;
        m_strFlagName[i] ="f l b 30";m_dFlagX[i] = -57.5;m_dFlagY[i] =  30.0;i++;
        m_strFlagName[i] ="f r t 30";m_dFlagX[i] =  57.5;m_dFlagY[i] = -30.0;i++;
        m_strFlagName[i] ="f r t 20";m_dFlagX[i] =  57.5;m_dFlagY[i] = -20.0;i++;
        m_strFlagName[i] ="f r t 10";m_dFlagX[i] =  57.5;m_dFlagY[i] = -10.0;i++;
        m_strFlagName[i] ="f r 0";   m_dFlagX[i] =  57.5;m_dFlagY[i] =   0.0;i++;
        m_strFlagName[i] ="f r b 10";m_dFlagX[i] =  57.5;m_dFlagY[i] =  10.0;i++;
        m_strFlagName[i] ="f r b 20";m_dFlagX[i] =  57.5;m_dFlagY[i] =  20.0;i++;
        m_strFlagName[i] ="f r b 30";m_dFlagX[i] =  57.5;m_dFlagY[i] =  30.0;i++;
        m_strFlagName[i] ="f l t";   m_dFlagX[i] = -52.5;m_dFlagY[i] = -34.0;i++;
        m_strFlagName[i] ="f l b";   m_dFlagX[i] = -52.5;m_dFlagY[i] =  34.0;i++;
        m_strFlagName[i] ="f r t";   m_dFlagX[i] =  52.5;m_dFlagY[i] = -34.0;i++;
        m_strFlagName[i] ="f r b";   m_dFlagX[i] =  52.5;m_dFlagY[i] =  34.0;i++;
    }
    //==============================================
    //大文字の視覚情報を解決する
    protected String getLandMarker(
        String message,
        double playerX,
        double playerY) {
        //(B)を解決する
        message = message.replaceFirst("B", "b");
        //(F)を解決する
        if (message.indexOf("(F)") > -1) {
            String name = "(F)";
            double min_dist = OUT_OF_RANGE;
            int i;
            //全ての旗の候補の座標との距離を計算し、
            //一番近いと推測する旗を決める。
            for (i = 2; i < 55; i++) {
                double dist =
                    getDistance(playerX, playerY, m_dFlagX[i], m_dFlagY[i]);
                if (min_dist > dist) {
                    min_dist = dist;
                    name = m_strFlagName[i];
                }
            }
            message = message.replaceFirst("F", name);
        }
        //(G)を解決する
        if (message.indexOf("(G)") > -1) {
            String name = "(G)";
            double min_dist = OUT_OF_RANGE;
            int i;
            //全てのゴールの候補の座標との距離を計算し、
            //一番近いと推測するゴールを決める。
            for (i = 0; i < 2; i++) {
                double dist =
                    getDistance(playerX, playerY, m_dFlagX[i], m_dFlagY[i]);
                if (min_dist > dist) {
                    min_dist = dist;
                    name = m_strFlagName[i];
                }
            }
            message = message.replaceFirst("G", name);
        }
        return message;
    }
    //==============================================
    //自分の座標を計算する
    protected Point2D.Double estimatePosition(
        String message,
        double neckDir,
        double playerX,
        double playerY) {
        Point2D.Double result = new Point2D.Double(OUT_OF_RANGE, OUT_OF_RANGE);
        //大文字情報を解決する
        message = getLandMarker(message, playerX, playerY);
        //旗とゴールの情報を視覚情報から抜き出す
        String flag =
            getObjectMessage(message, "((g") + getObjectMessage(message, "((f");
        //全ての視覚情報に入っている旗・ゴールに対し自分の位置の推定計算を行う
        int index0 = flag.indexOf("((");
        double X = 0.0, Y = 0.0, W = 0.0, S = 0.0;
        int flags = 0;
        while (index0 > -1) {
            //旗・ゴールの名前を求める
            int index1 = flag.indexOf(")", index0 + 2);
            int index2 = flag.indexOf(")", index1 + 1);
            String name = flag.substring(index0 + 2, index1);
            int j = 0;
            while (m_strFlagName[j].endsWith(name) == false) {
                j++;
            }
            //旗・ゴールまでの距離・角度を求める
            double dist = getParam(flag, name, 1);
            double dir = getParam(flag, name, 2);
            double rad = Math.toRadians(normalizeAngle(dir + neckDir));
            //計算結果の重みを距離から求める
            W = 1 / dist;
            //自分の座標の推定計算を行い、重みと乗算する
            X += W * (m_dFlagX[j] - dist * Math.cos(rad));
            Y += W * (m_dFlagY[j] - dist * Math.sin(rad));
            S += W;
            flags++;
            index0 = flag.indexOf("((", index0 + 2);
        }
        //自分の座標の最終推定計算を行う
        if (flags > 0) {
            result.x = X / S;
            result.y = Y / S;
        }
        //デバッグ用の表示を行う
        if (m_debugLv08) {
            System.out.println("X=" + result.x + " Y=" + result.y);
        }
        return result;
    }
    //==============================================
    //距離を計算する
    protected double getDistance(double x0, double y0, double x1, double y1) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        return Math.sqrt(dx * dx + dy * dy);
    }
    //==============================================
    //視覚メッセージを解析する
    protected void analyzeVisualMessage(String message) {
        int time = (int) getParam(message, "see", 1);
        if (time < 1)
            return;
        //首の方向を計算する
        double m_dNeck = getNeckDir(message);
        if (m_dNeck == OUT_OF_RANGE)
            return;
        //初期位置移動時の座標を推定する
        if (checkInitialMode()) {
            m_dX = m_dKickOffX;
            m_dY = m_dKickOffY;
        }
        //自分の座標を計算する
        Point2D.Double pos = new Point2D.Double();
        pos = estimatePosition(message, m_dNeck, m_dX, m_dY);
        m_dX = pos.x;
        m_dY = pos.y;
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv08[] player1 = new PlayerLv08[11];
        Thread[] thread1 = new Thread[11];
        int i;
        for (i = 0; i < 11; i++) {
            String teamname = "Lv08";
            player1[i] = new PlayerLv08();
            thread1[i] = new Thread(player1[i]);
            player1[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread1[i].start();
        }
        PlayerLv07[] player2 = new PlayerLv07[11];
        Thread[] thread2 = new Thread[11];
        for (i = 0; i < 11; i++) {
            String teamname = "Lv07";
            player2[i] = new PlayerLv07();
            thread2[i] = new Thread(player2[i]);
            player2[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread2[i].start();
        }
        player1[10].m_debugLv08 = true;
        System.out.println("試合への登録終了");
    }
}
