import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI {
    JFrame frame = new JFrame();
    JList moduleList;
    DefaultListModel<Modul> defaultListModelModuleNames =new DefaultListModel<>();
    JPanel panel= new JPanel();
    JScrollPane scrollPane;
    public GUI (){
        frame.setBounds(100, 100, 250,250);
        panel.setBounds(100, 100, 250,250);
        panel.add(scrollPane = new JScrollPane(moduleList));


        moduleList.setLayoutOrientation(ListSelectionModel.SINGLE_SELECTION);
        moduleList = new JList<>(defaultListModelModuleNames);
        moduleList.setVisibleRowCount(-1);
        moduleList.setSelectionMode(JList.HORIZONTAL_WRAP);
        moduleList.addListSelectionListener(new ElementSelected());


        scrollPane.setBounds(10,10 ,75 , 230 );
        panel.setLayout(null);
        frame.setVisible(true);

    }



    class ElementSelected implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent arg0) {

        }
    }

}
