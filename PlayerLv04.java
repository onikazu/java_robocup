//==============================================
//ボールへ向く
public class PlayerLv04 extends PlayerLv03 {
    //定数
    final double OUT_OF_RANGE = 999.9; //エラーを示す値
    private boolean m_debugLv04 = false; //デバッグのフラグ
    //==============================================
    //キーワードで始まる物体データをメッセージから取り出す
    protected String getObjectMessage(String message, String keyword) {
        String result = "";
        //メッセージからキーワードで始まる文字列を取り出す部分
        //message = "......keyword) 20 10 5 3)......"
        //ボールのメッセージを抜き出す
        int index0 = message.indexOf(keyword);
        while (-1 < index0) {
            //キーワードが見えているときはキーワードを含む文字列を結果に追加する
            //検索をindexOf()メソッドで行う
            int index1 = message.indexOf(")", index0 + 2);
            int index2 = message.indexOf(")", index1 + 1);
            //文字列の部分取り出しをsubstring()メソッドで行い結果に追加する
            result += message.substring(index0, index2 + 1);
            result += ")";
            index0 = message.indexOf(keyword, index2);
        }
        return result;
    }
    //==============================================
    //message中から指定したキーワードの後ろにある数値を取り出す
    protected double getParam(String message, String keyword, int number) {
        String str = "(" + keyword;
        //キーワードを探す
        int index0 = message.indexOf(str);
        if (index0 < 0) {
            System.err.println("Lv04:エラー:キーワード指定が誤っています");
            return OUT_OF_RANGE;
        }
        //パラメータの直後の区切りを探す
        int index1 = message.indexOf(" ", index0 + str.length());
        switch (number) {
            case 4 :
                index1 = message.indexOf(" ", index1 + 1);
            case 3 :
                index1 = message.indexOf(" ", index1 + 1);
            case 2 :
                index1 = message.indexOf(" ", index1 + 1);
            default :
                }
        int index2 = message.indexOf(" ", index1 + 1);
        int index3 = message.indexOf(")", index1 + 1);
        if (index3 < index2 && index3 != -1 || index2 == -1)
            index2 = index3;
        //パラメータを読み込む
        double result;
        try {
            result = Double.parseDouble(message.substring(index1, index2));
        } catch (NumberFormatException e) {
            //System.err.println("Lv04:エラー:値が数値ではありません");
            result = OUT_OF_RANGE;
        }
        //結果を返す
        return result;
    }
    //==============================================
    //命令を作る(ボールが見えているとき)
    protected void play(String message, double ballDist, double ballDir) {
        //ボールが見えているときの処理は空にしておく
    }
    //==============================================
    //命令を作る
    protected void play(String message) {
        //初期位置への移動が必要かを検査する
        if (checkInitialMode()) {
            //初期位置へmove命令で移動する
            setKickOffPosition();
            String command = "(move " + m_dKickOffX + " " + m_dKickOffY + ")";
            send(command);
        } else {
            //通常の行動
            //ボールが見えるかを検査する
            message = message.replace('B', 'b');
            String ball = getObjectMessage(message, "((b");
            if (ball.startsWith("((b")) {
                //ボールが見えた時の行動
                double ballDist = getParam(ball, "(b)", 1);
                double ballDir = getParam(ball, "(b)", 2);
                play(message, ballDist, ballDir);
            } else {
                //ボールが見えない時の行動
                String command = "(turn 30)";
                send(command);
            }
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv04[] player = new PlayerLv04[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname;
            if (i < 11)
                teamname = "Lv04Left";
            else
                teamname = "Lv04Right";
            player[i] = new PlayerLv04();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[10].m_debugLv04 = true;
        System.out.println("試合への登録終了");
    }
}
