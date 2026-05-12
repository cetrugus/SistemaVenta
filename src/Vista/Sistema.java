/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import Modelo.Cliente;
import Modelo.ClienteDAO;
import Modelo.Detalle;
import Modelo.Empresa;
import Modelo.EmpresaDAO;
import Modelo.InventarioDAO;
import Modelo.Productos;
import Modelo.ProductosDAO;
import Modelo.Proveedor;
import Modelo.ProveedorDAO;
import Modelo.Venta;
import Modelo.VentaDAO;
import Reportes.Excel;
import static Vista.Login.tipoUsuario;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import javax.swing.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.itextpdf.text.pdf.PdfPCell;
import java.io.Writer;

/**
 *
 * @author Tavo
 */
public class Sistema extends javax.swing.JFrame {

    Cliente cl = new Cliente();
    ClienteDAO client = new ClienteDAO();
    Proveedor pr = new Proveedor();
    ProveedorDAO PrDAO = new ProveedorDAO();
    Productos pro = new Productos();
    ProductosDAO proDAO = new ProductosDAO();
    Venta v = new Venta();
    Venta vent = new Venta();
    VentaDAO vDAO = new VentaDAO();
    Detalle Dv = new Detalle();
    DefaultTableModel modelo = new DefaultTableModel();
    int item;
    double Totalpagar = 0.00;
    String ivaProducto = "NO";
    InventarioDAO invDAO = new InventarioDAO();
    Empresa empresa = new Empresa();
    EmpresaDAO empresaDAO = new EmpresaDAO();
    private double montoCajaInicial = 0;

    public Sistema() {
        initComponents();

        this.setLocationRelativeTo(null);
        txtIdCliente.setVisible(false);
        txtIdProveedor.setVisible(false);
        AutoCompleteDecorator.decorate(cbxProveedorPro);
        proDAO.ConsultarProveedor(cbxProveedorPro);
        LabelVendedor.setText(Login.nombreUsuario);
        menuCerrarSesion.setEnabled(false);

        //cargador de imagen del logo
        SwingUtilities.invokeLater(() -> {
            rutaLogo = "logo_pdf.png"; // ← siempre este nombre
            mostrarLogo(rutaLogo);
        });

        // Agrega solo estas líneas:
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            lblHora.setText(LocalTime.now().format(formatoHora));
            lblFecha.setText(LocalDate.now().format(formatoFecha));
        });
        timer.setInitialDelay(0);
        timer.start();

        //Icono de carrito de compras del banner
        ImageIcon icono = new ImageIcon(getClass().getResource("/Img/Carrito-de-compras_logo.png"));
        setIconImage(icono.getImage());

        TableVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableVentaMouseClicked(evt);
            }
        });
        try {
            cargarDatosEmpresa();
        } catch (Exception e) {
            System.out.println("Sin datos de empresa: " + e.toString());
        }
        // Para ocultar las pestañas de la vista del sistema
        jTabbedPane1.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int placement, int runCount, int maxTabHeight) {
                return 0;
            }
        });

        // ← AGREGAR ESTO: ocultar botón si no es Administrador
        // Administrador = 1, Cajero = 2, Bodega = 3
        switch (Login.tipoUsuario) {
            case 1: // Administrador - ve todo
                jButton9.setVisible(true);
                menuSalir.setVisible(true);
                break;
            case 2: // Cajero - solo ventas y clientes
                jButton6.setVisible(false); // ocultar Config
                jButton9.setVisible(false);
                jButton3.setVisible(false); // ocultar Proveedores
                jButton7.setVisible(false); // ocultar Inventario
                jButton1.setEnabled(false); // ← Ventas deshabilitado hasta abrir caja
                menuSalir.setVisible(true);
                break;
            case 3: // Bodega - solo inventario y productos
                jButton9.setVisible(false);
                jButton1.setVisible(false); // ocultar Nueva Venta
                jButton2.setVisible(false); // ocultar Clientes
                jButton5.setVisible(false); // ocultar Ventas
                jButton6.setVisible(false); // ocultar Config
                menuSalir.setVisible(true);
                break;
        }
        // Si NO es cajero, ocultar el menú de caja
        if (Login.tipoUsuario != 2) {
            menuIniciarSesion.setEnabled(false);
            menuCerrarSesion.setEnabled(false);
        }

        TableProveedor.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && TableProveedor.getSelectedRow() != -1) {

                int fila = TableProveedor.getSelectedRow();
                Object estado = TableProveedor.getValueAt(fila, 7);

                int estadoVal = Integer.parseInt(estado.toString().trim());

                if (estadoVal == 1) {
                    btnEliminarProveedor.setEnabled(true);    // Boton Activo
                    btnGuardarProveedor.setEnabled(true);     // Boton Activo
                    btnNuevoProveedor.setEnabled(true);       // Boton Activo
                    btnActaluzarProveedor.setEnabled(true); // Boton Activo
                } else {
                    btnEliminarProveedor.setEnabled(true);    // Boton Activo
                    btnGuardarProveedor.setEnabled(false);    // Boton inactivo
                    btnNuevoProveedor.setEnabled(false);      // Boton inactivo
                    btnActaluzarProveedor.setEnabled(false);// Boton inactivo
                }
            }
        });

    }

    private void mostrarLogo(String nombreArchivo) {
        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
            String ruta = "src/Img/" + nombreArchivo;
            ImageIcon imagen = new ImageIcon(ruta);

            // Obtener tamaño real del label
            int ancho = LabelLogo.getWidth() > 0 ? LabelLogo.getWidth() : LabelLogo.getPreferredSize().width;
            int alto = LabelLogo.getHeight() > 0 ? LabelLogo.getHeight() : LabelLogo.getPreferredSize().height;

            // Si sigue siendo 0 usar tamaño fijo
            if (ancho <= 0) {
                ancho = 158;
            }
            if (alto <= 0) {
                alto = 158;
            }

            Image imgEscalada = imagen.getImage().getScaledInstance(
                    ancho, alto, Image.SCALE_SMOOTH
            );
            LabelLogo.setText("");
            LabelLogo.setIcon(new ImageIcon(imgEscalada));
        } else {
            // Si no hay imagen mostrar texto placeholder
            LabelLogo.setIcon(null);
            LabelLogo.setText("Aquí va su logo");
            LabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }
    }

    public void ListarCliente() {
        //modelo.setRowCount(0);
        List<Cliente> ListarCl = client.ListarCliente();
        modelo = (DefaultTableModel) TableCliente.getModel();

        Object[] ob = new Object[8]; // 👈 ahora son 8

        for (int i = 0; i < ListarCl.size(); i++) {
            ob[0] = ListarCl.get(i).getId();
            ob[1] = ListarCl.get(i).getNit();
            ob[2] = ListarCl.get(i).getNombre();
            ob[3] = ListarCl.get(i).getTelefono();
            ob[4] = ListarCl.get(i).getCorreo();
            ob[5] = ListarCl.get(i).getDireccion();
            ob[6] = ListarCl.get(i).getRazon();
            ob[7] = ListarCl.get(i).getEstado(); // 👈 IMPORTANTE
            modelo.addRow(ob);
        }

        TableCliente.setModel(modelo);

        aplicarColorEstado(); // lo llamas desde aquí

        // Activa o desactiva los botones según sea el estado del cliente
        TableCliente.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && TableCliente.getSelectedRow() != -1) {

                int fila = TableCliente.getSelectedRow();
                Object estado = TableCliente.getValueAt(fila, 7);

                int estadoVal = Integer.parseInt(estado.toString().trim());

                if (estadoVal == 1) {
                    btnEliminarCliente.setEnabled(true);    // Boton Activo
                    btnGuardarCliente.setEnabled(true);     // Boton Activo
                    btnNuevoCliente.setEnabled(true);       // Boton Activo
                    btnActualizarClientye.setEnabled(true); // Boton Activo
                } else {
                    btnEliminarCliente.setEnabled(true);    // Boton Activo
                    btnGuardarCliente.setEnabled(false);    // Boton inactivo
                    btnNuevoCliente.setEnabled(false);      // Boton inactivo
                    btnActualizarClientye.setEnabled(false);// Boton inactivo
                }
            }
        });

    }

    private void cargarDatosEmpresa() {
        try {
            Empresa emp = empresaDAO.ObtenerEmpresa();
            if (emp != null && emp.getNit() != null && !emp.getNit().isEmpty()) {
                txtNitEmpresa.setText(emp.getNit());
                txtNombreEmpresa.setText(emp.getNombre());
                txtCorreoEmpresa.setText(emp.getCorreo());
                txtDireccionEmpresa.setText(emp.getDireccion());
                txtRazonSocialEmpresa.setText(emp.getRazonSocial());
                txtTelefonoEmpresa.setText(emp.getTelefono());
                rutaLogo = emp.getLogo();
                mostrarLogo(rutaLogo);
                empresa.setId(emp.getId()); // ✅ guardar el id para el update

                // ✅ Si ya hay datos, bloquear Guardar y habilitar Actualizar
                btnGuardarEmpresa.setEnabled(false);
                btnActualizarEmpresa.setEnabled(true);
            } else {
                // ✅ Si no hay datos, habilitar Guardar y bloquear Actualizar
                btnGuardarEmpresa.setEnabled(true);
                btnActualizarEmpresa.setEnabled(false);
            }
        } catch (Exception e) {
            System.out.println("Error cargando empresa: " + e.toString());
        }
    }

    // Color del listado de los clientes si estan en estado activo o no
    private void aplicarColorEstado() {
        TableCliente.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Object estado = table.getModel().getValueAt(row, 7);

                if (!isSelected) {
                    int estadoVal = Integer.parseInt(estado.toString().trim());

                    if (estadoVal == 1) {
                        c.setBackground(new Color(232, 255, 234)); // Verde suave - Activo
                        c.setForeground(new Color(0, 120, 0));     // Texto verde oscuro
                    } else {
                        c.setBackground(new Color(189, 189, 189)); // Gris - Inactivo
                        c.setForeground(new Color(245, 73, 39)); // Texto gris oscuro
                    }
                }
                return c;
            }
        });

        // Ocultar el texlabel ID
        TableCliente.getColumnModel().getColumn(0).setMinWidth(0);
        TableCliente.getColumnModel().getColumn(0).setMaxWidth(0);
        TableCliente.getColumnModel().getColumn(0).setPreferredWidth(0);

        // ocultar la columna de estado (columna 6)
        TableCliente.getColumnModel().getColumn(6).setMinWidth(0);
        TableCliente.getColumnModel().getColumn(6).setMaxWidth(0);
        TableCliente.getColumnModel().getColumn(6).setPreferredWidth(0);
    }

    //Mostrar la hora y fecha
    public class RelojLabel extends JFrame {

        public RelojLabel() {
            JLabel lblHora = new JLabel();
            lblHora.setFont(lblHora.getFont().deriveFont(32f));
            lblHora.setHorizontalAlignment(SwingConstants.CENTER);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            Timer timer = new Timer(1000, e -> {
                lblHora.setText(LocalTime.now().format(formatter));
            });

            timer.setInitialDelay(0);
            timer.start();

            add(lblHora);
            setSize(300, 100);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setVisible(true);
        }
    }

    public void LimpiarTable() {
        DefaultTableModel modelo = (DefaultTableModel) TableCliente.getModel();
        modelo.setRowCount(0);
    }

    public void LimpiarTablePr() {
        DefaultTableModel modelo = (DefaultTableModel) TableProducto.getModel();
        modelo.setRowCount(0);
    }

    public void LimpiarTablePro() {
        DefaultTableModel modelo = (DefaultTableModel) TableProveedor.getModel();
        modelo.setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem2 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        LabelLogo = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        LabelVendedor = new javax.swing.JLabel();
        lblHora = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnEliminarventa = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtCodigoVenta = new javax.swing.JTextField();
        txtDescripcionVenta = new javax.swing.JTextField();
        txtCantidadVenta = new javax.swing.JTextField();
        txtPrecioVenta = new javax.swing.JTextField();
        txtStockDisponible = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableVenta = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNitventa = new javax.swing.JTextField();
        txtNombreClienteventa = new javax.swing.JTextField();
        btnGenerarVenta = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        LabelTotal = new javax.swing.JLabel();
        txtTelefonoCV = new javax.swing.JTextField();
        txtDireccionCV = new javax.swing.JTextField();
        txtRazonCV = new javax.swing.JTextField();
        txtIdPro = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lblSubTotal = new javax.swing.JLabel();
        lblIva = new javax.swing.JLabel();
        lblCant = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txtCorreoVenta = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtNitCliente = new javax.swing.JTextField();
        txtNombreCliente = new javax.swing.JTextField();
        txtTelefonoCliente = new javax.swing.JTextField();
        txtDireccionCliente = new javax.swing.JTextField();
        txtRazonCliente = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableCliente = new javax.swing.JTable();
        btnGuardarCliente = new javax.swing.JButton();
        btnActualizarClientye = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        btnNuevoCliente = new javax.swing.JButton();
        txtIdCliente = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtNitProveedor = new javax.swing.JTextField();
        txtNombreProveedor = new javax.swing.JTextField();
        txtTelefonoProveedor = new javax.swing.JTextField();
        txtDireccionProveedor = new javax.swing.JTextField();
        txtRazonProveedor = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        TableProveedor = new javax.swing.JTable();
        btnGuardarProveedor = new javax.swing.JButton();
        btnActaluzarProveedor = new javax.swing.JButton();
        btnNuevoProveedor = new javax.swing.JButton();
        btnEliminarProveedor = new javax.swing.JButton();
        txtIdProveedor = new javax.swing.JTextField();
        btnExcelProveedor = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        txtCorreoProv = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtCodigoPro = new javax.swing.JTextField();
        txtDesPro = new javax.swing.JTextField();
        txtcantPro = new javax.swing.JTextField();
        txtPrecioPro = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        cbxProveedorPro = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        TableProducto = new javax.swing.JTable();
        btnGuardarPro = new javax.swing.JButton();
        btnActualizarpro = new javax.swing.JButton();
        btnNuevoPro = new javax.swing.JButton();
        btnEliminarPro = new javax.swing.JButton();
        btnExcelPro = new javax.swing.JButton();
        txtIdpro = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        cmbIva = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        lblIvaProducto = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        lblPrecioConIva = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        TableVentas = new javax.swing.JTable();
        btnPdfVentas = new javax.swing.JButton();
        txtIdVenta = new javax.swing.JTextField();
        btnVerVenta = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtNitEmpresa = new javax.swing.JTextField();
        txtNombreEmpresa = new javax.swing.JTextField();
        txtTelefonoEmpresa = new javax.swing.JTextField();
        txtDireccionEmpresa = new javax.swing.JTextField();
        txtRazonSocialEmpresa = new javax.swing.JTextField();
        btnActualizarEmpresa = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtCorreoEmpresa = new javax.swing.JTextField();
        btnGuardarEmpresa = new javax.swing.JButton();
        lblLogo = new javax.swing.JLabel();
        btnSeleccionarLogo = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuIniciarSesion = new javax.swing.JMenuItem();
        menuCerrarSesion = new javax.swing.JMenuItem();
        menuSalir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SOFTWARE DE VENTAS");
        setIconImages(null);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(180, 190, 199));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Nventa.png"))); // NOI18N
        jButton1.setText("Nueva Venta");
        jButton1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Clientes.png"))); // NOI18N
        jButton2.setText("Clientes");
        jButton2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/proveedor.png"))); // NOI18N
        jButton3.setText("Proveedor");
        jButton3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/producto.png"))); // NOI18N
        jButton4.setText("Productos");
        jButton4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/compras.png"))); // NOI18N
        jButton5.setText("Ventas");
        jButton5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/config.png"))); // NOI18N
        jButton6.setText("Config");
        jButton6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        LabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/logo!.jpg"))); // NOI18N

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/inventario1.24.png"))); // NOI18N
        jButton7.setText("Inventario");
        jButton7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        LabelVendedor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelVendedor.setText("Vendedor");

        lblHora.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHora.setForeground(new java.awt.Color(51, 51, 255));
        lblHora.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lblFecha.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFecha.setForeground(new java.awt.Color(51, 51, 255));
        lblFecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Versión 1.0 Gustavo Celis 2026 ©");
        jLabel39.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/persona-de-libre-dedicacion 24.png"))); // NOI18N
        jButton9.setText("Usuarios");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LabelVendedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblHora, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(LabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LabelVendedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblHora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel39)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, 800));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/label6.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 0, -1, 140));

        jPanel2.setEnabled(false);

        jLabel3.setText("Código");

        jLabel4.setText("Descripción");

        jLabel5.setText("Cantidad");

        jLabel6.setText("Precio");

        btnEliminarventa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/eliminar.png"))); // NOI18N
        btnEliminarventa.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnEliminarventa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarventaActionPerformed(evt);
            }
        });

        jLabel7.setText("Stock disponible");

        txtCodigoVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoVentaActionPerformed(evt);
            }
        });
        txtCodigoVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodigoVentaKeyPressed(evt);
            }
        });

        txtDescripcionVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescripcionVentaActionPerformed(evt);
            }
        });

        txtCantidadVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadVentaActionPerformed(evt);
            }
        });
        txtCantidadVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadVentaKeyPressed(evt);
            }
        });

        txtPrecioVenta.setEditable(false);

        TableVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Cantidad", "Precio", "IVA", "Total", "Total IVA"
            }
        ));
        TableVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableVentaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TableVenta);
        if (TableVenta.getColumnModel().getColumnCount() > 0) {
            TableVenta.getColumnModel().getColumn(0).setPreferredWidth(30);
            TableVenta.getColumnModel().getColumn(1).setPreferredWidth(100);
            TableVenta.getColumnModel().getColumn(2).setPreferredWidth(1);
            TableVenta.getColumnModel().getColumn(3).setPreferredWidth(30);
            TableVenta.getColumnModel().getColumn(4).setPreferredWidth(15);
            TableVenta.getColumnModel().getColumn(5).setPreferredWidth(40);
            TableVenta.getColumnModel().getColumn(6).setPreferredWidth(30);
        }

        jLabel8.setText("CC/NIT");

        jLabel9.setText("Nombre");

        txtNitventa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNitventaActionPerformed(evt);
            }
        });
        txtNitventa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNitventaKeyPressed(evt);
            }
        });

        txtNombreClienteventa.setEditable(false);

        btnGenerarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/print.png"))); // NOI18N
        btnGenerarVenta.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnGenerarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarVentaActionPerformed(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/money.png"))); // NOI18N
        jLabel10.setText("Total a Pagar:");

        LabelTotal.setText("Total");

        txtTelefonoCV.setEditable(false);
        txtTelefonoCV.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtTelefonoCV.setEnabled(false);

        txtDireccionCV.setEditable(false);
        txtDireccionCV.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDireccionCV.setEnabled(false);

        txtRazonCV.setEditable(false);
        txtRazonCV.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRazonCV.setEnabled(false);

        txtIdPro.setText("ID");
        txtIdPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdProActionPerformed(evt);
            }
        });

        jLabel2.setText("SubTotal:");

        jLabel33.setText("IVA:");

        jLabel34.setText("Items:");

        lblSubTotal.setText("Sub");

        lblIva.setText("Iva");

        lblCant.setText("Cantidad");

        jLabel41.setText("Corre:");

        txtCorreoVenta.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txtDescripcionVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtCantidadVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPrecioVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdPro, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(41, 41, 41)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtStockDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarventa, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(85, 85, 85)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel34)
                                .addGap(18, 18, 18)
                                .addComponent(lblCant, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(174, 174, 174))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNitventa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel41))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNombreClienteventa, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(txtTelefonoCV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtDireccionCV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtRazonCV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(txtCorreoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(225, 225, 225)
                                        .addComponent(btnGenerarVenta)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel10)))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblSubTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblIva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LabelTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtDescripcionVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCodigoVenta)))
                        .addComponent(btnEliminarventa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtStockDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCantidadVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel2)
                    .addComponent(lblSubTotal)
                    .addComponent(jLabel34)
                    .addComponent(lblCant))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNitventa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(lblIva)
                    .addComponent(txtNombreClienteventa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGenerarVenta)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTelefonoCV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDireccionCV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRazonCV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCorreoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(113, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", jPanel2);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("CC/NIT:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Nombre:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Teléfono:");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Dirección:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("Razón Social:");

        txtNitCliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtTelefonoCliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtDireccionCliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtRazonCliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        TableCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CC/NIT", "Nombre", "Teléfono", "Correo", "Dirección", "Razon Social", "Estado"
            }
        ));
        TableCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableClienteMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(TableCliente);
        if (TableCliente.getColumnModel().getColumnCount() > 0) {
            TableCliente.getColumnModel().getColumn(0).setPreferredWidth(10);
            TableCliente.getColumnModel().getColumn(1).setPreferredWidth(50);
            TableCliente.getColumnModel().getColumn(2).setPreferredWidth(100);
            TableCliente.getColumnModel().getColumn(3).setPreferredWidth(50);
            TableCliente.getColumnModel().getColumn(4).setPreferredWidth(50);
            TableCliente.getColumnModel().getColumn(5).setPreferredWidth(80);
            TableCliente.getColumnModel().getColumn(6).setPreferredWidth(80);
            TableCliente.getColumnModel().getColumn(7).setPreferredWidth(1);
        }

        btnGuardarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/GuardarTodo.png"))); // NOI18N
        btnGuardarCliente.setText("Guardar");
        btnGuardarCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnGuardarCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarCliente.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnGuardarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarClienteActionPerformed(evt);
            }
        });

        btnActualizarClientye.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Actualizar (2).png"))); // NOI18N
        btnActualizarClientye.setText("Actualizar");
        btnActualizarClientye.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnActualizarClientye.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnActualizarClientye.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarClientyeActionPerformed(evt);
            }
        });

        btnEliminarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/encendido-apagado.png"))); // NOI18N
        btnEliminarCliente.setText("Estado");
        btnEliminarCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnEliminarCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        btnNuevoCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/nuevo.png"))); // NOI18N
        btnNuevoCliente.setText("Nuevo");
        btnNuevoCliente.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnNuevoCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoClienteActionPerformed(evt);
            }
        });

        txtIdCliente.setText("ID");
        txtIdCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdClienteActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        jButton8.setText("EXC");
        jButton8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Correo:");

        txtCorreo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtNitCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNuevoCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGuardarCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnActualizarClientye, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEliminarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel11)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtCorreo, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtRazonCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIdCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                            .addComponent(txtNitCliente))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRazonCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnActualizarClientye, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGuardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNuevoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("tab2", jPanel3);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("CC/NIT:");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Nombre:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Teléfono:");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Dirección:");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Razón Social:");

        TableProveedor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CC/NIT", "Nombre", "Teléfono", "Correo", "Dirección", "Razón Social", "Estado"
            }
        ));
        TableProveedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableProveedorMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(TableProveedor);
        if (TableProveedor.getColumnModel().getColumnCount() > 0) {
            TableProveedor.getColumnModel().getColumn(0).setPreferredWidth(10);
            TableProveedor.getColumnModel().getColumn(1).setPreferredWidth(50);
            TableProveedor.getColumnModel().getColumn(2).setPreferredWidth(100);
            TableProveedor.getColumnModel().getColumn(3).setPreferredWidth(50);
            TableProveedor.getColumnModel().getColumn(4).setPreferredWidth(50);
            TableProveedor.getColumnModel().getColumn(5).setPreferredWidth(80);
            TableProveedor.getColumnModel().getColumn(6).setPreferredWidth(70);
            TableProveedor.getColumnModel().getColumn(7).setPreferredWidth(1);
        }

        btnGuardarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/GuardarTodo.png"))); // NOI18N
        btnGuardarProveedor.setText("Guardar");
        btnGuardarProveedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnGuardarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProveedorActionPerformed(evt);
            }
        });

        btnActaluzarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Actualizar (2).png"))); // NOI18N
        btnActaluzarProveedor.setText("Actualizar");
        btnActaluzarProveedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnActaluzarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActaluzarProveedorActionPerformed(evt);
            }
        });

        btnNuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/nuevo.png"))); // NOI18N
        btnNuevoProveedor.setText("Nuevo");
        btnNuevoProveedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnNuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoProveedorActionPerformed(evt);
            }
        });

        btnEliminarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/encendido-apagado.png"))); // NOI18N
        btnEliminarProveedor.setText("Estado");
        btnEliminarProveedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnEliminarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProveedorActionPerformed(evt);
            }
        });

        txtIdProveedor.setText("ID");

        btnExcelProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        btnExcelProveedor.setText("EXC");
        btnExcelProveedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnExcelProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelProveedorActionPerformed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel40.setText("Correo:");

        txtCorreoProv.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnExcelProveedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNuevoProveedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGuardarProveedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEliminarProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnActaluzarProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel40)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtCorreoProv, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(txtNitProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtTelefonoProveedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addComponent(txtDireccionProveedor, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtRazonProveedor, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtNombreProveedor, javax.swing.GroupLayout.Alignment.LEADING)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNitProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTelefonoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDireccionProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRazonProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCorreoProv, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnActaluzarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEliminarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNuevoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnExcelProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jTabbedPane1.addTab("tab3", jPanel4);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Código:");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Descripción:");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Cantidad:");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("Precio:");

        txtcantPro.setEnabled(false);

        txtPrecioPro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPrecioProKeyReleased(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("Proveedor:");

        cbxProveedorPro.setEditable(true);

        TableProducto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Código", "descripción", "Proveedor", "Stock", "Precio", "IVA", "Iva", "Precio Total", "Estado"
            }
        ));
        TableProducto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableProductoMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(TableProducto);
        if (TableProducto.getColumnModel().getColumnCount() > 0) {
            TableProducto.getColumnModel().getColumn(0).setPreferredWidth(20);
            TableProducto.getColumnModel().getColumn(1).setPreferredWidth(40);
            TableProducto.getColumnModel().getColumn(2).setPreferredWidth(100);
            TableProducto.getColumnModel().getColumn(3).setPreferredWidth(60);
            TableProducto.getColumnModel().getColumn(4).setPreferredWidth(40);
            TableProducto.getColumnModel().getColumn(5).setPreferredWidth(50);
            TableProducto.getColumnModel().getColumn(6).setPreferredWidth(5);
            TableProducto.getColumnModel().getColumn(7).setPreferredWidth(10);
            TableProducto.getColumnModel().getColumn(8).setPreferredWidth(40);
            TableProducto.getColumnModel().getColumn(9).setPreferredWidth(1);
        }

        btnGuardarPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/GuardarTodo.png"))); // NOI18N
        btnGuardarPro.setText("Guardar");
        btnGuardarPro.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnGuardarPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProActionPerformed(evt);
            }
        });

        btnActualizarpro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Actualizar (2).png"))); // NOI18N
        btnActualizarpro.setText("Actualizar");
        btnActualizarpro.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnActualizarpro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarproActionPerformed(evt);
            }
        });

        btnNuevoPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/nuevo.png"))); // NOI18N
        btnNuevoPro.setText("Nuevo");
        btnNuevoPro.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnNuevoPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoProActionPerformed(evt);
            }
        });

        btnEliminarPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/encendido-apagado.png"))); // NOI18N
        btnEliminarPro.setText("Estado");
        btnEliminarPro.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnEliminarPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProActionPerformed(evt);
            }
        });

        btnExcelPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        btnExcelPro.setText("EXC");
        btnExcelPro.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnExcelPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelProActionPerformed(evt);
            }
        });

        txtIdpro.setText("ID");

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel35.setText("IVA:");

        cmbIva.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SI", "NO" }));
        cmbIva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIvaActionPerformed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setText("Iva:");

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel37.setText("Costo:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtcantPro)
                        .addComponent(txtPrecioPro)
                        .addComponent(jLabel22)
                        .addComponent(jLabel23)
                        .addComponent(jLabel24)
                        .addComponent(jLabel25)
                        .addComponent(jLabel26)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                            .addComponent(txtCodigoPro, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtIdpro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtDesPro)
                        .addComponent(cbxProveedorPro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnExcelPro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNuevoPro, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35)
                                    .addComponent(cmbIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel36)
                                    .addComponent(lblIvaProducto)))
                            .addComponent(btnGuardarPro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel37)
                            .addComponent(btnEliminarPro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(lblPrecioConIva)
                                .addGap(28, 28, 28))
                            .addComponent(btnActualizarpro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCodigoPro, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdpro, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDesPro, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtcantPro, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrecioPro, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxProveedorPro, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(jLabel36)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblIvaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPrecioConIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardarPro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnActualizarpro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNuevoPro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminarPro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnExcelPro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jTabbedPane1.addTab("tab4", jPanel5);

        TableVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Cliente", "Vendedor", "Total"
            }
        ));
        jScrollPane5.setViewportView(TableVentas);
        if (TableVentas.getColumnModel().getColumnCount() > 0) {
            TableVentas.getColumnModel().getColumn(0).setPreferredWidth(20);
            TableVentas.getColumnModel().getColumn(1).setPreferredWidth(60);
            TableVentas.getColumnModel().getColumn(2).setPreferredWidth(60);
            TableVentas.getColumnModel().getColumn(3).setPreferredWidth(60);
        }

        btnPdfVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/pdf.png"))); // NOI18N
        btnPdfVentas.setText("PDF");
        btnPdfVentas.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtIdVenta.setText("ID");

        btnVerVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/compras.png"))); // NOI18N
        btnVerVenta.setText("Ver");
        btnVerVenta.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnVerVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(240, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnPdfVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnVerVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtIdVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPdfVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnVerVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtIdVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab5", jPanel6);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("CC/NIT");

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("Nombre");

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel29.setText("Teléfono");

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setText("Dirección");

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel31.setText("Razón Social");

        txtRazonSocialEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRazonSocialEmpresaActionPerformed(evt);
            }
        });

        btnActualizarEmpresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Actualizar (2).png"))); // NOI18N
        btnActualizarEmpresa.setText("Actualizar");
        btnActualizarEmpresa.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnActualizarEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarEmpresaActionPerformed(evt);
            }
        });

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("DATOS DE LA EMPRESA");

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel38.setText("Correo electrónico");

        btnGuardarEmpresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/GuardarTodo.png"))); // NOI18N
        btnGuardarEmpresa.setText("Guardar");
        btnGuardarEmpresa.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        btnGuardarEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarEmpresaActionPerformed(evt);
            }
        });

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setText("LOGO");
        lblLogo.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnSeleccionarLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/adjuntar-archivo.png"))); // NOI18N
        btnSeleccionarLogo.setText("Adjuntar logo");
        btnSeleccionarLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarLogoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNitEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDireccionEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30)
                            .addComponent(jLabel27)
                            .addComponent(jLabel38)
                            .addComponent(txtCorreoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtNombreEmpresa)
                                .addComponent(jLabel31)
                                .addComponent(txtRazonSocialEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel28)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(txtTelefonoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel29))
                        .addGap(89, 89, 89)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSeleccionarLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(94, 94, 94))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardarEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnActualizarEmpresa)
                .addGap(352, 352, 352))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(jLabel32)
                .addGap(62, 62, 62)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSeleccionarLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(jLabel28)
                            .addGap(18, 18, 18)
                            .addComponent(txtNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel31)
                            .addGap(18, 18, 18)
                            .addComponent(txtRazonSocialEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel29)
                            .addGap(18, 18, 18)
                            .addComponent(txtTelefonoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(jLabel27)
                            .addGap(18, 18, 18)
                            .addComponent(txtNitEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel30)
                            .addGap(18, 18, 18)
                            .addComponent(txtDireccionEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel38)
                            .addGap(18, 18, 18)
                            .addComponent(txtCorreoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(51, 51, 51)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActualizarEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardarEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab6", jPanel7);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, 960, 600));

        jMenu1.setText("Menú");

        menuIniciarSesion.setText("Iniciar Sesión");
        menuIniciarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuIniciarSesionActionPerformed(evt);
            }
        });
        jMenu1.add(menuIniciarSesion);

        menuCerrarSesion.setText("Cerrar Sesión");
        menuCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCerrarSesionActionPerformed(evt);
            }
        });
        jMenu1.add(menuCerrarSesion);

        menuSalir.setText("Salir");
        menuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSalirActionPerformed(evt);
            }
        });
        jMenu1.add(menuSalir);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Ayuda");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        LimpiarTable();
        LimpiarCliente();
        ListarCliente();
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        LimpiarTablePr();
        LimpiarProveedor();
        ListarProveedor();
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        LimpiarTablePr();
        LimpiarProductos();
        ListarProductos();
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(4);
        LimpiarTable();
        ListarVentas();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(5);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        Inventario inventario = new Inventario();
        inventario.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btnVerVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerVentaActionPerformed
        // TODO add your handling code here:
        // En el ActionListener del botón "Ver"

        int filaSeleccionada = TableVentas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta primero");
            return;
        }

        int idVenta = Integer.parseInt(TableVentas.getValueAt(filaSeleccionada, 0).toString());

        // Recoger todos los IDs de la tabla
        List<Integer> listaIds = new ArrayList<>();
        for (int i = 0; i < TableVentas.getRowCount(); i++) {
            listaIds.add(Integer.parseInt(TableVentas.getValueAt(i, 0).toString()));
        }

        DetalleVenta dialogo = new DetalleVenta(idVenta); // ← constructor original intacto
        dialogo.inicializarNavegacion(listaIds, filaSeleccionada); // ← línea nueva
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);

    }//GEN-LAST:event_btnVerVentaActionPerformed

    private void btnEliminarProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProActionPerformed
        // TODO add your handling code here:
        int fila = TableProducto.getSelectedRow();

        // 🔴 Validar si seleccionó algo
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }

        int id = Integer.parseInt(TableProducto.getModel().getValueAt(fila, 0).toString());
        int estadoActual = Integer.parseInt(TableProducto.getModel().getValueAt(fila, 9).toString());

        // Mensaje dinámico según estado actual
        String mensaje = estadoActual == 1 ? "¿Desea desactivar este producto?" : "¿Desea activar este producto?";

        int confirm = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (proDAO.EliminarProducto(id)) {
                modelo.setRowCount(0);
                ListarProductos();
            } else {
                JOptionPane.showMessageDialog(null, "Error al bloquear Producto");
            }
        }
        LimpiarTablePr();
        LimpiarProductos();
        ListarProductos();
    }//GEN-LAST:event_btnEliminarProActionPerformed

    private void btnNuevoProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoProActionPerformed
        // TODO add your handling code here:
        LimpiarProductos();
    }//GEN-LAST:event_btnNuevoProActionPerformed

    private void btnActualizarproActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarproActionPerformed
        // TODO add your handling code here:
        if ("".equals(txtIdPro.getText())) {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        } else if (!"".equals(txtCodigoPro.getText()) || !"".equals(txtDesPro.getText())
                || !"".equals(txtcantPro.getText()) || !"".equals(txtPrecioPro.getText())) {

            double precio = Double.parseDouble(txtPrecioPro.getText()); // ✅ corregido
            String tieneIva = cmbIva.getSelectedItem().toString();
            double valorIva = tieneIva.equals("SI") ? precio * 0.19 : 0.00; // ✅ nuevo
            double precioFinal = precio + valorIva; // ✅ nuevo

            pro.setCodigo(txtCodigoPro.getText());
            pro.setNombre(txtDesPro.getText());
            pro.setProveedor(cbxProveedorPro.getSelectedItem().toString());
            pro.setStock(Integer.parseInt(txtcantPro.getText()));
            pro.setPrecio(precio);          // ✅ corregido
            pro.setIva(tieneIva);           // ✅ nuevo
            pro.setValorIva(valorIva);      // ✅ nuevo
            pro.setPrecioFinal(precioFinal); // ✅ nuevo
            pro.setId(Integer.parseInt(txtIdPro.getText()));

            proDAO.ModificarProductos(pro);
            JOptionPane.showMessageDialog(null, "Actualizado correctamente");
            LimpiarTablePr();
            LimpiarProductos();
            ListarProductos();
        }
        btnGuardarProveedor.setEnabled(true);      // Activo para guardar nuevo
        btnNuevoProveedor.setEnabled(true);        // Activo
        btnEliminarProveedor.setEnabled(false);    // Inactivo (no hay nada seleccionado)
        btnActualizarpro.setEnabled(false);  // Inactivo (no hay nada seleccionado)
        btnExcelProveedor.setEnabled(false);             // Inactivo (no hay nada seleccionado)

        // Poner el foco en el primer campo
        txtIdProveedor.requestFocus();
    }//GEN-LAST:event_btnActualizarproActionPerformed

    private void btnGuardarProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProActionPerformed
        // TODO add your handling code here:

        if (!"".equals(txtCodigoPro.getText()) || !"".equals(txtDesPro.getText())
                || !"".equals(cbxProveedorPro.getSelectedItem()) || !"".equals(txtcantPro.getText())
                || !"".equals(txtPrecioPro.getText())) {

            double precio = Double.parseDouble(txtPrecioPro.getText().trim());
            String tieneIva = cmbIva.getSelectedItem().toString(); // tu combo SI/NO

            // ✅ Calcular IVA automáticamente
            double valorIva = tieneIva.equals("SI") ? precio * 0.19 : 0.00;
            double precioFinal = precio + valorIva;

            pro.setCodigo(txtCodigoPro.getText());
            pro.setNombre(txtDesPro.getText());
            pro.setProveedor(cbxProveedorPro.getSelectedItem().toString());
            pro.setStock(Integer.parseInt(txtcantPro.getText()));
            pro.setPrecio(Double.parseDouble(txtPrecioPro.getText()));
            pro.setIva(tieneIva);           // ✅ nuevo
            pro.setValorIva(valorIva);      // ✅ nuevo
            pro.setPrecioFinal(precioFinal); // ✅ nuevo

            proDAO.RegistrarProducto(pro);
            JOptionPane.showMessageDialog(null, "Producto registrado");
        } else {
            JOptionPane.showMessageDialog(null, "Los campos estan vacios");
        }
        LimpiarTablePr();
        LimpiarProductos();
        ListarProductos();
    }//GEN-LAST:event_btnGuardarProActionPerformed

    private void TableProductoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableProductoMouseClicked
        // TODO add your handling code here:
        int fila = TableProducto.rowAtPoint(evt.getPoint());
        txtIdPro.setText(TableProducto.getValueAt(fila, 0).toString());
        txtCodigoPro.setText(TableProducto.getValueAt(fila, 1).toString());
        txtDesPro.setText(TableProducto.getValueAt(fila, 2).toString());
        cbxProveedorPro.setSelectedItem(TableProducto.getValueAt(fila, 3).toString());
        txtcantPro.setText(TableProducto.getValueAt(fila, 4).toString());
        txtPrecioPro.setText(TableProducto.getValueAt(fila, 5).toString());
    }//GEN-LAST:event_TableProductoMouseClicked

    private void btnEliminarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProveedorActionPerformed
        // TODO add your handling code here:
        int fila = TableProveedor.getSelectedRow();

        // 🔴 Validar si seleccionó algo
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor");
            return;
        }

        int id = Integer.parseInt(TableProveedor.getModel().getValueAt(fila, 0).toString());
        int estadoActual = Integer.parseInt(TableProveedor.getModel().getValueAt(fila, 6).toString());

        // Mensaje dinámico según estado actual
        String mensaje = estadoActual == 1 ? "¿Desea desactivar este proveedor?" : "¿Desea activar este Proveedor?";

        int confirm = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (PrDAO.EliminarProveedor(id)) {
                modelo.setRowCount(0);
                ListarProveedor();
            } else {
                JOptionPane.showMessageDialog(null, "Error al bloquear Proveedor");
            }
        }
        LimpiarTablePro();
        LimpiarProveedor();
        ListarProveedor();
    }//GEN-LAST:event_btnEliminarProveedorActionPerformed

    private void btnNuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoProveedorActionPerformed
        // TODO add your handling code here:
        LimpiarProveedor();
    }//GEN-LAST:event_btnNuevoProveedorActionPerformed

    private void btnActaluzarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActaluzarProveedorActionPerformed
        // TODO add your handling code here:
        if ("".equals(txtIdProveedor.getText())) {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        } else {
            pr.setNit(Integer.parseInt(txtNitProveedor.getText()));
            pr.setNombre(txtNombreProveedor.getText());
            pr.setTelefono(txtTelefonoProveedor.getText());
            pr.setCorreo(txtCorreoProv.getText());
            pr.setDireccion(txtDireccionProveedor.getText());
            pr.setRazon(txtRazonProveedor.getText());
            pr.setId(Integer.parseInt(txtIdProveedor.getText()));
            if (!"".equals(txtIdProveedor.getText()) || !"".equals(txtNombreProveedor.getText()) || !"".equals(txtCorreoProv.getText()) || !"".equals(txtTelefonoProveedor.getText()) || !"".equals(txtDireccionProveedor.getText()) || !"".equals(txtRazonProveedor.getText()));
            PrDAO.ModificarProveedor(pr);
            LimpiarTablePro();
            LimpiarProveedor();
            ListarProveedor();
        }
        JOptionPane.showMessageDialog(null, "Actualizado correctamente");
    }//GEN-LAST:event_btnActaluzarProveedorActionPerformed

    private void btnGuardarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProveedorActionPerformed
        // TODO add your handling code here:
        if (!"".equals(txtNitProveedor.getText()) || !"".equals(txtNombreProveedor.getText()) || !"".equals(txtTelefonoProveedor.getText()) || !"".equals(txtTelefonoProveedor.getText()) || !"".equals(txtDireccionProveedor.getText()) || !"".equals(txtRazonProveedor.getText())) {
            pr.setNit(Integer.parseInt(txtNitProveedor.getText()));
            pr.setNombre(txtNombreProveedor.getText());
            pr.setTelefono(txtTelefonoProveedor.getText());
            pr.setCorreo(txtCorreoProv.getText());
            pr.setDireccion(txtDireccionProveedor.getText());
            pr.setRazon(txtRazonProveedor.getText());
            PrDAO.RegistrarProveedor(pr);
            JOptionPane.showMessageDialog(null, "Proveedor registrado");
        } else {
            JOptionPane.showMessageDialog(null, "Los campos estan vacios");
        }
    }//GEN-LAST:event_btnGuardarProveedorActionPerformed

    private void TableProveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableProveedorMouseClicked
        // TODO add your handling code here:
        int fila = TableProveedor.rowAtPoint(evt.getPoint());
        if (fila < 0 || fila >= TableProveedor.getRowCount()) {
            return;
        }
        java.util.function.Function<Object, String> safe = val -> val != null ? val.toString() : "";
        txtIdProveedor.setText(TableProveedor.getValueAt(fila, 0).toString());
        txtNitProveedor.setText(TableProveedor.getValueAt(fila, 1).toString());
        txtNombreProveedor.setText(TableProveedor.getValueAt(fila, 2).toString());
        txtTelefonoProveedor.setText(TableProveedor.getValueAt(fila, 3).toString());
        Object correo = TableProveedor.getValueAt(fila, 4);
        txtCorreoProv.setText(correo != null ? correo.toString() : "");
        txtDireccionProveedor.setText(TableProveedor.getValueAt(fila, 5).toString());
        txtRazonProveedor.setText(TableProveedor.getValueAt(fila, 6).toString());
    }//GEN-LAST:event_TableProveedorMouseClicked

    private void txtIdClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdClienteActionPerformed

    private void btnNuevoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoClienteActionPerformed
        // TODO add your handling code here:
        LimpiarCliente();
    }//GEN-LAST:event_btnNuevoClienteActionPerformed

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed
        // TODO add your handling code here:
        int fila = TableCliente.getSelectedRow();

        // 🔴 Validar si seleccionó algo
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente");
            return;
        }

        int id = Integer.parseInt(TableCliente.getModel().getValueAt(fila, 0).toString());
        int estadoActual = Integer.parseInt(TableCliente.getModel().getValueAt(fila, 6).toString());

        // Mensaje dinámico según estado actual
        String mensaje = estadoActual == 1 ? "¿Desea desactivar este cliente?" : "¿Desea activar este cliente?";

        int confirm = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (client.EliminarCliente(id)) {
                modelo.setRowCount(0);
                ListarCliente();
            } else {
                JOptionPane.showMessageDialog(null, "Error al bloquear cliente");
            }
        }
        LimpiarTable();
        LimpiarCliente();
        ListarCliente();
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void btnActualizarClientyeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarClientyeActionPerformed
        // TODO add your handling code here:
        if ("".equals(txtIdCliente.getText())) {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        } else {
            cl.setNit(Integer.parseInt(txtNitCliente.getText()));
            cl.setNombre(txtNombreCliente.getText());
            cl.setTelefono(txtTelefonoCliente.getText());
            cl.setCorreo(txtCorreo.getText());
            cl.setDireccion(txtDireccionCliente.getText());
            cl.setRazon(txtRazonCliente.getText());
            cl.setId(Integer.parseInt(txtIdCliente.getText()));
            if (!"".equals(txtIdCliente.getText()) || !"".equals(txtNombreCliente.getText()) || !"".equals(txtTelefonoCliente.getText()) || !"".equals(txtCorreo.getText()) || !"".equals(txtDireccionCliente.getText()) || !"".equals(txtRazonCliente.getText()));
            client.ModificarCliente(cl);
            LimpiarTable();
            LimpiarCliente();
            ListarCliente();
        }
        JOptionPane.showMessageDialog(null, "Actualizado correctamente");
    }//GEN-LAST:event_btnActualizarClientyeActionPerformed

    private void btnGuardarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarClienteActionPerformed
        // TODO add your handling code here:
        if (!"".equals(txtNitCliente.getText()) || !"".equals(txtNombreCliente.getText()) || !"".equals(txtTelefonoCliente.getText()) || !"".equals(txtCorreo.getText()) || !"".equals(txtDireccionCliente.getText())) {
            cl.setNit(Integer.parseInt(txtNitCliente.getText()));
            cl.setNombre(txtNombreCliente.getText());
            cl.setTelefono(txtTelefonoCliente.getText());
            cl.setDireccion(txtDireccionCliente.getText());
            cl.setRazon(txtRazonCliente.getText());
            client.RegistrarCliente(cl);
            JOptionPane.showMessageDialog(null, "¡Cliente Registrado correctamente!");
        } else {
            JOptionPane.showMessageDialog(null, "¡¡Los campos se encuentran vacios!!");
        }
        LimpiarTable();
        LimpiarCliente();
        ListarCliente();
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_btnGuardarClienteActionPerformed

    private void TableClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableClienteMouseClicked
        // TODO add your handling code here:
        int fila = TableCliente.rowAtPoint(evt.getPoint());
        txtIdCliente.setText(TableCliente.getValueAt(fila, 0).toString());
        txtNitCliente.setText(TableCliente.getValueAt(fila, 1).toString());
        txtNombreCliente.setText(TableCliente.getValueAt(fila, 2).toString());
        txtTelefonoCliente.setText(TableCliente.getValueAt(fila, 3).toString());
        Object correo = TableCliente.getValueAt(fila, 4);
        txtCorreo.setText(correo != null ? correo.toString() : "");
        txtDireccionCliente.setText(TableCliente.getValueAt(fila, 5).toString());
        txtRazonCliente.setText(TableCliente.getValueAt(fila, 6).toString());
    }//GEN-LAST:event_TableClienteMouseClicked

    private void txtIdProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdProActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdProActionPerformed

    private void btnGenerarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarVentaActionPerformed
        // TODO add your handling code here:
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de registrar la venta?",
                "Confirmar Venta",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            // 1. Selección forma de pago
            String[] formasPago = {"Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito", "Transferencia Electrónica"};
            int formaPago = JOptionPane.showOptionDialog(
                    this,
                    "¿Cómo desea realizar el pago?",
                    "Forma de Pago",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    formasPago,
                    formasPago[0]
            );

            if (formaPago == JOptionPane.CLOSED_OPTION) {
                return;
            }

            // 2. Si es efectivo, mostrar panel de cobro
            if (formaPago == 0) {
                boolean pagoOk = procesarPagoEfectivo();
                if (!pagoOk) {
                    return;
                }
            }

            // 3. Registrar venta (comentado mientras se prueban)
            //RegistrarVenta();
            //RegistrarDetalle();
            //ActualizarStock();
            // 4. Guardar PDFs automáticamente
            pdf();                          // PDF carta — guarda y abre
            //GuardarTirillaSilencioso();     // PDF tirilla — solo guarda

            // 5. Mostrar tirilla en pantalla
            ImprimirFacturaTirilla();

            LimpiarTableVenta();
            LimpiarClienteVenta();
        }
    }//GEN-LAST:event_btnGenerarVentaActionPerformed

    private void ImprimirFacturaCarta() {
        try {
            // Opción 1: Con JasperReports
            // HashMap params = new HashMap();
            // params.put("id_venta", idVentaGenerada);
            // JasperPrintManager.printReport(...);

            // Opción 2: Con PrinterJob básico
            JOptionPane.showMessageDialog(null, "Imprimiendo en hoja carta...");
            // aquí va tu lógica de impresión carta
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al imprimir: " + e.getMessage());
        }
    }

    /*private void ImprimirFacturaTirilla() {
        try {
            JOptionPane.showMessageDialog(null, "Imprimiendo tirilla térmica...");
            // aquí va tu lógica de impresión térmica
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al imprimir: " + e.getMessage());
        }
    }*/
    private void EnviarFacturaCorreo() {
        String correoDestino = txtCorreoVenta.getText().trim();

        if (correoDestino.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El cliente no tiene correo registrado");
            return; // ← sale sin crashear
        }

        try {
            // ... código de envío ...
            JOptionPane.showMessageDialog(null, "Factura enviada a: " + correoDestino);
        } catch (Exception e) {
            // Si falla el envío, muestra el error pero NO interrumpe el flujo
            JOptionPane.showMessageDialog(null,
                    "No se pudo enviar el correo: " + e.getMessage()
                    + "\nVerifica tu conexión y configuración SMTP.");
        }
    }

    private void txtNitventaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNitventaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!"".equals(txtNitventa.getText())) {
                int nit = Integer.parseInt(txtNitventa.getText());
                cl = client.Buscarcliente(nit);
                if (cl.getNombre() != null) {
                    txtNombreClienteventa.setText("" + cl.getNombre());
                    txtTelefonoCV.setText("" + cl.getTelefono());
                    txtCorreoVenta.setText("" + cl.getCorreo());
                    txtDireccionCV.setText("" + cl.getDireccion());
                    txtRazonCV.setText("" + cl.getRazon());
                } else {
                    txtNitventa.setText("");
                    JOptionPane.showMessageDialog(null, "El cliente no esta registrado");
                }
            }
        }
    }//GEN-LAST:event_txtNitventaKeyPressed

    private void txtNitventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNitventaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNitventaActionPerformed

    private void txtCantidadVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadVentaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtCantidadVenta.getText().isEmpty()
                    || txtCodigoVenta.getText().isEmpty()
                    || txtPrecioVenta.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Complete todos los campos");
                return;
            }
            try {
                String cod = txtCodigoVenta.getText();
                String descripcion = txtDescripcionVenta.getText();
                int cant = Integer.parseInt(txtCantidadVenta.getText().trim());
                double precio = Double.parseDouble(txtPrecioVenta.getText().trim());
                double total = cant * precio;
                double valorIva = ivaProducto.equals("SI") ? total * 0.19 : 0.00;
                double totalFinal = total + valorIva;

                int stock = txtStockDisponible.getText().isEmpty() ? cant
                        : Integer.parseInt(txtStockDisponible.getText().trim());
                if (stock >= cant) {
                    modelo = (DefaultTableModel) TableVenta.getModel();
                    int filaSeleccionada = TableVenta.getSelectedRow();
                    if (filaSeleccionada != -1) {
                        modelo.removeRow(filaSeleccionada);
                        item = item - 1;
                    }
                    for (int i = 0; i < TableVenta.getRowCount(); i++) {
                        if (TableVenta.getValueAt(i, 1).equals(descripcion)) {
                            JOptionPane.showMessageDialog(null, "El producto ya se encuentra registrado");
                            return;
                        }
                    }
                    item = item + 1;
                    Object[] O = new Object[7];
                    O[0] = cod;
                    O[1] = descripcion;
                    O[2] = cant;
                    O[3] = precio;
                    O[4] = valorIva;         // ✅ IVA calculado
                    O[5] = total;     // ✅ Total en columna 5
                    O[6] = totalFinal;     // ✅ Total en columna 5
                    modelo.addRow(O);
                    TableVenta.setModel(modelo);
                    actualizarCantidadCarrito();
                    TotalPagar();
                    txtCodigoVenta.setText("");
                    txtDescripcionVenta.setText("");
                    txtCantidadVenta.setText("");
                    txtPrecioVenta.setText("");
                    txtStockDisponible.setText("");
                    txtCodigoVenta.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "Stock no disponible");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingrese valores numéricos válidos");
            }
        }
    }//GEN-LAST:event_txtCantidadVentaKeyPressed

    private void actualizarCantidadCarrito() {
        int totalCantidad = 0;
        for (int i = 0; i < TableVenta.getRowCount(); i++) {
            totalCantidad += Integer.parseInt(TableVenta.getValueAt(i, 2).toString());
        }
        lblCant.setText(String.valueOf(totalCantidad));
    }

    private void txtCantidadVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadVentaActionPerformed

    private void txtCodigoVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoVentaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!"".equals(txtCodigoVenta.getText())) {
                String cod = txtCodigoVenta.getText();
                pro = proDAO.BuscarPro(cod);
                if (pro.getNombre() != null) {
                    txtDescripcionVenta.setText("" + pro.getNombre());
                    txtPrecioVenta.setText("" + pro.getPrecio());
                    txtStockDisponible.setText("" + pro.getStock());
                    ivaProducto = pro.getIva() != null ? pro.getIva() : "NO"; // ✅ asignar aquí
                    txtCantidadVenta.requestFocus();
                } else {
                    txtDescripcionVenta.setText("");
                    txtPrecioVenta.setText("");
                    txtStockDisponible.setText("");
                    txtCodigoVenta.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Ingrese el codigo del producto");
                txtCodigoVenta.requestFocus();
            }
        }
    }//GEN-LAST:event_txtCodigoVentaKeyPressed

    private void btnEliminarventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarventaActionPerformed
        // TODO add your handling code here:
        // ✅ 1. Primero obtener la fila seleccionada
        int filaSeleccionada = TableVenta.getSelectedRow();

        // ✅ 2. Validar que haya una fila seleccionada
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para eliminar");
            return;
        }

        // ✅ 3. Preguntar ANTES de eliminar
        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro que desea eliminar este producto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            // ✅ 4. Solo aquí se elimina
            modelo = (DefaultTableModel) TableVenta.getModel();
            modelo.removeRow(filaSeleccionada);
            actualizarCantidadCarrito();
            TotalPagar();
            txtCodigoVenta.requestFocus();
            JOptionPane.showMessageDialog(null, "Producto eliminado correctamente");
        } else {
            JOptionPane.showMessageDialog(null, "No se elimina");
        }
    }//GEN-LAST:event_btnEliminarventaActionPerformed

    private void TableVentaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableVentaMouseClicked
        // TODO add your handling code here:
        int fila = TableVenta.getSelectedRow();
        if (fila != -1) {
            txtCodigoVenta.setText(TableVenta.getValueAt(fila, 0).toString());
            txtDescripcionVenta.setText(TableVenta.getValueAt(fila, 1).toString());
            txtCantidadVenta.setText(TableVenta.getValueAt(fila, 2).toString());
            txtPrecioVenta.setText(TableVenta.getValueAt(fila, 3).toString());
        }
    }//GEN-LAST:event_TableVentaMouseClicked

    private void txtPrecioProKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioProKeyReleased
        // TODO add your handling code here:
        calcularIvaProducto();
    }//GEN-LAST:event_txtPrecioProKeyReleased

    private void cmbIvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIvaActionPerformed
        // TODO add your handling code here:
        calcularIvaProducto();
    }//GEN-LAST:event_cmbIvaActionPerformed

    private void btnSeleccionarLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarLogoActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imágenes", "jpg", "jpeg", "png", "gif"
        ));
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                File archivoOrigen = fileChooser.getSelectedFile();

                // ← CAMBIO: siempre guarda como logo_pdf.png con ruta absoluta
                String rutaDestino = System.getProperty("user.dir") + "/src/Img/logo_pdf.png";
                File archivoDestino = new File(rutaDestino);
                archivoDestino.getParentFile().mkdirs();
                Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                rutaLogo = "logo_pdf.png"; // ← CAMBIO: siempre el mismo nombre
                mostrarLogo(rutaLogo);

                JOptionPane.showMessageDialog(null, "Logo actualizado correctamente");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al cargar imagen: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnSeleccionarLogoActionPerformed

    private void guardarConfigLogo(String nombreArchivo) {
        try {
            String ruta = System.getProperty("user.dir") + "/src/Img/config.properties";
            System.out.println("Guardando config en: " + ruta); // DEBUG
            java.io.FileWriter fw = new java.io.FileWriter(ruta);
            fw.write("logo=" + nombreArchivo);
            fw.close();
            System.out.println("Config guardada correctamente"); // DEBUG
        } catch (IOException e) {
            System.out.println("Error al guardar config: " + e.getMessage());
        }
    }

    private String cargarConfigLogo() {
        try {
            String ruta = System.getProperty("user.dir") + "/src/Img/config.properties";
            System.out.println("Buscando config en: " + ruta); // DEBUG
            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.FileReader(ruta)
            );
            String linea = br.readLine();
            br.close();
            if (linea != null && linea.startsWith("logo=")) {
                String nombre = linea.replace("logo=", "").trim();
                System.out.println("Logo encontrado: " + nombre); // DEBUG
                return nombre;
            }
        } catch (IOException e) {
            System.out.println("No hay config guardada: " + e.getMessage());
        }
        return null;
    }

    private void btnGuardarEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarEmpresaActionPerformed
        // TODO add your handling code here:
        if (txtNitEmpresa.getText().isEmpty() || txtNombreEmpresa.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Complete los campos obligatorios");
            return;
        }

        empresa.setNit(txtNitEmpresa.getText());
        empresa.setNombre(txtNombreEmpresa.getText());
        empresa.setCorreo(txtCorreoEmpresa.getText());
        empresa.setDireccion(txtDireccionEmpresa.getText());
        empresa.setRazonSocial(txtRazonSocialEmpresa.getText());
        empresa.setTelefono(txtTelefonoEmpresa.getText());
        empresa.setLogo(rutaLogo); // ✅ guardar nombre del archivo

        empresaDAO.GuardarEmpresa(empresa);
        JOptionPane.showMessageDialog(null, "Datos guardados correctamente");
    }//GEN-LAST:event_btnGuardarEmpresaActionPerformed

    private void btnActualizarEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarEmpresaActionPerformed
        // TODO add your handling code here:
        empresa.setNit(txtNitEmpresa.getText());
        empresa.setNombre(txtNombreEmpresa.getText());
        empresa.setCorreo(txtCorreoEmpresa.getText());
        empresa.setDireccion(txtDireccionEmpresa.getText());
        empresa.setRazonSocial(txtRazonSocialEmpresa.getText());
        empresa.setTelefono(txtTelefonoEmpresa.getText());
        empresa.setLogo(rutaLogo); // ✅ actualizar nombre del archivo

        empresaDAO.ActualizarEmpresa(empresa);
        JOptionPane.showMessageDialog(null, "Datos actualizados correctamente");
    }//GEN-LAST:event_btnActualizarEmpresaActionPerformed

    private void btnExcelProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelProActionPerformed
        // TODO add your handling code here:
        Excel.reporte();
    }//GEN-LAST:event_btnExcelProActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        Excel.reporteCliente();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void btnExcelProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnExcelProveedorActionPerformed

    private void txtCodigoVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoVentaActionPerformed

    private void txtDescripcionVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescripcionVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescripcionVentaActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        Usuarios usuario = new Usuarios();
        usuario.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void menuIniciarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuIniciarSesionActionPerformed
        // TODO add your handling code here:
        if (Login.tipoUsuario != 2) {
            JOptionPane.showMessageDialog(this,
                    "Esta opción es solo para Cajeros");
            return;
        }

        // Panel para pedir monto de apertura
        JPanel panel = new JPanel(new java.awt.GridLayout(3, 1, 5, 5));
        panel.add(new JLabel("Apertura de Caja"));
        panel.add(new JLabel("¿Con cuánto dinero inicia la sesión?"));
        JTextField txtMonto = new JTextField();
        panel.add(txtMonto);

        int resultado = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Apertura de Caja",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                double montoCaja = Double.parseDouble(
                        txtMonto.getText().replace(",", ".").trim()
                );

                if (montoCaja <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "El monto debe ser mayor a cero");
                    return;
                }

                // Guardar monto de apertura
                montoCajaInicial = montoCaja;

                // Habilitar botón Ventas
                jButton5.setEnabled(true);

                // Deshabilitar opción Iniciar Sesión y habilitar Cerrar Sesión
                menuIniciarSesion.setEnabled(false);
                menuCerrarSesion.setEnabled(true);

                JOptionPane.showMessageDialog(this,
                        String.format("Caja abierta con: $ %,.2f%n¡Bienvenido %s!",
                                montoCaja, Login.nombreUsuario));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Ingrese un valor numérico válido");
            }
        }
    }//GEN-LAST:event_menuIniciarSesionActionPerformed

    private void menuCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCerrarSesionActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cerrar la sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Limpiar datos de sesión
            Login.tipoUsuario = 0;
            Login.nombreUsuario = "";
            montoCajaInicial = 0;

            // Abrir Login
            Login loginForm = new Login();
            loginForm.setVisible(true);

            // Cerrar Sistema
            dispose();
        }
    }//GEN-LAST:event_menuCerrarSesionActionPerformed

    private void menuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSalirActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir?",
                "Salir",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Limpiar datos de sesión
            Login.tipoUsuario = 0;
            Login.nombreUsuario = "";
            montoCajaInicial = 0;

            // Abrir Login
            Login loginForm = new Login();
            loginForm.setVisible(true);

            // Cerrar Sistema
            dispose();
        }
    }//GEN-LAST:event_menuSalirActionPerformed

    private void txtRazonSocialEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRazonSocialEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRazonSocialEmpresaActionPerformed

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir?",
                "Salir",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Login.tipoUsuario = 0;
            Login.nombreUsuario = "";
            montoCajaInicial = 0;

            Login loginForm = new Login();
            loginForm.setVisible(true);
            dispose();
        }
    }

    private String rutaLogo = "";

    public void ListarProveedor() {

        List<Proveedor> ListaPr = PrDAO.ListarProveedor();
        modelo = (DefaultTableModel) TableProveedor.getModel();
        modelo.setRowCount(0);

        Object[] ob = new Object[8]; // 👈 ahora son 7

        for (int i = 0; i < ListaPr.size(); i++) {
            ob[0] = ListaPr.get(i).getId();
            ob[1] = ListaPr.get(i).getNit();
            ob[2] = ListaPr.get(i).getNombre();
            ob[3] = ListaPr.get(i).getTelefono();
            ob[4] = ListaPr.get(i).getCorreo();
            ob[5] = ListaPr.get(i).getDireccion();
            ob[6] = ListaPr.get(i).getRazon();
            ob[7] = ListaPr.get(i).getEstado(); // 👈 IMPORTANTE

            modelo.addRow(ob);
        }

        TableProveedor.setModel(modelo);

        aplicarColorEstadoPr(); // lo llamas desde aquí

        // Activa o desactiva los botones según sea el estado del cliente
    }

    // Color del listado de los clientes si estan en estado activo o no
    private void aplicarColorEstadoPr() {
        TableProveedor.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Object estado = table.getModel().getValueAt(row, 7);

                if (!isSelected) {
                    int estadoVal = Integer.parseInt(estado.toString().trim());

                    if (estadoVal == 1) {
                        c.setBackground(new Color(232, 255, 234)); // Verde suave - Activo
                        c.setForeground(new Color(0, 120, 0));     // Texto verde oscuro
                    } else {
                        c.setBackground(new Color(189, 189, 189)); // Gris - Inactivo
                        c.setForeground(new Color(245, 73, 39)); // Texto gris oscuro
                    }
                }
                return c;
            }
        });

        // Ocultar el texlabel ID
        TableProveedor.getColumnModel().getColumn(0).setMinWidth(0);
        TableProveedor.getColumnModel().getColumn(0).setMaxWidth(0);
        TableProveedor.getColumnModel().getColumn(0).setPreferredWidth(0);

        // ocultar la columna de estado (columna 6)
        TableProveedor.getColumnModel().getColumn(6).setMinWidth(0);
        TableProveedor.getColumnModel().getColumn(6).setMaxWidth(0);
        TableProveedor.getColumnModel().getColumn(6).setPreferredWidth(0);
    }

    public void ListarProductos() {

        List<Productos> ListaPro = proDAO.ListarProductos();
        modelo = (DefaultTableModel) TableProducto.getModel();
        modelo.setRowCount(0); // ✅ limpiar tabla antes de listar
        Object[] ob = new Object[10];
        for (int i = 0; i < ListaPro.size(); i++) {
            ob[0] = ListaPro.get(i).getId();
            ob[1] = ListaPro.get(i).getCodigo();
            ob[2] = ListaPro.get(i).getNombre();
            ob[3] = ListaPro.get(i).getProveedor();
            ob[4] = ListaPro.get(i).getStock();
            ob[5] = ListaPro.get(i).getPrecio();
            ob[6] = ListaPro.get(i).getIva() != null ? ListaPro.get(i).getIva() : "NO"; // ✅ getIva() no getValorIva()
            ob[7] = ListaPro.get(i).getValorIva();
            ob[8] = ListaPro.get(i).getPrecioFinal();
            ob[9] = ListaPro.get(i).getEstado();
            modelo.addRow(ob);
        }

        TableProducto.setModel(modelo);

        aplicarColorEstadoPro(); // lo llamas desde aquí

        // Activa o desactiva los botones según sea el estado del cliente
        TableProducto.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && TableProducto.getSelectedRow() != -1) {

                int fila = TableProducto.getSelectedRow();
                Object estado = TableProducto.getValueAt(fila, 9);

                int estadoVal = Integer.parseInt(estado.toString().trim());

                if (estadoVal == 1) {
                    btnEliminarPro.setEnabled(true);    // Boton Activo
                    btnGuardarPro.setEnabled(true);     // Boton Activo
                    btnNuevoPro.setEnabled(true);       // Boton Activo
                    btnActualizarpro.setEnabled(true); // Boton Activo
                } else {
                    btnEliminarPro.setEnabled(true);    // Boton Activo
                    btnGuardarPro.setEnabled(false);    // Boton inactivo
                    btnNuevoPro.setEnabled(false);      // Boton inactivo
                    btnActualizarpro.setEnabled(false);// Boton inactivo
                }
            }
        });
    }

    public void ListarVentas() {

        List<Venta> ListaVenta = vDAO.ListarVentas();
        modelo = (DefaultTableModel) TableVentas.getModel();

        modelo.setRowCount(0); // 👈 LIMPIAR TABLA

        Object[] ob = new Object[4];

        for (int i = 0; i < ListaVenta.size(); i++) {
            ob[0] = ListaVenta.get(i).getId();
            ob[1] = ListaVenta.get(i).getCliente();
            ob[2] = ListaVenta.get(i).getVendedor();
            ob[3] = ListaVenta.get(i).getTotal();
            modelo.addRow(ob);
        }

        TableVentas.setModel(modelo);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Sistema.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sistema.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sistema.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sistema.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Sistema().setVisible(true);
            }
        });

    }

    private void aplicarColorEstadoPro() {
        TableProducto.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Object estado = table.getModel().getValueAt(row, 9);

                if (!isSelected) {
                    int estadoVal = Integer.parseInt(estado.toString().trim());

                    if (estadoVal == 1) {
                        c.setBackground(new Color(232, 255, 234)); // Verde suave - Activo
                        c.setForeground(new Color(0, 120, 0));     // Texto verde oscuro
                    } else {
                        c.setBackground(new Color(189, 189, 189)); // Gris - Inactivo
                        c.setForeground(new Color(245, 73, 39)); // Texto gris oscuro
                    }
                }
                return c;
            }
        });

        // Ocultar el texlabel ID
        TableProducto.getColumnModel().getColumn(0).setMinWidth(0);
        TableProducto.getColumnModel().getColumn(0).setMaxWidth(0);
        TableProducto.getColumnModel().getColumn(0).setPreferredWidth(0);

        // ocultar la columna de estado (columna 9)
        TableProducto.getColumnModel().getColumn(9).setMinWidth(0);
        TableProducto.getColumnModel().getColumn(9).setMaxWidth(0);
        TableProducto.getColumnModel().getColumn(9).setPreferredWidth(0);
    }

    private void calcularIvaProducto() {
        try {
            double precio = Double.parseDouble(txtPrecioPro.getText().trim());
            String tieneIva = cmbIva.getSelectedItem().toString();

            if (tieneIva.equals("SI")) {
                double iva = precio * 0.19;
                double precioConIva = precio + iva;
                lblIvaProducto.setText(String.format("%.2f", iva));
                lblPrecioConIva.setText(String.format("%.2f", precioConIva));
            } else {
                lblIvaProducto.setText("0.00");
                lblPrecioConIva.setText(String.format("%.2f", precio));
            }
        } catch (NumberFormatException e) {
            lblIvaProducto.setText("0.00");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelLogo;
    private javax.swing.JLabel LabelTotal;
    private javax.swing.JLabel LabelVendedor;
    private javax.swing.JTable TableCliente;
    private javax.swing.JTable TableProducto;
    private javax.swing.JTable TableProveedor;
    private javax.swing.JTable TableVenta;
    private javax.swing.JTable TableVentas;
    private javax.swing.JButton btnActaluzarProveedor;
    private javax.swing.JButton btnActualizarClientye;
    private javax.swing.JButton btnActualizarEmpresa;
    private javax.swing.JButton btnActualizarpro;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnEliminarPro;
    private javax.swing.JButton btnEliminarProveedor;
    private javax.swing.JButton btnEliminarventa;
    private javax.swing.JButton btnExcelPro;
    private javax.swing.JButton btnExcelProveedor;
    private javax.swing.JButton btnGenerarVenta;
    private javax.swing.JButton btnGuardarCliente;
    private javax.swing.JButton btnGuardarEmpresa;
    private javax.swing.JButton btnGuardarPro;
    private javax.swing.JButton btnGuardarProveedor;
    private javax.swing.JButton btnNuevoCliente;
    private javax.swing.JButton btnNuevoPro;
    private javax.swing.JButton btnNuevoProveedor;
    private javax.swing.JButton btnPdfVentas;
    private javax.swing.JButton btnSeleccionarLogo;
    private javax.swing.JButton btnVerVenta;
    private javax.swing.JComboBox<String> cbxProveedorPro;
    private javax.swing.JComboBox<String> cmbIva;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCant;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblIva;
    private javax.swing.JTextField lblIvaProducto;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JTextField lblPrecioConIva;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JMenuItem menuCerrarSesion;
    private javax.swing.JMenuItem menuIniciarSesion;
    private javax.swing.JMenuItem menuSalir;
    private javax.swing.JTextField txtCantidadVenta;
    private javax.swing.JTextField txtCodigoPro;
    private javax.swing.JTextField txtCodigoVenta;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtCorreoEmpresa;
    private javax.swing.JTextField txtCorreoProv;
    private javax.swing.JTextField txtCorreoVenta;
    private javax.swing.JTextField txtDesPro;
    private javax.swing.JTextField txtDescripcionVenta;
    private javax.swing.JTextField txtDireccionCV;
    private javax.swing.JTextField txtDireccionCliente;
    private javax.swing.JTextField txtDireccionEmpresa;
    private javax.swing.JTextField txtDireccionProveedor;
    private javax.swing.JTextField txtIdCliente;
    private javax.swing.JTextField txtIdPro;
    private javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtIdVenta;
    private javax.swing.JTextField txtIdpro;
    private javax.swing.JTextField txtNitCliente;
    private javax.swing.JTextField txtNitEmpresa;
    private javax.swing.JTextField txtNitProveedor;
    private javax.swing.JTextField txtNitventa;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtNombreClienteventa;
    private javax.swing.JTextField txtNombreEmpresa;
    private javax.swing.JTextField txtNombreProveedor;
    private javax.swing.JTextField txtPrecioPro;
    private javax.swing.JTextField txtPrecioVenta;
    private javax.swing.JTextField txtRazonCV;
    private javax.swing.JTextField txtRazonCliente;
    private javax.swing.JTextField txtRazonProveedor;
    private javax.swing.JTextField txtRazonSocialEmpresa;
    private javax.swing.JTextField txtStockDisponible;
    private javax.swing.JTextField txtTelefonoCV;
    private javax.swing.JTextField txtTelefonoCliente;
    private javax.swing.JTextField txtTelefonoEmpresa;
    private javax.swing.JTextField txtTelefonoProveedor;
    private javax.swing.JTextField txtcantPro;
    // End of variables declaration//GEN-END:variables

    //private void TotalPagar(){
    //Totalpagar = 0,00;
    //int numFila = TotalVenta.getRowCount();
    //for (int i = 0; i < numFila; i++){
    //double cal = Double.parseDouble(String.valueOf(TableVenta.getModel().getValueAt(i, 4)));
    //Totalpagar = Totalpagar + cal;
    //}
    //LabelTotal.setText(String.format("%.2f", Totalpagar) 
    //}
    private void LimpiarCliente() {
        txtIdCliente.setText("");
        txtNitCliente.setText("");
        txtNombreCliente.setText("");
        txtTelefonoCliente.setText("");
        txtCorreo.setText("");
        txtDireccionCliente.setText("");
        txtRazonCliente.setText("");
    }

    private void LimpiarProveedor() {
        txtIdProveedor.setText("");
        txtNitProveedor.setText("");
        txtNombreProveedor.setText("");
        txtTelefonoProveedor.setText("");
        txtCorreoProv.setText("");
        txtDireccionProveedor.setText("");
        txtRazonProveedor.setText("");
    }

    private void LimpiarProductos() {
        txtIdpro.setText("");
        txtCodigoPro.setText("");
        txtDesPro.setText("");
        cbxProveedorPro.setSelectedItem("");
        txtcantPro.setText("");
        txtPrecioPro.setText("");

    }

    private void TotalPagar() {
        double subTotal = 0.00;
        double totalIva = 0.00;
        int numFila = TableVenta.getRowCount();

        for (int i = 0; i < numFila; i++) {
            double iva = Double.parseDouble(String.valueOf(TableVenta.getModel().getValueAt(i, 4)));    // columna 4 = IVA
            double totalProducto = Double.parseDouble(String.valueOf(TableVenta.getModel().getValueAt(i, 5))); // columna 5 = Total
            subTotal += totalProducto;
            totalIva += iva;
        }

        double totalPagar = subTotal + totalIva;
        lblSubTotal.setText(String.format("%.2f", subTotal));
        lblIva.setText(String.format("%.2f", totalIva));
        LabelTotal.setText(String.format("%.2f", totalPagar));
        Totalpagar = totalPagar;
    }

    private void RegistrarVenta() {
        String cliente = txtNombreClienteventa.getText();
        String vendedor = LabelVendedor.getText();
        double monto = Totalpagar;
        v.setCliente(cliente);
        v.setVendedor(vendedor);
        v.setTotal(monto);
        vDAO.RegistrarVenta(v);
    }

    private void RegistrarDetalle() {
        int id = vDAO.IdVenta();
        for (int i = 0; i < TableVenta.getRowCount(); i++) {
            String cod = TableVenta.getValueAt(i, 0).toString();
            int cant = Integer.parseInt(TableVenta.getValueAt(i, 2).toString());
            double precio = Double.parseDouble(TableVenta.getValueAt(i, 3).toString());
            Dv.setCod_pro(cod);
            Dv.setCantidad(cant);
            Dv.setPrecio(precio);
            Dv.setId(id);
            vDAO.RegistrarDetalle(Dv);
        }
    }

    private void ActualizarStock() {
        for (int i = 0; i < TableVenta.getRowCount(); i++) {
            String cod = TableVenta.getValueAt(i, 0).toString();
            int cant = Integer.parseInt(TableVenta.getValueAt(i, 2).toString());
            pro = proDAO.BuscarPro(cod);
            int StockActual = pro.getStock() - cant;
            vDAO.ActualizarStock(StockActual, cod);
        }
    }

    private void LimpiarTableVenta() {
        modelo = (DefaultTableModel) TableVenta.getModel();
        modelo.setRowCount(0); // ← elimina todas las filas de una vez
    }

    private void LimpiarClienteVenta() {
        txtNitventa.setText("");
        txtNombreClienteventa.setText("");
        txtTelefonoCV.setText("");
        txtCorreoVenta.setText("");
        txtDireccionCV.setText("");
        txtRazonCV.setText("");
    }

    private void pdf() {
        try {
            System.out.println("rutaLogo = " + rutaLogo);
            System.out.println("user.dir = " + System.getProperty("user.dir"));
            System.out.println("Ruta completa = " + System.getProperty("user.dir") + "/src/Img/" + rutaLogo);

            File carpeta = new File("src/pdf/");
            carpeta.mkdirs();

            File file = new File("src/pdf/venta.pdf");
            FileOutputStream archivo = new FileOutputStream(file);
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, archivo);
            PiePagina evento = new PiePagina();
            writer.setPageEvent(evento);
            doc.open();

            // Logo
            com.itextpdf.text.Image img = null;
            String rutaImagenLogo = System.getProperty("user.dir") + "/src/Img/logo_pdf.png";
            File archivoLogo = new File(rutaImagenLogo);

            System.out.println("Buscando logo en: " + rutaImagenLogo); // DEBUG
            System.out.println("Existe: " + archivoLogo.exists()); // DEBUG

            if (archivoLogo.exists()) {
                img = com.itextpdf.text.Image.getInstance(rutaImagenLogo);
                img.scaleToFit(100, 80);
            }
            com.itextpdf.text.Font negrita = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12,
                    com.itextpdf.text.Font.BOLD,
                    com.itextpdf.text.BaseColor.BLUE
            );
            com.itextpdf.text.Font normal = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11,
                    com.itextpdf.text.Font.NORMAL
            );

            // Fecha y número de factura
            Paragraph fecha = new Paragraph();
            Date date = new Date();
            fecha.add(new Chunk("Factura: 1\n", negrita));
            fecha.add(new Chunk("Fecha: "
                    + new SimpleDateFormat("dd-MM-yyyy").format(date) + "\n\n", normal));

            // Tabla encabezado
            PdfPTable encabezado = new PdfPTable(4);
            encabezado.setWidthPercentage(100);
            encabezado.getDefaultCell().setBorder(0);
            float[] columnaEncabezado = new float[]{30f, 30f, 70f, 40f};
            encabezado.setWidths(columnaEncabezado);
            encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);

            if (img != null) {
                com.itextpdf.text.pdf.PdfPCell celdaLogo
                        = new com.itextpdf.text.pdf.PdfPCell(img, true);
                celdaLogo.setBorder(0);
                encabezado.addCell(celdaLogo);
            } else {
                com.itextpdf.text.pdf.PdfPCell celdaVacia
                        = new com.itextpdf.text.pdf.PdfPCell(new Phrase(""));
                celdaVacia.setBorder(0);
                encabezado.addCell(celdaVacia);
            }

            // Datos del encabezado
            String nit = txtNitEmpresa.getText();
            String nom = txtNombreEmpresa.getText();
            String tel = txtTelefonoEmpresa.getText();
            String dir = txtDireccionEmpresa.getText();
            String ra = txtRazonSocialEmpresa.getText();
            String co = txtCorreoEmpresa.getText();

            encabezado.addCell("");
            encabezado.addCell("Nit: " + nit
                    + "\nNombre: " + nom
                    + "\nTelefono: " + tel
                    + "\nDireccion: " + dir
                    + "\nRazon: " + ra
                    + "\nCorreo: " + co);
            encabezado.addCell(fecha);
            doc.add(encabezado);

            // Datos del cliente
            Paragraph cli = new Paragraph();
            cli.add(Chunk.NEWLINE);
            cli.add("Datos del cliente" + "\n\n");
            doc.add(cli);

            PdfPTable tablacli = new PdfPTable(5);
            tablacli.setWidthPercentage(100);
            tablacli.getDefaultCell().setBorder(0);
            float[] columnacli = new float[]{20f, 50f, 20f, 50f, 50f};
            tablacli.setWidths(columnacli);
            tablacli.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell cl1 = new PdfPCell(new Phrase("Nit"));
            PdfPCell cl2 = new PdfPCell(new Phrase("Nombre"));
            PdfPCell cl3 = new PdfPCell(new Phrase("Telefono"));
            PdfPCell cl4 = new PdfPCell(new Phrase("Direccion"));
            PdfPCell cl5 = new PdfPCell(new Phrase("Correo"));
            cl1.setBorder(0);
            cl2.setBorder(0);
            cl3.setBorder(0);
            cl4.setBorder(0);
            cl5.setBorder(0);
            cl1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cl2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cl3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cl4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cl5.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tablacli.addCell(cl1);
            tablacli.addCell(cl2);
            tablacli.addCell(cl3);
            tablacli.addCell(cl4);
            tablacli.addCell(cl5);
            tablacli.addCell(txtNitventa.getText());
            tablacli.addCell(txtNombreClienteventa.getText());
            tablacli.addCell(txtTelefonoCV.getText());
            tablacli.addCell(txtDireccionCV.getText());
            tablacli.addCell(txtCorreoVenta.getText());

            doc.add(tablacli);

            //Productos
            Paragraph pro = new Paragraph();
            pro.add(Chunk.NEWLINE);
            pro.add("Datos de los Productos" + "\n\n");
            doc.add(pro);

            PdfPTable tablapro = new PdfPTable(6);
            tablapro.setWidthPercentage(100);
            tablapro.getDefaultCell().setBorder(0);
            float[] columnapro = new float[]{10f, 30f, 20f, 20f, 30f, 30f};
            tablapro.setWidths(columnapro);
            tablapro.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell pro1 = new PdfPCell(new Phrase("Cant", negrita));
            PdfPCell pro2 = new PdfPCell(new Phrase("Descripcion", negrita));
            PdfPCell pro3 = new PdfPCell(new Phrase("Precio unitario", negrita));
            PdfPCell pro4 = new PdfPCell(new Phrase("IVA", negrita));
            PdfPCell pro5 = new PdfPCell(new Phrase("Precio Total", negrita));
            PdfPCell pro6 = new PdfPCell(new Phrase("Valor Total", negrita));
            pro1.setBorder(0);
            pro2.setBorder(0);
            pro3.setBorder(0);
            pro4.setBorder(0);
            pro5.setBorder(0);
            pro6.setBorder(0);
            pro1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pro2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pro3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pro4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pro5.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pro6.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tablapro.addCell(pro1);
            tablapro.addCell(pro2);
            tablapro.addCell(pro3);
            tablapro.addCell(pro4);
            tablapro.addCell(pro5);
            tablapro.addCell(pro6);
            if (TableVenta.getRowCount() > 0) {
                for (int j = 0; j < TableVenta.getColumnCount(); j++) {
                    System.out.println("Columna " + j + ": " + TableVenta.getValueAt(0, j));
                }
            }
            for (int i = 0; i < TableVenta.getRowCount(); i++) {
                String cantidad = TableVenta.getValueAt(i, 2).toString();
                String producto = TableVenta.getValueAt(i, 1).toString();
                String precio = TableVenta.getValueAt(i, 3).toString();
                String iva = TableVenta.getValueAt(i, 4).toString();
                String total = TableVenta.getValueAt(i, 5).toString();
                String totalFinal = TableVenta.getValueAt(i, 6).toString();
                tablapro.addCell(cantidad);
                tablapro.addCell(producto);
                tablapro.addCell(precio);
                tablapro.addCell(iva);
                tablapro.addCell(total);
                tablapro.addCell(totalFinal);

            }
            doc.add(tablapro);
            tablapro.addCell(txtNitventa.getText());
            tablapro.addCell(txtNombreClienteventa.getText());
            tablapro.addCell(txtTelefonoCV.getText());
            tablapro.addCell(txtDireccionCV.getText());
            tablapro.addCell(txtCorreoVenta.getText());

            /*Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE);
            com.itextpdf.text.Font fuenteTotal = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 13,
                    com.itextpdf.text.Font.BOLD,
                    com.itextpdf.text.BaseColor.BLACK
            );
            info.add(new Chunk("Total a Pagar: $ " + LabelTotal.getText(), fuenteTotal));
            info.setAlignment(Element.ALIGN_RIGHT);
            doc.add(info);*/
            com.itextpdf.text.Font fuenteTotal = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11,
                    com.itextpdf.text.Font.BOLD,
                    com.itextpdf.text.BaseColor.BLACK
            );

            Paragraph separador = new Paragraph();
            separador.add(Chunk.NEWLINE);
            separador.add(Chunk.NEWLINE); // ← agrega más NEWLINE si quieres más espacio
            doc.add(separador);

            // Misma cantidad de columnas que tablapro (5 columnas)
            PdfPTable tablaTotal = new PdfPTable(6);
            tablaTotal.setWidthPercentage(100);
            float[] anchoTotal = new float[]{10f, 30f, 20f, 20f, 30f, 30f}; // ← mismos anchos que tablapro
            tablaTotal.setWidths(anchoTotal);

            // Columnas vacías (cant, descripcion, precio unitario)
            PdfPCell vacia1 = new PdfPCell(new Phrase(""));
            PdfPCell vacia2 = new PdfPCell(new Phrase(""));
            PdfPCell vacia3 = new PdfPCell(new Phrase(""));
            PdfPCell vacia4 = new PdfPCell(new Phrase(""));
            vacia1.setBorder(0);
            vacia2.setBorder(0);
            vacia3.setBorder(0);
            vacia4.setBorder(0);

            // Celda "Total a Pagar:" debajo de columna IVA/Precio Total
            PdfPCell celdaEtiqueta = new PdfPCell(new Phrase("Total a Pagar:", fuenteTotal));
            celdaEtiqueta.setBorder(0);
            celdaEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Celda con el valor debajo de columna "Valor Total"
            PdfPCell celdaValor = new PdfPCell(new Phrase("$ " + LabelTotal.getText(), fuenteTotal));
            celdaValor.setBorder(0);
            celdaValor.setHorizontalAlignment(Element.ALIGN_LEFT);

            tablaTotal.addCell(vacia1);
            tablaTotal.addCell(vacia2);
            tablaTotal.addCell(vacia3);
            tablaTotal.addCell(vacia4);
            tablaTotal.addCell(celdaEtiqueta);
            tablaTotal.addCell(celdaValor);

            doc.add(tablaTotal);

            Paragraph firma = new Paragraph();
            firma.add(Chunk.NEWLINE);
            firma.add("Firma de cancelado\n\n");
            firma.add("__________________");
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);

            Paragraph mensaje = new Paragraph();
            mensaje.add(Chunk.NEWLINE);
            mensaje.add("Gracias por su compra");
            mensaje.setAlignment(Element.ALIGN_CENTER);
            doc.add(mensaje);

            /*Paragraph pie = new Paragraph();
            pie.add(Chunk.NEWLINE);
            pie.add("Desarrollado por Gustavo Celis ©");
            pie.setAlignment(Element.ALIGN_CENTER);
            doc.add(pie);*/
            doc.close();
            archivo.close();

            // Abrir el PDF automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());

        }
    }

    // Agrega esta clase ANTES del método pdf(), dentro de la clase Sistema
    class PiePagina extends com.itextpdf.text.pdf.PdfPageEventHelper {

        @Override
        public void onEndPage(com.itextpdf.text.pdf.PdfWriter writer, Document document) {
            try {
                com.itextpdf.text.Font fuentePie = new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 9,
                        com.itextpdf.text.Font.ITALIC,
                        com.itextpdf.text.BaseColor.GRAY
                );

                // Tabla para el pie de página
                PdfPTable tablaPie = new PdfPTable(1);
                tablaPie.setTotalWidth(document.getPageSize().getWidth()
                        - document.leftMargin()
                        - document.rightMargin());

                PdfPCell celda = new PdfPCell(new Phrase("Desarrollado por Gustavo Celis ©", fuentePie));
                celda.setBorder(0);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setBorderWidthTop(1f); // línea separadora arriba del pie
                tablaPie.addCell(celda);

                // Posición fija en el pie de cada página
                tablaPie.writeSelectedRows(
                        0, -1,
                        document.leftMargin(),
                        document.bottomMargin(), // ← altura fija desde abajo
                        writer.getDirectContent()
                );

            } catch (Exception e) {
                System.out.println("Error pie de página: " + e.getMessage());
            }
        }
    }

    private void ImprimirFacturaTirilla() {
        try {
            File carpeta = new File("src/pdf/");
            carpeta.mkdirs();

            File file = new File("src/pdf/tirilla.pdf");
            FileOutputStream archivo = new FileOutputStream(file);

            // Tamaño tirilla 80mm de ancho
            com.itextpdf.text.Rectangle tamañoTirilla
                    = new com.itextpdf.text.Rectangle(226, 800); // 80mm en puntos
            Document doc = new Document(tamañoTirilla, 5, 5, 10, 10);
            PdfWriter.getInstance(doc, archivo);
            doc.open();

            com.itextpdf.text.Font titulo = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.COURIER, 9,
                    com.itextpdf.text.Font.BOLD
            );
            com.itextpdf.text.Font normal = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.COURIER, 8,
                    com.itextpdf.text.Font.NORMAL
            );
            com.itextpdf.text.Font pequeña = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.COURIER, 7,
                    com.itextpdf.text.Font.NORMAL
            );

            // LOGO
            try {
                String rutaImagenLogo = System.getProperty("user.dir") + "/src/Img/logo_pdf.png";
                File archivoLogo = new File(rutaImagenLogo);

                if (archivoLogo.exists()) {
                    com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(rutaImagenLogo);

                    // Ajustar al ancho de la tirilla (IMPORTANTE)
                    img.scaleToFit(100, 50); // ancho máximo recomendado
                    img.setAlignment(Element.ALIGN_CENTER);

                    doc.add(img);
                } else {
                    System.out.println("No se encontró el logo en: " + rutaImagenLogo);
                }
            } catch (Exception e) {
                System.out.println("Error cargando logo: " + e.getMessage());
            }

            // Encabezado empresa
            Paragraph encabezado = new Paragraph();
            encabezado.setAlignment(Element.ALIGN_CENTER);
            encabezado.add(new Chunk(txtNombreEmpresa.getText() + "\n", titulo));
            encabezado.add(new Chunk(txtDireccionEmpresa.getText() + "\n", normal));
            encabezado.add(new Chunk("Tel: " + txtTelefonoEmpresa.getText() + "\n", normal));
            encabezado.add(new Chunk("NIT: " + txtNitEmpresa.getText() + "\n", normal));
            doc.add(encabezado);

            // Separador
            doc.add(new Paragraph("--------------------------------", pequeña));

            // Fecha y factura
            Paragraph info = new Paragraph();
            info.setAlignment(Element.ALIGN_LEFT);
            Date date = new Date();
            info.add(new Chunk("Fecha: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date) + "\n", normal));
            info.add(new Chunk("Cliente: " + txtNombreClienteventa.getText() + "\n", normal));
            info.add(new Chunk("NIT/CC: " + txtNitventa.getText() + "\n", normal));
            doc.add(info);

            // Separador
            doc.add(new Paragraph("--------------------------------", pequeña));

            // Encabezado productos
            PdfPTable tablapro = new PdfPTable(4);
            tablapro.setWidthPercentage(100);
            tablapro.getDefaultCell().setBorder(0);
            float[] anchos = new float[]{8f, 30f, 15f, 20f};
            tablapro.setWidths(anchos);

            PdfPCell h1 = new PdfPCell(new Phrase("Cnt", titulo));
            PdfPCell h2 = new PdfPCell(new Phrase("Descripcion", titulo));
            PdfPCell h3 = new PdfPCell(new Phrase("P.U", titulo));
            PdfPCell h4 = new PdfPCell(new Phrase("Total", titulo));
            h1.setBorder(0);
            h2.setBorder(0);
            h3.setBorder(0);
            h4.setBorder(0);
            tablapro.addCell(h1);
            tablapro.addCell(h2);
            tablapro.addCell(h3);
            tablapro.addCell(h4);

            // Filas productos
            for (int i = 0; i < TableVenta.getRowCount(); i++) {
                String cant = TableVenta.getValueAt(i, 2).toString();
                String desc = TableVenta.getValueAt(i, 1).toString();
                String precio = TableVenta.getValueAt(i, 3).toString();
                String total = TableVenta.getValueAt(i, 5).toString();

                PdfPCell c1 = new PdfPCell(new Phrase(cant, normal));
                PdfPCell c2 = new PdfPCell(new Phrase(desc, normal));
                PdfPCell c3 = new PdfPCell(new Phrase(precio, normal));
                PdfPCell c4 = new PdfPCell(new Phrase(total, normal));
                c1.setBorder(0);
                c2.setBorder(0);
                c3.setBorder(0);
                c4.setBorder(0);
                tablapro.addCell(c1);
                tablapro.addCell(c2);
                tablapro.addCell(c3);
                tablapro.addCell(c4);
            }
            doc.add(tablapro);

            // Separador
            doc.add(new Paragraph("--------------------------------", pequeña));

// Totales alineados con las columnas de productos
            PdfPTable tablaTotales = new PdfPTable(4);
            tablaTotales.setWidthPercentage(100);
            float[] anchosTotales = new float[]{8f, 20f, 20f, 20f};
            tablaTotales.setWidths(anchosTotales);

// Fila SubTotal
            PdfPCell vacio1 = new PdfPCell(new Phrase("", normal));
            PdfPCell vacio2 = new PdfPCell(new Phrase("", normal));
            PdfPCell labelSub = new PdfPCell(new Phrase("SubTotal: $", normal));
            PdfPCell valorSub = new PdfPCell(new Phrase(lblSubTotal.getText(), normal));
            vacio1.setBorder(0);
            vacio2.setBorder(0);
            labelSub.setBorder(0);
            valorSub.setBorder(0);
            labelSub.setHorizontalAlignment(Element.ALIGN_LEFT);
            valorSub.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.addCell(vacio1);
            tablaTotales.addCell(vacio2);
            tablaTotales.addCell(labelSub);
            tablaTotales.addCell(valorSub);

// Fila IVA
            PdfPCell vacio3 = new PdfPCell(new Phrase("", normal));
            PdfPCell vacio4 = new PdfPCell(new Phrase("", normal));
            PdfPCell labelIva = new PdfPCell(new Phrase("IVA:      $", normal));
            PdfPCell valorIva = new PdfPCell(new Phrase(lblIva.getText(), normal));
            vacio3.setBorder(0);
            vacio4.setBorder(0);
            labelIva.setBorder(0);
            valorIva.setBorder(0);
            labelIva.setHorizontalAlignment(Element.ALIGN_LEFT);
            valorIva.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.addCell(vacio3);
            tablaTotales.addCell(vacio4);
            tablaTotales.addCell(labelIva);
            tablaTotales.addCell(valorIva);

// Fila TOTAL
            PdfPCell vacio5 = new PdfPCell(new Phrase("", normal));
            PdfPCell vacio6 = new PdfPCell(new Phrase("", normal));
            PdfPCell labelTot = new PdfPCell(new Phrase("TOTAL:   $", titulo));
            PdfPCell valorTot = new PdfPCell(new Phrase(LabelTotal.getText(), titulo));
            vacio5.setBorder(0);
            vacio6.setBorder(0);
            labelTot.setBorder(0);
            valorTot.setBorder(0);
            labelTot.setHorizontalAlignment(Element.ALIGN_LEFT);
            valorTot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.addCell(vacio5);
            tablaTotales.addCell(vacio6);
            tablaTotales.addCell(labelTot);
            tablaTotales.addCell(valorTot);

            doc.add(tablaTotales);

            // Separador
            doc.add(new Paragraph("--------------------------------", pequeña));

            // Pie
            Paragraph pie = new Paragraph();
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.add(new Chunk("\nGracias por su compra\n", titulo));
            pie.add(new Chunk("Desarrollado por Gustavo Celis ©\n", pequeña));
            doc.add(pie);

            doc.close();
            archivo.close();

            // Abrir tirilla automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al imprimir tirilla: " + e.getMessage());
        }
    }

    private boolean procesarPagoEfectivo() {
        final double[] totalVenta = {0};
        try {
            String totalStr = LabelTotal.getText().replace(",", ".").replace("$", "").trim();
            totalVenta[0] = Double.parseDouble(totalStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el total de la venta.");
            return false;
        }

        JPanel panelEfectivo = new JPanel(new java.awt.GridLayout(4, 1, 5, 5));
        panelEfectivo.add(new JLabel("Total a pagar:  $ " + LabelTotal.getText()));
        panelEfectivo.add(new JLabel("Ingrese el valor recibido:"));
        JTextField txtPago = new JTextField();
        panelEfectivo.add(txtPago);
        JLabel lblCambioPanel = new JLabel("Cambio: $ 0,00");
        lblCambioPanel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        lblCambioPanel.setForeground(new java.awt.Color(0, 128, 0));
        panelEfectivo.add(lblCambioPanel);

        txtPago.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void actualizar() {
                try {
                    double pagado = Double.parseDouble(txtPago.getText().replace(",", ".").trim());
                    double cambio = pagado - totalVenta[0];
                    if (cambio >= 0) {
                        lblCambioPanel.setText(String.format("Cambio: $ %,.2f", cambio));
                        lblCambioPanel.setForeground(new java.awt.Color(0, 128, 0));
                    } else {
                        lblCambioPanel.setText(String.format("Faltan: $ %,.2f", Math.abs(cambio)));
                        lblCambioPanel.setForeground(java.awt.Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    lblCambioPanel.setText("Cambio: $ 0,00");
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }
        });

        int confirmPago = JOptionPane.showConfirmDialog(
                this, panelEfectivo, "Pago en Efectivo",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (confirmPago != JOptionPane.OK_OPTION) {
            return false;
        }

        try {
            double pagado = Double.parseDouble(txtPago.getText().replace(",", ".").trim());
            if (pagado < totalVenta[0]) {
                JOptionPane.showMessageDialog(this,
                        "El valor ingresado es menor al total de la venta.",
                        "Pago insuficiente", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor numérico válido.");
            return false;
        }

        return true;
    }

    /*private void GuardarTirillaSilencioso() {
        try {
            File carpeta = new File("src/pdf/");
            carpeta.mkdirs();

            File file = new File("src/pdf/tirilla.pdf");
            FileOutputStream archivo = new FileOutputStream(file);

            com.itextpdf.text.Rectangle tamañoTirilla
                    = new com.itextpdf.text.Rectangle(226, 800);
            Document doc = new Document(tamañoTirilla, 5, 5, 10, 10);
            PdfWriter.getInstance(doc, archivo);
            doc.open();

            // ... aquí va exactamente el mismo contenido interno de ImprimirFacturaTirilla() ...
            // Copia todo el código entre doc.open() y doc.close() de ese método
            doc.close();
            archivo.close();
            // ← Sin Desktop.open() para que NO abra automáticamente

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al guardar tirilla: " + e.getMessage());
        }
    }*/
}
