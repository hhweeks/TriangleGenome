import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class HillClimber {
	//repeat supersedes revert
	public BufferedImage image;
	boolean repeat;
	boolean revert;
	Gene lastGene;
	// I need to to define input parameters based on which allele is which.
	int lastAllele;
	int lastShift;

	public HillClimber(BufferedImage img) {
		img = image;
		repeat=false;
		revert=false;


	}

	
	
	public void climbLoop(Genome genome,int N){
		
		for(int i=0;i<N;i++){
			
			repeat=climbStep(genome);
			
		}
		
		
	}
	
	
	
	
	
	
	public boolean climbStep(Genome genome) {

		long startScore = Statistics.getFitScore(
				GenomeUtilities.getBufferedImage(genome), image);

		if (repeat) {

			// mutate here

		} 
		else if(revert){
			// mutate negative
			//random mutate
			
		}
		else {
			// random mutate

		}
		
		long endScore = Statistics.getFitScore(
				GenomeUtilities.getBufferedImage(genome), image);
		
		
		
		return endScore<startScore;
		
		
		

	}

	public Gene getWurstQuadrent(Genome genome) {
		long tmpscore = 0;
		long score;
		int tmp = 0;
		for (int i = 0; i < 4; i++) {
			score = getLocalFit(genome.geneList.get(i));
			if (score > tmpscore) {
				tmp = i;
				tmpscore = score;
			}

		}

		return genome.geneList.get(tmp);

	}

	public long getLocalFit(Gene gene) {

		Raster raster = this.image.getRaster();
		int red, blue, green;
		long sum = 0;

		int minX = Math.min(gene.xpoints[0],
				Math.min(gene.xpoints[1], gene.xpoints[2]));
		int maxX = Math.max(gene.xpoints[0],
				Math.max(gene.xpoints[1], gene.xpoints[2]));
		int minY = Math.min(gene.ypoints[0],
				Math.min(gene.ypoints[1], gene.ypoints[2]));
		int maxY = Math.max(gene.ypoints[0],
				Math.max(gene.ypoints[1], gene.ypoints[2]));

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (gene.contains(x, y)) {
					int[] pixel = { 0, 0, 0 };
					raster.getPixel(x, y, pixel);

					red = (pixel[0] - gene.r);
					red = red * red;
					blue = pixel[0] - gene.b;
					blue = blue * blue;
					green = pixel[0] - gene.g;
					green = green * green;
					sum = red + green + blue;

					// System.out.println(red+";"+green+";"+blue);

				}

			}

		}

		return sum / ((maxX - minX) * (maxY - minY));
	}

}
