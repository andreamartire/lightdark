#!/usr/bin/python3.5

import re
import requests
import json
import time
import traceback
import datetime

wallet = {
	'BTC': { 'num': 0.00002, 'price': 7527.6 },
	'ETH': { 'num': 0.10902, 'price': 295.31 },
	'XRP': { 'num': 0, 'price': 0 },
	'LTC': { 'num': 0, 'price': 0 },
	'GNO': { 'num': 0, 'price': 0 },
	'EOS': { 'num': 0, 'price': 0 },
	'BCH': { 'num': 0, 'price': 0 },
	'DASH': { 'num': 0, 'price': 0 },
	'XMR': { 'num': 0, 'price': 0 },
	'XLM': { 'num': 0, 'price': 0 },
	'ETC': { 'num': 0.36, 'price': 13.88 },
	'REP': { 'num': 0, 'price': 0 },
	'ICN': { 'num': 0, 'price': 0 },
	'MLN': { 'num': 0, 'price': 0 },
	'ZEC': { 'num': 0, 'price': 0 },
	'DOGE': { 'num': 0, 'price': 0 },
	'USDT': { 'num': 0, 'price': 0 },
	'USD': { 'num': 0, 'price': 0 }
}

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

btc = {}
eth = {}
xrp = {}
ltc = {}
gno = {}
eos = {}
bch = {}
dash = {}
xmr = {}
etc = {}
rep = {}
icn = {}
mln = {}
zec = {}
doge = {}
usdt = {}
usd = {}

fee = 0.26
bound = 0.009

def updateMktData():
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

			if(coin["name"] == "BTC"):
				global btc
				btc = coin

			if(coin["name"] == "ETH"):
				global eth
				eth = coin

			if(coin["name"] == "BCH"):#Bch market name
				global bch
				bch = coin
	
			if(coin["name"] == "XRP"):
				global xrp
				xrp = coin

			if(coin["name"] == "LTC"):
				global ltc
				ltc = coin

			if(coin["name"] == "EOS"):
				global eos
				eos = coin
		
			if(coin["name"] == "GNO"):
				global gno
				gno = coin

			if(coin["name"] == "BCH"):
				global bch
				bch = coin

			if(coin["name"] == "DASH"):
				global dash
				dash = coin
			
			if(coin["name"] == "XMR"):
				global xmr
				xmr = coin

			if(coin["name"] == "XLM"):
				global xlm
				xlm = coin
			
			if(coin["name"] == "ETC"):
				global etc
				etc = coin
			
			if(coin["name"] == "REP"):
				global rep
				rep = coin
			
			if(coin["name"] == "ICN"):
				global icn
				icn = coin
			
			if(coin["name"] == "MLN"):
				global mln
				mln = coin
		
			if(coin["name"] == "ZEC"):
				global zec
				zec = coin

			if(coin["name"] == "DOGE"):#xdg 
				global doge
				doge = coin

			if(coin["name"] == "USDT"):
				global usdt
				usdt = coin

			global coins
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
	print "===== MARKET ===="
	print str(btc)
	print str(eth)
	print str(xrp)
	print str(ltc)
	print str(gno)
	print str(eos)
	print str(bch)
	print str(dash)
	print str(xmr)
	print str(xlm)
	print str(etc)
	print str(rep)
	print str(icn)
	print str(mln)
	print str(zec)
	print str(doge)
	print str(usdt)
	print str(usd)
	print "================="

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
	tot += btc["price"]*wallet["BTC"]["num"]
	tot += eth["price"]*wallet["ETH"]["num"]
	tot += xrp["price"]*wallet["XRP"]["num"]
	tot += ltc["price"]*wallet["LTC"]["num"]
	tot += gno["price"]*wallet["GNO"]["num"]
	tot += eos["price"]*wallet["EOS"]["num"]
	tot += bch["price"]*wallet["BCH"]["num"]
	tot += dash["price"]*wallet["DASH"]["num"]
	tot += xmr["price"]*wallet["XMR"]["num"]
	tot += xlm["price"]*wallet["XLM"]["num"]
	tot += etc["price"]*wallet["ETC"]["num"]
	tot += rep["price"]*wallet["REP"]["num"]
	tot += icn["price"]*wallet["ICN"]["num"]
	tot += mln["price"]*wallet["MLN"]["num"]
	tot += zec["price"]*wallet["ZEC"]["num"]
	tot += doge["price"]*wallet["DOGE"]["num"]
	tot += usdt["price"]*wallet["USDT"]["num"]
	tot += usdt["price"]*wallet["USD"]["num"]
	
	return tot

def getLastChangeWalletVal(wallet):

	tot = 0
	tot += wallet["BTC"]["price"]*wallet["BTC"]["num"]
	tot += wallet["ETH"]["price"]*wallet["ETH"]["num"]
	tot += wallet["XRP"]["price"]*wallet["XRP"]["num"]
	tot += wallet["LTC"]["price"]*wallet["LTC"]["num"]
	tot += wallet["GNO"]["price"]*wallet["GNO"]["num"]
	tot += wallet["EOS"]["price"]*wallet["EOS"]["num"]
	tot += wallet["BCH"]["price"]*wallet["BCH"]["num"]
	tot += wallet["DASH"]["price"]*wallet["DASH"]["num"]
	tot += wallet["XMR"]["price"]*wallet["XMR"]["num"]
	tot += wallet["XLM"]["price"]*wallet["XLM"]["num"]
	tot += wallet["ETC"]["price"]*wallet["ETC"]["num"]
	tot += wallet["REP"]["price"]*wallet["REP"]["num"]
	tot += wallet["ICN"]["price"]*wallet["ICN"]["num"]
	tot += wallet["MLN"]["price"]*wallet["MLN"]["num"]
	tot += wallet["ZEC"]["price"]*wallet["ZEC"]["num"]
	tot += wallet["DOGE"]["price"]*wallet["DOGE"]["num"]
	tot += wallet["USDT"]["price"]*wallet["USDT"]["num"]
	tot += wallet["USD"]["price"]*wallet["USD"]["num"]
	
	return tot

def showWalletLine(wallet, key):
	if(wallet[key]["num"] <> 0):
		print (key + ": " + str(wallet[key]["num"]) + "\tV:" +str(coins[key]["price"]*wallet[key]["num"]) + "\tM:" +str(coins[key]["price"]))

def showWallet(wallet):
	print ("================ WALLET ================")
	showWalletLine(wallet, "ETH")
	showWalletLine(wallet, "BTC")
	showWalletLine(wallet, "XRP")
	showWalletLine(wallet, "LTC")
	showWalletLine(wallet, "GNO")
	showWalletLine(wallet, "EOS")
	showWalletLine(wallet, "BCH")
	showWalletLine(wallet, "DASH")
	showWalletLine(wallet, "XMR")
	showWalletLine(wallet, "XLM")
	showWalletLine(wallet, "ETC")
	showWalletLine(wallet, "REP")
	showWalletLine(wallet, "ICN")
	showWalletLine(wallet, "MLN")
	showWalletLine(wallet, "ZEC")
	showWalletLine(wallet, "DOGE")
	showWalletLine(wallet, "USDT")
	showWalletLine(wallet, "USD")
	
	print ("====== LAST CHANGE VAL: " + str(getLastChangeWalletVal(wallet)) + " ======")
	print ("========== CURR TOT: " + str(getWalletVal(wallet)) + " ==========")

def engine(wallet):
	btcVal = wallet["BTC"]
	ethVal = wallet["ETH"]
	xrpVal = wallet["XRP"]
	ltcVal = wallet["LTC"]
	gnoVal = wallet["GNO"]
	eosVal = wallet["EOS"]
	bchVal = wallet["BCH"]
	dashVal = wallet["DASH"]
	xmrVal = wallet["XMR"]
	xlmVal = wallet["XLM"]
	etcVal = wallet["ETC"]
	repVal = wallet["REP"]
	icnVal = wallet["ICN"]
	mlnVal = wallet["MLN"]
	zecVal = wallet["ZEC"]
	dogeVal = wallet["DOGE"]
	usdtVal = wallet["USDT"]
	usdVal = wallet["USD"]

	actions = [];

	lastChangeVal = getLastChangeWalletVal(wallet)
	currVal = getWalletVal(wallet)

	if(currVal <= lastChangeVal + currVal*fee):
		print ("NB. IF CHANGE LOSING VALUE")
		#no action
		return []
	
	#btc strategy
	if(btcVal > 0):
		if(eth["1h"]>=bound):
			#nochange. default strategy
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "ETH", "gain": eth["1h"]})
		if(xrp["1h"]>=bound):
			#buy xrp with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "XRP", "gain": xrp["1h"]})
		if(ltc["1h"]>=bound):
			#buy ltc with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "LTC", "gain": ltc["1h"]})
		if(gno["1h"]>=bound):
			#buy gno with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "GNO", "gain": gno["1h"]})
		if(eos["1h"]>=bound):
			#buy eos with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "EOS", "gain": eos["1h"]})
		if(bch["1h"]>=bound):
			#buy bch with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "BCH", "gain": bch["1h"]})
		if(dash["1h"]>=bound):
			#buy dash with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "DASH", "gain": dash["1h"]})
		if(xmr["1h"]>=bound):
			#buy xmr with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "XMR", "gain": xmr["1h"]})
		if(xlm["1h"]>=bound):
			#buy xlm with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "XLM", "gain": xlm["1h"]})
		if(etc["1h"]>=bound):
			#buy etc with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "ETC", "gain": etc["1h"]})
		if(rep["1h"]>=bound):
			#buy rep with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "REP", "gain": rep["1h"]})
		if(icn["1h"]>=bound):
			#buy dash with icn
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "ICN", "gain": icn["1h"]})
		if(mln["1h"]>=bound):
			#buy mln with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "MLN", "gain": mln["1h"]})
		if(zec["1h"]>=bound):
			#buy zec with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "ZEC", "gain": zec["1h"]})
		if(doge["1h"]>=bound):
			#buy doge with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "DOGE", "gain": doge["1h"]})
		if(usdt["1h"]>=bound):
			#buy usdt with btc
			actions.append({"type": "CHANGE", "old_coin": "BTC", "new_coin": "USDT", "gain": usdt["1h"]})

	#eth strategy
	if(ethVal > 0 and eth["1h"]<0):
		if(btc["1h"]>=bound):
			#buy btc with eth
			actions.append({"type": "CHANGE", "old_coin": "ETH", "new_coin": "BTC", "gain": btc["1h"]})
		if(gno["1h"]>=bound):
			#buy gno with eth
			actions.append({"type": "CHANGE", "old_coin": "ETH", "new_coin": "GNO", "gain": gno["1h"]})
		if(eos["1h"]>=bound):
			#buy eos with eth
			actions.append({"type": "CHANGE", "old_coin": "ETH", "new_coin": "EOS", "gain": eos["1h"]})

	#xrp strategy
	if(xrpVal > 0 and xrp["1h"]<0):
		#buy btc with xrp
		actions.append({"type": "CHANGE", "old_coin": "XRP", "new_coin": "BTC", "gain": btc["1h"]})

	#ltc strategy
	if(ltcVal > 0 and ltc["1h"]<0):
		#buy btc with ltc
		actions.append({"type": "CHANGE", "old_coin": "LTC", "new_coin": "BTC", "gain": btc["1h"]})

	#eos strategy
	if(eosVal > 0 and eos["1h"]<0):
		if(btc["1h"]>=bound):
			#buy btc with btc
			actions.append({"type": "CHANGE", "old_coin": "EOS", "new_coin": "BTC", "gain": btc["1h"]})
		elif(eth["1h"]>=bound):
			#buy eth with eth
			actions.append({"type": "CHANGE", "old_coin": "EOS", "new_coin": "ETH", "gain": eos["1h"]})
	
	#gno strategy
	if(gnoVal > 0 and gno["1h"]<0):
		if(btc["1h"]>=bound):
			#buy btc with btc
			actions.append({"type": "CHANGE", "old_coin": "GNO", "new_coin": "BTC", "gain": btc["1h"]})
		elif(eth["1h"]>=bound):
			#buy eth with eth
			actions.append({"type": "CHANGE", "old_coin": "GNO", "new_coin": "ETH", "gain": eth["1h"]})
	
	#bch strategy
	if(bchVal > 0 and bch["1h"]<0):
		#buy btc with bch
		actions.append({"type": "CHANGE", "old_coin": "BCH", "new_coin": "BTC", "gain": btc["1h"]})

	#dash strategy
	if(dashVal > 0 and dash["1h"]<0):
		#buy btc with ltc
		actions.append({"type": "CHANGE", "old_coin": "DASH", "new_coin": "BTC", "gain": btc["1h"]})
	
	#xmr strategy
	if(xmrVal > 0 and xmr["1h"]<0):
		#buy btc with xlm
		actions.append({"type": "CHANGE", "old_coin": "XMR", "new_coin": "BTC", "gain": btc["1h"]})
	
	#xlm strategy
	if(xmrVal > 0 and xlm["1h"]<0):
		#buy btc with xlm
		actions.append({"type": "CHANGE", "old_coin": "XLM", "new_coin": "BTC", "gain": btc["1h"]})

	#etc strategy
	if(etcVal > 0 and etc["1h"]<0):
		#buy btc with xlm
		actions.append({"type": "CHANGE", "old_coin": "ETC", "new_coin": "BTC", "gain": btc["1h"]})

	#rep strategy
	if(repVal > 0 and rep["1h"]<0):
		#buy btc with rep
		actions.append({"type": "CHANGE", "old_coin": "REP", "new_coin": "BTC", "gain": btc["1h"]})

	#icn strategy
	if(icnVal > 0 and icn["1h"]<0):
		#buy btc with icn
		actions.append({"type": "CHANGE", "old_coin": "ICN", "new_coin": "BTC", "gain": btc["1h"]})
	
	#mln strategy
	if(mlnVal > 0 and mln["1h"]<0):
		#buy btc with mln
		actions.append({"type": "CHANGE", "old_coin": "MLN", "new_coin": "BTC", "gain": btc["1h"]})

	#zec strategy
	if(zecVal > 0 and zec["1h"]<0):
		#buy btc with zec
		actions.append({"type": "CHANGE", "old_coin": "ZEC", "new_coin": "BTC", "gain": btc["1h"]})

	#doge strategy
	if(dogeVal > 0 and doge["1h"]<0):
		#buy btc with doge
		actions.append({"type": "CHANGE", "old_coin": "DOGE", "new_coin": "BTC", "gain": btc["1h"]})
	
	#usdt strategy
	if(usdtVal > 0 and usdt["1h"]<0):
		#buy btc with zec
		actions.append({"type": "CHANGE", "old_coin": "USDT", "new_coin": "BTC", "gain": btc["1h"]})

	maxGain = 0
	bestAction = None
	for action in actions:
		print ("Candidate: " + str(action))
		if(action["gain"] > maxGain):
			maxGain = action["gain"]
			bestAction = action

	if(bestAction is None):
		return []
	return [bestAction]

lastWalletVal = 0

while(True):
	print ("--------------------------------------------------------------------------------------------------")
	print datetime.datetime.now().strftime("%Y-%m-%d %H:%M")
	updateMktData()

	showWallet(wallet)

	actions = engine(wallet)

	print ("ACTIONS: " + str(actions))

	if(len(actions)>0):
		for action in actions:
			if(action["type"] == "CHANGE"):
				change(wallet, coins, action["old_coin"], action["new_coin"])
	
	lastWalletVal = getWalletVal(wallet)

	showWallet(wallet)

	time.sleep(60*5)# 5 min



