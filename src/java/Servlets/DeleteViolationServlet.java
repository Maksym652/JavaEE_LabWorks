package Servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import FirestoreDB.FirestoreDB;
import Lab4EJB.ErrorWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import violation.Violation;
import workWithDB.DAO;
import workWithDB.violation.SQLite;

/**
 *
 * @author 1
 */
@WebServlet(urlPatterns = {"/DeleteViolation"})
public class DeleteViolationServlet extends HttpServlet {
    
    @Inject @FirestoreDB
    DAO<Violation> daoViolation;
    
    @EJB
    ErrorWriter ew;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ID = request.getParameter("ID");
        if(daoViolation.getByID(ID)==null){
             ew.write("Record with ID=''"+ID+"'' not found;");
             response.sendRedirect(request.getContextPath()+"/violationNotExistError");
        }
        else{
            removeViolation(ID);
            response.sendRedirect(request.getContextPath()+"/GetViolations");   
        }
    }
    public void removeViolation(String ID) throws FileNotFoundException, IOException{
        daoViolation.delete(ID);
    }
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
