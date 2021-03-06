//JTS imports
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import javax.swing.*;
import java.awt.*;
import java.util.*;


class GUIOutput {
    public volatile Renderer renderer = null;
    public synchronized Renderer getRenderer() {
	return this.renderer;
    }
    
    public int width = 640;
    public int height = 480;
    
    public void run() {
	renderer = new Renderer(width, height);
	System.out.println("GUIOuput.renderer:" + renderer.hashCode());
	System.out.println("Hello from a thread!");
	
	JFrame frame = new JFrame("GUI Output");
	frame.setBackground(new Color(0, 0, 0));
	frame.setSize(width, height);
	frame.setVisible(true);
	System.out.println("adding renderer");
	frame.getContentPane().add(renderer);
	//frame.pack();
    }
}



class Renderer extends JPanel
{
    //public synchronized Geometry geometry = null;
    public static  int NumberOfObjects = 50;
    public static int xOffset = 20;
    private volatile LinearRing[] linearRings = new LinearRing[NumberOfObjects];
    private Color[] linearRingColors = new Color[NumberOfObjects];
    private Color defaultColor = new Color(255, 0, 0);
    
    public int width = 640;
    public int height = 480;
    
    public Renderer(int width, int height){
	this.width = width;
	this.height = height;
    }

    public synchronized void setLinearRing(LinearRing lr, int index){
	if(index >= NumberOfObjects){
	    return;
	}
	this.linearRings[index] = lr;
	repaint();
    }

    public synchronized LinearRing getLinearRing(int index) {
	if(index >= NumberOfObjects){
	    return null;
	}
	return linearRings[index];
    }
    public synchronized void setLinearRingColor(Color c, int index){
	if(index >= NumberOfObjects){
	    return;
	}
	this.linearRingColors[index] = c;
	repaint();
    }
    public synchronized void setDefaultColor(Color c){
	this.defaultColor = c;
    }
    
    public void paintComponent(Graphics g)  {
	super.paintComponent(g);
	for(int i = 0; i < NumberOfObjects; i++){	    
	    if(linearRings[i] == null){
		continue;
	    }
	    RenderObject(g, i);
	}
	try {
	    Thread.currentThread().sleep(1000);
	}catch(java.lang.InterruptedException e){
	    
	}
	repaint();
	
    }
    public void RenderObject(Graphics g, int index){
	if(linearRings[index] == null){
	    return;
	}
	g.setColor(defaultColor);
	//g.setColor(new Color(255, 0, 0));
	//g.drawRect(0, 0, 400, 200);
	if(linearRingColors[index] != null){
	    g.setColor(linearRingColors[index]);
	}
	int numPoints = linearRings[index].getNumPoints();
	for(int i = 0; i < numPoints - 1; i++){
	    Point p1 = linearRings[index].getPointN(i);
	    Point p2 = linearRings[index].getPointN(i + 1);
	    int titleBarOffset = -35;
	    //translate and then flip the y-coordinate for the output.
	    g.drawLine((int)p1.getX() + xOffset, -1 * ((int)p1.getY() - height) + titleBarOffset, (int)p2.getX() + xOffset, -1 * ( (int)p2.getY() - height) + titleBarOffset);
	}
	
    }
}







class Runner
{
    public static GeometryFactory geometryFactory = null;
    public static GUIOutput gui = null;
    public static void main(String args[]) throws java.lang.InterruptedException, Exception {
	
	gui = new GUIOutput();
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    gui.run();
		}
 	    });
	while(gui.getRenderer() == null){
	    
	}
 	//gui.run();
 	geometryFactory = new GeometryFactory();
	Util.geometryFactory = geometryFactory;
 	//while (gui.getRenderer() == null){
 	//    int x = 1;
 	renderGeometry();
     }


    public static void fit1() throws Exception {
    	Renderer renderer = gui.getRenderer();
	
    	LinearRing s1 = Util.makeS1(100);
    	Coordinate p1 = new Coordinate(100, 0);
    	Coordinate p2 = new Coordinate(0, 0);
    	Coordinate p3 = new Coordinate(100, 100);
	
    	LinearRing tri1 = Util.makeTriangle(p1, p2, p3);
    	LinearRing tri2 = Util.makeTriangle(new Coordinate(200, 0), new Coordinate(250, 50), new Coordinate(200, 100));
    	LinearRing newS1 = null;
    	Util.PrintShape("s1", s1);

	
    	Polygon poly1 = new Polygon(s1, null, geometryFactory);
    	Geometry poly1epsilon = new Polygon(s1, null, geometryFactory);
    	poly1epsilon = poly1epsilon.buffer(100 * Util.EPSILON).union(poly1epsilon);

    	Polygon poly2 = new Polygon(tri1, null, geometryFactory);
    	System.out.println("p1.covers(p2):" + poly1.covers(poly2));
	
    	ArrayList<LinearRing> shapes = new ArrayList<LinearRing>();
    	shapes.add(tri1);
    	shapes.add(tri2);
    	//System.out.println("Fit:" + Util.Fit(s1, shapes, null));
    	Util.PrintShape("tri1", tri1);	
    	Util.PrintShape("tri2", tri2);
	
    	System.out.println("s1.covers(tri1):" + poly1.covers(new Polygon(tri1, null, geometryFactory)));
    	System.out.println("s1.covers(tri2):" + poly1.covers(new Polygon(tri2, null, geometryFactory)));

    	System.out.println("s1epsilon.covers(tri1):" + poly1epsilon.covers(new Polygon(tri1, null, geometryFactory)));
    	System.out.println("s1epsilon.covers(tri2):" + poly1epsilon.covers(new Polygon(tri2, null, geometryFactory)));
	
    	//Util.PrintShape("newS1", newS1);
    	renderer.setLinearRing(s1, 0);
    	renderer.setLinearRingColor(new Color(0, 0, 0), 0);
    	renderer.setLinearRing(tri1, 1);
    	renderer.setLinearRingColor(new Color(255, 0, 0), 1);
    	renderer.setLinearRing(tri2, 2);
    	renderer.setLinearRingColor(new Color(0, 255, 0), 2);
    
    }
    
    public static void fitTangram() throws Exception {
	Renderer renderer = gui.getRenderer();
	
	LinearRing shape = Util.makeSquare(1.0 * Util.scaleFactor);
	Polygon poly = new Polygon(shape, null, geometryFactory);
	ArrayList<LinearRing> pieces = Util.makeTangramPieces();
	
	renderer.setLinearRing(shape, 0);
	for(int i = 0; i < pieces.size(); i++){
	    renderer.setLinearRing(pieces.get(i), i + 1);
	}
	Util.TotalNumShapes = pieces.size();
	//System.out.println("Fit:" + Util.Fit(shape, pieces, null));
	LinearRing mediumTri = Util.makeMediumTriangle();
	//renderer.setLinearRing(Util.resultShape, 0);
	

    }

    public static void renderGeometry() throws Exception {	
	//fit1();
	fitTangram();
    }


    
    
}






