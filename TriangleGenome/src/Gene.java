import java.awt.Point;
import java.awt.Polygon;

public class Gene extends Polygon
{
  public static final int NPOINTS=3;//triangle
  //public Polygon myTriangle;
  public int r,g,b,a;
  
  
  
  public Gene(){
	  for(int i=0;i<NPOINTS;i++)
	    {
	      xpoints[i]=0;
	      ypoints[i]=0;
	    }
	    npoints=NPOINTS;
	  
  }
  
  
  
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
  
  public void setPoints(Point[] myVertices){
	  if (myVertices.length>NPOINTS){
		  System.err.println("More than 3 points were passed to Gene"+this);
		  return;
	  }
	  for(int i=0;i<NPOINTS;i++)
	    {
	      this.xpoints[i]=myVertices[i].x;
	      this.ypoints[i]=myVertices[i].y;
	    }
	  npoints=NPOINTS;
  }
  
  
  
  
  
  
  public void print()
  {
    System.out.print(xpoints[0]+","+ypoints[0]+";");
    System.out.print(xpoints[1]+","+ypoints[1]+";");
    System.out.print(xpoints[2]+","+ypoints[2]+";");
    System.out.print(r+","+g+","+b+","+a+"\n");
  }
  
  
  
}
