#!/usr/bin/python3.5

import re
import requests
import json
import time
import traceback

from pprint import pprint

data = json.load(open('config.json', 'r', encoding="utf-8"))

for currBook in data["audiobooks"]:

	titles = {}
	for ch in currBook["contents"]:
		currTitle = ch["title"]
		#pprint(currTitle)	
		
		if(currTitle in titles):
			pprint("Book = " + currBook["metadata"]["title"])
			print("ERROR DUPLICATE FOUND " + str(ch["title"]))
		else:
			titles[currTitle] = 1




