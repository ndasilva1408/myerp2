package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;


import com.dummy.myerp.model.bean.comptabilite.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================

    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }


    /**
     * {@inheritDoc}
     */
    // TODO à tester : Done
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws NotFoundException {
        // TODO à implémenter : Done
        // Bien se réferer à la JavaDoc de cette méthode !
        /* Le principe :
                1.  Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture
                    (table sequence_ecriture_comptable)*/
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(pEcritureComptable.getDate()));
        String codeJournal = pEcritureComptable.getJournal().getCode();
        String reference;

        SequenceEcritureComptable sequenceEcritureComptable = getDaoProxy().getComptabiliteDao()
                .getSequenceEcritureComptableByCodeYear(codeJournal, year);


            /*    2.  * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
                        1. Utiliser le numéro 1.
                    * Sinon :
                        1. Utiliser la dernière valeur + 1

                   4.  Enregistrer (insert/update) la valeur de la séquence en persitance
                    (table sequence_ecriture_comptable)
         */

        if (sequenceEcritureComptable == null) {
            sequenceEcritureComptable = new SequenceEcritureComptable(year, 1);
            reference = "00001";
            getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(codeJournal, sequenceEcritureComptable);
        } else {
            sequenceEcritureComptable.setDerniereValeur(sequenceEcritureComptable.getDerniereValeur() + 1);
            reference = Integer.toString(sequenceEcritureComptable.getDerniereValeur());

            while (reference.length() < 5) {
                reference = "0" + reference;
            }
            getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(codeJournal, sequenceEcritureComptable);

        }
        //    3.  Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
        reference = codeJournal + "-" + year + "/" + reference;
        pEcritureComptable.setReference(reference);

    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester : Done
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // TODO tests à compléter : Done
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                    new ConstraintViolationException(
                            "L'écriture comptable ne respecte pas les contraintes de validation",
                            vViolations));
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
                || vNbrCredit < 1
                || vNbrDebit < 1) {
            throw new FunctionalException(
                    "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // TODO ===== RG_Compta_5 : Format et contenu de la référence : Done
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
        try {
            this.checkEcritureComptableReference(pEcritureComptable);
        } catch (FunctionalException | NotFoundException ex) {
            throw new FunctionalException(ex.getMessage());
        }

        // ===== RG_Compta_7 : Les montants des lignes d'écritures peuvent comporter 2 chiffres maximum après la virgule.
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            int scaleCredit = (vLigneEcritureComptable.getCredit() != null) ? vLigneEcritureComptable.getCredit().scale() : 0;
            int scaleDebit = (vLigneEcritureComptable.getDebit() != null) ? vLigneEcritureComptable.getDebit().scale() : 0;
            if (scaleCredit > 2 ||
                    scaleDebit > 2)
                throw new FunctionalException("Les montants des lignes d'écritures peuvent comporter 2 chiffres maximum après la virgule.");
        }
    }

    /**
     * RG_COMPTA_5
     * Vérifie si La référence d'une ecriture comptable est composée du code du {@link JournalComptable}
     * suivi de l'année de l'{@link EcritureComptable} sur 4 chiffres
     * puis d'un numéro de séquence (sur 5 chiffres) incrémenté automatiquement à chaque écriture (dernière valeur a récupérer dans la table SequenceEcritureComptable)
     * Le formatage de la référence est : XX-AAAA/##### -> BQ-2016/00001, il est vérifiée par le validator dans le model {@link SequenceEcritureComptable} via une expression régulière
     *
     * @param ecritureComptable {@link EcritureComptable} dont on veut tester la reference
     * @throws FunctionalException si la référence enfreint une de ces règles
     */
    protected void checkEcritureComptableReference(EcritureComptable ecritureComptable) throws FunctionalException, NotFoundException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ecritureComptable.getDate());
        String refYear = String.valueOf(calendar.get(Calendar.YEAR));

        if (ecritureComptable.getReference() != null) {
            String[] splitRef = ecritureComptable.getReference().split("[-/]");
            //Check Journal Code
            if (!splitRef[0].equals(ecritureComptable.getJournal().getCode())) {
                throw new FunctionalException(
                        "La référence de l'écriture " + splitRef[0] +
                                " ne correspond pas au code journal "
                                + ecritureComptable.getJournal().getCode() + ".");
            }
            //Check Year
            else if (!splitRef[1].equals(refYear)) {
                throw new FunctionalException(
                        "La référence de l'écriture " + splitRef[1] +
                                " ne correspond pas à l'année de l'écriture " + refYear + "."
                );
            }
            if (ecritureComptable.getId() == null) {
                int sequenceVerified = this.getNextSequenceFromEcritureComptable(ecritureComptable, calendar.get(Calendar.YEAR)) - 1;
                if (sequenceVerified == 0) {
                    sequenceVerified = 1;
                }

                // Format the new sequence with 0
                String formatSequenceVerified = String.format("%05d", sequenceVerified);
                if (!splitRef[2].equals(formatSequenceVerified)) {
                    throw new FunctionalException(
                            "Le numéro de séquence de l'écriture " + splitRef[2] +
                                    " ne correspond pas à la dernière séquence du journal " + formatSequenceVerified +"."
                    );
                }
            }
        } else {
            throw new FunctionalException(
                    "La référence de l'écriture ne peut pas être nulle."
            );
        }


    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                        pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                        || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    private int getNextSequenceFromEcritureComptable(EcritureComptable ecritureComptable, int annee) throws NotFoundException {
        SequenceEcritureComptable sequence = getDaoProxy().getComptabiliteDao().getSequenceEcritureComptableByCodeYear(ecritureComptable.getJournal().getCode(), annee);
        return sequence.getDerniereValeur() + 1;
    }
}
