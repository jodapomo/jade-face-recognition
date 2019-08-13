package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: EnviarReconocimiento
* @author ontology bean generator
* @version 2019/08/13, 11:18:27
*/
public class EnviarReconocimiento implements Predicate {

   /**
* Protege name: reconocimiento
   */
   private Reconocimiento reconocimiento;
   public void setReconocimiento(Reconocimiento value) { 
    this.reconocimiento=value;
   }
   public Reconocimiento getReconocimiento() {
     return this.reconocimiento;
   }

}
