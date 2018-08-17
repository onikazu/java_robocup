//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//キック・移動・視線目標を元に動作する
public class PlayerLv21 extends PlayerLv20 {
    //メンバ:
    private boolean m_debugLv21 = false;
    //==============================================
    //コンストラクタ
    public PlayerLv21() {
        super();
    }
    //==============================================
    //キック
    protected void kick() {
        int t = m_iTime;
        DecimalFormat f = new DecimalFormat("###0.00");
        //キックの目標方向を計算する
        double kickDir =
            getDirection(m_dX[t], m_dY[t], m_dKickX[t], m_dKickY[t]);
        double kickAngle = normalizeAngle(kickDir - m_dBody[t]);
        //全力で蹴るキックコマンドを作る
        m_strCommand[t] = "(kick 100 " + f.format(kickAngle) + ")";
    }
    //==============================================
    //目的地に移動する
    protected void move() {
        int t = m_iTime;
        DecimalFormat f = new DecimalFormat("###0.00");
        double moveDir =
            getDirection(m_dX[t], m_dY[t], m_dMoveX[t], m_dMoveY[t]);
        double moveDist =
            getDistance(m_dX[t], m_dY[t], m_dMoveX[t], m_dMoveY[t]);
        double kickable_area = player_size + ball_size + kickable_margin;
        //目的地にすでに到着しているならば行動しない
        if (moveDist < kickable_area) {
            m_strCommand[t] = "(turn 0)";
            return;
        }
        double turn = normalizeAngle(moveDir - m_dBody[t]);
        double speed = Math.sqrt(m_dVX[t] * m_dVX[t] + m_dVY[t] * m_dVY[t]);
        double turn_moment = turn * (1 + inertia_moment * speed * player_decay);
        double dist = getDistance(m_dX[t], m_dY[t], m_dMoveX[t], m_dMoveY[t]);
        double dash_power_max = 100.0;
        //ダッシュパワーを計算する
        double dash_power = 40.0;
        if (checkNearest(m_dMoveX[t], m_dMoveY[t])) {
            dash_power = Math.max(40.0, m_dStamina[t] / stamina_max * 100.0);
        }
        //到着するまでのステップ数を計算する
        double d = dist;
        int count = 0;
        while (d > kickable_area && count < 50) {
            //1カウントごとの計算をする
            speed =
                    speed * player_decay
                            + dash_power * m_dEffort[m_iTime] * dash_power_rate;
            d -= speed;
            count++;
        }
        //回転角度の検査を行い、行動の種類を決める
        if (Math.abs(turn) < 20.0) {
            turn = 0.0;
            dist = getDistance(m_dX[t], m_dY[t], m_dMoveX[t], m_dMoveY[t]);
            if (dist > 0.75) {
                //前進コマンドの作成
                m_strCommand[t] = "(dash " + f.format(dash_power) + ")";
            }
        } else if (Math.abs(turn) > 160.0 && dist < 3.51) {
            turn = 0.0;
            if (dist > 0.75) {
                //後進コマンドの作成
                m_strCommand[t] = "(dash " + f.format(-dash_power) + ")";
            }
        } else if (Math.abs(turn_moment) <= 180.0) {
            //回転コマンドの作成
            m_strCommand[t] = "(turn " + f.format(turn_moment) + ")";
            if (m_debugLv21) {
                System.out.println();
                System.out.println();
                System.out.print(" t=" + t);
                System.out.print(" m_dMoveX[t]=" + f.format(m_dMoveX[t]));
                System.out.print(" m_dMoveY[t]=" + f.format(m_dMoveY[t]));
                System.out.print(" moveDir=" + f.format(moveDir));
                System.out.print(" moveDist=" + f.format(moveDist));
                System.out.print(
                    " m_dHeadAngle[t]=" + f.format(m_dHeadAngle[t]));
                System.out.print(" m_dNeck[t]=" + f.format(m_dNeck[t]));
                System.out.print(" m_dBody[t]=" + f.format(m_dBody[t]));
                System.out.print(" speed=" + f.format(speed));
                System.out.print(" turn=" + f.format(turn));
                System.out.print(" turn_moment=" + f.format(turn_moment));
            }
        } else {
            //回転できないほど速度が出ているので
            //減速コマンドの作成
            double speed_dir = getDirection(0, 0, m_dVX[t], m_dVY[t]);
            //スタミナからくるダッシュ量の調整
            double rate = dash_power_rate * m_dEffort[t];
            if (Math.abs(normalizeAngle(speed_dir - moveDir)) < 20.0) {
                //後進しているので前に加速する
                dash_power = Math.max(speed * player_decay / rate, dash_power);
            } else {
                //前進しているので後ろに加速する
                dash_power =
                    Math.min(-speed * player_decay / rate, -dash_power);
            }
            m_strCommand[t] =
                "(dash " + f.format(dash_power) + ")(say " + turn_moment + ")";
        }
    }
    //==============================================
    //ボールが見えているときの行動を決定する
    protected void playWithBall() {
        int t = m_iTime;
        super.playWithBall();
        if (m_debugLv21) {
            DecimalFormat f = new DecimalFormat("###0.00");
            System.out.println();
            System.out.print(" 時刻" + t);
            System.out.print(" 視覚" + m_iVisualTime);
            System.out.print(" player" + m_iNumber);
            System.out.print(" 位置(" + f.format(m_dX[t]));
            System.out.print(" ," + f.format(m_dY[t]) + ")");
            System.out.print(" 速度(" + f.format(m_dVX[t]));
            System.out.print(" ," + f.format(m_dVY[t]) + ")");
            System.out.print(" 首=" + f.format(m_dNeck[t]));
            System.out.print(" 体=" + f.format(m_dBody[t]));
            System.out.print(" ball(" + f.format(m_dBallX[t]));
            System.out.print(" ," + f.format(m_dBallY[t]) + ")");
            System.out.print(" 速度(" + f.format(m_dBallVX[t]));
            System.out.print(" ," + f.format(m_dBallVY[t]) + ")");
            System.out.print(" コマンド=" + m_strCommand[t]);
            System.out.print(" \t時刻" + t);
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv21[] player = new PlayerLv21[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            if (i < 11)
                teamname += "Left";
            else
                teamname += "Right";
            player[i] = new PlayerLv21();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[9].m_debugLv21 = true;
        System.out.println("試合への登録終了");
    }
}