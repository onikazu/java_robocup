//==============================================
//パッケージの読み込み
import java.io.*;
import java.net.*;
//==============================================
//選手を試合に参加させる
public class PlayerLv00 {
    //==============================================
    //属性
    protected String m_strHostName = "localhost"; //サーバ名
    protected int m_iPort = 6000; //ポート番号
    protected DatagramSocket m_socket; //ソケット
    private boolean m_debugLv00 = false; //デバッグのフラグ
    final int length_of_buffer = 4096; //パケット通信データの長さ
    //==============================================
    //コンストラクタ
    public PlayerLv00() {
        //ソケットの初期化
        try {
            //通常の処理
            m_socket = new DatagramSocket();
        } catch (SocketException e) {
            //エラー発生時の処理
            System.err.println("Lv00:ソケットが作成できなかった");
            System.exit(1);
        }
    }
    //==============================================
    //サーバへコマンドを送信する
    protected void send(String command) {
        if (command.length() == 0)
            return;
        command += "\0";
        try {
            //IPアドレス作成
            InetSocketAddress address =
                new InetSocketAddress(m_strHostName, m_iPort);
            //パケット作成
            DatagramPacket packet =
                new DatagramPacket(
                    command.getBytes(),
                    command.length(),
                    address);
            //パケット送信
            m_socket.send(packet);
        } catch (SocketException e) {
            System.err.println("Lv00:パケットが作成できなかった");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Lv00:送信に失敗した");
            System.exit(1);
        }
    }
    //==============================================
    //サーバからメッセージを受信する
    protected String receive() {
        try {
            //受信パケットの準備
            byte[] buffer = new byte[length_of_buffer];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            //メッセージの受信
            m_socket.receive(packet);
            m_iPort = packet.getPort();
            //メッセージの文字列化
            String message =
                new String(packet.getData(), 0, packet.getLength());
            return message;
        } catch (IOException e) {
            System.err.println("Lv00:受信に失敗した");
            System.exit(1);
            return "";
        }
    }
    //==============================================
    //メイン
    //選手を1人作成し、サーバーへ接続する。
    public static void main(String[] args) {
        PlayerLv00 player = new PlayerLv00(); //選手を一人分作る
        String command = "(init Lv00 (goalie)(version 15.40))"; //初期化コマンド
        player.send(command); //コマンドを送信する
        System.out.println("送信:" + command); //内容を表示する
        String message = player.receive(); //コマンドを受信する
        System.out.println("受信:" + message); //内容を表示する
        System.out.println("試合への登録完了");
        //サーバから続きのメッセージを受信し続ける
        while (true) {
            message = player.receive();
        }
    }
}