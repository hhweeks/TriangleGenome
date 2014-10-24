import java.awt.image.BufferedImage;
import java.awt.image.Raster;
/****************************************************************************
 * HammingDistance
 *
 * @author Paige Romero
 * 			used to calculate fitscores for modeled images
 ****************************************************************************/
public class Statistics {
	
	/****************************************************************************
	 * getFitScore 
	 * Input:genomeImage, masterImage
	 * calculate the model fit score for a given image
	 ****************************************************************************/
	public static long getFitScore(BufferedImage genomeImage,
			BufferedImage masterImage) {

		Raster masterRaster = masterImage.getRaster();

		Raster genomeRaster = genomeImage.getRaster();

		int redDistance;
		int blueDistance;
		int greenDistance;
		long euclidianDistanceSqr = 0;

		int[] genomePixArray = new int[3];
		int[] masterPixArray = new int[3];

		for (int i = 0; i < genomeImage.getWidth()
				&& i < masterImage.getWidth(); i++) {
			for (int j = 0; j < genomeImage.getHeight()
					&& j < masterImage.getHeight(); j++) {
				genomeRaster.getPixel(i, j, genomePixArray);

				// System.out.println(masterRaster);

				masterRaster.getPixel(i, j, masterPixArray);

				redDistance = genomePixArray[0] - masterPixArray[0];
				blueDistance = genomePixArray[1] - masterPixArray[1];
				greenDistance = genomePixArray[2] - masterPixArray[2];

				euclidianDistanceSqr += (redDistance * redDistance)
						+ (blueDistance * blueDistance)
						+ (greenDistance * greenDistance);
			}
		}
		return euclidianDistanceSqr
				/ (masterImage.getHeight() * masterImage.getWidth());
	}
	/****************************************************************************
	 * getSmallFitScore 
	 * Input:genomeImage, masterImage ,small step
	 * calculate the model fit score for a given image on a small step
	 ****************************************************************************/
	public static long getSmallFitScore(BufferedImage genomeImage,
			BufferedImage masterImage, int iStep) {
		Raster masterRaster = masterImage.getRaster();
		Raster genomeRaster = genomeImage.getRaster();

		int redDistance;
		int blueDistance;
		int greenDistance;
		long euclidianDistanceSqr = 0;

		int[] genomePixArray = new int[3];
		int[] masterPixArray = new int[3];

		for (int i = 0; i < Math.min(genomeImage.getWidth(),
				masterImage.getWidth()); i += iStep) {
			for (int j = 0; j < Math.min(genomeImage.getHeight(),
					masterImage.getHeight()); j++) {
				redDistance = genomeRaster.getPixel(i, j, genomePixArray)[0]
						- masterRaster.getPixel(i, j, masterPixArray)[0];
				blueDistance = genomeRaster.getPixel(i, j, genomePixArray)[1]
						- masterRaster.getPixel(i, j, masterPixArray)[1];
				greenDistance = genomeRaster.getPixel(i, j, genomePixArray)[2]
						- masterRaster.getPixel(i, j, masterPixArray)[2];

				euclidianDistanceSqr += (redDistance * redDistance)
						+ (blueDistance * blueDistance)
						+ (greenDistance * greenDistance);
			}
		}
		return euclidianDistanceSqr
				/ (masterImage.getHeight() * masterImage.getWidth());
	}
}
