package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: CancelarAsignacion
* @author ontology bean generator
* @version 2019/08/12, 23:38:50
*/
public class CancelarAsignacion implements Predicate {

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
