//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//ボールの位置の予測(kickコマンド)
public class PlayerLv17 extends PlayerLv16 {
    //メンバ:
    String m_strBall = "";
    protected double[] m_dBallX, m_dBallY; //ボールの座標
    protected double[] m_dBallVX, m_dBallVY; //ボールの速度
    protected double[] m_dBallAX, m_dBallAY; //ボールの加速度
    double ball_size = 0.085; //ボールの大きさ
    double player_size = 0.3; //プレイヤーの大きさ
    double kickable_margin = 0.7; //キック可能距離
    double ball_decay = 0.96; //ボールの1ステップあたりの速度減衰率
    double kick_power_rate = 0.027; //キックパワーが加速度に変更されるときの係数
    private boolean m_debugLv17 = false;
    //==============================================
    //コンストラクタ
    public PlayerLv17() {
        super();
        m_dBallX = new double[GAME_LENGTH];
        m_dBallY = new double[GAME_LENGTH];
        m_dBallVX = new double[GAME_LENGTH];
        m_dBallVY = new double[GAME_LENGTH];
        m_dBallAX = new double[GAME_LENGTH];
        m_dBallAY = new double[GAME_LENGTH];
    }
    //==============================================
    //kickコマンドの予測を作る
    protected void predictKickCommand(int i) {
        int next = (i + 1) % GAME_LENGTH;
        if (m_iVisualTime < 0)
            return;
        String command = m_strCommand[i];
        //kickコマンドの解析
        if (command.startsWith("(kick")) {
            //パラメータを取得する
            double kick_power = getParam(command, "kick", 1);
            double kick_angle = getParam(command, "kick", 2);
            double angle = normalizeAngle(m_dBody[i] + kick_angle);
            double kick_rad = angle * Math.PI / 180;
            //ボールと体の角度の差の計算
            double ball_rad =
                Math.atan2(m_dBallY[i] - m_dY[i], m_dBallX[i] - m_dX[i]);
            double dir = normalizeAngle(180 / Math.PI * ball_rad - m_dBody[i]);
            double dir_diff = Math.abs(dir / 180);
            //ボールと体の距離の差の計算
            double dist =
                getDistance(m_dX[i], m_dY[i], m_dBallX[i], m_dBallY[i]);
            double dist_diff =
                (dist - player_size - ball_size) / kickable_margin;
            if (dist_diff < 1.0) {
                //ボールにかかる加速度の計算
                double ep =
                    kick_power_rate * (1 - 0.25 * dir_diff - 0.25 * dist_diff);
                m_dBallAX[i] = kick_power * ep * Math.cos(kick_rad);
                m_dBallAY[i] = kick_power * ep * Math.sin(kick_rad);
            } else {
                m_dBallAX[i] = 0.0;
                m_dBallAY[i] = 0.0;
            }
        }
        //ボールの移動予測計算
        m_dBallVX[next] = (m_dBallVX[i] + m_dBallAX[i]) * ball_decay;
        m_dBallVY[next] = (m_dBallVY[i] + m_dBallAY[i]) * ball_decay;
        m_dBallX[next] = m_dBallX[i] + m_dBallVX[i] + m_dBallAX[i];
        m_dBallY[next] = m_dBallY[i] + m_dBallVY[i] + m_dBallAY[i];
    }
    //==============================================
    //予測を作る
    protected void predict(int start, int end) {
        super.predict(start, end);
        //デバッグ出力
        if (m_debugLv17 && 0 < m_iTime && m_iTime < 25) {
            DecimalFormat f = new DecimalFormat("###0.00");
            System.out.println();
            System.out.print(" 時刻" + m_iTime);
            System.out.print(" ボール");
            System.out.print(" 位置(" + f.format(m_dBallX[m_iTime]));
            System.out.print(" ," + f.format(m_dBallY[m_iTime]) + ")");
            System.out.print(" 速度(" + f.format(m_dBallVX[m_iTime]));
            System.out.print(" ," + f.format(m_dBallVY[m_iTime]) + ")");
            System.out.print(" 自分");
            System.out.print(" 位置(" + f.format(m_dX[m_iTime]));
            System.out.print(" ," + f.format(m_dY[m_iTime]) + ")");
            System.out.print(" 速度(" + f.format(m_dVX[m_iTime]));
            System.out.print(" ," + f.format(m_dVY[m_iTime]) + ")");
            System.out.print(" 首=" + f.format(m_dNeck[m_iTime]));
            System.out.print(" 体=" + f.format(m_dBody[m_iTime]));
        }
    }
    //==============================================
    //サーバから受け取ったサーバパラメータを処理する
    protected void analyzeServerParam(String message) {
        super.analyzeServerParam(message);
        ball_decay = getParam(m_strServerParam, "ball_decay", 1);
        player_decay = getParam(m_strServerParam, "player_decay", 1);
        ball_size = getParam(m_strServerParam, "ball_size", 1);
        kick_power_rate = getParam(m_strServerParam, "kick_power_rate", 1);
    }
    //==============================================
    //サーバから受け取ったプレイヤタイプを処理する
    protected void analyzePlayerType(String message) {
        super.analyzePlayerType(message);
        String str = m_strPlayerType[m_iPlayerType];
        player_size = getParam(str, "player_size", 1);
        kickable_margin = getParam(str, "kickable_margin", 1);
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv17[] player = new PlayerLv17[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 11; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            player[i] = new PlayerLv17();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[9].m_debugLv17 = true;
        System.out.println("試合への登録終了");
    }
}
