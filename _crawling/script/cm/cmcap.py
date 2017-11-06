#!/usr/bin/python3.5

import re
import requests
import json
import time
import traceback
import datetime
import krakenex

wallet = {
	'BTC': { 'num': 0, 'price': 0 },
	'ETH': { 'num': 0, 'price': 302.44 },
	'XRP': { 'num': 0, 'price': 0 },
	'LTC': { 'num': 0, 'price': 0 },
	'GNO': { 'num': 0, 'price': 0 },
	'EOS': { 'num': 0, 'price': 0 },
	'BCH': { 'num': 0, 'price': 0 },
	'DASH': { 'num': 0, 'price': 0 },
	'XMR': { 'num': 0, 'price': 0 },
	'XLM': { 'num': 0, 'price': 0 },
	'ETC': { 'num': 0, 'price': 0 },
	'REP': { 'num': 0, 'price': 0 },
	'ICN': { 'num': 0, 'price': 0 },
	'MLN': { 'num': 0, 'price': 56.38 },
	'ZEC': { 'num': 0, 'price': 0 },
	'DOGE': { 'num': 0, 'price': 0 },
	'USDT': { 'num': 0, 'price': 0 }
}

coinMap = {"XXBT": "BTC", "XETH": "ETH", "XETC": "ETC", "XMLN": "MLN"}

k = krakenex.API()
k.load_key('kraken.key')
balance = k.query_private('Balance')["result"]

#print (balance)

for key in balance:
	if(key in coinMap.keys()):
		p = wallet[coinMap[key]]["price"]
		wallet[coinMap[key]] = { 'num': float(balance[key]), 'price': p }
	else:
		p = wallet[key]["price"]
		wallet[key] = { 'num': float(balance[key]), 'price': p }

headers = {
    'User-Agent' : 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
}

#print ('Start')

link = "https://coinmarketcap.com/all/views/all/"

#<span class="currency-symbol"><a href="/currencies/infchain/">INF</a></span>
#<span class="currency-symbol"><a href="/currencies/bitcoin/">BTC</a></span>
regCoinName = re.compile(".*?<span class=\"currency-symbol\">.*?>(.*?)</a></span>")

#<a class="currency-name-container" href="/currencies/bitcoin/">Bitcoin</a>
regCoinDesc = re.compile(".*?<a class=\"currency-name-container\".*?>(.*?)</a>")

#<a href="/currencies/infchain/#markets" class="price" data-usd="0.00726931" data-btc="1.05737e-06" >$0.007269</a>
regCoinPrice = re.compile(".*? class=\"price\" .*?>\$(.*?)</a>")

#<td class="no-wrap percent-1h  positive_change  text-right" data-usd="0.32" data-btc="0.82" >0.32%</td>
regLastHourChange = re.compile(".*?<td class=\"no-wrap percent-1h .*?>(&gt; )?(.*?)%</td>")

#<td class="no-wrap percent-24h  positive_change  text-right" data-usd="2.33" data-btc="5.11" >=2.33%</td>
regLastDayChange = re.compile(".*?<td class=\"no-wrap percent-24h .*?>(&gt; )?(.*?)%</td>")

#<td class="no-wrap percent-7d  negative_change text-right" data-usd="-26.69" data-btc="-41.32" >-26.69%</td>
regLastWeekChange = re.compile(".*?<td class=\"no-wrap percent-7d .*?>(&gt; )?(.*?)%</td>")

#print ("size: " + str(len(rows)))

previousCoins = {}
coins = {}

fee = 0.26
bound = 1

def updateMktData():
	global coins
	previousCoins = coins
	coins = {}

	sourceMainPage = str(requests.get(link, headers = headers).text)

	rows = str(sourceMainPage).split("<tbody>")
	rows = str(rows[1]).split("</tbody>")
	rows = str(rows[0]).split("</tr>")

	for i in range(len(rows)):
		#print ("C: " + rows[i] + "\n");

		elements = str(rows[i]).split("\n")

		coin = {}
		coin["desc"] = None
		coin["price"] = 0
		coin["5m"] = 0
		coin["1h"] = 0
		coin["24h"] = 0
		coin["7d"] = 0

		for j in range(len(elements)):
			if(regCoinName.match(elements[j])):
				cName = str(regCoinName.match(elements[j]).group(1))
				#print("Name:\t"+cName)
				coin["name"] = cName
			
			if(regCoinDesc.match(elements[j])):
				cDesc = str(regCoinDesc.match(elements[j]).group(1))
				#print("Desc:\t"+cDesc)
				coin["desc"] = cDesc
			
			if(regCoinPrice.match(elements[j])):
				cPrice = float(regCoinPrice.match(elements[j]).group(1).replace(",",''))
				#print("Price:\t"+str(cPrice))
				coin["price"] = cPrice
			
			if(regLastHourChange.match(elements[j])):
				cLastHourChange = float(regLastHourChange.match(elements[j]).group(2))
				#print("1h:\t"+str(cLastHourChange))
				coin["1h"] = cLastHourChange
			
			if(regLastDayChange.match(elements[j])):
				cLastDayChange = float(regLastDayChange.match(elements[j]).group(2))
				#print("24h:\t"+str(cLastDayChange))
				coin["24h"] = cLastDayChange
						
			if(regLastWeekChange.match(elements[j])):
				cLastWeekChange = float(regLastWeekChange.match(elements[j]).group(2))
				#print("7d:\t"+str(cLastWeekChange))
				coin["7d"] = cLastWeekChange

		if('name' in coin):
			coins[coin["name"]] = coin

			#print("--------------------------------------")

	if(len(previousCoins.keys()) > 0):
		for key in previousCoins.keys():
			#print ("-- key "+key)
			oldCoin = previousCoins[key]
			newCoin = coins[key]	

			#print ("Old Price "+str(oldCoin["price"]))
			#print ("New Price "+str(newCoin["price"]))
			delta = (newCoin["price"] - oldCoin["price"])/oldCoin["price"]
			coins[key]["5m"] = delta		
			#print ("% var "+str(delta))


	#print(json.dumps(coins, sort_keys=True, indent=4))
	print ("======================================= MARKET ======================================")
	for key in wallet.keys():
		#print (key)
		print (str(coins[key]))
	print ("=====================================================================================")

def change(wallet, coins, oldVal, newVal):
	print ("CHANGE "+oldVal+" to "+newVal)
	old = wallet[oldVal]
	if(newVal not in wallet):
		wallet[newVal] = 0
	new = wallet[newVal]

	oldCoinMkt = coins[oldVal]
	newCoinMkt = coins[newVal]

	startVal = old["num"]*oldCoinMkt["price"] 
	#print ("startVal " + str(startVal))
	valAfterFee = startVal - (startVal*fee/100)#fee
	#print ("valAfterFee " + str(valAfterFee))
	valConverted = valAfterFee/newCoinMkt["price"]
	#print ("valConverted " + str(valConverted))
	
	wallet[oldVal]["num"] = 0
	wallet[newVal]["num"] = valConverted

def getWalletVal(wallet):
	tot = 0
	for key in wallet.keys():
		tot += coins[key]["price"]*wallet[key]["num"]	
	return tot

def getLastChangeWalletVal(wallet):
	tot = 0
	for key in wallet.keys():
		tot += wallet[key]["price"]*wallet[key]["num"]	
	return tot

def showWalletLine(wallet, key):
	if(wallet[key]["num"] != 0):
		print (key + ": " + str("{0:.10f}".format(wallet[key]["num"])) + "\tV:" +str("{0:.5f}".format(coins[key]["price"]*wallet[key]["num"])) + "\tM:" +str(coins[key]["price"]) + "\t(Old " + str(wallet[key]["price"]) + ")")

def showWallet(wallet):
	print ("================================ WALLET ================================")
	for key in wallet.keys():
		showWalletLine(wallet, key)

	lastW = getLastChangeWalletVal(wallet)
	currW = getWalletVal(wallet)
	print ("LAST CHANGE VAL: " + str(lastW))
	print ("CURRENT VALUE:   " + str(currW))
	print ("========================== " + str("{0:.2f}".format((currW-lastW))) + "$ (" + str("{0:.3f}".format(((currW-lastW)/lastW)*100)) + "%) ==========================")

def engine(wallet):

	actions = [];

	lastChangeVal = getLastChangeWalletVal(wallet)
	currVal = getWalletVal(wallet)
	
	currFee = currVal*fee/100

	print ("IF CHANGE -> CurrValue: " + str(currVal) + " FutureValue: " +str(currVal - currFee) + " (Fee: " + str(currFee) + ")")
	if(currVal - currFee <= lastChangeVal):
		print ("NB. IF CHANGE LOSE VALUE. ")
	else:
		print ("NB. IF CHANGE GAIN. ")
	
	#TODO no action
	return []
	
	#btc strategy
	if(wallet["BTC"]["num"] > 0):
		if(coins["ETH"]["1h"]>=bound):
			#nochange. default strategy
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "ETH", "gain": coins["ETH"]["1h"]})
		if(coins["XRP"]["1h"]>=bound):
			#buy xrp with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "XRP", "gain": coins["XRP"]["1h"]})
		if(coins["LTC"]["1h"]>=bound):
			#buy ltc with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "LTC", "gain": coins["LTC"]["1h"]})
		if(coins["GNO"]["1h"]>=bound):
			#buy gno with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "GNO", "gain": coins["GNO"]["1h"]})
		if(coins["EOS"]["1h"]>=bound):
			#buy eos with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "EOS", "gain": coins["EOS"]["1h"]})
		if(coins["BCH"]["1h"]>=bound):
			#buy bch with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "BCH", "gain": coins["BCH"]["1h"]})
		if(coins["DASH"]["1h"]>=bound):
			#buy dash with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "DASH", "gain": coins["DASH"]["1h"]})
		if(coins["XMR"]["1h"]>=bound):
			#buy xmr with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "XMR", "gain": coins["XMR"]["1h"]})
		if(coins["XLM"]["1h"]>=bound):
			#buy xlm with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "XLM", "gain": coins["XLM"]["1h"]})
		if(coins["ETC"]["1h"]>=bound):
			#buy etc with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "ETC", "gain": coins["ETC"]["1h"]})
		if(coins["REP"]["1h"]>=bound):
			#buy rep with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "REP", "gain": coins["REP"]["1h"]})
		if(coins["ICN"]["1h"]>=bound):
			#buy dash with icn
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "ICN", "gain": coins["ICN"]["1h"]})
		if(coins["MLN"]["1h"]>=bound):
			#buy mln with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "MLN", "gain": coins["MLN"]["1h"]})
		if(coins["ZEC"]["1h"]>=bound):
			#buy zec with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "ZEC", "gain": coins["ZEC"]["1h"]})
		if(coins["DOGE"]["1h"]>=bound):
			#buy doge with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "DOGE", "gain": coins["DOGE"]["1h"]})
		if(coins["USDT"]["1h"]>=bound):
			#buy usdt with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "USDT", "gain": coins["USDT"]["1h"]})

	#eth strategy
	if(wallet["ETH"]["num"] > 0 and coins["ETH"]["1h"]<0):
		if(coins["BTC"]["1h"]>=bound):
			#buy btc with eth
			actions.append({"type": "CHANGE", "old_coin": "ETH", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})
		if(coins["GNO"]["1h"]>=bound):
			#buy gno with eth
			actions.append({"type": "CHANGE", "old_coin": "ETH", "new_coin": "GNO", "gain": coins["GNO"]["1h"]})
		if(coins["EOS"]["1h"]>=bound):
			#buy eos with eth
			actions.append({"type": "CHANGE", "old_coin": "ETH", "new_coin": "EOS", "gain": coins["EOS"]["1h"]})

	#xrp strategy
	if(wallet["XRP"]["num"] > 0 and coins["XRP"]["1h"]<0):
		#buy btc with xrp
		actions.append({"type": "CHANGE", "old_coin": "XRP", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	#ltc strategy
	if(wallet["LTC"]["num"] > 0 and coins["LTC"]["1h"]<0):
		#buy btc with ltc
		actions.append({"type": "CHANGE", "old_coin": "LTC", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	#eos strategy
	if(wallet["EOS"]["num"] > 0 and coins["EOS"]["1h"]<0):
		if(coins["BTC"]["1h"]>=bound):
			#buy btc with btc
			actions.append({"type": "CHANGE", "old_coin": "EOS", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})
		elif(coins["ETH"]["1h"]>=bound):
			#buy eth with eth
			actions.append({"type": "CHANGE", "old_coin": "EOS", "new_coin": "ETH", "gain": coins["ETC"]["1h"]})
	
	#gno strategy
	if(wallet["GNO"]["num"] > 0 and coins["GNO"]["1h"]<0):
		if(btc["1h"]>=bound):
			#buy btc with btc
			actions.append({"type": "CHANGE", "old_coin": "GNO", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})
		elif(eth["1h"]>=bound):
			#buy eth with eth
			actions.append({"type": "CHANGE", "old_coin": "GNO", "new_coin": "ETH", "gain": coins["ETH"]["1h"]})
	
	#bch strategy
	if(wallet["BCH"]["num"] > 0 and coins["BCH"]["1h"]<0):
		#buy btc with bch
		actions.append({"type": "CHANGE", "old_coin": "BCH", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	#dash strategy
	if(wallet["DASH"]["num"] > 0 and coins["DASH"]["1h"]<0):
		#buy btc with ltc
		actions.append({"type": "CHANGE", "old_coin": "DASH", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})
	
	#xmr strategy
	if(wallet["XMR"]["num"] > 0 and coins["XMR"]["1h"]<0):
		#buy btc with xlm
		actions.append({"type": "CHANGE", "old_coin": "XMR", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})
	
	#xlm strategy
	if(wallet["XLM"]["num"] > 0 and coins["XLM"]["1h"]<0):
		#buy btc with xlm
		actions.append({"type": "CHANGE", "old_coin": "XLM", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	#etc strategy
	if(wallet["ETC"]["num"] > 0 and coins["ETC"]["1h"]<0):
		#buy btc with xlm
		actions.append({"type": "CHANGE", "old_coin": "ETC", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	#rep strategy
	if(wallet["REP"]["num"] > 0 and coins["REP"]["1h"]<0):
		#buy btc with rep
		actions.append({"type": "CHANGE", "old_coin": "REP", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	#icn strategy
	if(wallet["ICN"]["num"] > 0 and coins["ICN"]["1h"]<0):
		#buy btc with icn
		actions.append({"type": "CHANGE", "old_coin": "ICN", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})
	
	#mln strategy
	if(wallet["MLN"]["num"] > 0 and coins["MLN"]["1h"]<0):
		#buy btc with mln
		actions.append({"type": "CHANGE", "old_coin": "MLN", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	#zec strategy
	if(wallet["ZEC"]["num"] > 0 and coins["ZEC"]["1h"]<0):
		#buy btc with zec
		actions.append({"type": "CHANGE", "old_coin": "ZEC", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	#doge strategy
	if(wallet["DOGE"]["num"] > 0 and coins["DOGE"]["1h"]<0):
		#buy btc with doge
		actions.append({"type": "CHANGE", "old_coin": "DOGE", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})
	
	#usdt strategy
	if(wallet["USDT"]["num"] > 0 and coins["USDT"]["1h"]<0):
		#buy btc with zec
		actions.append({"type": "CHANGE", "old_coin": "USDT", "new_coin": "BTC", "gain": coins["BTC"]["1h"]})

	maxGain = 0
	bestAction = None
	for action in actions:
		#print ("Candidate: " + str(action))
		if(action["gain"] > maxGain):
			maxGain = action["gain"]
			bestAction = action

	if(bestAction is None):
		return []
	return [bestAction]

lastWalletVal = 0

while(True):
	print ("--------------------------------------------------------------------------------------------------")
	print (datetime.datetime.now().strftime("%Y-%m-%d %H:%M"))
	updateMktData()

	showWallet(wallet)

	actions = engine(wallet)

	print ("ACTIONS: " + str(actions))

	if(len(actions)>0):
		for action in actions:
			if(False and action["type"] == "CHANGE"):#TODO
				change(wallet, coins, action["old_coin"], action["new_coin"])
	
	lastWalletVal = getWalletVal(wallet)

	showWallet(wallet)

	time.sleep(60*5)# 5 min



