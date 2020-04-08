package com.dummy.myerp.consumer;

import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.CompteComptableRM;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

/*
@ExtendWith(MockitoExtension.class)
public class ComptabiliteDaoImplTest {
    @Mock
    JdbcTemplate vJdvcTemplate;
    @Mock
    CompteComptableRM vRm;
    @Mock
    CompteComptable compteComptable;

         String SQLgetListCompteComptable;


    @Tag("getListCompteComptable")
    @Test
    void getListCompteComptable() {
            //need DB ? Comment test ?

        List<CompteComptable> listComptable = new ArrayList<CompteComptable>(){
            {
               add(compteComptable);
            }
        };

        //Act
        List<CompteComptable> vList = vJdvcTemplate.query(SQLgetListCompteComptable, vRm);

        //Assert
        assertSame(listComptable ,vList);

    }


}*/
