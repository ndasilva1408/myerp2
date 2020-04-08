package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.dummy.myerp.model.bean.comptabilite.CompteComptable.getByNumero;
import static org.assertj.core.api.Assertions.assertThat;

public class CompteComptableTest {


    @Tag("getByNumero")
    @Test
    void getByNumero_ShouldReturnCompteComptableByNumero(){
        //Arrange
        List <CompteComptable>pList = new ArrayList<>();
        CompteComptable compteComptable = new CompteComptable(8,"-1455");
        CompteComptable compteComptable2 = new CompteComptable(42,"5434875");
        pList.add(compteComptable);
        pList.add(compteComptable2);

        //Act
        getByNumero(pList,8);

        //Assert
        assertThat(getByNumero(pList,8) == compteComptable);
    }
    @Tag("getByNumero")
    @Test
    void getByNumero_ShouldReturnNull_WhenpNumeroisNull(){
        //Arrange
        List <CompteComptable>pList = new ArrayList<>();
        CompteComptable compteComptable = new CompteComptable(8,"-1455");
        CompteComptable compteComptable2 = new CompteComptable(42,"5434875");
        pList.add(compteComptable);
        pList.add(compteComptable2);

        //Act
        getByNumero(pList,3);

        //Assert
        assertThat(getByNumero(pList,3) == null);

    }

}
