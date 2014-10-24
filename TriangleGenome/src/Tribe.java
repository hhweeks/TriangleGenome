import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Tribe extends Thread {
	Random rand = new Random();
	volatile ArrayList<Genome> genomeList = new ArrayList<>();
	BufferedImage masterImage;
	ImageContainer imagecontainer;
	private final Object GUI_INITIALIZATION_MONITOR = new Object();
	public final long GENEDIFFERENCELIMIT=500;
	public volatile boolean pauseThreadFlag = false;
	long timeStamp;
	public int tribeId;
	// private volatile boolean running=true; // Run unless told to pause
	// public static final int STARTINGTRIBESIZE=8;
	public static final int ENDINGTRIBESIZE = 10;
	public volatile boolean climbLoop = true;
	int startingTribeSize;
	TriangleGenomeGUI imagePanel;

	public Tribe(BufferedImage image, TriangleGenomeGUI tg) {
		startingTribeSize = TriangleGenomeGUI.STARTINGTRIBESIZE;
		masterImage = image;
		imagePanel = tg;
		boolean haveGrid=false;
		// populate genome list
		for (int i = 0; i < startingTribeSize; i++) {
			
			Genome genome = new Genome(masterImage);
			int seed=(int) Math.abs(rand.nextGaussian() *2/3);
			
			while(seed>GenomeUtilities.NSEEDING){
				seed=(int) Math.abs(rand.nextGaussian() * 2/3);
				
			}
			
			
			if(!haveGrid)seed++;
			//System.out.println("tribe builder   "+seed);
			switch (seed) {
			case 0:
				GenomeUtilities.mixedSampleGenome(genome, masterImage);
				break;

			case 1:
				GenomeUtilities.averagingGenome(genome, masterImage);
				break;
				
			case 2:
				GenomeUtilities.averageGridGenome(genome, masterImage);
				haveGrid=true;
				break;
			

			}
			genomeList.add(genome);
			genome.startFitscore = Statistics.getFitScore(
					GenomeUtilities.getBufferedImage(genome), masterImage);
			genome.genomeId = i;
		}
		// imagecontainer.setImage(GenomeUtilities.getBufferedImage(genome));
	}

	public void run() {

		climbLoop = true;
		climbRoutine();
	}

	public void climbRoutine() {
		goToLocalMax(TriangleGenomeGUI.NBREEDSTEPS);

	}

	public void interCrossRoutine(int sigma) {
		for (int i=0;i<imagePanel.STARTINGTRIBESIZE;i++){ 
			checkForPaused();
			Genome genome = genomeList.get(i);
			try {
				genome.hc.pauseThread();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("threads paused   :"+this);
		
		Genome son = new Genome(masterImage);
		Genome daughter = new Genome(masterImage);

		generateFitScores();
		Collections.sort(genomeList);
		//System.out.println("sorted   :"+this);
		int index0 = (int) Math.abs(rand.nextGaussian() * sigma);
		int index1 = (int) Math.abs(rand.nextGaussian() * sigma);
		int geneDiffAttempts=0;
		while (index0 >= genomeList.size()) {
			index0 = (int) Math.abs(rand.nextGaussian() * sigma);
		}

		while (index1 >= genomeList.size() || index1 == index0) {
			index1 = (int) Math.abs(rand.nextGaussian() * sigma);
		}
		
		//ensure genetic diversity
while(HammingDistance.calcDiff(genomeList.get(index0), genomeList.get(index1))<GENEDIFFERENCELIMIT){
		while (index0 >= genomeList.size()) {
			index0 = (int) Math.abs(rand.nextGaussian() * sigma);
		}

		while (index1 >= genomeList.size() || index1 == index0) {
			index1 = (int) Math.abs(rand.nextGaussian() * sigma);
		}
		if(geneDiffAttempts<5) break;
}
		
		
		
		checkForPaused();
		
		CrossOver.multiBreed(genomeList.get(index0), genomeList.get(index1),son, daughter, 3);
		genomeList.add(GenomeUtilities.genomeCopy(son));
		genomeList.add(GenomeUtilities.genomeCopy(daughter));
		//System.out.println("crossover");
		if (genomeList.size() > ENDINGTRIBESIZE) {
			generateFitScores();
			try{
			Collections.sort(genomeList);}
			catch(java.util.ConcurrentModificationException e){
				
			}

			ArrayList<Genome> fitGenomeHolder = new ArrayList<Genome>();
			for (int i = 0; i < startingTribeSize; i++) {
				fitGenomeHolder.add(genomeList.get(i));
			}

			genomeList.clear();
			genomeList.addAll(fitGenomeHolder);
		}

		climbLoop = true;

		// System.out.println("threads resume");
		imagePanel.drawGenome = imagePanel.tribeList.get(imagePanel.tribeSlider
				.getValue()).genomeList.get(imagePanel.genomeSlider.getValue());
		goToLocalMax(TriangleGenomeGUI.NBREEDSTEPS);

		//System.out.println("inerCross ends");
	}

	public LinkedList<Genome> intraCrossRoutin() {
		synchronized (GUI_INITIALIZATION_MONITOR) {
			int sigma = genomeList.size() / 2;
			LinkedList<Genome> toCrossList = new LinkedList<>();

//			Genome son = new Genome(masterImage);
//			Genome daughter = new Genome(masterImage);

			generateFitScores();
			Collections.sort(genomeList);
			// sigma=genomeList.size()/2;//didn't we initialize sigma to this
			// value?

			int index0 = (int) Math.abs(rand.nextGaussian() * sigma);
			int index1 = (int) Math.abs(rand.nextGaussian() * sigma);

			while (index0 >= genomeList.size()) {
				index0 = (int) Math.abs(rand.nextGaussian() * sigma);
			}

			while (index1 >= genomeList.size() || index1 == index0) {
				index1 = (int) Math.abs(rand.nextGaussian() * sigma);
			}

			Genome genome0 = GenomeUtilities.genomeCopy(genomeList.get(index0));
			Genome genome1 = GenomeUtilities.genomeCopy(genomeList.get(index1));
			genome0.masterImage = masterImage;
			genome1.masterImage = masterImage;

			toCrossList.add(genome0);
			toCrossList.add(genome1);
			for (int i=0;i<genomeList.size();i++) {
			Genome genome = genomeList.get(i);
				genome.startFitscore = Statistics.getFitScore(
						GenomeUtilities.getBufferedImage(genome), masterImage);
			}

			return toCrossList;
		}
	}

	public void checkForPaused() {
		synchronized (GUI_INITIALIZATION_MONITOR) {
			while (pauseThreadFlag) {
				try {
					GUI_INITIALIZATION_MONITOR.wait();

					

				} catch (Exception e) {
				}
			}
		}
	}

	public void pauseThread() throws InterruptedException {
		synchronized (GUI_INITIALIZATION_MONITOR) {
		pauseThreadFlag = true;
		for (int i=0;i<imagePanel.STARTINGTRIBESIZE;i++){ 
			Genome genome = genomeList.get(i);
			//synchronized (genome){
			//genome.hc.interrupt();
			genome.hc.pauseThread();
		
			}
	}
	}

	public void resumeThread() {
		synchronized (GUI_INITIALIZATION_MONITOR) {
			pauseThreadFlag = false;
			GUI_INITIALIZATION_MONITOR.notify();
			for (int i=0;i<imagePanel.STARTINGTRIBESIZE;i++){ 
				Genome genome = genomeList.get(i);
				//synchronized (genome){
				//genome.hc.start();
				genome.hc.resumeThread();
				//}
			}

		}
	}

	public void generateFitScores() {
		//for (Genome genome : genomeList) {
			for (int i=0;i<genomeList.size();i++) {
				Genome genome=genomeList.get(i);
			
			// creates a fitscore from each image.


			 genome.fitscore=Statistics.getFitScore(GenomeUtilities.getBufferedImage(genome),masterImage);
			}
		}
	

	public void goToLocalMax(int N) {

	

		checkForPaused();
		for (int i = 0; i < imagePanel.STARTINGTRIBESIZE; i++) {
			Genome genome = genomeList.get(i);
			genome.hc = new HillClimber(masterImage,genome, N);

			if(N>1)genome.hc.doBreed=true;
			genome.hc.tribe = this;
			if (!genome.hc.isAlive()) {
				genome.hc.start();
			}
		}

	}

}