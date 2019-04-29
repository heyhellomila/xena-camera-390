import bs4 as bs
import sys # system arguments for dynamic updates
import urllib.request

sauce = urllib.request.urlopen('https://223-164523871-gh.circle-artifacts.com/0/home/circleci/app/src/main/kotlin/app/build/reports/lint-results-debug.html').read()

#Making source code into a Beautiful soup object
soup = bs.BeautifulSoup(sauce, 'lxml')

print(soup)
