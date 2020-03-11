import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import robocode.BulletHitEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.control.*;
import robocode.control.events.*;

//@OwainBell, UoR 2016
public class BattleRunner {

	RobocodeEngine engine;
	BattleObserver battleObserver;
	BattleSpecification battleSpec;
	BattlefieldSpecification battlefield;
	String sittingDucks = "sample.SittingDuck,sample.SittingDuck,sample.SittingDuck,sample.SittingDuck,sampleex.GBot";
	String ramFire = "sample.RamFire,sampleex.GBot";
	String fire = "sample.Fire,sampleex.GBot";
	String track = "sample.Tracker,sampleex.GBot";
	String alien = "sampleex.Alien,sampleex.GBot";
	
	//set the enemy here
	String opponent = track;
	
	float avgFitness = 0;
	float[][] fitness = new float[RunEvo.POP_SIZE][2];

	public BattleRunner() {
		battleObserver = new BattleObserver();

		// Disable log messages from Robocode
		RobocodeEngine.setLogMessagesEnabled(false);

		// Create the RobocodeEngine
		// RobocodeEngine engine = new RobocodeEngine(); // Run from current
		// working directory
		engine = new RobocodeEngine(new java.io.File("C:/Robocode")); // Run from default dir
		// Add our own battle listener to the RobocodeEngine
		engine.addBattleListener(battleObserver);

		// Show the Robocode battle view
		engine.setVisible(true);
		battlefield = new BattlefieldSpecification(800, 600); // 800x600
	}

	public void BattleShutdown() {

		// Cleanup our RobocodeEngine
		engine.close();

		// Make sure that the Java VM is shut down properly
		System.exit(0);
	}

	public void RunBattles() {
		// Setup the battle specification

		int numberOfRounds = 1;

		for (int i = 0; i < RunEvo.POP_SIZE; i++) {
			RobotSpecification[] selectedRobots = engine.getLocalRepository(
					opponent + i); //here is where the enemy bot is chosen/////////////////////////////////////////////
			battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);
			engine.runBattle(battleSpec, true);

			fitness[i][0] = battleObserver.GetFit(); // zero is actual fitness
			battleObserver.fit = 0;
			fitness[i][1] = i; // array slot 2 is the original index, which will
								// be the index in the array of bots

		}

		CalcAvg();
		BubbleSort();
		PrintBest();
	}

	public int GetChamp1(){
		//returns original index of champs
		return (int)fitness[0][1];
	}
	public int GetChamp2(){
		//returns original index of champs
		return (int)fitness[1][1];
	}
	
	public int GetChamp(int index){
		return (int)fitness[index][1];
	}
	
	public void PrintBest() {
		System.out.println(fitness[0][0] + " : champ fitness" + "   " + fitness[0][1] + " : champ index");
	}

	public void CalcAvg(){
		avgFitness = 0;
		float total = 0;
		int totalPop = RunEvo.POP_SIZE;
		for (int i = 0; i < totalPop; i++){
			total += fitness[i][0];
		}
		
		avgFitness = total / totalPop;
		System.out.println("AVG FITNESS : " + avgFitness);
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("avgfit.csv", true)))) {
		    //out.println("the text");
		    //more code
		    out.print("," + avgFitness);
		    //more code
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
	public void BubbleSort() {
		int n = RunEvo.POP_SIZE;
		float temp = 0;

		for (int i = 0; i < n; i++) {
			for (int j = 1; j < (n - i); j++) {
				if (fitness[j - 1][0] < fitness[j][0]) {
					// swap the elements!
					temp = fitness[j - 1][0];
					float oldIndex;
					oldIndex = fitness[j - 1][1];
					fitness[j - 1][0] = fitness[j][0];// sorting both fitness
					fitness[j - 1][1] = fitness[j][1];// and keeping original
														// index with it

					fitness[j][0] = temp;
					fitness[j][1] = oldIndex;
				}

			}
		}
	}
}

class BattleObserver extends BattleAdaptor {

	public float fit = 0;

	// Called when the battle is completed successfully with battle results
	public void onBattleCompleted(BattleCompletedEvent e) {
		System.out.println("-- Battle has completed --");
		// Print out the sorted results with the robot names
		System.out.println("Battle results:");
		for (robocode.BattleResults result : e.getSortedResults()) {
			System.out.println("  " + result.getTeamLeaderName() + ": " + result.getScore());
			String bName = result.getTeamLeaderName();
			bName = bName.replaceAll("[\\da-z.]", "");
			// System.out.println(bName + " : prototype name");
			if (bName.equals("GB")) {
				// fit = result.g
				// System.out.println(fit + " : fitness");
				// System.out.println("name does equal GBot");
			}
			try(BufferedReader br = new BufferedReader(new FileReader("fit.csv"))) {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();

			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    String everything = sb.toString();
			    
			    fit = Float.parseFloat(everything);
			    System.out.println("Final fitness : " + fit);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	// we need battle events here that we need. e.g. hit wall, shot enemy
	//these battle events don't work, i tink I know why, try function at bottom and check what event it is
	public void onBulletHit(BulletHitEvent e) {
		fit++;
	}

	public void onRobotDeath(RobotDeathEvent e) {
		// killed another bot
		fit += 20;
	}

	public void onHitWall(HitWallEvent e) {
		// bumped into wall
		fit -= 0.2;
	}

	public float GetFit() { // no, I won't
		return fit;
	}

	// Called when the game sends out an information message during the battle
	public void onBattleMessage(BattleMessageEvent e) {
		System.out.println("Msg> " + e.getMessage());
	}

	// Called when the game sends out an error message during the battle
	public void onBattleError(BattleErrorEvent e) {
		System.out.println("Err> " + e.getError());
	}
}