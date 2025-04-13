import java.io.*;
import java.util.*;

public class ListaZakupow {
    private static final String PLIK_PRODUKTY = "produkty.txt";
    private static final String PLIK_ZAKUPY = "zakupy.txt";
    private static Map<String, List<Produkt>> produkty = new LinkedHashMap<>();
    private static Map<String, Map<String, Double>> listaZakupow = new LinkedHashMap<>();

    public static Map<String, List<Produkt>> getProdukty() {
        return produkty;
    }

    public static Map<String, Map<String, Double>> getListaZakupow() {
        return listaZakupow;
    }

    public static void wczytajProdukty() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PLIK_PRODUKTY))) {
            String linia;
            String aktualnaKategoria = null;

            while ((linia = reader.readLine()) != null) {
                if (linia.endsWith(":")) {
                    aktualnaKategoria = linia.substring(0, linia.length() - 1);
                    produkty.put(aktualnaKategoria, new ArrayList<>());
                } else if (!linia.isEmpty() && aktualnaKategoria != null) {
                    String[] daneProduktu = linia.split(",");
                    String nazwa = daneProduktu[0].trim();
                    String jednostka = daneProduktu[1].trim();
                    boolean czyCalkowite = jednostka.equalsIgnoreCase("sztuki");
                    produkty.get(aktualnaKategoria).add(new Produkt(nazwa, jednostka, czyCalkowite));
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania listy produktów: " + e.getMessage());
        }
    }

    public static void wczytajListeZakupow() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PLIK_ZAKUPY))) {
            String linia;
            String aktualnaKategoria = null;

            while ((linia = reader.readLine()) != null) {
                if (linia.endsWith(":")) {
                    aktualnaKategoria = linia.substring(0, linia.length() - 1);
                    listaZakupow.put(aktualnaKategoria, new LinkedHashMap<>());
                } else if (!linia.isEmpty() && aktualnaKategoria != null) {
                    String[] daneZakupu = linia.split(",");
                    String nazwa = daneZakupu[0].trim();
                    Double ilosc = Double.valueOf(daneZakupu[1].trim());
                    listaZakupow.get(aktualnaKategoria).put(nazwa, ilosc);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania listy zakupów: " + e.getMessage());
        }
    }

    public static void zapiszListeZakupow() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLIK_ZAKUPY))) {
            listaZakupow.forEach((kategoria, produkty) -> {
                writer.println(kategoria + ":");
                produkty.forEach((nazwa, ilosc) -> writer.println(nazwa + "," + ilosc));
            });
            System.out.println("Lista zakupów została zapisana.");
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisywania listy zakupów: " + e.getMessage());
        }
    }

    public static void wyczyscListeZakupow() {
        listaZakupow.clear();
    }
}
