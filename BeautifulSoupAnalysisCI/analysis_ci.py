
## for when parsing raw data from a txt file -- alternative solution

import re #regex

log = # open the raw log as a string

ex = re.compile(" > VoiceActivationTest\w* FAILED\\r\\n.+?(?=\\r\\n)")
x = ex.findall(log)

for match in x:
    arr = match.split("\r\n")
