/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import bd.MySql;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ontology.Asignacion;
import ontology.AsignacionCancelada;
import ontology.AsignarSalon;
import ontology.CancelarAsignacion;
import ontology.EnviarRecomendacion;
import ontology.FaceRecognitionOntology;
import ontology.Recomendacion;
import ontology.Salon;
import ontology.SalonAsignado;
import ontology.Usuario;
import ontology.UsuarioLogueado;

/**
 *
 * @author Usuario
 */
public class AgenteGestion extends Agent {

    private final Codec codec = new SLCodec();
    private final Ontology ontologia = FaceRecognitionOntology.getInstance();
    
    
    @Override
    protected void setup() {
        
        MySql.Conectar();
        
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);
        
        this.addBehaviour(new ComportamientoEscucharMensajes());
    }
    
    private class ComportamientoEscucharMensajes extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {

                if (msg.getPerformative() == ACLMessage.INFORM) {
                    try {
                        ContentElement ce = getContentManager().extractContent(msg);
                        if (ce instanceof UsuarioLogueado) {
                            escucharLoginUsuario(ce, msg);
                        }
                    } catch (Codec.CodecException | OntologyException ex) {
                        Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (msg.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        ContentElement ce = getContentManager().extractContent(msg);
                        if (ce instanceof AsignarSalon) {
                            escucharAsignarSalon(ce, msg);
                        } else if (ce instanceof CancelarAsignacion) {
                            escucharCancelarAsignacion(ce, msg);
                        }
                    } catch (Codec.CodecException | OntologyException ex) {
                        Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else {
                    block();
                }
            }
        } 
    }
    
    private void escucharAsignarSalon(ContentElement ce, ACLMessage msg) {
        AsignarSalon enviar = (AsignarSalon) ce;
        asignarSalon(enviar.getCedula(), enviar.getIdAsignacion(), msg);
    }
    
    private void escucharCancelarAsignacion(ContentElement ce, ACLMessage msg) {
        CancelarAsignacion enviar = (CancelarAsignacion) ce;
        cancelarAsignacion(enviar.getIdAsignacion(), msg);
    }
    
    private void asignarSalon(int cedula, int idAsignacion, ACLMessage msg) {
        try {
            ACLMessage reply = msg.createReply();
            reply.setSender(getAID());
            reply.setLanguage(codec.getName());
            reply.setOntology(ontologia.getName());
            reply.setPerformative(ACLMessage.INFORM);
            
            SalonAsignado salonAsignado = new SalonAsignado();
            
            String updateAsignacion = "UPDATE asignacion "
                + "SET usuario = " + cedula 
                + " WHERE id = " + idAsignacion;
            
            MySql.ejecutarUpdate(updateAsignacion);
            
            String asignacionQuery = "SELECT asignacion.id, asignacion.dia, asignacion.hora, salon.bloque, salon.numero, salon.facultad "
                    + "FROM asignacion "
                    + "INNER JOIN salon ON asignacion.salon = salon.id "
                    + "WHERE asignacion.id = " + idAsignacion;
            
            ResultSet asignaciondb = null;
            asignaciondb = MySql.ejecutarQuery(asignacionQuery);
            
            if ( asignaciondb != null ) {
                try {
                    while(asignaciondb.next()) {
                        Salon salon = new Salon();
                        Asignacion asignacion = new Asignacion();
                        
                        asignacion.setId(asignaciondb.getInt(1));
                        asignacion.setDia(asignaciondb.getString(2));
                        asignacion.setHora(asignaciondb.getString(3));
                        
                        salon.setBloque(asignaciondb.getInt(4));
                        salon.setNumero(asignaciondb.getInt(5));
                        salon.setFacultad(asignaciondb.getString(6));
                        
                        asignacion.setSalon(salon);
                        salonAsignado.setAsignacion(asignacion);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            getContentManager().fillContent(reply, salonAsignado);
            send(reply);
        } catch (Codec.CodecException ex) {
            Logger.getLogger(AgenteGestion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OntologyException ex) {
            Logger.getLogger(AgenteGestion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void cancelarAsignacion(int idAsignacion, ACLMessage msg) {
        try {
            ACLMessage reply = msg.createReply();
            reply.setSender(getAID());
            reply.setLanguage(codec.getName());
            reply.setOntology(ontologia.getName());
            reply.setPerformative(ACLMessage.INFORM);
            
            AsignacionCancelada asignacionCancelada = new AsignacionCancelada();
            
            String updateAsignacion = "UPDATE asignacion "
                + "SET usuario = NULL " 
                + "WHERE id = " + idAsignacion;
            
            MySql.ejecutarUpdate(updateAsignacion);
            
            String asignacionQuery = "SELECT asignacion.id, asignacion.dia, asignacion.hora, salon.bloque, salon.numero, salon.facultad "
                + "FROM asignacion "
                + "INNER JOIN salon ON asignacion.salon = salon.id "
                + "WHERE asignacion.id = " + idAsignacion;
            
            ResultSet asignaciondb = null;
            asignaciondb = MySql.ejecutarQuery(asignacionQuery);
            
            if ( asignaciondb != null ) {
                try {
                    while(asignaciondb.next()) {
                        Salon salon = new Salon();
                        Asignacion asignacion = new Asignacion();
                        
                        asignacion.setId(asignaciondb.getInt(1));
                        asignacion.setDia(asignaciondb.getString(2));
                        asignacion.setHora(asignaciondb.getString(3));
                        
                        salon.setBloque(asignaciondb.getInt(4));
                        salon.setNumero(asignaciondb.getInt(5));
                        salon.setFacultad(asignaciondb.getString(6));
                        
                        asignacion.setSalon(salon);
                        asignacionCancelada.setAsignacion(asignacion);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            getContentManager().fillContent(reply, asignacionCancelada);
            send(reply);
        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(AgenteGestion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void escucharLoginUsuario(ContentElement ce, ACLMessage msg) {
        UsuarioLogueado enviar = (UsuarioLogueado) ce;
        Usuario usuario = enviar.getUsuario();
        enviarRecomendacion(usuario, msg);
    }
    
    private void enviarRecomendacion(Usuario usuario, ACLMessage msg) {
        try {
            ACLMessage reply = msg.createReply();
            reply.setSender(getAID());
            reply.setLanguage(codec.getName());
            reply.setOntology(ontologia.getName());
            reply.setPerformative(ACLMessage.INFORM);
            
            Recomendacion recomendacion = new Recomendacion();
            
            ResultSet asignacionesdb = null;
            String getAsignaciones = "SELECT asignacion.id, asignacion.dia, asignacion.hora, salon.bloque, salon.numero, salon.facultad "
                    + "FROM asignacion "
                    + "INNER JOIN salon ON asignacion.salon = salon.id "
                    + "WHERE asignacion.usuario IS NULL";
            
            asignacionesdb = MySql.ejecutarQuery(getAsignaciones);
            
            if ( asignacionesdb != null ) {
                try {
                    while(asignacionesdb.next()) {
                        Salon salon = new Salon();
                        Asignacion asignacion = new Asignacion();
                        
                        asignacion.setId(asignacionesdb.getInt(1));
                        asignacion.setDia(asignacionesdb.getString(2));
                        asignacion.setHora(asignacionesdb.getString(3));
                        
                        salon.setBloque(asignacionesdb.getInt(4));
                        salon.setNumero(asignacionesdb.getInt(5));
                        salon.setFacultad(asignacionesdb.getString(6));
                        
                        asignacion.setSalon(salon);
                        recomendacion.addAsignaciones(asignacion);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            EnviarRecomendacion enviarRecomendacion = new EnviarRecomendacion();
            enviarRecomendacion.setRecomendacion(recomendacion);
            getContentManager().fillContent(reply, enviarRecomendacion);
            send(reply);
        } catch (Codec.CodecException ex) {
            Logger.getLogger(AgenteGestion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OntologyException ex) {
            Logger.getLogger(AgenteGestion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
