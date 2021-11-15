/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lab4EJB;

import java.time.LocalDateTime;
import javax.ejb.Remote;
import violation.Violation;

/**
 *
 * @author 1
 */
@Remote
public interface ViolationServiceRemote {
    public Violation createViolation(String number, String owner, String type, LocalDateTime time, float fine);
    public Violation createViolation(String ID, String number, String owner, String type, LocalDateTime time, float fine);
}
