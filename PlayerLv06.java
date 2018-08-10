//==============================================
//一番ボールに近い味方プレイヤーを計算する
class PlayerLv06 extends PlayerLv05 {
    //メンバ
    protected boolean m_debugLv06 = false;
    //==============================================
    //味方プレイヤーの中で自分がボールに一番近いか検査する
    protected boolean checkNearest(
        String message,
        double ballDist,
        double ballDir) {
        //味方チームの名前が含まれる視覚情報データのキーワードを作る
        String teamname = "(p \"" + m_strTeamName + "\"";
        String player = getObjectMessage(message, "((p");
        int index0 = player.indexOf(teamname, 0);
        //視覚情報内に味方チームの名前で始まるデータが在る限り以下を繰り返す。
        while (index0 > -1) {
            //パラメータの前後の位置をindexOfメソッドで探す
            int index1 = player.indexOf(")", index0);
            int index2 = player.indexOf(" ", index1 + 1);
            int index3 = player.indexOf(" ", index2 + 1);
            int index4 = player.indexOf(" ", index3 + 1);
            int index5 = player.indexOf(")", index3 + 1);
            if (index5 < index4 || index4 == -1)
                index4 = index5;
            //パラメータの値を得る
            double playerDist =
                Double.parseDouble(player.substring(index2, index3));
            double playerDir =
                Double.parseDouble(player.substring(index3, index4));
            //余弦定理で味方プレイヤーとボールとの距離を計算する
            double A = ballDist;
            double B = playerDist;
            double rad = Math.PI / 180.0 * (playerDir - ballDir);
            double dist = Math.sqrt(A * A + B * B - 2 * A * B * Math.cos(rad));
            //味方プレイヤーの中で自分がボールに一番近いか検査する
            if (dist < ballDist)
                return false;
            index0 = player.indexOf(teamname, index0 + teamname.length());
        }
        return true;
    }
    //==============================================
    //命令を作る(ボールが遠いとき)
    protected String getCommandAsDefence(
        String message,
        double ballDist,
        double ballDir) {
        //味方ゴールの文字列を求める
        String command = "";
        String goal = "(g l)";
        if (m_strSide.startsWith("r"))
            goal = "(g r)";
        //味方ゴールが視界に入っているか検査する
        if (message.indexOf(goal) > -1) {
            //味方ゴールまでの距離を検査する
            double goalDist = getParam(message, goal, 1);
            if (goalDist > 50.0) {
                //味方ゴールから遠すぎるので前進してハーフライン付近まで帰る
                command = "(dash 80)";
            }
        }
        return command;
    }
    //==============================================
    //命令を作る(ボールが見えているとき)
    protected void play(String message, double ballDist, double ballDir) {
        String command = "";
        //ボールが体の正面にあるか検査する
        if (Math.abs(ballDir) < 20.0) {
            //ボールが体の正面にある
            //ボールが蹴る距離にあるかどうか検査する
            if (ballDist < 1.0) {
                //ボールが蹴れるならば蹴る
                command = kick(message);
            } else //選手がボールに一番近い味方選手であるかどうか検査する
                if (checkNearest(message,
                    ballDist,
                    ballDir)) { //ボールに一番近いので前進する
                    command = "(dash 80)";
                } else {
                    //ボールに近い味方がいるのでボールの監視とオフサイドの回避を行う
                    command = getCommandAsDefence(message, ballDist, ballDir);
                }
        } else {
            //ボールがからだの正面になければ回転をする
            command = "(turn " + ballDir + ")";
        }
        //命令をサーバに送る
        send(command);
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv06[] player1 = new PlayerLv06[11];
        Thread[] thread1 = new Thread[11];
        int i;
        //Lv06の選手でチームを作る
        for (i = 0; i < 11; i++) {
            String teamname = "Lv06";
            player1[i] = new PlayerLv06();
            thread1[i] = new Thread(player1[i]);
            player1[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread1[i].start();
        }
        //Lv05の選手でチームを作る
        PlayerLv05[] player2 = new PlayerLv05[11];
        Thread[] thread2 = new Thread[11];
        for (i = 0; i < 11; i++) {
            String teamname = "Lv05";
            player2[i] = new PlayerLv05();
            thread2[i] = new Thread(player2[i]);
            player2[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread2[i].start();
        }
        player1[10].m_debugLv06 = true;
        System.out.println("試合への登録終了");
    }
}
