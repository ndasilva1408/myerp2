package com.dummy.myerp.testconsumer.consumer;


import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


@SpringJUnitConfig(locations = {
    "classpath:com/dummy/myerp/testconsumer/consumer/bootstrapContext.xml"})

public class ComptabiliteDaoImpIT extends AbstractDbConsumer {

  @Autowired
  private ComptabiliteDao dao;
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void getListCompteComptable() {
    assertThat(dao.getListCompteComptable().isEmpty()).isFalse();

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
  public void GivenId_whenGetEcritureComptable_isNotNull() throws NotFoundException {
    EcritureComptable ecritureComptable = dao.getEcritureComptable(-1);
    assertNotNull(ecritureComptable);
  }



  @Test
  public void GivenRef_whenGetEcritureComptableByRef_isNotNull() throws NotFoundException {
    EcritureComptable ecritureComptable = dao.getEcritureComptableByRef("AC-2016/00001");
    assertNotNull(ecritureComptable);
  }





  @Test
  public void GivenEmptyId_whenLoadListLigneEcriture_isNull(){
    EcritureComptable ecritureComptable = new EcritureComptable();

    dao.loadListLigneEcriture(ecritureComptable);
    assertTrue(ecritureComptable.getListLigneEcriture().isEmpty());
  }






  @Test
  public void GivenEcritureComptable_WhenInsertFullEcritureComptable_isInserted(){

    int nbResult = dao.getListEcritureComptable().size();
    EcritureComptable ecritureComptable = new EcritureComptable();
    ecritureComptable.setLibelle("TestDao");
    ecritureComptable.setDate(new Date());

    JournalComptable journal = new JournalComptable();
    journal.setCode("AC");
    journal.setLibelle("Achat");

    ecritureComptable.setJournal(journal);
    ecritureComptable.setReference("AC-2016/00002");

    dao.insertEcritureComptable(ecritureComptable);


    assertEquals((dao.getListEcritureComptable().size()),(nbResult+1));
    assertNotNull(ecritureComptable.getId());

    dao.deleteEcritureComptable(ecritureComptable.getId());
  }

  @Test
  public void GivenEcritureComptable_WhenUpdateEcritureComptable_isUpdated() throws NotFoundException {

    Calendar now = Calendar.getInstance();

    String newLibelle = "Changement libelle";
    EcritureComptable oldEcritureComptable = dao.getEcritureComptable(-1);
    String oldLibelle = oldEcritureComptable.getLibelle();
    oldEcritureComptable.setLibelle(newLibelle);
    oldEcritureComptable.setDate(now.getTime());


    dao.updateEcritureComptable(oldEcritureComptable);


    EcritureComptable newEcritureComptable = dao.getEcritureComptable(-1);
    Calendar updatedDate = Calendar.getInstance();
    updatedDate.setTime(newEcritureComptable.getDate());
    assertEquals((now.get(Calendar.DATE)),(updatedDate.get(Calendar.DATE)));
    assertEquals((newEcritureComptable.getLibelle()),(newLibelle));

    oldEcritureComptable.setLibelle(oldLibelle);
    dao.updateEcritureComptable(oldEcritureComptable);
  }

  @Test
  public void GivenIdEcritureComptable_WhenDeleteEcritureComptable_ObjectIsDeletedAndLigneEcritureComptableIsDeleted() throws NotFoundException {

    EcritureComptable ecritureComptable = dao.getEcritureComptable(-1);
    ecritureComptable.setId(null);
    dao.insertEcritureComptable(ecritureComptable);
    int nbEcriture = dao.getListEcritureComptable().size();


    dao.deleteEcritureComptable(ecritureComptable.getId());


    assertEquals((dao.getListEcritureComptable().size()),(nbEcriture-1));
  }


}
