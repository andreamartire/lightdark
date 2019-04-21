#!/usr/bin/python3.5

import urllib.request
import re
import requests
import json
import time
import traceback
from bs4 import BeautifulSoup

print ('Start')

listContentId = []
listContentId.append("Le-linee-dombra-95ea113a-8ed3-4543-b29f-0d227b227bae")#Le linee d'ombra
listContentId.append("Peter-Pan-3b4c4232-6a77-4ef0-bf93-785a72bf689f")#
listContentId.append("Le-notti-bianche-6e506202-274f-41dd-b7ad-ed1b79dfc08c")#
listContentId.append("Mephisto-d07ec271-cb25-4ff2-aff6-653e540ab1f2")#
listContentId.append("Utz-c0c8080b-e823-4db6-b1fc-fec052899180")#
listContentId.append("Il-gioco-dei-regni-78819daf-bb84-4b04-a561-b389d2b22183")#

#TODO Il conformista
# https://www.raiplayradio.it/programmi/adaltavoce/archivio/puntate/Il-conformista-657fb6ac-1c2a-473d-9bf2-43d56e51ff20

audiobooks = []
bookId = 0

print ("=== Content List ===")
for i in range(len(listContentId)):
	page = 1
	print (listContentId[i])

	book = {}
	book['id'] = bookId
	bookContents = []
	chapterId = 0

	image = ''
	image_300 = ''
	image_433 = ''	
	booktitle = ''
	
	foundChapters = True

	while (foundChapters):
		
		url = 'https://www.raiplayradio.it/programmi/adaltavoce/archivio/puntate/' + listContentId[i] + "/" + str(page)
		print("Url: " + url)		
		params = {}
		headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36',
			   'Accept' : 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8' ,
			   'Accept-Language' : 'it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7',
			   'Accept-Encoding' : 'gzip, deflate, br',
			   'X-Requested-With': 'XMLHttpRequest',
			   'Host': 'www.raiplayradio.it',
			   'Referer': 'https://www.raiplayradio.it/playlist/' + listContentId[i] + '.html',
			   'Cookie': '_iub_cs-844946=%7B%22consent%22%3Atrue%2C%22timestamp%22%3A%222018-12-11T09%3A54%3A42.004Z%22%2C%22version%22%3A%220.13.24%22%2C%22id%22%3A844946%7D; wt_rla=602039762736393%2C21%2C1544522080017; AKA_A2=A',
			   'Connection': 'keep-alive',
			   'Cache-Control': 'max-age=0',
			   'Upgrade-Insecure-Requests': '1'
		} 	
		
		response = requests.get(url, params=params, headers=headers)		

		try:
			#print (response)

			body = BeautifulSoup(response.content, "html.parser")
		
			#print(body.prettify())

			# td = soup.body.td  # or soup.find('td', id='aisd_calendar-2014-04-28-0')
			# print (td['date'].strip('*'))
			# link = soup.find('div', {'class': 'contents'}).a
			# print (link['href'])
		
			print('-------')
			listElements = body.find_all('div')
		
			#print ('\n\n\n------------------ ELEMENTS ------------------\n\n\n')
			#print (listElements)

			foundChapters = False

			for el in listElements:
				chapter = {}

				#print ("------------------------------------")
				#print (el)
			
				try:
					#print ("data-mediapolis: " + el['data-mediapolis'])	
					#print ("data-title: " + el['data-title'])	

					if(booktitle == ''):
						booktitle = el['data-title']

					chapter['contentId'] = listContentId[i]
					chapter['id'] = chapterId
					chapter['title'] = el['data-title']
					chapter['desc'] = el.find('p').string
					chapter['url'] = el['data-mediapolis']
					chapter['format'] = 'mp3'
					chapter['weblink'] = url
					chapter['webLinkLookup'] = False
				
					chapterId = chapterId + 1
				
					#print ('Chapter: ' + str(chapter))

					bookContents.append(chapter)

					foundChapters = True

				except:
					# print ('---Exception')
					print ("")
		except:
			receivedNotFound = True
			print ('----------------------- Exception ---------------------')
			traceback.print_exc()
			print(url)
	
		page = page + 1

		print ('Next page: ' + str(page))

	bookContents = list(reversed(bookContents))

	book['contents'] = bookContents
	
	book['metadata'] = {		
		'image': 'http://www.rai.tv' + image,
		'title': booktitle,
		'author': booktitle,
		'other_images' : {
			'image_300' : 'http://www.rai.tv' + image_300,
			'image_433' : 'http://www.rai.tv' + image_433
		},
		'provider' : {
			'name' : 'Ad Alta Voce',
			'site' : 'http://www.adaltavoce.rai.it/',
			'image' : ''
		}
	}

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

	time.sleep(3)

json_data = json.dumps(data)

print(json.dumps(data, sort_keys=True, indent=4))



