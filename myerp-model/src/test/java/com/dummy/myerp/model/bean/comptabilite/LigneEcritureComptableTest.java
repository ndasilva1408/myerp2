package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

public class LigneEcritureComptableTest {


    @Test
    public void toStringTest(){
        LigneEcritureComptable ligne = new LigneEcritureComptable(
                new CompteComptable(1,"dummy compte"),
                "dummy ligne",
                new BigDecimal(100.00),
                new BigDecimal(0));

        String expectedString = "LigneEcritureComptable{compteComptable="+ligne.getCompteComptable()+
                ", libelle='"+ligne.getLibelle()+"', "+
                "debit="+ligne.getDebit()+", "+
                "credit="+ligne.getCredit()+"}";
        String resultString = ligne.toString();
        assertThat (expectedString).isEqualTo(resultString);
    }
}
