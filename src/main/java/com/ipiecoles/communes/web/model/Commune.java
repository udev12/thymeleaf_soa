package com.ipiecoles.communes.web.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 *
 */
@Entity
public class Commune {

    public static final String REGEX_CODE_INSEE = "^[0-9]{1}[0-9AB]{1}[0-9]{3}$";
    public static final String REGEX_CODE_POSTAL = "^[0-9]{5}$";
    public static final String REGEX_NOM_COMMUNE = "^[A-Za-z-' ]+[0-9]{0,2}$";
//    public static final String REGEX_LATITUDE = "^[0-9]{0,3}[.]{1}[0-9]{0,11}$";

    @Id
    @Length(max = 5)
    @NotBlank
    private String codeInsee;

    @NotBlank
    private String nom;

    @Length(max = 5)
    @NotBlank
    private String codePostal;

    private Double latitude;

    private Double longitude;

    public Commune() {
    }

    // Attention, créer le constructeur avant les annotations des attributs, donc pas de "@NotBlanl" das le constructeur
    public Commune(String codeInsee, String nom, String codePostal, Double latitude, Double longitude) {
        this.codeInsee = codeInsee;
        this.nom = nom;
        this.codePostal = codePostal;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCodeInsee() {
        return codeInsee;
    }

    public void setCodeInsee(String codeInsee) {
        this.codeInsee = codeInsee;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getDistance(Double latitude, Double longitude) {
        Double lat1 = Math.toRadians(latitude);
        Double lng1 = Math.toRadians(longitude);
        Double lat2 = Math.toRadians(this.latitude);
        Double lng2 = Math.toRadians(this.longitude);

        double dlon = lng2 - lng1;
        double dlat = lat2 - lat1;

        double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(6371.009 * c);
    }

    @Override
    public String toString() {
        return "Commune{" +
                "codeInsee='" + codeInsee + '\'' +
                ", nom='" + nom + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
