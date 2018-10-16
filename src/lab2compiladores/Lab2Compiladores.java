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
    private final JPanel sioTablePanel;
    private final JPanel inputPanel;
    private final Box inputPanelBox;
    private final JPanel transformedGrammarPanel;
    private final JLabel transformedGrammarLabel;
    private final JPanel firstSetsPanel;
    private final JLabel firstSetsLabel;
    private final JPanel followingSetsPanel;
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
        mTablePanel = new JPanel();
        mTablePanel.setBorder(new TitledBorder("Tabla M"));
        
        sioTablePanel = new JPanel();
        sioTablePanel.setBorder(new TitledBorder("Tabla Pila-Entrada-Salida"));
        
        
        tablesVBox.add(mTablePanel);
        tablesVBox.add(sioTablePanel);
        
        GridLayout gl = new GridLayout(2,2);
        gridFormPanel = new JPanel(gl);
        
        inputPanel = new JPanel(new FlowLayout());
        inputPanelBox = new Box(BoxLayout.Y_AXIS);
        inputPanel.setBorder(new TitledBorder("Entrada de Datos"));
        
        transformedGrammarPanel = new JPanel();
        transformedGrammarPanel.setBorder(new TitledBorder("Gramática sin recursividad y factorizada"));
        transformedGrammarLabel = new JLabel("");
        transformedGrammarPanel.add(transformedGrammarLabel);
        
        firstSetsPanel = new JPanel();
        firstSetsPanel.setBorder(new TitledBorder("Conjuntos PRIMERO"));
        firstSetsLabel = new JLabel("");
        firstSetsPanel.add(firstSetsLabel);
        
        followingSetsPanel = new JPanel();
        followingSetsPanel.setBorder(new TitledBorder("Conjuntos SIGUIENTE"));
        followingSetsLabel = new JLabel("");
        followingSetsPanel.add(followingSetsLabel);
        
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
                } catch (IOException ex) {
                    Logger.getLogger(Lab2Compiladores.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        testString = new JTextField("", 20);
        analizeString = new JButton("Analizar");
        
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
