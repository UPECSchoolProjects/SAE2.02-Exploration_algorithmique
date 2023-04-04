# Ce fichier est un test de l'implementation de l'algorithme de stemming en python avant de l'implémenter en Java.
# il n'est pas utilisé dans le projet.
# il s'arrete au début de l'étape 1

import unittest 
import stemming as st


class TestStemming(unittest.TestCase):

    test_case_mark_non_vowels = [
        ('jouer', 'joUer'),
        ('ennuie', 'ennuIe'),
        ('yeux', 'Yeux'),
        ('quand', 'qUand'),
        ('croyiez', 'croYiez')
    ]

    def test_mark_non_vowels(self):
        for word, expected in self.test_case_mark_non_vowels:
            with self.subTest(word=word, expected=expected):
                self.assertEqual(st.mark_non_vowels(word), expected)

    test_case_find_RV = [
        ('aimer', 'er'),
        ('adorer', 'rer'),
        ('voler', 'ler'),
        ('tapis', 'is')
    ]

    def test_find_RV(self):
        for word, expected in self.test_case_find_RV:
            with self.subTest(word=word, expected=expected):
                RVStart = st.find_RV(word)
                self.assertEqual(word[RVStart:], expected)

    test_case_find_R1_and_R2 = [
        ('fameusement', 'eusement', 'ement'),
        ('beautiful', 'iful', 'ul'),
        ('beauty', 'y', ''),
        ('animadversion', 'imadversion', 'adversion'),
        ('sprinkled', 'kled', ''),
        ('eucharist', 'harist', 'ist')
    ]

    def test_find_R1_and_R2(self):
        for word, expected_R1, expected_R2 in self.test_case_find_R1_and_R2:
            with self.subTest(word=word, expected_R1=expected_R1, expected_R2=expected_R2):
                R1Start = st.find_R1(word)
                R2Start = st.find_R2(word, R1Start)
                self.assertEqual(word[R1Start:], expected_R1)
                self.assertEqual(word[R2Start:], expected_R2)
        
if __name__ == '__main__':
    unittest.main()
    
