import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;


public class TriangleGenomeGUI extends JFrame{
	
	
	ImagePanel imageWindow;
	ImagePanel triangleWindow;
	JComboBox<String> imageSelect;
	JSlider triangleSlider;
	JButton startButton;
	
	public TriangleGenomeGUI()throws IOException{
		String path="C:\\Users\\Paige\\Documents\\GitHub\\TriangleGenome\\TriangleGenome\\src\\TriangleGenome\\TriangleGenome\\src\\";
		String[] filenames={"MonaLisa.png","poppyfields.png"};
		
		JPanel controlPanel=new JPanel();
	
		controlPanel.setBounds(0, 500, 800, 100);
		controlPanel.setBackground(Color.BLACK);
		
		
		
		
		imageSelect=new JComboBox<String>(filenames);
		imageSelect.setSelectedIndex(0);
		
		String filename = (String)imageSelect.getSelectedItem();
		File imageFile=new File(path+filename);
		BufferedImage img;

		img = ImageIO.read(imageFile);
	
		
		imageWindow=new ImagePanel(img,0,0);
		
		imageSelect.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String flname=(String)imageSelect.getSelectedItem();
				String path="C:\\Users\\Paige\\Documents\\GitHub\\TriangleGenome\\TriangleGenome\\src\\TriangleGenome\\TriangleGenome\\src\\";
				
				File imageFile=new File(path+flname);
				try{imageWindow.changeImage(ImageIO.read(imageFile));}
				catch(IOException ec){
					System.out.println("you're dumb");
				}
				
				
			}
			
			
			
			
		});
		
		
		
		
		
		
		
		
		
		
		controlPanel.add(imageSelect);
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		//populate frame
		this.add(controlPanel);
		this.add(imageWindow);
		
		
		
		
		
		this.setSize(750, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public class ImagePanel extends JPanel{

	    private BufferedImage image;
	    int x,y;

	    public ImagePanel(BufferedImage imagein,int xin,int yin) {
	                      
	          image = imagein;
	          x=xin;
	          y=yin;
	      
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawImage(image, x, y, null);  }
	    
	    private void changeImage(BufferedImage cim){
	    	image=cim;
	    	this.repaint();
	    	
	    }

	}
	
	
	
public static void main(String[] args) throws IOException{
	TriangleGenomeGUI tg=new TriangleGenomeGUI();
	
	
}


}
