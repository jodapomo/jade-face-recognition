package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: AsignarSalon
* @author ontology bean generator
* @version 2019/08/13, 00:20:21
*/
public class AsignarSalon implements Predicate {

   /**
* Protege name: usuario
   */
   private Usuario usuario;
   public void setUsuario(Usuario value) { 
    this.usuario=value;
   }
   public Usuario getUsuario() {
     return this.usuario;
   }

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
