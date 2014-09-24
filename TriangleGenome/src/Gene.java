import java.awt.Point;
import java.awt.Polygon;

public class Gene extends Polygon
{
  public static final int NPOINTS=3;//triangle
  public Polygon myTriangle;
  public int r,g,b,a;
  
  public Gene(Point[] myVertices, int[] myColors)//constructor to pass colors and vertex locations
  {
    for(int i=0;i<NPOINTS;i++)
    {
      xpoints[i]=myVertices[i].x;
      ypoints[i]=myVertices[i].y;
    }
    npoints=NPOINTS;
    
    r=myColors[0];
    g=myColors[1];
    b=myColors[2];
    a=myColors[3];
  }
  
  public void print()
  {
    System.out.println(r+g+b+a);
  }
}
