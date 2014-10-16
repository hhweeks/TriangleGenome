import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.*;


public class XMLUtil
{
  public static void writeXML(String fileName, Genome myGenome)
  {    
    String rootElement="gene";    
    XMLOutputFactory myXOF=XMLOutputFactory.newInstance();
    try
    {      
      XMLStreamWriter myXSW= myXOF.createXMLStreamWriter(new FileOutputStream(fileName), "UTF-8");
      
      //declaration
      myXSW.writeStartDocument("UTF-8", "1.0");
      myXSW.writeCharacters("\n");
      
      //write the root element      
      myXSW.writeStartElement("genome");
      
      //write genomes variable, width and height
      myXSW.writeStartElement("width");
      myXSW.writeCharacters(Integer.toString(myGenome.IMG_WIDTH));
      myXSW.writeEndElement();
      
      myXSW.writeStartElement("height");
      myXSW.writeCharacters(Integer.toString(myGenome.IMG_WIDTH));
      myXSW.writeEndElement();
      
      for(int index=0;index<myGenome.geneList.size();index++)
      {
        Gene gene=myGenome.geneList.get(index);
        
        //store fields in gene all variables
        String x1=Integer.toString(gene.xpoints[0]);
        String y1=Integer.toString(gene.ypoints[0]);
        String x2=Integer.toString(gene.xpoints[1]);
        String y2=Integer.toString(gene.ypoints[1]);
        String x3=Integer.toString(gene.xpoints[2]);
        String y3=Integer.toString(gene.ypoints[2]);
        String r=Integer.toString(gene.r);
        String g=Integer.toString(gene.g);
        String b=Integer.toString(gene.b);
        String a=Integer.toString(gene.a);
        
        //write gene index
        myXSW.writeStartElement("gene");
        myXSW.writeStartElement("index" + Integer.toString(index));
        myXSW.writeEndElement();
        
        //write gene's elements
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("x1");
        myXSW.writeCharacters("\n\t\t"+x1);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("y1");
        myXSW.writeCharacters("\n\t\t"+y1);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("x2");
        myXSW.writeCharacters("\n\t\t"+x2);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("y2");
        myXSW.writeCharacters("\n\t\t"+y2);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("x3");
        myXSW.writeCharacters("\n\t\t"+x3);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("y3");
        myXSW.writeCharacters("\n\t\t"+y3);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("red");
        myXSW.writeCharacters("\n\t\t"+r);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("green");
        myXSW.writeCharacters("\n\t\t"+g);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("blue");
        myXSW.writeCharacters("\n\t\t"+b);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("alpha");
        myXSW.writeCharacters("\n\t\t"+a);
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
        
        myXSW.writeEndElement();
      }
      myXSW.writeEndElement();
      
    //write end of document
      myXSW.writeEndDocument();
      myXSW.flush();
      myXSW.close();
      
      
    }catch (XMLStreamException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (FileNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static Genome readXML(String filePath)
  {
    Genome myGenome=null;//TODO write xml needs to have a field for width height so this can be initialized TODO actually, should just hand it an image from a file pathname
    Gene myGene=null;
    int genWidth=0;
    int genHeight=0;
    
    XMLInputFactory myXIF=XMLInputFactory.newInstance();
    
    try
    {
      XMLEventReader myXER=myXIF.createXMLEventReader(new FileInputStream(filePath));
      while(myXER.hasNext())
      {
        XMLEvent myEvent=myXER.nextEvent();
        if(myEvent.isStartElement())
        {
          StartElement mySE=myEvent.asStartElement();
          if(mySE.getName().getLocalPart().equals("width"))
          {
            myEvent=myXER.nextEvent();
            genWidth=Integer.parseInt(myEvent.asCharacters().getData());
          }
          else if(mySE.getName().getLocalPart().equals("height"))
          {
            myEvent=myXER.nextEvent();
            genHeight=Integer.parseInt(myEvent.asCharacters().getData());
            //if height found, we can now initialize myGenome
            myGenome=new Genome(genWidth,genHeight);
          }
          else if(mySE.getName().getLocalPart().equals("gene"))
          {
            myGene=new Gene();
          }
          //add gene's alleles
          //x1
          else if(mySE.getName().getLocalPart().equals("x1"))
          {
            myEvent=myXER.nextEvent();
            myGene.xpoints[0]=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //y1
          else if(mySE.getName().getLocalPart().equals("y1"))
          {
            myEvent=myXER.nextEvent();
            myGene.ypoints[0]=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //x2
          else if(mySE.getName().getLocalPart().equals("x2"))
          {
            myEvent=myXER.nextEvent();
            myGene.xpoints[1]=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //y2
          else if(mySE.getName().getLocalPart().equals("y2"))
          {
            myEvent=myXER.nextEvent();
            myGene.ypoints[1]=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //x3
          else if(mySE.getName().getLocalPart().equals("x3"))
          {
            myEvent=myXER.nextEvent();
            myGene.xpoints[2]=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //y3
          else if(mySE.getName().getLocalPart().equals("y3"))
          {
            myEvent=myXER.nextEvent();
            myGene.ypoints[2]=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //r
          else if(mySE.getName().getLocalPart().equals("red"))
          {
            myEvent=myXER.nextEvent();
            myGene.r=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //g
          else if(mySE.getName().getLocalPart().equals("green"))
          {
            myEvent=myXER.nextEvent();
            myGene.g=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //b
          else if(mySE.getName().getLocalPart().equals("blue"))
          {
            myEvent=myXER.nextEvent();
            myGene.b=Integer.parseInt(myEvent.asCharacters().getData());
          }
          //a
          else if(mySE.getName().getLocalPart().equals("alpha"))
          {
            myEvent=myXER.nextEvent();
            myGene.a=Integer.parseInt(myEvent.asCharacters().getData());
          }
        }
        if(myEvent.isEndElement())
        {
          EndElement myEE=myEvent.asEndElement();
          if(myEE.getName().getLocalPart().equals("gene"));
          {
            myGenome.geneList.add(myGene);
          }
        }
      }
    } catch (FileNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (XMLStreamException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return myGenome;    
  }
  

}
