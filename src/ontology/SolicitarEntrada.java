package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: SolicitarEntrada
* @author ontology bean generator
* @version 2019/08/13, 11:18:27
*/
public class SolicitarEntrada implements Predicate {

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
   private String facultad;
   public void setFacultad(String value) { 
    this.facultad=value;
   }
   public String getFacultad() {
     return this.facultad;
   }

   /**
* Protege name: hora
   */
   private String hora;
   public void setHora(String value) { 
    this.hora=value;
   }
   public String getHora() {
     return this.hora;
   }

   /**
* Protege name: dia
   */
   private String dia;
   public void setDia(String value) { 
    this.dia=value;
   }
   public String getDia() {
     return this.dia;
   }

}
