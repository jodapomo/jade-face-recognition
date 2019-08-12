package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: NuevoUsuario
* @author ontology bean generator
* @version 2019/08/12, 00:32:33
*/
public class NuevoUsuario implements Predicate {

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

}
