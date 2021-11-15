/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lab4EJB;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

/**
 *
 * @author 1
 */
@Singleton
@Lock(LockType.WRITE)
@AccessTimeout(value=2, unit=TimeUnit.SECONDS)
public class ErrorWriter {
    private String filepath="D:\\Файли програм\\WebApplication1\\errors.txt";
    private FileWriter fw;
    
    
    public void write(String str) throws IOException{
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        FileWriter fw = new FileWriter(filepath, true);
        fw.write(time+str+"\n");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            
        }
        fw.close();
    }
}
