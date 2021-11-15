/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package violation;

import FirestoreDB.FirestoreDB;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import workWithDB.DAO;
import workWithDB.violation.DAOViolationSQLite;
import workWithDB.violation.SQLite;

/**
 *
 * @author User
 */
public class SaveViolationService {
    @Inject @FirestoreDB
    DAO<Violation> daoViolation;
    
    public void addViolation(@Observes @Added Violation v){
        daoViolation.create(v);
    }
    public void editViolation(@Observes @Edited Violation v){
        daoViolation.update(v);
    }
}
