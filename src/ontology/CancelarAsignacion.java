package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: CancelarAsignacion
* @author ontology bean generator
* @version 2019/08/13, 11:18:27
*/
public class CancelarAsignacion implements Predicate {

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
