package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: SolicitarEntrada
* @author ontology bean generator
* @version 2019/08/12, 23:38:50
*/
public class SolicitarEntrada implements Predicate {

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
* Protege name: salon
   */
   private Salon salon;
   public void setSalon(Salon value) { 
    this.salon=value;
   }
   public Salon getSalon() {
     return this.salon;
   }

}
