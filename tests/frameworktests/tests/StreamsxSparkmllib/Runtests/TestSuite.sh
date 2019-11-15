#Make sure instance and domain is running

setVar 'TTPR_timeout' 60000

PREPS='cleanUpInstAndDomainAtStart mkDomain startDomain mkInst startInst'
FINS='cleanUpInstAndDomainAtStop'