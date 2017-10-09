#!/usr/bin/python3.5

import urllib.request
import re
import requests
import json
import time
import traceback

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
		# escludo intestazione Letture
		if (regNotContent.match(rows[i])):
			print("--- EXCLUDE --- " + str(regNotContent.match(rows[i]).group(1)) + "---")
		else:
			print("--- ADD ---"+str(regContent.match(rows[i]).group(1)))
			listContentId.append(str(regContent.match(rows[i]).group(1)))

#Example http://www.radio3.rai.it/dl/portaleRadio/Programmi/Page-9fe19bce-1c27-4b63-b41e-2d7581d21374.html?set=ContentSet-b63181f3-deec-4cb7-84db-96169b0725e7&type=A

audiobooks = []
bookId = 0

print ("=== Content List ===")
for i in range(len(listContentId)):
	
	print (listContentId[i])

	book = {}
	book['id'] = bookId
	bookContents = []
	chapterId = 0

	image = ''
	image_300 = ''
	image_433 = ''	
	
	#esclude libro non ancora disponibile
	if (1<2):
		j = 0
		receivedNotFound = False
		
		

		while (not receivedNotFound):
			#print ("J="+str(j))
			url = 'http://www.radio3.rai.it/dl/portaleRadio/programmi/json/liste/' + listContentId[i] + '-json-A-' + str(j) + '.html'
			params = {}
			headers = {'User-Agent': 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0',
				   'Accept' : 'text/html, */*; q=0.01',
				   'Accept-Language' : 'en-US,en;q=0.5',
				   'Accept-Encoding' : 'gzip, deflate',
				   'X-Requested-With': 'XMLHttpRequest',
				   'Host': 'www.radio3.rai.it',
				   'Referer': 'http://www.radio3.rai.it/dl/portaleRadio/Programmi/Page-9fe19bce-1c27-4b63-b41e-2d7581d21374.html?set=' + listContentId[i] + '&type=A',
				   'Cookie': '_iub_cs-844946=%7B%22consent%22%3Atrue%2C%22timestamp%22%3A%222017-05-07T17%3A39%3A33.267Z%22%2C%22version%22%3A%220.13.9%22%2C%22id%22%3A844946%7D',
			    	   'Connection': 'keep-alive',
				   'Cache-Control': 'max-age=0'
			} 

			#http://mediapolisvod.rai.it/relinker/relinkerServlet.htm?cont=7ESImH65m1oeeqqEEqual

			response = requests.get(url, params=params, headers=headers)		

			try:
				print (response)
				listElements = response.json().get('list',[])
				
				print ('\n\n\n------------------ ELEMENTS ------------------\n\n\n')
				print (listElements)

				for el in listElements:
					chapter = {}

					print ("------------------------------------")
					print (el)
					print ("Desc: " + el.get('desc', ''))					
					print ("Name: " + el.get('name', ''))
					print ("Mp3: " + el.get('mp3', ''))
					print ("WebLink: " + el.get('weblink', ''))

					chapter['contentId'] = listContentId[i]
					chapter['id'] = chapterId
					chapter['title'] = el.get('name', '')
					chapter['desc'] = el.get('desc', '')
					chapter['url'] = el.get('mp3', '')
					chapter['format'] = 'mp3'
					chapter['weblink'] = el.get('weblink', '')
					chapter['webLinkLookup'] = False

					image = el.get('image', '')
					image_300 = el.get('image_300', '')	
					image_433 = el.get('image_433', '')

					if(chapter['url'] == ''):
						#force detect audio url
						print ('\n\nFORCED ' + chapter['weblink'] + '\n\n')
						
						wp = urllib.request.urlopen(chapter['weblink'])
						sourceBookPage = wp.read()

						#print ("\n\n\n")
						#print (sourceBookPage)
						#print ("\n\n\n")

						# var audioUrl = "http://mediapolisvod.rai.it/relinker/relinkerServlet.htm?cont=7ESImH65m1oeeqqEEqual";
						regAudioUrl = re.compile(".*?audioUrl\s*=\s*\"(.*?)\"")
						
						if (regAudioUrl.match(str(sourceBookPage))):
							print("--- URL ---"+str(regAudioUrl.match(str(sourceBookPage)).group(1)))
							chapter['url'] = str(regAudioUrl.match(str(sourceBookPage)).group(1))
							chapter['webLinkLookup'] = True
	
					chapterId = chapterId + 1
					
					print ('Chapter: ' + str(chapter))

					bookContents.append(chapter)
			except:
				receivedNotFound = True
				print ('----------------------- Exception ---------------------')
				traceback.print_exc()
				print(url)

			j = j+1
		
		book['contents'] = bookContents
		
		book['metadata'] = {		
			'image': 'http://www.rai.tv' + image,
			'other_images' : {
				'image_300' : 'http://www.rai.tv' + image_300,
				'image_433' : 'http://www.rai.tv' + image_433,
			},
			'provider' : {
				'name' : 'Ad Alta Voce',
				'site' : 'http://www.adaltavoce.rai.it/',
				'image' : ''
			}
		},

		audiobooks.append(book)
	
	bookId = bookId + 1
	chapterId = 0

	data = {}
	data['version'] = 0
	data['config'] = {}
	data['audiobooks'] = audiobooks

	out_file = open("config_test.json","w")
	out_file.write(json.dumps(data, sort_keys=True, indent=4))
	out_file.close()

	time.sleep(10)

json_data = json.dumps(data)

print(json.dumps(data, sort_keys=True, indent=4))



