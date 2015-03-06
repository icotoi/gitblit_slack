logger.info("slack hook triggered by ${user.username} for ${repository.name}")


// define your jenkins url here or set groovy.slackHook in
// gitblit.properties or web.xml
def slackHookURL = gitblit.getString('groovy.slackHook', 'https://hooks.slack.com/services/xx/xx/xx')

// define the trigger url
def triggerUrl = slackHookURL

def refName = ""
for (i in commands) {
	//refName += i.toString()
	newId = i.getNewId()
	//logger.info(refName)
	def repo = receivePack.getRepository()
	def head = repo.getRef(i.getRefName())
	def walk = receivePack.getRevWalk()
	def commit =  walk.parseCommit(head.getObjectId())
	logger.info(commit.getFullMessage())
	refName += commit.getShortMessage() + " on branch " + i.getRefName()
}
def queryString = "payload={\"text\": \"${user.username} has pushed: "+ refName +"\", \"username\": \"${repository.name}\"}"
logger.debug(queryString)

def url = new URL(triggerUrl)
def connection = url.openConnection()
connection.setRequestMethod("POST")
connection.doOutput = true
def writer = new OutputStreamWriter(connection.outputStream)
writer.write(queryString)
writer.flush()
writer.close()
connection.connect()

def recaptchaResponse = connection.content.text
logger.debug(recaptchaResponse)
