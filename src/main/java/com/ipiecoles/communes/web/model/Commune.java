package com.ipiecoles.communes.web.model;

import org.springframework.security.access.annotation.Secured;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Entity
public class Commune {
//    @Id
//    @Column(length = 5)
//    private String codeInsee;
//
//    private String nom;
//
//    @Column(length = 5)
//    private String codePostal;
//
//    private Double latitude;
//
//    private Double longitude;

    public static final String REGEX_CODE_INSEE = "^[0-9]{1}[0-9AB]{1}[0-9]{3}$";
    public static final String REGEX_CODE_POSTAL = "^[0-9]{5}$";
    public static final String REGEX_NOM_COMMUNE = "^[A-Za-z-' ]+[0-9]{0,2}$";
    public static final String REGEX_LATITUDE = "^[0-9]{0,3}[.]{1}[0-9]{0,11}$";

    //5 chiffres (le deuxième caractère peut être A ou B)
    //Obligatoire
    @Id
    @Column(length = 5)
    @Pattern(regexp=REGEX_CODE_INSEE,message="Le code INSEE doit contenir 5 chiffres (Le deuxième caractère peut être A ou B pour les communes de Corse)")
    @NotBlank
    private String codeInsee;

    //Taille max 50
    //Obligatoire
    //Lettres majuscules minuscules, tirets, apostrophes, espaces et éventuellement terminé par 1 ou 2 chiffres (arrondissements)
//    @Size(max = 50)
    @Pattern(regexp=REGEX_NOM_COMMUNE,message="Le nom de la commune ne peut contenir que des lettres, des tirets, des espaces et éventuellement le numéro d'arrondissement")
    private String nom;

    @Column(length = 5)
    //5 chiffres
    //Obligatoire
    @Pattern(regexp=REGEX_CODE_POSTAL,message="Le code postal doit contenir 5 chiffres")
    private String codePostal;

    //Intervalle pour la latitude ?
    // Facultatif
//    @Pattern(regexp=REGEX_LATITUDE,message="La latitude doit contenir 5 chiffres")
//    @Min(value=-28, message="La latitude ne peut être inférieure à -28")
//    @Max(value=67, message="La latitude ne peut être supérieure à 67")
    @Min(value=-90, message="doit être supérieure ou égale à -90")
    @Max(value=90, message="doit être inférieure ou égale à 90")
    private Double latitude;

    //Intervalle pour la longitude ?
    // Facultatif
//    @Min(value=-179, message="La longitude ne peut être inférieure à -179")
//    @Max(value=168, message="La longitude ne peut être supérieure à 168")
    @Min(value=-179, message="doit être supérieure ou égale à -180")
    @Max(value=168, message="doit être inférieure ou égale à 180")
    private Double longitude;

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

    public Commune() {
    }

    public Commune(String codeInsee, String nom, String codePostal, Double latitude, Double longitude) {
        this.codeInsee = codeInsee;
        this.nom = nom;
        this.codePostal = codePostal;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getDistance(Double latitude, Double longitude){
        Double lat1 = Math.toRadians(latitude);
        Double lng1 = Math.toRadians(longitude);
        Double lat2 = Math.toRadians(this.latitude);
        Double lng2 = Math.toRadians(this.longitude);

        double dlon = lng2 - lng1;
        double dlat = lat2 - lat1;

        double a = Math.pow((Math.sin(dlat/2)),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon/2),2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

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
