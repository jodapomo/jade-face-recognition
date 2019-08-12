/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import ontology.EnviarReconocimiento;
import ontology.FaceRecognitionOntology;
import ontology.Horario;
import ontology.Reconocimiento;
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
                System.out.println("Agente Reconocimiento activo el reconocimiento");
                
                //String idUsuario = reconocerRostro();
                String idUsuario = "1";
                
                ACLMessage mensaje = new ACLMessage();
                AID r = new AID();
                r.setLocalName("Interfaz");
                mensaje.setSender(getAID());
                mensaje.addReceiver(r);
                mensaje.setLanguage(codec.getName());
                mensaje.setOntology(ontologia.getName());
                mensaje.setPerformative(ACLMessage.INFORM);
                Usuario usuario = new Usuario();
                usuario.setNombre(idUsuario);
                usuario.setHorario(new Horario());
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
            ProcessBuilder pb = new ProcessBuilder("python", "reconocimiento.py");
            pb.directory(new File("C:\\Users\\USUARIO\\Documents\\jade-face-recognition\\py"));
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
        
        return idReconocido;
    }
}
