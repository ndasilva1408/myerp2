package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;

import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class})


public class ComptabiliteManagerImplTest {
    private static ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();

    @InjectMocks
    private ComptabiliteManagerImpl classUnderTest;

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
    public void addReferenceFromNewJournal() throws NotFoundException {
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
    public void addReferenceFromOldJournal() throws NotFoundException {
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

    @Test
    void checkEcritureComptable() throws Exception {
        JournalComptable journalComptable1 = new JournalComptable();
        journalComptable1.setCode("AB");
        EcritureComptable ecritureComptable1 = new EcritureComptable();
        ecritureComptable1.setId(1);
        ecritureComptable1.setLibelle("Libelle");
        ecritureComptable1.setDate(localdate);
        ecritureComptable1.setJournal(journalComptable1);
        ecritureComptable1.setReference("AB-2020/00001");
        ecritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(123)));
        ecritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null,
                new BigDecimal(123), null));


        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        when(comptabiliteDao.getEcritureComptableByRef(ecritureComptable1.getReference())).thenReturn(ecritureComptable1);

        manager.checkEcritureComptable(ecritureComptable1);
    }

    @Test()
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable = new EcritureComptable();

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> classUnderTest.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("L'écriture comptable ne respecte pas les règles de gestion.");
    }


    //------ TEST RG2 ---


    @Test
    @Tag("RG2")
    @DisplayName("Soit 1234 en debit et credit , test d'équilibre de l'ecriture")
    public void checkEcritureComptableUnitRG2_WithTwoLignesEcrituresBalanced() throws Exception {

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(1);
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
    public void checkEcritureComptableUnitRG2_With0InDebitAndCredit() throws Exception {
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

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> classUnderTest.checkEcritureComptable(vEcritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");

    }

// --- TEST RG5

    @Test
    public void checkEcritureComptableReferenceRG5_WhenRefIsNull() throws Exception {

        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getReference()).thenReturn(null);



        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> classUnderTest.checkEcritureComptableReference(ecritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("La référence de l'écriture ne peut pas être nulle.");

    }


    @Test
    public void checkEcritureComptableReferenceRG5_whenJournalCodeIsWrong()  {

        String wrongCodeJournal = "AA";
        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getReference()).thenReturn("AB-2020/00001");
        when(ecritureComptable.getJournal()).thenReturn(journalComptable);
        when(ecritureComptable.getJournal().getCode()).thenReturn(wrongCodeJournal);

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> classUnderTest.checkEcritureComptableReference(ecritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("La référence de l'écriture AB ne correspond pas au code journal AA.");


    }

    @Test
    public void checkEcritureComptableReferenceRG5_whenYearIsWrong() {
        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getReference()).thenReturn("AB-2010/00001");
        when(ecritureComptable.getJournal()).thenReturn(journalComptable);
        when(ecritureComptable.getJournal().getCode()).thenReturn("AB");

        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> classUnderTest.checkEcritureComptableReference(ecritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("La référence de l'écriture 2010 ne correspond pas à l'année de l'écriture 2020.");


    }

    @Test
    public void checkEcritureComptableReferenceRG5_whenYearIdIsNull() throws Exception {


        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getReference()).thenReturn("AB-2020/00001");
        when(ecritureComptable.getJournal()).thenReturn(journalComptable);
        when(ecritureComptable.getJournal().getCode()).thenReturn("AB");
        when(ecritureComptable.getId()).thenReturn(null);
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        when(comptabiliteDao.getSequenceEcritureComptableByCodeYear(journalComptable.getCode(), 2020)).thenReturn(sequenceEcritureComptable);
        when(sequenceEcritureComptable.getDerniereValeur()).thenReturn(00000);
        manager.checkEcritureComptableReference(ecritureComptable);

    }

    @Test
    public void checkEcritureComptableReferenceRG5_whenSequenceNumberIsWrong() throws Exception {
        when(ecritureComptable.getDate()).thenReturn(localdate);
        when(ecritureComptable.getReference()).thenReturn("AB-2020/00001");
        when(ecritureComptable.getJournal()).thenReturn(journalComptable);
        when(ecritureComptable.getJournal().getCode()).thenReturn("AB");
        when(ecritureComptable.getId()).thenReturn(null);
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        when(comptabiliteDao.getSequenceEcritureComptableByCodeYear(journalComptable.getCode(), 2020)).thenReturn(sequenceEcritureComptable);
        when(sequenceEcritureComptable.getDerniereValeur()).thenReturn(120000);


        FunctionalException functionalException = assertThrows(FunctionalException.class,
                () -> classUnderTest.checkEcritureComptableReference(ecritureComptable));

        assertThat(functionalException.getMessage())
                .isEqualTo("Le numéro de séquence de l'écriture 00001 ne correspond pas à la dernière séquence du journal 120000.");


    }


//------------------------ ANCIEN TEST DEJA PRESENT--------------------------------------

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
