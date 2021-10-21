import idGenerators.CurrentTime;
import idGenerators.IdGenerator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 1
 */

@Named("violService")
public class ViolationService {
    @Inject @Named("RandNum")
    private IdGenerator idGenerator;

    /*public ViolationService() {
        this.idGenerator = new IdGeneratorByCurrentDateTime();
    }*/
    public Violation createViolation(String number, String owner, String type, LocalDateTime time, float fine){
        return new Violation(idGenerator.generate(), number, owner, type, time, fine);
    }

    @AroundInvoke
    private Object setViolationCount(InvocationContext ic) throws Exception{
        if(ic.getMethod().getName().equals("createViolation")){
            StringBuilder buffer;
            String filePath = new File("").getAbsolutePath();
            filePath = filePath.concat("/web/WEB-INF/violationList.html");
            try (FileReader fr = new FileReader(filePath)) {
            Scanner scan = new Scanner(fr);
            buffer = new StringBuilder();
            String str = "";
            while(scan.hasNextLine()){
                str = scan.nextLine();
                if(str.contains("<tr><th>Всього</th>")){
                    str = str.replace("<tr><th>Всього</th>", "");
                    str = str.replace("</th></tr>", "");
                    int num = Integer.parseInt(str);
                    num++;
                    str = "<tr><th>Всього</th><th>"+num+"</th></tr>\n";
                }
                buffer.append(str).append('\n');
            }
            }
        try(FileWriter fw = new FileWriter(filePath)){
            fw.write(buffer.toString());
            fw.close();
        }
        }
        return ic.proceed();
    }
}
