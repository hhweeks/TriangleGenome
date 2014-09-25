import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GenomeGenerator{
	public static final int NPOINTS = 3;// triangle
	public static final int NCOLORS = 4;// r,g,b,a


	public static void drawRandomTriangles(TriangleGenomeGUI.ImagePanel myPic) {
		// myPic must contain an image
		BufferedImage myIm = myPic.getImage();

		Graphics myGraphics = myIm.getGraphics();
		int height = myIm.getHeight();
		int width = myIm.getWidth();

		Random rand = new Random();

		Point[] myVertices = new Point[NPOINTS];
		for (int i = 0; i < NPOINTS; i++) {
			myVertices[i] = new Point(0, 0);
			myVertices[i].x = rand.nextInt(width);
			myVertices[i].y = rand.nextInt(height);
		}

		int[] myColors = new int[NCOLORS];
		for (int i = 0; i < NCOLORS; i++) {
			myColors[i] = rand.nextInt(255);
		}
		//set background to white
		myGraphics.setColor(Color.white);
		myGraphics.fillRect(0, 0, width, height);

		Genome myGenome = new Genome();
		myGenome.setRandomGenome(height, width);
		for (int i = 0; i < 200; i++) {
			myGraphics.setColor(new Color(myGenome.geneList.get(i).r,
					myGenome.geneList.get(i).g, myGenome.geneList.get(i).b,
					myGenome.geneList.get(i).a));
			myGraphics.fillPolygon(myGenome.geneList.get(i));
		}
		myPic.repaint();
	}

}
