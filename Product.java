class Produkt {
    private String nazwa;
    private String jednostka;
    private boolean czyCalkowite;

    public Produkt(String nazwa, String jednostka, boolean czyCalkowite) {
        this.nazwa = nazwa;
        this.jednostka = jednostka;
        this.czyCalkowite = czyCalkowite;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getJednostka() {
        return jednostka;
    }

    public boolean isCzyCalkowite() {
        return czyCalkowite;
    }
    @Override
    public String toString() {
        return nazwa + " (" + jednostka + ")";
    }
}