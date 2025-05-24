package com.uwu.Stemming;

/**
 * Cette classe aide à l'implementation de l'algorithme de racinisation donnée
 * dans le cahier des
 * charges.
 * 
 * Elle a été écrite par Mélodie LOTTO dans le cadre du travail personnel de la
 * SAE2.02
 */

public interface IReplaceStep {

    /**
     * Represente une étape de remplacement dans l'algorithme de racinisation
     * @param word
     */
    public void replace(WordRV word);
}
