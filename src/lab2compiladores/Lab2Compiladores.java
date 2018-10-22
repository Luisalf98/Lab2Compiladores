package lab2compiladores;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Lab2Compiladores {
    
    private final JFrame jFrame;
    private final JScrollPane jScrollPane;
    private final Box hBox;
    private final Box tablesVBox;
    private final JPanel gridFormPanel;
    private final JPanel mTablePanel;
    private final JPanel mTablePanelFlow;
    private final JPanel mTableGridPanel;
    private final JScrollPane mTableScrollPane;
    private JLabel[][] mTable;
    private final JPanel sioTablePanel;
    private final JPanel sioTablePanelFlow;
    private JPanel sioTableGridPanel;
    private final JScrollPane sioTableScrollPane;
    private ArrayList<JLabel> sioTable;
    private final JPanel inputPanel;
    private final Box inputPanelBox;
    private final JPanel transformedGrammarPanel;
    private final JPanel transformedGrammarPanelFlow;
    private final JScrollPane transformedGrammarScrollPane;
    private final JLabel transformedGrammarLabel;
    private final JPanel firstSetsPanel;
    private final JPanel firstSetsPanelFlow;
    private final JScrollPane firstSetsScrollPane;
    private final JLabel firstSetsLabel;
    private final JPanel followingSetsPanel;
    private final JPanel followingSetsPanelFlow;
    private final JScrollPane followingSetsScrollPane;
    private final JLabel followingSetsLabel;
    private final JFileChooser jFileChooser;
    private final JDialog fileChooserDialog;
    private final JTextField fileLocation;
    private final JButton chooseFile;
    private final JButton compile;
    private final JPanel jp1;
    private final JPanel jp2;
    private final JPanel jp3;
    private final JTextField testString;
    private final JButton analizeString;
    
    private ASD asd;
    
    public Lab2Compiladores(){
        jFrame = new JFrame("Análisis sintáctico descendente");
        jFrame.setSize(600, 400);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jFrame.setLayout(new BorderLayout());
        
        hBox = new Box(BoxLayout.X_AXIS);
        tablesVBox = new Box(BoxLayout.Y_AXIS);
        mTablePanelFlow = new JPanel();
        mTablePanel = new JPanel(new BorderLayout());
        mTablePanel.setBorder(new TitledBorder("Tabla M"));
        mTableGridPanel = new JPanel();
        mTableScrollPane = new JScrollPane();
        mTableScrollPane.setPreferredSize(new Dimension(250,200));
        mTableScrollPane.setViewportView(mTablePanelFlow);
        mTablePanelFlow.add(mTableGridPanel);
        mTablePanel.add(mTableScrollPane);
        
        sioTablePanelFlow = new JPanel();
        sioTablePanel = new JPanel(new BorderLayout());
        sioTablePanel.setBorder(new TitledBorder("Tabla Pila-Entrada-Salida"));
        sioTableGridPanel = new JPanel(new GridLayout(0,3));
        sioTableScrollPane = new JScrollPane();
        sioTableScrollPane.setViewportView(sioTablePanelFlow);
        sioTableScrollPane.setPreferredSize(new Dimension(250,400));
        sioTablePanel.add(sioTableScrollPane);
        sioTablePanelFlow.add(sioTableGridPanel);
        
        tablesVBox.add(mTablePanel);
        tablesVBox.add(sioTablePanel);
        
        GridLayout gl = new GridLayout(2,2);
        gridFormPanel = new JPanel(gl);
        
        inputPanel = new JPanel(new FlowLayout());
        inputPanelBox = new Box(BoxLayout.Y_AXIS);
        inputPanel.setBorder(new TitledBorder("Entrada de Datos"));
        
        transformedGrammarPanel = new JPanel(new BorderLayout());
        transformedGrammarPanelFlow = new JPanel();
        transformedGrammarScrollPane = new JScrollPane(transformedGrammarPanelFlow);
        transformedGrammarPanel.setBorder(new TitledBorder("Gramática sin recursividad y factorizada"));
        transformedGrammarLabel = new JLabel("");
        transformedGrammarPanelFlow.add(transformedGrammarLabel);
        transformedGrammarPanel.add(transformedGrammarScrollPane);
        
        firstSetsPanel = new JPanel(new BorderLayout());
        firstSetsPanelFlow = new JPanel();
        firstSetsPanel.setBorder(new TitledBorder("Conjuntos PRIMERO"));
        firstSetsLabel = new JLabel("");
        firstSetsPanelFlow.add(firstSetsLabel);
        firstSetsScrollPane = new JScrollPane(firstSetsPanelFlow);
        firstSetsPanel.add(firstSetsScrollPane);
        
        followingSetsPanel = new JPanel(new BorderLayout());
        followingSetsPanelFlow = new JPanel();
        followingSetsPanel.setBorder(new TitledBorder("Conjuntos SIGUIENTE"));
        followingSetsLabel = new JLabel("");
        followingSetsPanelFlow.add(followingSetsLabel);
        followingSetsScrollPane = new JScrollPane(followingSetsPanelFlow);
        followingSetsPanel.add(followingSetsScrollPane);
        
        gridFormPanel.add(inputPanel);
        gridFormPanel.add(firstSetsPanel);
        gridFormPanel.add(transformedGrammarPanel);
        gridFormPanel.add(followingSetsPanel);
        
        hBox.add(gridFormPanel);
        hBox.add(tablesVBox);
        
        fileLocation = new JTextField("");
        fileLocation.setBorder(new LineBorder(Color.BLACK, 1));
        fileLocation.setPreferredSize(new Dimension(300,20));
        
        fileChooserDialog = new JDialog(jFrame, "Selector de archivo");
        fileChooserDialog.setLayout(new BorderLayout());
        jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.addActionListener((ActionEvent e) -> {
            if(e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)){
                fileChooserDialog.setVisible(false);
            }else{
                fileLocation.setText(jFileChooser.getSelectedFile().toString());
                fileChooserDialog.setVisible(false);
            }
        });
        fileChooserDialog.setSize(jFileChooser.getPreferredSize());
        fileChooserDialog.add(jFileChooser);
        
        
        
        Image img = new ImageIcon(this.getClass().getResource("../resources/icon.png"))
                .getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        chooseFile = new JButton(new ImageIcon(img));
        chooseFile.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                fileChooserDialog.setVisible(true);
            }
        });
        chooseFile.setPreferredSize(new Dimension(20,20));
        
        jp1 = new JPanel(new FlowLayout());
        jp1.setBorder(new TitledBorder("Elegir archivo con gramática"));
        jp2 = new JPanel(new FlowLayout());
        jp3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 30));
        jp3.setBorder(new TitledBorder("Analizar cadena"));
        
        jp1.add(fileLocation);
        jp1.add(chooseFile);
        
        
        compile = new JButton("Compilar gramática");
        compile.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                try {
                    asd = new ASD(fileLocation.getText());
                    fileLocation.setText("");
                    asd.showNewGrammar(transformedGrammarLabel);
                    asd.showFirstOrFollowing(firstSetsLabel, asd.getFirstSets(), "PRIMERO");
                    asd.showFirstOrFollowing(followingSetsLabel, asd.getFollowingSets(), "SIGUIENTE");
                    int rows = asd.getNF().size()+1;
                    int cols = asd.getT().size()+2;
                    mTable = new JLabel[rows][cols];
                    mTableGridPanel.removeAll();
                    sioTableGridPanel.removeAll();
                    mTableGridPanel.setLayout(new GridLayout(rows, cols));
                    asd.showMTable(mTableGridPanel, mTable, rows, cols);
                    
                    jFrame.repaint();
                    jFrame.validate();
                } catch (IOException ex) {
                    Logger.getLogger(Lab2Compiladores.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        testString = new JTextField("", 20);
        analizeString = new JButton("Analizar");
        analizeString.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                sioTableGridPanel.removeAll();
                asd.analizeString(testString.getText(), sioTableGridPanel, sioTable);
                testString.setText("");
                
                jFrame.validate();
                jFrame.repaint();
            }
        });
        
        jp2.add(compile);
        jp3.add(testString);
        jp3.add(analizeString);
        
        inputPanelBox.add(jp1);
        inputPanelBox.add(jp2);
        inputPanelBox.add(jp3);
        inputPanel.add(inputPanelBox);
        
        jScrollPane = new JScrollPane(hBox);
        jFrame.add(jScrollPane);
        
        jFrame.setVisible(true);
    }
    
    public static void main(String[] args) {
        Lab2Compiladores lab2Compiladores = new Lab2Compiladores();
    }
    
}
