#!/usr/bin/python3.5

import urllib.request
import re
import requests

print ('Start')

#http://www.radio3.rai.it/dl/portaleRadio/Programmi/Page-9fe19bce-1c27-4b63-b41e-2d7581d21374.html

startPage = "http://www.radio3.rai.it/dl/portaleRadio/Programmi/Page-9fe19bce-1c27-4b63-b41e-2d7581d21374.html"
wp = urllib.request.urlopen(startPage)
sourceMainPage = wp.read()


rows = str(sourceMainPage).split("<li class=")#, sourceMainPage.count(str))

#prepare regex to extract audiobook contentId
regContent = re.compile(".*?<a target=\"top\" id=\"(ContentSet-.*)\" href=\"#\">.*?</a>")
regNotContent = re.compile(".*?<a target=\"top\" id=\"(ContentSet-.*\" href=\"#\">Letture.*</a>)")

listContentId = []

for i in range(len(rows)):
	#print ("ROW: " + rows[i] + "\n");
	if (regContent.match(rows[i])):
		if (regNotContent.match(rows[i])):
			print("--- " + str(regNotContent.match(rows[i]).group(1)) + "---")
		else:
			#print(str(regContent.match(rows[i]).group(1)))
			listContentId.append(str(regContent.match(rows[i]).group(1)))

#Example http://www.radio3.rai.it/dl/portaleRadio/Programmi/Page-9fe19bce-1c27-4b63-b41e-2d7581d21374.html?set=ContentSet-b63181f3-deec-4cb7-84db-96169b0725e7&type=A

print ("=== Content List ===")
for i in range(len(listContentId)):
	i = i+2
	print (listContentId[i])
	
	#esclude libro non ancora disponibile
	if (listContentId[i] != 'ContentSet-c8dea08e-d95a-40ef-8f07-13276ee004b0'):

		j = 0
		receivedNotFound = False

		while (not receivedNotFound):
			print ("J="+str(j))
			url = 'http://www.radio3.rai.it/dl/portaleRadio/programmi/json/liste/' + listContentId[i] + '-json-A-' + str(j) + '.html'
			params = {}
			headers = {'User-Agent': 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0',
				   'Accept' : 'text/html, */*; q=0.01',
				   'Accept-Language' : 'en-US,en;q=0.5',
				   'Accept-Encoding' : 'deflate',
				   'X-Requested-With': 'XMLHttpRequest',
				   'Host': 'www.radio3.rai.it',
				   'Referer': 'http://www.radio3.rai.it/dl/portaleRadio/Programmi/Page-9fe19bce-1c27-4b63-b41e-2d7581d21374.html?set=' + listContentId[i] + '&type=A',
				   'Cookie': '_iub_cs-844946=%7B%22consent%22%3Atrue%2C%22timestamp%22%3A%222017-05-07T17%3A39%3A33.267Z%22%2C%22version%22%3A%220.13.9%22%2C%22id%22%3A844946%7D',
			    	   'Connection': 'keep-alive',
				   'Cache-Control': 'max-age=0'
			} 

			response = requests.get(url, params=params, headers=headers)
	
			try:
				
				#print (response)
				listElements = response.json()['list']

				print (listElements)

				for el in listElements:
					print (" * " + el['desc'] + " - " + el['name'] + "\n\t" + el['mp3'])
					#print ("Mp3: " + el['mp3'])
			except:
				receivedNotFound = True


			j = j+1

	if i >= 3:
		break

