//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//目標位置を指定時刻に通るキックを実現する
public class PlayerLv24 extends PlayerLv23 {
    //メンバ:
    private boolean m_debugLv24 = false;
    //==============================================
    //キックの目標位置
    protected void setKickTarget() {
        int t = m_iTime;
        //相手ゴール
        if (m_strSide.startsWith("r")) {
            m_dKickX[t] = -52.5;m_dKickY[t] = 0.0;m_iKickTime[t] = m_iTime + 1;
        } else {
            m_dKickX[t] = 52.5;m_dKickY[t] = 0.0;m_iKickTime[t] = m_iTime + 1;
        }
        //デバッグ用
        if (m_strTeamName.startsWith("PlayerLv24")) {
            if (m_iNumber == 10) {
                //10番のキック目標
                m_dKickX[t] = -1;m_dKickY[t] = 5.0;m_iKickTime[t] = t + 5;
            } else if (m_iNumber == 11) {
                //11番のキック目標
                m_dKickX[t] = -8.0;m_dKickY[t] = 15.0;m_iKickTime[t] = t + 10;
            } else {
                //その他の選手はボールを停止させる
                m_dKickX[t] = m_dBallX[m_iTime];m_dKickY[t] = m_dBallY[m_iTime];
                m_iKickTime[t] = m_iTime + 10;
            }
        }
    }
    //==============================================
    //１ステップ後の目標にキックする
    protected void kick(double kickX, double kickY) {
        int t = m_iTime;
        DecimalFormat f = new DecimalFormat("###0.00");
        //ボールにかかる加速度
        double kickAX = kickX - m_dBallX[t] - m_dBallVX[t];
        double kickAY = kickY - m_dBallY[t] - m_dBallVY[t];
        //ボールと体の角度の差の計算
        double ball_rad =
            Math.atan2(m_dBallY[t] - m_dY[t], m_dBallX[t] - m_dX[t]);
        double dir = normalizeAngle(180 / Math.PI * ball_rad - m_dBody[t]);
        double dir_diff = Math.abs(dir / 180);
        //ボールと体の距離の差の計算
        double dist = getDistance(m_dX[t], m_dY[t], m_dBallX[t], m_dBallY[t]);
        double dist_diff = (dist - player_size - ball_size) / kickable_margin;
        double rate =
            kick_power_rate * (1 - 0.25 * dir_diff - 0.25 * dist_diff);
        //ボールを蹴る
        double rad = Math.atan2(kickAY, kickAX);
        double kick_dir = normalizeAngle(Math.toDegrees(rad) - m_dBody[t]);
        double kick_power = Math.sqrt(kickAX * kickAX + kickAY * kickAY) / rate;
        m_strCommand[t] =
            "(kick " + f.format(kick_power) + " " + f.format(kick_dir) + ")";
    }
    //==============================================
    //キック
    protected void kick() {
        //目標方向
        int t = m_iTime;
        double dx = m_dKickX[t] - m_dBallX[t];
        double dy = m_dKickY[t] - m_dBallY[t];
        double rad = (Math.atan2(dy, dx));
        //到着時刻と現在時刻の差
        int steps = m_iKickTime[t] - m_iTime;
        double s = (1 - Math.pow(ball_decay, steps)) / (1 - ball_decay);
        //次の1ステップ目にどこにボールがくるかの目標距離の計算
        double dist = Math.sqrt(dx * dx + dy * dy) / s;
        //絶対座標へ変換
        double kickX = dist * Math.cos(rad) + m_dBallX[t];
        double kickY = dist * Math.sin(rad) + m_dBallY[t];
        kick(kickX, kickY);
        if (m_strTeamName.startsWith("PlayerLv24")) {
            System.out.println();
            DecimalFormat f = new DecimalFormat("###0.00");
            System.out.print(" 時刻" + m_iTime);
            System.out.print(" 背番号" + m_iNumber);
            System.out.print(" ボール位置("+ f.format(m_dBallX[m_iTime]));
            System.out.print(" ,"+ f.format(m_dBallY[m_iTime])+ ")");
            System.out.print(" 速度("+ f.format(m_dBallVX[m_iTime]));
			System.out.print(" ,"+ f.format(m_dBallVY[m_iTime])+ ")");
            System.out.print(" キック目標");
            System.out.print(" 時刻" + m_iKickTime[t]);
            System.out.print(" 位置(" + f.format(m_dKickX[m_iTime]));
            System.out.print(" ," + f.format(m_dKickY[m_iTime]) + ")");
            System.out.print(" 1ステップ後のキック目標");
            System.out.print(
                " (" + f.format(kickX) + " ," + f.format(kickY) + ")");
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv24[] player = new PlayerLv24[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 11; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            if (i < 11)
                teamname += "Left";
            else
                teamname += "Right";
            player[i] = new PlayerLv24();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[9].m_debugLv24 = true;
        System.out.println("試合への登録終了");
    }
}
