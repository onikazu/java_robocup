//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//自分の位置の予測（moveコマンド）
public class PlayerLv12 extends PlayerLv11 {
    //メンバ:
    protected double[] m_dX, m_dY; //プレイヤーの座標
    protected double[] m_dVX, m_dVY; //プレイヤーの速度
    protected double[] m_dAX, m_dAY; //プレイヤーの加速度
    protected double[] m_dNeck, m_dBody; //プレイヤーの体・首方向
    protected double[] m_dStamina; //プレイヤーのスタミナ
    protected double[] m_dEffort; //プレイヤーのダッシュ効率
    protected double[] m_dRecovery; //プレイヤーの回復力
    private boolean m_debugLv12 = false; //デバッグ用フラグ
    //==============================================
    //コンストラクタ
    public PlayerLv12() {
        super();
        m_dX = new double[GAME_LENGTH];    m_dY = new double[GAME_LENGTH];
        m_dVX = new double[GAME_LENGTH];   m_dVY = new double[GAME_LENGTH];
        m_dAX = new double[GAME_LENGTH];   m_dAY = new double[GAME_LENGTH];
        m_dNeck = new double[GAME_LENGTH]; m_dBody = new double[GAME_LENGTH];
        m_dStamina = new double[GAME_LENGTH];
        m_dEffort = new double[GAME_LENGTH];
        m_dRecovery = new double[GAME_LENGTH];
        int i;
        for (i = 0; i < GAME_LENGTH; i++) {
            m_strCommand[i] = "";
        }
    }
    //==============================================
    //予測を作る（Moveコマンド）
    protected void predictMoveCommand(int i) {
        String command = m_strCommand[i];
        //moveコマンドの解析
        if (command.startsWith("(move")) {
            //コマンドパラメータの取得
            double x = getParam(command, "move", 1);
            double y = getParam(command, "move", 2);
            //右側のフィールドの場合の座標の補正
            if (m_strSide.startsWith("r")) {
                x = -x;  y = -y;
            }
            //予測の決定
            m_dX[i] = x; m_dY[i] = y;
            m_dAX[i] = m_dVX[i] = 0.0;
            m_dAY[i] = m_dVY[i] = 0.0;
        }
        //次のタイミングのフィールド上の予測も行う
        int next = (i + 1) % GAME_LENGTH;
        m_dX[next] = m_dX[i];
        m_dY[next] = m_dY[i];
    }
    //==============================================
    //予測を作る
    protected void predict(int start, int end) {
        super.predict(start, end);
        //デバッグ用出力
        if (m_debugLv12 && m_iTime > 0 && m_iTime < 20) {
            DecimalFormat f = new DecimalFormat("###0.00");
            System.out.println();
            System.out.print(" 時刻" + m_iTime);
            System.out.print(" 位置(" + f.format(m_dX[m_iTime]));
            System.out.print("," + f.format(m_dY[m_iTime]) + ")");
        }
    }
    //==============================================
    //初期メッセージの解析
    protected void analyzeInitialMessage(String message) {
        //いままで開発した内容の実行
        super.analyzeInitialMessage(message);
        //moveコマンドが実行される前に3m間隔で画面上部に並んだときの座標を計算する
        if (m_strSide.startsWith("r")) {
            m_dX[0] = 3 + 3 * m_iNumber;  m_dY[0] = -37.0;
        } else {
            m_dX[0] = -3 - 3 * m_iNumber; m_dY[0] = -37.0;
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv12[] player = new PlayerLv12[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 1; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            player[i] = new PlayerLv12();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[0].m_debugLv12 = true;
        System.out.println("試合への登録終了");
    }
}
