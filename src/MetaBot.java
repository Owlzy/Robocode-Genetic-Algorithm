import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import robocode.BulletHitEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.control.events.BattleCompletedEvent;

public class MetaBot {
	
	// Static Variables //////////////////////////////////////////////////////
		final static 
			String PATH = new String("C:\\robocode\\robots\\sampleex"); //path where to save bot
			String PACKAGE = new String("sampleex");
			String JARS = new String("C:\\robocode\\libs\\robocode.jar;");
	
	
	
	public static int NUM_CHROMOS = 5;
	
	String 
	botName = new String(),
	phenome[] = new String[NUM_CHROMOS],
	sourceCode = new String(),
	fileName;
	
		public MetaBot(String bName){
			//constructor just for testing, probably removed later
			botName = bName; //pass in bot name 
			
			
			//commenting out for now
			//RandPhen();
			//SetCode();
		}
		
		public void SetPhenome(String[] _phen){
			phenome = _phen;
		}
		
		//test function, randomise phenome
		public void RandPhen(){
			for (int i = 0; i < phenome.length; i++){
				phenome[i] = String.valueOf(Math.random() % 5);
			}
		}
	
	// FileIO Methods
		public void SetCode(){
			
			//botName = "testbot";//just for testing
			
			sourceCode =
					"package "+PACKAGE+";" +
					"\nimport robocode.*;" +
				"	import java.io.BufferedWriter;	import java.io.FileWriter;	import java.io.IOException; import java.io.PrintWriter;" +
					
"\nimport robocode.*;" +
"\nimport robocode.*;" +
"\nimport robocode.*;" +
"\nimport robocode.*;" +
					
					"\nimport robocode.util.Utils;" +
					"\nimport java.awt.Color;\n" +
					"\n" +		
					"\npublic class " + botName + " extends AdvancedRobot {" +
					"\n" +
					"\n double fit = 0;" +
					//"\n static double runVar2 = 0;" +
					//"\n" +
					"\n	public void run() {" +
					"\n" +
					"\nsetAdjustGunForRobotTurn(true);" +
					//"\nsetAdjustRadarForGunTurn(true);" +
					"\n" +
					"\n		setColors(Color.red,Color.blue,Color.green);" +	
					"\n		while(true) {" +
					"\n			turnGunRight(Double.POSITIVE_INFINITY);" +
					//"\n			turnRight(runVar1);" +
					//"\n			setAhead(runVar2);" +
					"\n		}" +
					"\n" +	
					"\n	}" +
					"\n	public void onScannedRobot(ScannedRobotEvent e) {" +
					"\n" +
					"\n // --- PHENOME 1 ---" +
					"\n		setAhead(" + phenome[0] + ");" +
					"\n" +
					"\n // --- PHENOME 2 ---" +
					"\n		setTurnRight("+ phenome[1] +");"  +
					"\n" +
					"\n // --- PHENOME 3 ---" +
					"\n		setTurnGunRight("+ phenome[2] +");"  +
					"\n" +
					"\n // --- PHENOME 4 ---" +
					"\n		setTurnRadarRight("+ phenome[3] +");"  +
					"\n" +
					"\n // --- PHENOME 5 ---" +
					"\n		setFire("+ phenome[4] +");"  +
					//"\n}" +
					"\n" +
					//"\n // --- PHENOME 6,7 ---" +
					//"\n		runVar1 = " + phenome[5] + ";" +
					//"\n" +
					//"\n		runVar2 = " + phenome[6] + ";" +
					//"\n" +
					"\n	}" +
					"\n" +	
					/*
					"\npublic void onHitByBullet(HitByBulletEvent e) {" +
					"\n" +
					"\n // --- PHENOME 6 ---" +
					"\n		setTurnRadarRight("+ phenome[7] +");"  +
					"\n"
					"\n	}" +
					"\n" +
					"\npublic void onHitWall(HitWallEvent e) {" +
					"\n		back(20);" +
					"\n		setAhead("+ phenome[8] +");"  +
					"\n	}" +
					*/
					//bullet hit
					"\n"
					+
					"\npublic void onBulletHit(BulletHitEvent e) {" +
					"\n" +
						"fit++;" +
					"\n"
					+ "} \n"
			
					+
					"\npublic void onRobotDeath(RobotDeathEvent e)  {" +
					"\n" +
					"fit += 100;" +
					"\n"
					+ "} \n"
					
+
"\npublic void onHitWall(HitWallEvent ev)  {" +
"\n" +
"if(fit>1){fit-= 0.2;}" +
"\n"
+ "} \n"


+
"\npublic void onWin(WinEvent ev) {" +
"\n" +
"fit += getEnergy() * 2;" +
"\n String path = \"fit.csv\";" 
+ "try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, false)))) {"
		 +
		"    out.print(fit); \n" +
	"}catch (IOException e) {}" 
		
		
+ "} \n"

+
"\npublic void onDeath(DeathEvent ev) {" +
"\n" +
"fit += getEnergy() * 2;" +
"\n String path = \"fit.csv\";" 
+ "try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, false)))) {"
		 +
		"    out.print(fit); \n" +
	"}catch (IOException e) {}" 
		
		
+ "}} \n"
				
				;
		}
		
		public static void execute(String command) throws Exception{
			Process process = Runtime.getRuntime().exec(command);
			//printMsg(command + " stdout:", process.getInputStream());
			//printMsg(command + " stderr:", process.getErrorStream());
			process.waitFor();
			if(process.exitValue() != 0)
				System.out.println(command + "exited with value " + process.exitValue());
		}
		private static void printMsg(String name, InputStream ins) throws Exception {
			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(ins));
			while((line = in.readLine()) != null){
				System.out.println(name + " " + line);
			}
		}
		
		
		String WriteToFile(){
			try{
				FileWriter fstream = new FileWriter(PATH+"\\"+botName+".java");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(sourceCode);
				out.close();
			}catch(Exception e){
				System.err.println("Error: " + e.getMessage());
			}
			
			// Compile code
			try {
				execute("javac -cp " + JARS + " " + PATH + "\\" + botName + ".java");
			}catch(Exception e){
				e.printStackTrace();
			}
			return (PATH+"\\"+botName+".class");
		}

}
