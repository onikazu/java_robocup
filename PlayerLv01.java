//==============================================
//複数の選手を試合に参加させる
public class PlayerLv01 extends PlayerLv00 implements Runnable {
    //==============================================
    //属性
    protected int m_iNumber = 0; //背番号
    protected String m_strTeamName = ""; //チーム名
    protected String m_strSide = ""; //守るゴール
    protected String m_strPlayMode = ""; //現在のプレイモード
    private boolean m_debugLv01 = false;

    //==============================================
    //初期化コマンドを送信する
    public void initialize(
        int number,
        String team_name,
        String server_name,
        int server_port) {
        //メンバ変数への格納
        m_iNumber = number;
        m_strTeamName = team_name;
        m_strHostName = server_name;
        m_iPort = server_port;
        //初期化コマンドの作成
        String command;
        if (m_iNumber == 1)
            command = "(init " + m_strTeamName + " (goalie)(version 15.40))";
        else
            command = "(init " + m_strTeamName + " (version 15.40))";
        //コマンドの送信
        send(command);
    }
    //==============================================
    //スレッドで実行する処理の本体
    //サーバからのメッセージを受信し、メッセージに対応する行動を行いつづける
    public void run() {
        while (true) //以下をCTRL+Cキーが押されるまで繰り返す
            {
            String message = receive(); //メッセージを受信する
            analyzeMessage(message); //メッセージに対する行動を行う
        }
    }
    //==============================================
    //初期メッセージを解析する
    protected void analyzeInitialMessage(String message) {
        //messageの例 = "(init r 1 before_kick_off)"
        //message中の空白位置をindexOf( )メソッドで求める
        int index0 = message.indexOf(" ");
        int index1 = message.indexOf(" ", index0 + 1);
        int index2 = message.indexOf(" ", index1 + 1);
        int index3 = message.indexOf(")", index2 + 1);
        //守るゴールの取得
        m_strSide = message.substring(index0 + 1, index1);
        //背番号の取得
        m_iNumber = Integer.parseInt(message.substring(index1 + 1, index2));
        //現在の試合状態（プレイモード）の取得
        m_strPlayMode = message.substring(index2 + 1, index3);
    }
    //==============================================
    //サーバから受け取ったメッセージを処理する
    protected void analyzeMessage(String message) {
        if (message.startsWith("(init ")) {
            //初期メッセージの処理
            analyzeInitialMessage(message);
        } else if (message.startsWith("(warning")) {
            //エラーメッセージの処理
            System.err.println(message);
        } else if (message.startsWith("(error")) {
            //警告メッセージの処理
            System.err.println(message);
        }
    }
    //==============================================
    //メイン
    //選手を11人作成し、サーバーへそれぞれ接続する。
    public static void main(String[] args) {
        PlayerLv01[] player = new PlayerLv01[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 11; i++) //選手の数だけ繰り返し
            {
            player[i] = new PlayerLv01(); //選手オブジェクト作成
            thread[i] = new Thread(player[i]); //並列動作させるスレッド作成
            player[i].initialize(i + 1, "Lv01", "localhost", 6000); //初期コマンド送信
            thread[i].start(); //並列動作開始
        }
        player[0].m_debugLv01 = true;
        System.out.println("試合への登録終了");
    }
}
