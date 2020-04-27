package com.dummy.myerp.model.bean.comptabilite;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SequenceEcritureComptableTest {
    private int annee = 2020;
    private int derniereValeur = 1;



    @Test
    void toStringTest() {
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setAnnee(annee);
        sequenceEcritureComptable.setDerniereValeur(derniereValeur);
        String expectedString = "SequenceEcritureComptable{annee=2020, derniereValeur=1}";
        String resultString = sequenceEcritureComptable.toString();
        assertThat(expectedString).isEqualTo(resultString);
    }
}
