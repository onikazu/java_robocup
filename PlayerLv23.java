//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//ボールトラップをする位置を計算する
public class PlayerLv23 extends PlayerLv22 {
    //メンバ:
    //トラップ計算をするときの余裕（大きいほど誤差に強くなる）
    double m_iTrapMarginSteps = 3;
    double player_speed_max = 1.20;
    private boolean m_debugLv23 = false;
    //==============================================
    //トラップの場所を計算する
    protected void setTrapPosition() {
        int t = m_iTime;
        int moveTurn = 2;
        double dash_power = 100.0 * m_dEffort[t] * m_dStamina[t] / stamina_max;
        setTrapPosition(dash_power, moveTurn);
        //トラップ場所までの距離を計算する
        double d = getDistance(m_dTrapX, m_dTrapY, m_dX[t], m_dY[t]);
        //体の回転角度が何度になっているか確かめる
        double rad = Math.atan2(m_dTrapY - m_dY[t], m_dTrapX - m_dX[t]);
        double turnAngle =
            Math.abs(normalizeAngle(Math.toDegrees(rad) - m_dBody[t]));
        //トラップ場所の座標を計算する
        if (turnAngle < 10.0 || turnAngle > 170.0) {
            //回転せず、前進・後進させる
            moveTurn = 0;
            setTrapPosition(dash_power, moveTurn);
        } else if (turnAngle < 60.0) {
            // 回転を行う
            moveTurn = 1;
            setTrapPosition(dash_power, moveTurn);
        } else {
            //減速して回転を行う
            moveTurn = 2;
            setTrapPosition(dash_power, moveTurn);
        }
        //デバッグ出力
        if (m_debugLv23 && m_iTime > 0 && m_iTime < 40) {
            DecimalFormat f = new DecimalFormat("###0.00");
            System.out.println();
            System.out.print(" 時刻" + m_iTime);
            System.out.print(" 視覚" + m_iVisualTime);
            System.out.print(" トラップ=" + m_iTrapTime);
            System.out.print(" 場所(" + f.format(m_dTrapX));
            System.out.print(" " + f.format(m_dTrapY) + ")");
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
    //トラップの場所を計算するサブメソッド
    protected void setTrapPosition(double dash_power, int stable_steps) {
        //パラメータ
        //dash ダッシュパワー
        //stable_steps ターンコマンドなどを実行するため、ダッシュしないステップ数
        int t = m_iTime;
        int next = (t + 1) % GAME_LENGTH;
        double kickable_area = player_size + ball_size + kickable_margin;
        double cover_area = kickable_area;
        double ball_dist =
            getDistance(m_dX[t], m_dY[t], m_dBallX[t], m_dBallY[t]);
        double player_speed = 0.0;
        //キックできる範囲内にボールが入るまで繰り返す
        while (cover_area < ball_dist && Math.abs(t - next) < 100) {
            //ボールの将来位置の予測
            m_dBallX[next] = m_dBallX[t] + m_dBallVX[t];
            m_dBallY[next] = m_dBallY[t] + m_dBallVY[t];
            m_dBallVX[next] = m_dBallVX[t] * ball_decay;
            m_dBallVY[next] = m_dBallVY[t] * ball_decay;
            //選手の計算開始位置の予測
            m_dX[next] = m_dX[t] + m_dVX[t];
            m_dY[next] = m_dY[t] + m_dVY[t];
            m_dVX[next] = m_dVX[t] * player_decay;
            m_dVY[next] = m_dVY[t] * player_decay;
            m_dBody[next] = m_dBody[t];
            m_dNeck[next] = m_dNeck[t];
            //減速をして回転をする場合も考えて、
            //指定されたステップ内では守備範囲を拡大しない。
            double current_power = 0.0;
            if ((t - m_iTime) >= stable_steps + m_iTrapMarginSteps) //3はマージン
                {
                current_power = Math.abs(dash_power);
            }
            double speed =
                player_speed * player_decay + current_power * dash_power_rate;
            player_speed = Math.min(speed, player_speed_max);
            //選手のキック範囲の予測
            cover_area += (player_speed * 0.9);
            //時刻を１ずらす
            t = (t + 1) % GAME_LENGTH;
            next = (t + 1) % GAME_LENGTH;
            ball_dist = getDistance(m_dX[t], m_dY[t], m_dBallX[t], m_dBallY[t]);
        }
        //結果を保存する
        m_dTrapX = m_dBallX[t];
        m_dTrapY = m_dBallY[t];
        m_iTrapTime = t;
    }
    //==============================================
    //パラメータの読み込み(プレイヤータイプ)
    protected void analyzePlayerType(String message) {
        super.analyzePlayerType(message);
        String type = m_strPlayerType[m_iPlayerType];
        double player_speed_max = getParam(type, "player_speed_max", 1);
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv23[] player = new PlayerLv23[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            if (i < 11)
                teamname += "Left";
            else
                teamname += "Right";
            player[i] = new PlayerLv23();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[2].m_debugLv23 = true;
        System.out.println("試合への登録終了");
    }
}
