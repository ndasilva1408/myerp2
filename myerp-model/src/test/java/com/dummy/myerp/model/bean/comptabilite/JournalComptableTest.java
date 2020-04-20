package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.dummy.myerp.model.bean.comptabilite.JournalComptable.getByCode;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class JournalComptableTest {




    @Tag("getByCode")
    @Test
    void getByNumero_ShouldReturnCompteComptableByNumero(){
        //Arrange
        List<JournalComptable> pList = new ArrayList<>();



        JournalComptable journalComptable1 = new JournalComptable("8","-1455");
        JournalComptable journalComptable2 = new JournalComptable("42","5434875");
        pList.add(journalComptable1);
        pList.add(journalComptable2);

        //Act
        getByCode(pList,"8");

        //Assert
        Assert.assertEquals(getByCode(pList,"8"),journalComptable1);
    }

    @Tag("getByCode")
    @Test
    void getByNumero_ShouldReturnNull(){
        //Arrange

        List<JournalComptable> pList = new ArrayList<>();

        JournalComptable journalComptable1 = new JournalComptable("8","-1455");
        JournalComptable journalComptable2 = new JournalComptable("42","5434875");
        pList.add(journalComptable1);
        pList.add(journalComptable2);

        //Act
        getByCode(pList,"");

        //Assert
        Assert.assertNull(getByCode(pList,""));
    }
}
