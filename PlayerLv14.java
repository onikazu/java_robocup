//==============================================
//プレイヤーのスタミナ係数の読み込み
public class PlayerLv14 extends PlayerLv13 {
    //メンバ:
    double stamina_max = 4000; //スタミナ最大値
    double stamina_inc_max = 45; //スタミナ回復最大値
    double recover_min = 0.5; //recoveryの範囲の最小値
    double extra_stamina = 0.0; //予備のスタミナ
    double recover_dec_thr = 0.3; //スタミナ回復力を減らし始める倍率の境界
    double recover_dec = 0.002; //スタミナ回復力を減らす場合の減少値
    double effort_min = 0.6; //ダッシュ実行効率最小値
    double effort_max = 1.0; //ダッシュ実行効率最大値
    double effort_dec_thr = 0.3; //ダッシュ実行効率を減らし始める倍率の境界
    double effort_inc_thr = 0.6; //ダッシュ実行効率を増やし始める倍率の境界
    double effort_dec = 0.005; //ダッシュ実行効率を減らす場合の減少値
    double effort_inc = 0.01; //ダッシュ実行効率を増やす場合の増大値
    private boolean m_debugLv14 = false;
    //上記の「倍率」とはスタミナ最大値に対する倍率を指す。
    //==============================================
    //パラメータの読み込み(プレイヤータイプ)
    protected void analyzePlayerType(String message) {
        //前回まで開発した内容の実行
        super.analyzePlayerType(message);
        //パラメータを読み込む
        String type = m_strPlayerType[m_iPlayerType];
        dash_power_rate = getParam(type, "dash_power_rate", 1);
        stamina_inc_max = getParam(type, "stamina_inc_max", 1);
        extra_stamina = getParam(type, "extra_stamina", 1);
        effort_max = getParam(type, "effort_max", 1);
        effort_min = getParam(type, "effort_min", 1);
    }
    //==============================================
    //パラメータの読み込み(サーバパラメータ)
    protected void analyzeServerParam(String message) {
        //前回まで開発した内容の実行
        super.analyzeServerParam(message);
        //パラメータを読み込む
        stamina_max = getParam(message, "stamina_max", 1);
        recover_dec_thr = getParam(message, "recover_dec_thr", 1);
        recover_dec = getParam(message, "recover_dec", 1);
        recover_min = getParam(message, "recover_min", 1);
        effort_dec_thr = getParam(message, "effort_dec_thr", 1);
        effort_inc_thr = getParam(message, "effort_inc_thr", 1);
        effort_dec = getParam(message, "effort_dec", 1);
        effort_inc = getParam(message, "effort_inc", 1);
    }
    //==============================================
    //体調メッセージを解析する
    protected void analyzePhysicalMessage(String message) {
        //今まで開発した内容を実行する
        super.analyzePhysicalMessage(message);
        //ハープタイムごとにスタミナ系の計算パラメータは回復する
        if (m_strPlayMode.startsWith("before_kick_off")) {
            m_dRecovery[m_iTime] = 1.0;
            m_dEffort[m_iTime] = 1.0;
            m_dStamina[m_iTime] = stamina_max;
        }
    }
    //==============================================
    //メイン
    public static void main(String[] args) {
        PlayerLv14[] player = new PlayerLv14[11];
        Thread[] thread = new Thread[11];
        int i;
        for (i = 0; i < 1; i++) {
            String teamname = player.getClass().toString().substring(8, 18);
            player[i] = new PlayerLv14();
            thread[i] = new Thread(player[i]);
            player[i].initialize((i % 11 + 1), teamname, "localhost", 6000);
            thread[i].start();
        }
        player[0].m_debugLv14 = true;
        System.out.println("試合への登録終了");
    }
}
