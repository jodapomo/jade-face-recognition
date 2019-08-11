/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import bd.MySql;
import extra.ButtonColumn;
import interfaz.FrameIngresarSalon;
import interfaz.FramePrincipal;
import interfaz.FrameRegistrarUsuario;
import interfaz.FrameSistema;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import ontology.Asignacion;
import ontology.EnviarReconocimiento;
import ontology.FaceRecognitionOntology;
import ontology.Facultad;
import ontology.Horario;
import ontology.Reconocimiento;
import ontology.Salon;
import ontology.Usuario;


/**
 *
 * @author Usuario
 */
public class AgenteInterfaz extends Agent {
    
    
    FramePrincipal ventanaPrincipal = new FramePrincipal();
    FrameIngresarSalon ventanaIngresarSalon = new FrameIngresarSalon();
    FrameRegistrarUsuario ventanaRegistrarUsuario = new FrameRegistrarUsuario();
    FrameSistema ventanaSistema = new FrameSistema();
    
    private Usuario usuario;
    
    private final Codec codec = new SLCodec();
    private final Ontology ontologia = FaceRecognitionOntology.getInstance();
    
    
    @Override
    protected void setup() {
        System.out.println("Agente Interfaz corriendo");
        
        ventanaPrincipal.ingresarSalonButton.setVisible(false);
        ventanaPrincipal.ingresarSistemaButton.setVisible(false);
        ventanaPrincipal.setVisible(true);
        
        setFrames();
        
        ventanaPrincipal.labelMensaje.setText("Reconociendo...");
        
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);
        
        this.addBehaviour(new ComportamientoEscucharReconocimiento());
    }
    
    private class ComportamientoEscucharReconocimiento extends CyclicBehaviour {

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
                        if (ce instanceof EnviarReconocimiento) {
                            EnviarReconocimiento enviar = (EnviarReconocimiento) ce;
                            Reconocimiento reconocimiento = enviar.getReconocimiento();
                            usuario = reconocimiento.getUsuario();

                            ventanaPrincipal.ingresarSalonButton.setVisible(true);
                            ventanaPrincipal.ingresarSistemaButton.setVisible(true);
                            ventanaPrincipal.labelMensaje.setText("Usuario reconocido: " + usuario.getNombre());
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
    
    private boolean isUsuarioLogged() {
        return usuario != null;
    }
    
    
    ActionListener volverButtonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            volverButtonActionPerformed(evt);
        }
    };
    
    private void setFrames() {
        ventanaPrincipal.registrarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                registrarUsuarioButtonActionPerformed(evt);
            }
        });
        ventanaPrincipal.ingresarSalonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ingresarSalonButtonActionPerformed(evt);
            }
        });  
        ventanaPrincipal.ingresarSistemaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ingresarSistemaButtonActionPerformed(evt);
            }
        });   
        
        ventanaRegistrarUsuario.volverButton.addActionListener(volverButtonActionListener);
        ventanaSistema.volverButton.addActionListener(volverButtonActionListener);
        ventanaIngresarSalon.volverButton.addActionListener(volverButtonActionListener);
        
        setRegistrarUsuarioFrame();
    }

    private void registrarUsuarioButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                       
        ventanaPrincipal.setVisible(false);
        ventanaRegistrarUsuario.setVisible(true);
        ventanaRegistrarUsuario.mensajeLabel.setText("<html>Ingrese los datos y luego<br/>de click en Registrar Rostro</html>");
    }                                                      

    private void ingresarSistemaButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                      
        ventanaPrincipal.setVisible(false);
        ventanaSistema.setVisible(true);
        setSistemaFrame();
    }                                                     

    private void ingresarSalonButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        ventanaPrincipal.setVisible(false);
        ventanaIngresarSalon.setVisible(true);
    }
    
    private void volverButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        ventanaPrincipal.setVisible(true);
        
        ventanaIngresarSalon.setVisible(false);
        ventanaSistema.setVisible(false);
        ventanaRegistrarUsuario.setVisible(false);
    }
    
    private void setRegistrarUsuarioFrame() {
       
        ventanaRegistrarUsuario.registrarRostroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Usuario newUsuario = new Usuario();
                int cedula = Integer.parseInt(ventanaRegistrarUsuario.inputCedula.getText());
                String nombre = ventanaRegistrarUsuario.inputNombre.getText();
                String role = (String) ventanaRegistrarUsuario.rolInput.getSelectedItem();
                String facultad = (String) ventanaRegistrarUsuario.facultadInput.getSelectedItem();
     
                
                String Query = "INSERT INTO usuario VALUES ("+cedula+",'"+nombre+"','"+role+"','"+facultad+"')";
                MySql.Conectar();
                MySql.ejecutar(Query);
                if ( nombre.length() > 0 && role.length() > 0 && facultad.length() > 0 ) {
                    ventanaRegistrarUsuario.mensajeLabel.setText("Registrando rostro...");
                    ventanaRegistrarUsuario.progresoRegistrarRostro.setValue(50);
                    ventanaRegistrarUsuario.registrarRostroButton.setEnabled(false);
                    registrarRostro(cedula);
                } else {
                    ventanaRegistrarUsuario.mensajeLabel.setText("<html>Llene todos los campos antes<br/>de registrar el rostro.</html>");  
                }
            }
        }); 
    }
    
    private void registrarRostro(int id) {
        System.out.println("Registrando rostro...");

        try {           
            ProcessBuilder pb = new ProcessBuilder("python", "capture.py", String.valueOf(id));
            
            //RUTA JOSE
            //pb.directory(new File("G:\\Mi unidad\\UNIVERSIDAD\\Multiagentes\\Proyecto\\FaceRecognition\\py"));
            //RUTA ANDRES
            //pb.directory(new File("\\home\\andres\\jade-face-recognition\\py"));
            pb.directory(new File("home/andres/jade-face-recognition/py"));
            
            
            Process p = pb.start();
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            
            Thread readStdIn = new Thread(new Runnable(){
                String s = null;
                
                @Override
                public void run(){
                    try {
                        while((s = stdInput.readLine()) != null){
                            System.out.println(s);
                        }
                        SwingUtilities.invokeLater(new Runnable(){
                            @Override
                            public void run(){
                                ventanaRegistrarUsuario.mensajeLabel.setText("Rostro registrado correctamente");
                                ventanaRegistrarUsuario.progresoRegistrarRostro.setValue(100);
                            }
                        });
                    } catch (IOException ex) {
                        Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            Thread readStdErr = new Thread(new Runnable(){
                String s = null;
                public void run(){
                    try{
                        while((s = stdError.readLine()) != null){  
                            System.out.println(s);
                        }
                    }
                    catch(IOException ex){
                        Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                    }
                };
            });
            
            readStdIn.start();
            readStdErr.start();

        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
    }
    
    private void setSistemaFrame() {
        
        Facultad facultad = new Facultad();
        facultad.setNombre("Minas");
        
        Salon salon = new Salon();
        salon.setBloque("M8");
        salon.setFacultad(facultad);
        salon.setNumero(201);
        
        Asignacion asignacion = new Asignacion();
        asignacion.setUsuario(usuario);
        asignacion.setFecha("lunes");
        asignacion.setHora("8-10");
        asignacion.setSalon(salon);
        
        Horario horario = usuario.getHorario();
        
        horario.addAsignaciones(asignacion);
        horario.addAsignaciones(asignacion);
        horario.addAsignaciones(asignacion);
        horario.addAsignaciones(asignacion);
        
        Object[][] data = {};
        String[] head = {"Fecha", "Hora", "Bloque", "Número", "Facultad", "Acción"};
        DefaultTableModel dmHorario = new DefaultTableModel(data, head);
        
        fillHorarioDataModel(horario, dmHorario);
        
        ventanaSistema.horarioTable.setModel(dmHorario);
        
        ButtonColumn buttonColumn = new ButtonColumn(ventanaSistema.horarioTable, cancelar, 5);
    }
    
    private void fillHorarioDataModel(Horario horario, DefaultTableModel dm ) {
        
        Object[] asignaciones = horario.getAsignaciones().toArray();
        
        for (Object b : asignaciones) {
            Asignacion a = (Asignacion) b;
            String row[] = 
                {
                    a.getFecha(),
                    a.getHora(),
                    a.getSalon().getBloque(),
                    Integer.toString(a.getSalon().getNumero()),
                    a.getSalon().getFacultad().getNombre(),
                    "Cancelar"
                };
            dm.addRow(row);
        }
    }
    
    Action cancelar = new AbstractAction() {
        public void actionPerformed(ActionEvent e)
        {
            JTable table = (JTable)e.getSource();
            int modelRow = Integer.valueOf( e.getActionCommand() );
            ((DefaultTableModel)table.getModel()).removeRow(modelRow);
        }
    };
 
    
}
