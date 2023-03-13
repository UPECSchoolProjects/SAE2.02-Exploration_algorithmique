package com.uwu;
package com.uwu.Conversion;
import java.io.File;

public class analyse {
    
    public phrase(test){
        File file = new File(test);
        BufferedReader br = new BufferedReader(new FileReader("test"));
        String st;
        while ((st = br.readLine()) != null);
        return st;
    }
}
