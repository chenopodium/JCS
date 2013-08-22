package joy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;


/**
 *
 * @author Chantal Roth, 2013
 */
public class Utils {
    
     static final Logger log =  Logger.getLogger("Utils");
     
     public static boolean writeStringToFile(File f, String content, boolean append) {
        PrintWriter fout = null;
        try {
            fout = new PrintWriter(new BufferedWriter(new FileWriter(f, append)));
            fout.print(content);
            fout.flush();
            fout.close();
            return true;
        } catch (FileNotFoundException e) {
            warn("File " + f + " not found: "+e.getMessage());
        } catch (IOException e) {
            warn("IO Exception: "+e.getMessage());
        } finally {
            if (fout != null) {
                fout.flush();
                fout.close();
            }
        }
        return false;
    }
     private static void warn(String msg) {
       log.warning(msg);
    }
    
     
}
