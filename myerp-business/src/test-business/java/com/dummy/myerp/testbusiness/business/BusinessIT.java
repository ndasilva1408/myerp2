package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.dummy.myerp.business.impl.AbstractBusinessManager.configure;
import static com.dummy.myerp.testbusiness.business.BusinessTestCase.getBusinessProxy;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/com/dummy/myerp/testbusiness/business/bootstrapContext.xml")
public class BusinessIT {


    private ComptabiliteManagerImpl manager;


    @Autowired
    private BusinessProxy business;
    @Autowired
    private DaoProxy dao;
    @Autowired
    private TransactionManager transactionManager;

    @Before
    public void init() {
        configure(business, dao, transactionManager);
    }


    @Before
    public void setup() {
        manager = new ComptabiliteManagerImpl();
    }

    //------------------------------------------------------------------Test DAO -----------------------------------------------------------
    @Test
    public void getListCompteComptableTest() {
        assertFalse(manager.getListCompteComptable().isEmpty());
    }

    @Test
    public void getListJournalComptableTest() {
        assertFalse(manager.getListJournalComptable().isEmpty());
    }

    @Test
    public void getListEcritureComptableTest() {
        assertFalse(manager.getListEcritureComptable().isEmpty());
    }


    @Test
    public void givenEcritureComptable_thenInsertEcritureComptable() {
        BigDecimal bd = new BigDecimal(10);
        bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
        Date date = new Date();
        JournalComptable journalComptable = new JournalComptable();
        journalComptable.setCode("AC");
        journalComptable.setLibelle("Achat");

        CompteComptable compteComptable = dao.getComptabiliteDao().getListCompteComptable().get(1);

        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable();
        ligneEcritureComptable.setCompteComptable(compteComptable);
        ligneEcritureComptable.setLibelle("test");
        ligneEcritureComptable.setDebit(bd);
        ligneEcritureComptable.setCredit(bd);

        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable();
        ligneEcritureComptable1.setCompteComptable(compteComptable);
        ligneEcritureComptable1.setLibelle("test");
        ligneEcritureComptable1.setDebit(bd);
        ligneEcritureComptable1.setCredit(bd);

        EcritureComptable ecritureComptable1 = new EcritureComptable();
        ecritureComptable1.setDate(date);

        ecritureComptable1.setLibelle("IntegrationTest1");
        ecritureComptable1.setReference("AC-2020/00001");
        ecritureComptable1.setJournal(journalComptable);
        ecritureComptable1.getListLigneEcriture().add(ligneEcritureComptable);
        ecritureComptable1.getListLigneEcriture().add(ligneEcritureComptable1);


        dao.getComptabiliteDao().insertEcritureComptable(ecritureComptable1);


    }

    @Test
    public void pickEcritureComptable_thenUpdateit() {
        String update = "Update Libelle";
        List<EcritureComptable> ecritureComptableList = business.getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable2 = ecritureComptableList.get(1);
        ecritureComptable2.setLibelle("Update Libelle");

        dao.getComptabiliteDao().updateEcritureComptable(ecritureComptable2);

        assertEquals(ecritureComptable2.getLibelle(), (update));
    }

    @Test
    public void givenUpdatedEcriture_thenUpdate() throws FunctionalException, NotFoundException {
        List<EcritureComptable> ecritureComptableList = business.getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable = ecritureComptableList.get(3);
        String oldLibelle = ecritureComptable.getLibelle();
        String newLibelle = "Libelle Test Update";
        String ref = ecritureComptable.getReference();
        ecritureComptable.setLibelle(newLibelle);

        business.getComptabiliteManager().updateEcritureComptable(ecritureComptable);
        assertEquals(dao.getComptabiliteDao().getEcritureComptableByRef(ref).getLibelle(), newLibelle);

        //reinit
        ecritureComptable.setLibelle(oldLibelle);
        business.getComptabiliteManager().updateEcritureComptable(ecritureComptable);
    }

    //Test soutenance odsq

    @Test
    public void chooseEcritureComptableID_thenDelete() {
        dao.getComptabiliteDao().deleteEcritureComptable(1);
    }

    //------------------------------------------------------------------Test Integration Differentes RG-----------------------------------------------------------

    @Test
    @Tag("RG2")
    @Rollback()
    public void checkRG2_WhenInsertEcritureComptable() throws NotFoundException {
        String message = null;

        CompteComptable compteComptable1 = business.getComptabiliteManager().getListCompteComptable().get(1);
        CompteComptable compteComptable2 = business.getComptabiliteManager().getListCompteComptable().get(2);


        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable();
        ligneEcritureComptable1.setCredit(BigDecimal.valueOf(2000));
        ligneEcritureComptable1.setLibelle("test");
        ligneEcritureComptable1.setCompteComptable(compteComptable1);
        ligneEcritureComptable1.setDebit(BigDecimal.ZERO);

        LigneEcritureComptable ligneEcritureComptable2 = new LigneEcritureComptable();
        ligneEcritureComptable2.setCredit(BigDecimal.valueOf(100));
        ligneEcritureComptable2.setLibelle("test");
        ligneEcritureComptable2.setDebit(BigDecimal.ZERO);
        ligneEcritureComptable2.setCompteComptable(compteComptable2);


        List<EcritureComptable> listEcriture = business.getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable = dao.getComptabiliteDao().getEcritureComptable(-1);
        ecritureComptable.getListLigneEcriture().add(0, ligneEcritureComptable1);
        ecritureComptable.getListLigneEcriture().add(1, ligneEcritureComptable2);

        ecritureComptable.getListLigneEcriture().get(1).setCredit(new BigDecimal(1300));


        try {
            business.getComptabiliteManager().insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            message = e.getMessage();
        } finally {

            assertEquals((getBusinessProxy().getComptabiliteManager().getListEcritureComptable().size()), (listEcriture.size()));
            assertEquals((message), ("L'écriture comptable n'est pas équilibrée."));
        }
    }


    @Tag("RG3")
    @Test
    @Rollback(true)
    public void checkRG3_WhenInsertEcritureComptable() {

        List<EcritureComptable> listEcriture = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable = new EcritureComptable();
        JournalComptable journalComptable = new JournalComptable();
        Date date = new Date();

        journalComptable.setLibelle("Garantie");
        journalComptable.setCode("GR");

        ecritureComptable.setJournal(journalComptable);
        ecritureComptable.setDate(date);
        ecritureComptable.setReference("GR-2020/00001");
        ecritureComptable.setLibelle("Garantie");


        LigneEcritureComptable ligneEcritureComptableEdit = new LigneEcritureComptable();
        ligneEcritureComptableEdit.setCredit(new BigDecimal(-12000));
        ligneEcritureComptableEdit.setDebit(BigDecimal.ZERO);
        ligneEcritureComptableEdit.setLibelle("test");
        ligneEcritureComptableEdit.setCompteComptable(business.getComptabiliteManager().getListCompteComptable().get(1));


        LigneEcritureComptable ligneEcritureComptableEdit1 = new LigneEcritureComptable();
        ligneEcritureComptableEdit1.setCredit(new BigDecimal(12000));
        ligneEcritureComptableEdit1.setDebit(BigDecimal.ZERO);
        ligneEcritureComptableEdit1.setLibelle("test");
        ligneEcritureComptableEdit1.setCompteComptable(business.getComptabiliteManager().getListCompteComptable().get(1));


        ecritureComptable.getListLigneEcriture().add(ligneEcritureComptableEdit);
        ecritureComptable.getListLigneEcriture().add(ligneEcritureComptableEdit1);


        String message = null;

        try {
            business.getComptabiliteManager().insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            message = e.getMessage();
        } finally {

            assertEquals((getBusinessProxy().getComptabiliteManager().getListEcritureComptable().size()), (listEcriture.size()));
            assertEquals(("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit."), (message));
        }

    }


    @Tag("RG4")
    @Test
    public void checkRG4_givenEcritureWithNegativeNumberAndNewSequence_WhenInsertEcriture_Persisted() throws NotFoundException {
        List<EcritureComptable> listEcriture = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable = business.getComptabiliteManager().getListEcritureComptable().get(0);
        ecritureComptable.getListLigneEcriture().get(0).setDebit(new BigDecimal(-1500));
        ecritureComptable.getListLigneEcriture().get(1).setCredit(new BigDecimal(-1500));
        business.getComptabiliteManager().addReference(ecritureComptable);


        try {
            business.getComptabiliteManager().insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            e.getMessage();
        } finally {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ecritureComptable.getDate());
            int yearInRef = calendar.get(Calendar.YEAR);

            assertNotNull(dao.getComptabiliteDao().getSequenceEcritureComptableByCodeYear(ecritureComptable.getJournal().getCode(), yearInRef));

            assertEquals((business.getComptabiliteManager().getListEcritureComptable().size()), (listEcriture.size()));

            getBusinessProxy().getComptabiliteManager().deleteEcritureComptable(ecritureComptable.getId());
        }
    }

    @Tag("RG5")
    @Test
    public void CheckRG5_givenEcriture_PersistedWithGoodSeqNbr() throws NotFoundException {
        List<EcritureComptable> ecritureComptableList = business.getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable = ecritureComptableList.get(3);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ecritureComptable.getDate());
        SequenceEcritureComptable sequenceEcritureComptable = dao.getComptabiliteDao().getSequenceEcritureComptableByCodeYear(
                ecritureComptable.getJournal().getCode(),
                calendar.get(Calendar.YEAR));

        EcritureComptable ecritureComptable1 = this.getEcritureComptable();
        ecritureComptable1.setDate(ecritureComptable.getDate());
        ecritureComptable1.setJournal(ecritureComptable1.getJournal());
        business.getComptabiliteManager().addReference(ecritureComptable1);

        try {
            business.getComptabiliteManager().insertEcritureComptable(ecritureComptable1);
        } catch (FunctionalException e) {
            e.getMessage();
        } finally {

            assertNotNull(sequenceEcritureComptable);
            assertEquals((business.getComptabiliteManager().getListEcritureComptable().size()), ecritureComptableList.size() + 1);

            business.getComptabiliteManager().deleteEcritureComptable(ecritureComptable1.getId());
        }


    }

    @Tag("RG6")
    @Test
    public void checkRG6_whenUpdate() {
        List<EcritureComptable> ecritureComptableList = business.getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable = ecritureComptableList.get(0);
        ecritureComptable.setReference(ecritureComptableList.get(1).getReference());
        ecritureComptable.getJournal().setCode(ecritureComptableList.get(1).getJournal().getCode());
        Exception exception = assertThrows(FunctionalException.class, () -> business.getComptabiliteManager().updateEcritureComptable(ecritureComptable));
        assertEquals("Une autre écriture comptable existe déjà avec la même référence.", exception.getMessage());
        assertEquals(business.getComptabiliteManager().getListEcritureComptable().size(), ecritureComptableList.size());
    }


    private EcritureComptable getEcritureComptable() {
        List<CompteComptable> compteComptableList = business.getComptabiliteManager().getListCompteComptable();
        List<JournalComptable> journalComptableList = business.getComptabiliteManager().getListJournalComptable();

        JournalComptable journalComptable = journalComptableList.get(0);
        CompteComptable compteComptable1 = compteComptableList.get(0);
        CompteComptable compteComptable2 = compteComptableList.get(1);

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("IntegrationTestLibelle");
        ecritureComptable.setJournal(journalComptable);

        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable();
        ligneEcritureComptable1.setCompteComptable(compteComptable1);
        ligneEcritureComptable1.setDebit(new BigDecimal(13000));
        ligneEcritureComptable1.setLibelle("IntegrationTestLibelle");

        LigneEcritureComptable ligneEcritureComptable2 = new LigneEcritureComptable();
        ligneEcritureComptable2.setCompteComptable(compteComptable2);
        ligneEcritureComptable2.setCredit(new BigDecimal(13000));
        ligneEcritureComptable2.setLibelle("IntegrationTestLibelle");

        ecritureComptable.getListLigneEcriture().add(ligneEcritureComptable1);
        ecritureComptable.getListLigneEcriture().add(ligneEcritureComptable2);

        return ecritureComptable;
    }
}
