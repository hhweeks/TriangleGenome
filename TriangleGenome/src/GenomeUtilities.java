import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Random;

/****************************************************************************
 * GenomeUtilities
 *
 * @author Hans Weeks
 * @author Paige Romero This static object contains methods useful for
 *         manipulating Genomes and Genes which we choose not to add to the
 *         Genome or Gene class as to make those objects as lite as possible.
 ****************************************************************************/
public class GenomeUtilities {
	public static final int NPOINTS = 3;// points of a triangle
	public static final int NCOLORS = 4;// r,g,b,a
	public static final int NSEEDING = 3;
	static Random rand = new Random();

	/****************************************************************************
	 * setRandomGenome Input:Genome to randomize Output:none Description:when a
	 * Genome is initialized, it has 200 genes with values of 0. This method
	 * initialized random values for each gene. TODO currently totally random,
	 * no assurance of covering the background.
	 ****************************************************************************/
	public static void setRandomGenome(Genome genome) {
		for (Gene myGene : genome.geneList) {
			Point[] myVertices = new Point[NPOINTS];
			for (int j = 0; j < NPOINTS; j++) {
				myVertices[j] = new Point(0, 0);
				myVertices[j].x = rand.nextInt(genome.IMG_WIDTH);
				myVertices[j].y = rand.nextInt(genome.IMG_HEIGHT);
			}

			int[] myColors = new int[NCOLORS];

			for (int j = 0; j < NCOLORS; j++) {
				myColors[j] = rand.nextInt(255);
			}

			myGene.r = myColors[0];
			myGene.g = myColors[1];
			myGene.b = myColors[2];
			myGene.a = myColors[3];
			myGene.setPoints(myVertices);
		}

		// averagingGenome(genome,new BufferedImage(90, 90,
		// BufferedImage.TYPE_INT_RGB));
	}

	/****************************************************************************
	 * mixedSampleGenome Input:Genome to randomize Output:none Description:when
	 * a Genome is initialized, it has 200 genes with values of 0. This method
	 * initialized random values for each gene. Covers the background and gets
	 * averages and samples of all the colors in the image.
	 ****************************************************************************/
	public static void mixedSampleGenome(Genome genome,
			BufferedImage masterImage) {
		int centerx = rand.nextInt(genome.IMG_WIDTH / 16)
				- rand.nextInt(genome.IMG_WIDTH / 16) + genome.IMG_WIDTH / 2;
		int centery = rand.nextInt(genome.IMG_HEIGHT / 16)
				- rand.nextInt(genome.IMG_HEIGHT / 16) + genome.IMG_HEIGHT / 2;

		Raster masterRaster = masterImage.getRaster();

		int[] x0 = { 0, 0, centerx };
		int[] y0 = { 0, genome.IMG_HEIGHT, centery };
		int[] x1 = { 0, centerx, genome.IMG_WIDTH };
		int[] y1 = { 0, centery, 0 };
		int[] x2 = { genome.IMG_WIDTH, genome.IMG_WIDTH, centerx };
		int[] y2 = { 0, genome.IMG_HEIGHT, centery };
		int[] x3 = { 0, centerx, genome.IMG_WIDTH };
		int[] y3 = { genome.IMG_HEIGHT, centery, genome.IMG_HEIGHT };

		genome.geneList.get(0).xpoints = x0;
		genome.geneList.get(0).ypoints = y0;
		genome.geneList.get(1).xpoints = x1;
		genome.geneList.get(1).ypoints = y1;
		genome.geneList.get(2).xpoints = x2;
		genome.geneList.get(2).ypoints = y2;
		genome.geneList.get(3).xpoints = x3;
		genome.geneList.get(3).ypoints = y3;

		for (int i = 4; i < 200; i++)// bottom 4 triangle?
		{
			Point[] myVertices = new Point[NPOINTS];
			for (int j = 0; j < NPOINTS; j++) {
				myVertices[j] = new Point(0, 0);
				myVertices[j].x = rand.nextInt(genome.IMG_WIDTH);
				myVertices[j].y = rand.nextInt(genome.IMG_HEIGHT);
			}
			genome.geneList.get(i).setPoints(myVertices);
		}

		for (Gene myGene : genome.geneList) {
			int geneCase = rand.nextInt(10);

			if (geneCase < 9) {
				sampleRanomPixel(myGene, masterRaster);
			} else if (geneCase < 9)// these final 2 cases currently unused,
									// sampling whole pixels seems to be more
									// effective
			{
				sampleRandomChannel(myGene, masterRaster);
			} else {
				ranomGene(myGene);//
			}
		}
		genome.startFitscore = Statistics.getFitScore(
				GenomeUtilities.getBufferedImage(genome), genome.masterImage);

	}

	/****************************************************************************
	 * averagingGenome Input:Genome to randomize Output:none Description:when a
	 * Genome is initialized, it has 200 genes with values of 0. This method
	 * initialized random values for each gene. Covers the background and
	 * assigns the color of each image to the average value of pixels covered by
	 * that triangle.
	 ****************************************************************************/
	public static void averagingGenome(Genome genome, BufferedImage masterImage) {
		BufferedImage copiedImage = deepCopy(masterImage);
		int centerx = rand.nextInt(genome.IMG_WIDTH / 16)
				- rand.nextInt(genome.IMG_WIDTH / 16) + genome.IMG_WIDTH / 2;
		int centery = rand.nextInt(genome.IMG_HEIGHT / 16)
				- rand.nextInt(genome.IMG_HEIGHT / 16) + genome.IMG_HEIGHT / 2;

		Raster masterRaster = masterImage.getRaster();

		int[] x0 = { 0, 0, centerx };
		int[] y0 = { 0, genome.IMG_HEIGHT, centery };
		int[] x1 = { 0, centerx, genome.IMG_WIDTH };
		int[] y1 = { 0, centery, 0 };
		int[] x2 = { genome.IMG_WIDTH, genome.IMG_WIDTH, centerx };
		int[] y2 = { 0, genome.IMG_HEIGHT, centery };
		int[] x3 = { 0, centerx, genome.IMG_WIDTH };
		int[] y3 = { genome.IMG_HEIGHT, centery, genome.IMG_HEIGHT };

		genome.geneList.get(0).xpoints = x0;
		genome.geneList.get(0).ypoints = y0;
		genome.geneList.get(1).xpoints = x1;
		genome.geneList.get(1).ypoints = y1;
		genome.geneList.get(2).xpoints = x2;
		genome.geneList.get(2).ypoints = y2;
		genome.geneList.get(3).xpoints = x3;
		genome.geneList.get(3).ypoints = y3;

		for (int i = 4; i < 200; i++)// bottom 4 triangle?
		{
			Point[] myVertices = new Point[NPOINTS];
			for (int j = 0; j < NPOINTS; j++) {
				myVertices[j] = new Point(0, 0);
				myVertices[j].x = rand.nextInt(genome.IMG_WIDTH);
				myVertices[j].y = rand.nextInt(genome.IMG_HEIGHT);
			}
			genome.geneList.get(i).setPoints(myVertices);
		}
		for (int i = 0; i < genome.geneList.size(); i++) {
			Gene myGene = genome.geneList.get(i);
			int randomAlpha = 5;
			if (i < 8) {
				randomAlpha = rand.nextInt(50) + 10;
			} else {
				randomAlpha = rand.nextInt(50) + 100;
			}

			myGene.a = randomAlpha;

			int[] myColors = getAreaColorAvg(myGene, copiedImage);
			myGene.r = myColors[0];
			myGene.g = myColors[1];
			myGene.b = myColors[2];

		}
		genome.startFitscore = Statistics.getFitScore(
				GenomeUtilities.getBufferedImage(genome), genome.masterImage);
	}

	/****************************************************************************
	 * averageGridGenome Input:Genome to randomize Output:none
	 * Description:Covers the image with 100 rectangles each being covered by
	 * two triangles, each with an average value of all the pixels being covered
	 * by that triangle
	 ****************************************************************************/
	public static void averageGridGenome(Genome genome,
			BufferedImage masterImage) {
		int h = masterImage.getHeight();
		int w = masterImage.getWidth();
		int xScale = (w / 10);
		int yScale = (h / 10);
		// System.out.println(xScale+";"+yScale);
		BufferedImage copiedImage = deepCopy(masterImage);
		for (int k = 0; k < 2; k++) {

			for (int i = 0; i < 100; i++) {
				Point[] myVertices = new Point[NPOINTS];
				for (int j = 0; j < NPOINTS; j++) {
					myVertices[j] = new Point(0, 0);
					if (j == 2) {
						if (k % 2 == 0) {
							myVertices[j].x = (i % 10) * (xScale);
							myVertices[j].y = (i / 10 + 1) * yScale;

						} else {
							myVertices[j].x = (i % 10 + 1) * xScale;
							myVertices[j].y = (i / 10) * (yScale);

						}

					} else {
						myVertices[j].x = (i % 10 + j) * (xScale);
						myVertices[j].y = (i / 10 + j) * (yScale);
					}
				}

				genome.geneList.get(100 * k + i).setPoints(myVertices);
			}
		}
		for (int i = 0; i < genome.geneList.size(); i++) {
			Gene myGene = genome.geneList.get(i);
			int randomAlpha = 5;
			randomAlpha = rand.nextInt(150) + 100;

			myGene.a = randomAlpha;

			int[] myColors = getAreaColorAvg(myGene, copiedImage);
			myGene.r = myColors[0];
			myGene.g = myColors[1];
			myGene.b = myColors[2];

		}
		genome.startFitscore = Statistics.getFitScore(
				GenomeUtilities.getBufferedImage(genome), genome.masterImage);
	}

	/****************************************************************************
	 * deepCopy Input:BufferedImage to copy Output:Copy of BufferedImage
	 * Description:Makes an element by element duplicate of input bufferedImage
	 ****************************************************************************/
	static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/****************************************************************************
	 * sampleRanomPixel Input:Gene to sample to, Raster to sample from
	 * Output:Copy of BufferedImage Description:samples a random pixel from
	 * master raster and assigns it color to input gene.
	 ****************************************************************************/
	static void sampleRanomPixel(Gene myGene, Raster masterRaster) {
		int genesMaxX = maxGenesDim(myGene.xpoints);
		int genesMaxY = maxGenesDim(myGene.ypoints);
		int genesMinX = minGenesDim(myGene.xpoints);
		int genesMinY = minGenesDim(myGene.ypoints);
		int x = -1;
		int y = -1;
		int i = 0;
		while (!myGene.contains(x, y)) {
			// System.out.println("supprise mother fucker");
			x = rand.nextInt(genesMaxX - genesMinX) + genesMinX;
			y = rand.nextInt(genesMaxY - genesMinY) + genesMinY;

			if (i > 3)
				break;
			i++;

		}

		int[] samplePixel = new int[4];
		masterRaster.getPixel(x, y, samplePixel);
		myGene.r = samplePixel[0];
		myGene.g = samplePixel[1];
		myGene.b = samplePixel[2];
		int randomAlpha = rand.nextInt(50) + 55;
		myGene.a = randomAlpha;
	}

	static void sampleRandomChannel(Gene myGene, Raster masterRaster) {
		int x0 = rand.nextInt(masterRaster.getWidth());
		int y0 = rand.nextInt(masterRaster.getHeight());
		int x1 = rand.nextInt(masterRaster.getWidth());
		int y1 = rand.nextInt(masterRaster.getHeight());
		int x2 = rand.nextInt(masterRaster.getWidth());
		int y2 = rand.nextInt(masterRaster.getHeight());
		int[] samplePixel0 = new int[4];
		int[] samplePixel1 = new int[4];
		int[] samplePixel2 = new int[4];
		masterRaster.getPixel(x0, y0, samplePixel0);
		myGene.r = samplePixel0[0];
		masterRaster.getPixel(x1, y1, samplePixel1);
		myGene.g = samplePixel1[1];
		masterRaster.getPixel(x2, y1, samplePixel2);
		myGene.b = samplePixel2[2];
		int randomAlpha = rand.nextInt(50) + 55;
		myGene.a = randomAlpha;
	}

	static void ranomGene(Gene myGene) {
		int randomRed = rand.nextInt(254);
		int randomGreen = rand.nextInt(254);
		int randomBlue = rand.nextInt(254);
		int randomAlpha = rand.nextInt(50) + 55;
		myGene.r = randomRed;
		myGene.g = randomGreen;
		myGene.b = randomBlue;
		myGene.a = randomAlpha;
	}

	static int maxGenesDim(int[] dim) {
		int max = 0;
		for (int dimension : dim) {
			if (dimension > max) {
				max = dimension;
			}
		}
		return max;
	}

	static int minGenesDim(int[] dim) {
		int min = Integer.MAX_VALUE;
		for (int dimension : dim) {
			if (dimension < min) {
				min = dimension;
			}
		}
		return min;
	}

	/****************************************************************************
	 * getAreaColorAvg Input:Gene to set color of, BufferedImage to sample from
	 * Output:3 element array to set a color Description:gets the color that is
	 * the average of all the pixels covered by gene.
	 ****************************************************************************/
	public static int[] getAreaColorAvg(Gene gene, BufferedImage image) {
		int redCount = 0;
		int blueCount = 0;
		int greenCount = 0;
		int nPixCount = 1;
		Raster raster = image.getRaster();

		// Find the min and max of the x and y
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

					try {
						raster.getPixel(x, y, pixel);
					} catch (IndexOutOfBoundsException e) {
						System.out.println(gene + ";" + x + ";" + y);
					}

					redCount += pixel[0];
					blueCount += pixel[1];
					greenCount += pixel[2];
					nPixCount++;
				}
			}
		}

		int red = redCount / nPixCount;
		int blue = blueCount / nPixCount;
		int green = greenCount / nPixCount;

		int[] pixout = { red, blue, green };
		return pixout;
	}

	/****************************************************************************
	 * drawNTriangles Input:int N (0-200), imagePanel where triangles are being
	 * drawn, and Genome being drawn Output:none Description:draws triangles in
	 * layers 0-200. Is called by the slider on the GUI to allow us to see the
	 * triangles that may be entirely covered up otherwise. TODO correct?
	 ****************************************************************************/
	public static void drawNTriangles(int N,
			TriangleGenomeGUI.ImagePanel myPic, Genome myGenome) {
		BufferedImage myIm = new BufferedImage(
				TriangleGenomeGUI.imageWindow.image.getWidth(),
				TriangleGenomeGUI.imageWindow.image.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics myGraphics = myIm.getGraphics();
		int height = myIm.getHeight();
		int width = myIm.getWidth();

		myGraphics.setColor(Color.white);
		myGraphics.fillRect(0, 0, width, height);
		for (int i = 0; i < N; i++) {
			Gene gene = myGenome.geneList.get(i);
			checkColorValues(gene);
			// gene.print();
			myGraphics.setColor(new Color(gene.r, gene.g, gene.b, gene.a));
			myGraphics.fillPolygon(gene);
		}
		myPic.image = myIm;
		myPic.repaint();
	}

	/****************************************************************************
	 * getBufferedImage Input:Genome Output:Buffered Image of that Genome
	 * Description:draws the Genome as a BufferedImage
	 ****************************************************************************/
	public static BufferedImage getBufferedImage(Genome myGenome) {
		long startTime = System.currentTimeMillis();
		BufferedImage myIm = new BufferedImage(myGenome.IMG_WIDTH,
				myGenome.IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics myGraphics = myIm.getGraphics();
		int height = myIm.getHeight();
		int width = myIm.getWidth();

		myGraphics.setColor(Color.white);
		myGraphics.fillRect(0, 0, width, height);
		// Color polyColor=new Color(0,0,0);
		for (int i = 0; i < myGenome.geneList.size(); i++) {
			Gene gene = myGenome.geneList.get(i);
			checkColorValues(gene);
			// gene.print();

			myGraphics.setColor(new Color(gene.r, gene.g, gene.b, gene.a));
			myGraphics.fillPolygon(gene);

			// System.out.println(System.currentTimeMillis()-startTime);

			myGraphics.setColor(new Color(gene.r, gene.g, gene.b, gene.a));
			try{
			myGraphics.fillPolygon(myGenome.geneList.get(i));
			}
			catch(IndexOutOfBoundsException e){
				
			}
		}
		return myIm;
	}

	/****************************************************************************
	 * getScaledBufferedImage Input:Genome to draw scaled version of, desired
	 * scale Output:scaled Buffered Image of image produced by genome
	 * Description:creates a scaled version of an image produced by myGenome
	 ****************************************************************************/
	public static BufferedImage getScaledBufferedImage(Genome myGenome,
			int scale) {
		BufferedImage myIm = new BufferedImage(myGenome.IMG_WIDTH,
				myGenome.IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics myGraphics = myIm.getGraphics();
		int height = myIm.getHeight() / scale;
		int width = myIm.getWidth() / scale;

		myGraphics.setColor(Color.white);
		myGraphics.fillRect(0, 0, width, height);
		// Color polyColor=new Color(0,0,0);
		for (int i = 0; i < myGenome.geneList.size(); i++) {
			Gene gene = GenomeUtilities.geneCopy(myGenome.geneList.get(i));

			checkColorValues(gene);
			// gene.print();

			myGraphics.setColor(new Color(gene.r, gene.g, gene.b, gene.a));
			// myGraphics.fillPolygon(gene);

			// System.out.println(System.currentTimeMillis()-startTime);

			myGraphics.setColor(new Color(gene.r, gene.g, gene.b, gene.a));
			// System.out.println(gene);
			for (int xi = 0; xi < gene.xpoints.length; xi++) {
				gene.xpoints[xi] /= scale;
			}
			for (int yi = 0; yi < gene.ypoints.length; yi++) {
				gene.ypoints[yi] /= scale;
			}

			// System.out.println(gene);

			myGraphics.fillPolygon(gene);
		}
		return myIm;
	}

	/****************************************************************************
	 * scaleImage Input:input BufferedImage, desired scale Output:Scaled
	 * BufferedImage Description:returns a scaled bufferedImage of input
	 ****************************************************************************/
	public static BufferedImage scaleImage(BufferedImage inImage, int scale) {

		BufferedImage image = deepCopy(inImage);

		int w = image.getWidth() / scale;
		int h = image.getHeight() / scale;

		BufferedImage resizedImg = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();// image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.drawImage(image, 0, 0, w, h, null);

		g2.dispose();
		return resizedImg;
	}

	/****************************************************************************
	 * checkValues Input:Gene to check, image height,image width Output:none
	 * Description:ensures a gene has reasonable values
	 ****************************************************************************/

	public static void checkValues(Gene gene, int width, int height) {
		checkColorValues(gene);
		for (int i = 0; i < gene.xpoints.length; i++) {
			if (gene.xpoints[i] > width)
				gene.xpoints[i] = width;
			if (gene.xpoints[i] < 0)
				gene.xpoints[i] = 0;

		}
		for (int i = 0; i < gene.ypoints.length; i++) {
			if (gene.ypoints[i] > height)
				gene.ypoints[i] = height;
			if (gene.ypoints[i] < 0)
				gene.ypoints[i] = 0;

		}

	}

	/****************************************************************************
	 * checkColorValues Input:Gene to check Output:none Description:ensures a
	 * gene has reasonable color values
	 ****************************************************************************/
	public static void checkColorValues(Gene gene) {
		if (gene.a > 255)
			gene.a = 255;
		if (gene.a < 0)
			gene.a = 0;
		if (gene.r > 255)
			gene.r = 255;
		if (gene.r < 0)
			gene.r = 0;
		if (gene.g > 255)
			gene.g = 255;
		if (gene.g < 0)
			gene.g = 0;
		if (gene.b > 255)
			gene.b = 255;
		if (gene.b < 0)
			gene.b = 0;

	}

	/****************************************************************************
	 * genomeEqual Input:two Genomes to compare Output:boolean
	 * Description:compares each of 200 genes in a Genome by calling geneEquals
	 * on them.
	 ****************************************************************************/
	public static boolean genomeEqual(Genome oneGenome, Genome twoGenome) {

		for (int i = 0; i < oneGenome.geneList.size(); i++) {
			if (!geneEqual(oneGenome.geneList.get(i), twoGenome.geneList.get(i)))
				return false;
		}
		return true;
	}

	/****************************************************************************
	 * geneEqual Input:Genes to compare Output:boolean Description:compares 10
	 * alleles (base-pairs?) in a gene
	 ****************************************************************************/
	public static boolean geneEqual(Gene gene1, Gene gene2) {

		return (gene1.xpoints[0] == gene2.xpoints[0]
				&& gene1.xpoints[1] == gene2.xpoints[1]
				&& gene1.ypoints[0] == gene2.ypoints[0]
				&& gene1.ypoints[1] == gene2.ypoints[1] && gene1.r == gene2.r
				&& gene1.g == gene2.g && gene1.b == gene2.b && gene1.a == gene2.a);
	}

	/****************************************************************************
	 * genomeCopy Input:Genome Output:new Genome with identical genes
	 * Description:deep copy of genome, calls gene copy
	 ****************************************************************************/
	public static Genome genomeCopy(Genome myGenome) {

		Genome genomeOut = new Genome(myGenome.IMG_WIDTH, myGenome.IMG_HEIGHT);
		genomeOut.geneList.clear();
		for (int i = 0; i < myGenome.geneList.size(); i++) {

			genomeOut.geneList.add(geneCopy(myGenome.geneList.get(i)));

		}
		return genomeOut;
	}
	/****************************************************************************
	 * genomeOverWrite Input:Genome Output:none
	 * Description:overwrite the genome of one input with that of another
	 ****************************************************************************/
	
	public static void genomeOverWrite(Genome saveGenome,Genome copyGenome) {

		
		for (int i = 0; i < copyGenome.geneList.size(); i++) {

			saveGenome.geneList.set(i,geneCopy(copyGenome.geneList.get(i)));

		}
	}

	/****************************************************************************
	 * geneCopy Input:Gene Output:new Gene with same alleles (base-pairs)
	 * Description:deep copy of gene
	 ****************************************************************************/
	public static Gene geneCopy(Gene myGene) {
		Gene geneOut = new Gene();
		try{
		geneOut.xpoints[0] = myGene.xpoints[0];
		geneOut.ypoints[0] = myGene.ypoints[0];
		geneOut.xpoints[1] = myGene.xpoints[1];
		geneOut.ypoints[1] = myGene.ypoints[1];
		geneOut.xpoints[2] = myGene.xpoints[2];
		geneOut.ypoints[2] = myGene.ypoints[2];
		geneOut.r = myGene.r;
		geneOut.g = myGene.g;
		geneOut.b = myGene.b;
		geneOut.a = myGene.a;
		geneOut.npoints = myGene.npoints;
		}
		catch(NullPointerException e){
			
			
		}
		return geneOut;
	}

	/****************************************************************************
	 * hammingDistance Input: Output: Description:TODO redundant, hamming
	 * distance now its own class
	 ****************************************************************************/
	public static int hammingDistance(Genome genome1, Genome genome2) {
		int hScore = 0;

		for (int i = 0; i < genome1.geneList.size(); i++) {

			hScore += hammingGeneHelper(genome1.geneList.get(i),
					genome2.geneList.get(i));

		}
		return hScore;

	}

	/****************************************************************************
	 * hammingDistanceHeler Input: Output: Description:TODO redundant, hamming
	 * distance now its own class
	 ****************************************************************************/
	private static int hammingGeneHelper(Gene gene1, Gene gene2) {
		int count = 0;
		if (gene1.xpoints[0] != gene2.xpoints[0])
			count++;
		if (gene1.xpoints[1] != gene2.xpoints[1])
			count++;
		if (gene1.xpoints[2] != gene2.xpoints[2])
			count++;
		if (gene1.ypoints[0] != gene2.ypoints[0])
			count++;
		if (gene1.ypoints[1] != gene2.ypoints[1])
			count++;
		if (gene1.ypoints[2] != gene2.ypoints[2])
			count++;
		if (gene1.r != gene2.r)
			count++;
		if (gene1.g != gene2.g)
			count++;
		if (gene1.b != gene2.b)
			count++;
		if (gene1.a != gene2.a)
			count++;
		return count;
	}
}
