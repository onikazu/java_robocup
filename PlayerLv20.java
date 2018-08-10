//==============================================
//パッケージの読み込み
import java.text.*;
//==============================================
//キック・移動・視線の目標を決定する
public class PlayerLv20 extends PlayerLv19 {
    //メンバ:
    private boolean m_debugLv20 = false;
    protected double[] m_dKickX, m_dKickY; //キックをする目標
    protected double[] m_dMoveX, m_dMoveY; //移動する目標
    protected double[] m_dFaceX, m_dFaceY; //視線を向ける目標
    protected int[] m_iKickTime, m_iMoveTime; //目標を達成する予定時刻
    protected double m_dTrapX; //トラップをする場所X
    protected double m_dTrapY; //トラップをする場所Y
    protected int m_iTrapTime; //トラップをする時刻Time	
    //==============================================
    //コンストラクタ
    public PlayerLv20() {
        super();
        int i;
        m_dKickX = new double[GAME_LENGTH];
        m_dKickY = new double[GAME_LENGTH];
        m_iKickTime = new int[GAME_LENGTH];
        m_dMoveX = new double[GAME_LENGTH];
        m_dMoveY = new double[GAME_LENGTH];
        m_iMoveTime = new int[GAME_LENGTH];
        m_dFaceX = new double[GAME_LENGTH];
        m_dFaceY = new double[GAME_LENGTH];
        for (i = 0; i < GAME_LENGTH; i++) {
            m_iTime = i;
            setKickTarget();
            setMoveTarget();
            setFaceTarget();
        }
        m_iTime = 0;
    }
    //==============================================
    //キックをする目標の場所と時刻を決める
    protected void setKickTarget() {
        //相手ゴールを自分の守るゴール方向から求め目標とする
        int t = m_iTime;
        if (m_strSide.startsWith("r"))
            m_dKickX[t] = -52.5;
        else
            m_dKickX[t] = 52.5;
        m_dKickY[t] = 0;
        //全力で蹴らせるため、到着時刻は次の時刻を指定する
        m_iKickTime[t] = m_iTime + 1;
    }
    //==============================================
    //トラップの場所を計算する
    protected void setTrapPosition() {
        //トラップの場所は現在のボール位置
        int t = m_iTime;
        m_dTrapX = m_dBallX[t];
        m_dTrapY = m_dBallY[t];
    }
    //==============================================
    //守備位置を計算する
    protected void setDefencePosition() {
        //守備位置はボールの座標を基準に決める
        int t = m_iTime;
        setDefencePosition(m_dBallX[t], m_dBallY[t]);
    }
    //==============================================
    //目的地に一番近い味方選手であるか判断する
    protected boolean checkNearest(double targetX, double targetY) {
        //仮に背番号10番の選手が目的地に近いものとする
        if (m_iNumber == 10)
            return true;
        else
            return false;
    }
    //==============================================
    //セットプレイであるか判断する
    protected boolean checkSetPlay() {
        if (m_strPlayMode.indexOf("fault") > -1)
            return false;
        if (m_strPlayMode.startsWith("kick_off_")
            || m_strPlayMode.startsWith("kick_in_")
            || m_strPlayMode.startsWith("goal_kick_")
            || m_strPlayMode.startsWith("corner_kick_")
            || m_strPlayMode.startsWith("free_kick_")
            || m_strPlayMode.startsWith("indirect_free_kick_")
            || m_strPlayMode.startsWith("panalty_kick_")) {
            return true;
        } else {
            return false;
        }
    }
    //==============================================
    //移動をする目標の場所と時刻を決める
    protected void setMoveTarget() {
        //トラップをする位置が味方選手の中で一番近ければトラップ
        //そうでなければ、守備位置へ移動する
        int t = m_iTime;
        m_iMoveTime[t] = t + 100; //スタミナに余裕を持って走らせるため			
        setTrapPosition();
        setDefencePosition();
        if (m_strPlayMode.startsWith("play_on")
            || (checkSetPlay() && m_strPlayMode.endsWith(m_strSide))) {
            //通常のプレイの時の動作
            if (checkNearest(m_dTrapX, m_dTrapY)) {
                m_dMoveX[t] = m_dTrapX;
                m_dMoveY[t] = m_dTrapY;
            } else {
                m_dMoveX[t] = m_dDefenceX;
                m_dMoveY[t] = m_dDefenceY;
            }
        } else {
            //相手のセットプレイの時の動作
            m_dMoveX[t] = m_dX[t];
            m_dMoveY[t] = m_dY[t];
        }
        //フィールド外に移動しようとするときの補正
        if (m_dMoveX[t] < -52.5)
            m_dMoveX[t] = -52.5;
        if (m_dMoveX[t] > 52.5)
            m_dMoveX[t] = 52.5;
        if (m_dMoveY[t] < -34.0)
            m_dMoveY[t] = -34.0;
        if (m_dMoveY[t] > 34.0)
            m_dMoveY[t] = 34.0;
    }
    //==============================================
    //視線を送る場所を決める
    protected void setFaceTarget() {
        //予測した位置のボールを見る
        int t = m_iTime;
        int next = (t + 1) % GAME_LENGTH;
        m_dFaceX[t] = m_dBallX[next];
        m_dFaceY[t] = m_dBallY[next];
    }
    //==============================================
    //キックできるか検査する
    protected boolean checkKickable() {
        //体とボールの大きさとキックができる距離を足した長さがキック可能距離
        //キック可能距離とボールまでの距離を比較する。
        int t = m_iTime;
        double kickablearea = ball_size + player_size + kickable_margin;
        double d = getDistance(m_dBallX[t], m_dBallY[t], m_dX[t], m_dY[t]);
        if (d > kickablearea)
            return false;
        else
            return true;
    }
    //==============================================
    //キック
    protected void kick() {
    }
    //==============================================
    //目的地に移動する
    protected void move() {
    }
    //==============================================
    //ボールが見えているときの行動を決定する
    protected void playWithBall() {
        int t = m_iTime;
        //キック可能ならばキック目標を決めキックする
        if (checkKickable()) {
            setKickTarget();
            kick();
        }
        //キック不可ならば移動目標を決め移動する
        else {
            setMoveTarget();
            move();
        }
        //キック行動および移動行動の結果を予測する
        predict(t, t + 1);
        //視線目標の計算
        setFaceTarget();
        //予測を基に目標に首を向ける
        lookAt(m_dFaceX[t], m_dFaceY[t]);
        //デバッグ用出力
        if (m_debugLv20) {
            DecimalFormat f = new DecimalFormat("###0.00");
            System.out.println();
            System.out.print(" 時刻" + t);
            System.out.print(" 視覚" + m_iVisualTime);
            System.out.print(" player" + m_iNumber);
            System.out.print(" 位置(" + f.format(m_dX[t]));
            System.out.print(" ," + f.format(m_dY[t]) + ")");
            System.out.print(" キック(" + f.format(m_dKickX[t]));
            System.out.print(" ," + f.format(m_dKickY[t]) + ")");
            System.out.print(" 移動(" + f.format(m_dMoveX[t]));
            System.out.print(" ," + f.format(m_dMoveY[t]) + ")");
            System.out.print(" 視線(" + f.format(m_dFaceX[t]));
            System.out.print(" ," + f.format(m_dFaceY[t]) + ")");
            System.out.print(" コマンド=" + m_strCommand[t]);
            System.out.print(" \t時刻" + t);
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv20[] player = new PlayerLv20[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            if (i < 11)
                teamname += "Left";
            else
                teamname += "Right";
            player[i] = new PlayerLv20();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[9].m_debugLv20 = true;
        System.out.println("試合への登録終了");
    }
}
