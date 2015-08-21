package pachetu;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

public class Polygonul extends JPanel {

	private static final long serialVersionUID = 1L;
	Polygon pentagon;
	Polygon rhombus;
	Ellipse2D.Double outerCircle;
	Ellipse2D.Double innerCircle;

	public Polygonul() {

		int x = 573;
		int y = 345;
		int width = 40;
		int height = 40;
		int R = width / 5;
		int diameter = width / 2;
		
		// deseneaza rombul
		rhombus = new Polygon();
		rhombus.addPoint(x, y + (height / 2));
		rhombus.addPoint(x + (width / 2), y + height);
		rhombus.addPoint(x + width, y + (height / 2));
		rhombus.addPoint(x + (width / 2), y);
		// g.draw(rhombus);

	    outerCircle = new Ellipse2D.Double(((width - diameter) / 2) + x-2, ((height - diameter) / 2-2) + y, diameter+4, diameter+4);
	    innerCircle = new Ellipse2D.Double(((width - diameter) / 2) + x, ((height - diameter) / 2) + y, diameter, diameter);

		int[][] xy = getPolygonArrays(width/2+x, height/2+y, R, 5);
		pentagon = new Polygon(xy[0], xy[1], 5);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.blue);
		
	    Stroke orginalStroke = g2.getStroke();
	    g2.setStroke(new BasicStroke(2.0f));
		g2.draw(pentagon);
		g2.setStroke(orginalStroke);
		
		g2.draw(rhombus);
		g2.draw(outerCircle);
	    g2.draw(innerCircle);
	}

	private int[][] getPolygonArrays(int cx, int cy, int R, int sides) {
		int[] x = new int[sides];
		int[] y = new int[sides];
		double thetaInc = 2 * Math.PI / sides;
		double theta = (sides % 2 == 0) ? thetaInc : -Math.PI / 2;
		for (int j = 0; j < sides; j++) {
			x[j] = (int) (cx + R * Math.cos(theta));
			y[j] = (int) (cy + R * Math.sin(theta));
			theta += thetaInc;
		}
		return new int[][] { x, y };
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new Polygonul());
		f.pack();
		f.setLocation(100, 100);
		f.setVisible(true);
	}
}
