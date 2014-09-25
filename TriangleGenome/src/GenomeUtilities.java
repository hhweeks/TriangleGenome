import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

//support utilities for Genome creation, manipulation, and drawing.

public class GenomeUtilities {
	public static final int NPOINTS = 3;// triangle
	public static final int NCOLORS = 4;// r,g,b,a

	// generates a completely random genome of 200 genes
	public static void setRandomGenome(int width, int height, Genome genome) {
		Random rand = new Random();
		for (Gene myGene : genome.geneList) {
			Point[] myVertices = new Point[NPOINTS];
			for (int j = 0; j < NPOINTS; j++) {
				myVertices[j] = new Point(0, 0);
				myVertices[j].x = rand.nextInt(width);
				myVertices[j].y = rand.nextInt(height);
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
	}

	// draw 0-N triangles/genes on myPic
	public static void drawNTriangles(int N,
			TriangleGenomeGUI.ImagePanel myPic, Genome myGenome) {
		BufferedImage myIm = myPic.getImage();
		Graphics myGraphics = myIm.getGraphics();
		int height = myIm.getHeight();
		int width = myIm.getWidth();

		myGraphics.setColor(Color.white);
		myGraphics.fillRect(0, 0, width, height);
		for (int i = 0; i < N; i++) {
			myGraphics.setColor(new Color(myGenome.geneList.get(i).r,
					myGenome.geneList.get(i).g, myGenome.geneList.get(i).b,
					myGenome.geneList.get(i).a));
			myGraphics.fillPolygon(myGenome.geneList.get(i));
		}
		myPic.repaint();
	}

}
