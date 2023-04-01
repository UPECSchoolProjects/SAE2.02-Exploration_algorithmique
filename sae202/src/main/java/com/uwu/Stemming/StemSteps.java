package com.uwu.Stemming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.uwu.Stemming.WordRV.Region;

public class StemSteps {
    /*
     * Cette classe aide à l'implementation de l'algorithme de racinisation donnée dans le cahier des
     * charges.
     * 
     * Elle a été écrite par Maxime LOTTO dans le cadre du travail personnel de la SAE2.02
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
                        if (suffixIndex == -1)
                            return;
                        if (suffixIndex == 0)
                            return;
                        int previousCharIndex = suffixIndex - 1;
                        
                        // check if in RV
                        if (previousCharIndex < word.getRV_Start())
                            return;
                        
                        // check if preceded by a vowel
                        char previousChar = word.getWord().charAt(previousCharIndex);
                        if (Stemming.isVowel(previousChar)) {
                            word.setWord(Stemming.replaceLast(word.getWord(), longestSuffix, ""));
                        }
                    }
                }
            });
}
