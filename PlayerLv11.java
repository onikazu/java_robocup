//==============================================
//過去の視覚情報から現在のフィールド状況を予測する準備
class PlayerLv11 extends PlayerLv10 {
    //定数
    final int GAME_LENGTH = 6000; //試合の長さ
    //変数
    protected String m_strServerParam; //サーバのパラメータ
    protected String m_strPlayerParam; //プレイヤーのパラメータ
    protected String m_strPlayerType[]; //プレイヤータイプ
    protected int m_iPlayerType = 0;
    protected boolean m_debugLv11 = false;
    protected String[] m_strCommand; //コマンド履歴
    protected int m_iTime = -1; //体調情報の時刻
    protected int m_iVisualTime = -1; //視覚情報の時刻
    //==============================================
    //コンストラクタ
    public PlayerLv11() {
        super();
        m_strCommand = new String[GAME_LENGTH];
        m_strPlayerType = new String[20];
    }
    //==============================================
    //サーバパラメータを解析する
    protected void analyzeServerParam(String message) {
        m_strServerParam = message;
    }
    //==============================================
    //プレイヤーパラメータを解析する
    protected void analyzePlayerParam(String message) {
        m_strPlayerParam = message;
    }
    //==============================================
    //プレイヤータイプを解析する
    protected void analyzePlayerType(String message) {
        int id = (int) getParam(message, "id", 1);
        m_strPlayerType[id] = message;
    }
    //==============================================
    //視覚メッセージを解析する
    protected void analyzeVisualMessage(String message) {
        m_iVisualTime = (int) getParam(message, "see", 1);
    }
    //==============================================
    //体調メッセージを解析する
    protected void analyzePhysicalMessage(String message) {
        m_iTime = (int) getParam(message, "sense_body", 1);
    }
    //==============================================
    //フィールドの予測用メソッド
    protected void predictMoveCommand(int i) {    } //このクラスでは空にする
    protected void predictDashCommand(int i) {    } //このクラスでは空にする
    protected void predictTurnCommand(int i) {    } //このクラスでは空にする
    protected void predictKickCommand(int i) {    } //このクラスでは空にする
    //==============================================
    //フィールドの予測を作る
    protected void predict(int start, int end) {
        if (m_iVisualTime < 0)
            return;
        //プレイヤーの位置の予測
        int i;
        for (i = start; i < end; i++) {
            predictMoveCommand(i);
            predictDashCommand(i);
            predictTurnCommand(i);
            predictKickCommand(i);
        }
        if (m_debugLv11 && m_iTime > 0 && m_iTime < 20) {
            System.out.println();
            System.out.print("時刻　体調情報=" + m_iTime);
            System.out.print("視覚情報=" + m_iVisualTime);
        }
    }
    //==============================================
    //行動を決定する
    protected void play() {
        //コマンドのデフォルトを決める
        m_strCommand[m_iTime] = "(turn 0)";
        //キックオフ前か検査し、必要ならば初期位置へ移動する
        if (checkInitialMode()) {
            if (checkInitialMode()) {
                setKickOffPosition();
                String command =
                    "(move " + m_dKickOffX + " " + m_dKickOffY + ")";
                m_strCommand[m_iTime] = command;
            }
        }
    }
    //==============================================
    //サーバから受け取ったメッセージを処理する
    protected void analyzeMessage(String message) {
        //初期メッセージの処理
        if (message.startsWith("(init "))
            analyzeInitialMessage(message);
        //視覚メッセージの処理
        else if (message.startsWith("(see "))
            analyzeVisualMessage(message);
        //体調メッセージの処理
        else if (message.startsWith("(sense_body ")) {
            //体調メッセージを解析する
            analyzePhysicalMessage(message);
            //視覚情報の時刻から現在までの予測を作る
            if (m_iVisualTime < m_iTime)
                predict(m_iVisualTime, m_iTime);
            //予測を基に行動する
            play();
            //コマンドを実行する
            send(m_strCommand[m_iTime]);
        }
        //聴覚メッセージの処理
        else if (message.startsWith("(hear "))
            analyzeAuralMessage(message);
        //サーバパラメータの処理
        else if (message.startsWith("(server_param"))
            analyzeServerParam(message);
        //プレイヤーパラメータの処理
        else if (message.startsWith("(player_param"))
            analyzePlayerParam(message);
        //プレイヤータイプの処理
        else if (message.startsWith("(player_type"))
            analyzePlayerType(message);
        //警告・エラーの処理
        else
            System.err.println("Lv11:サーバからエラーが伝えられた:" + message);
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv11[] player = new PlayerLv11[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 1; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            player[i] = new PlayerLv11();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[0].m_debugLv11 = true;
        System.out.println("試合への登録終了");
    }
}
