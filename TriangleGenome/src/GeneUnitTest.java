import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class GeneUnitTest extends JFrame
{
  public static final int NPOINTS=3;//triangle
  public static final int NCOLORS=4;//r,g,b,a
  static Graphics myGraphics;
  static Picture myPic;  
  
  
  public static void main(String[] args)
  {
    GeneUnitTest tg=new GeneUnitTest();
    myPic= new Picture(500,500);
    myGraphics=myPic.getOffScreenGraphics();
    
    int[] xpoints={50, 10, 90};
    int[] ypoints={10, 50, 50};
    int npoints=3;
    
    Random rand= new Random();
    
    Point[] myVertices= new Point[NPOINTS];
    for(int i=0;i<NPOINTS;i++)
    {
      myVertices[i]=new Point(0,0);
      myVertices[i].x=rand.nextInt(500);
      myVertices[i].y=rand.nextInt(500);
    }
    
    int[] myColors=new int[NCOLORS];
    for(int i=0;i<NCOLORS;i++)
    {
      myColors[i]=rand.nextInt(255);
    }
    
    Gene blueGene= new Gene(myVertices,myColors);
    myGraphics.setColor(Color.white);
    myGraphics.fillRect(0, 0, 500, 500);
    
    Genome myGenome=new Genome();
    for(int i=0;i<200;i++)
    {
      myGraphics.setColor(new Color(myGenome.geneList.get(i).r,myGenome.geneList.get(i).g,myGenome.geneList.get(i).b,myGenome.geneList.get(i).a));
      tg.paint(myGraphics, myGenome.geneList.get(i));     
    }
    myPic.repaint();
  }
  
  public void paint(Graphics g, Gene p)
  {
    g.fillPolygon(p);
  }
  
}
