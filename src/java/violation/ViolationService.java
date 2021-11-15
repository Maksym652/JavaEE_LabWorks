package violation;

import Lab4EJB.ViolationServiceLocal;
import Lab4EJB.ViolationServiceRemote;
import idGenerators.CurrentTime;
import idGenerators.IdGenerator;
import idGenerators.NumInDbAndCurrentTime;
import idGenerators.NumberInDB;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.*;


@Named
@Stateless
public class ViolationService implements ViolationServiceLocal, ViolationServiceRemote{
    @Inject @CurrentTime
    private IdGenerator idGenerator;
   
    @Inject @Added
    private Event<Violation> violationAddedEvent;
    @Inject @Edited
    private Event<Violation> violationEditedEvent;
    
    @Resource ValidatorFactory factory;
    @Resource Validator validator;
    
    public ViolationService(){}
    
    @Override
    public Violation createViolation(String number, String owner, String type, LocalDateTime time, float fine){
        Violation v = new Violation(idGenerator.generate(), number, owner, type, time, fine);
        Set<ConstraintViolation<Violation>> errors = validator.validate(v);
        if(errors.isEmpty()){
            violationAddedEvent.fire(v);
        }
        else{
            for(ConstraintViolation<Violation> error : errors){
                System.out.println(error.getMessage());
            }
        }
        return v;
    }

    @Override
    public Violation createViolation(String ID, String number, String owner, String type, LocalDateTime time, float fine){
        Violation v = new Violation(ID, number, owner, type, time, fine);
         Set<ConstraintViolation<Violation>> errors = validator.validate(v);
        if(errors.isEmpty()){
            violationEditedEvent.fire(v);
        }
        else{
            for(ConstraintViolation<Violation> error : errors){
                System.out.println(error.getMessage());
            }
        };
        return v;
    }
}
