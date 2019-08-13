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
import jade.core.AID;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import ontology.Asignacion;
import ontology.EnviarRecomendacion;
import ontology.EnviarReconocimiento;
import ontology.FaceRecognitionOntology;
import ontology.Horario;
import ontology.Recomendacion;
import ontology.Reconocimiento;
import ontology.Usuario;
import ontology.UsuarioLogueado;


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
    private Recomendacion recomendacion;
    
    private final Codec codec = new SLCodec();
    private final Ontology ontologia = FaceRecognitionOntology.getInstance();
    
    
    @Override
    protected void setup() {
        
        MySql.Conectar();
        
        System.out.println("Agente Interfaz corriendo");
        
        ventanaPrincipal.ingresarSistemaButton.setVisible(false);
        ventanaPrincipal.setVisible(true);
        
        setFramePrincipal();
        
        ventanaPrincipal.labelMensaje.setText("Reconociendo...");
        
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
                        
                        if (ce instanceof EnviarReconocimiento) {
                            escucharReconocimiento(ce);
                        } else if(ce instanceof EnviarRecomendacion) {
                            escucharRecomendacion(ce);
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
    
    
    private void escucharReconocimiento(ContentElement ce) {
        EnviarReconocimiento enviar = (EnviarReconocimiento) ce;
        Reconocimiento reconocimiento = enviar.getReconocimiento();

        if ( ( usuario == null ) || ( usuario.getCedula() != reconocimiento.getUsuario().getCedula() ) ) {
            usuario = reconocimiento.getUsuario();
            System.out.println(usuario.getNombre() + " se logueo.");
            ventanaPrincipal.ingresarSistemaButton.setVisible(true);
            ventanaPrincipal.labelMensaje.setText("<html>Usuario reconocido: " + usuario.getNombre() + " Cédula: " + usuario.getCedula() +"</html>");
            if ( ventanaSistema.isVisible()) {
                ventanaPrincipal.setVisible(true);
                ventanaSistema.dispose();
            }
            enviarNotificacionLogin();
        } else {
            System.out.println("Mismo usuario logueado.");
        }
    }
    
    private void escucharRecomendacion(ContentElement ce) {
        EnviarRecomendacion enviar = (EnviarRecomendacion) ce;
        recomendacion = enviar.getRecomendacion();
    }
    
    private void enviarNotificacionLogin() {
        try {
            ACLMessage mensaje = new ACLMessage();
            AID r = new AID();
            r.setLocalName("Gestion");
            mensaje.setSender(getAID());
            mensaje.addReceiver(r);
            mensaje.setLanguage(codec.getName());
            mensaje.setOntology(ontologia.getName());
            mensaje.setPerformative(ACLMessage.INFORM);
            
            UsuarioLogueado usuarioLogueado = new UsuarioLogueado();
            usuarioLogueado.setUsuario(usuario);
            getContentManager().fillContent(mensaje, usuarioLogueado);
            send(mensaje);
        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    ActionListener volverButtonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            volverButtonActionPerformed(evt);
        }
    };
    
    private void setFramePrincipal() {
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
    }

    private void registrarUsuarioButtonActionPerformed(java.awt.event.ActionEvent evt) {  
        // close main window
        ventanaPrincipal.dispose();
        
        // restart window
        ventanaRegistrarUsuario.getContentPane().removeAll();
        ventanaRegistrarUsuario.myInitComponents();
        
        // set window
        ventanaRegistrarUsuario.setVisible(true);
        setRegistrarUsuarioFrame();
        
    }                                                      

    private void ingresarSistemaButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                      
        // close main window
        ventanaPrincipal.dispose();
        
        // restart window
        ventanaSistema.getContentPane().removeAll();
        ventanaSistema.myInitComponents();
        
        // set window
        ventanaSistema.setVisible(true);
        setSistemaFrame();
    }                                                     

    private void ingresarSalonButtonActionPerformed(java.awt.event.ActionEvent evt) {      
        // close main window
        ventanaPrincipal.dispose();
        
        // restart window
        ventanaIngresarSalon.getContentPane().removeAll();
        ventanaIngresarSalon.myInitComponents();
        
        // set window
        ventanaIngresarSalon.setVisible(true);
        setIngresarSalonFrame();
    }
    
    private void volverButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        ventanaPrincipal.setVisible(true);
        
        ventanaIngresarSalon.dispose();
        ventanaSistema.dispose();
        ventanaRegistrarUsuario.dispose();
    }
    
    private void setRegistrarUsuarioFrame() {
        
        ResultSet facultades = null;
        
        ventanaRegistrarUsuario.volverButton.addActionListener(volverButtonActionListener);
        ventanaRegistrarUsuario.mensajeLabel.setText("<html>Ingrese los datos y luego de click en Registrar Rostro</html>");
        
        String getFacultades = "SELECT nombre from facultad";
        facultades = MySql.ejecutarQuery(getFacultades);
        final DefaultComboBoxModel modelFacultades = new DefaultComboBoxModel();
        ventanaRegistrarUsuario.facultadInput.setModel(modelFacultades);
        
        if ( facultades != null ) {
            try {
                while(facultades.next()) {
                    modelFacultades.addElement(facultades.getString(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
       
        ventanaRegistrarUsuario.registrarRostroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Usuario newUsuario = new Usuario();
                int cedula = Integer.parseInt(ventanaRegistrarUsuario.inputCedula.getText());
                String nombre = ventanaRegistrarUsuario.inputNombre.getText();
                String role = (String) ventanaRegistrarUsuario.rolInput.getSelectedItem();
                String facultad = (String) ventanaRegistrarUsuario.facultadInput.getSelectedItem();
     
                if ( nombre.length() > 0 && role.length() > 0 && facultad.length() > 0 ) {
                    ventanaRegistrarUsuario.mensajeLabel.setText("Registrando rostro...");
                    ventanaRegistrarUsuario.progresoRegistrarRostro.setValue(50);
                    ventanaRegistrarUsuario.registrarRostroButton.setEnabled(false);
                    registrarRostro(cedula);
                } else {
                    ventanaRegistrarUsuario.mensajeLabel.setText("<html>Llene todos los campos antes de registrar el rostro.</html>");  
                }
            }
        }); 
        
        ventanaRegistrarUsuario.registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Usuario newUsuario = new Usuario();
                int cedula = Integer.parseInt(ventanaRegistrarUsuario.inputCedula.getText());
                String nombre = ventanaRegistrarUsuario.inputNombre.getText();
                String role = (String) ventanaRegistrarUsuario.rolInput.getSelectedItem();
                String facultad = (String) ventanaRegistrarUsuario.facultadInput.getSelectedItem();
     
                
                
                if ( nombre.length() > 0 && role.length() > 0 && facultad.length() > 0 ) {
                    ventanaRegistrarUsuario.mensajeLabel.setText("Registrando usuario...");
                    String insertarUsuarioQuery = "INSERT INTO usuario VALUES ("+cedula+",'"+nombre+"','"+role+"','"+facultad+"')";
                    
                    MySql.ejecutarUpdate(insertarUsuarioQuery);
                    ventanaRegistrarUsuario.dispose();
                    ventanaPrincipal.setVisible(true);
                } else {
                    ventanaRegistrarUsuario.mensajeLabel.setText("<html>Llene todos los campos antes de registrar el rostro.</html>");  
                }
            }
        }); 
    }
    
    private void registrarRostro(int id) {
        System.out.println("Registrando rostro...");

        try {           
            ProcessBuilder pb = new ProcessBuilder("python", "capture.py", String.valueOf(id));
            
            //RUTA JOSE
            pb.directory(new File("C:\\Users\\USUARIO\\Documents\\jade-face-recognition\\py"));
            //RUTA ANDRES
            //pb.directory(new File("\\home\\andres\\jade-face-recognition\\py"));
            //pb.directory(new File("home/andres/jade-face-recognition/py"));
            
            
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
                                ventanaRegistrarUsuario.registrarButton.setEnabled(true);
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
        
        ventanaSistema.volverButton.addActionListener(volverButtonActionListener);

        Object[][] data = {};
        String[] head = {"Fecha", "Hora", "Bloque", "Número", "Facultad", "Acción"};
        
        DefaultTableModel dmHorario = new DefaultTableModel(data, head);
        fillHorarioDataModel(usuario.getHorario(), dmHorario);
        ventanaSistema.horarioTable.setModel(dmHorario);
        ButtonColumn buttonColumnCancelar = new ButtonColumn(ventanaSistema.horarioTable, cancelar, 5);
        
        if ( recomendacion != null ) {
            DefaultTableModel dmRecomendacion = new DefaultTableModel(data, head);
            fillRecomendacionDataModel(recomendacion, dmRecomendacion);
            ventanaSistema.recomendacionesTable.setModel(dmRecomendacion);
            ButtonColumn buttonColumnReservar = new ButtonColumn(ventanaSistema.recomendacionesTable, cancelar, 5);
        }
        
        ventanaSistema.nombreLabel.setText(usuario.getNombre());
        ventanaSistema.cedulaLabel.setText(Integer.toString(usuario.getCedula()));
        ventanaSistema.rolLabel.setText(usuario.getRol());
        ventanaSistema.facultadLabel.setText(usuario.getFacultad());
    }
    
    private void fillHorarioDataModel(Horario horario, DefaultTableModel dm ) {
        
        Object[] asignaciones = horario.getAsignaciones().toArray();
        
        for (Object b : asignaciones) {
            Asignacion a = (Asignacion) b;
            String row[] = 
                {
                    a.getDia(),
                    a.getHora(),
                    Integer.toString(a.getSalon().getBloque()),
                    Integer.toString(a.getSalon().getNumero()),
                    a.getSalon().getFacultad(),
                    "Cancelar"
                };
            dm.addRow(row);
        }
    }
    
    private void fillRecomendacionDataModel(Recomendacion recomendacion, DefaultTableModel dm ) {
        
        Object[] asignaciones = recomendacion.getAsignaciones().toArray();
        
        for (Object b : asignaciones) {
            Asignacion a = (Asignacion) b;
            String row[] = 
                {
                    a.getDia(),
                    a.getHora(),
                    Integer.toString(a.getSalon().getBloque()),
                    Integer.toString(a.getSalon().getNumero()),
                    a.getSalon().getFacultad(),
                    "Reservar"
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
    
    private void setIngresarSalonFrame() {
        ventanaIngresarSalon.volverButton.addActionListener(volverButtonActionListener);
        
        ResultSet facultades = null;
        String getFacultades = "SELECT nombre from facultad";
        facultades = MySql.ejecutarQuery(getFacultades);
        final DefaultComboBoxModel modelFacultades = new DefaultComboBoxModel();
        ventanaIngresarSalon.facultadInput.setModel(modelFacultades);
        
        if ( facultades != null ) {
            try {
                while(facultades.next()) {
                    modelFacultades.addElement(facultades.getString(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }   
}
