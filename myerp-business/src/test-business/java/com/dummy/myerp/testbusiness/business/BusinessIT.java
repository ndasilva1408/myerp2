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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.dummy.myerp.business.impl.AbstractBusinessManager.configure;
import static com.dummy.myerp.testbusiness.business.BusinessTestCase.getBusinessProxy;
import static org.junit.Assert.*;

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


        EcritureComptable ecritureComptableTest = new EcritureComptable();

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
    public void chooseEcritureComptableID_thenDelete() {
        dao.getComptabiliteDao().deleteEcritureComptable(1);
    }

    //------------------------------------------------------------------Test Integration Differentes RG-----------------------------------------------------------

    @Test
    @Tag("RG2")
    @Rollback()
    public void givenEcritureWithoutRG2_WhenInsertEcritureComptable_NotPersisted() throws NotFoundException {
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

        //tst
        compteComptable1.getLibelle();
        compteComptable1.getNumero();
        ecritureComptable.getLibelle();
        ecritureComptable.getReference();
        ecritureComptable.getJournal();
        ecritureComptable.getDate();
        ecritureComptable.getJournal().getCode();
        ecritureComptable.getJournal().getLibelle();
        ligneEcritureComptable1.getLibelle();

        try {
            business.getComptabiliteManager().insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            message = e.getMessage();
        } finally {

            assertEquals((getBusinessProxy().getComptabiliteManager().getListEcritureComptable().size()), (listEcriture.size()));
            assertEquals((message), ("L'écriture comptable n'est pas équilibrée."));
        }
    }


  /*  @Tag("RG3")
    @Test
    @Rollback(true)
    public void givenEcritureWithoutRG3_WhenInsertEcritureComptable_NotPersisted() {

        List<EcritureComptable> listEcriture = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable = business.getComptabiliteManager().getListEcritureComptable().get(3);


        LigneEcritureComptable ligneEcritureComptableEdit = ecritureComptable.getListLigneEcriture().get(1);
        ligneEcritureComptableEdit.setCredit(new BigDecimal(-12000));
        ligneEcritureComptableEdit.setDebit(BigDecimal.ZERO);


        LigneEcritureComptable ligneEcritureComptableEdit1 = ecritureComptable.getListLigneEcriture().get(0);
        ligneEcritureComptableEdit1.setCredit(new BigDecimal(12000));
        ligneEcritureComptableEdit1.setDebit(BigDecimal.ZERO);

        ecritureComptable.getListLigneEcriture().remove(1);
        ecritureComptable.getListLigneEcriture().remove(0);
        ecritureComptable.getListLigneEcriture().add(ligneEcritureComptableEdit);
        ecritureComptable.getListLigneEcriture().add(ligneEcritureComptableEdit1);

        String message = null;

        try {
            business.getComptabiliteManager().insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            message = e.getMessage();
        } finally {

            assertEquals((getBusinessProxy().getComptabiliteManager().getListEcritureComptable().size()), (listEcriture.size()));
            assertEquals(("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit."),(message));
        }
    }*/

    @Tag("RG4")
    @Test
    public void checkRG4_givenEcritureWithNegativeNumberAndNewSequence_WhenInsertEcriture_Persisted() throws NotFoundException {
        List<EcritureComptable> listEcriture = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
        EcritureComptable ecritureComptable = business.getComptabiliteManager().getListEcritureComptable().get(0);
        ecritureComptable.getListLigneEcriture().get(0).setDebit(new BigDecimal(-1500));
        ecritureComptable.getListLigneEcriture().get(1).setCredit(new BigDecimal(-1500));
        business.getComptabiliteManager().addReference(ecritureComptable);

        //when
        String message = null;
        try {
            business.getComptabiliteManager().insertEcritureComptable(ecritureComptable);
        } catch (FunctionalException e) {
            e.getMessage();
        } finally {
            //then
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ecritureComptable.getDate());
            int yearInRef = calendar.get(Calendar.YEAR);

            assertNotNull(dao.getComptabiliteDao().getSequenceEcritureComptableByCodeYear(ecritureComptable.getJournal().getCode(), yearInRef));

            assertEquals((business.getComptabiliteManager().getListEcritureComptable().size()), (listEcriture.size()));

            getBusinessProxy().getComptabiliteManager().deleteEcritureComptable(ecritureComptable.getId());
        }
    }
}
