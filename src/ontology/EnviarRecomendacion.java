package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: EnviarRecomendacion
* @author ontology bean generator
* @version 2019/08/12, 20:48:40
*/
public class EnviarRecomendacion implements Predicate {

   /**
* Protege name: recomendacion
   */
   private Recomendacion recomendacion;
   public void setRecomendacion(Recomendacion value) { 
    this.recomendacion=value;
   }
   public Recomendacion getRecomendacion() {
     return this.recomendacion;
   }

}
