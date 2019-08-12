package ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Recomendacion
* @author ontology bean generator
* @version 2019/08/12, 00:32:33
*/
public class Recomendacion implements Concept {

   /**
* Protege name: asignaciones
   */
   private List asignaciones = new ArrayList();
   public void addAsignaciones(Asignacion elem) { 
     List oldList = this.asignaciones;
     asignaciones.add(elem);
   }
   public boolean removeAsignaciones(Asignacion elem) {
     List oldList = this.asignaciones;
     boolean result = asignaciones.remove(elem);
     return result;
   }
   public void clearAllAsignaciones() {
     List oldList = this.asignaciones;
     asignaciones.clear();
   }
   public Iterator getAllAsignaciones() {return asignaciones.iterator(); }
   public List getAsignaciones() {return asignaciones; }
   public void setAsignaciones(List l) {asignaciones = l; }

}
