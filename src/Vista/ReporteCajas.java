/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import Modelo.AperturaCaja;
import Modelo.ReporteCajaDAO;
import java.awt.Image;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 *
 * @author Tavo
 */
public class ReporteCajas extends javax.swing.JFrame {

    ReporteCajaDAO cajaDAO = new ReporteCajaDAO();
    DefaultTableModel modelo = new DefaultTableModel();

    public ReporteCajas() {
        initComponents();
        this.setLocationRelativeTo(null);
        ImageIcon icono = new ImageIcon(getClass().getResource("/Img/carrito-de-compras_logo.png"));
        setIconImage(icono.getImage());
        txtUsuario.setText(Login.nombreUsuario);

        SwingUtilities.invokeLater(() -> {
            rutaLogo = "logo_pdf.png"; // ← siempre este nombre
            mostrarLogo(rutaLogo);
        });

        limpiar.addActionListener(evt -> {
            ChooserDesde.setDate(null);
            ChooserHasta.setDate(null);
            cargarDatos(cajaDAO.ListarMovimientosCaja());
        });

        //fecha y hora
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            jLabelhora.setText(LocalTime.now().format(formatoHora));
            jLabelfecha.setText(LocalDate.now().format(formatoFecha));
        });
        timer.setInitialDelay(0);
        timer.start();

        // Configurar tabla
        configurarTabla();

        // Cargar datos
        cargarDatos(cajaDAO.ListarMovimientosCaja());
    }

    private void configurarTabla() {
        modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "Cajero", "F. Apertura", "F. Cierre",
                    "Apertura", "Cierre", "Efectivo",
                    "T. Crédito", "T. Débito", "Transfer.",
                    "Diferencia", "Estado"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCaja.setModel(modelo);

        // Ocultar columna ID
        tablaCaja.getColumnModel().getColumn(0).setMinWidth(0);
        tablaCaja.getColumnModel().getColumn(0).setMaxWidth(0);

        // Anchos de columnas
        tablaCaja.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaCaja.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaCaja.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaCaja.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(6).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(7).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(8).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(9).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(10).setPreferredWidth(80);
        tablaCaja.getColumnModel().getColumn(11).setPreferredWidth(70);

        // Al seleccionar una fila, mostrar fechas en lblApertura y txtCierre
        tablaCaja.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaCaja.getSelectedRow();
                if (fila >= 0) {
                    Object fechaAp = modelo.getValueAt(fila, 2); // F. Apertura
                    Object fechaCi = modelo.getValueAt(fila, 3); // F. Cierre
                    txtApertura.setText(fechaAp != null ? fechaAp.toString() : "--");
                    txtCierre.setText(fechaCi != null ? fechaCi.toString() : "--");
                }
            }
        });
    }

    private void cargarDatos(List<AperturaCaja> lista) {
        modelo.setRowCount(0);

        double totalApertura = 0, totalCierre = 0;
        double totalEfectivo = 0, totalCredito = 0;
        double totalDebito = 0, totalTransfer = 0;
        double totalDiferencia = 0;

        for (AperturaCaja ap : lista) {
            String estadoStr = ap.getEstado() == 1 ? "Abierta" : "Cerrada";

            modelo.addRow(new Object[]{
                ap.getId(),
                ap.getNombreUsuario(),
                ap.getFechaApertura(),
                ap.getFechaCierre() != null ? ap.getFechaCierre() : "--",
                String.format("$ %,.2f", ap.getMontoApertura()),
                String.format("$ %,.2f", ap.getMontoCierre()),
                String.format("$ %,.2f", ap.getTotalEfectivo()),
                String.format("$ %,.2f", ap.getTotalTarjetaCredito()),
                String.format("$ %,.2f", ap.getTotalTarjetaDebito()),
                String.format("$ %,.2f", ap.getTotalTransferencia()),
                String.format("$ %,.2f", ap.getDiferencia()),
                estadoStr
            });

            // Acumular totales solo de cajas cerradas
            if (ap.getEstado() == 0) {
                totalApertura += ap.getMontoApertura();
                totalCierre += ap.getMontoCierre();
                totalEfectivo += ap.getTotalEfectivo();
                totalCredito += ap.getTotalTarjetaCredito();
                totalDebito += ap.getTotalTarjetaDebito();
                totalTransfer += ap.getTotalTransferencia();
                totalDiferencia += ap.getDiferencia();
            }
        }

        // Mostrar totales en labels
        txtApertura.setText("--");
        txtCierre.setText("--");
        lblApertura.setText(String.format("$ %,.2f", totalApertura));
        lblTotalCierre.setText(String.format("$ %,.2f", totalCierre));
        lblTotalEfectivo.setText(String.format("$ %,.2f", totalEfectivo));
        lblTotalCredito.setText(String.format("$ %,.2f", totalCredito));
        lblTotalDebito.setText(String.format("$ %,.2f", totalDebito));
        lblTotalTransfer.setText(String.format("$ %,.2f", totalTransfer));
        lblTotalDiferencia.setText(String.format("$ %,.2f", totalDiferencia));

        // Color diferencia
        if (totalDiferencia >= 0) {
            lblTotalDiferencia.setForeground(new Color(0, 120, 0));
        } else {
            lblTotalDiferencia.setForeground(Color.RED);
        }

        // Aplicar colores a filas
        aplicarColores();
    }

    private void aplicarColores() {
        tablaCaja.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    Object estado = table.getModel()
                            .getValueAt(row, 11);
                    if ("Abierta".equals(estado.toString())) {
                        // Caja abierta — amarillo
                        c.setBackground(new Color(255, 255, 204));
                        c.setForeground(new Color(150, 100, 0));
                    } else {
                        // Caja cerrada — verde
                        c.setBackground(new Color(232, 255, 234));
                        c.setForeground(new Color(0, 120, 0));
                    }
                }
                return c;
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCaja = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        ChooserDesde = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        ChooserHasta = new com.toedter.calendar.JDateChooser();
        filtrar = new javax.swing.JButton();
        limpiar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnPDF = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        LabelLogo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelfecha = new javax.swing.JLabel();
        jLabelhora = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtApertura = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lblApertura = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCierre = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lblTotalEfectivo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        lblTotalCredito = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        lblTotalDebito = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblTotalTransfer = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        lblTotalCierre = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        lblTotalDiferencia = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SOFTWARE ADMINISTRATIVO DE VENTAS");

        tablaCaja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Id Usuario", "Usuario", "Nombre", "Apertura", "Monto", "Cierre", "Monto", "Efectivo", "T.Credito", "T.Debito", "Ganancia", "Transferencia", "Estado"
            }
        ));
        jScrollPane1.setViewportView(tablaCaja);
        if (tablaCaja.getColumnModel().getColumnCount() > 0) {
            tablaCaja.getColumnModel().getColumn(0).setPreferredWidth(10);
            tablaCaja.getColumnModel().getColumn(1).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(2).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(3).setPreferredWidth(50);
            tablaCaja.getColumnModel().getColumn(4).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(5).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(6).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(7).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(8).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(9).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(10).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(11).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(12).setPreferredWidth(20);
            tablaCaja.getColumnModel().getColumn(13).setPreferredWidth(10);
        }

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel15.setText("Desde:");

        jLabel16.setText("Hasta:");

        filtrar.setText("Filtrar");
        filtrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtrarActionPerformed(evt);
            }
        });

        limpiar.setText("limpiar");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ChooserDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ChooserHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(limpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filtrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(limpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(filtrar)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel15)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ChooserDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ChooserHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32))))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/pdf.png"))); // NOI18N
        btnPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPDFActionPerformed(evt);
            }
        });

        btnExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/excel.png"))); // NOI18N
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        LabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelLogo.setText("Logo");
        LabelLogo.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Fecha y hora:");

        jLabelfecha.setText("dd/mm/yyyy");

        jLabelhora.setText("hh:mm:ss");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Usuario:");

        txtUsuario.setText("txtUsuario");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Reporte de cajas");

        jLabel3.setText("Cajero:");

        jLabel5.setText("ID");

        jLabel6.setText("Apertura:");

        jLabel7.setText("Monto:");

        jLabel8.setText("Cierre:");

        jLabel9.setText("Efectivo:");

        jLabel10.setText("T.Credito:");

        jLabel11.setText("T.Debito:");

        jLabel12.setText("Transferencia:");

        jLabel13.setText("Monto:");

        jLabel14.setText("Ganancia:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextField1)
                        .addComponent(LabelLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addComponent(jLabel3)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtCierre, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtApertura, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalDebito, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTotalEfectivo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTotalCierre, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelfecha, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelhora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel7)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTotalCredito)
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTotalTransfer)
                                .addComponent(lblApertura)
                                .addComponent(jLabel14)
                                .addComponent(lblTotalDiferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel5)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel2)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelfecha)
                            .addComponent(jLabelhora))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUsuario)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCierre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalCierre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblApertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalDiferencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void filtrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtrarActionPerformed
        // TODO add your handling code here:
        filtrar();
    }//GEN-LAST:event_filtrarActionPerformed

    private void btnPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPDFActionPerformed
        // TODO add your handling code here:
        exportarPDF();
    }//GEN-LAST:event_btnPDFActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        // TODO add your handling code here:
        Reportes.Excel.reporteCaja(modelo);
    }//GEN-LAST:event_btnExcelActionPerformed

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
            java.util.logging.Logger.getLogger(ReporteCajas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReporteCajas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReporteCajas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReporteCajas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReporteCajas().setVisible(true);
            }
        });
    }

    private void filtrar() {
        if (ChooserDesde.getDate() == null || ChooserHasta.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione ambas fechas para filtrar");
            return;
        }
        java.text.SimpleDateFormat sdf
                = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String desde = sdf.format(ChooserDesde.getDate());
        String hasta = sdf.format(ChooserHasta.getDate());
        cargarDatos(cajaDAO.FiltrarPorFecha(desde, hasta));
    }

    private void exportarPDF() {
        try {
            java.io.File carpeta = new java.io.File("src/pdf/");
            carpeta.mkdirs();
            java.io.File file = new java.io.File("src/pdf/reporte_caja.pdf");

            com.itextpdf.text.Document doc
                    = new com.itextpdf.text.Document(
                            com.itextpdf.text.PageSize.A4.rotate());
            com.itextpdf.text.pdf.PdfWriter.getInstance(
                    doc, new java.io.FileOutputStream(file));
            doc.open();

            // Título
            com.itextpdf.text.Font fontTitulo
                    = new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            14, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Paragraph titulo
                    = new com.itextpdf.text.Paragraph(
                            "Reporte de Caja\n\n", fontTitulo);
            titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            doc.add(titulo);

            // Tabla
            com.itextpdf.text.pdf.PdfPTable tabla
                    = new com.itextpdf.text.pdf.PdfPTable(11);
            tabla.setWidthPercentage(100);

            com.itextpdf.text.Font fontHeader
                    = new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            8, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal
                    = new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            7, com.itextpdf.text.Font.NORMAL);

            // Headers
            String[] headers = {
                "Cajero", "F. Apertura", "F. Cierre",
                "Apertura", "Cierre", "Efectivo",
                "T. Crédito", "T. Débito", "Transfer.",
                "Diferencia", "Estado"
            };
            for (String h : headers) {
                com.itextpdf.text.pdf.PdfPCell cell
                        = new com.itextpdf.text.pdf.PdfPCell(
                                new com.itextpdf.text.Phrase(h, fontHeader));
                cell.setBackgroundColor(
                        com.itextpdf.text.BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(
                        com.itextpdf.text.Element.ALIGN_CENTER);
                tabla.addCell(cell);
            }

            // Datos
            for (int i = 0; i < modelo.getRowCount(); i++) {
                for (int j = 1; j < modelo.getColumnCount(); j++) {
                    Object val = modelo.getValueAt(i, j);
                    com.itextpdf.text.pdf.PdfPCell cell
                            = new com.itextpdf.text.pdf.PdfPCell(
                                    new com.itextpdf.text.Phrase(
                                            val != null ? val.toString() : "",
                                            fontNormal));
                    cell.setHorizontalAlignment(
                            com.itextpdf.text.Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                }
            }

            doc.add(tabla);
            doc.close();

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar PDF: " + e.getMessage());
        }
    }

    private String rutaLogo = "";

    private void mostrarLogo(String nombreArchivo) {
        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {

            java.net.URL ruta = getClass().getResource("/Img/" + nombreArchivo);

            if (ruta != null) {

                ImageIcon imagen = new ImageIcon(ruta);

                int ancho = LabelLogo.getWidth() > 0
                        ? LabelLogo.getWidth()
                        : LabelLogo.getPreferredSize().width;

                int alto = LabelLogo.getHeight() > 0
                        ? LabelLogo.getHeight()
                        : LabelLogo.getPreferredSize().height;

                if (ancho <= 0) {
                    ancho = 158;
                }

                if (alto <= 0) {
                    alto = 158;
                }

                Image imgEscalada = imagen.getImage().getScaledInstance(
                        ancho,
                        alto,
                        Image.SCALE_SMOOTH
                );

                LabelLogo.setText("");
                LabelLogo.setIcon(new ImageIcon(imgEscalada));

            } else {

                System.out.println("No se encontró la imagen: " + nombreArchivo);

                LabelLogo.setIcon(null);
                LabelLogo.setText("Logo no encontrado");
            }

        } else {

            LabelLogo.setIcon(null);
            LabelLogo.setText("Aquí va su logo");
            LabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser ChooserDesde;
    private com.toedter.calendar.JDateChooser ChooserHasta;
    private javax.swing.JLabel LabelLogo;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnPDF;
    private javax.swing.JButton filtrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelfecha;
    private javax.swing.JLabel jLabelhora;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField lblApertura;
    private javax.swing.JTextField lblTotalCierre;
    private javax.swing.JTextField lblTotalCredito;
    private javax.swing.JTextField lblTotalDebito;
    private javax.swing.JTextField lblTotalDiferencia;
    private javax.swing.JTextField lblTotalEfectivo;
    private javax.swing.JTextField lblTotalTransfer;
    private javax.swing.JButton limpiar;
    private javax.swing.JTable tablaCaja;
    private javax.swing.JTextField txtApertura;
    private javax.swing.JTextField txtCierre;
    private javax.swing.JLabel txtUsuario;
    // End of variables declaration//GEN-END:variables
}
