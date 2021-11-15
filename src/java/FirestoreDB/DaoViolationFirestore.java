/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FirestoreDB;

import violation.Violation;
import workWithDB.DAO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

/**
 *
 * @author 1
 */
@FirestoreDB
@Stateless
public class DaoViolationFirestore implements DAO<Violation>{
    
    @Inject
    Firestore db;
    
    @Resource(name = "exchangeRateInUAH")
    private Float exchangeRate;
    
    @Resource
    private TimerService timerService;
    
    @Override
    public boolean create(Violation entity) {
        DocumentReference docRef = db.collection("violations").document(entity.getID());
        Map<String, Object> data = new HashMap<>();
        data.put("carNumber", entity.getCarNum());
        data.put("ownerName", entity.getOwnerName());
        data.put("violationType", entity.getViolationType());
        data.put("dateTime", entity.getDateTimeAsString());
        data.put("fine", entity.getFine()*exchangeRate);
        ApiFuture<WriteResult> result = docRef.set(data);
        
        ScheduleExpression afterYear = new ScheduleExpression()
                .year(entity.getDateTime().getYear()+1)
                .month(entity.getDateTime().getMonthValue())
                .dayOfMonth(entity.getDateTime().getDayOfMonth())
                .hour(entity.getDateTime().getHour())
                .minute(entity.getDateTime().getMinute());
        timerService.createCalendarTimer(afterYear, new TimerConfig(entity.getID(), true));
        
        return result.isDone();
    }

    @Override
    public boolean update(Violation entity) {
        DocumentReference docRef = db.collection("violations").document(entity.getID());
        Map<String, Object> data = new HashMap<>();
        data.put("carNumber", entity.getCarNum());
        data.put("ownerName", entity.getOwnerName());
        data.put("violationType", entity.getViolationType());
        data.put("dateTime", entity.getDateTimeAsString());
        data.put("fine", entity.getFine()*exchangeRate);
        ApiFuture<WriteResult> result = docRef.set(data);
        return result.isDone();
    }

    @Override
    public boolean delete(String id) {
        DocumentReference docRef = db.collection("violations").document(id);
        ApiFuture<WriteResult> result = docRef.delete();
        return result.isDone();
    }

    @Override
    public List<Violation> getAll(){
        List<Violation> violations = new ArrayList<Violation>();
        ApiFuture<QuerySnapshot> query = db.collection("violations").get();
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();
        } catch (ExecutionException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        Violation v;
        for (QueryDocumentSnapshot document : documents) {
            v=new Violation(
                    document.getId(),
                    document.getString("carNumber"),
                    document.getString("ownerName"),
                    document.getString("violationType"),
                    LocalDateTime.parse(document.getString("dateTime"), Violation.violDateTimeFormatter),
                    document.get("fine", float.class)/exchangeRate
            );
            violations.add(v);
        }
        return violations;
    }

    @Override
    public Violation getByID(String ID) {
        if(getAll().stream().filter(x -> x.getID().equals(ID)).toArray().length==0){
            return null;
        }
        return (Violation)getAll().stream().filter(x -> x.getID().equals(ID)).toArray()[0];
    } 
    
    @Schedule(hour="*", persistent = false)
    public void writeStatistics(){
        List<Violation> violations = getAll();
        Map<String, Integer> typeCounts = new HashMap<String, Integer>() {};
        typeCounts.put("all", violations.size());
        for(Violation v : violations){
            if(typeCounts.containsKey(v.getViolationType())){
                typeCounts.put(v.getViolationType(), typeCounts.get(v.getViolationType())+1 );
            }
            else{
                typeCounts.put(v.getViolationType(), 1);
            }
        }
        DocumentReference docRef = db.collection("statistics").document("byTypes");
        ApiFuture<WriteResult> result = docRef.set(typeCounts);
    }
    
    @Timeout
    public void deleteAfterYear(Timer timer){
        String id = (String)timer.getInfo();
        DocumentReference docRef = db.collection("violations").document(id);
        ApiFuture<WriteResult> result = docRef.delete();
    }
}
