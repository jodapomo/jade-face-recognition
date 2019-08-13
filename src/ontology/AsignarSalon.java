package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: AsignarSalon
* @author ontology bean generator
* @version 2019/08/13, 00:52:23
*/
public class AsignarSalon implements Predicate {

   /**
* Protege name: cedula
   */
   private int cedula;
   public void setCedula(int value) { 
    this.cedula=value;
   }
   public int getCedula() {
     return this.cedula;
   }

   /**
* Protege name: idAsignacion
   */
   private int idAsignacion;
   public void setIdAsignacion(int value) { 
    this.idAsignacion=value;
   }
   public int getIdAsignacion() {
     return this.idAsignacion;
   }

}
