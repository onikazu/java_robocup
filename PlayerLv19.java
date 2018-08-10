//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//首を動かしてボールを捜す選手
public class PlayerLv19 extends PlayerLv18 {
    //メンバ:
    protected int m_iBallTime = -6;
    protected int m_iSearchCount = 0;
    private boolean m_debugLv19 = false;
    //==============================================
    //視覚情報の解析を行う
    protected void analyzeVisualMessage(String message) {
        //前回まで開発した内容を実行する
        super.analyzeVisualMessage(message);
        //視覚情報到着時にボールを見失ったか検査する
        if (message.indexOf("(b") > -1) {
            //ボールが見えている時は探索カウントを常に0にする
            m_iBallTime = m_iVisualTime;
            m_iSearchCount = 0;
        } else if (checkFresh(m_iBallTime) == false) {
            //ボールを見失っている時は探索カウントを検査し、
            //0まで減っているときはカウントを9とする。
            if (m_iSearchCount == 0 && checkInitialMode() == false) {
                m_iSearchCount = 9;
            }
        }
    }
    //==============================================
    //時刻が最近の時刻であるか検査する
    protected boolean checkFresh(int time) {
        if (m_iTime - time > 3) return false;
        else return true;
    }
    //==============================================
    //ボールを探す
    protected void searchBall(int searchCount) {
        //ボールを探すための行動をカウントによって使い分ける
        int t = m_iTime;
        if (m_iSearchCount == 9) {
            m_strCommand[t] += "(turn_neck 180)";             //右を向く
            m_strCommand[t] += "(change_view wide high)";
        }
        if (m_iSearchCount == 6) {
            m_strCommand[t] += "(turn_neck -180)";            //左を向く
            m_strCommand[t] += "(change_view wide high)";
        }
        if (m_iSearchCount == 3) {
            m_strCommand[t] = "(turn 180)";                   //後ろを向く
            m_strCommand[t] += "(turn_neck 90)";
            m_strCommand[t] += "(change_view wide high)";
        }
    }
    //==============================================
    //座標で指定された目標に首を向ける
    protected void lookAt(double faceX, double faceY) {
        int t = m_iTime;    //時刻t=m_iTimeのturnコマンドを解析する
        double turn_angle = 0.0;
        String command = m_strCommand[t];
        if (command.startsWith("(turn ")) {
            double moment = getParam(command, "turn", 1);
            double vx = m_dVX[t];
            double vy = m_dVY[t];
            double speed = Math.sqrt(vx * vx + vy * vy);
            turn_angle = moment / (1 + inertia_moment * speed);
        }
        //次ステップに首を向ける方向（目標方向）を計算する
        DecimalFormat f = new DecimalFormat("###0.00");
        double face_dir = getDirection(m_dX[t], m_dY[t], faceX, faceY);
        //首と体の方向と目標方向の差を求める
        double neck_diff = normalizeAngle(face_dir - turn_angle - m_dNeck[t]);
        double body_diff = normalizeAngle(face_dir - turn_angle - m_dBody[t]);
        //首が体の方向から左右９０度を超えて回転するときの補正を行う
        if (m_dHeadAngle[t] + neck_diff > maxneckang)
            neck_diff = normalizeAngle(maxneckang - m_dHeadAngle[t]);
        else if (m_dHeadAngle[t] + neck_diff < minneckang)
            neck_diff = normalizeAngle(minneckang - m_dHeadAngle[t]);
        //デバッグ用の出力
        if (m_debugLv19 && t > 0 && t < 30) {
            System.out.println();
            System.out.print(" 時刻" + t);
            System.out.print(" 視覚" + m_iVisualTime);
            System.out.print(" 目標");
            System.out.print(" 位置(" + f.format(m_dBallX[t]));
            System.out.print(" ," + f.format(m_dBallY[t]) + ")");
            System.out.print(" 方向=" + f.format(face_dir));
            System.out.print(" 自分");
            System.out.print(" 位置(" + f.format(m_dX[t]));
            System.out.print(" ," + f.format(m_dY[t]) + ")");
            System.out.print(" 首=" + f.format(m_dNeck[t]));
            System.out.print(" 体=" + f.format(m_dBody[t]));
        }
        //目標までの距離が短く体の正面からの角度が少ないときは視野を狭める
        if (Math.abs(body_diff) < 90 + 22.5 / 2
            && Math.abs(neck_diff) < 22.5) {
            m_strCommand[m_iTime] += "(turn_neck " + f.format(neck_diff) + ")";
            m_strCommand[m_iTime] += "(change_view narrow high)";
        } else if (
            Math.abs(body_diff) < 90 + 45.0 / 2
                && Math.abs(neck_diff) < 45.0) {
            m_strCommand[m_iTime] += "(turn_neck " + f.format(neck_diff) + ")";
            m_strCommand[m_iTime] += "(change_view normal high)";
        } else {
            m_strCommand[m_iTime] += "(turn_neck " + f.format(neck_diff) + ")";
            m_strCommand[m_iTime] += "(change_view wide high)";
        }
    }
    //==============================================
    //ボールが見えているときの行動を決定する
    protected void playWithBall() {
        int t = m_iTime;
        m_strCommand[t] = "(turn 0)";
        lookAt(m_dBallX[t], m_dBallY[t]);
    }
    //==============================================
    //行動を決定する
    protected void play() {
        int t = m_iTime;
        m_strCommand[t] = "(turn 0)";
        //初期フォーメーションに移動する必要があるかどうか検査する
        if (checkInitialMode()) {
            //Moveコマンドを実行する
            if (checkInitialMode()) {
                setKickOffPosition();
                String command ="(move " + m_dKickOffX + " " + m_dKickOffY + ")";
                m_strCommand[t] = command;
            }
        }
        //そうでない場合
        else {
            //通常の動作を行う
            if (Math.abs(m_dNeck[t]) > 180.0) return;
            if (Math.abs(m_dBody[t]) > 180.0) return;
            if (t > 0) {
                //ボールが視野に入っているか検査する
                if (checkFresh(m_iBallTime) == false){
                    searchBall(m_iSearchCount); //ボールが見えていないときの行動
                } else {
                    playWithBall();  //ボールが見えているときの行動
                }
            }
        }
        //ボール検索カウンタを減らす
        m_iSearchCount--;
        if (m_iSearchCount < 0) m_iSearchCount = 0;
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv19[] player = new PlayerLv19[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            if (i < 11) teamname += "Left"; else teamname += "Right";
            player[i] = new PlayerLv19();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[5].m_debugLv19 = true;
        System.out.println("試合への登録終了");
    }
}