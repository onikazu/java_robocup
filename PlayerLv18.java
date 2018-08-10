//==============================================
//パッケージの読み込み
import java.util.*;
import java.text.*;
import java.awt.geom.*; //Point2D.Doubleを使うため
//==============================================
//自分の視界にあるボールの情報を計算する
public class PlayerLv18 extends PlayerLv17 {
    //メンバ:
    protected int m_iBallTime = 0;
    private boolean m_debugLv18 = false;
    //==============================================
    //視覚メッセージを解析する
    protected void analyzeVisualMessage(String message) {
        super.analyzeVisualMessage(message);
        int t = m_iVisualTime;	                              //時刻の記憶
        m_dX[t] = OUT_OF_RANGE;  m_dY[t] = OUT_OF_RANGE;
        message = getLandMarker(message, m_dX[t], m_dY[t]);   //視覚情報中の大文字の情報を解決する
        m_dNeck[t] = getNeckDir(message);                     //首の方向
        if (m_dNeck[t] == OUT_OF_RANGE) return;
        m_dBody[t] = normalizeAngle(m_dNeck[t] - m_dHeadAngle[t]); //体の方向
        //自分の位置
        Point2D.Double pos = estimatePosition(message, m_dNeck[t], m_dX[t], m_dY[t]); 
        if (pos.x == OUT_OF_RANGE) return;
        m_dX[t] = pos.x; m_dY[t] = pos.y;
        //自分が見えているボール座標の計算
        if (message.indexOf("(b)") == -1) return;
        //パラメータの取得
        m_iBallTime = t;
        String ball = getObjectMessage(message, "((b");
        StringTokenizer st = new StringTokenizer(ball);
        double ball_dist = getParam(message, "(b)", 1);
        double ball_dir = getParam(message, "(b)", 2);
        double rad = Math.toRadians(normalizeAngle(m_dNeck[t] + ball_dir));
        m_dBallX[t] = m_dX[t] + ball_dist * Math.cos(rad);//絶対X座標で保存
        m_dBallY[t] = m_dY[t] + ball_dist * Math.sin(rad);//絶対Y座標で保存
        m_dBallVX[t] = 0; m_dBallVY[t] = 0;
        if (t > 0) {  //ボール速度を座標の差から計算する
            int pre = (t - 1) % GAME_LENGTH;
            m_dBallVX[t] = m_dX[t] - m_dX[pre]; m_dBallVY[t] = m_dY[t] - m_dY[pre];
        }
        //速度を求めるための情報を受け取ったか検査する
        if (st.countTokens() > 4) {
            //スクリーンショットを取るためのデバッグ用コード
            //if(m_debugLv18 && t>19) System.out.println();
            //速度を計算するためのパラメータを受け取る
            double dist_change = getParam(message, "(b)", 3);
            double dir_change = getParam(message, "(b)", 4);
            //自分を中心とした極座標系へ変換する
            double vx = dist_change;
            double vy = dir_change * ball_dist * (Math.PI / 180);
            double ballR = Math.sqrt(vx * vx + vy * vy);
            double ballDeg = Math.toDegrees(Math.atan2(vy, vx));
            //フィールドの座標系上での相対速度に変換する
            double ballDegAbs = normalizeAngle(ball_dir + ballDeg + m_dNeck[t]);
            double ballRad = Math.toRadians(ballDegAbs);
            double vx_r = ballR * Math.cos(ballRad);
            double vy_r = ballR * Math.sin(ballRad);
            //自分の速度を足してボールの絶対速度を求める
            m_dBallVX[t] = vx_r + m_dVX[t];
            m_dBallVY[t] = vy_r + m_dVY[t];
            if (m_debugLv18 && t < 30) {
                DecimalFormat f = new DecimalFormat("###0.00");
                DecimalFormat g = new DecimalFormat("###0.000");
                System.out.println();
                System.out.print(" 時刻" + t + " 位置(" + f.format(m_dX[t]));
                System.out.print(" ," + f.format(m_dY[t]) + ")");
                System.out.print(" 速度(" + f.format(m_dVX[t]));
                System.out.print(" ," + f.format(m_dVY[t]) + ")");
                System.out.print(" 首=" + f.format(m_dNeck[t]));
                System.out.print(" 体=" + f.format(m_dBody[t]));
                System.out.print(" 位置(" + f.format(m_dBallX[t]));
                System.out.print(" ," + f.format(m_dBallY[t]) + ")");
                System.out.print(" 速度(" + f.format(m_dBallVX[t]));
                System.out.print(" ," + f.format(m_dBallVY[t]) + ")");
                System.out.print(" ball=" + ball);
            }
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv18[] player = new PlayerLv18[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 11; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            player[i] = new PlayerLv18();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[9].m_debugLv18 = true;
        System.out.println("試合への登録終了");
    }
}
