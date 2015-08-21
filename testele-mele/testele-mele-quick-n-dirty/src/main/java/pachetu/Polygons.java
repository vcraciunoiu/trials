package pachetu;

import java.awt.*;
import javax.swing.*;

public class Polygons extends JPanel {
	
	private static final long serialVersionUID = 1L;
	Polygon[] polygons = new Polygon[3];

    public Polygons() {
        Dimension d = getPreferredSize();
        int x1 = d.width/4;
        int y1 = d.height/3;
        int x2 = d.width*3/4;
        int x3 = d.width/2;
        int y3 = d.height*3/4;
        int R = d.width/6;
        int[][] xy = getPolygonArrays(x1, y1, R, 3);
        polygons[0] = new Polygon(xy[0], xy[1], 3);
        xy = getPolygonArrays(x2, y1, R, 5);
        polygons[1] = new Polygon(xy[0], xy[1], 5);
        xy = getPolygonArrays(x3, y3, R, 6);
        polygons[2] = new Polygon(xy[0], xy[1], 6);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.blue);
        for(int j = 0; j < polygons.length; j++) {
            g2.draw(polygons[j]);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(400,400);
    }

    private int[][] getPolygonArrays(int cx, int cy, int R, int sides) {
        int[] x = new int[sides];
        int[] y = new int[sides];
        double thetaInc = 2*Math.PI/sides;
        double theta = (sides % 2 == 0) ? thetaInc : -Math.PI/2;
        for(int j = 0; j < sides; j++) {
            x[j] = (int)(cx + R*Math.cos(theta));
            y[j] = (int)(cy + R*Math.sin(theta));
            theta += thetaInc;
        }
        return new int[][]{ x, y };
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Polygons());
        f.pack();
        f.setLocation(100,100);
        f.setVisible(true);
    }
}
