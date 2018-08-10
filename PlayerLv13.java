//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//自分の位置の予測（dashコマンド）
public class PlayerLv13 extends PlayerLv12 {
    //メンバ:
    double dash_power_rate = 0.006; //ダッシュ係数
    double player_decay = 0.4; //プレイヤー速度のステップごとの減衰率
    private boolean m_debugLv13 = false;
    //==============================================
    //予測を作る
    protected void predictDashCommand(int i) {
        String command = m_strCommand[i];
        if (command.startsWith("(dash")) {
            //dashコマンドの解析
            double dash_power = getParam(command, "dash", 1);
            double p = Math.PI;
            double rad = m_dBody[i] * Math.PI / 180;
            //X成分・Y成分の加速度の計算
            double ax = dash_power * dash_power_rate * Math.cos(rad);
            double ay = dash_power * dash_power_rate * Math.sin(rad);
            m_dAX[i] = ax; m_dAY[i] = ay;
        }
        ///フィールド上の次ステップの速度・位置・加速度の予測
        int next = (i + 1) % GAME_LENGTH;
        m_dVX[next] = (m_dVX[i] + m_dAX[i]) * player_decay;
        m_dVY[next] = (m_dVY[i] + m_dAY[i]) * player_decay;
        m_dX[next] = m_dX[i] + m_dVX[i] + m_dAX[i];
        m_dY[next] = m_dY[i] + m_dVY[i] + m_dAY[i];
        m_dAX[next] = 0.0; m_dAY[next] = 0.0;
    }
    //==============================================
    //予測を作る
    protected void predict(int start, int end) {
        super.predict(start, end);
        //デバッグ出力
        if (m_debugLv13 && 0 < m_iTime && m_iTime < 50) {
            DecimalFormat f = new DecimalFormat("###0.00");
            DecimalFormat g = new DecimalFormat("###0.000");
            System.out.println();
            System.out.print(" 時刻" + m_iTime);
            System.out.print(" 位置(" + f.format(m_dX[m_iTime]));
            System.out.print(" ," + f.format(m_dY[m_iTime]) + ")");
            System.out.print(" 速度(" + f.format(m_dVX[m_iTime]));
            System.out.print(" ," + f.format(m_dVY[m_iTime]) + ")");
        }
    }
    //==============================================
    //行動を決定する
    protected void play() {
        super.play();
        if (m_strPlayMode.startsWith("kick_off")) {
            String command;
            if (m_iTime % 10 < 5) command = "(dash 100)";
            else command = "(dash -100)";
            m_strCommand[m_iTime] = command;
        }
    }
    //==============================================
    //サーバパラメータを解析する
    protected void analyzeServerParam(String message) {
        //前回まで開発した内容の実行
        super.analyzeServerParam(message);
        //サーバパラメータ内でこのクラスの計算に必要なものを得る
        player_decay = getParam(m_strServerParam, "player_decay", 1);
        dash_power_rate = getParam(m_strServerParam, "dash_power_rate", 1);
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv13[] player = new PlayerLv13[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 11; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            player[i] = new PlayerLv13();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[0].m_debugLv13 = true;
        System.out.println("試合への登録終了");
    }
}
