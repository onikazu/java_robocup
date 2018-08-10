//==============================================
//首の向いている方向を計算する
class PlayerLv07 extends PlayerLv06 {
    protected boolean m_debugLv07 = false;
    //==============================================
    //角度の正規化を行う
    protected double normalizeAngle(double angle) {
        //角度が大幅に大きい場合のエラー処理
        if (Math.abs(angle) > 720.0) {
            System.err.println("Lv07:角度指定の誤りがあります。");
            System.exit(1);
        }
        //角度が180度を越えたときは360度減らす。
        while (angle > 180.0)
            angle -= 360.0;
        //角度が-180度を越えたときは360度増やす。
        while (angle < -180.0)
            angle += 360.0;
        return angle;
    }
    //==============================================
    //ラインの情報から首の角度を計算する
    protected double getNeckDir(String message) {
        //一番距離の遠いライン情報を視覚情報から取り出す
        int index0 = message.indexOf("((l");
        String lineName = "";
        String line = "";
        double lineDist = -OUT_OF_RANGE;
        double lineDir = -OUT_OF_RANGE;
        while (index0 > -1) {
            int index1 = message.indexOf(")", index0 + 3);
            lineName = message.substring(index0 + 1, index1 + 1);
            line = "(" + lineName;
            int index2 = message.indexOf(")", index1 + 1);
            line += message.substring(index1 + 1, index2 + 1);
            double dist = getParam(line, lineName, 1);
            double dir = getParam(line, lineName, 2);
            if (dist > lineDist) {
                lineDist = dist;
                lineDir = dir;
            }
            index0 = message.indexOf("((l", index0 + 3);
        }
        if (lineDist == OUT_OF_RANGE) {
            return OUT_OF_RANGE;
        }
        //ラインごとに場合わけをして首の角度を計算する
        double playerNeck = OUT_OF_RANGE;
        if (lineName.startsWith("(l b)"))
            if (0 < lineDir && lineDir <= 90)
                playerNeck = 180 - lineDir;
            else
                playerNeck = -lineDir;
        else if (lineName.startsWith("(l t)"))
            if (0 < lineDir && lineDir <= 90)
                playerNeck = -lineDir;
            else
                playerNeck = -180 - lineDir;
        else if (lineName.startsWith("(l l)"))
            if (0 < lineDir && lineDir <= 90)
                playerNeck = -90 - lineDir;
            else
                playerNeck = 90 - lineDir;
        else if (lineName.startsWith("(l r)"))
            if (0 < lineDir && lineDir <= 90)
                playerNeck = 90 - lineDir;
            else
                playerNeck = -90 - lineDir;
        return playerNeck;
    }
    //==============================================
    //ボールを蹴る
    protected String kick(String message) {
        //相手ゴールを示す文字列を求める
        String targetGoal;
        if (m_strSide.startsWith("r"))
            targetGoal = "(g l)";
        else
            targetGoal = "(g r)";
        //相手ゴールが見えているか検査する
        int index0 = message.indexOf(targetGoal);
        if (index0 > -1) {
            //相手ゴールが見えているので相手ゴールへ蹴る
            double goalDist, goalDir;
            goalDist = getParam(message, targetGoal, 1);
            goalDir = getParam(message, targetGoal, 2);
            return "(kick 100 " + goalDir + ")";
        } else {
            //相手ゴールが直接見えないので、相手ゴールライン方向に蹴る
            //首（＝体）が向いている方向を求める
            double neckDir = getNeckDir(message);
            //相手ゴールライン方向を求める
            double attackDir = 0.0;
            if (m_strSide.startsWith("r"))
                attackDir = 180.0;
            //キックをする方向を求める
            double kickDir = normalizeAngle(attackDir - neckDir);
            if (m_strPlayMode.startsWith("play_on"))
                return "(kick 30 " + kickDir + ")";
            else
                return "(kick 100 " + kickDir + ")";
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv07[] player1 = new PlayerLv07[11];
        Thread[] thread1 = new Thread[11];
        int i;
        for (i = 0; i < 11; i++) {
            String teamname = "Lv07";
            player1[i] = new PlayerLv07();
            thread1[i] = new Thread(player1[i]);
            player1[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread1[i].start();
        }
        PlayerLv06[] player2 = new PlayerLv06[11];
        Thread[] thread2 = new Thread[11];
        for (i = 0; i < 11; i++) {
            String teamname = "Lv06";
            player2[i] = new PlayerLv06();
            thread2[i] = new Thread(player2[i]);
            player2[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread2[i].start();
        }
        player1[10].m_debugLv07 = true;
        System.out.println("試合への登録終了");
    }
}
