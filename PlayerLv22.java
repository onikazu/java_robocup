//==============================================
import java.util.*;
import java.text.*;
//==============================================
//自分の視界にある選手の情報を計算する
//目的地に一番近い味方選手であるか判断する
public class PlayerLv22 extends PlayerLv21 {
    //メンバ:
    protected LinkedList m_listPlayer;
    private boolean m_debugLv22 = false;
    //==============================================
    //コンストラクタ
    public PlayerLv22() {
        super();
        m_listPlayer = new LinkedList();
    }
    //==============================================
    //キーワードで始まる物体データをメッセージから順に取り出す
    protected LinkedList getObjectList(String message, String keyword) {
        LinkedList list = new LinkedList();
        //メッセージからキーワードで始まる文字列を取り出す繰り返し
        //message = "......keyword) 20 10 5 3)......"
        int index0 = message.indexOf(keyword);
        while (-1 < index0) {
            //キーワードが見えているときの処理
            int index1 = message.indexOf(")", index0 + 2);
            int index2 = message.indexOf(")", index1 + 1);
            String strObject = message.substring(index0, index2 + 1);
            String player = getObjectMessage(strObject);
            list.addLast(player);
            index0 = message.indexOf(keyword, index2);
        }
        return list;
    }
    //==============================================
    //物体データのフィールド座標系での情報を計算する
    protected String getObjectMessage(String obj) {
        //obj="((objName) 40 30 20 10 0 0)"
        //              ^index0
        //                               ^index1
        //result="((team friend)(number 6)
        //			(x -10)(y 5)(vx 1)(vy -2)(body 30)(neck 0)"
        int t = m_iVisualTime;
        double dist = 0;
        double dir = 0;
        double dist_change = 0;
        double dir_change = 0;
        double neck = 0;
        double body = 0;
        int index0 = obj.indexOf(") ");
        int index1 = obj.indexOf(")", index0 + 1);
        String result = "";
        String name = obj.substring(0, index0 + 2);
        int index2 = name.indexOf("\"");
        int index3 = name.indexOf("\"", index2 + 1);
        int index4 = name.indexOf(")");
        //プレイヤーが敵側が味方側かでチーム文字列を変更する
        String s = "((p \"" + m_strTeamName + "\"";
        String team;
        if (name.startsWith(s)) //味方のプレイヤーの場合
            team = "friend";
        else //敵側プレイヤーの場合
            team = "enemy";
        //背番号およびキーパー情報の処理
        int number = 0;
        if (index3 + 1 < index4
            && name.indexOf("(p)") == -1
            && name.indexOf("(P)") == -1) {
            //背番号まで情報がある場合
            String str = name.substring(index3 + 1, index4);
            //ゴールキーパ情報を取る
            if (str.indexOf("goalie") > 0) {
                str = str.replaceFirst("goalie", " ");
            }
            number = (int) (Double.parseDouble(str));
        }
        //パラメータを数値データに変換する
        String str = obj.substring(index0 + 1, index1);
        StringTokenizer st = new StringTokenizer(str);
        int count = st.countTokens();
        if (st.hasMoreTokens())
            dist = Double.parseDouble(st.nextToken());
        if (st.hasMoreTokens())
            dir = Double.parseDouble(st.nextToken());
        // 変更部分　増村
        if (st.hasMoreTokens()){
            try{
                dist_change = Double.parseDouble(st.nextToken());
            }catch(NumberFormatException e){
                System.out.println("<obj>");
                System.out.println(obj);
                System.out.println("<dist_change>");
                System.out.println(dist_change);
            }
        }
        if (st.hasMoreTokens())
            dir_change = Double.parseDouble(st.nextToken());
        if (st.hasMoreTokens())
            body = Double.parseDouble(st.nextToken());
        if (st.hasMoreTokens())
            neck = Double.parseDouble(st.nextToken());
        //位置を計算する
        double rad = Math.toRadians(normalizeAngle(dir + m_dNeck[t]));
        double X = m_dX[t] + dist * Math.cos(rad);
        double Y = m_dY[t] + dist * Math.sin(rad);
        //速度を計算する
        double VX = 0;
        double VY = 0;
        if (count >= 4) {
            //自分を中心とした極座標系へ変換する
            double vx = dist_change;
            double vy = dir_change * dist * (Math.PI / 180);
            double R = Math.sqrt(vx * vx + vy * vy);
            double Deg = Math.toDegrees(Math.atan2(vy, vx));
            //フィールドの座標系上での相対速度に変換する
            double DegAbs = normalizeAngle(dir + Deg + m_dNeck[t]);
            double Rad = Math.toRadians(DegAbs);
            double vx_r = R * Math.cos(Rad);
            double vy_r = R * Math.sin(Rad);
            //自分の速度を足して絶対速度を求める
            VX = vx_r + m_dVX[t];
            VY = vy_r + m_dVY[t];
        }
        //体と首の角度を計算する
        double BODY = m_dBody[t];
        double NECK = m_dNeck[t];
        if (count >= 5)
            BODY = normalizeAngle(BODY + m_dNeck[t]);
        if (count >= 6)
            NECK = normalizeAngle(NECK + m_dNeck[t]);
        DecimalFormat g = new DecimalFormat("###0.00");
        //解析結果を作成する
        result = "(";
        result += "(team " + team + ")";
        result += "(number " + number + ")";
        result += "(x " + g.format(X) + ")" + "(y " + g.format(Y) + ")";
        result += "(vx " + g.format(VX) + ")" + "(vy " + g.format(VY) + ")";
        result += "(body "
            + g.format(BODY)
            + ")"
            + "(neck "
            + g.format(NECK)
            + ")";
        result += ")";
        getParam(result, "x", 1);
        return result;
    }
    //==============================================
    //視覚メッセージを解析する
    protected void analyzeVisualMessage(String message) {
        //前回まで開発した内容を実行する
        super.analyzeVisualMessage(message);
        //自分の座標の内容のチェックをする
        int t = m_iVisualTime;
        if (Math.abs(m_dNeck[t]) > 180.0)
            return;
        if (Math.abs(m_dX[t]) > 60.0)
            return;
        if (Math.abs(m_dY[t]) > 40.0)
            return;
        //視界にある選手の情報を取り出す
        String str = "((p";
        m_listPlayer = getObjectList(message, str);
        LinkedList list1 = getObjectList(message, "((p");
        LinkedList list2 = getObjectList(message, "((P");
        //選手の情報をリスト形式に保存する
        int i;
        m_listPlayer.clear();
        for (i = 0; i < list1.size(); i++)
            m_listPlayer.add(list1.get(i));
        for (i = 0; i < list2.size(); i++)
            m_listPlayer.add(list2.get(i));
        //デバッグ用出力
        if (m_debugLv22 && m_iTime > 8 && m_iTime < 12) {
            System.out.println();
            System.out.println("背番号 " + m_iNumber + " 時刻" + m_iTime);
            for (i = 0; i < m_listPlayer.size(); i++) {
                String player = m_listPlayer.get(i).toString();
                System.out.println(player);
            }
        }
    }
    //==============================================
    //目的地に一番近い味方選手であるか判断する
    protected boolean checkNearest(double targetX, double targetY) {
        int t = m_iTime;
        //自分と目的地までの距離を計算する
        double d = getDistance(m_dX[t], m_dY[t], targetX, targetY);
        boolean result = true;
        String s = "friend";
        int i;
        //リストの全ての選手データに対し以下の検査を行う
        if (m_listPlayer == null)
            return false;
        for (i = 0; i < m_listPlayer.size(); i++) {
            String player = m_listPlayer.get(i).toString();
            double x = getParam(player, "x", 1);
            double y = getParam(player, "y", 1);
            //選手データの座標と目的地の距離が自分より短いか検査する
            if (d > getDistance(targetX, targetY, x, y)) {
                if (player.indexOf(s) > -1) {
                    result = false;
                }
            }
        }
        return result;
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv22[] player = new PlayerLv22[22];
        Thread[] thread = new Thread[22];
        int i;
        for (i = 0; i < 22; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            if (i < 11)
                teamname += "Left";
            else
                teamname += "Right";
            player[i] = new PlayerLv22();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[2].m_debugLv22 = true;
        System.out.println("試合への登録終了");
    }
}
