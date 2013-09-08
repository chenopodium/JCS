/*
 * Tests Joy Christian's 3-sphere model for the EPR-Bohm correlation
 */
package joy;

import java.util.Random;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;

/**
 *
 * @author Chantal Roth, 2013
 */
public class JoyChristianSimulation {
    
    /* logging */           
    private static final Logger log =  Logger.getLogger("JCSimulation");    
    
    /* whether to pick a and b on a plane or on the sphere */
    private static boolean PICK_AB_VECTORS_ON_SPHERE = false;
    
    /* whether to pick e on a plane or on the sphere - default must be SPHERE! */
    private static boolean PICK_E_VECTORS_ON_SPHERE = true;

    /* maximum angle in degrees that we are considering */
    private static final double MAX_ANGLE = 2*Math.PI;
    
    /* constant phase shifts */
    private static final double phi_op = +0.000;  // in radians
    private static final double phi_oq = +0.000;  // in radians
    private static final double phi_or = -1.517;  // in radians
    private static final double phi_os = +0.663;  // in radians
    
    /* nr experiments per angle */
    private static final int total_nr_experiments = 1000000;
    
    /* results */
    private Results results;
    
    /* utility to generate random values */
    private Random random;
    
    public JoyChristianSimulation() {
        random = new Random();
        results = new Results();
    }

    public void runExperiment() {               
        for (int i = 0; i  < total_nr_experiments; i++) {           
            oneRun( );                            
        }       
        PlotPanel.show(results);        
        results.printResults();
    }

    private void oneRun() {        
        Vector3D a, b, e;
        if (PICK_AB_VECTORS_ON_SPHERE ){
            a = randomVectorOnSphere();            
            b = randomVectorOnSphere();
        }
        else {
            a = new Vector3D(1.0, 0.0, 0.0);
            b = this.randomVectorOnPlane(Math.random()*MAX_ANGLE);            
        }
        if (PICK_E_VECTORS_ON_SPHERE) {
            e = randomVectorOnSphere();  
        }
        else e = this.randomVectorOnPlane(Math.random()*2.0*Math.PI);                        
        
        double eta_ab = a.angle(b);
     
        Vector3d ae = cross(a,e);
        Vector3d be = cross(b,e);
       
        double eta_ae = angle(a,e);
        double eta_be = angle(b,e);            
        double eta_cross = angle(ae,be);
        
        double N_a = Math.sqrt(Math.cos(eta_ae + phi_op) * Math.cos(eta_ae + phi_op) + Math.sin(eta_ae + phi_oq) * Math.sin(eta_ae + phi_oq)); 
        double N_b = Math.sqrt(Math.cos(eta_be + phi_or) * Math.cos(eta_be + phi_or) + Math.sin(eta_be + phi_os) * Math.sin(eta_be + phi_os));
        
        double C_a1 = Math.cos(eta_ae + phi_op)/N_a; // ordinary channel; lambda = +1
        double C_a2 = Math.cos(eta_ae + phi_op + Math.PI)/N_a; // ordinary channel; lambda = -1
        
        double C_b1 = Math.cos(eta_be + phi_or + Math.PI/2)/N_b; // extraordinary channel; lambda = +1
        double C_b2 = Math.cos(eta_be + phi_or + 3*Math.PI/2)/N_b; // extraordinary channel; lambda = -1
        
        double C_ab = (-Math.cos(eta_ae + phi_op) * Math.cos(eta_be + phi_or) + Math.cos(eta_cross) * Math.sin(eta_ae + phi_oq) * Math.sin(eta_be + phi_os))/((N_a)*(N_b));
        
        results.addExperimentCount(eta_ab);
        results.addExperimentCount(Math.PI*2 -eta_ab);
        
//        results.addAEPlus(eta_ab, throwDie(C_a1));
//        results.addAEPlus(eta_ab, throwDie(C_a2));
//        
//        results.addAEMinus(eta_ab, throwDie(C_b1));
//        results.addAEMinus(eta_ab, throwDie(C_b2));
        
        results.addPlusResult(eta_ab, throwDie(C_ab));
        results.addPlusResult(Math.PI*2 -eta_ab, throwDie(C_ab));
        results.addPlusResult(eta_ab, throwDie(C_ab));
        results.addPlusResult(Math.PI*2 -eta_ab, throwDie(C_ab));
    }  
    
    public int throwDie(double C) {
        // probability is between 0 and 1
        double probability = Math.abs(C);
        // generates a random value beween 0 and 1
        double dieValue = random.nextDouble();
        // so if the probability is 0, the die throw will never pass
        // if the probabiity is 1, it will always pass
        // if the probabilty is 0.5, it will pass 50% of the time
        boolean throwPasses = dieValue < probability;
        int detection; // is either 0, 1 or -1
        if (throwPasses) {
            detection = (int)-Math.signum(C);
        }
        else detection = 0;
        return detection;
    }
    private double angle(Vector3d a, Vector3d b) {
         double eta_ab = a.angle(b);         
         return eta_ab;
    }
    
    private Vector3d cross(Vector3d a, Vector3d b) {       
        return new Vector3d(
                a.y*b.z - a.z*b.y, 
                a.z*b.x - a.x*b.z,
                a.x*b.y - a.y*b.x);
    }
   
    private Vector3D randomVectorOnSphere() {
        double x, y, z, length;
        do {
            x = random.nextGaussian();
            y = random.nextGaussian();
            z = random.nextGaussian();            
            length = x * x + y * y + z * z;            
        } while (length <= Double.MIN_NORMAL);
        double s = Math.sqrt(1.0 / length);
        return new Vector3D(x * s, y * s, z * s);
    }
    
    private Vector3D randomVectorOnPlane(double eta_ab) {
        double x = Math.cos(eta_ab);
        double y = Math.sin(eta_ab);        
        return new Vector3D(x, y, 0.0);
    }
    
     public static void main(String[] args) {
        JoyChristianSimulation test = new JoyChristianSimulation();
        test.runExperiment();
    }     
     
     /* logging, mainly for debugging */
    private void log(String msg) {
       log.info(msg);
    }

    public Vector3D randomVectorRelativeTo(Vector3D a) {
        Vector3D b = new Vector3D(a.x, a.y, a.z);
        b.rotateX(Math.random()*2.0*Math.PI);
        b.rotateY(Math.random()*2.0*Math.PI);
        b.rotateZ(Math.random()*2.0*Math.PI);
        return b;
    }

    
}