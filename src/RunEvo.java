import java.util.Random;

public class RunEvo {

	Random randGen;
	BattleRunner myBattleRunner;
	
	boolean isElitist = true;
	boolean isPoolBreed = true;
	boolean isEliteDaddy = true;
	
	final static int maxGens = 10;
	final static int breedPoolSize = 10; //must be less than pop size
	final static int POP_SIZE = 10, NUM_TERM = 7, NUM_FUN = NUM_TERM - 1;

	static double PROB_CROSSOVER = 0.5, PROB_REPLICATION = 0.05, PROB_MUTATION = 0.1,
			fitnesses[] = new double[POP_SIZE];
	static Phenome[] pool = new Phenome[POP_SIZE];

	public static void main(String[] args) {
		System.out.println("running main");
		RunEvo thisGP = new RunEvo();
	}

	public RunEvo() {
		randGen = new Random(System.currentTimeMillis() + 100);
		InitPop();// init the population with random bots first
		myBattleRunner = new BattleRunner();

		RunAllGens();
		System.out.println("All done!");
	}

	public void RunAllGens() {
		System.out.println("running all generations");
		for (int i = 0; i < maxGens; i++) {
			
			System.out.println("running gen : " + i);	
			myBattleRunner.RunBattles(); // first run the battles
			
			// now do the breed
			if(i == maxGens - 1){
				break;
			}
			if (isPoolBreed){
				PoolBreed();
			}
			else {
				Breed();	
			}

		}
		myBattleRunner.BattleShutdown();
	}

	public void SingleGen() {
		System.out.println("running single gen");
		myBattleRunner.RunBattles(); // first run the battles
		Breed();
		myBattleRunner.BattleShutdown();
	}

	public void PrintGenomes(String[] dadTerms, String[] mumTerms, String[] dadFuns, String[] mumFuns) {
		for (int i = 0; i < dadTerms.length; i++) {
			System.out.println(dadTerms[i] + " : Dad terms");
		}
		for (int i = 0; i < dadFuns.length; i++) {
			System.out.println(dadFuns[i] + " : Dad funs");
		}
		for (int i = 0; i < mumTerms.length; i++) {
			System.out.println(mumTerms[i] + " : Mum terms");
		}
		for (int i = 0; i < mumFuns.length; i++) {
			System.out.println(mumFuns[i] + " : Mum funs");
		}
	}

	public void PoolBreed(){
		
		//grab the champion
		Phenome theChamp = pool[myBattleRunner.GetChamp1()];
		Phenome[] parents = new Phenome[breedPoolSize];
		
		for (int i = 0; i < breedPoolSize; i++){
			parents[i] = pool[myBattleRunner.GetChamp(i)];
		}

		Phenome dadPhen;
		Phenome mumPhen;

		Phenome childPhen = new Phenome(99999); // an aribitary high number used
		System.out.println("Breeding");
		for (int botNum = 0; botNum < POP_SIZE; botNum++) {

			int randomParent = randGen.nextInt(breedPoolSize - 1);
			if (isEliteDaddy){
				dadPhen = theChamp;
				randomParent = randGen.nextInt(breedPoolSize - 2);
				mumPhen = parents[randomParent + 1]; //make sure the daddy doesn't breed with himself
			}
			else{
				dadPhen = parents[randomParent];
				randomParent = randGen.nextInt(breedPoolSize - 1);
				mumPhen = parents[randomParent]; 
			}
			for (int pNum = 0; pNum < Phenome.numPhenomes; pNum++) {
				boolean foundDivEnd = false;
				for (int k = 0; k < NUM_TERM; k++) {
					double rand = Math.random();
					// if rand < 0.5 take term from dad
					if (rand < 0.5) {

						childPhen.termGenome[k][pNum] = dadPhen.termGenome[k][pNum];
					} else {
						childPhen.termGenome[k][pNum] = mumPhen.termGenome[k][pNum];
					}
					if (k < NUM_FUN) {
						rand = Math.random(); // now do a roll for functions
						if (rand < 0.5) {
							childPhen.funGenome[k][pNum] = dadPhen.funGenome[k][pNum];
						} else {
							childPhen.funGenome[k][pNum] = mumPhen.funGenome[k][pNum];
						}

						if (k == NUM_FUN - 1) {
							/// probably need a div zero check here!
							if (childPhen.funGenome[k][pNum].equals("/")) {
								foundDivEnd = true;
							}
						}

					}

					if (foundDivEnd && childPhen.termGenome[k][pNum].equals("0")) {
						// def have a div zero here so set last term to 1
						childPhen.termGenome[k][pNum] = "1";
					}

				}
				double mutateRoll = Math.random();
				if (mutateRoll < 0.005) {
					System.out.println("MUTATION ABOUT TO OCCUR");
					double rand = Math.random();
					if (rand < 0.5) {
						// pick a term to mutate
						int randInt = randGen.nextInt(NUM_TERM - 1);
						childPhen.termGenome[randInt][pNum] = childPhen.MutateTerm();
					} else {
						// pick fun to mutate
						int randInt = randGen.nextInt(NUM_FUN - 1);
						int randInt2 = randGen.nextInt(4);
						childPhen.funGenome[randInt][pNum] = Phenome.funs[randInt2];

					}
					System.out.println("MUTATION JUST OCCURED");
				}

			}

			//if elitist replace random bot with the champion
			if (isElitist){
				int randomNum = randGen.nextInt(POP_SIZE - 1);
				pool[randomNum] = theChamp;
				pool[randomNum].botID = randomNum;
			}
			
			// should have a complete child so put back in the pool
			pool[botNum] = childPhen;
			pool[botNum].botID = botNum; // make sure to get correct id
			pool[botNum].BuildPhens();
			pool[botNum].BuildCreature();

		}
		
	}
	
	public void Breed() {

		Phenome dadPhen = pool[myBattleRunner.GetChamp1()];
		Phenome mumPhen = pool[myBattleRunner.GetChamp2()];

		Phenome childPhen = new Phenome(99999); // an aribitary high number used
												// here, is only a temp var

		System.out.println("Breeding");
		for (int botNum = 0; botNum < POP_SIZE; botNum++) {

			for (int pNum = 0; pNum < Phenome.numPhenomes; pNum++) {
				boolean foundDivEnd = false;
				// pool[myBattleRunner.GetChamp1()].phenomes[j];
				// String mummyPhen =
				// pool[myBattleRunner.GetChamp2()].phenomes[j];
				for (int k = 0; k < NUM_TERM; k++) {
					double rand = Math.random();
					// if rand < 0.5 take term from dad
					if (rand < 0.5) {
						childPhen.termGenome[k][pNum] = dadPhen.termGenome[k][pNum];
					} else {
						childPhen.termGenome[k][pNum] = mumPhen.termGenome[k][pNum];
					}
					if (k < NUM_FUN) {
						rand = Math.random(); // now do a roll for functions
						if (rand < 0.5) {
							childPhen.funGenome[k][pNum] = dadPhen.funGenome[k][pNum];
						} else {
							childPhen.funGenome[k][pNum] = mumPhen.funGenome[k][pNum];
						}

						if (k == NUM_FUN - 1) {
							/// probably need a div zero check here!
							if (childPhen.funGenome[k][pNum].equals("/")) {
								foundDivEnd = true;
							}
						}

					}

					if (foundDivEnd && childPhen.termGenome[k][pNum].equals("0")) {
						// def have a div zero here so set last term to 1
						childPhen.termGenome[k][pNum] = "1";
					}

				}
				double mutateRoll = Math.random();
				if (mutateRoll < 0.005) {
					System.out.println("MUTATION ABOUT TO OCCUR");
					double rand = Math.random();
					if (rand < 0.5) {
						// pick a term to mutate
						int randInt = randGen.nextInt(NUM_TERM - 1);
					//	rand = Math.random() * 100;
						childPhen.termGenome[randInt][pNum] = childPhen.MutateTerm();
					} else {
						// pick fun to mutate
						int randInt = randGen.nextInt(NUM_FUN - 1);
						int randInt2 = randGen.nextInt(4);
						childPhen.funGenome[randInt][pNum] = Phenome.funs[randInt2];

					}
					System.out.println("MUTATION JUST OCCURED");
				}

			}

			//if elitist replace random bot with the champion
			if (isElitist){
				int randomNum = randGen.nextInt(POP_SIZE);
				pool[randomNum] = dadPhen;
				pool[randomNum].botID = randomNum;
			}
			
			// should have a complete child so put back in the pool
			pool[botNum] = childPhen;
			pool[botNum].botID = botNum; // make sure to get correct id
			pool[botNum].BuildPhens();
			pool[botNum].BuildCreature();
		}
	}

	public void InitPop() {
		System.out.println("init pop");
		for (int i = 0; i < POP_SIZE; i++) {
			pool[i] = new Phenome(i);
			pool[i].NewCreature();
			pool[i].BuildPhens();
			pool[i].BuildCreature();
		}
	}
}
