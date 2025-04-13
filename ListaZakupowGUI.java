import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class ListaZakupowGUI {
    private JFrame frame;
    private JList<String> listaKategorii;
    private JList<Produkt> listaProduktow;
    private JTextArea textAreaZakupy;
    private JTextField textFieldIlosc;
    private JComboBox<String> comboBoxKategoria;

    public ListaZakupowGUI() {
        ListaZakupow.wczytajProdukty();
        ListaZakupow.wczytajListeZakupow();
        initialize();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ListaZakupowGUI window = new ListaZakupowGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JButton btnDodajProdukt = new JButton("Dodaj produkt");
        btnDodajProdukt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dodajProduktDoListy();
            }
        });
        panel.add(btnDodajProdukt);

        JButton btnUsunProdukt = new JButton("Usuń produkt");
        btnUsunProdukt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                usunProduktZListy();
            }
        });
        panel.add(btnUsunProdukt);

        JButton btnZapiszListe = new JButton("Zapisz listę zakupów");
        btnZapiszListe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ListaZakupow.zapiszListeZakupow();
                odswiezZakupy();
            }
        });
        panel.add(btnZapiszListe);

        JButton btnWyczyscListe = new JButton("Wyczyść listę zakupów");
        btnWyczyscListe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ListaZakupow.wyczyscListeZakupow();
                odswiezZakupy();
            }
        });
        panel.add(btnWyczyscListe);

        JButton btnEdytujProdukty = new JButton("Edytuj produkty");
        btnEdytujProdukty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                edytujPlikProdukty();
                odswiezKategorie();
            }
        });
        panel.add(btnEdytujProdukty);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        JPanel panelKategorii = new JPanel();
        splitPane.setLeftComponent(panelKategorii);
        panelKategorii.setLayout(new BorderLayout(0, 0));

        listaKategorii = new JList<>(new DefaultListModel<>());
        listaKategorii.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                wyswietlProduktyZKategorii();
            }
        });
        panelKategorii.add(new JScrollPane(listaKategorii), BorderLayout.CENTER);

        JPanel panelProduktow = new JPanel();
        splitPane.setRightComponent(panelProduktow);
        panelProduktow.setLayout(new BorderLayout(0, 0));

        listaProduktow = new JList<>(new DefaultListModel<>());
        listaProduktow.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                wyswietlInformacjeOProdukcie();
            }
        });
        panelProduktow.add(new JScrollPane(listaProduktow), BorderLayout.CENTER);

        JPanel panelZakupow = new JPanel();
        panelProduktow.add(panelZakupow, BorderLayout.SOUTH);
        panelZakupow.setLayout(new BorderLayout(0, 0));

        JLabel lblIlosc = new JLabel("Ilość:");
        panelZakupow.add(lblIlosc, BorderLayout.WEST);

        textFieldIlosc = new JTextField();
        panelZakupow.add(textFieldIlosc, BorderLayout.CENTER);
        textFieldIlosc.setColumns(10);

        comboBoxKategoria = new JComboBox<>();
        panelZakupow.add(comboBoxKategoria, BorderLayout.EAST);

        JPanel panelZakupy = new JPanel();
        frame.getContentPane().add(panelZakupy, BorderLayout.SOUTH);
        panelZakupy.setLayout(new BorderLayout(0, 0));

        textAreaZakupy = new JTextArea();
        panelZakupy.add(new JScrollPane(textAreaZakupy), BorderLayout.CENTER);

        odswiezKategorie();
        odswiezZakupy();
    }

    private void dodajProduktDoListy() {
        String wybranaKategoria = listaKategorii.getSelectedValue();
        Produkt wybranyProdukt = listaProduktow.getSelectedValue();
        if (wybranaKategoria != null && wybranyProdukt != null) {
            try {
                double ilosc = Double.parseDouble(textFieldIlosc.getText());
                if (wybranyProdukt.isCzyCalkowite() && ilosc % 1 != 0) {
                    JOptionPane.showMessageDialog(frame, "Ilość produktu w sztukach musi być liczbą całkowitą.");
                    return;
                }
                ListaZakupow.getListaZakupow().putIfAbsent(wybranaKategoria, new LinkedHashMap<>());
                ListaZakupow.getListaZakupow().get(wybranaKategoria).put(wybranyProdukt.getNazwa(), ilosc);
                odswiezZakupy();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Nieprawidłowa ilość.");
            }
        }
    }

    private void usunProduktZListy() {
        String wybranaKategoria = listaKategorii.getSelectedValue();
        Produkt wybranyProdukt = listaProduktow.getSelectedValue();
        if (wybranaKategoria != null && wybranyProdukt != null) {
            if (ListaZakupow.getListaZakupow().containsKey(wybranaKategoria)) {
                ListaZakupow.getListaZakupow().get(wybranaKategoria).remove(wybranyProdukt.getNazwa());
                odswiezZakupy();
            }
        }
    }

    private void odswiezKategorie() {
        DefaultListModel<String> model = (DefaultListModel<String>) listaKategorii.getModel();
        model.clear();
        ListaZakupow.getProdukty().keySet().forEach(model::addElement);
    }

    private void wyswietlProduktyZKategorii() {
        String wybranaKategoria = listaKategorii.getSelectedValue();
        DefaultListModel<Produkt> model = (DefaultListModel<Produkt>) listaProduktow.getModel();
        model.clear();
        if (wybranaKategoria != null) {
            ListaZakupow.getProdukty().get(wybranaKategoria).forEach(model::addElement);
        }
    }

    private void wyswietlInformacjeOProdukcie() {
        Produkt wybranyProdukt = listaProduktow.getSelectedValue();
        if (wybranyProdukt != null) {
            comboBoxKategoria.setSelectedItem(wybranyProdukt.getJednostka());
        }
    }

    private void odswiezZakupy() {
        StringBuilder sb = new StringBuilder();
        ListaZakupow.getListaZakupow().forEach((kategoria, produkty) -> {
            sb.append(kategoria).append(":\n");
            produkty.forEach((nazwa, ilosc) -> sb.append(nazwa).append(", ").append(ilosc).append("\n"));
        });
        textAreaZakupy.setText(sb.toString());
    }

    private void edytujPlikProdukty() {
        try {
            File plikProdukty = new File("produkty.txt");
            if (!plikProdukty.exists()) {
                JOptionPane.showMessageDialog(frame, "Plik produkty.txt nie istnieje.");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(plikProdukty));
            StringBuilder zawartosc = new StringBuilder();
            String linia;
            while ((linia = reader.readLine()) != null) {
                zawartosc.append(linia).append("\n");
            }
            reader.close();

            JPanel panel = new JPanel(new GridLayout(3, 1));
            JTextField tfKategoria = new JTextField();
            JTextField tfProdukt = new JTextField();
            panel.add(new JLabel("Kategoria:"));
            panel.add(tfKategoria);
            panel.add(new JLabel("Produkt:"));
            panel.add(tfProdukt);

            int wynik = JOptionPane.showConfirmDialog(frame, panel, "Dodaj nową kategorię i produkt",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (wynik == JOptionPane.OK_OPTION) {
                String kategoria = tfKategoria.getText().trim();
                String produkt = tfProdukt.getText().trim();

                if (zawartosc.indexOf(kategoria) == -1) { 
                    zawartosc.append(kategoria).append(":\n").append(produkt).append("\n");
                } else {
                    int pozycjaKategorii = zawartosc.indexOf(kategoria);
                    int koniecKategorii = zawartosc.indexOf("\n", pozycjaKategorii);
                    zawartosc.insert(koniecKategorii + 1, produkt + "\n");
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(plikProdukty));
                writer.write(zawartosc.toString());
                writer.close();
                ListaZakupow.wczytajProdukty();
                odswiezKategorie();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Błąd podczas edycji pliku produkty.txt.");
        }
    }
}

