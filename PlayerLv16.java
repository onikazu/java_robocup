//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//自分の体と首の方向の予測（turn,turn_neckコマンド）
public class PlayerLv16 extends PlayerLv15 {
    //メンバ:
    double inertia_moment = 5.0; //回転モーメント
    double maxneckang = 90.0; //首が時計方向へ回る最大値
    double minneckang = -90.0; //首が反時計方向へ回る最小値
    protected double[] m_dHeadAngle;
    private boolean m_debugLv16 = false;
    //==============================================
    //コンストラクタ
    public PlayerLv16() {
        super();
        m_dHeadAngle = new double[GAME_LENGTH];
    }
    //==============================================
    //体調メッセージの解析
    protected void analyzePhysicalMessage(String message) {
        //前回までに開発した内容を実行する
        super.analyzePhysicalMessage(message);
        if (m_dNeck[m_iTime] == OUT_OF_RANGE)
            return;
        //地面に対するスピードの計算
        //パラメータの取得と速度の大きさの計算
        double speed = getParam(message, "speed", 1);
        double speed_angle = getParam(message, "speed", 2);
        double rad =
            normalizeAngle(m_dNeck[m_iTime] + speed_angle) * Math.PI / 180.0;
        //X方向・Y方向の速度成分の計算
        double vx = speed * Math.cos(rad);
        double vy = speed * Math.sin(rad);
        m_dVX[m_iTime] = vx;
        m_dVY[m_iTime] = vy;
        //体の方向の計算
        double head_angle = getParam(message, "head_angle", 1);
        double body_angle = normalizeAngle(m_dNeck[m_iTime] - head_angle);
        m_dHeadAngle[m_iTime] = head_angle;
        m_dBody[m_iTime] = body_angle;
    }
    //==============================================
    //turnコマンド,turn_neckコマンドの予測を作る
    protected void predictTurnCommand(int i) {
        int next = (i + 1) % GAME_LENGTH;
        if (m_dNeck[i] == OUT_OF_RANGE)
            return;
        String command = m_strCommand[i];
        //turnコマンドの解析
        if (command.startsWith("(turn ")) {
            //パラメータの取得
            double moment = getParam(command, "turn", 1);
            //選手のスピードの計算
            double vx = m_dVX[i];
            double vy = m_dVY[i];
            double speed = Math.sqrt(vx * vx + vy * vy);
            //実際の体の回転角度を計算する
            double turn_angle = moment / (1 + inertia_moment * speed);
            m_dNeck[next] = normalizeAngle(m_dNeck[i] + turn_angle);
            m_dBody[next] = normalizeAngle(m_dBody[i] + turn_angle);
        } else {
            m_dNeck[next] = m_dNeck[i];
            m_dBody[next] = m_dBody[i];
        }
        int index0 = command.indexOf("(turn_neck");
        //turn_neckコマンドの解析
        if (index0 > -1) {
            //パラメータの取得
            int index1 = command.indexOf(" ", index0 + 9);
            int index2 = command.indexOf(")", index1 + 1);
            double angle =
                Double.parseDouble(
                    command.substring(index1, index2).toString());
            //首が体の正面から左右に90度以上曲がらないか検査する
            double head_angle = normalizeAngle(m_dNeck[i] - m_dBody[i]);
            if (maxneckang < head_angle + angle) {
                //時計回り側に首が曲がりすぎているときの計算
                m_dNeck[next] = normalizeAngle(m_dBody[i] + maxneckang);
            } else if (minneckang > head_angle + angle) {
                //反時計回り側に首が曲がりすぎている時の計算
                m_dNeck[next] = normalizeAngle(m_dBody[i] + minneckang);
            } else {
                //通常の首の回転の場合の計算
                m_dNeck[next] = normalizeAngle(m_dNeck[next] + angle);
            }
        }
    }
    //==============================================
    //予測を作る
    protected void predict(int start, int end) {
        super.predict(start, end);
        //デバッグ出力
        if (m_debugLv16 && 0 < m_iTime && m_iTime < 20) {
            DecimalFormat f = new DecimalFormat("###0.00");
            DecimalFormat g = new DecimalFormat("###0.000");
            System.out.println();
            System.out.print(" 時刻" + m_iTime);
            System.out.print(" start" + start);
            System.out.print(" end" + end);
            System.out.print(" スタミナ=" + f.format(m_dStamina[m_iTime]));
            System.out.print(" 実行力=" + g.format(m_dEffort[m_iTime]));
            System.out.print(" 回復力=" + g.format(m_dRecovery[m_iTime]));
            System.out.print(" 位置(" + f.format(m_dX[m_iTime]));
            System.out.print(" ," + f.format(m_dY[m_iTime]) + ")");
            System.out.print(" 速度(" + f.format(m_dVX[m_iTime]));
            System.out.print(" ," + f.format(m_dVY[m_iTime]) + ")");
            System.out.print(" 首=" + f.format(m_dNeck[m_iTime]));
            System.out.print(" 体=" + f.format(m_dBody[m_iTime]));
        }
    }
    //==============================================
    //プレイヤータイプを解析する
    protected void analyzePlayerType(String message) {
        super.analyzePlayerType(message);
        String type = m_strPlayerType[m_iPlayerType];
        inertia_moment = getParam(type, "inertia_moment", 1);
    }
    //==============================================
    //サーバパラメータを解析する
    protected void analyzeServerParam(String message) {
        super.analyzeServerParam(message);
        maxneckang = getParam(message, "maxneckang", 1);
        minneckang = getParam(message, "minneckang", 1);
    }
    //==============================================
    //行動を決定する
    protected void play() {
        //前回まで開発した内容を実行する
        super.play();
        //速度をつけて体を回転させるような動きをさせる
        if (m_strPlayMode.startsWith("kick_off")) {
            //時刻により処理を分ける
            String command = "(turn 0)";
            if (m_iTime == 1)
                command = "(turn 80)";
            else if (m_iTime < 5)
                command = "(dash 100)";
            else if (m_iTime == 5)
                command = "(turn 90)";
            else if (m_iTime < 15)
                command = "(turn 0)(turn_neck -20)";
            else if (m_iTime == 15)
                command = "(turn -30)(turn_neck 90)";
            else if (m_iTime < 18)
                command = "(dash 100)";
            else if (m_iTime == 18)
                command = "(kick 30 45)";
            //コマンドの登録を行う
            m_strCommand[m_iTime] = command;
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv16[] player = new PlayerLv16[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 1; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            player[i] = new PlayerLv16();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[0].m_debugLv16 = true;
        System.out.println("試合への登録終了");
    }
}
