package com.uwu.Conversion.HTMLParser;

import java.util.ArrayList;

/**
 * Classe permettant de sélectionner des éléments dans le DOM. Dans le cadre de notre projet, nous n'avons pas besoin d'une vrai
 * implementation d'un selecteur, donc on peut juste selectionner sur les attributs des éléments (par exemple, 'id' ou 'class')
 */
public class DOMSelecter {
    ArrayList<HTMLElement> root; // root du DOM

    public DOMSelecter(ArrayList<HTMLElement> root) {
        this.root = root;
    }

    /**
     * Selectionne le premier élément qui a l'attribut key avec la valeur value
     * @param key Nom de l'attribut dans lequel on veut chercher la valeur (par exemple, 'id' ou 'class')
     * @param value Valeur de l'attribut
     * @return Renvoie un HTMLElement le premier élément qui a l'attribut key avec la valeur value (null si aucun élément n'est trouvé)
     */
    public HTMLElement selectFirst(String key, String value) {
        // select on attributes
        for (HTMLElement element : root) {
            if (element.attributes.containsKey(key) && element.attributes.get(key).equals(value)) {
                return element;
            }
        }
        return null;
    }
}
