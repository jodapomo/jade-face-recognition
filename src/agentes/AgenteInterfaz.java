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
import jade.util.leap.ArrayList;
import jade.util.leap.List;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import ontology.Asignacion;
import ontology.AsignacionCancelada;
import ontology.AsignarSalon;
import ontology.CancelarAsignacion;
import ontology.EnviarPermisoEntrada;
import ontology.EnviarRecomendacion;
import ontology.EnviarReconocimiento;
import ontology.FaceRecognitionOntology;
import ontology.Horario;
import ontology.Recomendacion;
import ontology.Reconocimiento;
import ontology.SalonAsignado;
import ontology.SolicitarEntrada;
import ontology.Usuario;
import ontology.UsuarioLogueado;


/**
 *
 * @author Usuario
 */
public class AgenteInterfaz extends Agent {
    
    //RUTA JOSE
    //portatil
    //String rutaPython = "C:\\Users\\USUARIO\\Documents\\jade-face-recognition\\py";
    //pc
    String rutaPython = "D:\\Documentos\\jade-face-recognition\\py";
    // RUTA ANDRES
    //String rutaPython = "\\home\\andres\\jade-face-recognition\\py";
    //String rutaPython = "home/andres/jade-face-recognition/py";

    
    FramePrincipal ventanaPrincipal = new FramePrincipal();
    FrameIngresarSalon ventanaIngresarSalon = new FrameIngresarSalon();
    FrameRegistrarUsuario ventanaRegistrarUsuario = new FrameRegistrarUsuario();
    FrameSistema ventanaSistema = new FrameSistema();
    
    private Usuario usuario;
    private Recomendacion recomendacion;
    
    DefaultTableModel dmHorario;
    DefaultTableModel dmRecomendacion;
    
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
                        } else if(ce instanceof SalonAsignado) {
                            manejarAsignacion(ce);
                        } else if(ce instanceof AsignacionCancelada) {
                            manejarCancelacion(ce);
                        } else if(ce instanceof EnviarPermisoEntrada) {
                            manejarPermisoEntrada(ce);
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
            ventanaPrincipal.labelMensaje.setText("<html><div style='text-align: center;'>Usuario reconocido: " + usuario.getNombre() + " Cédula: " + usuario.getCedula() +"</div></html>");
            if ( ventanaSistema.isVisible()) {
                ventanaPrincipal.setVisible(true);
                ventanaSistema.dispose();
            }
            updateIngresarSalonFrame();
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
        ventanaRegistrarUsuario.mensajeLabel.setText("<html><div style='text-align: center;'>Ingrese los datos y luego de click en Registrar Rostro</div></html>");
        
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
                    ventanaRegistrarUsuario.mensajeLabel.setText("<html><div style='text-align: center;'>Llene todos los campos antes de registrar el rostro.</div></html>");  
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
                    ventanaRegistrarUsuario.mensajeLabel.setText("<html><div style='text-align: center;'>Llene todos los campos antes de registrar el rostro.</div></html>");  
                }
            }
        }); 
    }
    
    private void registrarRostro(int id) {
        System.out.println("Registrando rostro...");

        try {           
            ProcessBuilder pb = new ProcessBuilder("python", "capture.py", String.valueOf(id));
            
            pb.directory(new File(rutaPython));

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
        String[] head = {"Id","Fecha", "Hora", "Bloque", "Número", "Facultad", "Acción"};
        
        dmHorario = new DefaultTableModel(data, head);
        fillHorarioDataModel(usuario.getHorario(), dmHorario);
        ventanaSistema.horarioTable.setModel(dmHorario);
        ButtonColumn buttonColumnCancelar = new ButtonColumn(ventanaSistema.horarioTable, cancelar, 6);
        
        if ( recomendacion != null ) {
            dmRecomendacion = new DefaultTableModel(data, head);
            fillRecomendacionDataModel(recomendacion, dmRecomendacion);
            ventanaSistema.recomendacionesTable.setModel(dmRecomendacion);
            ButtonColumn buttonColumnReservar = new ButtonColumn(ventanaSistema.recomendacionesTable, reservar, 6);
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
                    Integer.toString(a.getId()),
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
                    Integer.toString(a.getId()),
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
            Integer idAsignacion = Integer.parseInt((String) table.getValueAt(modelRow, 0));
            solicitarCancelacionAsignacion(idAsignacion);
            ((DefaultTableModel)table.getModel()).removeRow(modelRow);
        }
    };
    
    private void solicitarCancelacionAsignacion(int idAsignacion) {
        try {
            ACLMessage mensaje = new ACLMessage();
            AID r = new AID();
            r.setLocalName("Gestion");
            mensaje.setSender(getAID());
            mensaje.addReceiver(r);
            mensaje.setLanguage(codec.getName());
            mensaje.setOntology(ontologia.getName());
            mensaje.setPerformative(ACLMessage.REQUEST);
            
            CancelarAsignacion cancelarAsignacion = new CancelarAsignacion();
            cancelarAsignacion.setCedula(usuario.getCedula());
            cancelarAsignacion.setIdAsignacion(idAsignacion);
            getContentManager().fillContent(mensaje, cancelarAsignacion);
            send(mensaje);
        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Action reservar = new AbstractAction() {
        public void actionPerformed(ActionEvent e)
        {
            JTable table = (JTable)e.getSource();
            int modelRow = Integer.valueOf( e.getActionCommand() );
            Integer idAsignacion = Integer.parseInt((String) table.getValueAt(modelRow, 0));
            solicitarReservarAsignacion(idAsignacion);
            ((DefaultTableModel)table.getModel()).removeRow(modelRow);
        }
    };
    
    private void solicitarReservarAsignacion(int idAsignacion) {
        try {
            ACLMessage mensaje = new ACLMessage();
            AID r = new AID();
            r.setLocalName("Gestion");
            mensaje.setSender(getAID());
            mensaje.addReceiver(r);
            mensaje.setLanguage(codec.getName());
            mensaje.setOntology(ontologia.getName());
            mensaje.setPerformative(ACLMessage.REQUEST);
            
            AsignarSalon asignarSalon = new AsignarSalon();
            asignarSalon.setCedula(usuario.getCedula());
            asignarSalon.setIdAsignacion(idAsignacion);
            getContentManager().fillContent(mensaje, asignarSalon);
            send(mensaje);
        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void manejarAsignacion(ContentElement ce) {
        SalonAsignado enviar = (SalonAsignado) ce;
        Asignacion asignacion = enviar.getAsignacion();
        
        usuario.getHorario().addAsignaciones(asignacion);
        
        recomendacion.setAsignaciones(removeAsignacion(recomendacion.getAsignaciones(), asignacion.getId()));
        
        String row[] = 
            {
                Integer.toString(asignacion.getId()),
                asignacion.getDia(),
                asignacion.getHora(),
                Integer.toString(asignacion.getSalon().getBloque()),
                Integer.toString(asignacion.getSalon().getNumero()),
                asignacion.getSalon().getFacultad(),
                "Cancelar"
            };
        dmHorario.addRow(row);
    }
    
    private void manejarCancelacion(ContentElement ce) {
        AsignacionCancelada enviar = (AsignacionCancelada) ce;
        Asignacion asignacion = enviar.getAsignacion();
        
        recomendacion.addAsignaciones(asignacion);
        
        usuario.getHorario().setAsignaciones(removeAsignacion(usuario.getHorario().getAsignaciones() , asignacion.getId()));
        
        String row[] = 
            {
                Integer.toString(asignacion.getId()),
                asignacion.getDia(),
                asignacion.getHora(),
                Integer.toString(asignacion.getSalon().getBloque()),
                Integer.toString(asignacion.getSalon().getNumero()),
                asignacion.getSalon().getFacultad(),
                "Reservar"
            };
        dmRecomendacion.addRow(row);
    }
    
    private List removeAsignacion(List asignaciones, int idAsignacion) {
        List newAsignaciones = new ArrayList();
        
        for ( int i = 0; i < asignaciones.size(); i ++ ) {
            if( ((Asignacion)asignaciones.get(i)).getId() != idAsignacion ) {
                newAsignaciones.add(asignaciones.get(i));
            }
        }
        
        return newAsignaciones;
    }
    
    private void updateIngresarSalonFrame() {
        ventanaIngresarSalon.mensajeReconocimientoLabel.setText("<html><div style='text-align: center;'>Usuario reconocido: " + usuario.getNombre() + " Cédula: " + usuario.getCedula() + "</div></html>");
        ventanaIngresarSalon.entrarButton.setEnabled(true);
    }

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
        
        if ( usuario != null ) {
            ventanaIngresarSalon.mensajeReconocimientoLabel.setText("<html><div style='text-align: center;'>Usuario reconocido: " + usuario.getNombre() + " Cédula: " + usuario.getCedula() + "</div></html>");
            ventanaIngresarSalon.entrarButton.setEnabled(true); 
        }
        
        ventanaIngresarSalon.entrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Integer bloque = Integer.parseInt(ventanaIngresarSalon.bloqueInput.getText());
                Integer numero = Integer.parseInt(ventanaIngresarSalon.numeroInput.getText());
                String hora = ventanaIngresarSalon.horaInput.getText();
                String facultad = (String) ventanaIngresarSalon.facultadInput.getSelectedItem();
                String dia = (String) ventanaIngresarSalon.diaInput.getSelectedItem();

                if ( facultad.length() > 0 && dia.length() > 0 && bloque > 0 && numero > 0 && hora.length() > 0 ) {
                    try {
                        ventanaIngresarSalon.mensajeLabel.setText("Solicitando entrada...");
                        
                        ACLMessage mensaje = new ACLMessage();
                        AID r = new AID();
                        r.setLocalName("Gestion");
                        mensaje.setSender(getAID());
                        mensaje.addReceiver(r);
                        mensaje.setLanguage(codec.getName());
                        mensaje.setOntology(ontologia.getName());
                        mensaje.setPerformative(ACLMessage.REQUEST);
                        
                        SolicitarEntrada solicitar = new SolicitarEntrada();
                        
                        solicitar.setCedula(usuario.getCedula());
                        solicitar.setBloque(bloque);
                        solicitar.setDia(dia);
                        solicitar.setFacultad(facultad);
                        solicitar.setHora(hora);
                        solicitar.setNumero(numero);
                        getContentManager().fillContent(mensaje, solicitar);
                        send(mensaje);
                    } catch (Codec.CodecException | OntologyException ex) {
                        ventanaIngresarSalon.mensajeLabel.setText("Error solicitando entrada.");
                        Logger.getLogger(AgenteInterfaz.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    ventanaIngresarSalon.mensajeLabel.setText("<html><div style='text-align: center;'>Llene todos los campos antes de intentar entrar.</div></html>");  
                }
            }
        });
        
    }
    
    private void manejarPermisoEntrada(ContentElement ce) {
        EnviarPermisoEntrada permiso = (EnviarPermisoEntrada) ce;
        
        if ( permiso.getPermitido()  ) {
            ventanaIngresarSalon.mensajeLabel.setText("Entrada permitida.");
            JOptionPane.showMessageDialog(null, "Entrada permitida. Puede ingresar al salón.");
            ventanaIngresarSalon.dispose();
            ventanaPrincipal.setVisible(true);
        } else {
            ventanaIngresarSalon.bloqueInput.setText("");
            ventanaIngresarSalon.numeroInput.setText("");
            ventanaIngresarSalon.horaInput.setText("");
            ventanaIngresarSalon.facultadInput.setSelectedItem(-1);
            ventanaIngresarSalon.diaInput.setSelectedItem(-1);
            ventanaIngresarSalon.mensajeLabel.setText("<html><div style='text-align: center;'>Entrada no permitida para este salón.</div></html>"); 
        }
    }
}
