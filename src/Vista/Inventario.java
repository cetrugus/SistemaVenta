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

    /**
     * Creates new form Inventario
     */
    public Inventario() {
        initComponents();
        this.setLocationRelativeTo(null);

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

    }

    public void ListarProductos() {
        List<Productos> ListaProInv = invDAO.ListarProductosInv(); // ← usa invDAO
        modelo = (DefaultTableModel) TableMovimiento.getModel();
        modelo.setRowCount(0); // ← limpiar antes de cargar

        Object[] ob = new Object[12];
        for (int i = 0; i < ListaProInv.size(); i++) {
            ob[0] = ListaProInv.get(i).getId();
            ob[1] = ListaProInv.get(i).getCodigo();
            ob[2] = ListaProInv.get(i).getNombre();
            ob[3] = ListaProInv.get(i).getProveedor();
            ob[4] = ListaProInv.get(i).getStock();
            ob[5] = ListaProInv.get(i).getUltimoMovimiento();
            ob[6] = ListaProInv.get(i).getFecha(); // Fecha In
            ob[7] = ListaProInv.get(i).getPrecio(); // Precio Compra
            ob[8] = ""; // Fecha Out
            ob[9] = ""; // Precio Venta
            ob[10] = ListaProInv.get(i).getEstado();
            ob[11] = ListaProInv.get(i).getUltimaObservacion();
            modelo.addRow(ob);
        }

        TableMovimiento.setModel(modelo);
        aplicarColorEstadoProInv(); // lo llamas desde aquí

        // Activa o desactiva los botones según sea el estado del cliente
        TableMovimiento.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && TableMovimiento.getSelectedRow() != -1) {

                int fila = TableMovimiento.getSelectedRow();
                Object estado = TableMovimiento.getValueAt(fila, 10);

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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCodInv = new javax.swing.JTextField();
        txtDesInv = new javax.swing.JTextField();
        cbxProveedorInv = new javax.swing.JComboBox<>();
        txtStockInv = new javax.swing.JTextField();
        cbxMovimientoInv = new javax.swing.JComboBox<>();
        tvtMotivoInv = new javax.swing.JTextField();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        btnGuardarProInv = new javax.swing.JButton();
        btnActualizarproinv = new javax.swing.JButton();
        btnNuevoProInv = new javax.swing.JButton();
        btnEliminarProInv = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtPrecioInv = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtIdInv = new javax.swing.JTextField();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SOFTWARE DE VENTAS");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Código:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Descripción:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Proveedor:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Cantidad:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Tipo de Movimiento:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Motivo/Observación:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Fecha:");

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

        btnNuevoProInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/nuevo.png"))); // NOI18N
        btnNuevoProInv.setText("Nuevo");

        btnEliminarProInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/encendido-apagado.png"))); // NOI18N
        btnEliminarProInv.setText("Estado");

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        jButton5.setText("Excel");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/pdf.png"))); // NOI18N
        jButton6.setText("PDF");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Precio:");

        jLabel13.setText("ID:");

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
                    .addComponent(txtStockInv)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNuevoProInv, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGuardarProInv, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEliminarProInv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnActualizarproinv, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(txtPrecioInv)
                    .addComponent(cbxMovimientoInv, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tvtMotivoInv)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel12)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtIdInv, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStockInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarProInv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizarproinv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoProInv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarProInv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TableMovimiento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Código", "Descripción", "Proveedor", "Stock", "Movimiento", "Fecha In", "Precio Compra", "Fecha Out", "Precio Venta", "Estado", "Observación"
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
            TableMovimiento.getColumnModel().getColumn(3).setPreferredWidth(100);
            TableMovimiento.getColumnModel().getColumn(4).setPreferredWidth(20);
            TableMovimiento.getColumnModel().getColumn(5).setPreferredWidth(20);
            TableMovimiento.getColumnModel().getColumn(6).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(7).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(8).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(9).setPreferredWidth(50);
            TableMovimiento.getColumnModel().getColumn(10).setPreferredWidth(1);
            TableMovimiento.getColumnModel().getColumn(11).setPreferredWidth(80);
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

        jLabel8.setText("Vendevor:");

        jLabel9.setText("Fecha y hora:");

        jLabel10.setText("dd/mm/yyyy");

        jLabel11.setText("hh:mm:ss");

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
                            .addComponent(jLabel8)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(59, 59, 59)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(42, 42, 42)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 148, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1190, 790));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TableMovimientoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableMovimientoMouseClicked
        // TODO add your handling code here:
        int fila = TableMovimiento.rowAtPoint(evt.getPoint());

        txtIdInv.setText(TableMovimiento.getValueAt(fila, 0).toString());
        txtCodInv.setText(TableMovimiento.getValueAt(fila, 1).toString());
        txtDesInv.setText(TableMovimiento.getValueAt(fila, 2).toString());
        cbxProveedorInv.setSelectedItem(TableMovimiento.getValueAt(fila, 3).toString());
        txtStockInv.setText(TableMovimiento.getValueAt(fila, 4).toString());
        txtPrecioInv.setText(TableMovimiento.getValueAt(fila, 7).toString());
        tvtMotivoInv.setText(TableMovimiento.getValueAt(fila, 11).toString());// ← índice 6 es Precio
        cbxMovimientoInv.setSelectedItem(TableMovimiento.getValueAt(fila, 5).toString());

        // Fecha — índice 5
        String fechaStr = TableMovimiento.getValueAt(fila, 6).toString();
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date fecha = sdf.parse(fechaStr);
            jDateChooser1.setDate(fecha);
        } catch (Exception e) {
            System.out.println("Error al parsear fecha: " + e.getMessage());
        }
    }//GEN-LAST:event_TableMovimientoMouseClicked

    private void btnGuardarProInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProInvActionPerformed
        // TODO add your handling code here:
        String cod = txtCodInv.getText().trim();
        String nombre = txtDesInv.getText().trim();
        String prov = cbxProveedorInv.getSelectedItem().toString();
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
            cantidad = Integer.parseInt(txtStockInv.getText().trim());
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
        double precio = pro.getPrecio();

        boolean ok = invDAO.RegistrarMovimiento(cod, nombre, prov, tipo,
                cantidad, precio, motivo, usuario);
        if (ok) {
            JOptionPane.showMessageDialog(null, "Movimiento registrado correctamente");
            ListarProductos();
            ListarMovimientos();
        } else {
            JOptionPane.showMessageDialog(null, "Error al registrar");
        }
        LimpiarDatosInv();
    }//GEN-LAST:event_btnGuardarProInvActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        ReporteUsFech dialogo = new ReporteUsFech(null, true);
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) {
            String usuario = dialogo.getUsuario();
            java.util.Date fecha = dialogo.getFecha();
            Excel.reporteInventario(usuario, fecha);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

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

                Object estado = table.getModel().getValueAt(row, 10); // ← índice 10

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

        // Ocultar columna ID (índice 0)
        /**
         * TableMovimiento.getColumnModel().getColumn(0).setMinWidth(0);
         * TableMovimiento.getColumnModel().getColumn(0).setMaxWidth(0);
         * TableMovimiento.getColumnModel().getColumn(0).setPreferredWidth(0);
         *
         * // Ocultar columna Estado (índice 10)
         * TableMovimiento.getColumnModel().getColumn(10).setMinWidth(0);
         * TableMovimiento.getColumnModel().getColumn(10).setMaxWidth(0);
         * TableMovimiento.getColumnModel().getColumn(10).setPreferredWidth(0);
         * *
         */
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
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JTextField tvtMotivoInv;
    private javax.swing.JTextField txtCodInv;
    private javax.swing.JTextField txtDesInv;
    private javax.swing.JTextField txtIdInv;
    private javax.swing.JTextField txtPrecioInv;
    private javax.swing.JTextField txtStockInv;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
