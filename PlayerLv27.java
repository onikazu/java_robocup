//==============================================
//パッケージの読み込み
import java.text.*;

//==============================================
//味方同士で声を掛け合ってパスをする
public class PlayerLv27 extends PlayerLv26
{
	//メンバ:
	private boolean m_debugLv27 = false;

	

	

	//==============================================
	//メイン
	public static void main(String[] args)
	{
		PlayerLv27[] player = new PlayerLv27[22];
		Thread[] thread = new Thread[22];
		int i;
		for(i=0;i<22;i++)
		{
			String teamname =player.getClass().toString().substring(8,18);
			if(i<11) teamname +="Left"; else teamname += "Right";
			player[i] = new PlayerLv27();
			thread[i] = new Thread(player[i]);
			player[i].initialize((i%11+1),teamname,"localhost",6000);
			thread[i].start();
		}
		player[	9 ].m_debugLv27 = true;
		System.out.println("試合への登録終了");	
	}
}


