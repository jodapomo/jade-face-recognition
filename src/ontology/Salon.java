package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Salon
* @author ontology bean generator
* @version 2019/08/12, 00:32:33
*/
public class Salon implements Concept {

   /**
* Protege name: numero
   */
   private int numero;
   public void setNumero(int value) { 
    this.numero=value;
   }
   public int getNumero() {
     return this.numero;
   }

   /**
* Protege name: bloque
   */
   private int bloque;
   public void setBloque(int value) { 
    this.bloque=value;
   }
   public int getBloque() {
     return this.bloque;
   }

   /**
* Protege name: facultad
   */
   private Facultad facultad;
   public void setFacultad(Facultad value) { 
    this.facultad=value;
   }
   public Facultad getFacultad() {
     return this.facultad;
   }

   /**
* Protege name: id
   */
   private int id;
   public void setId(int value) { 
    this.id=value;
   }
   public int getId() {
     return this.id;
   }

}
