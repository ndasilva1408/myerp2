package com.dummy.myerp.testconsumer.consumer;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;

import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;


@ContextConfiguration(locations = "classpath:consumerContextTest.xml")
public class ConsumerTest {
    @Autowired
    ComptabiliteDaoImpl dao;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @Rollback
    public void testAddEcritureComptable(){
        Date date = new Date();

        JournalComptable journalComptable= new JournalComptable();
        journalComptable.setLibelle("Achat");
        journalComptable.setCode("AC");

        EcritureComptable ecritureComptableTest = new EcritureComptable();
        ecritureComptableTest.setLibelle("12");
        ecritureComptableTest.setDate(date);
        ecritureComptableTest.setReference("AC-2020/00001");
        ecritureComptableTest.setJournal(journalComptable);

        dao.insertEcritureComptable(ecritureComptableTest);

        List<EcritureComptable>ecritureComptableList = dao.getListEcritureComptable();
        Assert.assertEquals(ecritureComptableTest.getJournal().getCode(), ecritureComptableList.get(47).getJournal().getCode());
    }

    @Test
    @Rollback
    public void testDeleteEcritureComptable(){
        List<EcritureComptable> listAvantDelete = dao.getListEcritureComptable();
      EcritureComptable ecritureComptableTest =  dao.getListEcritureComptable().get(47);

      dao.deleteEcritureComptable(ecritureComptableTest.getId());

     Assert.assertEquals(dao.getListEcritureComptable().size(),(listAvantDelete.size()-1));
    }
}

