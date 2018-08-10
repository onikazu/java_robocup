//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//スタミナモデル計算つきのダッシュ予測
public class PlayerLv15 extends PlayerLv14 {
    private boolean m_debugLv15 = false;
    //==============================================
    //予測を作る（dashコマンド)
    protected void predictDashCommand(int i) {
        String command = m_strCommand[i];
        double stamina = m_dStamina[i];
        double recovery = m_dRecovery[i];
        double effort = m_dEffort[i];
        //スタミナモデル計算つきのダッシュ
        if (command.startsWith("(dash")) {
            //(1)スタミナを限界まで使うと予備スタミナが消費される。
            double dash_power = getParam(command, "dash", 1);
            if (dash_power < 0)
                dash_power = -dash_power * 2.0;
            if (stamina + extra_stamina < dash_power) {
                //予備スタミナ＋現在のスタミナを使い尽くした場合の処理
                dash_power = stamina + extra_stamina;
                stamina = 0.0;
                extra_stamina = 0.0;
            } else if (stamina < dash_power) {
                //予備スタミナを使う場合の処理
                extra_stamina -= (dash_power - stamina);
                stamina = 0.0;
            } else {
                //通常のスタミナ消費の処理
                stamina -= dash_power;
            }
            //(2)回復力減少限界値(recover_dec_thr)未満になると、
            //		回復力がrecover_decずつ減少する
            if (stamina <= recover_dec_thr * stamina_max) {
                if (recovery > recover_min)
                    recovery -= recover_dec;
                recovery = Math.max(recovery, recover_min);
            }
            //(3)ダッシュ実行効率減少限界値(effort_dec_thr)未満になると、
            //		ダッシュ実行率がeffot_decずつ減る
            if (stamina <= effort_dec_thr * stamina_max) {
                if (effort > effort_min)
                    effort -= effort_dec;
                effort = Math.max(effort, effort_min);
            }
            //(4)ダッシュ実行効率増大限界値(effort_inc_thr)より大きくなると、
            //		ダッシュ実行率がeffot_incずつ増える。
            if (stamina >= effort_inc_thr * stamina_max) {
                if (effort < effort_max)
                    effort += effort_inc;
                effort = Math.max(effort, effort_max);
            }
            //(5)後進dashコマンドのための補正
            if (getParam(command, "dash", 1) < 0)
                dash_power /= (-2.0);
            double p = Math.PI;
            double rad = m_dBody[i] * Math.PI / 180;
            double ax =
                dash_power * dash_power_rate * m_dEffort[i] * Math.cos(rad);
            double ay =
                dash_power * dash_power_rate * m_dEffort[i] * Math.sin(rad);
            m_dAX[i] = ax;
            m_dAY[i] = ay;
        }
        //(6)ステップごとにスタミナはrecovry*stamina_inc_maxずつ徐々に増える
        stamina += recovery * stamina_inc_max;
        stamina = Math.min(stamina, stamina_max);
        //自分のスタミナモデル状態の結果格納
        int next = (i + 1) % GAME_LENGTH;
        m_dStamina[next] = stamina;
        m_dEffort[next] = effort;
        m_dRecovery[next] = recovery;
        //フィールド上の予測
        m_dVX[next] = (m_dVX[i] + m_dAX[i]) * player_decay;
        m_dVY[next] = (m_dVY[i] + m_dAY[i]) * player_decay;
        m_dX[next] = m_dX[i] + m_dVX[i] + m_dAX[i];
        m_dY[next] = m_dY[i] + m_dVY[i] + m_dAY[i];
        m_dAX[next] = 0.0;
        m_dAY[next] = 0.0;
    }
    //==============================================
    //予測を作る
    protected void predict(int start, int end) {
        super.predict(start, end);
        //デバッグ出力
        if (m_debugLv15 && 0 < m_iTime && m_iTime < 50) {
            DecimalFormat f = new DecimalFormat("###0.00");
            DecimalFormat g = new DecimalFormat("###0.000");
            System.out.println();
            System.out.print(" 時刻" + m_iTime);
            System.out.print(" スタミナ=" + f.format(m_dStamina[m_iTime]));
            System.out.print(" 実行効率=" + g.format(m_dEffort[m_iTime]));
            System.out.print(" 回復力=" + g.format(m_dRecovery[m_iTime]));
            System.out.print(" 位置(" + f.format(m_dX[m_iTime]));
            System.out.print(" ," + f.format(m_dY[m_iTime]) + ")");
            System.out.print(" 速度(" + f.format(m_dVX[m_iTime]));
            System.out.print(" ," + f.format(m_dVY[m_iTime]) + ")");
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv15[] player = new PlayerLv15[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 1; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            player[i] = new PlayerLv15();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[0].m_debugLv15 = true;
        System.out.println("試合への登録終了");
    }
}
