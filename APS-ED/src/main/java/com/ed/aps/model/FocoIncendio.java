package com.ed.aps.model;

public class FocoIncendio {
    private String data;
    private String bioma;
    private String municipio;

    public FocoIncendio(String data, String bioma, String municipio) {
        this.data = data;
        this.bioma = bioma;
        this.municipio = municipio;
    }

    public String getData() { return data; }
    public String getBioma() { return bioma; }
    public String getMunicipio() { return municipio; }

    @Override
    public String toString() {
        return String.format("[%s | %s | %s]", data, bioma, municipio);
    }
}
