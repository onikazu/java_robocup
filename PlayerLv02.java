//==============================================
//フィールド上で初期フォーメーションを作る
public class PlayerLv02 extends PlayerLv01 {
    protected double m_dKickOffX, m_dKickOffY; //キックオフ時の場所
    private boolean m_debugLv02 = false; //デバッグのフラグ

    //==============================================
    //視覚メッセージを解析する
    protected void analyzeVisualMessage(String message) {
    }
    //==============================================
    //初期フォーメーションを取るかどうか検査する
    protected boolean checkInitialMode() {
        //キックオフ前の状態かを調べる
        if (m_strPlayMode.startsWith("before_kick_off")
            || m_strPlayMode.startsWith("goal_l")
            || m_strPlayMode.startsWith("goal_r"))
            return true;
        else
            return false;
    }
    //==============================================
    //キックオフ時の場所を背番号から求める
    protected void setKickOffPosition() {
        //背番号によりcase文で分岐をする
        switch (m_iNumber) {
            case 1 : m_dKickOffX = -50.0;m_dKickOffY =  -0.0; break;
            case 2 : m_dKickOffX = -40.0;m_dKickOffY = -15.0; break;
            case 3 : m_dKickOffX = -40.0;m_dKickOffY =  -5.0; break;
            case 4 : m_dKickOffX = -40.0;m_dKickOffY =  +5.0; break;
            case 5 : m_dKickOffX = -40.0;m_dKickOffY = +15.0; break;
            case 6 : m_dKickOffX = -20.0;m_dKickOffY = -15.0; break;
            case 7 : m_dKickOffX = -20.0;m_dKickOffY =  -5.0; break;
            case 8 : m_dKickOffX = -20.0;m_dKickOffY =  +5.0; break;
            case 9 : m_dKickOffX = -20.0;m_dKickOffY = +15.0; break;
            case 10 :m_dKickOffX =  -1.0;m_dKickOffY =  -5.0; break;
            case 11 :m_dKickOffX =  -4.0;m_dKickOffY = +10.0; break;
            default :System.err.println("背番号が1-11の範囲外です");
        }
    }
    //==============================================
    //命令を作り実行する
    protected void play(String message) {
        //初期フォーメーションを取るかどうか検査する
        if (checkInitialMode()) {
            //moveコマンドの命令作成と実行
            setKickOffPosition();
            String command = "(move " + m_dKickOffX + " " + m_dKickOffY + ")";
            send(command);
        }
    }
    //==============================================
    //サーバから受け取ったメッセージを処理する
    protected void analyzeMessage(String message) {
        //前回までに開発した内容を実行する
        super.analyzeMessage(message);
        //視覚メッセージの処理
        if (message.startsWith("(see ")) {
            analyzeVisualMessage(message); //メッセージの解析
            play(message); //行動の決定とコマンド送信
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        //2チーム分の選手を作成する
        PlayerLv02[] player = new PlayerLv02[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname;
            //変数iの値によってチーム名を変更する
            if (i < 11)
                teamname = "Lv02Left";
            else
                teamname = "Lv02Right";
            player[i] = new PlayerLv02();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        System.out.println("試合への登録終了");
    }
}
