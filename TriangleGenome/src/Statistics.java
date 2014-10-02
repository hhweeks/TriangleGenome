import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class Statistics {
	
	public static long getFitScore(BufferedImage genomeImage, BufferedImage masterImage) {
		Raster masterRaster = masterImage.getRaster();
		Raster genomeRaster = genomeImage.getRaster();

		int redDistance;
		int blueDistance;
		int greenDistance;
		long euclidianDistanceSqr=0;

		int[] genomePixArray = new int[3];
		int[] masterPixArray = new int[3];
		int genomeRed;
		for (int i = 0; i < Math.min(genomeImage.getWidth(),
				masterImage.getWidth()); i++) {
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
		return euclidianDistanceSqr;

	}
	
	
	
	
	
	
	
	
	
	

}
