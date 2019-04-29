from bs4 import BeautifulSoup
import urllib.request

sauce = urllib.request.urlopen(
    'https://223-164523871-gh.circle-artifacts.com/0/home/circleci/app/src/main/kotlin/app/build/reports/lint-results-debug.html').read()

# Making source code into a Beautiful soup object
soup: BeautifulSoup = BeautifulSoup(sauce, 'lxml')
soup.prettify() #formatter

def match_class(target):
    def do_match(tag):
        classes = tag.get('class', [])
        return all(c in classes for c in target)

    return do_match

spoon = soup.find_all(match_class(["issueColumn"]))
print(spoon)
