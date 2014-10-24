import java.awt.image.BufferedImage;
import java.util.ArrayList;

/****************************************************************************
 * UnitTestMain
 *
 * @author Hans Weeks updated
 * @author Romero
 * 
 *         This object holds 200 (NUM_GENES) genes (data for triangle) in an
 *         ArrayList It stores the dimensions of the picture that it is
 *         "representing", as well as a fitscore, rating how will it recreates
 *         the original image (updated by statistics)
 ****************************************************************************/
public class Genome implements Comparable<Genome> {
	public volatile BufferedImage masterImage;
	public BufferedImage scaledImage;// this gets set in hill climb
	public static final int NUM_GENES = 200;
	public long fitscore;
	public long startFitscore;
	public double improvmentRate;
	public final int IMG_WIDTH;
	public final int IMG_HEIGHT;
	public ArrayList<Gene> geneList = new ArrayList<>();
	public HillClimber hc;
	public int tribeId, genomeId;

	/****************************************************************************
	 * Constructor given diemnsion Input:dimensions of the picture being
	 * recreated Description:saves global variables and adds 200 new genes
	 ****************************************************************************/
	public Genome(int width, int height) {

		masterImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		hc = new HillClimber(masterImage, this, 50);
		IMG_WIDTH = width;
		IMG_HEIGHT = height;
		for (int i = 0; i < NUM_GENES; i++) {
			Gene myGene = new Gene();
			geneList.add(myGene);
		}
	}

	/****************************************************************************
	 * Constructor given BufferedImage Input:image being recreated
	 * Description:saves global variables and adds 200 new genes
	 ****************************************************************************/
	public Genome(BufferedImage image) {
		masterImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		hc = new HillClimber(masterImage, this, 50);
		IMG_WIDTH = image.getTileWidth();
		IMG_HEIGHT = image.getHeight();
		for (int i = 0; i < NUM_GENES; i++) {
			Gene myGene = new Gene();
			geneList.add(myGene);
		}
	}

	/****************************************************************************
	 * compareTo Input:Genome Output:int representing which Genome has better
	 * fitness score Description:compares 2 genomes, returns negative if passed
	 * genome is more fit
	 ****************************************************************************/
	public int compareTo(Genome genome) {
		long fitDiff = fitscore - genome.fitscore;
		if (fitDiff < 0l)
			return -1;
		else if (fitDiff == 0l)
			return 0;
		else
			return 1;
	}

	// @Override
	// public int compareTo(Object o) {
	// // TODO Auto-generated method stub
	// return 0;
	// }

}