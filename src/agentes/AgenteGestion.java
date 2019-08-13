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
import ontology.EnviarRecomendacion;
import ontology.FaceRecognitionOntology;
import ontology.Recomendacion;
import ontology.Salon;
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
        
        this.addBehaviour(new ComportamientoEscucharLoginUsuario());
    }
    
    private class ComportamientoEscucharLoginUsuario extends CyclicBehaviour {

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
                            UsuarioLogueado enviar = (UsuarioLogueado) ce;
                            Usuario usuario = enviar.getUsuario();
                            enviarRecomendacion(usuario, msg);
                        }
                    } catch (Codec.CodecException | OntologyException ex) {
                        Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    block();
                }
            }
        } 
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
