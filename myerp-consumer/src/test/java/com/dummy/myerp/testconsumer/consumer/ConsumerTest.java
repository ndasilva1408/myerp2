package com.dummy.myerp.testconsumer.consumer;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:consumerContextTest.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConsumerTest {
    @Autowired
    ComptabiliteDaoImpl dao;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getListCompteComptable_isNotEmpty() {
        List<CompteComptable> list = dao.getListCompteComptable();
        assertFalse(list.isEmpty());
    }

    @Test
    public void getListJournalComptable_isNotEmpty() {
        List<JournalComptable> list = dao.getListJournalComptable();
        assertFalse(list.isEmpty());
    }

    @Test
    public void getListEcritureComptable_isNotEmpty() {
        List<EcritureComptable> list = dao.getListEcritureComptable();
        assertFalse(list.isEmpty());
    }

    @Test
    public void GivenAnneeAndJournal_whenGetSequenceFromJournalAndAnnee_isNotNull() throws NotFoundException {
        SequenceEcritureComptable seq = dao.getSequenceEcritureComptableByCodeYear("VE",2020);
        assertNotNull(seq);
    }

    @Test
    public void GivenAnneeAndJournal_whenGetSequenceFromJournalAndAnnee_throwsNotFoundException() throws NotFoundException {
        exception.expect(NotFoundException.class);
        exception.expectMessage("SequenceEcritureComptable non trouvée : journal=AC annee=2002");

        SequenceEcritureComptable seq = dao.getSequenceEcritureComptableByCodeYear("AC",2002);

    }

    @Test
    public void GivenId_whenGetEcritureComptable_isNotNull() throws NotFoundException {
        EcritureComptable ecritureComptable = dao.getEcritureComptable(-1);
        assertNotNull(ecritureComptable);
    }

    @Test
    public void GivenId_whenGetEcritureComptable_throwsNotFoundException() throws NotFoundException {
        exception.expect(NotFoundException.class);
        exception.expectMessage("EcritureComptable non trouvée : id=1");
        EcritureComptable ecritureComptable = dao.getEcritureComptable(1);
    }

    @Test
    public void GivenRef_whenGetEcritureComptableByRef_isNotNull() throws NotFoundException {
        EcritureComptable ecritureComptable = dao.getEcritureComptableByRef("AC-2016/00001");
        assertNotNull(ecritureComptable);
    }

    @Test
    public void GivenId_whenGetEcritureComptableByRef_throwsNotFoundException() throws NotFoundException {
        exception.expect(NotFoundException.class);
        exception.expectMessage("EcritureComptable non trouvée : reference=AC-2000/00001");
        EcritureComptable ecritureComptable = dao.getEcritureComptableByRef("AC-2000/00001");
    }

    @Test
    public void GivenId_whenLoadListLigneEcriture_isNotNull() {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(-1);

        dao.loadListLigneEcriture(ecritureComptable);
        assertFalse(ecritureComptable.getListLigneEcriture().isEmpty());
    }

    @Test
    public void GivenEmptyId_whenLoadListLigneEcriture_isNull(){
        EcritureComptable ecritureComptable = new EcritureComptable();

        dao.loadListLigneEcriture(ecritureComptable);
        assertTrue(ecritureComptable.getListLigneEcriture().isEmpty());
    }






    @Test
    public void GivenEcritureComptable_WhenInsertFullEcritureComptable_isInserted(){
        //given
        int nbResult = dao.getListEcritureComptable().size();
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setLibelle("TestDao");
        ecritureComptable.setDate(new Date());

        JournalComptable journal = new JournalComptable();
        journal.setCode("AC");
        journal.setLibelle("Achat");

        ecritureComptable.setJournal(journal);
        ecritureComptable.setReference("AC-2016/00002");

        //when
        dao.insertEcritureComptable(ecritureComptable);

        //then
        assertEquals((dao.getListEcritureComptable().size()),(nbResult+1));
        assertNotNull(ecritureComptable.getId());

        //suppression de l'enregistrement pour garantir l'intégrité des tests
        dao.deleteEcritureComptable(ecritureComptable.getId());
    }

    @Test
    public void GivenEcritureComptable_WhenUpdateEcritureComptable_isUpdated() throws NotFoundException {
        //given
        Calendar now = Calendar.getInstance();

        String newLibelle = "Changement libelle";
        EcritureComptable oldEcritureComptable = dao.getEcritureComptable(-1);
        String oldLibelle = oldEcritureComptable.getLibelle();
        oldEcritureComptable.setLibelle(newLibelle);
        oldEcritureComptable.setDate(now.getTime());

        //when
        dao.updateEcritureComptable(oldEcritureComptable);

        //then
        EcritureComptable newEcritureComptable = dao.getEcritureComptable(-1);
        Calendar updatedDate = Calendar.getInstance();
        updatedDate.setTime(newEcritureComptable.getDate());
        assertEquals((now.get(Calendar.DATE)),(updatedDate.get(Calendar.DATE)));
        assertEquals((newEcritureComptable.getLibelle()),(newLibelle));

        oldEcritureComptable.setLibelle(oldLibelle);
        dao.updateEcritureComptable(oldEcritureComptable);
    }

    @Test
    @Rollback(true)
    public void GivenIdEcritureComptable_WhenDeleteEcritureComptable_ObjectIsDeletedAndLigneEcritureComptableIsDeleted() throws NotFoundException {
        // given
        EcritureComptable ecritureComptable = dao.getEcritureComptable(-1);
        ecritureComptable.setId(null);
        dao.insertEcritureComptable(ecritureComptable);
        int nbEcriture = dao.getListEcritureComptable().size();

        //when
        dao.deleteEcritureComptable(ecritureComptable.getId());

        //then
        assertEquals((dao.getListEcritureComptable().size()),(nbEcriture-1));
    }
}
