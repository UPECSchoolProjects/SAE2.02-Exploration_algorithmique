package com.uwu.Stemming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.uwu.Stemming.WordRV.Region;

/**
 * Cette classe détaille toutes les étapes de remplacement de l'algorithme de
 * racinisation
 * elle utilise l'interface IReplaceStep pour définir les étapes de remplacement
 * 
 * à chaque étape (si j'ai pas oublié) j'ai mis un commentaire avec la phrase du
 * cahier des charges
 * 
 * @author Maxime LOTTO
 */
public class StemSteps {
    /*
     * Cette classe aide à l'implementation de l'algorithme de racinisation donnée
     * dans le cahier des
     * charges.
     * 
     * Elle a été écrite par Maxime LOTTO dans le cadre du travail personnel de la
     * SAE2.02
     */

    /**
     * vérifie si le suffixe est précédé par une voyelle
     * utile pour l'étape 1 : // delete if in R1 and preceded by a non-vowel
     * 
     * @param word   le mot complet
     * @param suffix le suffixe à vérifier
     * @return true si le suffixe est précédé par une voyelle
     */
    public static boolean isPrecededByVowel(String word, String suffix) {
        int suffixIndex = word.lastIndexOf(suffix);
        if (suffixIndex == -1)
            return false;
        if (suffixIndex == 0)
            return false;
        char c = word.charAt(suffixIndex - 1);

        return Stemming.isVowel(c);
    }

    static List<IReplaceStep> step1 = Arrays.asList(
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("ance", "iqUe", "isme", "able", "iste", "eux", "ances", "iqUes",
                        "ismes", "ables", "istes");

                @Override
                public void replace(WordRV word) {
                    word.replaceSuffix(Region.R2, suffixes, "");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("atrice", "ateur", "ation", "atrices", "ateurs", "ations");

                @Override
                public void replace(WordRV word) {
                    boolean result = word.replaceSuffix(Region.R2, suffixes, "");
                    if (!result)
                        return;
                    // if preceded by ic, delete if in R2, else replace by iqU
                    ArrayList<Region> ICinRegions = word.endsWithRegion("ic");
                    if (ICinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "ic", ""));
                    } else if (ICinRegions.size() > 0) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "ic", "iqU"));
                    }
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("logie", "logies");

                @Override
                public void replace(WordRV word) {
                    word.replaceSuffix(Region.R2, suffixes, "log");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("usion", "ution", "usions", "utions");

                @Override
                public void replace(WordRV word) {
                    word.replaceSuffix(Region.R2, suffixes, "u");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("ence", "ences");

                @Override
                public void replace(WordRV word) {
                    word.replaceSuffix(Region.R2, suffixes, "ent");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("ement", "ements");

                @Override
                public void replace(WordRV word) {
                    boolean result = word.replaceSuffix(Region.RV, suffixes, "");
                    if (!result)
                        return;

                    ArrayList<Region> IVinRegions = word.endsWithRegion("iv");

                    // if preceded by iv, delete if in R2
                    if (IVinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "iv", ""));
                        // and if further preceded by at, delete if in R2
                        ArrayList<Region> ATinRegions = word.endsWithRegion("at");
                        if (ATinRegions.contains(Region.R2)) {
                            word.setWord(Stemming.replaceLast(word.getWord(), "at", ""));
                        }
                        return;
                    }

                    // if preceded by eus, delete if in R2, else replace by eux if in R1
                    ArrayList<Region> eusinRegions = word.endsWithRegion("eus");
                    if (eusinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "eus", ""));
                        return;
                    } else if (eusinRegions.contains(Region.R1)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "eus", "eux"));
                        return;
                    }

                    // if preceded by abl or iqU, delete if in R2,
                    ArrayList<Region> ABLinRegions = word.endsWithRegion("abl");
                    ArrayList<Region> IQUinRegions = word.endsWithRegion("iqU");
                    if (ABLinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "abl", ""));
                        return;
                    } else if (IQUinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "iqU", ""));
                        return;
                    }

                    // if preceded by ièr or Ièr, replace by i if in RV
                    ArrayList<Region> ierinRegions = word.endsWithRegion("ièr");
                    ArrayList<Region> IerinRegions = word.endsWithRegion("Ièr");
                    if (ierinRegions.contains(Region.RV)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "ièr", "i"));
                        return;
                    } else if (IerinRegions.contains(Region.RV)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "Ièr", "i"));
                        return;
                    }
                }

            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("ité", "ités");

                @Override
                public void replace(WordRV word) {
                    boolean result = word.replaceSuffix(Region.R2, suffixes, "");
                    if (!result)
                        return;

                    // if preceded by abil, delete if in R2, else replace by abl
                    ArrayList<Region> ABILinRegions = word.endsWithRegion("abil");
                    if (ABILinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "abil", ""));
                        return;
                    } else if (ABILinRegions.size() > 0) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "abil", "abl"));
                        return;
                    }

                    // if preceded by ic, delete if in R2, else replace by iqU
                    ArrayList<Region> ICinRegions = word.endsWithRegion("ic");
                    if (ICinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "ic", ""));
                        return;
                    } else if (ICinRegions.size() > 0) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "ic", "iqU"));
                        return;
                    }

                    // if preceded by iv, delete if in R2
                    ArrayList<Region> IVinRegions = word.endsWithRegion("iv");
                    if (IVinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "iv", ""));
                        return;
                    }
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("if", "ive", "ifs", "ives");

                @Override
                public void replace(WordRV word) {
                    boolean result = word.replaceSuffix(Region.R2, suffixes, "");
                    if (!result)
                        return;

                    // if preceded by at, delete if in R2
                    ArrayList<Region> ATinRegions = word.endsWithRegion("at");
                    if (ATinRegions.contains(Region.R2)) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "at", ""));

                        // and if further preceded by ic, delete if in R2, else replace by iqU
                        ArrayList<Region> ICinRegions = word.endsWithRegion("ic");
                        if (ICinRegions.contains(Region.R2)) {
                            word.setWord(Stemming.replaceLast(word.getWord(), "ic", ""));
                            return;
                        } else if (ICinRegions.size() > 0) {
                            word.setWord(Stemming.replaceLast(word.getWord(), "ic", "iqU"));
                            return;
                        }
                        return;
                    }
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("eaux");

                @Override
                public void replace(WordRV word) {
                    word.replaceSuffix(Region.ALL, suffixes, "eau");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("aux");

                @Override
                public void replace(WordRV word) {
                    word.replaceSuffix(Region.R1, suffixes, "al");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("euse", "euses");

                @Override
                public void replace(WordRV word) {
                    // delete if in R2, else replace by eux if in R1
                    boolean result = word.replaceSuffix(Region.R2, suffixes, "");
                    if (!result)
                        return;
                    word.replaceSuffix(Region.R1, suffixes, "eux");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("issement", "issements");

                @Override
                public void replace(WordRV word) {
                    // delete if in R1 and preceded by a non-vowel
                    // -> interpretation : la non-voyelle n'a pas besoin d'etre dans R1
                    String searchWord = word.getWord().substring(word.getR1_Start());
                    String longestSuffix = Stemming.getLongestSuffix(suffixes, searchWord);
                    if (longestSuffix != null) {
                        if (!isPrecededByVowel(word.getWord(), longestSuffix)) {
                            word.setWord(Stemming.replaceLast(word.getWord(), longestSuffix, ""));
                        }
                    }
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("amment");

                @Override
                public void replace(WordRV word) {
                    // replace with ant if in RV
                    word.replaceSuffix(Region.RV, suffixes, "ant");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("emment");

                @Override
                public void replace(WordRV word) {
                    // replace with ent if in RV
                    word.replaceSuffix(Region.RV, suffixes, "ent");
                }
            },
            new IReplaceStep() {
                List<String> suffixes = Arrays.asList("ment", "ments");

                @Override
                public void replace(WordRV word) {
                    // delete if preceded by a vowel in RV
                    // -> interpretation : la voyelle doit etre dans RV

                    String searchWord = word.getWord().substring(word.getRV_Start());
                    String longestSuffix = Stemming.getLongestSuffix(suffixes, searchWord);
                    if (longestSuffix != null) {
                        int suffixIndex = word.getWord().lastIndexOf(longestSuffix);

                        int previousCharIndex = suffixIndex - 1;
                        if (previousCharIndex < 0)
                            return;

                        // check if in RV
                        if (!word.isInRegion(Region.RV, previousCharIndex))
                            return;

                        // check if preceded by a vowel
                        char previousChar = word.getWord().charAt(previousCharIndex);
                        if (Stemming.isVowel(previousChar)) {
                            word.setWord(Stemming.replaceLast(word.getWord(), longestSuffix, ""));
                        }
                    }
                }
            });

    // Step2a
    public static IReplaceStep step2a = new IReplaceStep() {
        List<String> suffixes = Arrays.asList("îmes", "ît", "îtes", "i", "ie", "ies", "ir", "ira", "irai",
                "iraIent", "irais", "irait", "iras", "irent", "irez", "iriez", "irions", "irons", "iront", "is",
                "issaIent", "issais", "issait", "issant", "issante", "issantes", "issants", "isse",
                "issentisses", "issez", "issiez", "issions", "issons", "it");

        @Override
        public void replace(WordRV word) {
            // Search for the longest among the following suffixes and if found, delete if
            // the preceding character is neither a vowel nor H
            // Note that the preceding character itself must also be in RV.
            String searchWord = word.getWord().substring(word.getRV_Start());
            String longestSuffix = Stemming.getLongestSuffix(suffixes, searchWord);
            if (longestSuffix != null) {
                int suffixIndex = word.getWord().lastIndexOf(longestSuffix);

                int previousCharIndex = suffixIndex - 1;
                if (previousCharIndex < 0)
                    return;

                // check if in RV
                if (!word.isInRegion(Region.RV, previousCharIndex))
                    return;

                // delete if the preceding character is neither a vowel nor H
                char previousChar = word.getWord().charAt(previousCharIndex);
                if (!Stemming.isVowel(previousChar) && previousChar != 'H') {
                    word.setWord(Stemming.replaceLast(word.getWord(), longestSuffix, ""));
                    word.setFoundStep2aSuffixes(true);
                }
            }
        }
    };

    // Step 2b: Other verb suffixes
    // Search for the longest among the following suffixes, and perform the action
    // indicated.
    // rappel : tout se passe dans RV
    public static IReplaceStep step2b = new IReplaceStep() {
        List<String> suffixes1 = Arrays.asList("ions");
        List<String> suffixes2 = Arrays.asList("é", "ée", "ées", "és", "èrent", "er", "era", "erai", "eraIent", "erais",
                "erait", "eras", "erez", "eriez", "erions", "erons", "eront", "ez", "iez");
        List<String> suffixes3 = Arrays.asList("âmes", "ât", "âtes", "a", "ai", "aIent", "ais", "ait", "ant", "ante",
                "antes", "ants", "as", "asse", "assent", "asses", "assiez", "assions");

        @Override
        public void replace(WordRV word) {
            // ions delete if in R2
            word.replaceSuffix(Region.R2, suffixes1, "");

            // suffixes 2 delete.
            word.replaceSuffix(Region.RV, suffixes2, "");

            // delete
            // if preceded by e, delete
            String searchWord = word.getWord().substring(word.getRV_Start());
            String longestSuffix = Stemming.getLongestSuffix(suffixes3, searchWord);

            if (longestSuffix != null) {
                int suffixIndex = word.getWord().lastIndexOf(longestSuffix);
                int previousCharIndex = suffixIndex - 1;
                if (previousCharIndex < 0)
                    return;

                // check if in RV
                if (!word.isInRegion(Region.RV, previousCharIndex))
                    return;

                // delete if preceded by e
                char previousChar = word.getWord().charAt(previousCharIndex);
                if (previousChar == 'e') {
                    word.setWord(Stemming.replaceLast(word.getWord(), "e", ""));
                }
            }

        }
    };

    // ! If the last step to be obeyed — either step 1, 2a or 2b — altered the word,
    // do step 3
    // Step 3
    // Replace final Y with i or final ç with c
    public static IReplaceStep step3 = new IReplaceStep() {
        @Override
        public void replace(WordRV word) {
            // replace final Y with i
            if (word.getWord().endsWith("Y")) {
                word.setWord(Stemming.replaceLast(word.getWord(), "Y", "i"));
            }

            // replace final ç with c
            if (word.getWord().endsWith("ç")) {
                word.setWord(Stemming.replaceLast(word.getWord(), "ç", "c"));
            }
        }
    };

    // Step 4
    // If the word ends s, not preceded by a, i (unless itself preceded by H), o, u,
    // è or s, delete it.
    // In the rest of step 4, all tests are confined to the RV region
    public static IReplaceStep step4 = new IReplaceStep() {
        List<Character> pasAvantS = Arrays.asList('a', 'i', 'o', 'u', 'è', 's');
        List<String> remplaceParI = Arrays.asList("ier", "ière", "Ier", "Ière");

        @Override
        public void replace(WordRV word) {
            // if the word ends s, not preceded by a, i (unless itself preceded by H), o, u,
            // è or s, delete it.
            if (word.getWord().endsWith("s")) {
                int previousCharIndex = word.getWord().length() - 2;

                System.out.println("previousCharIndex : " + previousCharIndex);

                int previousEncore = previousCharIndex - 1;
                if (previousEncore > 0) {

                    System.out.println("previousCharIndex : " + previousCharIndex);

                    char previousChar = word.getWord().charAt(previousCharIndex);
                    char previousEncoreChar = word.getWord().charAt(previousEncore);

                    if (!pasAvantS.contains(previousChar) || (previousChar == 'i' && previousEncoreChar == 'H')) {
                        word.setWord(Stemming.replaceLast(word.getWord(), "s", ""));
                    }
                }
            }

            // dans le reste de step 4, tous les tests sont confinés à la région RV
            // "ion" delete if in R2 and preceded by s or t
            // interpretation -> s et t doivent être dans RV
            // So note that ion is removed only when it is in R2 — as well as being in RV —
            // and preceded
            // by s or t which must be in RV.
            word.deleteIfPrecededBySuffix(Region.R2, Arrays.asList("ions"), "", Arrays.asList('s', 't'), false,
                    Region.RV);

            // ier ière Ier Ière replace with i
            word.replaceSuffix(Region.RV, remplaceParI, "i");

            // e delete
            // print rv
            System.out.println("word " + word.getWord() + "RV :" + word.getRV() + " - suffix: e - "
                    + word.getWord().endsWith("e"));
            word.replaceSuffix(Region.RV, Arrays.asList("e"), "");
        }
    };

    // ! Always do steps 5 and 6.

    // Step 5 : Undouble
    // If the word ends enn, onn, ett, ell or eill, delete the last letter
    public static IReplaceStep step5 = new IReplaceStep() {
        List<String> suffixes = Arrays.asList("enn", "onn", "ett", "ell", "eill");

        @Override
        public void replace(WordRV word) {

            for (String suffix : suffixes) {
                System.out.println(
                        "word " + word.getWord() + " - suffix: " + suffix + " - " + word.getWord().endsWith(suffix));
                if (word.getWord().endsWith(suffix)) {
                    word.deleteLastLetter();
                }
            }
        }
    };

    // Step 6: Un-accent
    // If the words ends é or è followed by at least one non-vowel, remove the
    // accent from the e.
    public static IReplaceStep step6 = new IReplaceStep() {

        @Override
        public void replace(WordRV word) {
            int avantDernierIndex = word.getWord().length() - 2;
            if (avantDernierIndex < 0)
                return;
            char avantDernierChar = word.getWord().charAt(avantDernierIndex);

            if (avantDernierChar == 'é' || avantDernierChar == 'è') {
                int dernierIndex = word.getWord().length() - 1;
                if (dernierIndex < 0)
                    return;
                char dernierChar = word.getWord().charAt(dernierIndex);

                if (!Stemming.isVowel(dernierChar)) {
                    word.setWord(Stemming.replaceLast(word.getWord(), String.valueOf(avantDernierChar), "e"));
                }
            }
        }
    };

    // step finally
    // Turn any remaining I, U and Y letters in the word back into lower case.
    // Turn He and Hi back into ë and ï, and remove any remaining H
    public static IReplaceStep stepFinally = new IReplaceStep() {
        @Override
        public void replace(WordRV word) {
            word.setWord(word.getWord().replace("I", "i"));
            word.setWord(word.getWord().replace("U", "u"));
            word.setWord(word.getWord().replace("Y", "y"));
            word.setWord(word.getWord().replace("He", "ë"));
            word.setWord(word.getWord().replace("Hi", "ï"));
            word.setWord(word.getWord().replace("H", ""));
        }
    };
}
