import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Tribe {
	Random rand=new Random();
	ArrayList<Genome> genomeList= new ArrayList<>();
	BufferedImage masterImage;
	public static final int STARTINGTRIBESIZE=200;
	public static final int ENDINGTRIBESIZE=500;
	public Tribe(BufferedImage image){
		masterImage=image;
		//populate genome list
	for(int i=0;i<STARTINGTRIBESIZE;i++){
		genomeList.add(new Genome(masterImage));

	}
	
		
	}
	
	public void generateFitscores(){
	for(Genome genome:genomeList){
		//creates a fitscore from each image. 
		//Should we preserve the Buffered Image as a phenotype???
		genome.fitscore=Statistics.getFitScore(masterImage, GenomeUtilities.getBufferedImage(genome));
		
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
