package CompressionProject;
import Model.IconTextRenderer;
import java.awt.event.MouseAdapter;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import javax.accessibility.AccessibleContext;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
public class GUI extends javax.swing.JFrame {

    public GUI() {
        FlatLightLaf.setup();
        initComponents();
        toCompressTable.setRowHeight(24);
    }

    public java.io.File[] getSelectedFilesFromDialog(){
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Kérem válasszon ki fájlokat és/vagy mappákat");
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            return chooser.getSelectedFiles();
        }
        return null;
    }

    public java.io.File getSelectedFileFromDialog(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Kérem válasszon ki egy tömörített fájlt");
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            return chooser.getSelectedFile();
        }
        return null;
    }

    public void displayErrorMessage(String errorMessage){
        JOptionPane.showMessageDialog(this, errorMessage);
    }

    //Tömörítés fül
    public void setCompressTableModel(AbstractTableModel tableModel){
        toCompressTable.setModel(tableModel);
    }

    public void addPlusButtonListener(ActionListener actionListener){
        addElementToCompressBtn.addActionListener(actionListener);
    }

    public void addMinusButtonListener(ActionListener actionListener){
        removeElementToCompressBtn.addActionListener(actionListener);
    }

    public void addCompressTableMouseListener(MouseAdapter mouseAdapter){
        toCompressTable.addMouseListener(mouseAdapter);
    }

    public void addBackButtonListener(ActionListener actionListener){
        backButton.addActionListener(actionListener);
    }

    public void addNavigationTextFieldListener(ActionListener actionListener){
        navigationTextField.addActionListener(actionListener);
    }

    public void addCompressButtonListener(ActionListener actionListener){
        compressBtn.addActionListener(actionListener);
    }

    public void setCompressNavigationTextField(String text){
        navigationTextField.setText(text);
    }

    public String getCompressNavigationTextFieldValue(){
        return navigationTextField.getText();
    }

    public int[] getSelectedCompressTableRows(){
        return toCompressTable.getSelectedRows();
    }

    public void setTableColumnModel(int columnIndex, DefaultTableCellRenderer renderer){
        toCompressTable.getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
    }

    //Kibontás fül

    public void setDeCompressTableColumnModel(int columnIndex, DefaultTableCellRenderer renderer){
        deCompressTable.getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
    }

    public void setdeCompressTableModel(AbstractTableModel tableModel){
        deCompressTable.setModel(tableModel);
    }

    public void addDeCompressButtonListener(ActionListener actionListener){
        deCompressBtn.addActionListener(actionListener);
    }

    public void addChooseCompressedFileBtnListener(ActionListener actionListener){
        chooseCompressedFileBtn.addActionListener(actionListener);
    }

    public void setdeCompressNavigationTextField(String text){
        deCompressNavigationTextField.setText(text);
    }

    public void addDeCompressTableMouseListener(MouseAdapter mouseAdapter){
        deCompressTable.addMouseListener(mouseAdapter);
    }

    public void addDeCompressBackButtonListener(ActionListener actionListener){
        backButton1.addActionListener(actionListener);
    }

    public void addDeCompressNavigationTextFieldListener(ActionListener actionListener){
      deCompressNavigationTextField.addActionListener(actionListener);
    }

    public int[] getDeCompressTableSelectedRows(){
        return deCompressTable.getSelectedRows();
    }

    public Object[] chooseDirectory(){
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      chooser.setDialogTitle("Kérem válasszon ki egy mappát ahova történik a kicsomagolás");
      chooser.setAccessory(new CheckBoxAccessory());
      CheckBoxAccessory cb = (CheckBoxAccessory)chooser.getAccessory();
      boolean createNewFolder = cb.isBoxSelected();
      File selectedDir = null;
      if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
        selectedDir = chooser.getSelectedFile();
      }
      return new Object[]{createNewFolder, selectedDir.toPath()};
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        navigationTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        addElementToCompressBtn = new javax.swing.JButton();
        removeElementToCompressBtn = new javax.swing.JButton();
        compressBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        toCompressTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        backButton1 = new javax.swing.JButton();
        deCompressNavigationTextField = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        chooseCompressedFileBtn = new javax.swing.JButton();
        deCompressBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        deCompressTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        backButton.setForeground(java.awt.SystemColor.window);
        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow-left.png"))); // NOI18N

        navigationTextField.setText("\\");

            addElementToCompressBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/package-variant-plus(1).png"))); // NOI18N
            addElementToCompressBtn.setText("<html>Fájl/Mappa<br>hozzáadása");
            addElementToCompressBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            addElementToCompressBtn.setVerifyInputWhenFocusTarget(false);
            addElementToCompressBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

            removeElementToCompressBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/package-variant-minus.png"))); // NOI18N
            removeElementToCompressBtn.setText("<html>Fájl/Mappa<br>eltávolítása");
            removeElementToCompressBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            removeElementToCompressBtn.setVerifyInputWhenFocusTarget(false);
            removeElementToCompressBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

            compressBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/package-variant-closed.png"))); // NOI18N
            compressBtn.setText("Tömörítés");
            compressBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            compressBtn.setVerifyInputWhenFocusTarget(false);
            compressBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

            toCompressTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null},
                    {null, null, null},
                    {null, null, null},
                    {null, null, null},
                    {null, null, null}
                },
                new String [] {
                    "Név és kiterjesztés", "Létrehozás dátuma", "Módosítás dátuma"
                }
            ));
            jScrollPane1.setViewportView(toCompressTable);

            javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(compressBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(removeElementToCompressBtn)
                        .addComponent(addElementToCompressBtn))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
                    .addContainerGap())
            );
            jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(addElementToCompressBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(removeElementToCompressBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(compressBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(51, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
            jPanel5.setLayout(jPanel5Layout);
            jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(backButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(navigationTextField)
                    .addContainerGap())
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(backButton)
                        .addComponent(navigationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );

            jTabbedPane2.addTab("Tömörítés", jPanel1);

            backButton1.setForeground(java.awt.SystemColor.window);
            backButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow-left.png"))); // NOI18N

            deCompressNavigationTextField.setText("\\");

                chooseCompressedFileBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/package-variant-closed.png"))); // NOI18N
                chooseCompressedFileBtn.setText("<html><center>Kibontandó fájl<br>kiválasztása");
                chooseCompressedFileBtn.setToolTipText("");
                chooseCompressedFileBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                chooseCompressedFileBtn.setVerifyInputWhenFocusTarget(false);
                chooseCompressedFileBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

                deCompressBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/package-variant.png"))); // NOI18N
                deCompressBtn.setText("<html><center> Kiválasztott<br> fájlok és mappák <br> kibontása");
                deCompressBtn.setToolTipText("");
                deCompressBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                deCompressBtn.setVerifyInputWhenFocusTarget(false);
                deCompressBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

                deCompressTable.setModel(new javax.swing.table.DefaultTableModel(
                    new Object [][] {
                        {null, null, null},
                        {null, null, null},
                        {null, null, null},
                        {null, null, null},
                        {null, null, null}
                    },
                    new String [] {
                        "Név és kiterjesztés", "Létrehozás dátuma", "Módosítás dátuma"
                    }
                ));
                jScrollPane2.setViewportView(deCompressTable);

                javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
                jPanel7.setLayout(jPanel7Layout);
                jPanel7Layout.setHorizontalGroup(
                    jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chooseCompressedFileBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deCompressBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
                        .addContainerGap())
                );
                jPanel7Layout.setVerticalGroup(
                    jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, 0)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(chooseCompressedFileBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deCompressBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(51, Short.MAX_VALUE))
                );

                javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
                jPanel6.setLayout(jPanel6Layout);
                jPanel6Layout.setHorizontalGroup(
                    jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(backButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deCompressNavigationTextField)
                        .addContainerGap())
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                );
                jPanel6Layout.setVerticalGroup(
                    jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(backButton1)
                            .addComponent(deCompressNavigationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                );
                jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                );

                jTabbedPane2.addTab("Kibontás", jPanel2);

                jLabel1.setText("Készítette: Bognár Vilmos Zsolt");

                javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addContainerGap(738, Short.MAX_VALUE))
                );
                jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addContainerGap(393, Short.MAX_VALUE))
                );

                jTabbedPane2.addTab("Névjegy", jPanel3);

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane2)
                );
                layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                );

                pack();
            }// </editor-fold>//GEN-END:initComponents

    public JRootPane getRootPane() {
        return rootPane;
    }

    public boolean isRootPaneCheckingEnabled() {
        return rootPaneCheckingEnabled;
    }

    /**
     * @param args the command line arguments
     */
    public AccessibleContext getAccessibleContext() {
        return accessibleContext;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addElementToCompressBtn;
    private javax.swing.JButton backButton;
    private javax.swing.JButton backButton1;
    private javax.swing.JButton chooseCompressedFileBtn;
    private javax.swing.JButton compressBtn;
    private javax.swing.JButton deCompressBtn;
    private javax.swing.JTextField deCompressNavigationTextField;
    private javax.swing.JTable deCompressTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField navigationTextField;
    private javax.swing.JButton removeElementToCompressBtn;
    private javax.swing.JTable toCompressTable;
    // End of variables declaration//GEN-END:variables
}
