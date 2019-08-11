package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Asignacion
* @author ontology bean generator
* @version 2019/08/11, 01:43:24
*/
public class Asignacion implements Concept {

   /**
* Protege name: fecha
   */
   private String fecha;
   public void setFecha(String value) { 
    this.fecha=value;
   }
   public String getFecha() {
     return this.fecha;
   }

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
* Protege name: id
   */
   private int id;
   public void setId(int value) { 
    this.id=value;
   }
   public int getId() {
     return this.id;
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
