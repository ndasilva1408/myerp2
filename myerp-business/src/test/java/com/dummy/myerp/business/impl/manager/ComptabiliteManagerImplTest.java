package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;

import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class})
@EnableRuleMigrationSupport

public class ComptabiliteManagerImplTest {
    private static ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    EcritureComptable ecritureComptable;
    @Mock
    DaoProxy daoProxy;
    @Mock
    ComptabiliteDao comptabiliteDao;
    @Mock
    JournalComptable journalComptable;
    @Mock
    SequenceEcritureComptable sequenceEcritureComptable;
    @Mock
    Calendar calendar;


    @Mock
    private BusinessProxy businessProxy;


    Date localdate = new Date();


    @BeforeEach
    public void init() {
        AbstractBusinessManager.configure(businessProxy, daoProxy, TransactionManager.getInstance());

    }


    @Test
    public void addReferenceFromNewJournal() {
        //GIVEN
        int year = 2020;
        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getJournal()).thenReturn(journalComptable);
        when(journalComptable.getCode()).thenReturn("AC");
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        when(comptabiliteDao.getSequenceEcritureComptableByCodeYear(journalComptable.getCode(), year)).thenReturn(null);
        when(ecritureComptable.getReference()).thenReturn("AC-2020/00001");
        String assertRef = "AC-2020/00001";

        //WHEN
        manager.addReference(ecritureComptable);
        final String ref = ecritureComptable.getReference();

        //THEN
        assertThat(ref.equals(assertRef));


    }


    @Test
    public void addReferenceFromOldJournal() {
        //GIVEN
        int year = 2020;
        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getJournal()).thenReturn(journalComptable);
        when(journalComptable.getCode()).thenReturn("AC");
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        when(comptabiliteDao.getSequenceEcritureComptableByCodeYear(journalComptable.getCode(), year)).thenReturn(sequenceEcritureComptable);
        when(ecritureComptable.getReference()).thenReturn("AC-2020/00001");
        when(sequenceEcritureComptable.getDerniereValeur()).thenReturn(00003);
        String assertRef = "AC-2020/00004";

        //WHEN
        manager.addReference(ecritureComptable);
        final String ref = ecritureComptable.getReference();

        //THEN
        assertThat(ref.equals(assertRef));


    }
/*
    @Test
    void checkEcritureComptable() throws Exception {

        EcritureComptable ecritureComptable1 = new EcritureComptable();
        ecritureComptable1.setLibelle("Libelle");
        ecritureComptable1.setDate(localdate);
        ecritureComptable1.setJournal(new JournalComptable("AC", "Achat"));

        ecritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(123)));
        ecritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null,
                new BigDecimal(123), null));
        manager.checkEcritureComptable(ecritureComptable1);
    }

    @Test()
    public void checkEcritureComptableUnitViolation() throws Exception {
        exception.expect(FunctionalException.class);
        exception.expectMessage("L'écriture comptable ne respecte pas les règles de gestion.");
        EcritureComptable vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    //------ TEST RG2 ---


    @Test
    @Tag("RG2")
    @DisplayName("Soit 1234 en debit et credit , test d'équilibre de l'ecriture")
    public void checkEcritureComptableUnitRG2WithTwoLignesEcrituresBalanced() throws Exception {

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("BQ", "Barbecue"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.setReference("BQ-2020/00001");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null
                , new BigDecimal(1234), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null
                , null, new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(ecritureComptable);
    }


    @Test
    @Tag("RG2")
    @DisplayName("Soit 0 en debit et credit , test d'équilibre de l'ecriture")
    public void checkEcritureComptableUnitRG2With0InDebitAndCredit() throws Exception {
        exception.expect(FunctionalException.class);
        exception.expectMessage("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(0),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(0)));

        manager.checkEcritureComptableUnit(vEcritureComptable);

        assertThat(vEcritureComptable.isEquilibree());

    }


    @Test
    public void checkEcritureComptableUnitRG2() throws Exception {
        exception.expect(FunctionalException.class);
        exception.expectMessage("L'écriture comptable n'est pas équilibrée.");

        EcritureComptable ecritureComptable1 = new EcritureComptable();
        ecritureComptable1.setLibelle("Libelle");
        ecritureComptable1.setDate(localdate);
        ecritureComptable1.setJournal(new JournalComptable("AC", "Achat"));

        ecritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(12333)));
        ecritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null,
                new BigDecimal(123), null));
        manager.checkEcritureComptableUnit(ecritureComptable1);
    }


    @Test
    public void checkEcritureComptableReferenceRG5WhenRefIsNull() throws Exception {
        exception.expect(FunctionalException.class);
        exception.expectMessage("La référence de l'écriture ne peut pas être nulle.");

        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getReference()).thenReturn(null);

        manager.checkEcritureComptableReference(ecritureComptable);
    }


   @Test
    public void checkEcritureComptableReferenceRG5whenJournalCodeIsWrong() throws Exception {
        
        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getReference()).thenReturn("AB-2020/00001");
        when(calendar.get(Calendar.YEAR)).thenReturn(2020);
        when(ecritureComptable.getJournal().getCode()).thenReturn("AB");

        manager.checkEcritureComptableReference(ecritureComptable);
    }*/

    @Test
    public void checkEcritureComptableUnitViolation() {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit(vEcritureComptable));
    }

    @Test
    public void checkEcritureComptableUnitRG2() {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(1234)));

       assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit(vEcritureComptable));
    }

    @Test
    public void checkEcritureComptableUnitRG3() {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(-123),
                null));

        assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit(vEcritureComptable));
    }

}
