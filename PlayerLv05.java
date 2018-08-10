//==============================================
//ボールを蹴る
public class PlayerLv05 extends PlayerLv04 {
    //メンバ
    private boolean m_debugLv05 = false; //デバッグ用フラグ
    //==============================================
    //ボールを蹴る
    protected String kick(String message) {
        //相手側ゴールを示す文字列を得る
        String targetGoal;
        if (m_strSide.startsWith("r"))
            targetGoal = "(g l)";
        else
            targetGoal = "(g r)";
        //相手側ゴールが視覚情報の中にあるか検査する
        int index0 = message.indexOf(targetGoal);
        if (index0 > -1) {
            //ゴールが見えている場合はゴール方向に蹴る
            double goalDist, goalDir;
            goalDist = getParam(message, targetGoal, 1);
            goalDir = getParam(message, targetGoal, 2);
            return "(kick 100 " + goalDir + ")";
        } else {
            //ゴールが見えていない場合は斜め後ろに蹴る
            return "(kick 20 135)";
        }
    }
    //==============================================
    //命令を作る(ボールが見えているとき)
    protected void play(String message, double ballDist, double ballDir) {
        String command = "";
        //ボールが体の正面にあるか検査する
        if (Math.abs(ballDir) < 20.0) {
            //ボールが体の正面にある
            //ボールが蹴る距離にあるかどうか検査する
            if (ballDist < 1.0)
                command = kick(message); //ボールが蹴れるならば蹴る
            else if (checkNearest(message, ballDist, ballDir))
                command = "(dash 80)"; //ボールがまだ蹴れなければ前進する
        } else {
            //ボールがからだの正面になければ回転をする
            command = "(turn " + ballDir + ")";
        }
        //命令をサーバに送る
        send(command);
    }
    //==============================================
    //味方プレイヤーの中で自分がボールに一番近いか検査する
    protected boolean checkNearest(
        String message,
        double ballDist,
        double ballDir) {
        return true;
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv05[] player = new PlayerLv05[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname;
            if (i < 11)
                teamname = "Lv05Left";
            else
                teamname = "Lv05Right";
            player[i] = new PlayerLv05();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[10].m_debugLv05 = true;
        System.out.println("試合への登録終了");
    }
}
