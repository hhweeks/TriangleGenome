import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
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
      //myXSW.writeStartElement(rootElement);
      
      myXSW.writeStartElement("genome");
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
  

}
