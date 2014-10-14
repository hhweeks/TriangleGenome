import java.awt.image.BufferedImage;

public class ImageContainer
{
  private volatile BufferedImage image;

  public ImageContainer()
  {

  }

  public BufferedImage getImage()
  {
    return image;
  }

  public void setImage(BufferedImage img)
  {
    image=img;
  }
}
