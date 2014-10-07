import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Tribe extends Thread {
	Random rand=new Random();
	ArrayList<Genome> genomeList= new ArrayList<>();
	BufferedImage masterImage;
	
	private final Object GUI_INITIALIZATION_MONITOR = new Object();
    private boolean pauseThreadFlag = false;
	
	
	
	private volatile boolean running = true; // Run unless told to pause
	public static final int STARTINGTRIBESIZE=1;
	public static final int ENDINGTRIBESIZE=500;
	public Tribe(BufferedImage image){
		masterImage=image;
		//populate genome list
	for(int i=0;i<STARTINGTRIBESIZE;i++){
		System.out.println("tribe builder");
		Genome genome=new Genome(masterImage);
		GenomeUtilities.averagingGenome(genome, masterImage);
		genomeList.add(genome);

	}
	
		
	}
	public void run(){
		while(true){
		
			 checkForPaused();
			goToLocalMax(10);
		}
		
	}
	private void checkForPaused() {
        synchronized (GUI_INITIALIZATION_MONITOR) {
            while (pauseThreadFlag) {
                try {
                    GUI_INITIALIZATION_MONITOR.wait();
                } catch (Exception e) {}
            }
        }
    }

    public void pauseThread() throws InterruptedException {
        pauseThreadFlag = true;
    }

    public void resumeThread() {
        synchronized(GUI_INITIALIZATION_MONITOR) {
            pauseThreadFlag = false;
            GUI_INITIALIZATION_MONITOR.notify();
        }
    }
    
	public void generateFitscores(){
	for(Genome genome:genomeList){
		//creates a fitscore from each image. 
		//Should we preserve the Buffered Image as a phenotype???
		genome.fitscore=Statistics.getFitScore(masterImage, GenomeUtilities.getBufferedImage(genome));
		
	}
	}
	public void goToLocalMax(int N){
		HillClimber hc=new HillClimber(masterImage);
		
	    for(Genome genome:genomeList){
		hc.climbLoop(genome, N);
	    }
		
		
	}
	
	public static void nextGeneration(){
		//Collections.sort(genomeList);
		//while(genomeList.size()<ENDINGTRIBESIZE){
		Random rand =new Random();
		for(int i=0;i<50;i++){
		int index=(int) Math.abs(rand.nextGaussian()*66);

		System.out.println(index);
		}
		
		
		
		
		
		
	}
	
	
	public static void main(String[] args){
		//()
		nextGeneration();
	}
	

}
