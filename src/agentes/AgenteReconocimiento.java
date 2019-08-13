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
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import ontology.Asignacion;
import ontology.EnviarReconocimiento;
import ontology.FaceRecognitionOntology;
import ontology.Horario;
import ontology.Reconocimiento;
import ontology.Salon;
import ontology.Usuario;

/**
 *
 * @author Usuario
 */
public class AgenteReconocimiento extends Agent {
    
    private final Codec codec = new SLCodec();
    private final Ontology ontologia = FaceRecognitionOntology.getInstance();
    
    @Override
    protected void setup(){
        MySql.Conectar();
        System.out.println("Agente Reconocimiento corriendo");
        
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);
        
        addBehaviour(new ComportamientoEnviarReconocimiento(this, 4000));
    }
    
    
    private class ComportamientoEnviarReconocimiento extends TickerBehaviour {

        public ComportamientoEnviarReconocimiento(Agent a, long intervalo){
            super(a, intervalo);
        }



        @Override
        protected void onTick() {
            try {
                System.out.println("Agente Reconocimiento activó el reconocimiento");
                
                String cedula = reconocerRostro();
                Usuario usuario = getUsuarioDB(cedula);
                
                ACLMessage mensaje = new ACLMessage();
                AID r = new AID();
                r.setLocalName("Interfaz");
                mensaje.setSender(getAID());
                mensaje.addReceiver(r);
                mensaje.setLanguage(codec.getName());
                mensaje.setOntology(ontologia.getName());
                mensaje.setPerformative(ACLMessage.INFORM);

                Reconocimiento reconocimiento = new Reconocimiento();
                reconocimiento.setUsuario(usuario);
                EnviarReconocimiento enviarReconocimiento = new EnviarReconocimiento();
                enviarReconocimiento.setReconocimiento(reconocimiento);
                getContentManager().fillContent(mensaje, enviarReconocimiento);
                send(mensaje);
            } catch (Codec.CodecException | OntologyException ex) {
                Logger.getLogger(AgenteReconocimiento.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String reconocerRostro() {
        System.out.println("Reconociendo...");
        String s = null;
        
        String idReconocido = null;

        try {           
            ProcessBuilder pb = new ProcessBuilder("python", "reconocimiento2.py");
//            PORTATIL
//            pb.directory(new File("C:\\Users\\USUARIO\\Documents\\jade-face-recognition\\py"));
//            PC
            pb.directory(new File("D:\\Documentos\\jade-face-recognition\\py"));
            Process p = pb.start();
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            
            while ((s = stdInput.readLine()) != null) {
                if ( s.contains("reconocido")) {
                    String[] split = s.split("=");
                    idReconocido = split[1];
                }
                System.out.println(s);
            }
            
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
            
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
        System.out.println("Reconocido");
        return idReconocido;
    }
    
    private Usuario getUsuarioDB(String cedula) {
        Usuario usuario = new Usuario();
        Horario horario = new Horario();
        
        ResultSet usuariodb = null;
        String getUsuario = "SELECT * from usuario WHERE cedula = "+ cedula;
        usuariodb = MySql.ejecutarQuery(getUsuario);
        
        
        if ( usuariodb != null ) {
            try {
                while(usuariodb.next()) {
                    usuario.setCedula(usuariodb.getInt(1));
                    usuario.setNombre(usuariodb.getString(2));
                    usuario.setRol(usuariodb.getString(3));
                    usuario.setFacultad(usuariodb.getString(4));
                }
            } catch (SQLException ex) {
                Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ResultSet asignacionesdb = null;
            String getAsignaciones = "SELECT asignacion.id, asignacion.dia, asignacion.hora, salon.bloque, salon.numero, salon.facultad "
                    + "FROM asignacion "
                    + "INNER JOIN salon ON asignacion.salon = salon.id "
                    + "WHERE asignacion.usuario = " + cedula;
            
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
                        horario.addAsignaciones(asignacion);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        usuario.setHorario(horario);
        return usuario;
    }
}
