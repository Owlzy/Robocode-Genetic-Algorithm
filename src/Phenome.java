import java.util.Random;

public class Phenome {

	public int botID;
	public static int numPhenomes = MetaBot.NUM_CHROMOS;
	Random rand = new Random(System.currentTimeMillis());
	String[] phenomes = new String[numPhenomes];

	String[][] termGenome = new String[RunEvo.NUM_TERM][numPhenomes];
	String[][] funGenome = new String[RunEvo.NUM_FUN][numPhenomes];

	// define functions
	public static String plus = "+", minus = "-", divide = "*", multi = "/";
	public static String[] funs = { "+", "-", "/", "*" };

	public static String[] terms = { "getEnergy()", "getGunHeading()", "getHeading()", "getVelocity()", "getX()",
			"getY()", "", "", "", "", "", "", "0" };

	// define terminals / values
	public static String getEn = "getEnergy()", // gets robots energy level
			getGunH = "getGunHeading() ", // gets gun heading
			getHead = "getHeading()", // gets robots heading
			getVel = "getVelocity()", getX = "getX()", getY = "getY()", randVal0 = String.valueOf(Math.random() * 5),
			randVal1 = "(" + String.valueOf((Math.random() * 2) - 1) + ")",
			randVal2 = String.valueOf(Math.random() * 10), randVal3 = String.valueOf(Math.random() * 20),
			randVal4 = String.valueOf(Math.random() * 30), randVal5 = String.valueOf(Math.random() * 10), zeroVal = "0",
			sinTime = "Math.sin(System.currentTimeMillis())", sinBearing = "Math.sin(e.getBearingRadians())",
			bearing = "e.getBearingRadians()", distance = "e.getDistance()", sinHeading = "Math.sin(e.getHeadingRadians())";

	public Phenome(int _botID) {
		botID = _botID;
	}

	public void BuildCreature() {
		System.out.println("building bot");
		MetaBot aBot = new MetaBot("GBot" + botID);
		aBot.SetPhenome(phenomes);
		aBot.SetCode();
		aBot.WriteToFile();// write and compile
	}

	public void NewCreature() {
		// fill all the phenomes to create new a creature
		for (int i = 0; i < numPhenomes; i++) {
			RandGenome(i);
		}

	}

	public void BuildPhens() {
		for (int i = 0; i < numPhenomes; i++) {
			phenomes[i] = new String();
			for (int j = 0; j < RunEvo.NUM_TERM; j++) {
				// System.out.println(termGenome[j][i]);
				phenomes[i] += termGenome[j][i];

				if (j < funGenome.length) {
					// System.out.println(funGenome[j][i]);
					phenomes[i] += funGenome[j][i];
				}
			}
		}
	}

	public String MutateTerm() {
		
		int randVal = rand.nextInt(18);

		switch (randVal) {
		case 0:
			return getEn;
		case 1:
			return getGunH;
		case 2:
			return getHead;
		case 3:
			return getVel;
		case 4:
			return getX;
		case 5:
			return getY;
		case 7:
			return randVal0;
		case 8:
			return randVal1;
		case 9:
			return randVal2;
		case 10:
			return randVal3;
		case 11:
			return randVal4;
		case 12:
			return zeroVal;
		case 13:
			return "Math.PI";
		case 14:
			return sinTime;
		case 15:
			return sinBearing;
		case 16:
			return bearing;
		case 17:
			return distance;
		case 18:
			return sinHeading;
		default:
			return "0";
		}

	}

	public void RandGenome(int phenNum) {

		for (int i = 0; i < RunEvo.NUM_TERM; i++) {

			int randVal = rand.nextInt(18);

			switch (randVal) {
			case 0:
				termGenome[i][phenNum] = getEn;
				break;
			case 1:
				termGenome[i][phenNum] = getGunH;
				break;
			case 2:
				termGenome[i][phenNum] = getHead;
				break;
			case 3:
				termGenome[i][phenNum] = getVel;
				break;
			case 4:
				termGenome[i][phenNum] = getX;
				break;
			case 5:
				termGenome[i][phenNum] = getY;
				break;
			case 7:
				termGenome[i][phenNum] = randVal0;
				break;
			case 8:
				termGenome[i][phenNum] = randVal1;
				break;
			case 9:
				termGenome[i][phenNum] = randVal2;
				break;
			case 10:
				termGenome[i][phenNum] = randVal3;
				break;
			case 11:
				termGenome[i][phenNum] = randVal4;
				break;
			case 12:
				termGenome[i][phenNum] = zeroVal;
				break;
			case 13:
				termGenome[i][phenNum] = "Math.PI";
				break;
			case 14:
				termGenome[i][phenNum] = sinTime;
				break;
			case 15:
				termGenome[i][phenNum] = sinBearing;
				break;
			case 16:
				termGenome[i][phenNum] = bearing;
				break;
			case 17:
				termGenome[i][phenNum] = distance;
				break;
			case 18:
				termGenome[i][phenNum] = sinHeading;
				break;
			default:
				termGenome[i][phenNum] = "0";
				break;// by default put 0 as value
			}
			if (i < funGenome.length) {
				int randFun = rand.nextInt(4);
				funGenome[i][phenNum] = funs[randFun];
			}

		}

		if (funGenome[funGenome.length - 1][phenNum].equals("/")
				&& termGenome[termGenome.length - 1][phenNum].equals("0")) {
			// if division by zero occurs at end replace zero with a one
			// suppose this is a funny kind of mutation!
			termGenome[termGenome.length - 1][phenNum] = "1";
		}
	}

}
