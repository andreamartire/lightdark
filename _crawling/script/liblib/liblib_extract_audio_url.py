#!/usr/bin/python3.5

import urllib.request
import re
import requests
import json
import time
import traceback

print ('Start')

#https://www.liberliber.it/online/opere/audiolibri/elenco-per-opere/

sourceMainPage = "<li><em><a href=\"https://www.liberliber.it/online/autori/autori-t/lev-nikolaevic-tolstoj/anna-karenina-audiolibro/\">Anna Karenina [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-t/lev-nikolaevic-tolstoj/\">Lev Nikolaevič Tolstoj</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-t/lev-nikolaevic-tolstoj/confessione-audiolibro/\">Confessione [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-t/lev-nikolaevic-tolstoj/\">Lev Nikolaevič Tolstoj</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/matilde-serao/cristina-audiolibro/\">Cristina [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/matilde-serao/\">Matilde Serao</a></li>"

backup = "<li><em><a href=\"https://www.liberliber.it/online/autori/autori-g/antonio-gramsci/lalbero-del-riccio-audiolibro/\">L'albero del riccio [audiolibro]</a></em>,  di <a href=\"https://www.libekaliber.it/online/autori/autori-g/antonio-gramsci/\">Antonio Gramsci</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-d/charles-lutwidge-dodgson-alias-lewis-carroll/alice-nel-paese-delle-meraviglie-audiolibro/\">Alice nel paese delle meraviglie [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-d/charles-lutwidge-dodgson-alias-lewis-carroll/\">Charles Lutwidge Dodgson (alias Lewis Carroll)</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-t/lev-nikolaevic-tolstoj/anna-karenina-audiolibro/\">Anna Karenina [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-t/lev-nikolaevic-tolstoj/\">Lev Nikolaevič Tolstoj</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-p/plato-platone/apologia-di-socrate-audiolibro/\">Apologia di Socrate [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-p/plato-platone/\">Plato (Platone)</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-d/charles-dickens/le-avventure-di-nicola-nickleby-audiolibro/\">Le avventure di Nicola Nickleby [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-d/charles-dickens/\">Charles Dickens</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-c/giulio-cesare-croce/bertoldo-e-bertoldino-audiolibro/\">Bertoldo e Bertoldino [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-c/giulio-cesare-croce/\">Giulio Cesare Croce</a><br><span class=\"ll_libro_sottotitolo\">Col Cacasenno di Adriano Banchieri</span></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-m/giulio-mazzarino/breviario-dei-politici-secondo-il-cardinale-mazzarino-audiolibro/\">Breviario dei politici secondo il Cardinale Mazzarino [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-m/giulio-mazzarino/\">Giulio Mazzarino</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-g/maksim-gorkij-alias-aleksej-maksimovic-peskov/il-burlone-audiolibro/\">Il burlone [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-g/maksim-gorkij-alias-aleksej-maksimovic-peskov/\">Maksim Gor'kij (alias Aleksej Maksimovič Peškov)</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-d/charles-dickens/cantico-di-natale-audiolibro/\">Cantico di Natale [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-d/charles-dickens/\">Charles Dickens</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/harriet-beecher-stowe/la-capanna-dello-zio-tom-audiolibro/\">La capanna dello zio Tom [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/harriet-beecher-stowe/\">Harriet Beecher Stowe</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-f/salvatore-farina/carta-bollata-audiolibro/\">Carta bollata [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-f/salvatore-farina/\">Salvatore Farina</a><br><span class=\"ll_libro_sottotitolo\">Racconto</span></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/stendhal-alias-marie-henri-beyle/la-certosa-di-parma-audiolibro/\">La Certosa di Parma [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/stendhal-alias-marie-henri-beyle/\">Stendhal (alias Marie-Henri Beyle)</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-t/lev-nikolaevic-tolstoj/confessione-audiolibro/\">Confessione [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-t/lev-nikolaevic-tolstoj/\">Lev Nikolaevič Tolstoj</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-g/antonio-ghislanzoni/la-contessa-di-karolystria-audiolibro/\">La contessa di Karolystria [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-g/antonio-ghislanzoni/\">Antonio Ghislanzoni</a><br><span class=\"ll_libro_sottotitolo\">Storia tragicomica</span></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/italo-alias-ettore-schmitz-svevo/la-coscienza-di-zeno-audiolibro/\">La coscienza di Zeno [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/italo-alias-ettore-schmitz-svevo/\">Italo Svevo (alias Ettore Schmitz)</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/matilde-serao/cristina-audiolibro/\">Cristina [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/matilde-serao/\">Matilde Serao</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-d/edmondo-de-amicis/cuore-audiolibro/\">Cuore [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-d/edmondo-de-amicis/\">Edmondo De Amicis</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-g/guido-gozzano/la-danza-degli-gnomi-e-altre-fiabe-audiolibro/\">La danza degli gnomi e altre fiabe [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-g/guido-gozzano/\">Guido Gozzano</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-d/charles-dickens/david-copperfield-audiolibro/\">David Copperfield [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-d/charles-dickens/\">Charles Dickens</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-b/giovanni-boccaccio/decameron-mondadori-audiolibro/\">Decameron [Mondadori] [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-b/giovanni-boccaccio/\">Giovanni Boccaccio</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-a/dante-alighieri/la-divina-commedia-edizione-petrocchi-audiolibro/\">La Divina Commedia [edizione Petrocchi] [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-a/dante-alighieri/\">Dante Alighieri</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-g/johann-wolfgang-von-goethe/i-dolori-del-giovane-werther-audiolibro/\">I dolori del giovane Werther [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-g/johann-wolfgang-von-goethe/\">Johann Wolfgang von Goethe</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-e/erasmus-roterodamus-erasmo-da-rotterdam/elogio-della-follia-audiolibro/\">Elogio della Follia [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-e/erasmus-roterodamus-erasmo-da-rotterdam/\">Erasmus Roterodamus (Erasmo da Rotterdam)</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-c/domenico-ciampoli/fiabe-abruzzesi-audiolibro/\">Fiabe abruzzesi [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-c/domenico-ciampoli/\">Domenico Ciampoli</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-d/giovanni-della-casa/galateo-overo-de-costumi-audiolibro/\">Galateo, overo De' costumi [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-d/giovanni-della-casa/\">Giovanni della Casa</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-g/giornale-per-i-bambini/giornale-per-i-bambini-audiolibro/\">Giornale per i bambini [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-g/giornale-per-i-bambini/\">Giornale per i bambini</a><br><span class=\"ll_libro_sottotitolo\">Antologia</span></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-b/charlotte-bronte/jane-eyre-audiolibro/\">Jane Eyre [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-b/charlotte-bronte/\">Charlotte Brontë</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-p/marco-polo/il-milione-audiolibro/\">Il Milione [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-p/marco-polo/\">Marco Polo</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-m/mille-e-una-notte/le-mille-e-una-notte-audiolibro/\">Le mille e una notte [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-m/mille-e-una-notte/\">Mille e una notte</a><br><span class=\"ll_libro_sottotitolo\">Novelle arabe</span></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-p/ferdinando-paolieri/novelle-agrodolci-audiolibro/\">Novelle agrodolci [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-p/ferdinando-paolieri/\">Ferdinando Paolieri</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/emilio-salgari/le-novelle-marinaresche-di-mastro-catrame-audiolibro/\">Le novelle marinaresche di mastro Catrame [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/emilio-salgari/\">Emilio Salgari</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-p/ferdinando-paolieri/novelle-selvagge-audiolibro/\">Novelle selvagge [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-p/ferdinando-paolieri/\">Ferdinando Paolieri</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-p/ferdinando-paolieri/novelle-toscane-audiolibro/\">Novelle toscane [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-p/ferdinando-paolieri/\">Ferdinando Paolieri</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-a/ludovico-ariosto/orlando-furioso-segre-audiolibro/\">Orlando furioso [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-a/ludovico-ariosto/\">Ludovico Ariosto</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-b/james-matthew-barrie/peter-pan-nei-giardini-di-kensington-audiolibro/\">Peter Pan nei giardini di Kensington [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-b/james-matthew-barrie/\">James Matthew Barrie</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-a/louisa-may-alcott/piccole-donne-audiolibro/\">Piccole donne [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-a/louisa-may-alcott/\">Louisa May Alcott</a><br><span class=\"ll_libro_sottotitolo\">Romanzo</span></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-c/carlo-collodi-alias-carlo-lorenzini/pinocchio-audiolibro/\">Pinocchio [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-c/carlo-collodi-alias-carlo-lorenzini/\">Carlo Collodi (alias Carlo Lorenzini)</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-m/alessandro-francesco-tommaso-manzoni/i-promessi-sposi-edizione-a-mondadori-1985-audiolibro/\">I promessi sposi [edizione A. Mondadori, 1985] [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-m/alessandro-francesco-tommaso-manzoni/\">Alessandro Francesco Tommaso Manzoni</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-l/luther-blissett/q-audiolibro/\">Q [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-l/luther-blissett/\">Luther Blissett</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-p/charles-perrault/i-racconti-delle-fate-audiolibro/\">I racconti delle fate [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-p/charles-perrault/\">Charles Perrault</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-n/gherardo-nerucci/sessanta-novelle-popolari-montalesi-audiolibro/\">Sessanta novelle popolari montalesi [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-n/gherardo-nerucci/\">Gherardo Nerucci</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/william-shakespeare/sogno-di-una-notte-di-mezza-estate-audiolibro/\">Sogno di una notte di mezza estate [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/william-shakespeare/\">William Shakespeare</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/strenna-di-ascolti-per-il-natale/strenna-di-ascolti-per-il-natale-audiolibro/\">Strenna di ascolti per il Natale [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/strenna-di-ascolti-per-il-natale/\">Strenna di ascolti per il Natale</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-l/lao-tzu/tao-te-ching-audiolibro/\">Tao Te Ching [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-l/lao-tzu/\">Lao Tzu</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/william-shakespeare/la-tempesta-audiolibro/\">La tempesta [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/william-shakespeare/\">William Shakespeare</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/tre-uomini-in-una-barca-audiolibro/\">Tre uomini in una barca [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/\">Jerome K. Jerome</a><br><span class=\"ll_libro_sottotitolo\">(per tacer del cane)</span></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-e/emma-alias-emilia-ferretti-viola/una-fra-tante-audiolibro/\">Una fra tante [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-e/emma-alias-emilia-ferretti-viola/\">Emma (alias Emilia Ferretti Viola)</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-s/jonathan-swift/i-viaggi-di-gulliver-audiolibro/\">I viaggi di Gulliver [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-s/jonathan-swift/\">Jonathan Swift</a></li><li><em><a href=\"https://www.liberliber.it/online/autori/autori-d/federico-de-roberto/i-vicere-audiolibro/\">I Viceré [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-d/federico-de-roberto/\">Federico De Roberto</a></li>";

rows = str(sourceMainPage).split("<li><em>")#, sourceMainPage.count(str))

#prepare regex to extract audiobook contentId
regContent = re.compile(".*?<a href=\"(.*?)\">(.*) \[audiolibro\]</a>(.*?di <a href=\".*?\">(.*?)</a>)?")

audiobooks = []
listUrl = []

for i in range(len(rows)):
	print ("ROW: " + rows[i] + "\n");
	
	#<a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/tre-uomini-in-una-barca-audiolibro/\">Tre uomini in una barca [audiolibro]</a></em>,  di <a href=\"https://www.liberliber.it/online/autori/autori-j/jerome-k-jerome/\">Jerome K. Jerome</a><br><span class=\"ll_libro_sottotitolo\">(per tacer del cane)</span></li>
	if (regContent.match(rows[i])):
		link = str(regContent.match(rows[i]).group(1));
		print("--- Link ---"+link+"---")
		title = str(regContent.match(rows[i]).group(2))
		print("--- Title ---"+title+"---")
		author = str(regContent.match(rows[i]).group(4))
		print("--- Author ---"+author+"---")
		listUrl.append(link)
		
		book = {}
		bookContents = []
		
		print(link)
		
		wp = urllib.request.urlopen(link)
		book_str = str(wp.read())
		
		#with open('book_test.txt', 'r') as myfile:
		#	book_str=myfile.read().replace('\n', '')

		#chaptSectionReg = re.compile(".*\"lm_mp3\">(.*?)</ul><br")
		chaptLinkReg = re.compile(".*<a href=\"(.*?.mp3)\">(.*?)</a>")

		print(book_str)
		
		#if (chaptSectionReg.match(book_str)):
			#book_str = chaptSectionReg.match(book_str).group(1)

		chapterId = 0
		bookRows = book_str.split("<li>")
		for i in range(len(bookRows)):
			print ("BOOK ROW: " + bookRows[i] + "\n");
			#<a href="https://www.liberliber.it/mediateca/audiolibri/d/de_roberto/i_vicere/mp3/de_rober_i_vicere_sil_51_p3_9a.mp3">Parte terza: 9 - a</a></li>
			if (chaptLinkReg.match(bookRows[i])):
				url = chaptLinkReg.match(bookRows[i]).group(1)
				chapTitle = chaptLinkReg.match(bookRows[i]).group(2)
				print("--- Url ---" + url + "---")
				print("--- Title ---" + chapTitle + "---")

				chapter = {}
				chapter['id'] = chapterId
				chapter['title'] = chapTitle
				chapter['desc'] = ''
				chapter['url'] = url
				chapter['format'] = 'mp3'

				chapterId = chapterId + 1

				print ('Chapter: ' + str(chapter))

				bookContents.append(chapter)
		
		book['contents'] = bookContents
		
		book['metadata'] = {		
			'image': '',
			'other_images' : {
				'image_300' : '',
				'image_433' : '',
			},
			'provider' : {
				'name' : 'Liber Liber',
				'site' : 'https://www.liberliber.it',
				'image' : ''
			},
			'title': title,
			'author': author
		}
		
		print ('Book: ' + str(book))
		
		audiobooks.append(book)

	data = {}
	data['version'] = 0
	data['config'] = {}
	data['audiobooks'] = audiobooks

	out_file = open("config_liblib.json","w")
	out_file.write(json.dumps(data, sort_keys=True, indent=4))
	out_file.close()

	#time.sleep(10)

json_data = json.dumps(data)

print(json.dumps(data, sort_keys=True, indent=4))






