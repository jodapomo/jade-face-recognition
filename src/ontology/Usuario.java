package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Usuario
* @author ontology bean generator
* @version 2019/08/13, 11:18:27
*/
public class Usuario implements Concept {

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
* Protege name: nombre
   */
   private String nombre;
   public void setNombre(String value) { 
    this.nombre=value;
   }
   public String getNombre() {
     return this.nombre;
   }

   /**
* Protege name: horario
   */
   private Horario horario;
   public void setHorario(Horario value) { 
    this.horario=value;
   }
   public Horario getHorario() {
     return this.horario;
   }

   /**
* Protege name: rol
   */
   private String rol;
   public void setRol(String value) { 
    this.rol=value;
   }
   public String getRol() {
     return this.rol;
   }

}
