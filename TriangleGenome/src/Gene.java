import java.awt.Point;
import java.awt.Polygon;

/****************************************************************************
 * UnitTestMain
 *
 * @author Hans Weeks 
 * updated by Paige Romero
 *         This object holds 10 peices of
 *         data, representing the expression of a triangle: x and y coordinates
 *         for three points, r,g,b color values and opacity value a. Building
 *         block for the genome class.
 ****************************************************************************/
public class Gene extends Polygon
{
  public static final int NPOINTS = 3;// triangle
  public static final int NALLELE = 10;
  // public Polygon myTriangle;
  public int r, g, b, a;

  public Gene()
  {
    for (int i = 0; i < NPOINTS; i++)
    {
      xpoints[i] = 0;
      ypoints[i] = 0;
    }
    r = 0;
    g = 0;
    b = 0;
    a = 0;
    npoints = NPOINTS;
  }

  /****************************************************************************
   * Constructor
   * Input: coordinates x and y for three points in array, and 4 ints r,g,b,a 0<X<255
   * Description:creates a gene with desired genes
   ****************************************************************************/
  public Gene(Point[] myVertices, int[] myColors)// constructor to pass colors
  // and vertex locations
  {
    for (int i = 0; i < NPOINTS; i++)
    {
      xpoints[i] = myVertices[i].x;
      ypoints[i] = myVertices[i].y;
    }
    npoints = NPOINTS;

    r = myColors[0];
    g = myColors[1];
    b = myColors[2];
    a = myColors[3];
  }

  /****************************************************************************
   * setPoints
   * Input:array of points
   * Output:none
   * Description:set method for x and y vertices of triangle
   ****************************************************************************/
  public void setPoints(Point[] myVertices)
  {
    if (myVertices.length > NPOINTS)
    {
      System.err.println("More than 3 points were passed to Gene" + this);
      return;
    }
    for (int i = 0; i < NPOINTS; i++)
    {
      this.xpoints[i] = myVertices[i].x;
      this.ypoints[i] = myVertices[i].y;
    }
    npoints = NPOINTS;
  }

  /****************************************************************************
   * print
   ****************************************************************************/
  public void print()
  {
    System.out.print(xpoints[0] + "," + ypoints[0] + ";");
    System.out.print(xpoints[1] + "," + ypoints[1] + ";");
    System.out.print(xpoints[2] + "," + ypoints[2] + ";");
    System.out.print(r + "," + g + "," + b + "," + a + "\n");
  }

  /****************************************************************************
   * toString
   ****************************************************************************/
  public String toString()
  {

    return xpoints[0] + "," + ypoints[0] + ";" + xpoints[1] + "," + ypoints[1]
        + ";" + xpoints[2] + "," + ypoints[2] + ";" + r + "," + g + "," + b
        + "," + a;

  }

}