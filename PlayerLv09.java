//==============================================
//ボール位置と守備位置の計算
class PlayerLv09 extends PlayerLv08 {
    protected boolean m_debugLv09 = false;
    protected double m_dBallX, m_dBallY;
    protected double m_dDefenceX, m_dDefenceY;
    //==============================================
    //守備位置を計算する
    protected void setDefencePosition(double ballX, double ballY) {
        //背番号ごとに基準点からの相対位置を決める。
        double offsetX = 0.0, offsetY = 0.0;
        switch (m_iNumber) {
            case 1 : offsetX = -50.0; offsetY = -0.0;   break;
            case 2 : offsetX = -30.0; offsetY = -15.0;  break;
            case 3 : offsetX = -30.0; offsetY = -5.0;   break;
            case 4 : offsetX = -30.0; offsetY = +5.0;   break;
            case 5 : offsetX = -30.0; offsetY = +15.0;  break;
            case 6 : offsetX = -10.0; offsetY = -15.0;  break;
            case 7 : offsetX = -10.0; offsetY = -5.0;   break;
            case 8 : offsetX = -10.0; offsetY = +5.0;   break;
            case 9 : offsetX = -10.0; offsetY = +15.0;  break;
            case 10 :offsetX = 10.0;  offsetY = -5.0;   break;
            case 11 :offsetX = 10.0;  offsetY = +5.0;   break;
            default :
                }
        //選手がキーパーであるか検査する
        if (m_iNumber == 1) {
            //キーパーは固定の位置を決める。
            if (m_strSide.startsWith("r")) m_dDefenceX = 52.5;
            else m_dDefenceX = -52.5;
            m_dDefenceY = 0.0;
        } else {
            //キーパー以外はボールとフィールドの中心との平均からの相対位置を守備位置とする
            if (m_strSide.startsWith("r")) {
                m_dDefenceX = ballX / 2.0 - offsetX; m_dDefenceY = ballY / 2.0 - offsetY;
            } else {
                m_dDefenceX = ballX / 2.0 + offsetX; m_dDefenceY = ballY / 2.0 + offsetY;
            }
        }
    }
    //==============================================
    //視覚メッセージを解析する
    protected void analyzeVisualMessage(String message) {
        //前回まで開発した部分を実行する
        super.analyzeVisualMessage(message);
        //ボールの位置を計算する
        if (message.indexOf("(b)") == -1) return;
        double ballDist = getParam(message, "(b)", 1);
        double ballDir = getParam(message, "(b)", 2);
        double rad = Math.toRadians(normalizeAngle(m_dNeck + ballDir));
        m_dBallX = m_dX + ballDist * Math.cos(rad); m_dBallY = m_dY + ballDist * Math.sin(rad);
        //守備位置を計算する
        setDefencePosition(m_dBallX, m_dBallY);
        if (m_debugLv09) {
            System.out.println("defX=" + m_dDefenceX + " defY=" + m_dDefenceY);
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv09[] player1 = new PlayerLv09[11];
        Thread[] thread1 = new Thread[11];
        int i;
        for (i = 0; i < 11; i++) {
            String teamname = "Lv09";
            player1[i] = new PlayerLv09();
            thread1[i] = new Thread(player1[i]);
            player1[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread1[i].start();
        }
        PlayerLv08[] player2 = new PlayerLv08[11];
        Thread[] thread2 = new Thread[11];
        for (i = 0; i < 11; i++) {
            String teamname = "Lv08";
            player2[i] = new PlayerLv08();
            thread2[i] = new Thread(player2[i]);
            player2[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread2[i].start();
        }
        player1[1].m_debugLv09 = true;
        System.out.println("試合への登録終了");
    }
}
