package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/com/dummy/myerp/testbusiness/business/testContext.xml")
public class ComptabiliteManagerImplIT {
    private  ComptabiliteManagerImpl manager;
    private  EcritureComptable ecritureComptable;

    @Before
    public void setup() {
        manager = new ComptabiliteManagerImpl();
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat1"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle1");
        ecritureComptable.setReference("AC-2019/00001");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123.5),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(512),
                null, null,
                new BigDecimal(123.5)));
    }
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
}
