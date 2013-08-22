package joy;

import java.io.File;
import java.util.logging.Logger;

/**
 *
 * @author Chantal Roth, 2013
 */
public class Results {
    static Logger log =  Logger.getLogger("Results");
    
    private static double MAX_ANGLE = 360.0;
    private static int PLUS_TYPE = 0;
    private static int MINUS_TYPE = 1;   
    private static int AEPLUS_TYPE = 2;
    private static int AENEG_TYPE = 3;
     private static int NEITHER_TYPE = 4;
    private static int TOTAL_TYPE = 5;
    
    private double[][] databuckets;
    
    private double deltaAngle = 1.0;
    private int buckets = (int) (MAX_ANGLE/deltaAngle);       
    private int totalcount;
         
    public static String DESCRIPTIONS[] =  {
        "+1 counts for C, both channels",
        "-1 counts for C, both channels",        
        "+1 counts for C, single channel",
        "-1 counts for C, single channel",
        "neither +1 nor -1",
        "Total number"
    };    
    
    public Results() {
        databuckets = new double [buckets+1][TOTAL_TYPE+1];
        totalcount = 0;
    }
    
    public void addAEPlus(double angleRad, double value) { 
        double angleDeg = Math.toDegrees(angleRad);
        int bucket = getBucket(angleDeg);            
        if (value > 0) databuckets[bucket][AEPLUS_TYPE]+=value;        
    }
     public void addAEMinus(double angleRad, double value) { 
        double angleDeg = Math.toDegrees(angleRad);
        int bucket = getBucket(angleDeg);            
        if (value < 0) databuckets[bucket][AENEG_TYPE]+=-value;        
    }
     
    public void addPlusResult(double angleRad, double value) {
        add(angleRad, value, true);        
    } 
    public void addMinusResult(double angleRad, double value) {
        add(angleRad, value, false);        
    } 
    public void addExperimentCount(double angleRad) {
        double angleDeg = Math.toDegrees(angleRad);
        int bucket = getBucket(angleDeg);   
        databuckets[bucket][TOTAL_TYPE]++;
        totalcount++;
    }
    private void add(double angleRad, double value, boolean plus) { 
        double angleDeg = Math.toDegrees(angleRad);
        int bucket = getBucket(angleDeg);            
        if (Math.abs(value) == 1) {
            if (value > 0) {
                if (plus) databuckets[bucket][PLUS_TYPE]++;
           }
            else {
                if (!plus) databuckets[bucket][MINUS_TYPE]++;
            }            
        }              
    }
      /** Convert angle in degrees to bucket nr */
    public int getBucket(double angleDeg) {                          
        int bucket = (int) (Math.round(Math.abs(angleDeg) / deltaAngle));        
        return bucket;
    }
   
    public double getProbability(double angleDeg, int which) {
        int bucket = getBucket(angleDeg);
        double totalnr = ((double)(databuckets[bucket][TOTAL_TYPE]));
        double prob = (double)databuckets[bucket][which]/totalnr;      
        return prob;
    }
    private void log(String msg) {
       log.info(msg);
    }
   
    public int getNrBuckets() {
        return buckets;
    }
    public int getTotalcount() {
        return totalcount;
    } 
   
    public void printResults() {
        log("Total count: "+totalcount);
        
        String s= "Total count, "+totalcount+"\n;"
                + "angle degrees, plus counts, neg counts, neither, total\n";
        for (int a = 0; a < MAX_ANGLE; a++ ) {
            int b = getBucket(a);
            s +=a+", "+databuckets[b][PLUS_TYPE]+", "+databuckets[b][MINUS_TYPE]
                    +", "+databuckets[b][NEITHER_TYPE]+", "+databuckets[b][TOTAL_TYPE]+"\n";
        }    
        Utils.writeStringToFile(new File("results.csv"), s, false);
    }
}
