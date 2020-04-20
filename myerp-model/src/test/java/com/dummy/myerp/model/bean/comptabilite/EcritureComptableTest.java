package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class EcritureComptableTest {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                vLibelle,
                vDebit, vCredit);
        return vRetour;
    }

    @Mock
   LigneEcritureComptable ligneEcritureComptable;


    @Test
    @Tag("IsEquilibree")
    @DisplayName("Soit un débit  et un crédit égal, l'écriture est donc equilibrée")
    void isEquilibree_ShouldReturnTrueForEquilibrateEcriture() {

        EcritureComptable vEcriture = new EcritureComptable();


        //Arrange + Act
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", "200.50"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "33", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "33", "0"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "0", "33"));

       //Assert
        Assert.assertTrue(vEcriture.isEquilibree());

    }

    @Test
    @Tag("IsEquilibree")
    @DisplayName("Soit un débit et un crédit different, l'écriture est désequilibrée")
    void isEquilibree_ShouldReturnFalseForNonEquilibrateEcriture() {

        EcritureComptable vEcriture = new EcritureComptable();



        //Arrange + Act
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "210.50", "200.50"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "33", "223"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "33", "0"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "0", "3"));

        //Assert
        Assert.assertFalse(vEcriture.isEquilibree());
    }




    @Test
    @Tag("getTotalCredit")
    @DisplayName("Soit un credit total de 214, getTotalCredit doit return 214")
    void getTotalCredit_shouldReturnSommeofCredit(){
        EcritureComptable vEcriture = new EcritureComptable();

        //Arrange + Act
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", "200.50"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", "10.50"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "10", "0"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "0", "3"));

        //Assert
        Assert.assertEquals(vEcriture.getTotalCredit(),(BigDecimal.valueOf(214.00).setScale(2, RoundingMode.CEILING)));

    }

    @Test
    @Tag("getTotalCredit")
    @DisplayName("Soit un credit total de 0, getTotalCredit doit return null")
    void getTotalCredit_shouldReturnNull(){
        EcritureComptable vEcriture = new EcritureComptable();
        //Arrange + Act
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", "0"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "15120", "0"));

        //Assert
        Assert.assertEquals(vEcriture.getTotalCredit(),BigDecimal.ZERO);

    }


    @Test
    @Tag("getTotalDebit")
    @DisplayName("Soit un debit total de 30, getTotalCredit doit return30")
    void getTotalDebit_shouldReturnSommeofDebit(){
        EcritureComptable vEcriture = new EcritureComptable();
        //Arrange + Act
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", "200.50"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", "10.50"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "10", "0"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "0", "3"));

        //Assert
        Assert.assertEquals(vEcriture.getTotalDebit(),(BigDecimal.valueOf(30)));

    }

    @Test
    @Tag("getTotalDebit")
    @DisplayName("Soit un debit total de 0, getTotalDebit doit return null")
    void getTotalDebit_shouldReturnNull(){
        EcritureComptable vEcriture = new EcritureComptable();

        //Arrange + Act
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "0", "15820"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "0", "250"));

        //Assert
        Assert.assertEquals(vEcriture.getTotalDebit(),BigDecimal.ZERO);

    }
}
