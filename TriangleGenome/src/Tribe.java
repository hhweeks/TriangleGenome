import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Tribe {
	
	ArrayList<Genome> genomeList= new ArrayList<>();
	BufferedImage masterImage;
	
	public Tribe(BufferedImage image){
		masterImage=image;
		//populate genome list
	for(int i=0;i<200;i++){
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
	

}
