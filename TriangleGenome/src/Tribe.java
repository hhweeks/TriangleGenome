import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Tribe {
	Random rand=new Random();
	ArrayList<Genome> genomeList= new ArrayList<>();
	BufferedImage masterImage;
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
