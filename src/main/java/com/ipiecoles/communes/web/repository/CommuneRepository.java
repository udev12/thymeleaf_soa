package com.ipiecoles.communes.web.repository;

import com.ipiecoles.communes.web.model.Commune;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Dans notre repository, on définit entre-autres, les méthodes "countDistinctCodePostal()" et "countDistinctNom()".
 * On hérite de la classe "PagingAndSortingRepository"
 */
@Repository
public interface CommuneRepository extends PagingAndSortingRepository<Commune, String> {
    @Query("Select count(distinct c.codePostal) from Commune c")
    long countDistinctCodePostal();

    @Query("select count(distinct c.nom) from Commune c")
    long countDistinctNom();

    @Override
    Page<Commune> findAll(Pageable pageable);

    Page<Commune> findAllByNomLike(String nom, Pageable pageable);

    List<Commune> findByLatitudeBetweenAndLongitudeBetween(Double latMin, Double latMax, Double longMin, Double longMax);

    Page<Commune> findByNomContainingIgnoreCase(String search, Pageable pageable);

    Page<Commune> findByNomIgnoreCase(String search, Pageable pageable);

    @Query("SELECT c FROM Commune c WHERE c.nom = ?1")
    Commune findCommuneByNom(String search);

    @Query("SELECT c FROM Commune c WHERE c.codeInsee = ?1")
    Commune findCommuneByCodeInsee(String search);

}
