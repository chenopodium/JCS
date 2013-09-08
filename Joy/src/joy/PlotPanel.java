package joy;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * simple class that draws the correlation result from the experiment
 * @author Chantal Roth, 2013
 */
public class PlotPanel extends JPanel {
    
    static final Logger log = Logger.getLogger("PlotPanel");    
    static double MAX_PLOT_ANGLE = 360.0;
    static final int BORDER = 60;  
    
    private Results results;

    public PlotPanel(Results results) {
        this.results = results;
    }

    public static void show(Results results) {
        PlotPanel pan = new PlotPanel(results);
        JFrame f = new JFrame("Classical Simulation of the EPR-Bohm Correlation");
        f.getContentPane().add(pan);
        f.setSize(new Dimension(800, 600));
        f.setVisible(true);
        pan.saveImage("simulationresult.png");
    }

    public BufferedImage getImage(int w, int h) {
        BufferedImage bimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.paintComponent(bimage.createGraphics());
        return bimage;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2);

        
        // clear
        int w = this.getWidth();
        int h = this.getHeight();
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);

        // x from 0 to MAXANGLE
        double dx = (double) (w - 2 * BORDER) / MAX_PLOT_ANGLE;
        double dy = (double) (h - 2 * BORDER)/2;
        int my = (int) h/2;
        g.setColor(Color.black);
        g.drawLine(BORDER, my, w - BORDER, my);
        g.drawLine(BORDER, BORDER, BORDER, h - BORDER);

        g.setFont(new Font("sans serif", Font.PLAIN, 12));
        g.drawString("P = sin^2(eta/2)/2 and P = cos^2(eta/2)/2;   Total counts: " + results.getTotalcount(), BORDER + 20, 20);
        drawCoordinateSystem(g, dx, my, w, dy);
        drawData(g, dx, my, dy);
        drawReferenceCurves(g, dx, my, dy);
    }

    public void saveImage(String file) {
        BufferedImage img = getImage(800, 600);
        File f = new File(file);
        try {
            f.createNewFile();
            boolean ok = ImageIO.write(img, "png", f);
            if (!ok) {
                log("Faile to write to image " + f.getAbsolutePath());
            }
        } catch (Exception ex) {
            log("Unable to save image to " + f+": "+ex.getMessage());
        }
    }

    public void drawData(Graphics g, double dx, int my, double dy) {
        Color[] colors = {
            Color.blue, Color.red, Color.green.darker(), Color.gray.darker()
        };
        //g2.setStroke(new BasicStroke(2));
        double yscale = 1.0; // in case we want to scale the y axis 
        for (int which = 0; which < 4; which++) {
            g.setColor(colors[which]);
            g.drawString(Results.DESCRIPTIONS[which], BORDER + 20, which * 16 + 40);
            double xold = 0;
            for (double x = 0; x <= MAX_PLOT_ANGLE; x += 2.0) {
                double y = results.getProbability(x, which) * yscale;
                double yold = results.getProbability(xold, which) * yscale;
                int guix = (int) (x * dx) + BORDER;
                int guiy = (int) (my - y * dy);
                int guixold = (int) (xold * dx) + BORDER;
                int guiyold = (int) (my - yold * dy);
                g.drawLine(guix, guiy, guixold, guiyold);
                xold = x;
            }
        }
    }

    public void drawReferenceCurves(Graphics g, double dx, int my, double dy) {
        double xold = 0;
        for (double x = 0; x <= MAX_PLOT_ANGLE; x += 1.0) {
            g.setColor(Color.black);
            double rad = Math.toRadians(x);
            double y = - Math.cos(rad);
            double radold = Math.toRadians(xold);
            double yold = -Math.cos(radold);
            int guix = (int) (x * dx) + BORDER;
            int guiy = (int) (my - y * dy);
            int guixold = (int) (xold * dx) + BORDER;
            int guiyold = (int) (my - yold * dy);
            g.drawLine(guix, guiy, guixold, guiyold);
            xold = x;
        }

//        xold = 0;
//        for (double x = 0; x <= MAX_PLOT_ANGLE; x += 1.0) {
//            g.setColor(Color.black);
//            double rad = Math.toRadians(x);
//            double y = Math.pow(Math.cos(rad / 2.0), 2) / 2.0;
//            double radold = Math.toRadians(xold);
//            double yold = Math.pow(Math.cos(radold / 2.0), 2) / 2.0;
//            int guix = (int) (x * dx) + BORDER;
//            int guiy = (int) (my - y * dy);
//            int guixold = (int) (xold * dx) + BORDER;
//            int guiyold = (int) (my - yold * dy);
//            g.drawLine(guix, guiy, guixold, guiyold);
//            xold = x;
//        }
    }

    public void drawCoordinateSystem(Graphics g, double dx, int my, int w, double dy) {
        DecimalFormat f = new DecimalFormat("0.0");
        // draw x axis
        g.setColor(Color.black);
        for (double x = 0; x <= MAX_PLOT_ANGLE; x += 5.0) {
            int guix = (int) (x * dx) + BORDER;
            int guiy = (int) (my + 15);
            if (x % 5 == 0) {
                g.drawLine(guix, my - 3, guix, my + 3);
            }
            if (x % 45 == 0) {
                g.drawString("" + f.format(x), guix - 10, guiy);
            }
        }
        g.drawString("Angle eta_ab", w / 2 - BORDER, my + 30);
        // draw y axis      
        for (double y = 0-1.0; y <= 1.0; y += 0.1) {
            int guix = BORDER - 20;
            int guiy = (int) (my - y * dy);
            g.drawLine(guix + 10, guiy, BORDER, guiy);
            g.drawString("" + f.format(y), guix - 10, guiy + 5);
        }
    }

    private void log(String msg) {
        log.info(msg);
    }
}
