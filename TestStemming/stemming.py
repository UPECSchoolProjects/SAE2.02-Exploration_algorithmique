# Ce fichier est un test de l'implementation de l'algorithme de stemming en python avant de l'implémenter en Java.
# il n'est pas utilisé dans le projet.
# il s'arrete au début de l'étape 1

import re

vowels = ['a', 'e', 'i', 'o', 'u', 'y', 'â', 'à',
          'ë', 'é', 'ê', 'è', 'ï', 'î', 'ô', 'û', 'ù']


def construct_regex(vowels):
    # cette fonction est utilisée pour construire les regex en fonction de la liste des voyelles

    # RV_regex = r'(?<=.)([aeiouyâàëéêèïîôûù])'
    # (?<=.) est une assertion positive de lookbehind qui vérifie qu'il y a un caractère avant le groupe de capture
    # https://fr.javascript.info/regexp-lookahead-lookbehind
    vowelsList = ''.join(vowels)

    RV_regex = r'(?<=.)([' + vowelsList + '])'

    # R1 est la région après la première consonne suivant une voyelle, ou la fin du mot si il n'y a pas de consonne
    first_nonvowels_following_vowels = r'[' + \
        vowelsList + ']' + '[^' + vowelsList + ']'

    return (RV_regex, first_nonvowels_following_vowels)


RV_regex, first_nonvowels_following_vowels_regex = construct_regex(vowels)


def mark_non_vowels(word):
    # Assume the word is in lower case. Then, taking the letters in turn from the beginning to end of the word,
    # put u or i into upper case when it is both preceded and followed by a vowel; put y into upper case when it
    # is either preceded or followed by a vowel; and put u into upper case when it follows q
    def upperLetter(word, letterIndex):
        return word[:letterIndex] + word[letterIndex].upper() + word[letterIndex+1:]
    letterIndex = 0
    while letterIndex < len(word):
        previousLetter = word[letterIndex-1] if letterIndex > 0 else ''
        nextLetter = word[letterIndex+1] if letterIndex < len(word)-1 else ''

        previousLetterIsVowel = previousLetter in vowels
        nextLetterIsVowel = nextLetter in vowels

        if word[letterIndex] in ['u', 'i'] and previousLetterIsVowel and nextLetterIsVowel:
            word = upperLetter(word, letterIndex)
        if word[letterIndex] == 'y' and (previousLetterIsVowel or nextLetterIsVowel):
            word = upperLetter(word, letterIndex)
        if word[letterIndex] == 'u' and previousLetter == 'q':
            word = upperLetter(word, letterIndex)

        # Replace ë and ï with He and Hi. The H marks the vowel as having originally had a diaeresis, while the
        # vowel itself, lacking an accent, is able to match suffixes beginning in e or i.
        if word[letterIndex] == 'ë':
            word = word[:letterIndex] + 'He' + word[letterIndex+1:]
            letterIndex += 1
        if word[letterIndex] == 'ï':
            word = word[:letterIndex] + 'Hi' + word[letterIndex+1:]
            letterIndex += 1

        letterIndex += 1

    # The upper case forms are not then classed as vowels
    return word


def find_RV_old(word):
    # If the word begins with two vowels, RV is the region after the third letter, otherwise the region after the
    # first vowel not at the beginning of the word, or the end of the word if these positions cannot be found.
    # (Exceptionally, par, col or tap, at the beginning of a word is also taken to define RV as the region to their
    # right.)

    # return pos of the start of RV
    RVStart = len(word)
    R1Start = len(word)
    R2Start = len(word)
    if word[:3] in ['par', 'col', 'tap']:
        RVStart = 3
    elif word[0] in vowels and word[1] in vowels:
        # print('word[0] in vowels and word[1] in vowels')
        RVStart = 3
    else:
        # print('else')
        for letterIndex in range(1, len(word)):
            if word[letterIndex] in vowels:
                RVStart = letterIndex+1
                break

    # print('RVStart =', RVStart)
    return (RVStart, R1Start, R2Start)


def find_RV(word):
    # If the word begins with two vowels, RV is the region after the third letter, otherwise the region after the
    # first vowel not at the beginning of the word, or the end of the word if these positions cannot be found.
    # (Exceptionally, par, col or tap, at the beginning of a word is also taken to define RV as the region to their
    # right.)

    # return pos of the start of RV
    if word[:3] in ['par', 'col', 'tap']:
        return 3
    elif word[0] in vowels and word[1] in vowels:
        return 3
    else:
        match = re.search(RV_regex, word)
        return match.end(1) if match else len(word)


def find_R1(word):
    # R1 is the region after the first non-vowel following a vowel, or the end of the word if there is no such non-vowel.
    match = re.search(first_nonvowels_following_vowels_regex, word)
    return match.end() if match else len(word)


def find_R2(word, r1_index):
    # R2 is the region after the first non-vowel following a vowel in R1, or the end of the word if there is no such non-vowel.
    match = re.search(first_nonvowels_following_vowels_regex, word[r1_index:])
    matchedIndex = match.end() if match else len(word)
    return matchedIndex + r1_index


def find_longest_suffix_and_replace(word, suffixes, start_index, replace_with=''):
    # return the longest suffix in suffixes that is found in word, starting at start_index
    search_str = word[start_index:]
    changed = False

    longest_suffix = ""
    for suffix in suffixes:
        if search_str.endswith(suffix) and len(suffix) > len(longest_suffix):
            longest_suffix = suffix
    
    if longest_suffix != "":
        word = word[:start_index] + word[start_index:].replace(longest_suffix, replace_with, 1)
        changed = True
    
    return (word, changed)
    

suffixes_by_step = [
    ['ance', 'iqUe' ,'isme', 'able', 'iste', 'eux', 'ances', 'iqUes', 'ismes', 'ables', 'istes'], # delete if in R2
    ['atrice', 'ateur', 'ation', 'atrices', 'ateurs', 'ations'], # delete if in R2 - if preceded by ic, delete if in R2, else replace by iqU
    ['logie', 'logies'], # replace with log if in R2
    ['usion', 'ution', 'usions', 'utions'], # replace with u if in R2
    ['ence', 'ences'], # replace with ent if in R2
    ['ement', 'ements'], # delete if in RV
                        # if preceded by iv, delete if in R2 (and if further preceded by at, delete if in R2), otherwise,
                        # if preceded by eus, delete if in R2, else replace by eux if in R1, otherwise,
                        # if preceded by abl or iqU, delete if in R2, otherwise,
                        # if preceded by ièr or Ièr, replace by i if in RV
]

def stemm_algo(word):
    word = word.lower()
    word = mark_non_vowels(word)
    RVStart = find_RV(word)
    R1Start = find_R1(word)
    R2Start = find_R2(word, R1Start)

    return word


def compare(word):
    # compare execution time of the two find_RV functions
    import timeit
    print('find_RV_old:', timeit.timeit('find_RV_old(word)',
          setup='from __main__ import find_RV_old, word', number=10000))
    print('find_RV:', timeit.timeit('find_RV(word)',
          setup='from __main__ import find_RV, word', number=10000))


if __name__ == "__main__":
    word = 'adorer'
    # print(word)
    # stemm_algo(word)
    #compare(word)
    R1Start = find_R1('fameusement')
    R2Start = find_R2('fameusement', R1Start)
    print('fameusement'[R1Start:])
    print('fameusement'[R2Start:])
