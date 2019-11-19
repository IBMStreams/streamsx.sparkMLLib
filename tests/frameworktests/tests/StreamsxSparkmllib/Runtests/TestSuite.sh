#Make sure instance and domain is running

setVar 'TTPR_timeout' 1200

PREPS='cleanUpInstAndDomainAtStart mkDomain startDomain mkInst startInst'
FINS='cleanUpInstAndDomainAtStop'