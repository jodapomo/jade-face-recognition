package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: SalonAsignado
* @author ontology bean generator
* @version 2019/08/11, 01:43:24
*/
public class SalonAsignado implements Predicate {

   /**
* Protege name: asignacion
   */
   private Asignacion asignacion;
   public void setAsignacion(Asignacion value) { 
    this.asignacion=value;
   }
   public Asignacion getAsignacion() {
     return this.asignacion;
   }

}