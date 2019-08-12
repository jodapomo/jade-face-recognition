/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd;

import agentes.AgenteInterfaz;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andres
 */
public class MySql {
    
    
    public static String database = "facerecognition";
    public static String user = "root";
    public static String password = "";
    public static Connection conexion = null;
    
    public static void Conectar(){
        try {
                Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
        
  
                try {
                    conexion = DriverManager.getConnection("jdbc:mysql://localhost/"+database,user, password);
                } catch (SQLException ex) {
                    Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    
    public static void ejecutar(String Query){
        
        
        //String Query = "INSERT INTO usuario VALUES ("+cedula+",'"+nombre+"','"+role+"','"+facultad+"')";
        //System.out.println(Query);
        Statement st = null;
        try {
            st = conexion.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            st.execute(Query);
        } catch (SQLException ex) {
            Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    


    
}
