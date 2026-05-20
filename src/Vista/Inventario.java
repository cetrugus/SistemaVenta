/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import Modelo.InventarioDAO;
import Modelo.Productos;
import Modelo.ProductosDAO;
import Modelo.ProveedorDAO;
import Reportes.Excel;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Tavo
 */
public class Inventario extends javax.swing.JFrame {

    ProductosDAO proDAO = new ProductosDAO();
    InventarioDAO invDAO = new InventarioDAO(); // ← AGREGAR
    DefaultTableModel modelo = new DefaultTableModel(); // ← AGREGAR
    Productos pro;

    /**
     * Creates new form Inventario
     */
    public Inventario() {
        initComponents();
        this.setLocationRelativeTo(null);
        txtUsuario.setText(Login.nombreUsuario);

        ImageIcon icono = new ImageIcon(getClass().getResource("/Img/Carrito-de-compras_logo.png"));
        setIconImage(icono.getImage());

        // Agrega solo estas líneas:
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            jLabel11.setText(LocalTime.now().format(formatoHora));
            jLabel10.setText(LocalDate.now().format(formatoFecha));
        });
        timer.setInitialDelay(0);
        timer.start();

        AutoCompleteDecorator.decorate(cbxProveedorInv);
        proDAO.ConsultarProveedor(cbxProveedorInv);
        TableMovimientos.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Código", "Descripción", "Proveedor",
                    "Movimiento", "Motivo", "Usuario", "Fecha"}
        ));
        TableMovimientos.getColumnModel().getColumn(0).setMinWidth(0);
        TableMovimientos.getColumnModel().getColumn(0).setMaxWidth(0);
        TableMovimientos.getColumnModel().getColumn(0).setPreferredWidth(0);

        ListarProductos();   // carga tab1
        ListarMovimientos(); // carga tab2
        jDateChooser1.setDate(new java.util.Date());

    }

    public void ListarProductos() {
        List<Productos> ListaProInv = invDAO.ListarProductosInv(); // ← usa invDAO
        modelo = (DefaultTableModel) TableMovimiento.getModel();
        modelo.setRowCount(0); // ← limpiar antes de cargar

        Object[] ob = new Object[15];
        for (int i = 0; i < ListaProInv.size(); i++) {
            ob[0] = ListaProInv.get(i).getId();
            ob[1] = ListaProInv.get(i).getCodigo();
            ob[2] = ListaProInv.get(i).getNombre();
            ob[3] = ListaProInv.get(i).getUbicacion();
            ob[4] = ListaProInv.get(i).getProveedor();
            ob[5] = ListaProInv.get(i).getStock();
            ob[6] = ListaProInv.get(i).getUltimoMovimiento();
            ob[7] = ListaProInv.get(i).getFecha(); // Fecha In
            ob[8] = ListaProInv.get(i).getPrecio(); // Precio Compra
            ob[9] = ""; // Fecha Out
            ob[10] = ""; // Precio Venta
            ob[11] = ListaProInv.get(i).getEstado();
            ob[12] = ListaProInv.get(i).getUltimaObservacion();
            ob[13] = ListaProInv.get(i).getCantMin();
            ob[14] = ListaProInv.get(i).getCantMax();
            modelo.addRow(ob);
        }

        TableMovimiento.setModel(modelo);
        aplicarColorEstadoProInv(); // lo llamas desde aquí

        // Activa o desactiva los botones según sea el estado del cliente
        TableMovimiento.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && TableMovimiento.getSelectedRow() != -1) {

                int fila = TableMovimiento.getSelectedRow();
                Object estado = TableMovimiento.getValueAt(fila, 11);

                int estadoVal = Integer.parseInt(estado.toString().trim());

                if (estadoVal == 1) {
                    btnEliminarProInv.setEnabled(true);    // Boton Activo
                    btnGuardarProInv.setEnabled(true);     // Boton Activo
                    btnNuevoProInv.setEnabled(true);       // Boton Activo
                    btnActualizarproinv.setEnabled(true); // Boton Activo
                } else {
                    btnEliminarProInv.setEnabled(true);    // Boton Activo
                    btnGuardarProInv.setEnabled(false);    // Boton inactivo
                    btnNuevoProInv.setEnabled(false);      // Boton inactivo
                    btnActualizarproinv.setEnabled(false);// Boton inactivo
                }
            }
        });
    }

    public void ListarMovimientos() {
        List<String[]> lista = invDAO.ListarMovimientos();
        DefaultTableModel modeloMov = (DefaultTableModel) TableMovimientos.getModel();
        modeloMov.setRowCount(0);
        for (String[] fila : lista) {
            modeloMov.addRow(fila);
        }
        TableMovimientos.setModel(modeloMov); // ← solo esto, nada más
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCodInv = new javax.swing.JTextField();
        txtDesInv = new javax.swing.JTextField();
        cbxProveedorInv = new javax.swing.JComboBox<>();
        cbxMovimientoInv = new javax.swing.JComboBox<>();
        tvtMotivoInv = new javax.swing.JTextField();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        btnGuardarProInv = new javax.swing.JButton();
        btnActualizarproinv = new javax.swing.JButton();
        btnNuevoProInv = new javax.swing.JButton();
        btnEliminarProInv = new javax.swing.JButton();
        tbnExcel = new javax.swing.JButton();
        tbnPDF = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtPrecioInv = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtIdInv = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtCantMov = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtUbicacionInv = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableMovimiento = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableMovimientos = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtStockInv = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtCantMin = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtCantMax = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SOFTWARE DE VENTAS");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Código:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Descripción:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Proveedor:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Tipo de Movimiento:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Motivo/Observación:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Fecha:");

        txtCodInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodInvActionPerformed(evt);
            }
        });
        txtCodInv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodInvKeyPressed(evt);
            }
        });

        cbxProveedorInv.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-Seleccionar Proveedor-" }));

        cbxMovimientoInv.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-Seleccionar Motivo-", "ENTRADA", "SALIDA", "AJUSTE" }));

        btnGuardarProInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/GuardarTodo.png"))); // NOI18N
        btnGuardarProInv.setText("Guardar");
        btnGuardarProInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProInvActionPerformed(evt);
            }
        });

        btnActualizarproinv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/Actualizar (2).png"))); // NOI18N
        btnActualizarproinv.setText("Actualizar");
        btnActualizarproinv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarproinvActionPerformed(evt);
            }
        });

        btnNuevoProInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/nuevo.png"))); // NOI18N
        btnNuevoProInv.setText("Nuevo");
        btnNuevoProInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoProInvActionPerformed(evt);
            }
        });

        btnEliminarProInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/encendido-apagado.png"))); // NOI18N
        btnEliminarProInv.setText("Estado");
        btnEliminarProInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProInvActionPerformed(evt);
            }
        });

        tbnExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        tbnExcel.setText("Excel");
        tbnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbnExcelActionPerformed(evt);
            }
        });

        tbnPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/pdf.png"))); // NOI18N
        tbnPDF.setText("PDF");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Precio:");

        txtPrecioInv.setEditable(false);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("ID:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Cantidad del movimiento:");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Ubicación:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxProveedorInv, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDesInv)
                    .addComponent(txtCodInv)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbnExcel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNuevoProInv, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGuardarProInv, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEliminarProInv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnActualizarproinv, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(tbnPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(txtPrecioInv)
                    .addComponent(cbxMovimientoInv, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tvtMotivoInv)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel14)
                            .addComponent(jLabel12)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtCantMov)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtIdInv, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(txtUbicacionInv)))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCodInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDesInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxProveedorInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtCantMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrecioInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxMovimientoInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tvtMotivoInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUbicacionInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarProInv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizarproinv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoProInv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarProInv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbnPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TableMovimiento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Código", "Descripción", "Ubicación", "Proveedor", "Stock", "Movimiento", "Fecha In", "Precio Compra", "Fecha Out", "Precio Venta", "Estado", "Observación", "Cant Min", "Cant Min"
            }
        ));
        TableMovimiento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableMovimientoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TableMovimiento);
        if (TableMovimiento.getColumnModel().getColumnCount() > 0) {
            TableMovimiento.getColumnModel().getColumn(0).setPreferredWidth(10);
            TableMovimiento.getColumnModel().getColumn(1).setPreferredWidth(80);
            TableMovimiento.getColumnModel().getColumn(2).setPreferredWidth(100);
            TableMovimiento.getColumnModel().getColumn(3).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(4).setPreferredWidth(100);
            TableMovimiento.getColumnModel().getColumn(5).setPreferredWidth(20);
            TableMovimiento.getColumnModel().getColumn(6).setPreferredWidth(20);
            TableMovimiento.getColumnModel().getColumn(7).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(8).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(9).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(10).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(11).setPreferredWidth(1);
            TableMovimiento.getColumnModel().getColumn(12).setPreferredWidth(80);
            TableMovimiento.getColumnModel().getColumn(13).setPreferredWidth(10);
            TableMovimiento.getColumnModel().getColumn(14).setPreferredWidth(10);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 913, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 510, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Inventario", jPanel3);

        TableMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Código", "Descripción", "Proveedor", "Movimiento", "Motivo", "Fecha", "Usuario"
            }
        ));
        jScrollPane2.setViewportView(TableMovimientos);
        if (TableMovimientos.getColumnModel().getColumnCount() > 0) {
            TableMovimientos.getColumnModel().getColumn(0).setPreferredWidth(10);
            TableMovimientos.getColumnModel().getColumn(1).setPreferredWidth(20);
            TableMovimientos.getColumnModel().getColumn(2).setPreferredWidth(50);
            TableMovimientos.getColumnModel().getColumn(3).setPreferredWidth(80);
            TableMovimientos.getColumnModel().getColumn(4).setPreferredWidth(10);
            TableMovimientos.getColumnModel().getColumn(5).setPreferredWidth(20);
            TableMovimientos.getColumnModel().getColumn(6).setPreferredWidth(20);
            TableMovimientos.getColumnModel().getColumn(7).setPreferredWidth(20);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 913, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 510, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Historial", jPanel4);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Vendedor:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Fecha y hora:");

        jLabel10.setText("dd/mm/yyyy");

        jLabel11.setText("hh:mm:ss");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Cantidad actual:");

        txtStockInv.setEditable(false);

        jLabel16.setText("Canti Min");

        txtCantMin.setEditable(false);

        jLabel17.setText("Canti Max");

        txtCantMax.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtStockInv)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9))
                        .addGap(366, 366, 366))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCantMin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCantMax, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtStockInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtCantMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(txtCantMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1190, 790));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TableMovimientoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableMovimientoMouseClicked
        // TODO add your handling code here:
        int fila = TableMovimiento.rowAtPoint(evt.getPoint());
        if (fila < 0) {
            return;
        }

        java.util.function.Function<Object, String> safe = val -> val != null ? val.toString() : "";

        txtIdInv.setText(safe.apply(TableMovimiento.getValueAt(fila, 0)));
        txtCodInv.setText(safe.apply(TableMovimiento.getValueAt(fila, 1)));
        txtDesInv.setText(safe.apply(TableMovimiento.getValueAt(fila, 2)));
        cbxProveedorInv.setSelectedItem(safe.apply(TableMovimiento.getValueAt(fila, 4)));
        txtStockInv.setText(safe.apply(TableMovimiento.getValueAt(fila, 5)));
        txtPrecioInv.setText(safe.apply(TableMovimiento.getValueAt(fila, 8)));
        tvtMotivoInv.setText(safe.apply(TableMovimiento.getValueAt(fila, 12)));
        txtUbicacionInv.setText(safe.apply(TableMovimiento.getValueAt(fila, 3)));
        cbxMovimientoInv.setSelectedItem(safe.apply(TableMovimiento.getValueAt(fila, 5)));
        txtCantMov.setText("");
        txtCantMin.setText(safe.apply(TableMovimiento.getValueAt(fila, 13)));
        txtCantMax.setText(safe.apply(TableMovimiento.getValueAt(fila, 14)));

        Object fechaVal = TableMovimiento.getValueAt(fila, 7);
        if (fechaVal != null && !fechaVal.toString().isEmpty()) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date fecha = sdf.parse(fechaVal.toString());
                jDateChooser1.setDate(fecha);
            } catch (Exception e) {
                System.out.println("Error al parsear fecha: " + e.getMessage());
            }
        } else {
            jDateChooser1.setDate(null);
        }

    }//GEN-LAST:event_TableMovimientoMouseClicked

    private void btnGuardarProInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProInvActionPerformed
        // TODO add your handling code here:
        String cod = txtCodInv.getText().trim();
        String nombre = txtDesInv.getText().trim();
        String prov = cbxProveedorInv.getSelectedItem().toString();
        String usuario = txtUsuario.getText().trim();

        if (cod.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el código del producto");
            return;
        }
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el nombre del producto");
            return;
        }
        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el vendedor");
            return;
        }

        int cantMov;
        try {
            cantMov = Integer.parseInt(txtCantMov.getText().trim());
            if (cantMov <= 0) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser un número");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(txtPrecioInv.getText().trim());
            if (precio <= 0) {
                JOptionPane.showMessageDialog(null, "El precio debe ser mayor a 0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El precio debe ser un número");
            return;
        }

        // Construir el objeto Productos
        Productos nuevoProducto = new Productos();
        nuevoProducto.setCodigo(cod);
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setProveedor(prov);
        nuevoProducto.setStock(cantMov);   // ← stock inicial = cantidad del movimiento
        nuevoProducto.setPrecio(precio);

        boolean ok = invDAO.RegistrarProducto(nuevoProducto);
        if (ok) {
            JOptionPane.showMessageDialog(null, "Producto registrado correctamente");
            ListarProductos();
            ListarMovimientos();
            btnGuardarProInv.setEnabled(false);
            btnActualizarproinv.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(null, "Error al registrar producto");
        }
        LimpiarDatosInv();

    }//GEN-LAST:event_btnGuardarProInvActionPerformed

    private void tbnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbnExcelActionPerformed
        // TODO add your handling code here:
        ReporteUsFech dialogo = new ReporteUsFech(null, true);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String usuario = dialogo.getUsuario();
            java.util.Date desde = dialogo.getFecha();
            java.util.Date hasta = dialogo.getFechaHasta();
            String tipo = dialogo.getTipoReporte();

            if (tipo.equals("Inventario")) {
                Excel.reporteInventario(usuario, desde, hasta);
            } else {
                Excel.reporteHistorial(usuario, desde, hasta);
            }
        }
    }//GEN-LAST:event_tbnExcelActionPerformed

    private void btnActualizarproinvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarproinvActionPerformed
        // TODO add your handling code here:
        String cod = txtCodInv.getText().trim();
        String nombre = txtDesInv.getText().trim();
        String prov = cbxProveedorInv.getSelectedItem().toString();
        String ubi = txtUbicacionInv.getText().toString();
        String tipo = cbxMovimientoInv.getSelectedItem().toString();
        String motivo = tvtMotivoInv.getText().trim();
        String usuario = txtUsuario.getText().trim();

        if (cod.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el código del producto");
            return;
        }
        if (tipo.contains("Seleccionar")) {
            JOptionPane.showMessageDialog(null, "Seleccione el tipo de movimiento");
            return;
        }
        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el vendedor");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantMov.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser un número");
            return;
        }

        // Obtener precio actual del producto desde la tabla
        Productos pro = invDAO.BuscarPro(cod);

        if (pro == null) {
            JOptionPane.showMessageDialog(null, "Producto no encontrado con ese código");
            return;
        }

// ← AGREGÁ ESTAS LÍNEAS
        pro.setCodigo(txtCodInv.getText().trim());
        pro.setNombre(txtDesInv.getText().trim());
        pro.setProveedor(cbxProveedorInv.getSelectedItem().toString());
        pro.setUbicacion(txtUbicacionInv.getText().trim());
        pro.setStock(Integer.parseInt(txtStockInv.getText().trim()));
        pro.setPrecio(Double.parseDouble(txtPrecioInv.getText().trim()));
        jDateChooser1.setDate(new java.util.Date());
        pro.setCantMin(txtCantMin.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtCantMin.getText().trim()));
        pro.setCantMax(txtCantMax.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtCantMax.getText().trim()));
        if (pro == null) {
            JOptionPane.showMessageDialog(null, "Producto no encontrado con ese código");
            return;
        }
        System.out.println("ID del producto encontrado: " + pro.getId());
        double precio = pro.getPrecio();

        boolean actualizado = invDAO.ModificarProductos(pro);

        if (!actualizado) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el producto");
            return;
        }

        boolean ok = invDAO.RegistrarMovimiento(cod, nombre, prov, tipo,
                cantidad, precio, motivo, usuario);

        JOptionPane.showMessageDialog(null, "Producto actualizado correctamente"); // ← AGREGÁ
        ListarProductos();   // ← AGREGÁ el nombre exacto de tu método de listar
        ListarMovimientos();
        LimpiarDatosInv();

        btnGuardarProInv.setEnabled(true);      // Activo para guardar nuevo
        btnNuevoProInv.setEnabled(true);        // Activo
        btnEliminarProInv.setEnabled(false);    // Inactivo (no hay nada seleccionado)
        btnActualizarproinv.setEnabled(false);  // Inactivo (no hay nada seleccionado)
        tbnExcel.setEnabled(false);             // Inactivo (no hay nada seleccionado)
        tbnPDF.setEnabled(false);              // Inactivo (no hay nada seleccionado)

        // Quitar selección de la tabla
        TableMovimiento.clearSelection();

        // Poner el foco en el primer campo
        txtCodInv.requestFocus();
    }//GEN-LAST:event_btnActualizarproinvActionPerformed

    private void btnEliminarProInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProInvActionPerformed
        // TODO add your handling code here:
        int fila = TableMovimiento.getSelectedRow();

        // 🔴 Validar si seleccionó algo
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }
        //codigo provisional
        for (int i = 0; i < TableMovimiento.getColumnCount(); i++) {
            System.out.println("Columna " + i + ": " + TableMovimiento.getModel().getValueAt(fila, i));
        }

        int id = Integer.parseInt(TableMovimiento.getModel().getValueAt(fila, 0).toString());
        int estadoActual = Integer.parseInt(TableMovimiento.getModel().getValueAt(fila, 11).toString());

        // Mensaje dinámico según estado actual
        String mensaje = estadoActual == 1 ? "¿Desea desactivar este producto?" : "¿Desea activar este producto?";

        int confirm = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (invDAO.EliminarProducto(id)) {
                modelo.setRowCount(0);
                ListarProductos();
                ListarMovimientos();
            } else {
                JOptionPane.showMessageDialog(null, "Error al bloquear producto");
            }
        }
        LimpiarDatosInv();
        // Habilitar botones
        btnGuardarProInv.setEnabled(true);      // Activo para guardar nuevo
        btnNuevoProInv.setEnabled(true);        // Activo
        btnEliminarProInv.setEnabled(false);    // Inactivo (no hay nada seleccionado)
        btnActualizarproinv.setEnabled(false);  // Inactivo (no hay nada seleccionado)
        tbnExcel.setEnabled(false);             // Inactivo (no hay nada seleccionado)
        tbnPDF.setEnabled(false);              // Inactivo (no hay nada seleccionado)

        // Quitar selección de la tabla
        TableMovimiento.clearSelection();

        // Poner el foco en el primer campo
        txtCodInv.requestFocus();
    }//GEN-LAST:event_btnEliminarProInvActionPerformed

    private void btnNuevoProInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoProInvActionPerformed
        // TODO add your handling code here:
        LimpiarDatosInv();
        // Habilitar botones
        btnGuardarProInv.setEnabled(true);      // Activo para guardar nuevo
        btnNuevoProInv.setEnabled(true);        // Activo
        btnEliminarProInv.setEnabled(false);    // Inactivo (no hay nada seleccionado)
        btnActualizarproinv.setEnabled(false);  // Inactivo (no hay nada seleccionado)
        tbnExcel.setEnabled(false);             // Inactivo (no hay nada seleccionado)
        tbnPDF.setEnabled(false);              // Inactivo (no hay nada seleccionado)

        // Quitar selección de la tabla
        TableMovimiento.clearSelection();

        // Poner el foco en el primer campo
        txtCodInv.requestFocus();
    }//GEN-LAST:event_btnNuevoProInvActionPerformed

    private void txtCodInvKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodInvKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            if (!"".equals(txtCodInv.getText())) {

                String cod = txtCodInv.getText();
                pro = proDAO.BuscarPro(cod);

                if (pro != null && pro.getNombre() != null) {

                    txtDesInv.setText(pro.getNombre());
                    txtPrecioInv.setText(String.valueOf(pro.getPrecio()));
                    txtStockInv.setText(String.valueOf(pro.getStock()));
                    cbxProveedorInv.setSelectedItem(pro.getProveedor());
                    cbxMovimientoInv.requestFocus();

                } else {

                    txtDesInv.setText("");
                    txtPrecioInv.setText("");
                    txtStockInv.setText("");
                    txtCodInv.requestFocus();

                    JOptionPane.showMessageDialog(null, "Producto no encontrado");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Ingrese el código del producto");
                txtCodInv.requestFocus();
            }
        }

    }//GEN-LAST:event_txtCodInvKeyPressed

    private void txtCodInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodInvActionPerformed
        // TODO add your handling code here:
        buscarProducto();
    }//GEN-LAST:event_txtCodInvActionPerformed

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
            java.util.logging.Logger.getLogger(Inventario.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Inventario.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Inventario.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inventario.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inventario().setVisible(true);
            }
        });

    }

    private void aplicarColorEstadoProInv() {
        TableMovimiento.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Object estado = table.getModel().getValueAt(row, 11); // ← índice 10

                if (!isSelected) {
                    String estadoStr = estado.toString().trim();
                    if (!estadoStr.isEmpty()) { // ← validar que no esté vacío
                        int estadoVal = Integer.parseInt(estadoStr);
                        if (estadoVal == 1) {
                            c.setBackground(new java.awt.Color(232, 255, 234));
                            c.setForeground(new java.awt.Color(0, 120, 0));
                        } else {
                            c.setBackground(new java.awt.Color(189, 189, 189));
                            c.setForeground(new java.awt.Color(245, 73, 39));
                        }
                    }
                }
                return c;
            }
        });

        TableMovimiento.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && TableMovimiento.getSelectedRow() != -1) {

                int fila = TableMovimiento.getSelectedRow();
                Object estado = TableMovimiento.getValueAt(fila, 11);

                if (estado == null || estado.toString().trim().isEmpty()) {
                    return;
                }

                int estadoVal = Integer.parseInt(estado.toString().trim());

                if (estadoVal == 1) {
                    btnEliminarProInv.setEnabled(true);    // Boton Activo
                    btnGuardarProInv.setEnabled(false);     // Boton inactivo
                    btnNuevoProInv.setEnabled(true);       // Boton Activo
                    btnActualizarproinv.setEnabled(true); // Boton Activo
                    tbnExcel.setEnabled(true);// Boton Activo
                    tbnPDF.setEnabled(true);// Boton Activo
                } else {
                    btnEliminarProInv.setEnabled(true);    // Boton Activo
                    btnGuardarProInv.setEnabled(false);    // Boton inactivo
                    btnNuevoProInv.setEnabled(true);      // Boton Activo
                    btnActualizarproinv.setEnabled(false);// Boton inactivo
                    tbnExcel.setEnabled(false);// Boton inactivo
                    tbnPDF.setEnabled(false);// Boton inactivo
                }
            }
        });

        // Ocultar columna ID (índice 0)
        TableMovimiento.getColumnModel().getColumn(0).setMinWidth(0);
        TableMovimiento.getColumnModel().getColumn(0).setMaxWidth(0);
        TableMovimiento.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Ocultar columna precio venta (índice 10)
        TableMovimiento.getColumnModel().getColumn(10).setMinWidth(0);
        TableMovimiento.getColumnModel().getColumn(10).setMaxWidth(0);
        TableMovimiento.getColumnModel().getColumn(10).setPreferredWidth(0);
        
        // Ocultar columna Estado (índice 11)
        TableMovimiento.getColumnModel().getColumn(11).setMinWidth(0);
        TableMovimiento.getColumnModel().getColumn(11).setMaxWidth(0);
        TableMovimiento.getColumnModel().getColumn(11).setPreferredWidth(0);
        
        // Ocultar columna CanMin (índice 13)
        TableMovimiento.getColumnModel().getColumn(13).setMinWidth(0);
        TableMovimiento.getColumnModel().getColumn(13).setMaxWidth(0);
        TableMovimiento.getColumnModel().getColumn(13).setPreferredWidth(0);

        // Ocultar columna CanMax (índice 14)
        TableMovimiento.getColumnModel().getColumn(14).setMinWidth(0);
        TableMovimiento.getColumnModel().getColumn(14).setMaxWidth(0);
        TableMovimiento.getColumnModel().getColumn(14).setPreferredWidth(0);
    }

    private void LimpiarDatosInv() {
        txtIdInv.setText("");
        txtCodInv.setText("");
        txtDesInv.setText("");
        cbxProveedorInv.setSelectedIndex(0);
        txtStockInv.setText("");
        txtPrecioInv.setText("");
        cbxMovimientoInv.setSelectedIndex(0);
        tvtMotivoInv.setText("");
        jDateChooser1.setDate(null);
        txtCantMin.setText("");
        txtCantMax.setText("");
    }

    private void buscarProducto() {
        String cod = txtCodInv.getText().trim();

        if (cod.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el código del producto");
            txtCodInv.requestFocus();
            return;
        }

        pro = proDAO.BuscarPro(cod);

        if (pro != null && pro.getNombre() != null) {
            txtDesInv.setText(pro.getNombre());
            txtPrecioInv.setText(String.valueOf(pro.getPrecio()));
            txtStockInv.setText(String.valueOf(pro.getStock()));
            cbxProveedorInv.setSelectedItem(pro.getProveedor());
        } else {
            JOptionPane.showMessageDialog(null, "Producto no encontrado");
            txtDesInv.setText("");
            txtPrecioInv.setText("");
            txtStockInv.setText("");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TableMovimiento;
    private javax.swing.JTable TableMovimientos;
    private javax.swing.JButton btnActualizarproinv;
    private javax.swing.JButton btnEliminarProInv;
    private javax.swing.JButton btnGuardarProInv;
    private javax.swing.JButton btnNuevoProInv;
    private javax.swing.JComboBox<String> cbxMovimientoInv;
    private javax.swing.JComboBox<String> cbxProveedorInv;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton tbnExcel;
    private javax.swing.JButton tbnPDF;
    private javax.swing.JTextField tvtMotivoInv;
    private javax.swing.JTextField txtCantMax;
    private javax.swing.JTextField txtCantMin;
    private javax.swing.JTextField txtCantMov;
    private javax.swing.JTextField txtCodInv;
    private javax.swing.JTextField txtDesInv;
    private javax.swing.JTextField txtIdInv;
    private javax.swing.JTextField txtPrecioInv;
    private javax.swing.JTextField txtStockInv;
    private javax.swing.JTextField txtUbicacionInv;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
